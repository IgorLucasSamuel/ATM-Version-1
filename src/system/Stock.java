package system;

import models.BankNote;
import java.util.HashMap;
import java.util.Map;

/**
 * Class managing ATM physical stock: bank notes, ink, and paper
 *
 * IMPORTANT: Uses 'protected' fields instead of 'private'
 * WHY? To allow extension in Version 2 (StockV2 will extend this class)
 * This is part of the Open/Closed Principle (OCP)
 */
public class Stock {
    // Protected allows child classes (StockV2) to access these fields
    protected Map<BankNote, Integer> bankNotes;  // Stores count of each denomination
    protected int inkLevel;                       // Ink cartridge level (for receipts)
    protected int paperLevel;                     // Paper roll level (for receipts)

    /**
     * Constructor: Creates a new Stock and initializes it with default values
     */
    public Stock() {
        this.bankNotes = new HashMap<>();
        initializeStock();
    }

    /**
     * Initialize ATM with 10 of each bank note, 10 ink units, and 10 paper units
     * This is called when the ATM is first set up
     */
    private void initializeStock() {
        // 10 of each denomination
        bankNotes.put(BankNote.FIVE, 10);          // 10 x €5 = €50
        bankNotes.put(BankNote.TEN, 10);           // 10 x €10 = €100
        bankNotes.put(BankNote.TWENTY, 10);        // 10 x €20 = €200
        bankNotes.put(BankNote.FIFTY, 10);         // 10 x €50 = €500
        bankNotes.put(BankNote.ONE_HUNDRED, 10);   // 10 x €100 = €1000
        bankNotes.put(BankNote.TWO_HUNDRED, 10);   // 10 x €200 = €2000
        bankNotes.put(BankNote.FIVE_HUNDRED, 10);  // 10 x €500 = €5000
        // Total = €8850

        this.inkLevel = 10;    // 10 units of ink
        this.paperLevel = 10;  // 10 units of paper
    }

    /**
     * Get the count of a specific bank note denomination
     *
     * @param note The denomination to check
     * @return Number of notes available
     */
    public int getBankNoteCount(BankNote note) {
        return bankNotes.getOrDefault(note, 0);
    }

    /**
     * Get all bank notes with their counts
     * Returns a COPY to prevent external modification
     *
     * @return Map of all denominations and their counts
     */
    public Map<BankNote, Integer> getAllBankNotes() {
        return new HashMap<>(bankNotes);
    }

    /**
     * Get current ink level
     *
     * @return Ink units remaining
     */
    public int getInkLevel() {
        return inkLevel;
    }

    /**
     * Get current paper level
     *
     * @return Paper units remaining
     */
    public int getPaperLevel() {
        return paperLevel;
    }

    // ========== SETTERS (needed for JSON loading) ==========

    /**
     * Set ink level (used when loading from JSON)
     *
     * @param inkLevel New ink level
     */
    public void setInkLevel(int inkLevel) {
        this.inkLevel = inkLevel;
    }

    /**
     * Set paper level (used when loading from JSON)
     *
     * @param paperLevel New paper level
     */
    public void setPaperLevel(int paperLevel) {
        this.paperLevel = paperLevel;
    }

    /**
     * Set all bank notes (used when loading from JSON)
     *
     * @param bankNotes Map of denominations and counts
     */
    public void setBankNotes(Map<BankNote, Integer> bankNotes) {
        this.bankNotes = bankNotes;
    }

    /**
     * Calculate total cash value available in the ATM
     *
     * @return Total euro value of all notes
     */
    public double getTotalCash() {
        double total = 0;
        // Loop through each denomination and calculate its total value
        for (Map.Entry<BankNote, Integer> entry : bankNotes.entrySet()) {
            int noteValue = entry.getKey().getValue();  // Get €5, €10, etc.
            int noteCount = entry.getValue();            // Get how many we have
            total += noteValue * noteCount;              // Add to total
        }
        return total;
    }

    /**
     * Check if ATM can dispense the requested amount
     *
     * NEW LOGIC:
     * - Bank notes: Must have sufficient notes to make exact change
     * - Ink/Paper: Can be 0, but user gets a warning
     *
     * @param amount Amount requested
     * @return true if we have enough bank notes, false otherwise
     */
    public boolean canDispense(double amount) {
        // Check if we have enough total cash
        if (amount > getTotalCash()) {
            return false;
        }

        // Check if we can make exact change with available notes
        Map<BankNote, Integer> dispensed = calculateDispense(amount);
        if (dispensed == null) {
            return false;  // Can't make exact change
        }

        // Note: We don't check ink/paper here anymore
        // Ink/paper at 0 just means no receipt will be printed
        return true;
    }

    /**
     * Dispense cash - deducts bank notes and consumes ink/paper for receipt
     *
     * NEW BEHAVIOR:
     * - If ink or paper is low (1-5 units), warn the user but continue
     * - If ink or paper is 0, inform user no receipt will be printed
     * - If bank notes run out, transaction is CANCELED
     *
     * @param amount Amount to dispense
     * @return true if successful, false if insufficient bank notes
     */
    public boolean dispenseCash(double amount) {
        // Check if we have enough bank notes
        if (!canDispense(amount)) {
            System.out.println("❌ Transaction canceled: Insufficient bank notes.");
            return false;
        }

        // Check ink and paper levels and warn user
        boolean canPrintReceipt = true;

        if (inkLevel == 0 || paperLevel == 0) {
            // At 0: No receipt will be printed
            System.out.println("⚠️  WARNING: Receipt cannot be printed (insufficient supplies).");
            System.out.println("    Your transaction will proceed, but no receipt will be issued.");
            canPrintReceipt = false;
        } else if (inkLevel <= 5 || paperLevel <= 5) {
            // Low (1-5): Warn but continue
            System.out.println("⚠️  WARNING: Ink or paper running low.");
            System.out.println("    Receipt will be printed, but supplies need restocking soon.");
        }

        // Calculate which notes to dispense
        Map<BankNote, Integer> dispensed = calculateDispense(amount);
        if (dispensed == null) {
            System.out.println("❌ Transaction canceled: Cannot make exact change.");
            return false;
        }

        // Deduct the notes from stock
        for (Map.Entry<BankNote, Integer> entry : dispensed.entrySet()) {
            BankNote note = entry.getKey();
            int count = entry.getValue();
            bankNotes.put(note, bankNotes.get(note) - count);
        }

        // Consume ink and paper ONLY if both are available
        if (canPrintReceipt) {
            inkLevel--;
            paperLevel--;
            System.out.println("✓ Receipt printed.");
        } else {
            System.out.println("✗ No receipt printed.");
        }

        return true;
    }

    /**
     * Calculate which notes to dispense for the requested amount
     * Uses a greedy algorithm (largest notes first)
     *
     * @param amount Amount to dispense
     * @return Map of notes to dispense, or null if can't make exact change
     */
    private Map<BankNote, Integer> calculateDispense(double amount) {
        Map<BankNote, Integer> result = new HashMap<>();
        double remaining = amount;

        // Try largest notes first (greedy algorithm)
        BankNote[] notes = {
                BankNote.FIVE_HUNDRED,
                BankNote.TWO_HUNDRED,
                BankNote.ONE_HUNDRED,
                BankNote.FIFTY,
                BankNote.TWENTY,
                BankNote.TEN,
                BankNote.FIVE
        };

        for (BankNote note : notes) {
            int available = bankNotes.getOrDefault(note, 0);  // How many we have
            int needed = (int) (remaining / note.getValue()); // How many we need
            int toDispense = Math.min(available, needed);     // Take the smaller number

            if (toDispense > 0) {
                result.put(note, toDispense);
                remaining -= toDispense * note.getValue();
            }
        }

        // Check if we dispensed the exact amount (allow small floating point error)
        if (remaining > 0.01) {
            return null;  // Can't make exact change
        }

        return result;
    }

    /**
     * Display current stock levels in a formatted table
     */
    public void displayStockLevels() {
        System.out.println("\n========== ATM STOCK LEVELS ==========");
        System.out.println("BANK NOTES:");

        // Show each denomination with count and total value
        for (BankNote note : BankNote.values()) {
            int count = bankNotes.getOrDefault(note, 0);
            double value = note.getValue() * count;
            System.out.printf("  %-15s: %3d notes (€%.2f)\n", note, count, value);
        }

        System.out.printf("\nTotal Cash: €%.2f\n", getTotalCash());
        System.out.println("\nCONSUMABLES:");
        System.out.printf("  Ink Level   : %d units\n", inkLevel);
        System.out.printf("  Paper Level : %d units\n", paperLevel);
        System.out.println("======================================\n");
    }
}