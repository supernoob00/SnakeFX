package com.somerdin.snake;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.*;

public class SnakeApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // find high score text file
        int scoreFromFile = Score.loadHighScore();
        if (scoreFromFile == -1) {
            try {
                File file = new File(Score.TEXT_FILE_DIRECTORY);
                file.delete();
                file.setReadOnly();
                file.createNewFile();
                Score.setHighScore(0);
            } catch (IOException | SecurityException | NullPointerException e) {
                Score.setScoreSaved(false);
            }
        } else {
            Score.setHighScore(scoreFromFile);
            Score.setScoreSaved(true);
        }

        Canvas canvas = new Canvas(GameLoop.TOTAL_WIDTH, GameLoop.TOTAL_HEIGHT);
        canvas.setFocusTraversable(true);

        TitleScreen title = new TitleScreen(canvas);
        Scene scene = new Scene(new Pane(canvas));

        stage.setTitle("SnakeFX");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch();
    }
}