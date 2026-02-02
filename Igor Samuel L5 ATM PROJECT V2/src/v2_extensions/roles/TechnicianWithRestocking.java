package v2_extensions.roles;

import models.BankNote;
import v2_extensions.interfaces.IStockManager;
import v2_extensions.system.ATMSystemV2;
import java.util.Scanner;

/**
 * Enhanced Technician role for Version 2
 * Implements IStockManager interface (which extends IStockViewer from V1)
 *
 * NEW CAPABILITIES in V2:
 * - View stock levels (inherited from IStockViewer)
 * - Restock bank notes (NEW)
 * - Restock ink (NEW)
 * - Restock paper (NEW)
 * - Update firmware (NEW)
 *
 * This demonstrates OCP: New functionality added through interface extension
 * without modifying V1 Technician class
 */
public class TechnicianWithRestocking implements IStockManager {
    private ATMSystemV2 atmSystem;
    private Scanner scanner;

    public TechnicianWithRestocking(ATMSystemV2 atmSystem, Scanner scanner) {
        this.atmSystem = atmSystem;
        this.scanner = scanner;
    }

    /**
     * Display enhanced technician menu with restocking and firmware options
     */
    public void showMenu() {
        while (true) {
            System.out.println("\n========== TECHNICIAN MENU (V2 - Enhanced) ==========");
            System.out.println("1. View Stock Levels");
            System.out.println("2. View Machine Status");
            System.out.println("3. Restock Bank Notes");
            System.out.println("4. Restock Ink");
            System.out.println("5. Restock Paper");
            System.out.println("6. Update Firmware");
            System.out.println("7. Logout");
            System.out.println("=====================================================");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewStockLevels();
                    break;
                case "2":
                    viewMachineStatus();
                    break;
                case "3":
                    restockBankNotesMenu();
                    break;
                case "4":
                    restockInkMenu();
                    break;
                case "5":
                    restockPaperMenu();
                    break;
                case "6":
                    updateFirmware();
                    break;
                case "7":
                    logout();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    /**
     * View current stock levels (inherited from IStockViewer)
     */
    @Override
    public void viewStockLevels() {
        atmSystem.displayStockLevels();
    }

    /**
     * View machine status (delegates to ATM system)
     */
    private void viewMachineStatus() {
        atmSystem.displayMachineStatus();
    }

    /**
     * Menu for restocking bank notes
     */
    private void restockBankNotesMenu() {
        System.out.println("\n========== RESTOCK BANK NOTES ==========");
        System.out.println("Select denomination to restock:");
        System.out.println("1. €5");
        System.out.println("2. €10");
        System.out.println("3. €20");
        System.out.println("4. €50");
        System.out.println("5. €100");
        System.out.println("6. €200");
        System.out.println("7. €500");
        System.out.println("0. Cancel");
        System.out.println("========================================");
        System.out.print("Select option: ");

        String choice = scanner.nextLine().trim();

        BankNote selectedNote = null;
        switch (choice) {
            case "1": selectedNote = BankNote.FIVE; break;
            case "2": selectedNote = BankNote.TEN; break;
            case "3": selectedNote = BankNote.TWENTY; break;
            case "4": selectedNote = BankNote.FIFTY; break;
            case "5": selectedNote = BankNote.ONE_HUNDRED; break;
            case "6": selectedNote = BankNote.TWO_HUNDRED; break;
            case "7": selectedNote = BankNote.FIVE_HUNDRED; break;
            case "0":
                System.out.println("Cancelled.");
                return;
            default:
                System.out.println("Invalid selection.");
                return;
        }

        System.out.print("Enter quantity to add: ");
        try {
            int quantity = Integer.parseInt(scanner.nextLine().trim());
            restockBankNotes(selectedNote, quantity);
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity entered.");
        }
    }

    /**
     * Restock bank notes (implementation of IStockManager)
     */
    @Override
    public void restockBankNotes(BankNote note, int quantity) {
        if (quantity <= 0) {
            System.out.println("Quantity must be positive.");
            return;
        }

        atmSystem.restockBankNotes(note, quantity);
        System.out.println("New total for " + note + ": " + atmSystem.getBankNoteCount(note) + " notes");
    }

    /**
     * Menu for restocking ink
     */
    private void restockInkMenu() {
        System.out.println("\n========== RESTOCK INK ==========");
        System.out.printf("Current ink level: %d units\n", atmSystem.getInkLevel());
        System.out.print("Enter quantity to add: ");

        try {
            int quantity = Integer.parseInt(scanner.nextLine().trim());
            restockInk(quantity);
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity entered.");
        }
    }

    /**
     * Restock ink (implementation of IStockManager)
     */
    @Override
    public void restockInk(int quantity) {
        if (quantity <= 0) {
            System.out.println("Quantity must be positive.");
            return;
        }

        atmSystem.restockInk(quantity);
        System.out.println("New ink level: " + atmSystem.getInkLevel() + " units");
    }

    /**
     * Menu for restocking paper
     */
    private void restockPaperMenu() {
        System.out.println("\n========== RESTOCK PAPER ==========");
        System.out.printf("Current paper level: %d units\n", atmSystem.getPaperLevel());
        System.out.print("Enter quantity to add: ");

        try {
            int quantity = Integer.parseInt(scanner.nextLine().trim());
            restockPaper(quantity);
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity entered.");
        }
    }

    /**
     * Restock paper (implementation of IStockManager)
     */
    @Override
    public void restockPaper(int quantity) {
        if (quantity <= 0) {
            System.out.println("Quantity must be positive.");
            return;
        }

        atmSystem.restockPaper(quantity);
        System.out.println("New paper level: " + atmSystem.getPaperLevel() + " units");
    }

    /**
     * Update ATM firmware
     * NEW feature in V2 - simulates firmware update process
     */
    private void updateFirmware() {
        System.out.println("\n========== FIRMWARE UPDATE ==========");
        System.out.println("Current Firmware: " + atmSystem.getFirmwareVersion());
        System.out.println("Available Update: v2.1.0");
        System.out.println();
        System.out.print("Do you want to proceed with the update? (yes/no): ");

        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("yes") || confirm.equals("y")) {
            System.out.println("\n⏳ Starting firmware update...");
            simulateProgress("Downloading firmware", 3);
            simulateProgress("Verifying package", 2);
            simulateProgress("Installing update", 4);
            simulateProgress("Rebooting system", 2);

            atmSystem.updateFirmware("v2.1.0");

            System.out.println("\n✓ Firmware updated successfully!");
            System.out.println("New version: " + atmSystem.getFirmwareVersion());
            System.out.println("System is ready to operate.");
        } else {
            System.out.println("Firmware update cancelled.");
        }
        System.out.println("=====================================\n");
    }

    /**
     * Simulate progress bar for firmware update
     * Makes the update feel realistic
     */
    private void simulateProgress(String task, int seconds) {
        System.out.print(task + "... ");
        try {
            for (int i = 0; i < seconds; i++) {
                Thread.sleep(1000);
                System.out.print("█");
            }
            System.out.println(" Done!");
        } catch (InterruptedException e) {
            System.out.println("Interrupted!");
        }
    }

    /**
     * Logout technician
     */
    private void logout() {
        System.out.println("\n✓ Technician logged out.");
        System.out.println("Returning to main menu...\n");
    }
}