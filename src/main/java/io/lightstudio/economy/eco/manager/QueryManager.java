package io.lightstudio.economy.eco.manager;

//  999.999.999.999.999.999.999.999.999.999.999.999.999.999.999.999.999,99 -> max value for the database for NUMERIC(32, 2)

import io.lightstudio.economy.Light;
import io.lightstudio.economy.eco.LightEco;
import io.lightstudio.economy.eco.api.EcoProfile;
import io.lightstudio.economy.util.PlayerParser;
import io.lightstudio.economy.util.database.SQLDatabase;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class QueryManager {

    private final String tableName = "economy_core";
    private final SQLDatabase database;

    public QueryManager(SQLDatabase database) {
        this.database = database;
    }

    public void createEcoTable() {
        String query =
                "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                        "uuid varchar(36) NOT NULL UNIQUE," + " " +
                        "balance numeric(32,2)," + " " +
                        "is_towny TINYINT(1)," + " " +
                        "primary key (uuid))";
        database.executeSql(query);
    }

    public List<EcoProfile> loadAllEcoProfilesFromDatabaseInRam() {
        String sql = "SELECT * FROM " + tableName;
        return database.getAllEcoProfiles(sql);
    }

    public CompletableFuture<Boolean> prepareNewAccount(UUID uuid, boolean withStartBalance, int isTowny) {
        BigDecimal startBalance = LightEco.getSettingParams().defaultCurrency().getStartBalance();
        String query = "INSERT INTO " + tableName + "(uuid, balance, is_towny) VALUES (?,?,?)";

        return database.executeSqlFutureAsync(query, uuid.toString(), withStartBalance ? startBalance : 0.0, isTowny)
                .thenApplyAsync(result -> result > 0)
                .exceptionally(ex -> {
                    throw new RuntimeException(
                            "Failed to prepare new account for UUID: " + uuid + " is towny account -> " + isTowny, ex);
                });
    }

    public EcoProfile getEcoProfileFromDatabase(UUID uuid) {
        String sql = "SELECT * FROM " + tableName + " WHERE uuid = ?";
        return database.getSingleEcoProfile(sql, uuid.toString());
    }

    public void updateEcoProfileInDatabaseAsync(EcoProfile ecoProfile) {
        String sql = "UPDATE " + tableName + " SET balance = ? WHERE uuid = ?";
        database.executeSqlFutureAsync(sql, ecoProfile.getBalance(), ecoProfile.getUuid().toString())
                .thenApplyAsync(result -> {
                    if (result == 0) {
                        throw new RuntimeException("Failed to update eco profile for UUID: " + ecoProfile.getUuid());
                    }
                    // Light.getConsolePrinting().debug("Updated eco profile for UUID: " + ecoProfile.getUuid() + " with balance: " + ecoProfile.getBalance());
                    return null;
                });
    }

    public void updateEcoProfileInDatabase(EcoProfile ecoProfile) {
        String sql = "UPDATE " + tableName + " SET balance = ? WHERE uuid = ?";
        database.executeSqlFutureSync(sql, ecoProfile.getBalance(), ecoProfile.getUuid().toString())
                .thenApply(result -> {
                    if (result == 0) {
                        throw new RuntimeException("Failed to update eco profile for UUID: " + ecoProfile.getUuid());
                    }
                    return null;
                });
    }

    public CompletableFuture<Boolean> deletePlayerFromDatabaseAsync(String playerName) {
        return PlayerParser.getPlayerUUID(playerName).thenCompose(uuid -> {
            String sql = "DELETE FROM " + tableName + " WHERE uuid = ?";
            return database.executeSqlFutureAsync(sql, uuid.toString())
                    .thenApplyAsync(result -> result > 0)
                    .exceptionally(ex -> {
                        throw new RuntimeException("Failed to delete player " + playerName + " from database", ex);
                    });
        });
    }

    public CompletableFuture<Boolean> clearTableAsync() {
        String sql = "DELETE FROM " + tableName;
        return database.executeSqlFutureAsync(sql)
                .thenApplyAsync(result -> result > 0)
                .exceptionally(ex -> {
                    throw new RuntimeException("Failed to clear table " + tableName, ex);
                });
    }

    public CompletableFuture<Integer> setBalanceFromAccount(UUID uuid, BigDecimal balance) {
        String sql = "UPDATE " + tableName + " SET balance = ? WHERE uuid = ?";
        return database.executeSqlFutureAsync(sql, balance, uuid.toString());
    }

    public CompletableFuture<Boolean> withdrawFromAccount(UUID uuid, BigDecimal amount) {
        String sql = "UPDATE " + tableName + " SET balance = balance - ? WHERE uuid = ?";
        return database.executeSqlFutureAsync(sql, amount, uuid.toString())
                .thenApplyAsync(rowsUpdated -> rowsUpdated > 0);
    }

    public CompletableFuture<Boolean> depositFromAccount(UUID uuid, BigDecimal amount) {
        String sql = "UPDATE " + tableName + " SET balance = balance + ? WHERE uuid = ?";
        return database.executeSqlFutureAsync(sql, amount, uuid.toString())
                .thenApplyAsync(rowsUpdated -> rowsUpdated > 0);
    }
}
