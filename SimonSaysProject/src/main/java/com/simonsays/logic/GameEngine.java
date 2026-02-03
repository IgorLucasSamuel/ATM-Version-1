package com.simonsays.logic;

import com.simonsays.model.GameColour;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Core game engine for Simon Says.
 * Manages sequence generation, validation, and game state.
 * Demonstrates separation of concerns (logic separated from UI).
 */
public class GameEngine {
    private List<Integer> sequence;          // Stores colour sequence as IDs
    private List<Integer> playerInput;       // Stores player's current input
    private int currentRound;                // Current round number
    private GameState state;                 // Current game state
    private Random random;

    /**
     * Enum for game states.
     */
    public enum GameState {
        NOT_STARTED,
        SHOWING_SEQUENCE,
        WAITING_FOR_INPUT,
        GAME_OVER
    }

    /**
     * Constructor initializes a new game.
     */
    public GameEngine() {
        this.sequence = new ArrayList<>();
        this.playerInput = new ArrayList<>();
        this.random = new Random();
        this.state = GameState.NOT_STARTED;
        this.currentRound = 0;
    }

    /**
     * Starts a new game by resetting state and generating first sequence.
     */
    public void startNewGame() {
        sequence.clear();
        playerInput.clear();
        currentRound = 0;
        state = GameState.SHOWING_SEQUENCE;
        nextRound();
    }

    /**
     * Advances to next round by adding one colour to sequence.
     * Demonstrates List manipulation with generics.
     */
    public void nextRound() {
        currentRound++;
        playerInput.clear();

        // Add random colour (0-3) to sequence
        int newColour = random.nextInt(GameColour.values().length);
        sequence.add(newColour);

        state = GameState.SHOWING_SEQUENCE;
    }

    /**
     * Records player's colour choice and validates immediately.
     * @param colourId ID of colour clicked (0-3)
     * @return true if correct so far, false if wrong (game over)
     */
    public boolean addPlayerInput(int colourId) {
        if (state != GameState.WAITING_FOR_INPUT) {
            return false;
        }

        playerInput.add(colourId);

        // Validate input immediately
        int index = playerInput.size() - 1;
        if (!sequence.get(index).equals(colourId)) {
            state = GameState.GAME_OVER;
            return false;
        }

        // Check if round complete
        if (playerInput.size() == sequence.size()) {
            // Round completed successfully
            return true;
        }

        return true; // Correct so far, waiting for more input
    }

    /**
     * Checks if the current round is complete and correct.
     * @return true if player has entered full sequence correctly
     */
    public boolean isRoundComplete() {
        return playerInput.size() == sequence.size()
                && state != GameState.GAME_OVER;
    }

    /**
     * Marks sequence display as complete, ready for player input.
     */
    public void sequenceDisplayComplete() {
        state = GameState.WAITING_FOR_INPUT;
    }

    // Getters
    public List<Integer> getSequence() {
        return new ArrayList<>(sequence); // Return copy for safety
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public GameState getState() {
        return state;
    }

    /**
     * Gets current score (number of successfully completed rounds).
     * @return Current score
     */
    public int getScore() {
        // Score is rounds completed, not current round
        return Math.max(0, currentRound - 1);
    }

    /**
     * Checks if game is over.
     * @return true if game over
     */
    public boolean isGameOver() {
        return state == GameState.GAME_OVER;
    }
}