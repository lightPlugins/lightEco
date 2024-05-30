/**
 * This class represents the model for connection properties.
 * It includes various properties related to database connection settings.
 */
package io.lightplugins.dummy.util.database.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
@AllArgsConstructor
public class ConnectionProperties {

    private final long idleTimeout, maxLifetime, connectionTimeout, leakDetectionThreshold, keepAliveTime;
    private final int minimumIdle, maximumPoolSize;
    private final String testQuery, characterEncoding;

    public static ConnectionProperties fromConfig(FileConfiguration config) {

        String rootPath = "storage.advanced.";

        long connectionTimeout = config.getLong(rootPath + "connection-timeout");
        long idleTimeout = config.getLong(rootPath + "idle-timeout");
        long keepAliveTime = config.getLong(rootPath + "keep-alive-time");
        long maxLifeTime = config.getLong(rootPath + "max-life-time");
        int minimumIdle = config.getInt(rootPath + "minimum-idle");
        int maximumPoolSize = config.getInt(rootPath + "maximum-pool-size");
        long leakDetectionThreshold = config.getLong(rootPath + "leak-detection-threshold");
        String characterEncoding = config.getString(rootPath + "character-encoding", "utf8");
        String testQuery = config.getString(rootPath + "connection-test-query");
        return new ConnectionProperties(idleTimeout, maxLifeTime, connectionTimeout, leakDetectionThreshold, keepAliveTime, minimumIdle, maximumPoolSize, testQuery,characterEncoding);
    }
}