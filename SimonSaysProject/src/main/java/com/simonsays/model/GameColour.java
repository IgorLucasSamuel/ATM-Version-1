package com.simonsays.model;

/**
 * Enum representing the four colours in Simon Says game.
 * Using enum ensures type safety and prevents invalid colour values.
 *
 * Each colour has:
 * - id: numeric identifier (0-3) used for storing sequences
 * - hexCode: colour code for rendering in the UI
 */
public enum GameColour {
    RED(0, "#FF0000"),
    BLUE(1, "#0000FF"),
    GREEN(2, "#00FF00"),
    YELLOW(3, "#FFFF00");

    private final int id;
    private final String hexCode;

    /**
     * Constructor for GameColour enum.
     * @param id Numeric identifier for the colour (used in sequence)
     * @param hexCode Hex colour code for UI rendering
     */
    GameColour(int id, String hexCode) {
        this.id = id;
        this.hexCode = hexCode;
    }

    /**
     * Gets the numeric ID of this colour.
     * @return colour ID (0-3)
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the hex colour code for UI rendering.
     * @return hex colour code (e.g., "#FF0000")
     */
    public String getHexCode() {
        return hexCode;
    }

    /**
     * Converts numeric ID back to GameColour enum.
     * This is useful when reading sequences stored as integers.
     *
     * @param id The colour ID (0-3)
     * @return Corresponding GameColour
     * @throws IllegalArgumentException if ID is not 0-3
     */
    public static GameColour fromId(int id) {
        for (GameColour colour : values()) {
            if (colour.id == id) {
                return colour;
            }
        }
        throw new IllegalArgumentException("Invalid colour ID: " + id);
    }
}
