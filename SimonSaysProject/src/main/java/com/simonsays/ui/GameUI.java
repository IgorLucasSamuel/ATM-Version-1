package com.simonsays.ui;

import com.simonsays.logic.GameEngine;
import com.simonsays.model.GameColour;
import com.simonsays.model.Leaderboard;
import com.simonsays.model.Score;
import com.simonsays.persistence.JsonPersistence;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class GameUI {
    private Stage primaryStage;
    private GameEngine gameEngine;
    private JsonPersistence persistence;
    private Leaderboard leaderboard;

    private Rectangle[] colourButtons;
    private Label scoreLabel;
    private Label roundLabel;
    private Label messageLabel;
    private Button startButton;
    private Button leaderboardButton;

    private boolean inputEnabled = false;

    public GameUI(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.gameEngine = new GameEngine();
        this.persistence = new JsonPersistence();
        this.leaderboard = persistence.loadLeaderboard();

        colourButtons = new Rectangle[4];
    }

    public void start() {
        primaryStage.setTitle("Simon Says - OTHM Level 5 Assignment");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #2C3E50;");

        VBox topBox = createTopSection();
        root.setTop(topBox);

        GridPane gameGrid = createGameGrid();
        root.setCenter(gameGrid);

        HBox bottomBox = createBottomSection();
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 600, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createTopSection() {
        VBox topBox = new VBox(10);
        topBox.setAlignment(Pos.CENTER);

        Label title = new Label("SIMON SAYS");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: white;");

        roundLabel = new Label("Round: 0");
        roundLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #ECF0F1;");

        scoreLabel = new Label("Score: 0");
        scoreLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #ECF0F1;");

        messageLabel = new Label("Press START to begin!");
        messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #F39C12;");

        topBox.getChildren().addAll(title, roundLabel, scoreLabel, messageLabel);
        return topBox;
    }

    private GridPane createGameGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(30));

        GameColour[] colours = GameColour.values();
        int index = 0;

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                final int colourIndex = index;
                Rectangle button = createColourButton(colours[index]);
                colourButtons[index] = button;

                button.setOnMouseClicked(e -> handleColourClick(colourIndex));

                grid.add(button, col, row);
                index++;
            }
        }

        return grid;
    }

    private Rectangle createColourButton(GameColour colour) {
        Rectangle rect = new Rectangle(200, 200);
        rect.setFill(Color.web(colour.getHexCode()));
        rect.setArcWidth(20);
        rect.setArcHeight(20);
        rect.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 5);");
        return rect;
    }

    private HBox createBottomSection() {
        HBox bottomBox = new HBox(20);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20, 0, 0, 0));

        startButton = new Button("START GAME");
        startButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 30;");
        startButton.setOnAction(e -> startGame());

        leaderboardButton = new Button("LEADERBOARD");
        leaderboardButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 30;");
        leaderboardButton.setOnAction(e -> showLeaderboard());

        bottomBox.getChildren().addAll(startButton, leaderboardButton);
        return bottomBox;
    }

    private void handleColourClick(int colourIndex) {
        if (!inputEnabled) {
            return;
        }

        flashButton(colourIndex, false);

        boolean correct = gameEngine.addPlayerInput(colourIndex);

        if (!correct) {
            inputEnabled = false;
            messageLabel.setText("WRONG! Game Over!");
            messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #E74C3C;");
            handleGameOver();
            return;
        }

        if (gameEngine.isRoundComplete()) {
            inputEnabled = false;
            messageLabel.setText("Correct! Next round...");
            messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #2ECC71;");

            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> nextRound());
            pause.play();
        }
    }

    private void startGame() {
        gameEngine.startNewGame();
        updateDisplay();
        messageLabel.setText("Watch carefully...");
        messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #F39C12;");
        startButton.setDisable(true);

        playSequence();
    }

    private void playSequence() {
        inputEnabled = false;
        List<Integer> sequence = gameEngine.getSequence();

        PauseTransition initialPause = new PauseTransition(Duration.seconds(0.5));
        initialPause.setOnFinished(e -> playSequenceStep(sequence, 0));
        initialPause.play();
    }

    private void playSequenceStep(List<Integer> sequence, int index) {
        if (index >= sequence.size()) {
            gameEngine.sequenceDisplayComplete();
            inputEnabled = true;
            messageLabel.setText("Your turn! Repeat the sequence.");
            messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #3498DB;");
            return;
        }

        int colourId = sequence.get(index);
        Rectangle button = colourButtons[colourId];
        Color originalColour = Color.web(GameColour.fromId(colourId).getHexCode());

        // Make button bright white for flash effect
        button.setFill(Color.WHITE);
        button.setOpacity(1.0);

        PauseTransition flashOn = new PauseTransition(Duration.millis(500));
        flashOn.setOnFinished(e -> {
            button.setFill(originalColour);
            button.setOpacity(1.0);

            PauseTransition gap = new PauseTransition(Duration.millis(300));
            gap.setOnFinished(e2 -> playSequenceStep(sequence, index + 1));
            gap.play();
        });
        flashOn.play();
    }

    private void flashButton(int colourIndex, boolean autoRevert) {
        Rectangle button = colourButtons[colourIndex];
        Color originalColour = Color.web(GameColour.fromId(colourIndex).getHexCode());

        // Make button bright white for flash effect
        button.setFill(Color.WHITE);
        button.setOpacity(1.0);

        PauseTransition pause = new PauseTransition(Duration.millis(200));
        pause.setOnFinished(e -> {
            button.setFill(originalColour);
            button.setOpacity(1.0);
        });
        pause.play();
    }

    private void nextRound() {
        gameEngine.nextRound();
        updateDisplay();
        messageLabel.setText("Watch carefully...");
        messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #F39C12;");
        playSequence();
    }

    private void updateDisplay() {
        roundLabel.setText("Round: " + gameEngine.getCurrentRound());
        scoreLabel.setText("Score: " + gameEngine.getScore());
    }

    private void handleGameOver() {
        int finalScore = gameEngine.getScore();
        updateDisplay();

        if (leaderboard.isTopScore(finalScore) || finalScore > 0) {
            promptForName(finalScore);
        } else {
            showGameOverDialog(finalScore, false);
        }

        startButton.setDisable(false);
    }

    private void promptForName(int finalScore) {
        TextInputDialog dialog = new TextInputDialog("Player");
        dialog.setTitle("High Score!");
        dialog.setHeaderText("Congratulations! You scored " + finalScore);
        dialog.setContentText("Enter your name:");

        dialog.showAndWait().ifPresent(name -> {
            if (name.trim().isEmpty()) {
                name = "Anonymous";
            }

            Score newScore = new Score(name.trim(), finalScore);
            leaderboard.addScore(newScore);

            try {
                persistence.saveLeaderboard(leaderboard);
                showGameOverDialog(finalScore, true);
            } catch (IOException e) {
                showError("Failed to save score: " + e.getMessage());
            }
        });
    }

    private void showGameOverDialog(int score, boolean savedToLeaderboard) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Final Score: " + score);

        String content = savedToLeaderboard
                ? "Your score has been saved!\nWould you like to view the leaderboard?"
                : "Better luck next time!";
        alert.setContentText(content);

        if (savedToLeaderboard) {
            ButtonType viewButton = new ButtonType("View Leaderboard");
            ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(viewButton, closeButton);

            alert.showAndWait().ifPresent(response -> {
                if (response == viewButton) {
                    showLeaderboard();
                }
            });
        } else {
            alert.showAndWait();
        }
    }

    private void showLeaderboard() {
        Stage leaderboardStage = new Stage();
        leaderboardStage.setTitle("Top 10 Leaderboard");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: #34495E;");

        Label title = new Label("TOP 10 SCORES");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #F1C40F;");

        ListView<String> listView = new ListView<>();
        listView.setPrefHeight(400);
        listView.setStyle("-fx-font-size: 14px;");

        List<Score> topScores = leaderboard.getTopScores();
        if (topScores.isEmpty()) {
            listView.getItems().add("No scores yet. Be the first!");
        } else {
            for (int i = 0; i < topScores.size(); i++) {
                Score s = topScores.get(i);
                String entry = String.format("%d. %s - %d points (%s)",
                        i + 1, s.getPlayerName(), s.getScore(), s.getTimestamp());
                listView.getItems().add(entry);
            }
        }

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> leaderboardStage.close());

        layout.getChildren().addAll(title, listView, closeButton);

        Scene scene = new Scene(layout, 500, 550);
        leaderboardStage.setScene(scene);
        leaderboardStage.show();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}