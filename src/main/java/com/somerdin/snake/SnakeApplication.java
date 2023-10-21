package com.somerdin.snake;

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
    public static final int CANVAS_WIDTH = 600;
    public static final int CANVAS_HEIGHT = 600;

    private Game game = new Game(Game.WIDTH, Game.HEIGHT);
    private Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
    private GraphicsContext g = canvas.getGraphicsContext2D();

    private AnimationTimer timer = new AnimationTimer() {
        private long prevTime;

        @Override
        public void handle(long time) {
            if (time - prevTime > 0.99e9 / 30) {
                game.moveSnake();
                draw();
                prevTime = time;
            }
        }
    };

    private int cellLength = CANVAS_HEIGHT / Game.HEIGHT;

    @Override
    public void start(Stage stage) throws IOException {
        draw();
        Scene scene = new Scene(new Group(canvas));

        scene.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case LEFT -> game.changeSnakeDir(Direction.LEFT);
                case UP -> game.changeSnakeDir(Direction.UP);
                case RIGHT -> game.changeSnakeDir(Direction.RIGHT);
                case DOWN -> game.changeSnakeDir(Direction.DOWN);
            }
            System.out.println("Key pressed: " + key.getCode());
        });

        stage.setTitle("Super Snake");
        stage.setScene(scene);
        stage.show();
        timer.start();
    }

    private void draw() {
        g.setFill(Color.WHITE);
        g.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        g.setFill(Color.BLACK);

        for (int y = 0; y < game.height; y++) {
            for (int x = 0; x < game.width; x++) {
                Food o = game.getFood(new Point(x ,y));
                if (o != null) {
                    g.setFill(Color.GREEN);
                    int displayY = y * cellLength;
                    int displayX = x * cellLength;
                    g.fillRect(displayX, displayY, cellLength, cellLength);
                }

                g.setFill(Color.BLUE);
                for (Point p : game.snake.body()) {
                    g.fillRect(
                            p.x() * cellLength,
                            p.y() * cellLength,
                            cellLength,
                            cellLength);
                }
            }
        }
    }

    public static void main(String[] args) {
        Application.launch();
    }
}