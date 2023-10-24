package com.somerdin.snake;

import com.somerdin.snake.Point.PointInt;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class SnakeApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        GameLoop snakeGame = new GameLoop();
        Scene scene = new Scene(new Group(snakeGame.getCanvas()));

        stage.setTitle("SnakeFX");
        stage.setScene(scene);
        stage.show();
        snakeGame.start();
    }

    public static void main(String[] args) {
        Application.launch();
    }
}