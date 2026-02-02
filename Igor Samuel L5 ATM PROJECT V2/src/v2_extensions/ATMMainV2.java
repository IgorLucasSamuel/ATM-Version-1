package v2_extensions;

import v2_extensions.system.ATMSystemV2;

/**
 * Main entry point for ATM System Version 2
 *
 * VERSION 2 FEATURES (OCP Implementation):
 * - All V1 features: Withdraw, Deposit, Transfer, Check Balance
 * - NEW: Technician can restock bank notes, ink, and paper
 * - NEW: Technician can update firmware
 * - NEW: Enhanced machine status reporting
 *
 * DEMONSTRATES OPEN/CLOSED PRINCIPLE:
 * - V1 code remains UNCHANGED (closed for modification)
 * - V2 EXTENDS functionality through:
 *   - IStockManager interface (extends IStockViewer)
 *   - StockV2 class (extends Stock)
 *   - TechnicianWithRestocking (implements IStockManager)
 *   - ATMSystemV2 (extends ATMSystem)
 *
 * This architecture shows proper OCP compliance for university project
 */
public class ATMMainV2 {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║          ATM SYSTEM - VERSION 2                    ║");
        System.out.println("║     (Enhanced with OCP - Restocking Feature)       ║");
        System.out.println("╚════════════════════════════════════════════════════╝");

        displayCredentials();
        displayOCPInfo();

        ATMSystemV2 atm = new ATMSystemV2();
        atm.start();
    }

    /**
     * Display sample credentials for testing
     */
    private static void displayCredentials() {
        System.out.println("\n========== SAMPLE CREDENTIALS FOR TESTING ==========");
        System.out.println("\nUSER ACCOUNTS:");
        System.out.println("  Account: 1234567890, PIN: 1111, Balance: €1000.00");
        System.out.println("  Account: 0987654321, PIN: 2222, Balance: €500.00");
        System.out.println("  Account: 1111222233, PIN: 3333, Balance: €2500.00");

        System.out.println("\nTECHNICIAN ACCOUNTS (ENHANCED):");
        System.out.println("  Username: tech1, Password: pass1");
        System.out.println("  Username: admin, Password: admin123");

        System.out.println("\n====================================================");
    }

    /**
     * Display OCP implementation details (for academic demonstration)
     */
    private static void displayOCPInfo() {
        System.out.println("\n========== OCP IMPLEMENTATION DETAILS ==========");
        System.out.println("V1 Classes (UNCHANGED - Closed for Modification):");
        System.out.println("  - User, Account, Transaction, BankNote");
        System.out.println("  - Stock (only access modifiers: private → protected)");
        System.out.println("  - IStockViewer interface");
        System.out.println("  - Technician (read-only)");
        System.out.println("  - ATMSystem");

        System.out.println("\nV2 Extensions (ADDED - Open for Extension):");
        System.out.println("  - IStockManager (extends IStockViewer)");
        System.out.println("  - StockV2 (extends Stock, adds restocking)");
        System.out.println("  - TechnicianWithRestocking (implements IStockManager)");
        System.out.println("  - ATMSystemV2 (extends ATMSystem)");

        System.out.println("\nNEW FEATURES in V2:");
        System.out.println("  ✓ Technician can restock bank notes");
        System.out.println("  ✓ Technician can restock ink");
        System.out.println("  ✓ Technician can restock paper");
        System.out.println("  ✓ Technician can update firmware");
        System.out.println("  ✓ Enhanced machine status reporting");
        System.out.println("================================================\n");
    }
}