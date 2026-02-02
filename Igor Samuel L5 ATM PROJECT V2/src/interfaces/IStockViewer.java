package interfaces;

/**
 * Interface for viewing ATM stock levels (read-only access)
 * Used by Technician role in Version 1
 *
 * This interface defines the contract for anyone who needs to VIEW stock,
 * but not modify it. In V1, technicians can only view.
 */
public interface IStockViewer {

    /**
     * Display current stock levels of bank notes, ink, and paper
     * This method shows all stock information to the user
     */
    void viewStockLevels();
}
