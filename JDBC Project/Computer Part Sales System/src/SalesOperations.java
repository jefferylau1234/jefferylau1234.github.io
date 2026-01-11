/*
 * Implements Salesperson functions.
 * Menu functions:
 * 1) searchTable(sc):
 *    - Search parts by either Part Name or Manufacturer Name (partial match using LIKE).
 *    - Allow sorting results by price (ascending / descending).
 *    - Print results in a tabular format.
 *
 * 2) Transaction(sc):
 *    - Sell a part by inputting Part ID and Salesperson ID.
 *    - Check availability from PART table (available quantity > 0).
 *    - If available: decrement quantity and insert a new row into TRANSACTION table.
 *
 * - Uses DBConnection.getConnection() and PreparedStatement for parameterized queries.
 * - This file contains both query (SELECT) and update (UPDATE/INSERT) operations.
 * by Jeffery
 */



import java.sql.*;
import java.util.*;
import java.io.*;

public class SalesOperations {
    
    public static void showMenu(Scanner sc) {
        while (true) {
            System.out.println("\n-----Operations for salesperson menu-----");
            System.out.println("What kinds of operation would you like to perform?");
            System.out.println("1. Search for parts");
            System.out.println("2. Sell a part");
            System.out.println("3. Return to the main menu");
            System.out.print("Enter Your Choice: ");
            int choice = sc.nextInt();
            sc.nextLine();  

            switch (choice) {
                case 1:
                    searchTable(sc);
                    return;
                case 2:
                    Transaction(sc);
                    return;
                case 3:
                    return;
                default:
                    System.out.println("Invalid input.");
            }
        }
    }

    
    public static void searchTable(Scanner sc) {
    try (Connection conn = DBConnection.getConnection()) {

        System.out.println("Choose the Search criterion: ");
        System.out.println("1. Part Name");
        System.out.println("2. Manufacturer Name");
        System.out.print("Choose the search criterion: ");
        int choice = sc.nextInt();
        sc.nextLine();  
        
        System.out.print("Type in the Search Keyword: ");
        String keyword = sc.nextLine();

        System.out.println("Choose ordering:");
        System.out.println("1. By price, ascending order");
        System.out.println("2. By price, descending order");
        System.out.print("Choose the ordering: ");
        int ordering = sc.nextInt();


        String searchColumn;
        if (choice == 1) {
            searchColumn = "PNAME";
        } else if (choice == 2) {
            searchColumn = "MNAME";
        } else {
            System.out.println("Invalid search criterion.");
            return;
        }

        String order;
        if (ordering == 1) {
            order = "ASC";
        } else if (ordering == 2) {
            order = "DESC";
        } else {
            System.out.println("Invalid ordering choice.");
            return;
        }

        String query = "SELECT PID, PNAME, MNAME, CNAME, PAVAILABLEQUANTITY, PWARRANTYPERIOD, PPRICE " +
                       "FROM PART NATURAL JOIN MANUFACTURER NATURAL JOIN CATEGORY " +
                       "WHERE " + searchColumn + " LIKE ? " +
                       "ORDER BY PPRICE " + order;

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, "%" + keyword + "%");

            ResultSet rs = pstmt.executeQuery();
            System.out.printf("| %-2s | %-15s | %-15s | %-12s | %-8s | %-8s | %-8s |\n", "ID", "Name", "Manufacturer", "Category", "Quantity", "Warranty", "Price");

            while (rs.next()) {
                System.out.printf("| %-2d | %-15s | %-15s | %-12s | %-8d | %-8d | %-8.2f |\n", rs.getInt("PID"), rs.getString("PNAME"), rs.getString("MNAME"), rs.getString("CNAME"), rs.getInt("PAVAILABLEQUANTITY"), rs.getInt("PWARRANTYPERIOD"), rs.getDouble("PPRICE"));
            }

            System.out.println("End of Query");
        }

    } catch (SQLException e) {
        System.out.println("Fail to Query：" + e.getMessage());
    }
}
    
    
    
    public static void Transaction(Scanner sc) {
    try (Connection conn = DBConnection.getConnection()) {

        System.out.print("Enter the Part ID: ");
        int partId = sc.nextInt();

        System.out.print("Enter the Salesperson ID: ");
        int salespersonId = sc.nextInt();

        String checkQuery = "SELECT PNAME, PAVAILABLEQUANTITY FROM PART WHERE PID = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, partId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String partName = rs.getString("PNAME");
                int availableQty = rs.getInt("PAVAILABLEQUANTITY");

                if (availableQty > 0) {
                    String updateQuery = "UPDATE PART SET PAVAILABLEQUANTITY = PAVAILABLEQUANTITY - 1 WHERE PID = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, partId);
                        int updated = updateStmt.executeUpdate();

                        if (updated > 0) {
                            System.out.printf("Product: %s(id: %d) Remaining Quantity: %d%n", partName, partId, availableQty - 1);
                        } 
                        else {System.out.println("Fail to update the part.");}
                    }
                    
                    int max=0;
                    String getMaxID = "SELECT MAX(TID) FROM transaction";
                    try (PreparedStatement pstmt = conn.prepareStatement(getMaxID);
                        ResultSet abc = pstmt.executeQuery()) {  
                    if (abc.next()) {
                        max = abc.getInt("MAX(TID)");
                    }
                    }catch (SQLException e) {
                        System.out.printf("%d",max);
                    }
                    
                    
                    
                    
                    
                    String saveTransaction = "INSERT INTO TRANSACTION (TID, PID, SID, TDATE) VALUES (?, ?, ?, SYSDATE)";
                    try (PreparedStatement saveStmt = conn.prepareStatement(saveTransaction)) {
                        saveStmt.setInt(1,max+1);
                        saveStmt.setInt(2, partId);
                        saveStmt.setInt(3, salespersonId);
                        saveStmt.executeUpdate();
                    }
                    
                }
                else {System.out.println("Cannot sell part. Out of stock.");}
            }
            else {System.out.println("Part not found.");}
        }

    } catch (SQLException e) {System.out.println(" Transaction failed: " + e.getMessage());}
    }
    
}
