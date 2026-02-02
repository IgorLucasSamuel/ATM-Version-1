package v2_extensions.interfaces;

import interfaces.IStockViewer;
import models.BankNote;

/**
 * Interface for managing ATM stock (extends IStockViewer from V1)
 *
 * DEMONSTRATES OCP (Open/Closed Principle):
 * - EXTENDS IStockViewer from V1 (open for extension)
 * - V1's IStockViewer remains UNCHANGED (closed for modification)
 *
 * NEW CAPABILITIES in V2:
 * - Restock bank notes
 * - Restock ink
 * - Restock paper
 */
public interface IStockManager extends IStockViewer {

    /**
     * Restock bank notes of a specific denomination
     * NEW method in V2 - adds restocking capability
     *
     * @param note The denomination to restock
     * @param quantity Number of notes to add
     */
    void restockBankNotes(BankNote note, int quantity);

    /**
     * Restock ink cartridge
     * NEW method in V2 - adds restocking capability
     *
     * @param quantity Amount of ink units to add
     */
    void restockInk(int quantity);

    /**
     * Restock paper rolls
     * NEW method in V2 - adds restocking capability
     *
     * @param quantity Amount of paper units to add
     */
    void restockPaper(int quantity);
}