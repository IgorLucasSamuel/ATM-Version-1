package com.simonsays.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages the top 10 scores leaderboard.
 * Demonstrates use of Java Collections and Generics (List<Score>).
 */
public class Leaderboard {
    private static final int MAX_ENTRIES = 10;
    private List<Score> scores; // Generic List collection

    /**
     * Constructor initializes an empty ArrayList.
     * ArrayList chosen for O(1) access and efficient sorting.
     */
    public Leaderboard() {
        this.scores = new ArrayList<>();
    }

    /**
     * Adds a new score and maintains top 10 only.
     * Demonstrates collection manipulation and generics.
     * @param newScore Score to add
     * @return true if score made it to top 10, false otherwise
     */
    public boolean addScore(Score newScore) {
        scores.add(newScore);
        Collections.sort(scores); // Uses Score.compareTo()

        // Keep only top 10
        if (scores.size() > MAX_ENTRIES) {
            scores = new ArrayList<>(scores.subList(0, MAX_ENTRIES));
        }

        // Check if the new score is in top 10
        return scores.contains(newScore);
    }

    /**
     * Returns unmodifiable view of scores to prevent external modification.
     * @return List of top scores
     */
    public List<Score> getTopScores() {
        return Collections.unmodifiableList(scores);
    }

    /**
     * Replaces current scores (used when loading from JSON).
     * @param scores New list of scores
     */
    public void setScores(List<Score> scores) {
        this.scores = new ArrayList<>(scores);
        Collections.sort(this.scores);

        // Ensure only top 10
        if (this.scores.size() > MAX_ENTRIES) {
            this.scores = new ArrayList<>(this.scores.subList(0, MAX_ENTRIES));
        }
    }

    /**
     * Checks if a score qualifies for top 10.
     * @param score Score to check
     * @return true if qualifies for leaderboard
     */
    public boolean isTopScore(int score) {
        if (scores.size() < MAX_ENTRIES) {
            return true;
        }
        return score > scores.get(MAX_ENTRIES - 1).getScore();
    }

    /**
     * Gets the lowest score in top 10 (or 0 if less than 10 entries).
     * @return Minimum qualifying score
     */
    public int getMinimumQualifyingScore() {
        if (scores.isEmpty()) {
            return 0;
        }
        if (scores.size() < MAX_ENTRIES) {
            return 0;
        }
        return scores.get(MAX_ENTRIES - 1).getScore();
    }
}