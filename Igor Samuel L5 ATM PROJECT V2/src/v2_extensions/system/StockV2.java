package v2_extensions.system;

import models.BankNote;
import system.Stock;

/**
 * Extended Stock class for Version 2
 *
 * DEMONSTRATES OCP (Open/Closed Principle):
 * - EXTENDS system.Stock from V1 (V1 class remains unchanged)
 * - ADDS new restocking capabilities
 * - V1 Stock is CLOSED for modification (only uses protected fields)
 * - V2 StockV2 is OPEN for extension
 *
 * INHERITANCE EXPLANATION:
 * - Inherits all V1 functionality (dispenseCash, canDispense, etc.)
 * - Adds 3 new methods: addBankNotes(), addInk(), addPaper()
 * - Uses 'protected' fields from parent (bankNotes, inkLevel, paperLevel)
 */
public class StockV2 extends Stock {

    /**
     * Constructor: Inherits V1 Stock initialization
     * Calls parent constructor which initializes:
     * - 10 of each bank note denomination
     * - 10 units of ink
     * - 10 units of paper
     */
    public StockV2() {
        super();  // Calls Stock() constructor from V1
    }

    /**
     * Add bank notes to existing stock
     * NEW method in V2 - extends functionality
     *
     * Accesses 'protected' field 'bankNotes' from parent Stock class
     * This is why we changed private → protected in V1 (to allow extension)
     *
     * @param note The denomination to restock
     * @param quantity Number of notes to add
     */
    public void addBankNotes(BankNote note, int quantity) {
        if (quantity <= 0) {
            System.out.println("Error: Quantity must be positive.");
            return;
        }

        // Access protected field from parent Stock class
        int currentCount = bankNotes.getOrDefault(note, 0);
        int newCount = currentCount + quantity;

        // Update the stock
        bankNotes.put(note, newCount);

        double value = note.getValue() * quantity;
        System.out.println("✓ Successfully added " + quantity + " x " + note +
                " notes (€" + value + " total value).");
    }

    /**
     * Add ink units to existing stock
     * NEW method in V2 - extends functionality
     *
     * Accesses 'protected' field 'inkLevel' from parent Stock class
     *
     * @param quantity Amount of ink units to add
     */
    public void addInk(int quantity) {
        if (quantity <= 0) {
            System.out.println("Error: Quantity must be positive.");
            return;
        }

        // Access protected field from parent Stock class
        inkLevel += quantity;

        System.out.println("✓ Successfully added " + quantity + " units of ink.");
        System.out.println("  New ink level: " + inkLevel + " units");
    }

    /**
     * Add paper units to existing stock
     * NEW method in V2 - extends functionality
     *
     * Accesses 'protected' field 'paperLevel' from parent Stock class
     *
     * @param quantity Amount of paper units to add
     */
    public void addPaper(int quantity) {
        if (quantity <= 0) {
            System.out.println("Error: Quantity must be positive.");
            return;
        }

        // Access protected field from parent Stock class
        paperLevel += quantity;

        System.out.println("✓ Successfully added " + quantity + " units of paper.");
        System.out.println("  New paper level: " + paperLevel + " units");
    }
}
