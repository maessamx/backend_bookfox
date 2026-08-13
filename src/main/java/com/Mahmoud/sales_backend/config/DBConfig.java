package com.Mahmoud.sales_backend.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DBConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    public Connection connection() {
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Database Connected Successfully -> " + url);
            return conn;
        } catch (SQLException e) {
            System.err.println("DATABASE CONNECTION FAILED -> " + url);
            System.err.println("Reason: " + e.getMessage());
            System.err.println("Check that MySQL is running and the database exists (run sql1.md).");
            throw new RuntimeException(
                    "Cannot connect to MySQL at " + url + " — check application.properties", e);
        }
    }
}
