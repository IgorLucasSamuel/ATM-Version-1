package roles;

import models.Account;
import system.ATMSystem;
import java.util.Scanner;

/**
 * User role - represents a customer using the ATM
 * Users can:
 * - Withdraw cash
 * - Deposit cash
 * - Transfer money to another account
 * - Check their account balance
 */
public class User {
    private Account account;           // The user's bank account
    private ATMSystem atmSystem;       // Reference to the ATM system for transactions
    private Scanner scanner;           // For reading user input

    /**
     * Constructor: Creates a User with their account and ATM system access
     *
     * @param account The user's bank account
     * @param atmSystem The ATM system they're using
     * @param scanner Scanner for user input
     */
    public User(Account account, ATMSystem atmSystem, Scanner scanner) {
        this.account = account;
        this.atmSystem = atmSystem;
        this.scanner = scanner;
    }

    /**
     * Display user menu and handle user actions
     * This is the main interaction loop for the user
     */
    public void showMenu() {
        while (true) {
            System.out.println("\n========== USER MENU ==========");
            System.out.println("1. Check Balance");
            System.out.println("2. Withdraw Cash");
            System.out.println("3. Deposit Cash");
            System.out.println("4. Transfer Money");
            System.out.println("5. Logout");
            System.out.println("================================");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();

            // Handle user's menu choice
            switch (choice) {
                case "1":
                    checkBalance();
                    break;
                case "2":
                    withdrawCash();
                    break;
                case "3":
                    depositCash();
                    break;
                case "4":
                    transferMoney();
                    break;
                case "5":
                    logout();
                    return;  // Exit the menu loop
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    /**
     * Display the user's current account balance
     */
    private void checkBalance() {
        System.out.println("\n========== BALANCE INQUIRY ==========");
        System.out.printf("Account: %s\n", account.getAccountNumber());
        System.out.printf("Current Balance: €%.2f\n", account.getBalance());
        System.out.println("=====================================\n");
    }

    /**
     * Handle cash withdrawal process
     * Validates amount, checks ATM stock, checks account balance,
     * then processes the withdrawal
     */
    private void withdrawCash() {
        System.out.println("\n========== WITHDRAW CASH ==========");
        System.out.print("Enter amount to withdraw: €");

        try {
            double amount = Double.parseDouble(scanner.nextLine().trim());

            // VALIDATION 1: Amount must be positive
            if (amount <= 0) {
                System.out.println("Invalid amount. Please enter a positive value.");
                return;
            }

            // VALIDATION 2: Amount must be multiple of 5 (smallest note is €5)
            if (amount % 5 != 0) {
                System.out.println("Amount must be a multiple of €5.");
                return;
            }

            // VALIDATION 3: Check if ATM has enough stock (notes, ink, paper)
            if (!atmSystem.canDispenseCash(amount)) {
                System.out.println("ATM cannot dispense this amount due to insufficient stock.");
                System.out.println("Please try a smaller amount or contact support.");
                return;
            }

            // VALIDATION 4: Check if user has enough money in their account
            if (amount > account.getBalance()) {
                System.out.println("Insufficient funds in your account.");
                System.out.printf("Your current balance is: €%.2f\n", account.getBalance());
                return;
            }

            // PROCESS WITHDRAWAL: Deduct from account AND from ATM stock
            if (account.withdraw(amount) && atmSystem.dispenseCash(amount)) {
                System.out.println("\n✓ Withdrawal successful!");
                System.out.printf("Amount withdrawn: €%.2f\n", amount);
                System.out.printf("New balance: €%.2f\n", account.getBalance());
                System.out.println("Please take your cash and receipt.");
            } else {
                System.out.println("Withdrawal failed. Please try again.");
            }

        } catch (NumberFormatException e) {
            // Handle case where user enters non-numeric input
            System.out.println("Invalid amount entered.");
        }
        System.out.println("===================================\n");
    }

    /**
     * Handle cash deposit process
     * User deposits physical cash into the ATM
     * This increases their account balance
     */
    private void depositCash() {
        System.out.println("\n========== DEPOSIT CASH ==========");
        System.out.print("Enter amount to deposit: €");

        try {
            double amount = Double.parseDouble(scanner.nextLine().trim());

            // VALIDATION: Amount must be positive
            if (amount <= 0) {
                System.out.println("Invalid amount. Please enter a positive value.");
                return;
            }

            // VALIDATION: Amount should be reasonable (max €10,000 per deposit)
            if (amount > 10000) {
                System.out.println("Deposit limit is €10,000 per transaction.");
                System.out.println("For larger deposits, please visit a branch.");
                return;
            }

            // PROCESS DEPOSIT: Add to account balance
            account.setBalance(account.getBalance() + amount);
            System.out.println("\n✓ Deposit successful!");
            System.out.printf("Amount deposited: €%.2f\n", amount);
            System.out.printf("New balance: €%.2f\n", account.getBalance());

        } catch (NumberFormatException e) {
            System.out.println("Invalid amount entered.");
        }
        System.out.println("==================================\n");
    }

    /**
     * Handle money transfer to another account
     * User can send money from their account to another account
     */
    private void transferMoney() {
        System.out.println("\n========== TRANSFER MONEY ==========");
        System.out.print("Enter recipient account number: ");
        String recipientAccountNumber = scanner.nextLine().trim();

        // VALIDATION 1: Can't transfer to yourself
        if (recipientAccountNumber.equals(account.getAccountNumber())) {
            System.out.println("Cannot transfer money to your own account.");
            return;
        }

        // VALIDATION 2: Check if recipient account exists
        Account recipientAccount = atmSystem.getAccountByNumber(recipientAccountNumber);
        if (recipientAccount == null) {
            System.out.println("Recipient account not found.");
            return;
        }

        System.out.print("Enter amount to transfer: €");

        try {
            double amount = Double.parseDouble(scanner.nextLine().trim());

            // VALIDATION 3: Amount must be positive
            if (amount <= 0) {
                System.out.println("Invalid amount. Please enter a positive value.");
                return;
            }

            // VALIDATION 4: Check if user has enough balance
            if (amount > account.getBalance()) {
                System.out.println("Insufficient funds in your account.");
                System.out.printf("Your current balance is: €%.2f\n", account.getBalance());
                return;
            }

            // PROCESS TRANSFER: Deduct from sender, add to recipient
            account.setBalance(account.getBalance() - amount);
            recipientAccount.setBalance(recipientAccount.getBalance() + amount);

            System.out.println("\n✓ Transfer successful!");
            System.out.printf("Amount transferred: €%.2f\n", amount);
            System.out.printf("To account: %s\n", recipientAccountNumber);
            System.out.printf("Your new balance: €%.2f\n", account.getBalance());

        } catch (NumberFormatException e) {
            System.out.println("Invalid amount entered.");
        }
        System.out.println("====================================\n");
    }

    /**
     * Logout user and return to main menu
     */
    private void logout() {
        System.out.println("\n✓ Logging out...");
        System.out.println("Thank you for using our ATM!\n");
    }
}