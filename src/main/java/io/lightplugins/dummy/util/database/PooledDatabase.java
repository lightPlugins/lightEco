package io.lightplugins.dummy.util.database;

import com.zaxxer.hikari.HikariDataSource;
import io.lightplugins.dummy.Light;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class PooledDatabase extends SQLDatabase {

    protected static final AtomicInteger POOL_COUNTER = new AtomicInteger(0);
    protected HikariDataSource hikari;

    public PooledDatabase(Light plugin) {
        super(plugin);
    }

    @Override
    public void close() {
        if (this.hikari != null) {
            this.hikari.close();
            //this.plugin.getLogger().info("Database Connection closed");
        }
    }

    @Override
    public Connection getConnection() {
        try {
            return this.hikari.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
