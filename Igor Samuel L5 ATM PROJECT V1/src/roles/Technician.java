package roles;

import interfaces.IStockViewer;
import system.ATMSystem;
import system.Stock;
import java.util.Scanner;

/**
 * Technician role for Version 1
 * Technicians can ONLY VIEW stock levels and machine status (READ-ONLY access)
 * They implement the IStockViewer interface
 *
 * NOTE: In V2, we'll create an enhanced technician with restocking capabilities
 */
public class Technician implements IStockViewer {
    private ATMSystem atmSystem;  // Reference to ATM system to view stock
    private Scanner scanner;      // For reading user input

    /**
     * Constructor: Creates a Technician with access to the ATM system
     *
     * @param atmSystem The ATM system they're maintaining
     * @param scanner Scanner for user input
     */
    public Technician(ATMSystem atmSystem, Scanner scanner) {
        this.atmSystem = atmSystem;
        this.scanner = scanner;
    }

    /**
     * Display technician menu (Version 1 - read-only)
     * In V1, technicians can ONLY view stock levels and machine status
     */
    public void showMenu() {
        while (true) {
            System.out.println("\n========== TECHNICIAN MENU (V1) ==========");
            System.out.println("1. View Stock Levels");
            System.out.println("2. View Machine Status");
            System.out.println("3. Logout");
            System.out.println("===========================================");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewStockLevels();  // Calls the interface method
                    break;
                case "2":
                    viewMachineStatus();
                    break;
                case "3":
                    logout();
                    return;  // Exit menu loop
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    /**
     * View current stock levels
     * Implementation of IStockViewer interface method
     *
     * This delegates to the ATM system to display stock information
     */
    @Override
    public void viewStockLevels() {
        atmSystem.displayStockLevels();
    }

    /**
     * View comprehensive machine status
     * Shows BOTH operational status AND system information
     */
    private void viewMachineStatus() {
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║              MACHINE STATUS REPORT                 ║");
        System.out.println("╚════════════════════════════════════════════════════╝");

        Stock stock = atmSystem.getStock();

        // Get current data
        double totalCash = stock.getTotalCash();
        int inkLevel = stock.getInkLevel();
        int paperLevel = stock.getPaperLevel();

        // ========== SECTION 1: OPERATIONAL STATUS ==========
        System.out.println("\n========== OPERATIONAL STATUS ==========");

        // Determine overall status
        String status = "✓ OPERATIONAL";
        boolean hasWarnings = false;
        boolean hasCritical = false;

        // Check for critical issues
        if (totalCash == 0) {
            status = "❌ CRITICAL - OUT OF CASH";
            hasCritical = true;
        } else if (inkLevel == 0 && paperLevel == 0) {
            status = "⚠️  WARNING - NO RECEIPT SUPPLIES";
            hasWarnings = true;
        } else if (inkLevel <= 5 || paperLevel <= 5) {
            status = "⚠️  WARNING - LOW SUPPLIES";
            hasWarnings = true;
        } else if (totalCash < 500) {
            status = "⚠️  WARNING - LOW CASH";
            hasWarnings = true;
        }

        System.out.println("Overall Status: " + status);
        System.out.println();

        // Stock summary
        System.out.println("--- STOCK SUMMARY ---");
        System.out.printf("Total Cash Available: €%.2f %s\n", totalCash,
                totalCash == 0 ? "❌" : totalCash < 500 ? "⚠️" : "✓");
        System.out.printf("Ink Level: %d units %s\n", inkLevel,
                inkLevel == 0 ? "❌" : inkLevel <= 5 ? "⚠️" : "✓");
        System.out.printf("Paper Level: %d units %s\n", paperLevel,
                paperLevel == 0 ? "❌" : paperLevel <= 5 ? "⚠️" : "✓");

        // Operational capabilities
        System.out.println();
        System.out.println("--- CAPABILITIES ---");
        boolean canDispense = totalCash > 0;
        boolean canPrintReceipts = (inkLevel > 0 && paperLevel > 0);
        System.out.printf("Can Dispense Cash: %s\n", canDispense ? "YES ✓" : "NO ❌");
        System.out.printf("Can Print Receipts: %s\n", canPrintReceipts ? "YES ✓" : "NO ❌");

        // Display issues if any
        if (hasCritical || hasWarnings) {
            System.out.println();
            System.out.println("--- ISSUES DETECTED ---");
            if (totalCash == 0) {
                System.out.println("❌ CRITICAL: ATM has no cash - cannot process withdrawals");
                System.out.println("   ACTION REQUIRED: Immediate restocking needed");
            } else if (totalCash < 500) {
                System.out.println("⚠️  WARNING: Cash running low (below €500)");
                System.out.println("   RECOMMENDATION: Schedule restocking soon");
            }
            if (inkLevel == 0) {
                System.out.println("❌ CRITICAL: No ink - receipts cannot be printed");
                System.out.println("   ACTION REQUIRED: Replace ink cartridge");
            } else if (inkLevel <= 5) {
                System.out.println("⚠️  WARNING: Ink running low (≤5 units)");
                System.out.println("   RECOMMENDATION: Replace ink cartridge soon");
            }
            if (paperLevel == 0) {
                System.out.println("❌ CRITICAL: No paper - receipts cannot be printed");
                System.out.println("   ACTION REQUIRED: Replace paper roll");
            } else if (paperLevel <= 5) {
                System.out.println("⚠️  WARNING: Paper running low (≤5 units)");
                System.out.println("   RECOMMENDATION: Replace paper roll soon");
            }
        } else {
            System.out.println();
            System.out.println("✓ No issues detected - ATM operating normally");
        }

        // ========== SECTION 2: SYSTEM INFORMATION ==========
        System.out.println("\n========== SYSTEM INFORMATION ==========");

        // Get system data from ATM
        int totalAccounts = atmSystem.getTotalAccounts();
        int totalTechnicians = atmSystem.getTotalTechnicians();

        System.out.println("--- ACCOUNTS ---");
        System.out.printf("Total User Accounts: %d\n", totalAccounts);
        System.out.printf("Total Technicians: %d\n", totalTechnicians);

        System.out.println();
        System.out.println("--- CONFIGURATION ---");
        System.out.println("ATM Model: Standard v1.0");
        System.out.println("Persistence: JSON (Auto-save enabled)");
        System.out.println("Data Location: data/ folder");

        System.out.println();
        System.out.println("--- MAINTENANCE ---");
        System.out.println("Last Maintenance: N/A (V1 - read-only mode)");
        System.out.println("Next Scheduled: Upgrade to V2 for maintenance tracking");
        System.out.println("Technician Mode: READ-ONLY (no restocking available)");

        System.out.println("\n========================================");
        System.out.println("Note: Upgrade to Version 2 for restocking capabilities");
        System.out.println("========================================\n");
    }

    /**
     * Logout technician and return to main menu
     */
    private void logout() {
        System.out.println("\n✓ Technician logged out.");
        System.out.println("Returning to main menu...\n");
    }
}