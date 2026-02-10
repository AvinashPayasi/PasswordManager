package com.passwordmanager;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {

    private static final String PROPERTIES_FILE="application.properties";
    private static final Properties PROPERTIES=new Properties();

    static {
        try(InputStream in= DatabaseConfig.class
                .getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)){

            if(in==null){
                throw new IllegalStateException("application.properties not found");
            }
            PROPERTIES.load(in);
        } catch (IOException ioException) {
            throw new ExceptionInInitializerError(ioException);
        }
    }

    private DatabaseConfig(){}

    public static Connection getConnection() throws SQLException {
        String url= PROPERTIES.getProperty("db.url");
        String username= PROPERTIES.getProperty("db.username");
        String password = PROPERTIES.getProperty("db.password");

        if(url==null || username==null || password==null) {
            throw new IllegalStateException("DB Configuration Missing");
        }

        Connection connection= DriverManager.getConnection(url, username, password);
        connection.setAutoCommit(false);
        return connection;
    }
}
