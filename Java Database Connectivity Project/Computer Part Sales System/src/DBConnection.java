/*
 * DBConnection.java is a centralized helper for obtaining a JDBC Connection.
 * It stores database connection parameters (URL / username / password) and provide getConnection() for other classes to open a connection.
 * In production, credentials should not be hard-coded in source code. Consider using environment variables or a config file.
 * by Jeffery
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:oracle:thin:@db18.cse.cuhk.edu.hk:1521/oradb.cse.cuhk.edu.hk";
    private static final String USER = "h048";
    private static final String PASSWORD = "wreshVig";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}