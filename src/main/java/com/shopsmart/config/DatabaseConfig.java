package com.shopsmart.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.Properties;

public class DatabaseConfig {

    private static final HikariDataSource hikariDataSource;

    static {

        try(InputStream inputStream = DatabaseConfig.class.getClassLoader().getResourceAsStream("db.properties")) {

            Properties properties = new Properties();

            if (inputStream == null)
                throw new RuntimeException("Critical Error: Unable to find 'db.properties' inside the resources folder!");

            // Load properties file metadata keys
            properties.load(inputStream);

            HikariConfig hikariConfig = new HikariConfig();

            // Configure HikariCP connection pool infrastructure
            hikariConfig.setJdbcUrl(properties.getProperty("db.url"));
            hikariConfig.setUsername(properties.getProperty("db.username"));
            hikariConfig.setPassword(properties.getProperty("db.password"));

            // Pool Tuning Performance Configurations
            hikariConfig.setMaximumPoolSize(Integer.parseInt(properties.getProperty("db.pool.max-size")));
            hikariConfig.setIdleTimeout(Long.parseLong(properties.getProperty("db.pool.idle-timeout")));
            hikariConfig.setMaxLifetime(Long.parseLong(properties.getProperty("db.pool.max-lifetime")));

            // Driver level optimization properties for MySQL high-performance
            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            // Initialize the data source pool instance
            hikariDataSource = new HikariDataSource(hikariConfig);

        } catch (IOException e) {
            throw new ExceptionInInitializerError("Critical Error: Failed to parse db.properties configuration metadata. " + e.getMessage());
        }
    }

    // Method for connect to database
    public static Connection getConnection() throws SQLException {

        if (hikariDataSource == null)
            throw new SQLException("Database connection pool data source was not properly initialized!");

        return hikariDataSource.getConnection();
    }

    // Method for close HikariCP connection pool
    public static void closePool() {

        if (hikariDataSource != null && !hikariDataSource.isClosed()) {
            hikariDataSource.close();
        }
    }
}
