package com.Mahmoud.sales_backend.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            // بياخد البيانات من Environment Variables بدل hardcoded
            String host     = System.getenv("MYSQLHOST");
            String port     = System.getenv("MYSQLPORT");
            String database = System.getenv("MYSQLDATABASE");
            String user     = System.getenv("MYSQLUSER");
            String password = System.getenv("MYSQLPASSWORD");

            // Fallback للـ local development
            if (host == null) {
                host     = "localhost";
                port     = "3306";
                database = "mandoob2";
                user     = "root";
                password = "";
            }

            String url = "jdbc:mysql://" + host + ":" + port + "/" + database
                       + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

            this.connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database Connected Successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}