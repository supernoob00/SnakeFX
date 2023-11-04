package com.somerdin.snake;

import com.somerdin.snake.Point.PointInt;
import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

import java.util.Iterator;
import java.util.Map;

/**
 * Calls game state update methods and handles canvas drawing and animation.
 */
public class GameLoop {
    public static final int WALL_WIDTH = PixelTile.TILE_WIDTH_ACTUAL;
    public static final int PLAYABLE_AREA_WIDTH = GameState.WIDTH * PixelTile.TILE_WIDTH_ACTUAL;
    public static final int PLAYABLE_AREA_HEIGHT =
            GameState.HEIGHT * PixelTile.TILE_WIDTH_ACTUAL;
    public static final int GAME_AREA_WIDTH = PLAYABLE_AREA_WIDTH + 2 * WALL_WIDTH;
    public static final int TOTAL_HEIGHT =
            PLAYABLE_AREA_HEIGHT + 2 * WALL_WIDTH;
    public static final int GAME_INFO_WIDTH = 150;
    public static final int TOTAL_WIDTH = GAME_AREA_WIDTH + GAME_INFO_WIDTH;

    public static final Color GAME_INFO_BACKGROUND = Color.BLACK;
    public static final Color GAME_INFO_TEXT_COLOR = Color.WHITE;

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

    private int prevStage = 0;

    private long startTime = 0;
    // the timestamp of the most recent animation cycle start
    private long prevTime = 0;
    // the current timestamp; how far currently into animation cycle
    private long currentTime = 0;
    // number of game state updates which have occurred
    private long frameCount = 0;

    private long snakeExplodeTimestamp;
    private long newStageTimestamp;

    public GameLoop(Canvas canvas) {
        gameState = new GameState(GameState.HEIGHT, GameState.WIDTH);
        this.canvas = new Canvas(TOTAL_WIDTH, TOTAL_HEIGHT);
        startTime = System.nanoTime();

        // allows the canvas to register key events
        canvas.setFocusTraversable(true);

        // key press listeners on canvas
        canvas.setOnKeyPressed(key -> {
            if (gameState.isGameOver()) {
                restart();
                start();
            } else if (key.getCode() == KeyCode.SPACE
                    && gameState.getSnake().hasBoost()
                    && !gameState.getSnake().hasCooldown() ) {
                gameState.getSnake().speedUp();
            }
            // arrow key pressed; only change snake direction if key pressed not
            // opposite to current head direction
            else if (keyEventMap.containsKey(key.getCode())) {
                if (!gameState.isStarted()) {
                    gameState.setStarted(true);
                    start();
                } else {
                    Direction direction = keyEventMap.get(key.getCode());
                    if (direction != gameState.getSnake().getHead().getDir().opposite()) {
                        gameState.setQueuedDirection(direction);
                    }
                }
            } else if (key.getCode() == KeyCode.E) {
                gameState.eatAllCrumbs();
            } else if (key.getCode() == KeyCode.C) {
                System.out.println(gameState.crumbCount);
            }
        });

        // key release listeners on canvas
        canvas.setOnKeyReleased(key -> {
            if (gameState.isGameOver()) {
                return;
            }
            if (key.getCode() == KeyCode.SPACE) {
                gameState.getSnake().slowDown();
                if (gameState.getSnake().getBoostGauge() < 25) {
                    gameState.getSnake().resetCooldown();
                }
            }
        });

        g = canvas.getGraphicsContext2D();
        cellLength = PLAYABLE_AREA_HEIGHT / GameState.HEIGHT;

        timer = new AnimationTimer() {
            @Override
            public void handle(long time) {
                currentTime = time;
                // all game animations update at 60 FPS
                if (time - prevTime > 0.99e9 / DRAW_UPDATE_FPS) {
                    gameState.update(frameCount);
                    draw();
                    prevTime = time;

                    if (prevStage != gameState.getStage()) {
                        prevStage = gameState.getStage();
                        newStageTimestamp = time;
                    } else if (time - newStageTimestamp > 1_500_000_000L
                            && time - newStageTimestamp < 3_000_000_000L) {
                        drawStageText();
                    }
                    frameCount++;
                    if (frameCount > 1000) {
                        frameCount = 0;
                    }
                }
            }
        };
        draw();
        drawStartText();
    }

    public void start() {
        clear();
        draw();
        startTime = System.nanoTime();
        drawStartText();
        timer.start();
    }

    private void clear() {
        g.setFill(Color.BLACK);
        g.fillRect(WALL_WIDTH, WALL_WIDTH, PLAYABLE_AREA_WIDTH, PLAYABLE_AREA_HEIGHT);
        g.setFill(GAME_INFO_BACKGROUND);
        g.fillRect(GAME_AREA_WIDTH, 0, GAME_INFO_WIDTH, TOTAL_HEIGHT);
    }

    /**
     * Draws a scene which is an interpolation of the current and next game
     * states for smooth animation
     */
    private void draw() {
        clear();
        drawPaths();
        drawFood();
        drawBlades();
        if (gameState.snakeIsExploding()) {
            drawExplodingSnake();
            if (gameState.isGameOver()) {
                drawGameOver();
                if (frameCount % 60 < 30) {
                    drawContinueText();
                }
            }
        } else {
            drawSnake();
            if (gameState.bladesAreExploding()) {
                drawExplodingBlades();
            }
        }
        drawWalls();
        drawGameInfo();
    }

    private void drawSnake() {
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
            PixelTile.SHURIKEN.rotate(10);
            drawImage(PixelTile.SHURIKEN, sb.getPos().x() * cellLength,
                    sb.getPos().y() * cellLength);
        }
    }

    private void drawPaths() {
        for (SpinBlade sb : gameState.getBlades()) {
            BladePath bladePath = sb.getBladePath();
            PointInt current = bladePath.getStart();
            Iterator<Direction> it = bladePath.getPath().iterator();
            int i = 0;
            boolean first = true;
            while (it.hasNext() && i < bladePath.getDrawn()) {
                Direction dir = it.next();
                PixelTile bladePathTile =
                        PixelTile.getBladePathTileById(bladePath.getColorId());
                bladePathTile.setOrientation(dir);
                if (first && sb.isMoving()) {
                    drawImage(bladePathTile,
                            sb.getPos().x() * cellLength,
                            sb.getPos().y() * cellLength);
                } else {
                    drawImage(bladePathTile, current.x() * cellLength,
                            current.y() * cellLength);
                }
                current = current.go(dir);
                first = false;
                i++;
            }
        }
    }

    private void drawStageText() {
        Text gameText = new Text("STAGE " + gameState.getStage());
        gameText.setFont(SnakeApplication.font(80));
        g.setStroke(Color.WHITE);
        g.setLineWidth(2);
        double titleX =
                GAME_AREA_WIDTH / 2 - gameText.getLayoutBounds().getWidth() / 2;
        double titleY = 300;
        g.setFont(gameText.getFont());
        g.strokeText(gameText.getText(), titleX, titleY);
    }

    private void drawStartText() {
        Text gameText = new Text("PRESS ANY ARROW KEY TO START");
        gameText.setFont(SnakeApplication.font(36));
        g.setFill(Color.WHITE);
        double titleX =
                GAME_AREA_WIDTH / 2 - gameText.getLayoutBounds().getWidth() / 2;
        double titleY = 300;
        g.setFont(gameText.getFont());
        g.fillText(gameText.getText(), titleX, titleY);
    }

    private void drawFood() {
        for (int y = 0; y < gameState.height; y++) {
            for (int x = 0; x < gameState.width; x++) {
                Food food = gameState.getFood(new PointInt(x ,y));
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

    private void drawExplodingSnake() {
        ParticleManager pm = gameState.getSnakeParticles();
        g.setFill(Color.GREEN);
        drawParticles(pm);
    }

    private void drawExplodingBlades() {
        ParticleManager pm = gameState.getBladeParticles();
        g.setFill(Color.GREY);
        drawParticles(pm);
    }

    private void drawParticles(ParticleManager pm) {
        double particleSize = PixelTile.PIXEL_WIDTH;
        for (int i = 0; i < pm.getCount(); i++) {
            double x = WALL_WIDTH + pm.xPos[i];
            double y = WALL_WIDTH + pm.yPos[i];
            g.fillRect(x, y, particleSize, particleSize);
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

    private void drawGameOver() {
        Text gameText = new Text("GAME");
        Text overText = new Text("OVER");
        gameText.setFont(SnakeApplication.font(160));
        overText.setFont(SnakeApplication.font(160));
        g.setFill(Color.WHITE);
        double titleX =
                GAME_AREA_WIDTH / 2 - gameText.getLayoutBounds().getWidth() / 2;
        double titleY = 220;
        double overX =
                GAME_AREA_WIDTH / 2 - overText.getLayoutBounds().getWidth() / 2;
        double overY = 370;
        g.setFont(gameText.getFont());
        g.fillText(gameText.getText(), titleX, titleY);
        g.fillText(overText.getText(), overX, overY);
    }

    private void drawContinueText() {
        Text startText = new Text("PRESS ANY KEY TO CONTINUE");
        startText.setFont(SnakeApplication.font(36));
        double startTextX =
                GAME_AREA_WIDTH / 2 - startText.getLayoutBounds().getWidth() / 2;
        double startTextY = 470;
        g.setFont(startText.getFont());
        g.fillText(startText.getText(), startTextX, startTextY);
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

    private void drawGameInfo() {
        g.setFill(GAME_INFO_BACKGROUND);
        g.fillRect(GAME_AREA_WIDTH, 0, GAME_INFO_WIDTH, TOTAL_HEIGHT);
        g.setFill(GAME_INFO_TEXT_COLOR);
        g.setFont(SnakeApplication.font(24));
        g.fillText("SCORE x" + gameState.getScoreMultiplier(),
                GAME_AREA_WIDTH + 20, 50);
        g.fillText(String.valueOf(gameState.getScore()), GAME_AREA_WIDTH + 20, 100);
        g.fillText(String.valueOf(gameState.getSnake().getBoostGauge()),
                GAME_AREA_WIDTH + 20, 400);
        g.fillText(String.valueOf(gameState.getSnake().getSpeed()),
                GAME_AREA_WIDTH + 20, 550);
        drawBoostGauge(GAME_AREA_WIDTH + 20, 500);
    }

    private void drawBoostGauge(double x, double y) {
        g.setFill(GAME_INFO_TEXT_COLOR);
        g.setLineWidth(2);
        double outerLength = 60;
        double outerWidth = 20;
        g.strokeRect(x, y, outerLength, outerWidth);

        g.setFill(Color.RED);
        double innerLength = outerLength * 0.6;
        double innerWidth = outerWidth * 0.6;
        double percentFilled = gameState.getSnake().getBoostGauge() / 100D;
        g.fillRect(x + outerLength / 2 - innerLength / 2,
                y + outerWidth / 2 - innerWidth / 2,
                innerLength * percentFilled,
                innerWidth);
    }

    private void restart() {
        timer.stop();
        gameState = new GameState(GameState.HEIGHT, GameState.WIDTH);
        clear();

        startTime = 0;
        prevTime = 0;
        currentTime = 0;
        frameCount = 0;
        snakeExplodeTimestamp = 0;
        System.gc();
    }
}
