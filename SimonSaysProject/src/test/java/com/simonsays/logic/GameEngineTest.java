package com.simonsays.logic;

import com.simonsays.model.GameColour;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GameEngine class.
 * Demonstrates various testing techniques:
 * - Unit testing (isolated component testing)
 * - Boundary testing (edge cases)
 * - State testing (game state transitions)
 */
class GameEngineTest {

    private GameEngine gameEngine;

    @BeforeEach
    void setUp() {
        gameEngine = new GameEngine();
    }

    /**
     * UNIT TEST: Verify initial state of game engine.
     */
    @Test
    @DisplayName("Initial state should be NOT_STARTED")
    void testInitialState() {
        assertEquals(GameEngine.GameState.NOT_STARTED, gameEngine.getState());
        assertEquals(0, gameEngine.getCurrentRound());
        assertEquals(0, gameEngine.getScore());
    }

    /**
     * UNIT TEST: Verify game starts correctly.
     */
    @Test
    @DisplayName("Starting game should initialize round 1 with sequence")
    void testStartNewGame() {
        gameEngine.startNewGame();

        assertEquals(1, gameEngine.getCurrentRound());
        assertEquals(GameEngine.GameState.SHOWING_SEQUENCE, gameEngine.getState());
        assertEquals(1, gameEngine.getSequence().size());
    }

    /**
     * UNIT TEST: Verify sequence grows correctly.
     */
    @Test
    @DisplayName("Each round should add one colour to sequence")
    void testSequenceGrowth() {
        gameEngine.startNewGame();
        int initialSize = gameEngine.getSequence().size();

        gameEngine.nextRound();

        assertEquals(initialSize + 1, gameEngine.getSequence().size());
        assertEquals(2, gameEngine.getCurrentRound());
    }

    /**
     * BOUNDARY TEST: Verify sequence contains only valid colour IDs.
     */
    @Test
    @DisplayName("Sequence should only contain valid colour IDs (0-3)")
    void testSequenceValidColours() {
        gameEngine.startNewGame();

        // Generate multiple rounds
        for (int i = 0; i < 10; i++) {
            gameEngine.nextRound();
        }

        List<Integer> sequence = gameEngine.getSequence();
        for (int colourId : sequence) {
            assertTrue(colourId >= 0 && colourId < GameColour.values().length,
                    "Colour ID should be between 0 and 3");
        }
    }

    /**
     * UNIT TEST: Test correct player input.
     */
    @Test
    @DisplayName("Correct player input should be accepted")
    void testCorrectInput() {
        gameEngine.startNewGame();
        gameEngine.sequenceDisplayComplete();

        List<Integer> sequence = gameEngine.getSequence();
        boolean result = gameEngine.addPlayerInput(sequence.get(0));

        assertTrue(result);
        assertTrue(gameEngine.isRoundComplete());
    }

    /**
     * UNIT TEST: Test incorrect player input.
     */
    @Test
    @DisplayName("Incorrect input should trigger game over")
    void testIncorrectInput() {
        gameEngine.startNewGame();
        gameEngine.sequenceDisplayComplete();

        List<Integer> sequence = gameEngine.getSequence();
        int wrongColour = (sequence.get(0) + 1) % 4; // Different colour

        boolean result = gameEngine.addPlayerInput(wrongColour);

        assertFalse(result);
        assertTrue(gameEngine.isGameOver());
        assertEquals(GameEngine.GameState.GAME_OVER, gameEngine.getState());
    }

    /**
     * BOUNDARY TEST: Test input when not ready.
     */
    @Test
    @DisplayName("Input should be rejected when game not in WAITING_FOR_INPUT state")
    void testInputWhenNotReady() {
        gameEngine.startNewGame();
        // Don't call sequenceDisplayComplete()

        boolean result = gameEngine.addPlayerInput(0);

        assertFalse(result);
    }

    /**
     * UNIT TEST: Verify score calculation.
     */
    @Test
    @DisplayName("Score should equal completed rounds")
    void testScoreCalculation() {
        gameEngine.startNewGame();
        gameEngine.sequenceDisplayComplete();

        // Complete first round
        List<Integer> sequence = gameEngine.getSequence();
        gameEngine.addPlayerInput(sequence.get(0));

        assertEquals(0, gameEngine.getScore()); // Score updates after nextRound()

        gameEngine.nextRound();
        assertEquals(1, gameEngine.getScore());
    }

    /**
     * BOUNDARY TEST: Test multiple rounds.
     */
    @Test
    @DisplayName("Game should handle multiple successful rounds")
    void testMultipleRounds() {
        gameEngine.startNewGame();

        for (int round = 1; round <= 5; round++) {
            gameEngine.sequenceDisplayComplete();
            List<Integer> sequence = gameEngine.getSequence();

            // Play entire sequence
            for (int colourId : sequence) {
                assertTrue(gameEngine.addPlayerInput(colourId));
            }

            assertTrue(gameEngine.isRoundComplete());

            if (round < 5) {
                gameEngine.nextRound();
            }
        }

        assertEquals(4, gameEngine.getScore()); // 4 completed rounds
    }

    /**
     * UNIT TEST: Test game reset.
     */
    @Test
    @DisplayName("Starting new game should reset all state")
    void testGameReset() {
        gameEngine.startNewGame();
        gameEngine.nextRound();
        gameEngine.nextRound();

        // Start fresh game
        gameEngine.startNewGame();

        assertEquals(1, gameEngine.getCurrentRound());
        assertEquals(1, gameEngine.getSequence().size());
        assertFalse(gameEngine.isGameOver());
    }
}