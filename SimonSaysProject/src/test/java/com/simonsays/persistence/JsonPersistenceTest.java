package com.simonsays.persistence;

import com.simonsays.model.Leaderboard;
import com.simonsays.model.Score;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration and boundary tests for JSON persistence.
 * Tests file I/O, data integrity, and edge cases.
 */
class JsonPersistenceTest {

    private JsonPersistence persistence;
    private static final String TEST_FILE = "scores.json";

    @BeforeEach
    void setUp() {
        persistence = new JsonPersistence();
        // Clean up before each test
        persistence.deleteScoresFile();
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        persistence.deleteScoresFile();
    }

    /**
     * INTEGRATION TEST: Save and load leaderboard.
     */
    @Test
    @DisplayName("Should save and load leaderboard correctly")
    void testSaveAndLoad() throws IOException {
        Leaderboard original = new Leaderboard();
        original.addScore(new Score("Alice", 10));
        original.addScore(new Score("Bob", 15));
        original.addScore(new Score("Charlie", 8));

        // Save
        persistence.saveLeaderboard(original);
        assertTrue(persistence.scoresFileExists());

        // Load
        Leaderboard loaded = persistence.loadLeaderboard();
        List<Score> scores = loaded.getTopScores();

        assertEquals(3, scores.size());
        assertEquals("Bob", scores.get(0).getPlayerName()); // Highest first
        assertEquals(15, scores.get(0).getScore());
    }

    /**
     * BOUNDARY TEST: Load from non-existent file.
     */
    @Test
    @DisplayName("Should return empty leaderboard when file doesn't exist")
    void testLoadNonExistentFile() {
        assertFalse(persistence.scoresFileExists());

        Leaderboard leaderboard = persistence.loadLeaderboard();

        assertNotNull(leaderboard);
        assertTrue(leaderboard.getTopScores().isEmpty());
    }

    /**
     * BOUNDARY TEST: Save and load exactly 10 scores.
     */
    @Test
    @DisplayName("Should handle exactly 10 scores")
    void testExactlyTenScores() throws IOException {
        Leaderboard leaderboard = new Leaderboard();

        for (int i = 1; i <= 10; i++) {
            leaderboard.addScore(new Score("Player" + i, i * 10));
        }

        persistence.saveLeaderboard(leaderboard);
        Leaderboard loaded = persistence.loadLeaderboard();

        assertEquals(10, loaded.getTopScores().size());
    }

    /**
     * BOUNDARY TEST: Save more than 10 scores, verify only top 10 kept.
     */
    @Test
    @DisplayName("Should keep only top 10 scores when saving more")
    void testMoreThanTenScores() throws IOException {
        Leaderboard leaderboard = new Leaderboard();

        // Add 15 scores
        for (int i = 1; i <= 15; i++) {
            leaderboard.addScore(new Score("Player" + i, i * 5));
        }

        persistence.saveLeaderboard(leaderboard);
        Leaderboard loaded = persistence.loadLeaderboard();

        List<Score> scores = loaded.getTopScores();
        assertEquals(10, scores.size());

        // Verify highest scores are kept
        assertEquals(75, scores.get(0).getScore()); // 15 * 5
        assertEquals(30, scores.get(9).getScore()); // 6 * 5 (10th highest)
    }

    /**
     * INTEGRATION TEST: Test data persistence across multiple saves.
     */
    @Test
    @DisplayName("Should preserve data across multiple save operations")
    void testMultipleSaves() throws IOException {
        Leaderboard board1 = new Leaderboard();
        board1.addScore(new Score("Player1", 50));
        persistence.saveLeaderboard(board1);

        // Load and add more
        Leaderboard board2 = persistence.loadLeaderboard();
        board2.addScore(new Score("Player2", 60));
        persistence.saveLeaderboard(board2);

        // Load again
        Leaderboard board3 = persistence.loadLeaderboard();
        List<Score> scores = board3.getTopScores();

        assertEquals(2, scores.size());
        assertEquals(60, scores.get(0).getScore());
        assertEquals(50, scores.get(1).getScore());
    }

    /**
     * UNIT TEST: Test file deletion.
     */
    @Test
    @DisplayName("Should successfully delete scores file")
    void testFileDeletion() throws IOException {
        Leaderboard leaderboard = new Leaderboard();
        leaderboard.addScore(new Score("Test", 1));
        persistence.saveLeaderboard(leaderboard);

        assertTrue(persistence.scoresFileExists());
        assertTrue(persistence.deleteScoresFile());
        assertFalse(persistence.scoresFileExists());
    }

    /**
     * BOUNDARY TEST: Empty leaderboard.
     */
    @Test
    @DisplayName("Should handle saving empty leaderboard")
    void testEmptyLeaderboard() throws IOException {
        Leaderboard emptyBoard = new Leaderboard();
        persistence.saveLeaderboard(emptyBoard);

        Leaderboard loaded = persistence.loadLeaderboard();
        assertTrue(loaded.getTopScores().isEmpty());
    }
}