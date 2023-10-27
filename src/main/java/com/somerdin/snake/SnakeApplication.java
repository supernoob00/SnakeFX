package com.somerdin.snake;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class SnakeApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        GameLoop snakeGame = new GameLoop();
        Scene scene = new Scene(new Pane(snakeGame.getCanvas()));

        stage.setTitle("SnakeFX");
        stage.setScene(scene);
        stage.show();
        snakeGame.start();
    }

    public static void main(String[] args) {
        Application.launch();
    }
}