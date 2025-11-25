package ClinicSystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

/**
 * Initializes H2 databases from SQL dump files on first run
 * Handles MySQL to H2 conversion automatically
 */
public class DatabaseInitializer {
    
    public static void initializeDatabases() {
        try {
            System.out.println("===========================================");
            System.out.println("Checking database initialization...");
            System.out.println("===========================================");
            
            // Check if databases already have tables
            boolean adminInitialized = isDatabaseInitialized(DatabaseConnector.getAdminConnection(), "adminregistration");
            boolean studentInitialized = isDatabaseInitialized(DatabaseConnector.getStudentConnection(), "student");
            
            if (adminInitialized && studentInitialized) {
                System.out.println("✓ Databases already initialized.");
                return;
            }
            
            System.out.println("First run detected. Initializing databases...");
            
            // Initialize admin_data database
            if (!adminInitialized) {
                System.out.println("\n--- Initializing admin_data database ---");
                
                // Try SQL file first
                File adminSqlFile = new File("admin_data.sql");
                if (adminSqlFile.exists()) {
                    System.out.println("Found admin_data.sql, importing...");
                    importSQLFile("admin_data.sql", DatabaseConnector.getAdminConnection());
                } else {
                    // Create tables manually if SQL file doesn't exist
                    System.out.println("admin_data.sql not found, creating tables manually...");
                    createAdminTables();
                }
                System.out.println("✓ admin_data database initialized");
            }
            
            // Initialize students_data database
            if (!studentInitialized) {
                System.out.println("\n--- Initializing students_data database ---");
                
                // Try SQL file first
                File studentSqlFile = new File("students_data.sql");
                if (studentSqlFile.exists()) {
                    System.out.println("Found students_data.sql, importing...");
                    importSQLFile("students_data.sql", DatabaseConnector.getStudentConnection());
                } else {
                    // Create tables manually if SQL file doesn't exist
                    System.out.println("students_data.sql not found, creating tables manually...");
                    createStudentTables();
                }
                System.out.println("✓ students_data database initialized");
            }
            
            System.out.println("\n===========================================");
            System.out.println("✓ Database initialization complete!");
            System.out.println("===========================================\n");
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Database initialization failed!\n" + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Check if database has been initialized by checking if a table exists
     */
    private static boolean isDatabaseInitialized(Connection con, String tableName) {
        try {
            ResultSet rs = con.getMetaData().getTables(null, null, tableName.toUpperCase(), null);
            boolean exists = rs.next();
            rs.close();
            con.close();
            return exists;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Create admin database tables manually
     */
    private static void createAdminTables() throws Exception {
        Connection con = DatabaseConnector.getAdminConnection();
        Statement stmt = con.createStatement();
        
        // Create adminregistration table
        String createTable = "CREATE TABLE IF NOT EXISTS adminregistration (" +
            "EmploymentId VARCHAR(50) PRIMARY KEY, " +
            "FirstName VARCHAR(100), " +
            "LastName VARCHAR(100), " +
            "UserName VARCHAR(50) UNIQUE, " +
            "Department VARCHAR(100), " +
            "Password VARCHAR(255), " +
            "Confirm_Pass VARCHAR(255), " +
            "ProfilePicture VARCHAR(255), " +
            "CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")";
        
        stmt.execute(createTable);
        System.out.println("  ✓ Created table: adminregistration");
        
        stmt.close();
        con.close();
    }
    
    /**
     * Create student database tables manually
     */
    private static void createStudentTables() throws Exception {
        Connection con = DatabaseConnector.getStudentConnection();
        Statement stmt = con.createStatement();
        
        // Create student table
        String createStudentTable = "CREATE TABLE IF NOT EXISTS student (" +
            "LRN VARCHAR(12) PRIMARY KEY, " +
            "FirstName VARCHAR(100) NOT NULL, " +
            "LastName VARCHAR(100) NOT NULL, " +
            "Sex VARCHAR(10), " +
            "Weight VARCHAR(10), " +
            "Height VARCHAR(10), " +
            "Grade VARCHAR(10), " +
            "Section VARCHAR(50), " +
            "ProfilePicture VARCHAR(255), " +
            "CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")";
        
        stmt.execute(createStudentTable);
        System.out.println("  ✓ Created table: student");
        
        // Create diagnostics table
        String createDiagnosticsTable = "CREATE TABLE IF NOT EXISTS diagnostics (" +
            "diagnostic_id INT AUTO_INCREMENT PRIMARY KEY, " +
            "student_lrn_fk VARCHAR(12), " +
            "student_name VARCHAR(200), " +
            "diagnostic_notes TEXT, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (student_lrn_fk) REFERENCES student(LRN) ON DELETE CASCADE" +
            ")";
        
        stmt.execute(createDiagnosticsTable);
        System.out.println("  ✓ Created table: diagnostics");
        
        stmt.close();
        con.close();
    }
    
    /**
     * Import SQL file with MySQL to H2 conversion
     */
    private static void importSQLFile(String sqlFileName, Connection con) throws Exception {
        File sqlFile = new File(sqlFileName);
        
        if (!sqlFile.exists()) {
            throw new Exception("SQL file not found: " + sqlFileName);
        }
        
        Statement stmt = con.createStatement();
        StringBuilder sql = new StringBuilder();
        int successCount = 0;
        int skipCount = 0;
        
        try (BufferedReader br = new BufferedReader(new FileReader(sqlFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                
                // Skip comments and empty lines
                if (line.isEmpty() || 
                    line.startsWith("--") || 
                    line.startsWith("/*") || 
                    line.startsWith("*/") ||
                    line.startsWith("#")) {
                    continue;
                }
                
                sql.append(line).append(" ");
                
                // Execute when we hit a semicolon
                if (line.endsWith(";")) {
                    String statement = sql.toString().trim();
                    
                    // Convert MySQL syntax to H2 syntax
                    statement = convertMySQLToH2(statement);
                    
                    // Skip MySQL-specific commands
                    if (shouldSkipStatement(statement)) {
                        skipCount++;
                        sql = new StringBuilder();
                        continue;
                    }
                    
                    try {
                        stmt.execute(statement);
                        successCount++;
                    } catch (Exception e) {
                        System.err.println("  ⚠ Warning: Could not execute statement");
                        System.err.println("    SQL: " + statement.substring(0, Math.min(100, statement.length())) + "...");
                        System.err.println("    Error: " + e.getMessage());
                    }
                    
                    sql = new StringBuilder();
                }
            }
        }
        
        System.out.println("  ✓ Executed " + successCount + " SQL statements (" + skipCount + " skipped)");
        
        stmt.close();
        con.close();
    }
    
    /**
     * Convert MySQL syntax to H2 syntax
     */
    private static String convertMySQLToH2(String sql) {
        String converted = sql;
        
        // Remove backticks (MySQL uses them, H2 doesn't need them)
        converted = converted.replace("`", "");
        
        // Convert ENGINE=InnoDB to nothing
        converted = converted.replaceAll("(?i)ENGINE\\s*=\\s*\\w+", "");
        
        // Convert DEFAULT CHARSET=utf8 to nothing
        converted = converted.replaceAll("(?i)DEFAULT\\s+CHARSET\\s*=\\s*\\w+", "");
        converted = converted.replaceAll("(?i)CHARSET\\s*=\\s*\\w+", "");
        converted = converted.replaceAll("(?i)COLLATE\\s*=\\s*\\w+", "");
        
        // Convert AUTO_INCREMENT to AUTO_INCREMENT (H2 uses same syntax)
        // But remove AUTO_INCREMENT value assignments like AUTO_INCREMENT=5
        converted = converted.replaceAll("(?i)AUTO_INCREMENT\\s*=\\s*\\d+", "");
        
        // Convert DATETIME to TIMESTAMP
        converted = converted.replaceAll("(?i)\\bDATETIME\\b", "TIMESTAMP");
        
        // Convert INT(11) to INT
        converted = converted.replaceAll("(?i)INT\\(\\d+\\)", "INT");
        
        // Convert TINYINT to SMALLINT
        converted = converted.replaceAll("(?i)\\bTINYINT\\b", "SMALLINT");
        
        // Remove COMMENT statements
        converted = converted.replaceAll("(?i)COMMENT\\s+'[^']*'", "");
        
        // Clean up extra spaces and commas
        converted = converted.replaceAll("\\s+", " ");
        converted = converted.replaceAll(",\\s*\\)", ")");
        
        return converted.trim();
    }
    
    /**
     * Check if statement should be skipped
     */
    private static boolean shouldSkipStatement(String statement) {
        String upperStatement = statement.toUpperCase();
        
        return upperStatement.startsWith("SET ") ||
               upperStatement.startsWith("LOCK ") ||
               upperStatement.startsWith("UNLOCK ") ||
               upperStatement.startsWith("USE ") ||
               upperStatement.contains("SQL_MODE") ||
               upperStatement.contains("FOREIGN_KEY_CHECKS") ||
               upperStatement.contains("UNIQUE_CHECKS") ||
               upperStatement.contains("AUTOCOMMIT");
    }
}