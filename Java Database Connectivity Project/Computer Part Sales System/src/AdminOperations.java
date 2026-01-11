/*
 * Implements Administrator functions for setting up and managing the database.
 * Menu functions:
 * 1) createTables(): Create all table schemas (category, manufacturer, part, salesperson, transaction) following the ER Diagram that I posted
 * 2) deleteTables(): Drop all tables (with CASCADE CONSTRAINTS).
 * 3) loadData(folderPath): Load initial records from text files under a user-specified folder.
 * 4) showTable(tableName): Display all records of a specified table with column headers.
 *
 * - Each operation opens a JDBC Connection via DBConnection.getConnection().
 * - Data loading is split into helper methods:
 *   loadCategory(), loadManufacturer(), loadPart(), loadSalesperson(), loadTransaction().
 * - SQL insertion uses PreparedStatement to bind parameters from parsed text files.
 * by Jeffery
 */

import java.sql.*;
import java.util.*;
import java.io.*;

public class AdminOperations {

    public static void showMenu(Scanner sc) {
        while (true) {
            System.out.println("\n-----Operations for administrator menu-----");
            System.out.println("What kinds of operation would you like to perform?");
            System.out.println("1. Create all tables");
            System.out.println("2. Delete all tables");
            System.out.println("3. Load from datafile");
            System.out.println("4. Show content of a table");
            System.out.println("5. Return to the main menu");
            System.out.print("Enter Your Choice: ");
            int choice = sc.nextInt();
            sc.nextLine();  

            switch (choice) {
                case 1:
                    createTables();
                    return;
                case 2:
                    deleteTables();
                    return;
                case 3:
                    System.out.print("\nType in the Source Data Folder Path: ");
                    String input = sc.next();
                    loadData(input);
                    return;
                case 4:
                    System.out.print("Which table would you like to show: ");
                    String tableName = sc.next();
                    showTable(tableName);
                    return;
                case 5:
                    return;
                default:
                    System.out.println("Invalid input.");
            }
        }
    }

public static void showTable(String tableName) {
    try (Connection conn = DBConnection.getConnection();
         Statement stmt = conn.createStatement()) {

        ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        System.out.println("Content of table " + tableName + ":");
        
        for (int i = 1; i <= columnCount; i++) {
            System.out.print("| " + meta.getColumnName(i) + " ");
        }
        System.out.println("|");

        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                System.out.print("| " + rs.getString(i) + " ");
            }
            System.out.println("|");
        }

    } catch (SQLException e) {
        System.out.println(" Fail to show：" + e.getMessage());
    }
}
    
    
    
    
public static void loadData(String folderPath) {
    try {
        loadCategory(folderPath + "/category.txt");
        loadManufacturer(folderPath + "/manufacturer.txt");
        loadPart(folderPath + "/part.txt");
        loadSalesperson(folderPath + "/salesperson.txt");
        loadTransaction(folderPath + "/transaction.txt");

        System.out.println(" Processing...Done! Data is inputted to the database!");
    } catch (Exception e) {
        System.out.println(" Error while loading data: " + e.getMessage());
    }
}
    

public static void loadCategory(String path) {
    try (Connection conn = DBConnection.getConnection();
         Scanner scanner = new Scanner(new File(path))) {

        String sql = "INSERT INTO category (cID, cName) VALUES (?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] tokens = line.split("\\s+");
            int id = Integer.parseInt(tokens[0]);
            String name = String.join(" ", Arrays.copyOfRange(tokens, 1, tokens.length));

            pstmt.setInt(1, id);
            pstmt.setString(2, name);

            pstmt.executeUpdate();
        }

        System.out.println(" category.txt SUCCESSFUL!");
    } catch (Exception e) {
        System.out.println(" category.txt FAIL:" + e.getMessage());
    }
}
    
    
public static void loadManufacturer(String path) {
    try (Connection conn = DBConnection.getConnection();
         Scanner scanner = new Scanner(new File(path))) {

        String sql = "INSERT INTO manufacturer (mID, mName, mAddress, mPhoneNumber) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] tokens = line.split("\\s+");
            int id = Integer.parseInt(tokens[0]);
            int phone = Integer.parseInt(tokens[tokens.length - 1]);

            String name = tokens[1];
            String address = String.join(" ", Arrays.copyOfRange(tokens, 2, tokens.length-1));

            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, address);
            pstmt.setInt(4, phone);

            pstmt.executeUpdate();
        }

        System.out.println(" manufacturer.txt SUCCESSFUL!");
    } catch (Exception e) {
        System.out.println(" manufacturer.txt FAIL:" + e.getMessage());
    }
}
    
    
    
public static void loadPart(String path) {
    String sql = "INSERT INTO part (pID, pName, pPrice, mID, cID, pWarrantyPeriod, pAvailableQuantity) VALUES (?, ?, ?, ?, ?, ?, ?)";
    try (Connection conn = DBConnection.getConnection();
         Scanner scanner = new Scanner(new File(path));
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] tokens = line.split("\\s+");
            int len = tokens.length;

            if (len < 7) {
                System.out.println("I will skip this line due to not enough column data: " + line);
                continue;
            }

            int pID = Integer.parseInt(tokens[0]);
            int price = Integer.parseInt(tokens[len - 5]);
            int mID = Integer.parseInt(tokens[len - 4]);
            int cID = Integer.parseInt(tokens[len - 3]);
            int warranty = Integer.parseInt(tokens[len - 2]);
            int quantity = Integer.parseInt(tokens[len - 1]);

            String name = String.join(" ", Arrays.copyOfRange(tokens, 1, len - 5));

            pstmt.setInt(1, pID);
            pstmt.setString(2, name);
            pstmt.setInt(3, price);
            pstmt.setInt(4, mID);
            pstmt.setInt(5, cID);
            pstmt.setInt(6, warranty);
            pstmt.setInt(7, quantity);

            pstmt.executeUpdate();
        }

        System.out.println(" part.txt SUCCESSFUL!");
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println(" part.txt FAIL:" + e.getMessage());
    }
}

    
public static void loadSalesperson(String path) {
    try (Connection conn = DBConnection.getConnection();
         Scanner scanner = new Scanner(new File(path))) {

        String sql = "INSERT INTO salesperson (sID, sName, sAddress, sPhoneNumber, sExperience) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] tokens = line.split("\\s+");
            int id = Integer.parseInt(tokens[0]);
            String name = tokens[1] + " " + tokens[2];
            String address = String.join(" ", Arrays.copyOfRange(tokens, 3, tokens.length - 2));
            int phone = Integer.parseInt(tokens[tokens.length - 2]);
            int experience = Integer.parseInt(tokens[tokens.length - 1]);

            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, address);
            pstmt.setInt(4, phone);
            pstmt.setInt(5, experience);

            pstmt.executeUpdate();
        }

        System.out.println(" salesperson.txt SUCCESSFUL!");
    } catch (Exception e) {
        System.out.println(" salesperson.txt FAIL:" + e.getMessage());
    }
}

public static void loadTransaction(String path) {
    try (Connection conn = DBConnection.getConnection();
         Scanner scanner = new Scanner(new File(path))) {

        String sql = "INSERT INTO transaction (tID, pID, sID, tDate) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] tokens = line.split("\\s+");
            int tID = Integer.parseInt(tokens[0]);
            int pID = Integer.parseInt(tokens[1]);
            int sID = Integer.parseInt(tokens[2]);
            String tDate = tokens[3];

            try {
                pstmt.setInt(1, tID);
                pstmt.setInt(2, pID);
                pstmt.setInt(3, sID);
                pstmt.setDate(4, java.sql.Date.valueOf(convertDate(tDate)));

                pstmt.executeUpdate();
            } catch (Exception innerEx) {
                System.out.println(" tID=" + tID + " (pID=" + pID + ", sID=" + sID + ")：" + innerEx.getMessage());
            }
        }

        System.out.println(" transaction.txt SUCCESSFUL!");
    } catch (Exception e) {
        System.out.println(" transaction.txt FAIL:" + e.getMessage());
    }
}

private static String convertDate(String date) {
    String[] parts = date.split("/");
    return parts[2] + "-" + parts[1] + "-" + parts[0]; 
}
    
    
    
    
    public static void createTables() {
        try (Connection conn = DBConnection.getConnection(); 
            Statement stmt = conn.createStatement()) {

            String Category = "CREATE TABLE category (" +
                    "cID INTEGER PRIMARY KEY, " +
                    "cName VARCHAR(50))";

            String Manufacturer = "CREATE TABLE manufacturer (" +
                    "mID INTEGER PRIMARY KEY, " +
                    "mName VARCHAR(50), " +
                    "mAddress VARCHAR(100), " +
                    "mPhoneNumber INTEGER)";

            String Part = "CREATE TABLE part (" +
                    "pID INTEGER PRIMARY KEY, " +
                    "pName VARCHAR(100), " +
                    "pPrice INTEGER, " +
                    "mID INTEGER, " +
                    "cID INTEGER, " +
                    "pWarrantyPeriod INTEGER, " +
                    "pAvailableQuantity INTEGER, " +
                    "FOREIGN KEY (mID) REFERENCES manufacturer(mID), " +
                    "FOREIGN KEY (cID) REFERENCES category(cID))";

            String Salesperson = "CREATE TABLE salesperson (" +
                    "sID INTEGER PRIMARY KEY, " +
                    "sName VARCHAR(50), " +
                    "sAddress VARCHAR(100), " +
                    "sPhoneNumber INTEGER, " +
                    "sExperience INTEGER)";

            String Transaction = "CREATE TABLE transaction (" +
                    "tID INTEGER PRIMARY KEY, " +
                    "pID INTEGER, " +
                    "sID INTEGER, " +
                    "tDate DATE, " +
                    "FOREIGN KEY (pID) REFERENCES part(pID), " +
                    "FOREIGN KEY (sID) REFERENCES salesperson(sID))";

            stmt.executeUpdate(Category);
            stmt.executeUpdate(Manufacturer);
            stmt.executeUpdate(Part);
            stmt.executeUpdate(Salesperson);
            stmt.executeUpdate(Transaction);

            System.out.println("Processing...Done! Database is initialized!");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    
    
    public static void deleteTables() {
        try (Connection conn = DBConnection.getConnection(); 
            Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP TABLE transaction CASCADE CONSTRAINTS");
        } catch (SQLException e) {
            System.out.print("Error for transaction: " + e.getMessage());
        }
        
        try (Connection conn = DBConnection.getConnection(); 
            Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP TABLE part CASCADE CONSTRAINTS");
        } catch (SQLException e) {
            System.out.print("Error for part: " + e.getMessage());
        }
        
        try (Connection conn = DBConnection.getConnection(); 
            Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP TABLE salesperson CASCADE CONSTRAINTS");
        } catch (SQLException e) {
            System.out.print("Error for salesperson: " + e.getMessage());
        }
        
        try (Connection conn = DBConnection.getConnection(); 
            Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP TABLE manufacturer CASCADE CONSTRAINTS"); 
        } catch (SQLException e) {
            System.out.print("Error for manufacturer: " + e.getMessage());
        }
        
        try (Connection conn = DBConnection.getConnection(); 
            Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP TABLE category CASCADE CONSTRAINTS");
        } catch (SQLException e) {
            System.out.print("Error for category: " + e.getMessage());
        }
        
        System.out.println("Processing...Done! Database is removed!");
    }
}