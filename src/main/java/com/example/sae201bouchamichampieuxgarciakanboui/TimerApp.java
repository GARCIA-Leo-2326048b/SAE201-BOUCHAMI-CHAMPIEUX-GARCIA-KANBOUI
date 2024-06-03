package com.example.sae201bouchamichampieuxgarciakanboui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TimerApp extends Application {

    private long startTime;
    private long pausedTime;
    private boolean running = false;
    private AnimationTimer timer;
    private Label timeLabel;
    private TextField timeInput;
    private Button startButton;
    private Button pauseButton;
    private Button resetButton;

    @Override
    public void start(Stage primaryStage) {
        timeLabel = new Label("Time: 0.0s");
        timeInput = new TextField("10"); // default to 10 seconds
        startButton = new Button("Start");
        pauseButton = new Button("Pause");
        resetButton = new Button("Reset");

        startButton.setOnAction(e -> startTimer());
        pauseButton.setOnAction(e -> pauseTimer());
        resetButton.setOnAction(e -> resetTimer());

        VBox root = new VBox(10, timeInput, startButton, pauseButton, resetButton, timeLabel);
        Scene scene = new Scene(root, 300, 200);

        primaryStage.setTitle("JavaFX Timer");
        primaryStage.setScene(scene);
        primaryStage.show();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateTimer();
            }
        };
    }

    private void startTimer() {
        if (!running) {
            if (pausedTime == 0) {
                startTime = System.currentTimeMillis();
            } else {
                startTime = System.currentTimeMillis() - pausedTime;
            }
            timer.start();
            running = true;
        }
    }

    private void pauseTimer() {
        if (running) {
            pausedTime = System.currentTimeMillis() - startTime;
            timer.stop();
            running = false;
        }
    }

    private void resetTimer() {
        timer.stop();
        running = false;
        startTime = 0;
        pausedTime = 0;
        timeLabel.setText("Time: 0.0s");
    }

    private void updateTimer() {
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - startTime;
        double secondsElapsed = elapsed / 1000.0;
        double targetTime = Double.parseDouble(timeInput.getText());

        if (secondsElapsed >= targetTime) {
            timer.stop();
            running = false;
            timeLabel.setText("Time: " + targetTime + "s");
        } else {
            timeLabel.setText(String.format("Time: %.1fs", secondsElapsed));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

