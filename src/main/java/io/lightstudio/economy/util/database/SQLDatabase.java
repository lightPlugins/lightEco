package io.lightstudio.economy.util.database;

import io.lightstudio.economy.Light;
import io.lightstudio.economy.eco.api.EcoProfile;
import io.lightstudio.economy.util.NumberFormatter;
import io.lightstudio.economy.util.database.model.DatabaseTypes;
import me.lucko.helper.Schedulers;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public abstract class SQLDatabase {

    protected final Light plugin;

    SQLDatabase(Light plugin) {
        this.plugin = plugin;
    }

    public abstract DatabaseTypes getDatabaseType();

    public abstract void connect();

    public abstract void close();

    public abstract Connection getConnection();

    public PreparedStatement prepareStatement(Connection connection, String sql, Object... replacements) {

        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(sql);
            this.replaceQueryParameters(statement,replacements);

            //if (this.plugin.isDebugMode()) {
            //    this.plugin.getLogger().info("Statement prepared: " + sql + " (Replacement values: " + Arrays.toString(replacements) + ")");
            //}

            return statement;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CompletableFuture<Integer> executeSqlFuture(String sql, Object... replacements) {

        CompletableFuture<Integer> future = new CompletableFuture<>();


        CompletableFuture.runAsync(() -> {
            try (Connection c = getConnection(); PreparedStatement statement = prepareStatement(c, sql, replacements)) {
                int affectedLines = statement.executeUpdate();
                future.complete(affectedLines);
            } catch (SQLException e) {
                e.printStackTrace();
                //future.completeExceptionally(new RuntimeException("[Light] Could not execute SQL statement", e));
                throw new RuntimeException("[Light] Could not execute SQL statement", e);
            }
        });
        return future;
    }

    public CompletableFuture<ResultSet> executeQueryAsync(String sql, Object... replacements) {


        CompletableFuture<ResultSet> future = new CompletableFuture<>();
        AtomicInteger count = new AtomicInteger();

        new BukkitRunnable() {
            @Override
            public void run() {
                try (Connection c = getConnection(); PreparedStatement statement =
                        prepareStatement(c, sql, replacements); ResultSet set = statement.executeQuery()) {
                    while (set.next()) {
                        count.getAndIncrement();
                    }
                    future.complete(set);
                    Bukkit.getLogger().log(Level.INFO, "Query executed: " + sql +
                            " (Replacement values: " + Arrays.toString(replacements) + "). " +
                            "Found " + count.get() + " results.");
                } catch (SQLException e) {
                    future.completeExceptionally(e);
                    throw new RuntimeException("[Light] Could not execute SQL statement", e);
                }
            }
        }.runTaskAsynchronously(Light.instance);

        return future;
    }

    public List<EcoProfile> getAllEcoProfiles(String sql, Object... replacements) {
        List<EcoProfile> ecoProfiles = new ArrayList<>();
        try (Connection c = getConnection();
             PreparedStatement statement = prepareStatement(c, sql, replacements);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                BigDecimal balance = resultSet.getBigDecimal("balance");
                EcoProfile ecoProfile = new EcoProfile(uuid);
                ecoProfile.setBalance(NumberFormatter.formatBigDecimal(balance));
                ecoProfiles.add(ecoProfile);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch all eco profiles", e);
        }
        return ecoProfiles;
    }

    public void executeSql(String sql, Object... replacements) {

        if (sql == null || sql.isEmpty()) {
            return;
        }

        long startTime = System.currentTimeMillis();

        try (Connection c = getConnection(); PreparedStatement statement = prepareStatement(c,sql,replacements)) {

            statement.execute();
            long endTime = System.currentTimeMillis();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void replaceQueryParameters(PreparedStatement statement, Object[] replacements) {
        if (replacements != null) {
            for (int i = 0; i < replacements.length; i++) {
                int position = i + 1;
                Object value = replacements[i];
                try {
                    statement.setObject(position, value);
                } catch (SQLException e) {
                    this.plugin.getLogger().warning("Unable to set query parameter at position " + position + " to " + value + " for query: " + statement);
                    e.printStackTrace();
                }
            }
        }
    }

    public void executeSqlAsync(String sql, Object... replacements) {
        Schedulers.async().run(() -> this.executeSql(sql, replacements));
    }
}
