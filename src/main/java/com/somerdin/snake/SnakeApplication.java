package com.somerdin.snake;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class SnakeApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Canvas canvas = new Canvas(GameLoop.TOTAL_WIDTH, GameLoop.TOTAL_HEIGHT);
        canvas.setFocusTraversable(true);

        TitleScreen title = new TitleScreen(canvas);
        Scene scene = new Scene(new Pane(canvas));

        stage.setTitle("SnakeFX");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch();
    }
}