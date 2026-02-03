package com.simonsays.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single score entry in the leaderboard.
 * Implements Comparable to allow sorting by score (descending).
 */
public class Score implements Comparable<Score> {
    private String playerName;
    private int score;
    private String timestamp;

    /**
     * Constructor for creating a new score entry.
     * @param playerName Name of the player
     * @param score Score achieved (number of correct sequences)
     */
    public Score(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
        this.timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Default constructor for Gson deserialization
    public Score() {
    }

    // Getters and Setters
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Compare scores in descending order (highest first).
     * @param other Another Score object
     * @return Comparison result
     */
    @Override
    public int compareTo(Score other) {
        return Integer.compare(other.score, this.score); // Descending order
    }

    @Override
    public String toString() {
        return String.format("%s - Score: %d (%s)",
                playerName, score, timestamp);
    }
}
