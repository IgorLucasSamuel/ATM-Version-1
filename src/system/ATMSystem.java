package system;

import models.Account;
import roles.User;
import roles.Technician;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Main ATM System controller for Version 1
 *
 * This class manages:
 * 1. User authentication (account + PIN)
 * 2. Technician authentication (username + password)
 * 3. Stock management
 * 4. JSON persistence (auto-load on start, auto-save after sessions)
 * 5. Menu navigation
 *
 * IMPORTANT: Uses 'protected' fields to allow V2 extension (OCP principle)
 */
public class ATMSystem {
    // Protected allows ATMSystemV2 to access these fields
    protected Stock stock;                                  // ATM physical stock
    protected Map<String, Account> accounts;                // User accounts (key = account number)
    protected Map<String, String> technicianCredentials;    // Technician logins (key = username, value = password)
    protected Scanner scanner;                              // For user input
    protected PersistenceManager persistenceManager;        // Handles JSON save/load

    /**
     * Constructor: Initialize the ATM system
     * 1. Create empty data structures
     * 2. Create PersistenceManager
     * 3. Load existing data from JSON files
     * 4. If no data exists, initialize with default values
     */
    public ATMSystem() {
        this.stock = new Stock();
        this.accounts = new HashMap<>();
        this.technicianCredentials = new HashMap<>();
        this.scanner = new Scanner(System.in);
        this.persistenceManager = new PersistenceManager();

        // Try to load existing data
        loadData();

        // If no data was loaded (first run), initialize with defaults
        if (accounts.isEmpty()) {
            initializeAccounts();
        }
        if (technicianCredentials.isEmpty()) {
            initializeTechnicians();
        }
    }

    /**
     * Load all data from JSON files
     * Called when ATM starts up
     */
    private void loadData() {
        this.accounts = persistenceManager.loadAccounts();
        this.technicianCredentials = persistenceManager.loadTechnicians();
        persistenceManager.loadStock(stock);
        System.out.println("✓ Data loaded from JSON files.");
    }

    /**
     * Save all data to JSON files
     * Called after each user/technician session and on exit
     */
    public void saveData() {
        persistenceManager.saveAccounts(accounts);
        persistenceManager.saveTechnicians(technicianCredentials);
        persistenceManager.saveStock(stock);
        System.out.println("✓ Data saved to JSON files.");
    }

    /**
     * Initialize default user accounts
     * Only called if no accounts.json file exists (first run)
     */
    private void initializeAccounts() {
        accounts.put("1234567890", new Account("1234567890", "1111", 1000.00));
        accounts.put("0987654321", new Account("0987654321", "2222", 500.00));
        accounts.put("1111222233", new Account("1111222233", "3333", 2500.00));
    }

    /**
     * Initialize default technician credentials
     * Only called if no technicians.json file exists (first run)
     */
    private void initializeTechnicians() {
        technicianCredentials.put("tech1", "pass1");
        technicianCredentials.put("admin", "admin123");
    }

    /**
     * Start the ATM system - main entry point
     * Displays main menu and handles user/technician login
     */
    public void start() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║     WELCOME TO THE ATM SYSTEM (V1)     ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        // Main menu loop
        while (true) {
            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. User Login");
            System.out.println("2. Technician Login");
            System.out.println("3. Exit");
            System.out.println("================================");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    userLogin();
                    break;
                case "2":
                    technicianLogin();
                    break;
                case "3":
                    saveData();  // Save before exiting
                    System.out.println("\nThank you for using our ATM. Goodbye!");
                    scanner.close();
                    return;  // Exit the program
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    /**
     * Handle user login process
     * 1. Ask for account number
     * 2. Verify account exists
     * 3. Ask for PIN (3 attempts)
     * 4. If successful, show user menu
     * 5. Save data after user session ends
     */
    private void userLogin() {
        System.out.println("\n========== USER LOGIN ==========");
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine().trim();

        // Check if account exists
        Account account = accounts.get(accountNumber);
        if (account == null) {
            System.out.println("Account not found. Please try again.\n");
            return;
        }

        // PIN verification (3 attempts)
        int attempts = 3;
        while (attempts > 0) {
            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine().trim();

            if (account.validatePin(pin)) {
                // PIN correct - create User object and show their menu
                System.out.println("\n✓ Login successful!");
                System.out.println("Welcome, Account: " + accountNumber);

                User user = new User(account, this, scanner);
                user.showMenu();  // User interacts with ATM

                saveData();  // Save changes after user session
                return;
            } else {
                // PIN incorrect
                attempts--;
                if (attempts > 0) {
                    System.out.println("Incorrect PIN. " + attempts + " attempt(s) remaining.");
                } else {
                    System.out.println("Too many failed attempts. Returning to main menu.\n");
                }
            }
        }
    }

    /**
     * Handle technician login process
     * 1. Ask for username
     * 2. Ask for password
     * 3. Verify credentials
     * 4. If successful, show technician menu
     * 5. Save data after technician session ends
     */
    private void technicianLogin() {
        System.out.println("\n========== TECHNICIAN LOGIN ==========");
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        // Verify credentials
        String correctPassword = technicianCredentials.get(username);

        if (correctPassword != null && correctPassword.equals(password)) {
            // Credentials correct - create Technician object and show their menu
            System.out.println("\n✓ Technician login successful!");
            System.out.println("Welcome, " + username);

            Technician technician = new Technician(this, scanner);
            technician.showMenu();  // Technician views stock

            saveData();  // Save changes after technician session
        } else {
            System.out.println("Invalid credentials. Access denied.\n");
        }
    }

    // ========== PUBLIC METHODS (used by User and Technician classes) ==========

    /**
     * Check if ATM can dispense the requested amount
     * Delegates to Stock class
     *
     * @param amount Amount to check
     * @return true if ATM can dispense, false otherwise
     */
    public boolean canDispenseCash(double amount) {
        return stock.canDispense(amount);
    }

    /**
     * Dispense cash from ATM stock
     * Delegates to Stock class
     *
     * @param amount Amount to dispense
     * @return true if successful, false otherwise
     */
    public boolean dispenseCash(double amount) {
        return stock.dispenseCash(amount);
    }

    /**
     * Display current stock levels
     * Used by technicians to view stock
     * Delegates to Stock class
     */
    public void displayStockLevels() {
        stock.displayStockLevels();
    }

    /**
     * Get stock instance
     * Used for potential future extensions
     *
     * @return The Stock object
     */
    public Stock getStock() {
        return stock;
    }
    /**
     * Get an account by account number
     * Used for transfers between accounts
     *
     * @param accountNumber The account number to find
     * @return Account object or null if not found
     */
    public Account getAccountByNumber(String accountNumber) {
        return accounts.get(accountNumber);
    }
    /**
     * Get total number of user accounts in the system
     * Used by technicians to view system information
     *
     * @return Number of user accounts
     */
    public int getTotalAccounts() {
        return accounts.size();
    }

    /**
     * Get total number of technicians in the system
     * Used by technicians to view system information
     *
     * @return Number of technicians
     */
    public int getTotalTechnicians() {
        return technicianCredentials.size();
    }
}
