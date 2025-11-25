/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ClinicSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 ********************************************************* NOTE: This Connector
 * Class is only for Student Registration database. So That I can Create or Show
 * their Data in a proper Java GUI using my jTable
 * ******************************************
 *
 *
 * @author Louise
 */
public class DatabaseConnector {
    
    // H2 Database URLs (stored in the project folder)
    private static final String ADMIN_DB_URL = "jdbc:h2:./database/admin_data;AUTO_SERVER=TRUE";
    private static final String STUDENT_DB_URL = "jdbc:h2:./database/students_data;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "";
    
    /**
     * Get connection to admin_data database
     */
    public static Connection getAdminConnection() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
            Connection con = DriverManager.getConnection(ADMIN_DB_URL, DB_USER, DB_PASS);
            System.out.println("Admin database connection established successfully.");
            return con;
        } catch (ClassNotFoundException e) {
            System.err.println("H2 JDBC Driver not found. Check if h2.jar is in Libraries.");
            throw new SQLException("Driver not found.", e);
        }
    }
    
    /**
     * Get connection to students_data database
     */
    public static Connection getStudentConnection() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
            Connection con = DriverManager.getConnection(STUDENT_DB_URL, DB_USER, DB_PASS);
            System.out.println("Student database connection established successfully.");
            return con;
        } catch (ClassNotFoundException e) {
            System.err.println("H2 JDBC Driver not found. Check if h2.jar is in Libraries.");
            throw new SQLException("Driver not found.", e);
        }
    }
    
    /**
     * Legacy method for backward compatibility (uses student DB)
     */
    public static Connection getConnection() throws SQLException {
        return getStudentConnection();
    }
}
