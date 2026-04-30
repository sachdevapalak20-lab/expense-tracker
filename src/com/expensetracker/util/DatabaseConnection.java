package com.expensetracker.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection - Singleton class for managing MySQL JDBC connections.
 * Handles connection pooling and provides a single point of DB access.
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/expense_tracker?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER     = "root";
    private static final String PASSWORD = "aisha@2578"; // Change this

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[DB] Connection established successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("[DB] MySQL JDBC Driver not found: " + e.getMessage());
            throw new RuntimeException("JDBC Driver missing. Add mysql-connector-java to classpath.", e);
        } catch (SQLException e) {
            System.err.println("[DB] Connection failed: " + e.getMessage());
            throw new RuntimeException("Cannot connect to database. Check credentials in DatabaseConnection.java", e);
        }
    }

    /** Returns the singleton instance, creating it if needed. */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null || isConnectionClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    private static boolean isConnectionClosed() {
        try {
            return instance.connection == null || instance.connection.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }

    /** Returns the active JDBC Connection object. */
    public Connection getConnection() {
        return connection;
    }

    /** Closes the database connection. */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error closing connection: " + e.getMessage());
        }
    }
}