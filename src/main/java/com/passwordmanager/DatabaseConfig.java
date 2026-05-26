package com.passwordmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    private DatabaseConfig(){}

    public static Connection getConnection() throws SQLException {
        String url= System.getenv("DB_URL");
        String username= System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

        if(url==null || username==null || password==null) {
            throw new IllegalStateException("DB Configuration Missing");
        }

        Connection connection= DriverManager.getConnection(url, username, password);
        connection.setAutoCommit(false);
        return connection;
    }
}
