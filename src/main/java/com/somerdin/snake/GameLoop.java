package com.somerdin.snake;

import com.somerdin.snake.Point.PointInt;
import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.util.Map;

/**
 * Calls game state update methods and handles canvas drawing and animation.
 */
public class GameLoop {
    public static final int CANVAS_WIDTH = 640;
    public static final int CANVAS_HEIGHT = 640;

    public static final int DRAW_UPDATE_FPS = 60;

    public static final Map<KeyCode, Direction> keyEventMap = Map.of(
            KeyCode.LEFT, Direction.LEFT,
            KeyCode.UP, Direction.UP,
            KeyCode.RIGHT, Direction.RIGHT,
            KeyCode.DOWN, Direction.DOWN
    );

    private GameState gameState;
    private Canvas canvas;
    private GraphicsContext g;
    private int cellLength;
    private AnimationTimer timer;

    private Direction queuedDirection;

    private long startTime;
    // the timestamp of the most recent animation cycle start
    private long prevTime;
    // the current timestamp; how far currently into animation cycle
    private long currentTime;
    // number of game state updates which have occurred
    private long frameCount = 0;

    public GameLoop() {
        gameState = new GameState(GameState.WIDTH, GameState.HEIGHT);
        canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);

        // allows the canvas to register key events
        canvas.setFocusTraversable(true);

        // key press listeners on canvas
        canvas.setOnKeyPressed(key -> {
            // space bar (boost) key pressed
            if (key.getCode() == KeyCode.SPACE) {
                gameState.getSnake().speedUp();
            }
            // arrow key pressed; only change snake direction if key pressed not
            // opposite to current head direction
            else if (keyEventMap.containsKey(key.getCode())) {
                Direction direction = keyEventMap.get(key.getCode());
                if (direction != gameState.getSnake().getHead().getDir().opposite()) {
                    queuedDirection = direction;
                }
            }
        });

        // key release listeners on canvas
        canvas.setOnKeyReleased(key -> {
            if (key.getCode() == KeyCode.SPACE) {
                gameState.getSnake().slowDown();
            }
        });

        g = canvas.getGraphicsContext2D();
        cellLength = CANVAS_HEIGHT / GameState.HEIGHT;

        timer = new AnimationTimer() {
            @Override
            public void handle(long time) {
                currentTime = time;
                // all game animations update at 60 FPS
                if (time - prevTime > 0.99e9 / DRAW_UPDATE_FPS) {
                    gameState.update(frameCount);
                    drawBetweenState();
                    prevTime = time;
                    frameCount++;
                    if (frameCount > 10000) {
                        frameCount = 0;
                    }
                }
            }
        };
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void start() {
        drawState();
        startTime = System.nanoTime();
        timer.start();
    }

    private void drawState() {
        g.setFill(Color.WHITE);
        g.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        drawFood();

        // draw player snake
        g.setFill(Color.BLUE);
        for (SnakeCell sc : gameState.getSnake().getBody()) {
            g.fillRect(
                    sc.getPos().x() * cellLength,
                    sc.getPos().y() * cellLength,
                    cellLength,
                    cellLength);
        }
    }

    /**
     * Draws a scene which is an interpolation of the current and next game
     * states for smooth animation
     */
    private void drawBetweenState() {
        g.setFill(Color.WHITE);
        g.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        drawFood();

        // draw player snake
        Snake snake = gameState.getSnake();
        if (queuedDirection != null) {
            snake.setDirection(queuedDirection);
            queuedDirection = null;
        }
        boolean headDrawn = false;
        for (SnakeCell sc : snake.getBody()) {
            if (sc.isCorner()) {
                drawImage(PixelTile.SNAKE_BODY, sc.getPos().x() * cellLength,
                sc.getPos().y() * cellLength);
            }
            double x = interpolate(sc.getPos().x() * cellLength,
                    sc.getNextPos().x() * cellLength,
                    frameCount % snake.speed() / (double) snake.speed());
            double y = interpolate(sc.getPos().y() * cellLength,
                    sc.getNextPos().y() * cellLength,
                    frameCount % snake.speed() / (double) snake.speed());
            if (!headDrawn) {
                switch (sc.getDir()) {
                    case UP -> drawImage(PixelTile.SNAKE_HEAD_UP, x, y);
                    case LEFT -> drawImage(PixelTile.SNAKE_HEAD_LEFT, x, y);
                    case DOWN -> drawImage(PixelTile.SNAKE_HEAD_DOWN, x, y);
                    case RIGHT -> drawImage(PixelTile.SNAKE_HEAD_RIGHT, x, y);
                }
                headDrawn = true;
            } else {
                drawImage(PixelTile.SNAKE_BODY, x, y);
            }
        }
    }

    private void drawFood() {
        for (int y = 0; y < gameState.height; y++) {
            for (int x = 0; x < gameState.width; x++) {
                Food food = gameState.getFood(new PointInt(x ,y));
                if (food != null) {
                    int displayY = y * cellLength;
                    int displayX = x * cellLength;
                    switch (food) {
                        case APPLE:
                            drawImage(PixelTile.APPLE, displayX, displayY);
                            break;
                        default:
                            switch (food) {
                                case CRUMBS_1 -> drawImage(PixelTile.CRUMBS_1,
                                        displayX, displayY);
                                case CRUMBS_2 -> drawImage(PixelTile.CRUMBS_2
                                        , displayX, displayY);
                                case CRUMBS_3 -> drawImage(PixelTile.CRUMBS_3
                                        , displayX, displayY);
                                case CRUMBS_4 -> drawImage(PixelTile.CRUMBS_4
                                        , displayX, displayY);
                                case CRUMBS_5 -> drawImage(PixelTile.CRUMBS_5
                                        , displayX, displayY);
                                case CRUMBS_6 -> drawImage(PixelTile.CRUMBS_6
                                        , displayX, displayY);
                            }
                    }
                }
            }
        }
    }

    private void drawImage(ImageView toDraw, double x, double y) {
        Rectangle2D viewport = toDraw.getViewport();
        g.drawImage(toDraw.getImage(), viewport.getMinX(),
                viewport.getMinY(), viewport.getWidth(),
                viewport.getHeight(), x, y, viewport.getWidth(), viewport.getHeight());
    }

    private double interpolate(double a, double b, double f) {
        return a + f * (b - a);
    }
}
