package com.somerdin.snake;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class TitleScreen {
    private static final Color TEXT_COLOR = Color.WHITE;

    private Canvas canvas;

    public TitleScreen(Canvas canvas) {
        this.canvas = canvas;
        clear();
        drawTitleText();
        drawStartText();

        AnimationTimer timer = new AnimationTimer() {
            long prevTime;
            boolean startTextShown = false;

            @Override
            public void handle(long currentTime) {
                if (currentTime - prevTime > 500_000_000L) {
                    startTextShown = !startTextShown;
                    if (startTextShown) {
                        clear();
                        drawTitleText();
                        drawStartText();
                        drawAuthorText();
                    } else {
                        clear();
                        drawTitleText();
                        drawAuthorText();
                    }
                    prevTime = currentTime;
                }
            }
        };
        timer.start();

        canvas.setOnKeyPressed(keyEvent -> {
            System.out.println("Key pressed");
            timer.stop();
            GameLoop loop = new GameLoop(canvas);
            loop.start();
        });
    }

    private void clear() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void drawTitleText() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(TEXT_COLOR);

        Text titleText = new Text("SNAKE");
        titleText.setFont(SnakeApplication.font(160));
        double titleX =
                canvas.getWidth() / 2 - titleText.getLayoutBounds().getWidth() / 2;
        double titleY = 200;
        g.setFont(titleText.getFont());
        g.fillText(titleText.getText(), titleX, titleY);
    }

    private void drawStartText() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        Text startText = new Text("PRESS ANY KEY TO START");
        startText.setFont(SnakeApplication.font(36));
        double startTextX =
                canvas.getWidth() / 2 - startText.getLayoutBounds().getWidth() / 2;
        double startTextY = 450;
        g.setFont(startText.getFont());
        g.fillText(startText.getText(), startTextX, startTextY);
    }

    private void drawAuthorText() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        Text startText = new Text("GAME CREATED BY SAM S");
        startText.setFont(SnakeApplication.font(16));
        double startTextX =
                canvas.getWidth() - 1.1 * startText.getLayoutBounds().getWidth() ;
        double startTextY =
                canvas.getHeight() - 1.5 * startText.getLayoutBounds().getHeight();
        g.setFont(startText.getFont());
        g.fillText(startText.getText(), startTextX, startTextY);
    }
}
