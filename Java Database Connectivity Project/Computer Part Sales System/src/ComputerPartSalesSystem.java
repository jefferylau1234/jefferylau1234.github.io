/* 
 * The main entry point (main class) of the JDBC Sales System.
 * Responsibilities:
 * - Provide the top-level CLI menu (Administrator / Salesperson / Manager / Exit).
 * - Dispatch user choice to the corresponding operation module:
 *     AdminOperations.showMenu(sc), SalesOperations.showMenu(sc), ManagerOperations.showMenu(sc).
 *
 * - This class focuses on UI flow only; database logic is implemented in the Operations classes.
 * - Scanner is shared and passed to sub-menus to reuse the same input stream.
 * by Jeffery
 */



import java.util.Scanner;

public class ComputerPartSalesSystem {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("Welcome to sales system!");
            System.out.println("\n-----Main menu-----");
            System.out.println("What kinds of operation would you like to perform?");
            System.out.println("1. Operations for administrator");
            System.out.println("2. Operations for salesperson");
            System.out.println("3. Operations for manager");
            System.out.println("4. Exit this program");
            System.out.print("Enter Your Choice: ");
            int choice = sc.nextInt();
            sc.nextLine();  

            switch (choice) {
                case 1:
                    AdminOperations.showMenu(sc);
                    break;
                case 2:
                    SalesOperations.showMenu(sc);
                    break;
                case 3:
                    ManagerOperations.showMenu(sc);
                    break;
                case 4:
                    System.out.println("Bye bye!");
                    return;
                default:
                    System.out.println("This option is not implemented yet.");
            }
        }
    }
}