/*
 * Implements Manager functions for reporting and analytics queries.
 * Menu functions:
 * 1) listAllSalespersons(sc):
 *    - List all salespersons ordered by years of experience (ASC/DESC).
 *
 * 2) countTransactionRecords(sc):
 *    - Input a range [lowerBound, upperBound] on years of experience.
 *    - For each salesperson in that experience range, show number of transaction records.
 *
 * 3) showTotalSalesValue(sc):
 *    - Aggregate total sales value for each manufacturer (SUM of sold part prices),
 *      and sort by total sales value descending.
 *
 * - These functions mainly perform SELECT queries with grouping/aggregation and formatted output.
 * - Menu option "Show the N most popular part" is listed but may not be implemented yet.       
 * by Jeffery
 */


import java.sql.*;
import java.util.Scanner;

public class ManagerOperations {

    public static void showMenu(Scanner sc) {
        while (true) {
            System.out.println("\n-----Operations for manager menu-----");
            System.out.println("What kinds of operation would you like to perform?");
            System.out.println("1. List all salespersons");
            System.out.println("2. Count the no. of sales record of each salesperson under a specific range on years of experience");
            System.out.println("3. Show the total sales value of each manufacturer");
            System.out.println("4. Show the N most popular part");
            System.out.println("5. Return to the main menu");
            System.out.print("Enter Your Choice: ");

            int choice = sc.nextInt();
            sc.nextLine(); 

            switch (choice) {
                case 1:
                    listAllSalespersons(sc);
                    break;
                case 2:
                    countTransactionRecords(sc);
                    break;
                case 3:
                    showTotalSalesValue(sc);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("This operation isn't ready yet!");
            }
        }
    }


    private static void listAllSalespersons(Scanner sc) {
        System.out.println("Choose ordering:");
        System.out.println("1. By ascending order");
        System.out.println("2. By descending order");
        System.out.print("Choose the list ordering: ");
        int choice = sc.nextInt();
        sc.nextLine();

        String order;
        if (choice == 1){order = "ASC";}
        else if (choice == 2){order = "DESC";}
        else {order = "ASC";}
        
        String query = "SELECT SID, SNAME, SPHONENUMBER, SEXPERIENCE " +
                       "FROM salesperson " +
                       "ORDER BY SEXPERIENCE " + order;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            
            System.out.printf("| %-2s | %-20s | %-15s | %-20s |\n", "ID" , "Name", "Mobile Phone", "Years of Experience");
            while (rs.next()) {
                System.out.printf("| %-2d | %-20s | %-15d | %-20d |\n", rs.getInt("SID"), rs.getString("SNAME"), rs.getInt("SPHONENUMBER"), rs.getInt("SEXPERIENCE"));
            }
            System.out.println("End of Query");
            
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }


    private static void countTransactionRecords(Scanner sc) {
        System.out.print("Type in the lower bound for years of experience: ");
        int lowerbound = sc.nextInt();
        System.out.print("Type in the upper bound for years of experience: ");
        int upperbound = sc.nextInt();
        sc.nextLine();

        String query = "SELECT S.SID, S.SNAME, S.SPHONENUMBER, S.SEXPERIENCE, COUNT(T.TID) AS A " +
                       "FROM Salesperson S LEFT JOIN Transaction T ON S.SID = T.SID " +
                       "WHERE S.SEXPERIENCE " +"<= "+upperbound+" AND S.SEXPERIENCE "+">= "+lowerbound+" "+
                       "GROUP BY S.SID, S.SNAME, S.SPHONENUMBER, S.SEXPERIENCE " +
                       "ORDER BY S.SID DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

 
            try (ResultSet rs = pstmt.executeQuery()) {    
                
                System.out.printf("| %-2s | %-20s | %-20s | %-21s |\n", "ID" , "Name", "Years of Experience", "Number of Transaction");
                while (rs.next()) {
                    System.out.printf("| %-2d | %-20s | %-20d | %-21d |\n", rs.getInt("SID"), rs.getString("SNAME"), rs.getInt("SEXPERIENCE"), rs.getInt("A"));
                }
                System.out.println("End of Query");
            }

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }


    
    
    
    
    private static void showTotalSalesValue(Scanner sc) {
        String query = "SELECT M.MID, M.MName, SUM(P.PPRICE) AS A " +
                       "FROM Manufacturer M INNER JOIN part P ON P.MID = M.MID INNER JOIN transaction T ON T.PID = P.PID " +
                       "GROUP BY M.MID, M.MName "+
                       "ORDER BY SUM(P.PPRICE) DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.printf("| %-20s | %-20s | %-20s |\n", "Manufacturer ID" , "Manufacturer Name", "Total Sales Value");
            while (rs.next()) {
                System.out.printf("| %-20d | %-20s | %-20d |\n", rs.getInt("MID"), rs.getString("MNAME"), rs.getInt("A"));
            }
            System.out.println("End of Query");

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}

//1155214617