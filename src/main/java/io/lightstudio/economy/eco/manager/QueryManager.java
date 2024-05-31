package io.lightstudio.economy.eco.manager;

//  999.999.999.999.999.999.999.999.999.999.999.999.999.999.999.999.999,99 -> max value for the database for NUMERIC(32, 2)

import io.lightstudio.economy.Light;
import io.lightstudio.economy.eco.LightEco;
import io.lightstudio.economy.eco.api.EcoProfile;
import io.lightstudio.economy.util.NumberFormatter;
import io.lightstudio.economy.util.database.SQLDatabase;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class QueryManager {

    private final String tableName = "economy_core";
    private final SQLDatabase database;

    public QueryManager(SQLDatabase database) {
        this.database = database;
    }

    public CompletableFuture<ResultSet> test() {
        String query = "SELECT * FROM " + tableName;
        return database.executeQueryAsync(query);
    }

    public void createEcoTable() {
        String query = "CREATE TABLE IF NOT EXISTS " + tableName + "(uuid varchar(36) NOT NULL UNIQUE, balance numeric(32,2), primary key (uuid))";
        database.executeSql(query);
    }

    public CompletableFuture<Boolean> prepareNewAccount(UUID uuid, boolean withStartBalance) {
        BigDecimal startBalance = LightEco.instance.getSettingParams().defaultCurrency().getStartBalance();
        String query = "INSERT INTO " + tableName + "(uuid, balance) VALUES (?,?)";

        return database.executeSqlFuture(query, uuid.toString(), withStartBalance ? startBalance : 0.0)
                .thenApplyAsync(result -> result > 0)
                .exceptionally(ex -> {
                    throw new RuntimeException("Failed to prepare new account for UUID: " + uuid, ex);
                });
    }


    public CompletableFuture<Integer> setBalanceFromAccount(UUID uuid, BigDecimal balance) {
        String sql = "UPDATE " + tableName + " SET balance = ? WHERE uuid = ?";
        return database.executeSqlFuture(sql, balance, uuid.toString());
    }

    public CompletableFuture<Boolean> withdrawFromAccount(UUID uuid, BigDecimal amount) {
        String sql = "UPDATE " + tableName + " SET balance = balance - ? WHERE uuid = ?";
        return database.executeSqlFuture(sql, amount, uuid.toString())
                .thenApplyAsync(rowsUpdated -> rowsUpdated > 0);
    }

    public CompletableFuture<Boolean> depositFromAccount(UUID uuid, BigDecimal amount) {
        String sql = "UPDATE " + tableName + " SET balance = balance + ? WHERE uuid = ?";
        return database.executeSqlFuture(sql, amount, uuid.toString())
                .thenApplyAsync(rowsUpdated -> rowsUpdated > 0);
    }

    public List<EcoProfile> getAllEcoProfiles() {
        String sql = "SELECT * FROM " + tableName;
        List<EcoProfile> ecoProfiles = new ArrayList<>();
        try (ResultSet resultSet = database.executeQuery(sql)) {
            Light.getConsolePrinting().debug("start loading profiles " + sql);
            if (!resultSet.next()) {
                Light.getConsolePrinting().debug("ResultSet is empty");
                return ecoProfiles;
            }
            do {
                Light.getConsolePrinting().debug("TEST");
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                BigDecimal balance = resultSet.getBigDecimal("balance");
                EcoProfile ecoProfile = new EcoProfile(uuid);
                ecoProfile.setBalance(NumberFormatter.formatBigDecimal(balance));
                ecoProfiles.add(ecoProfile);
                Light.getConsolePrinting().debug("Loaded eco profile for UUID: " + uuid + " + balance: " + balance);
            } while (resultSet.next());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch all eco profiles", e);
        }
        Light.getConsolePrinting().debug("TEST2");
        return ecoProfiles;
    }
}
