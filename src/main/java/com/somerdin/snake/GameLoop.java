package com.somerdin.snake;

import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

import java.util.Map;

/**
 * Calls game state update methods and handles canvas drawing and animation.
 */
public class GameLoop {
    public static final int WALL_WIDTH = PixelTile.TILE_WIDTH;
    public static final int GAMEPLAY_AREA_WIDTH = GameState.WIDTH * PixelTile.TILE_WIDTH;
    public static final int GAMEPLAY_AREA_HEIGHT =
            GameState.HEIGHT * PixelTile.TILE_WIDTH;
    public static final int CANVAS_WIDTH = GAMEPLAY_AREA_WIDTH + 2 * WALL_WIDTH;
    public static final int CANVAS_HEIGHT =
            GAMEPLAY_AREA_HEIGHT + 2 * WALL_WIDTH;

    public static final int DRAW_UPDATE_FPS = 60;

    public static final Map<KeyCode, Direction> keyEventMap = Map.of(
            KeyCode.A, Direction.LEFT,
            KeyCode.LEFT, Direction.LEFT,
            KeyCode.W, Direction.UP,
            KeyCode.UP, Direction.UP,
            KeyCode.D, Direction.RIGHT,
            KeyCode.RIGHT, Direction.RIGHT,
            KeyCode.S, Direction.DOWN,
            KeyCode.DOWN, Direction.DOWN
    );

    private GameState gameState;
    private Canvas canvas;
    private GraphicsContext g;
    private int cellLength;
    private AnimationTimer timer;

    private long startTime;
    // the timestamp of the most recent animation cycle start
    private long prevTime = 0;
    // the current timestamp; how far currently into animation cycle
    private long currentTime;
    // number of game state updates which have occurred
    private long frameCount = 0;

    public GameLoop() {
        gameState = new GameState(GameState.WIDTH, GameState.HEIGHT);
        canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        startTime = System.nanoTime();

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
                    gameState.setQueuedDirection(direction);
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
        cellLength = GAMEPLAY_AREA_HEIGHT / GameState.HEIGHT;

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
                System.out.println(gameState.getTotalTime() - (time - startTime));
                gameState.setTimeRemaining(gameState.getTotalTime() - (time - startTime));
            }
        };
        drawWalls();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void start() {
        drawBetweenState();
        startTime = System.nanoTime();
        timer.start();
    }

    private void clear() {
        g.setFill(Color.BLACK);
        g.fillRect(WALL_WIDTH, WALL_WIDTH, GAMEPLAY_AREA_WIDTH, GAMEPLAY_AREA_HEIGHT);
    }

    /**
     * Draws a scene which is an interpolation of the current and next game
     * states for smooth animation
     */
    private void drawBetweenState() {
        clear();

        drawFood();

        drawBlades();

        // draw player snake
        Snake snake = gameState.getSnake();
        boolean headDrawn = false;
        for (SnakeCell sc : snake.getBody()) {
            if (sc.isCorner()) {
                drawImage(PixelTile.SNAKE_BODY,
                        sc.getPos().x() * cellLength,
                        sc.getPos().y() * cellLength);
            }
            double x = interpolate(sc.getPos().x() * cellLength,
                    sc.getNextPos().x() * cellLength,
                    frameCount % snake.speed() / (double) snake.speed());
            double y = interpolate(sc.getPos().y() * cellLength,
                    sc.getNextPos().y() * cellLength,
                    frameCount % snake.speed() / (double) snake.speed());
            if (!headDrawn) {
                PixelTile.SNAKE_HEAD.setOrientation(sc.getDir());
                drawImage(PixelTile.SNAKE_HEAD, x, y);
                headDrawn = true;
            } else {
                drawImage(PixelTile.SNAKE_BODY, x, y);
            }
        }
    }

    private void drawBlades() {
        for (SpinBlade sb : gameState.getBlades()) {
            double x = interpolate(sb.getPos().x() * cellLength,
                    sb.getNextPos().x() * cellLength,
                    frameCount % sb.speed() / (double) sb.speed());
            double y = interpolate(sb.getPos().y() * cellLength,
                    sb.getNextPos().y() * cellLength,
                    frameCount % sb.speed() / (double) sb.speed());
            PixelTile.SHURIKEN.rotate(60);
            drawImage(PixelTile.SHURIKEN, sb.getPos().x() * cellLength,
                    sb.getPos().y() * cellLength);
        }
    }

    private void drawFood() {
        for (int y = 0; y < gameState.height; y++) {
            for (int x = 0; x < gameState.width; x++) {
                Food food = gameState.getFood(new Point(x ,y));
                if (food != null) {
                    int displayY = y * cellLength;
                    int displayX = x * cellLength;
                    switch (food) {
                        case RED_APPLE:
                            drawImage(PixelTile.APPLE, displayX, displayY);
                            break;
                        case CRUMB:
                            drawImage(PixelTile.CRUMB, displayX, displayY);
                            break;
                    }
                }
            }
        }
    }

    private void drawImage(PixelTile toDraw, double x, double y) {
        g.save(); // saves the current state on stack, including the current transform
        rotate(g, toDraw.getRotate(), x + toDraw.width() / 2,
                y + toDraw.height() / 2);
        g.drawImage(toDraw.getImage(), toDraw.getX(),
                toDraw.getY(), toDraw.width(),
                toDraw.height(), x + WALL_WIDTH, y + WALL_WIDTH,
                toDraw.width(),
                toDraw.height());
        g.restore();
    }

    private double interpolate(double a, double b, double f) {
        return a + f * (b - a);
    }

    /**
     * Sets the transform for the GraphicsContext to rotate around a pivot point.
     *
     * @param gc the graphics context the transform to applied to.
     * @param angle the angle of rotation.
     * @param px the x pivot co-ordinate for the rotation (in canvas co-ordinates).
     * @param py the y pivot co-ordinate for the rotation (in canvas co-ordinates).
     */
    private void rotate(GraphicsContext gc, double angle, double px, double py) {
        Rotate r = new Rotate(angle, px + WALL_WIDTH, py + WALL_WIDTH);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }

    private void drawWalls() {
        for (int i = 0; i <= gameState.width + 1; i++) {
            for (int j = 0; j <= gameState.height + 1; j++) {
                if (i == 0 || i == gameState.width + 1
                        || j == 0 || j == gameState.height + 1) {
                    int x = i * cellLength;
                    int y = j * cellLength;
                    Rectangle2D viewport = PixelTile.WALL_IMG.getViewport();
                    g.drawImage(PixelTile.WALL_IMG.getImage(), viewport.getMinX(),
                            viewport.getMinY(), viewport.getWidth(),
                            viewport.getHeight(), x, y,
                            viewport.getWidth(),
                            viewport.getHeight());
                }
            }
        }
    }
}
