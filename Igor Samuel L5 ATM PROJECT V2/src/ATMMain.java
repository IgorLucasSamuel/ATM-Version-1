import system.ATMSystem;

/**
 * Main entry point for ATM System Version 1
 *
 * VERSION 1 FEATURES:
 * - User: Withdraw cash, Check balance
 * - Technician: View stock levels (READ ONLY)
 * - JSON Persistence: Auto-load on start, auto-save after sessions and on exit
 * - Stock Management: 10 units of each item (bank notes, ink, paper)
 *
 * HOW TO RUN:
 * Right-click this file → Run 'ATMMain.main()'
 */
public class ATMMain {

    public static void main(String[] args) {
        // Display welcome banner
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║          ATM SYSTEM - VERSION 1                    ║");
        System.out.println("╚════════════════════════════════════════════════════╝");

        // Show sample credentials for testing
        displayCredentials();

        // Create and start the ATM system
        ATMSystem atm = new ATMSystem();
        atm.start();  // This runs the main menu loop
    }

    /**
     * Display sample credentials for testing
     * These are the default accounts and technician logins
     * Only shown on first run (before JSON files exist)
     */
    private static void displayCredentials() {
        System.out.println("\n========== SAMPLE CREDENTIALS FOR TESTING ==========");

        System.out.println("\nUSER ACCOUNTS:");
        System.out.println("  Account: 1234567890, PIN: 1111, Balance: €1000.00");
        System.out.println("  Account: 0987654321, PIN: 2222, Balance: €500.00");
        System.out.println("  Account: 1111222233, PIN: 3333, Balance: €2500.00");

        System.out.println("\nTECHNICIAN ACCOUNTS:");
        System.out.println("  Username: tech1, Password: pass1");
        System.out.println("  Username: admin, Password: admin123");

        System.out.println("\nSTOCK LEVELS:");
        System.out.println("  Bank Notes: 10 of each denomination");
        System.out.println("  Ink: 10 units");
        System.out.println("  Paper: 10 units");
        System.out.println("  Total Cash: €8,850");

        System.out.println("\n====================================================");
    }
}
