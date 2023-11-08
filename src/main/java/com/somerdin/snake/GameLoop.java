package com.somerdin.snake;

import com.somerdin.snake.Point.PointInt;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.Iterator;
import java.util.Map;

/**
 * Calls game state update methods and handles canvas drawing and animation.
 */
public class GameLoop {
    public static final int WALL_WIDTH = Sprite.TILE_WIDTH_ACTUAL;
    public static final int PLAYABLE_AREA_WIDTH = GameState.WIDTH * Sprite.TILE_WIDTH_ACTUAL;
    public static final int PLAYABLE_AREA_HEIGHT =
            GameState.HEIGHT * Sprite.TILE_WIDTH_ACTUAL;
    public static final int GAME_AREA_WIDTH = PLAYABLE_AREA_WIDTH + 2 * WALL_WIDTH;
    public static final int TOTAL_HEIGHT =
            PLAYABLE_AREA_HEIGHT + 2 * WALL_WIDTH;
    public static final int GAME_INFO_WIDTH = 150;
    public static final int TOTAL_WIDTH = GAME_AREA_WIDTH + GAME_INFO_WIDTH;

    public static final Color GAME_INFO_BACKGROUND = Color.BLACK;
    public static final Color GAME_INFO_TEXT_COLOR = Color.WHITE;
    public static final Color CHECKERBOARD_LIGHT_COLOR = Color.web("#1919a6");

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

    private MediaPlayer mediaPlayer = new MediaPlayer(Audio.MUSIC);
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
        this.canvas = canvas;
        startTime = System.nanoTime();

        // key press listeners on canvas
        canvas.setOnKeyPressed(key -> {
            if (gameState.isGameOver()) {
                restart();
                start();
            } else if (key.getCode() == KeyCode.SPACE && gameState.getSnake().canBoost()) {
                gameState.getSnake().setBoosting(true);
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
                gameState.removeAllCrumbs();
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
                gameState.getSnake().setBoosting(false);
            }
        });

        g = canvas.getGraphicsContext2D();
        cellLength = PLAYABLE_AREA_HEIGHT / GameState.HEIGHT;

        timer = new AnimationTimer() {
            @Override
            // all game animations update at 60 FPS
            public void handle(long time) {
                currentTime = time;
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
            }
        };
        draw();
        drawStartText();
        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.seek(Duration.ZERO);
            mediaPlayer.play();
        });
    }

    public void start() {
        mediaPlayer.play();
        clear();
        draw();
        startTime = System.nanoTime();
        drawStartText();
        timer.start();
    }

    // draw checkerboard pattern
    private void clear() {
        g.setFill(Color.BLACK);
        g.fillRect(WALL_WIDTH, WALL_WIDTH, PLAYABLE_AREA_WIDTH, PLAYABLE_AREA_HEIGHT);
        g.setFill(GAME_INFO_BACKGROUND);
        g.fillRect(GAME_AREA_WIDTH, 0, GAME_INFO_WIDTH, TOTAL_HEIGHT);

        g.setFill(CHECKERBOARD_LIGHT_COLOR);
        // draw checkerboard pattern
        for (int i = 0; i < gameState.width * gameState.height; i++) {
            int x = i % gameState.width * cellLength + WALL_WIDTH;
            int y = i / gameState.height * cellLength + WALL_WIDTH;
            if (i % 2 == 0) {
                g.fillRect(x, y, cellLength, cellLength);
            }
        }
    }

    /**
     * Draws a scene which is an interpolation of the current and next game
     * states for smooth animation
     */
    private void draw() {
        clear();
        drawPaths();
        drawFood();
        if (gameState.snakeIsExploding()) {
            mediaPlayer.stop();
            drawExplodingSnake();
        }
        if (gameState.bladesAreExploding()) {
            drawExplodingBlades();
        }
        drawBlades();
        if (!gameState.snakeIsExploding()) {
            if (!gameState.getSnake().isInvulnerable()
                    || (frameCount - gameState.snakeInvulnerableTimestamp) % 10 < 5 ) {
                drawSnake();
            }
        }
        drawWalls();
        drawGameInfo();
        if (gameState.isGameOver()) {
            drawGameOver();
            // TODO: fix this - not blinking continue text
            if ((frameCount - gameState.gameOverTimestamp) % 60 < 30) {
                drawContinueText();
            }
        }
    }

    private void drawSnake() {
        Snake snake = gameState.getSnake();
        boolean headDrawn = false;
        for (SnakeCell sc : snake.getBody()) {
            if (sc.isCorner()) {
                drawSpriteToGameBounds(Sprite.SNAKE_BODY,
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
                Sprite.SNAKE_HEAD.setOrientation(sc.getDir());
                drawSpriteToGameBounds(Sprite.SNAKE_HEAD, x, y);
                headDrawn = true;
            } else {
                drawSpriteToGameBounds(Sprite.SNAKE_BODY, x, y);
            }
        }
    }

    private void drawBlades() {
        for (SpinBlade sb : gameState.getBlades()) {
            Sprite.SHURIKEN.rotate(10);
            drawSpriteToGameBounds(Sprite.SHURIKEN, sb.getPos().x() * cellLength,
                    sb.getPos().y() * cellLength);
        }
    }

    private void drawPaths() {
        g.setGlobalAlpha(0.8);
        for (SpinBlade sb : gameState.getBlades()) {
            BladePath bladePath = sb.getBladePath();
            PointInt current = bladePath.getStart();
            Iterator<Direction> it = bladePath.getPath().iterator();
            int i = 0;
            boolean first = true;
            Direction prev = null;
            while (it.hasNext() && i < bladePath.getDrawn()) {
                Direction dir = it.next();

                int corner;
                if (prev == Direction.DOWN && dir == Direction.RIGHT
                        || (prev == Direction.LEFT && dir == Direction.UP)) {
                    corner = Sprite.BOTTOM_LEFT_CORNER;
                } else if ((prev == Direction.DOWN && dir == Direction.LEFT)
                        || (prev == Direction.RIGHT && dir == Direction.UP)) {
                    corner = Sprite.BOTTOM_RIGHT_CORNER;
                } else if ((prev == Direction.UP && dir == Direction.RIGHT)
                        || (prev == Direction.LEFT && dir == Direction.DOWN)) {
                    corner = Sprite.TOP_LEFT_CORNER;
                } else if ((prev == Direction.UP && dir == Direction.LEFT)
                        || (prev == Direction.RIGHT && dir == Direction.DOWN)) {
                    corner = Sprite.TOP_RIGHT_CORNER;
                } else {
                    corner = -1;
                }

                Sprite bladePathTile = Sprite.getBladePathTileById(bladePath.getColorId(), corner);
                if (corner == -1) {
                    if (dir == Direction.LEFT || dir == Direction.RIGHT) {
                        bladePathTile.setRotateAngle(90);
                    } else {
                        bladePathTile.setRotateAngle(0);
                    }
                }
                if (first && sb.isMoving()) {
                    drawSpriteToGameBounds(bladePathTile,
                            sb.getPos().x() * cellLength,
                            sb.getPos().y() * cellLength);
                } else {
                    drawSpriteToGameBounds(bladePathTile, current.x() * cellLength,
                            current.y() * cellLength);
                }
                current = current.go(dir);
                first = false;
                i++;
                prev = dir;
            }
        }
        g.setGlobalAlpha(1.0);
    }

    private void drawStageText() {
        Text gameText = new Text("STAGE " + gameState.getStage());
        gameText.setFont(Font.ATARI_80);
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
        gameText.setFont(Font.ATARI_36);
        g.setFill(Color.WHITE);
        double titleX =
                GAME_AREA_WIDTH / 2 - gameText.getLayoutBounds().getWidth() / 2;
        double titleY = 300;
        g.setFont(gameText.getFont());
        g.fillText(gameText.getText(), titleX, titleY);
    }

    private void drawFood() {
        PointInt snakeHeadPoint = gameState.getSnake().getHead().getPos();
        double snakeHeadX = snakeHeadPoint.x() * cellLength;
        double snakeHeadY = snakeHeadPoint.y() * cellLength;
        for (int y = 0; y < gameState.height; y++) {
            for (int x = 0; x < gameState.width; x++) {
                Food food = gameState.getFood(new PointInt(x ,y));
                double displayY;
                double displayX;
                long suckTimestamp = gameState.markedForSuckTimestamp[y][x];
                if (suckTimestamp > 0) {
                    // frames to suck: 30
                    double factor = ((frameCount - suckTimestamp) / 15D);
                    displayX = interpolate(x * cellLength, snakeHeadX, factor);
                    displayY = interpolate(y * cellLength, snakeHeadY, factor);
                } else {
                    displayY = y * cellLength;
                    displayX = x * cellLength;
                }
                if (food != null) {
                    switch (food) {
                        case RED_APPLE ->
                                drawSpriteToGameBounds(Sprite.APPLE, displayX, displayY);
                        case CHERRY ->
                                drawSpriteToGameBounds(Sprite.CHERRY,
                                        displayX, displayY);
                        case COOKIE -> drawSpriteToGameBounds(Sprite.COOKIE, displayX, displayY);
                        case MAGNET -> drawSpriteToGameBounds(Sprite.MAGNET, displayX, displayY);
                        case CRUMB_1, CRUMB_2, CRUMB_3, CRUMB_4 ->
                                drawSpriteToGameBounds(Sprite.getCrumbById(food.getColorId()), displayX,
                                        displayY);
                    }
                }
            }
        }
    }

    private void drawExplodingSnake() {
        ParticleManager pm = gameState.getSnakeParticles();
        g.setFill(Color.GREEN);
        drawParticles(pm, 4);
    }

    private void drawExplodingBlades() {
        ParticleManager pm = gameState.getBladeParticles();
        g.setFill(Color.GREY);
        drawParticles(pm, 4);
    }

    private void drawParticles(ParticleManager pm, int pixels) {
        if (pm.allParticlesInvisible()) {
            return;
        }
        for (int id = 0; id < pm.getCount(); id++) {
            if (pm.visible[id]) {
                double x = WALL_WIDTH + pm.xPos[id];
                double y = WALL_WIDTH + pm.yPos[id];
                g.fillRect(x, y, pixels, pixels);
            }
        }
    }

    // TODO: this doesn't really work properly
    private void drawSpriteToGameBounds(Sprite toDraw, double x, double y) {
        g.save(); // saves the current state on stack, including the current transform
        rotate(g, toDraw.getRotate(), x + toDraw.width() / 2D,
                y + toDraw.height() / 2D);
        g.drawImage(toDraw.getImage(), toDraw.getX(),
                toDraw.getY(), toDraw.width(),
                toDraw.height(), x + WALL_WIDTH, y + WALL_WIDTH,
                toDraw.width(),
                toDraw.height());
        g.restore();
    }

    private void drawWallSprite(Sprite toDraw, double x, double y) {
        g.save(); // saves the current state on stack, including the current transform
        rotate(g, toDraw.getRotate(), x - toDraw.width() / 2D,
                y - toDraw.height() / 2D);
        g.drawImage(toDraw.getImage(), toDraw.getX(),
                toDraw.getY(), toDraw.width(),
                toDraw.height(), x, y,
                toDraw.width(),
                toDraw.height());
        g.restore();
    }

    private void drawGameOver() {
        Text gameText = new Text("GAME");
        Text overText = new Text("OVER");
        gameText.setFont(Font.ATARI_160);
        overText.setFont(Font.ATARI_160);
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
        startText.setFont(Font.ATARI_36);
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
                int x = j * cellLength;
                int y = i * cellLength;
                Sprite wallSprite;
                if (i == 0 || j == 0
                        || i == gameState.width + 1 || j == gameState.width + 1) {
                    // top left
                    if (i == 0 && j == 0) {
                        wallSprite = Sprite.WALL_CORNER;
                        wallSprite.setRotateAngle(180);
                    }
                    // top right
                    else if (j == gameState.width + 1 && i == 0) {
                        wallSprite = Sprite.WALL_CORNER;
                        wallSprite.setRotateAngle(-90);
                    }
                    // bottom right
                    else if (i == gameState.width + 1 && j == gameState.width + 1) {
                        wallSprite = Sprite.WALL_CORNER;
                        wallSprite.setRotateAngle(0);
                    }
                    // bottom left
                    else if (i == gameState.width + 1 && j == 0) {
                        wallSprite = Sprite.WALL_CORNER;
                        wallSprite.setRotateAngle(90);
                    }
                    // top
                    else if (i == 0) {
                        wallSprite = Sprite.WALL;
                        wallSprite.setRotateAngle(-90);
                    }
                    // right
                    else if (j == gameState.width + 1) {
                        wallSprite = Sprite.WALL;
                        wallSprite.setRotateAngle(0);
                    }
                    // bottom
                    else if (i == gameState.width + 1) {
                        wallSprite = Sprite.WALL;
                        wallSprite.setRotateAngle(90);
                    }
                    // left
                    else {
                        wallSprite = Sprite.WALL;
                        wallSprite.setRotateAngle(180);
                    }
                    drawWallSprite(wallSprite, x, y);
                }
            }
        }
    }

    private void drawGameInfo() {
        g.setFill(GAME_INFO_BACKGROUND);
        g.fillRect(GAME_AREA_WIDTH, 0, GAME_INFO_WIDTH, TOTAL_HEIGHT);
        g.setFill(GAME_INFO_TEXT_COLOR);
        g.setFont(Font.ATARI_24);
        g.fillText("SCORE x" + gameState.getScoreMultiplier(),
                GAME_AREA_WIDTH + 20, 50);
        g.fillText(String.valueOf(gameState.getScore()), GAME_AREA_WIDTH + 20, 100);
        g.fillText(String.valueOf(gameState.getSnake().getBoostGauge()),
                GAME_AREA_WIDTH + 20, 400);
        drawBoostGauge(GAME_AREA_WIDTH + 20, 500);
        drawHealth();
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

    private void drawHealth() {
        if (gameState.getHealth() < 0) {
            return;
        }
        int fullHeartCount = gameState.getHealth() / 4;
        double x = GAME_AREA_WIDTH - 5;
        double y = 100;

        for (int i = 0; i < fullHeartCount; i++) {
            drawSpriteToGameBounds(Sprite.FULL_HEART, x, y);
            if ((i + 1) % 4 == 0) {
                x = GAME_AREA_WIDTH - 5;
                y += 30;
            } else {
                x += 30;
            }
        }

        switch (gameState.getHealth() % 4) {
            case 1 -> drawSpriteToGameBounds(Sprite.QUARTER_HEART, x, y);
            case 2 -> drawSpriteToGameBounds(Sprite.HALF_HEART, x, y);
            case 3 -> drawSpriteToGameBounds(Sprite.THREE_QUARTERS_HEART, x, y);
            default -> {
                return;
            }
        };
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
    }
}
