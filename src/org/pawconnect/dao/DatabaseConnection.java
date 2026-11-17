package org.pawconnect.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Reusable Database Connection Manager for PawConnect using MySQL JDBC.
 * Loads the MySQL Connector driver, reads configuration properties,
 * and dispenses Connection instances using PreparedStatement standards.
 */
public class DatabaseConnection {
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    // Default connection properties
    private static String url = "jdbc:mysql://localhost:3306/pawconnect_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String user = "root";
    private static String password = "root";

    static {
        loadProperties();
        loadDriver();
    }

    private static void loadProperties() {
        Properties props = new Properties();
        try {
            // Check working directory for db.properties
            File file = new File("db.properties");
            if (file.exists()) {
                try (InputStream in = new FileInputStream(file)) {
                    props.load(in);
                }
            } else {
                // Check classpath resource
                try (InputStream in = DatabaseConnection.class.getResourceAsStream("/db.properties")) {
                    if (in != null) props.load(in);
                }
            }

            if (props.containsKey("db.url")) url = props.getProperty("db.url").trim();
            if (props.containsKey("db.user")) user = props.getProperty("db.user").trim();
            if (props.containsKey("db.password")) password = props.getProperty("db.password").trim();
        } catch (Exception e) {
            System.err.println("[DatabaseConnection] Note: Using default MySQL settings (" + e.getMessage() + ")");
        }
    }

    private static void loadDriver() {
        try {
            Class.forName(DRIVER_CLASS);
            System.out.println("[DatabaseConnection] MySQL JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("[DatabaseConnection] ERROR: MySQL JDBC Driver (" + DRIVER_CLASS + ") not found in classpath!");
            System.err.println("Ensure mysql-connector-j-*.jar is placed in the lib/ directory.");
        }
    }

    /**
     * Obtains an active connection to the MySQL database.
     * @return Connection object
     * @throws SQLException if connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Quick test to verify if MySQL connection is currently reachable.
     * @return true if connection succeeded, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Closes an active database Connection safely.
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing Connection: " + e.getMessage());
            }
        }
    }

    /**
     * Closes an active Statement or PreparedStatement safely.
     */
    public static void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing Statement: " + e.getMessage());
            }
        }
    }

    /**
     * Closes an active ResultSet safely.
     */
    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Error closing ResultSet: " + e.getMessage());
            }
        }
    }

    /**
     * Convenience method to close ResultSet, Statement, and Connection together.
     */
    public static void closeAll(ResultSet rs, Statement stmt, Connection conn) {
        close(rs);
        close(stmt);
        close(conn);
    }
}
