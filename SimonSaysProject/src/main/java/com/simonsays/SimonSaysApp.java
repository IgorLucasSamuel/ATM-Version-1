package com.simonsays;

import com.simonsays.ui.GameUI;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main entry point for Simon Says application.
 * Extends JavaFX Application class.
 *
 * OTHM Level 5 Diploma in IT Management – Software Engineering
 * Assignment: Simon Says Game with JSON Persistence
 *
 * @author [Your Name]
 * @version 1.0
 */
public class SimonSaysApp extends Application {

    /**
     * JavaFX start method - called when application launches.
     * @param primaryStage Main application window
     */
    @Override
    public void start(Stage primaryStage) {
        GameUI gameUI = new GameUI(primaryStage);
        gameUI.start();
    }

    /**
     * Main method - launches JavaFX application.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}