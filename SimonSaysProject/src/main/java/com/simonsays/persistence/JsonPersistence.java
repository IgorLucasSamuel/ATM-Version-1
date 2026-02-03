package com.simonsays.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.simonsays.model.Leaderboard;
import com.simonsays.model.Score;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles JSON file persistence for leaderboard data.
 * Uses Gson library for JSON serialization/deserialization.
 * Demonstrates file I/O and exception handling.
 */
public class JsonPersistence {
    private static final String SCORES_FILE = "scores.json";
    private final Gson gson;

    /**
     * Constructor initializes Gson with pretty printing.
     */
    public JsonPersistence() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    /**
     * Saves leaderboard to JSON file.
     * @param leaderboard Leaderboard to save
     * @throws IOException if file write fails
     */
    public void saveLeaderboard(Leaderboard leaderboard) throws IOException {
        try (Writer writer = new FileWriter(SCORES_FILE)) {
            // Convert List<Score> to JSON
            gson.toJson(leaderboard.getTopScores(), writer);
            System.out.println("Leaderboard saved successfully to " + SCORES_FILE);
        } catch (IOException e) {
            System.err.println("Error saving leaderboard: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Loads leaderboard from JSON file.
     * Handles missing file by creating empty leaderboard.
     * @return Loaded leaderboard or empty if file doesn't exist
     */
    public Leaderboard loadLeaderboard() {
        Leaderboard leaderboard = new Leaderboard();

        File file = new File(SCORES_FILE);
        if (!file.exists()) {
            System.out.println("No existing scores file found. Starting fresh.");
            return leaderboard;
        }

        try (Reader reader = new FileReader(SCORES_FILE)) {
            // Define type for List<Score> using TypeToken (required for generics)
            Type scoreListType = new TypeToken<List<Score>>(){}.getType();
            List<Score> scores = gson.fromJson(reader, scoreListType);

            if (scores != null && !scores.isEmpty()) {
                leaderboard.setScores(scores);
                System.out.println("Loaded " + scores.size() + " scores from file.");
            }
        } catch (IOException e) {
            System.err.println("Error loading leaderboard: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }

        return leaderboard;
    }

    /**
     * Checks if scores file exists.
     * @return true if file exists
     */
    public boolean scoresFileExists() {
        return new File(SCORES_FILE).exists();
    }

    /**
     * Deletes the scores file (useful for testing).
     * @return true if file was deleted
     */
    public boolean deleteScoresFile() {
        File file = new File(SCORES_FILE);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
}
