package v2_extensions.system;

import models.Account;
import models.BankNote;
import roles.User;
import system.ATMSystem;
import v2_extensions.roles.TechnicianWithRestocking;

/**
 * Enhanced ATM System for Version 2
 *
 * DEMONSTRATES OCP (Open/Closed Principle):
 * - EXTENDS system.ATMSystem from V1 (V1 remains unchanged)
 * - ADDS restocking capabilities and firmware management
 * - Uses V1 classes (User, Account) without changes
 * - Uses StockV2 (extends V1 Stock) for enhanced functionality
 *
 * IMPORTANT: Preserves JSON-loaded data when upgrading from V1 to V2
 */
public class ATMSystemV2 extends ATMSystem {
    private String firmwareVersion;  // NEW in V2: Track firmware version

    /**
     * Constructor: Initialize V2 system
     * Calls parent (V1) constructor, then upgrades to V2 stock
     */
    public ATMSystemV2() {
        super();  // Initialize V1 ATMSystem (loads data, initializes accounts)
        this.firmwareVersion = "v2.0.0";  // Initial V2 firmware version

        // Upgrade existing stock to V2 while preserving loaded data
        upgradeStockToV2();

        System.out.println("✓ ATM System V2 initialized (Firmware: " + firmwareVersion + ")");
    }

    /**
     * Upgrade V1 Stock to V2 Stock while preserving data
     * This ensures JSON-loaded data is not lost
     */
    private void upgradeStockToV2() {
        // Save current stock state from V1
        int currentInk = stock.getInkLevel();
        int currentPaper = stock.getPaperLevel();
        java.util.Map<BankNote, Integer> currentBankNotes = stock.getAllBankNotes();

        // Create new StockV2 and restore data
        StockV2 newStock = new StockV2();
        newStock.setInkLevel(currentInk);
        newStock.setPaperLevel(currentPaper);
        newStock.setBankNotes(currentBankNotes);

        // Replace stock with V2 version
        this.stock = newStock;
    }

    /**
     * Start the ATM system (OVERRIDES V1 start method)
     * Enhanced to show V2 branding and use V2 technician
     */
    @Override
    public void start() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║     WELCOME TO THE ATM SYSTEM (V2)     ║");
        System.out.println("║      Enhanced with Restocking!         ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        while (true) {
            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. User Login");
            System.out.println("2. Technician Login (Enhanced)");
            System.out.println("3. Exit");
            System.out.println("================================");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    userLoginV2();
                    break;
                case "2":
                    technicianLoginV2();
                    break;
                case "3":
                    saveData();
                    System.out.println("\nThank you for using our ATM V2. Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    /**
     * Handle user login (uses V1 User class - unchanged)
     */
    private void userLoginV2() {
        System.out.println("\n========== USER LOGIN ==========");
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine().trim();

        Account account = getAccountByNumber(accountNumber);
        if (account == null) {
            System.out.println("Account not found. Please try again.\n");
            return;
        }

        int attempts = 3;
        while (attempts > 0) {
            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine().trim();

            if (account.validatePin(pin)) {
                System.out.println("\n✓ Login successful!");
                System.out.println("Welcome, Account: " + accountNumber);

                User user = new User(account, this, scanner);
                user.showMenu();

                saveData();
                return;
            } else {
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
     * Handle technician login (uses NEW TechnicianWithRestocking from V2)
     */
    private void technicianLoginV2() {
        System.out.println("\n========== TECHNICIAN LOGIN (V2 - Enhanced) ==========");
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        if (technicianCredentials.containsKey(username) &&
                technicianCredentials.get(username).equals(password)) {

            System.out.println("\n✓ Technician login successful!");
            System.out.println("Welcome, " + username + " (V2 Enhanced Access)");

            TechnicianWithRestocking technician = new TechnicianWithRestocking(this, scanner);
            technician.showMenu();

            saveData();
        } else {
            System.out.println("Invalid credentials. Access denied.\n");
        }
    }

    // ========== NEW V2 Methods: Restocking Capabilities ==========

    /**
     * Restock bank notes
     * NEW in V2 - delegates to StockV2
     */
    public void restockBankNotes(BankNote note, int quantity) {
        ((StockV2) stock).addBankNotes(note, quantity);
    }

    /**
     * Restock ink
     * NEW in V2 - delegates to StockV2
     */
    public void restockInk(int quantity) {
        ((StockV2) stock).addInk(quantity);
    }

    /**
     * Restock paper
     * NEW in V2 - delegates to StockV2
     */
    public void restockPaper(int quantity) {
        ((StockV2) stock).addPaper(quantity);
    }

    /**
     * Get bank note count
     * Exposes stock information for technician
     */
    public int getBankNoteCount(BankNote note) {
        return stock.getBankNoteCount(note);
    }

    /**
     * Get ink level
     * Exposes stock information for technician
     */
    public int getInkLevel() {
        return stock.getInkLevel();
    }

    /**
     * Get paper level
     * Exposes stock information for technician
     */
    public int getPaperLevel() {
        return stock.getPaperLevel();
    }

    // ========== NEW V2 Methods: Firmware Management ==========

    /**
     * Get current firmware version
     * NEW in V2
     */
    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    /**
     * Update firmware version
     * NEW in V2 - simulates firmware update
     */
    public void updateFirmware(String newVersion) {
        this.firmwareVersion = newVersion;
        System.out.println("Firmware updated to: " + firmwareVersion);
    }

    /**
     * Display comprehensive machine status for V2
     */
    public void displayMachineStatus() {
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║              MACHINE STATUS REPORT (V2)            ║");
        System.out.println("╚════════════════════════════════════════════════════╝");

        double totalCash = stock.getTotalCash();
        int inkLevel = stock.getInkLevel();
        int paperLevel = stock.getPaperLevel();

        // Determine status
        String status = "✓ OPERATIONAL";
        if (totalCash == 0) {
            status = "❌ CRITICAL - OUT OF CASH";
        } else if (inkLevel == 0 || paperLevel == 0) {
            status = "⚠️  WARNING - NO RECEIPT SUPPLIES";
        } else if (inkLevel <= 5 || paperLevel <= 5 || totalCash < 500) {
            status = "⚠️  WARNING - LOW SUPPLIES";
        }

        System.out.println("\n========== OPERATIONAL STATUS ==========");
        System.out.println("Overall Status: " + status);
        System.out.println();

        System.out.println("--- STOCK SUMMARY ---");
        System.out.printf("Total Cash: €%.2f %s\n", totalCash,
                totalCash == 0 ? "❌" : totalCash < 500 ? "⚠️" : "✓");
        System.out.printf("Ink Level: %d units %s\n", inkLevel,
                inkLevel == 0 ? "❌" : inkLevel <= 5 ? "⚠️" : "✓");
        System.out.printf("Paper Level: %d units %s\n", paperLevel,
                paperLevel == 0 ? "❌" : paperLevel <= 5 ? "⚠️" : "✓");

        System.out.println();
        System.out.println("--- CAPABILITIES ---");
        System.out.printf("Can Dispense Cash: %s\n", totalCash > 0 ? "YES ✓" : "NO ❌");
        System.out.printf("Can Print Receipts: %s\n",
                (inkLevel > 0 && paperLevel > 0) ? "YES ✓" : "NO ❌");

        System.out.println("\n========== SYSTEM INFORMATION ==========");
        System.out.println("--- VERSION INFO ---");
        System.out.println("ATM Version: V2.0 (Enhanced)");
        System.out.println("Firmware: " + firmwareVersion);

        System.out.println();
        System.out.println("--- ACCOUNTS ---");
        System.out.printf("Total User Accounts: %d\n", getTotalAccounts());
        System.out.printf("Total Technicians: %d\n", getTotalTechnicians());

        System.out.println();
        System.out.println("--- FEATURES ---");
        System.out.println("✓ Cash Withdrawal & Deposit");
        System.out.println("✓ Money Transfer");
        System.out.println("✓ Balance Inquiry");
        System.out.println("✓ Stock Restocking (V2)");
        System.out.println("✓ Firmware Updates (V2)");
        System.out.println("✓ JSON Persistence");

        System.out.println("\n========================================\n");
    }
}