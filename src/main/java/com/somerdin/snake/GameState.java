package com.somerdin.snake;

import com.somerdin.snake.Point.PointDouble;
import com.somerdin.snake.Point.PointInt;
import com.somerdin.snake.Resource.Audio;
import com.somerdin.snake.Resource.Sprite;
import javafx.scene.shape.Circle;

import java.io.Writer;
import java.util.*;

public class GameState {
    public static final int WIDTH = 25;
    public static final int HEIGHT = 25;

    public static final double INITIAL_BOMB_RADIUS =
            Sprite.TILE_WIDTH_ACTUAL / 2;

    public final int width;
    public final int height;

    public final Item[][] food;
    public int crumbCount;

    private final Deque<SpinBlade> blades = new ArrayDeque<>();
    private final List<PointInt> crumbsToDraw = new ArrayList<>();
    private final Circle bombRadius = new Circle();

    public final TimedEvent GAME_OVER_EVENT =
            new TimedEvent(TimedEvent.TimedEventType.GAME_OVER);
    public final TimedEvent SNAKE_EXPLODE_EVENT =
            new TimedEvent(TimedEvent.TimedEventType.SNAKE_EXPLODE);
    public final TimedEvent INVULNERABLE_EVENT =
            new TimedEvent(TimedEvent.TimedEventType.INVULNERABLE);
    public final TimedEvent INVINCIBLE_POWER_UP_EVENT =
            new TimedEvent(TimedEvent.TimedEventType.INVINCIBLE_POWER_UP);
    public final TimedEvent BOMB_POWER_UP_EVENT =
            new TimedEvent(TimedEvent.TimedEventType.BOMB_POWER_UP);

    private Direction queuedDirection;

    private final Snake snake;
    private int health = 0;

    // particle coordinates are actual coordinates on canvas
    private ParticleManager snakeParticles;

    private boolean isStarted;

    private int score = 0;
    private int stage = 1;
    private long frames = 0;
    private int crumbClearCount = 0;

    private static long getSecondsFromFrames(long frames) {
        return frames / 60;
    }

    public GameState(int height, int width) {
        this.food = new Item[height][width];
        this.crumbCount = 0;
        this.width = width;
        this.height = height;
        snake = new Snake(new SnakeCell(Direction.RIGHT, new PointInt(14, 12),
                false));
        setInitialCrumbPattern();
        spawnBlade();
    }

    public Snake getSnake() {
        return this.snake;
    }

    public Item getFood(PointInt p) {
        return food[p.y()][p.x()];
    }

    public void placeFood(PointInt p, Item food) {
        Item existing = this.food[p.y()][p.x()];
        if (existing != null && existing.isCrumb() && food.isFruit()) {
            crumbCount--;
        } else if (food.isCrumb() && (existing == null || !existing.isCrumb())) {
            crumbCount++;
        }
        this.food[p.y()][p.x()] = food;
    }

    private void removeFood(PointInt p) {
        Item food = this.food[p.y()][p.x()];
        this.food[p.y()][p.x()] = null;
        if (food != null && food.isCrumb()) {
            crumbCount--;
            if (crumbCount == 0) {
                crumbClearCount++;
                makeBladesExplode();
                setInitialCrumbPattern();

                score += switch (crumbClearCount) {
                    case 1 -> 25_000;
                    case 2 -> 50_000;
                    case 3 -> 100_000;
                    case 4 -> 200_000;
                    default -> 500_000;
                };

                Audio.CRUMBS_CLEARED_SOUND.play();
            }
        }
    }

    public void moveSnake() {
        int headX = snake.getHead().getPos().x();
        int headY = snake.getHead().getPos().y();

        if (headX == 0 && snake.getHead().getDir() == Direction.LEFT) {
            if (headY == 0) {
                snake.setDirection(Direction.DOWN);
            } else if (headY == HEIGHT - 1) {
                snake.setDirection(Direction.UP);
            } else {
                Direction newDir = getRandomDir(Direction.DOWN, Direction.UP);
                snake.setDirection(newDir);
            }
        } else if (headY == 0 && snake.getHead().getDir() == Direction.UP) {
            if (headX == 0) {
                snake.setDirection(Direction.RIGHT);
            } else if (headX == WIDTH - 1) {
                snake.setDirection(Direction.LEFT);
            } else {
                Direction newDir = getRandomDir(Direction.LEFT,
                        Direction.RIGHT);
                snake.setDirection(newDir);
            }
        } else if (headX == WIDTH - 1 && snake.getHead().getDir() == Direction.RIGHT) {
            if (headY == 0) {
                snake.setDirection(Direction.DOWN);
            } else if (headY == HEIGHT - 1) {
                snake.setDirection(Direction.UP);
            } else {
                Direction newDir = getRandomDir(Direction.DOWN, Direction.UP);
                snake.setDirection(newDir);
            }
        } else if (headY == HEIGHT - 1 && snake.getHead().getDir() == Direction.DOWN) {
            if (headX == 0) {
                snake.setDirection(Direction.RIGHT);
            } else if (headX == WIDTH - 1) {
                snake.setDirection(Direction.LEFT);
            } else {
                Direction newDir = getRandomDir(Direction.LEFT,
                        Direction.RIGHT);
                snake.setDirection(newDir);
            }
        }
        snake.move();

        if (snake.headOnBody()
                && !INVULNERABLE_EVENT.inProgress(frames)
                && !INVINCIBLE_POWER_UP_EVENT.inProgress(frames)) {
            takeDamage();
        }

        Item foodAtHead = getFood(snake.getHead().getPos());
        if (foodAtHead != null) {
            eatFood(snake.getHead().getPos());
        }
    }

    private void eatFood(PointInt p) {
        Item foodAtHead = getFood(p);
        removeFood(p);
        if (!foodAtHead.isCrumb()) {
            spawnFruit();
        }
        if (foodAtHead.isFruit()) {
            snake.grow();
            health += foodAtHead.getHealthValue();
            Audio.EAT_FRUIT_SOUND.play();
        } else if (foodAtHead.isPowerUp()) {
            switch (foodAtHead.getFood()) {
                case INVINCIBLE -> {
                    INVINCIBLE_POWER_UP_EVENT.start(frames);
                    Audio.INVINCIBLE_SOUND.play();
                }
                case BOMB -> {
                    BOMB_POWER_UP_EVENT.start(frames);
                    bombRadius.setCenterX(GameLoop.WALL_WIDTH
                            + p.x() * Sprite.TILE_WIDTH_ACTUAL + Sprite.TILE_WIDTH_ACTUAL / 2);
                    bombRadius.setCenterY(GameLoop.WALL_WIDTH
                            + p.y() * Sprite.TILE_WIDTH_ACTUAL + Sprite.TILE_WIDTH_ACTUAL / 2);
                    bombRadius.setRadius(INITIAL_BOMB_RADIUS);
                    Audio.BOMB_SOUND.play();
                }
            }
        }
        score += foodAtHead.getScore() * getScoreMultiplier();
    }

    public void spawnFruit() {
        // TODO: failsafe if unable to find random empty tile
        PointInt random = getRandomPoint();
        Item existing = getFood(random);
        while (snake.containsPoint(random)
                || existing != null) {
            random = getRandomPoint();
            existing = getFood(random);
        }

        Food foodToPlace = null;
        // base probabilities:
        // APPLE: 0.70
        // CHERRY: 0.10
        // COOKIE: 0.05

        // INVINCIBILITY: 0.102
        // BOMB: 0.05
        double rand = Math.random();
        double[] probabilities = new double[]{0.88, 0.04, 0.02, 0.03,
                0.03};
        double adjustment = snake.getLength() * 0.015;
        double[] dist = new double[5];
        for (int i = 0; i < probabilities.length; i++) {
            if (i == 0) {
                dist[i] = probabilities[i] - adjustment;
            } else {
                dist[i] = dist[i - 1] + probabilities[i] + adjustment / 4;
            }
        }
        for (int i = 0; i < dist.length; i++) {
            if (rand < dist[i]) {
                foodToPlace = switch (i) {
                    case 1 -> Food.CHERRY;
                    case 2 -> Food.COOKIE;
                    case 3 -> Food.INVINCIBLE;
                    case 4 -> Food.BOMB;
                    default -> Food.RED_APPLE;
                };
                break;
            }
            foodToPlace = Food.RED_APPLE;
        }
        placeFood(random, new Item(foodToPlace, frames));
    }

    public void spawnBlade() {
        int spawnPlacement = (int) (Math.random() * width);
        int spawnSide = (int) (Math.random() * 4);

        double rand = Math.random();
        double speed;

        switch (stage) {
            case 1, 2 -> {
                if (rand < 0.5) {
                    speed = SpinBlade.SLOW_BLADE_SPEED;
                } else {
                    speed = SpinBlade.MEDIUM_BLADE_SPEED;
                }
            }
            case 3 -> {
                if (rand < 0.5) {
                    speed = SpinBlade.SLOW_BLADE_SPEED;
                } else {
                    speed = SpinBlade.MEDIUM_BLADE_SPEED;
                }
            }
            case 4 -> {
                if (rand < 0.3) {
                    speed = SpinBlade.SLOW_BLADE_SPEED;
                } else if (rand < 0.9) {
                    speed = SpinBlade.MEDIUM_BLADE_SPEED;
                } else {
                    speed = SpinBlade.FAST_BLADE_SPEED;
                }
            }
            case 5 -> {
                if (rand < 0.2) {
                    speed = SpinBlade.SLOW_BLADE_SPEED;
                } else if (rand < 0.5) {
                    speed = SpinBlade.MEDIUM_BLADE_SPEED;
                } else {
                    speed = SpinBlade.FAST_BLADE_SPEED;
                }
            }
            default -> {
                speed = SpinBlade.FAST_BLADE_SPEED;
            }
        };
        double size = 0.75;

        PointDouble start;
        SpinBlade blade = null;
        switch (spawnSide) {
            case 0 -> {
                start = new PointDouble(spawnPlacement, -1);
                blade = new SpinBlade(start, Direction.DOWN, speed, size);
            }
            case 1 -> {
                start = new PointDouble(spawnPlacement, width + 1);
                blade = new SpinBlade(start, Direction.UP, speed, size);
            }
            case 2 -> {
                start = new PointDouble(-2, spawnPlacement);
                blade = new SpinBlade(start, Direction.RIGHT, speed, size);
            }
            case 3 -> {
                start = new PointDouble(width + 1, spawnPlacement);
                blade = new SpinBlade(start, Direction.LEFT, speed, size);
            }
        }
        blades.add(blade);
    }

    public Iterable<SpinBlade> getBlades() {
        return blades;
    }

    public void spawnBlades() {
        int bladeCount = switch (stage) {
            case 1 -> 3;
            case 2 -> 6;
            case 3 -> 9;
            case 4 -> 12;
            case 5 -> 15;
            default -> 18;
        };
        while (blades.size() < bladeCount) {
            spawnBlade();
        }
    }

    public void update(long updateCount) {
        frames = updateCount;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Item item = food[i][j];
                if (item != null && item.expired(updateCount)) {
                    removeFood(new PointInt(j, i));
                    if (!isGameOver()) {
                        spawnFruit();
                    }
                }
            }
        }
        if (SNAKE_EXPLODE_EVENT.inProgress(updateCount)) {
            if (snakeParticles.isMoving()) {
                snakeParticles.updatePos(1);
            }
            if (SNAKE_EXPLODE_EVENT.framesPassed(updateCount) > 180
                    && !GAME_OVER_EVENT.inProgress(frames)) {
                if (score > Score.getHighScore()) {
                    Score.writeHighScore(score);
                    Score.setHighScore(score);
                }
                GAME_OVER_EVENT.start(updateCount);
                Audio.GAME_OVER_SOUND.play();
            }
            return;
        } else {
            if (BOMB_POWER_UP_EVENT.inProgress(updateCount)) {
                double radius = bombRadius.getRadius();
                bombRadius.setRadius(radius + BOMB_POWER_UP_EVENT.progress(updateCount) * 30);
            }
            // TODO: unexpected behavior if game state update fps is not
            //  divisible by snake speed
            if (updateCount % snake.speed() == 0) {
                if (queuedDirection != null) {
                    snake.setDirection(queuedDirection);
                    queuedDirection = null;
                }
                moveSnake();
            }

            Iterator<SpinBlade> it = blades.iterator();
            while (it.hasNext()) {
                SpinBlade sb = it.next();
                // TODO: need to write custom contains method
                double xDist =
                        bombRadius.getCenterX() - sb.getPos().x() * Sprite.TILE_WIDTH_ACTUAL - Sprite.TILE_WIDTH_ACTUAL;
                double yDist =
                        bombRadius.getCenterY() - sb.getPos().y() * Sprite.TILE_WIDTH_ACTUAL - Sprite.TILE_WIDTH_ACTUAL;
                if (BOMB_POWER_UP_EVENT.inProgress(frames)
                        && Math.pow(xDist, 2) + Math.pow(yDist, 2) < Math.pow(bombRadius.getRadius(), 2) && !sb.isExploding()) {
                    sb.makeExplode();
                }
                if (sb.containsAnyPoint(snake.getPoints())
                        && !sb.isExploding()) {
                    if (!INVULNERABLE_EVENT.inProgress(frames)
                            && !INVINCIBLE_POWER_UP_EVENT.inProgress(frames)) {
                        takeDamage();
                    } else if (INVINCIBLE_POWER_UP_EVENT.inProgress(frames)) {
                        makeBladeExplode(sb);
                    }
                }
                if (sb.isExploding()) {
                    if (sb.getParticles().isMoving()) {
                        sb.getParticles().updatePos(1);
                    } else if (!sb.getParticles().setRandomParticleInvisible()) {
                        it.remove();
                    }
                } else {
                    if (updateCount % BladePath.PATH_DRAW_SPEED == 0) {
                        sb.getBladePath().addDrawn();
                    }
                    if (sb.isMoving()) {
                        sb.move();
                    }
                    // remove from deque if spin-blade is out of bounds
                    if (sb.getBladePath().size() == 0) {
                        it.remove();
                        spawnBlade();
                    }
                }
            }
            spawnBlades();

            if (crumbsToDraw.size() > 0) {
                for (int i = 0; i < 6; i++) {
                    PointInt p = crumbsToDraw.remove(crumbsToDraw.size() - 1);
                    placeFood(p, new Item(Food.getRandomCrumb(), frames));
                }
                if (crumbsToDraw.isEmpty()) {
                    for (int i = 0; i < crumbClearCount + 1; i++) {
                        spawnFruit();
                    }
                }
            }
        }
        if (blades.size() == 0) {
            Audio.BLADE_SOUND.stop();
        }

        if ((getSecondsFromFrames(frames) > 30
                || snake.getLength() > 8) && stage == 1) {
            stage++;
        } else if ((getSecondsFromFrames(frames) > 150
                || snake.getLength() > 16) && stage == 2) {
            stage++;
        } else if ((getSecondsFromFrames(frames) > 270
                || snake.getLength() > 24) && stage == 3) {
            stage++;
        } else if ((getSecondsFromFrames(frames) > 290
                || snake.getLength() > 30) && stage == 4) {
            stage++;
        } else if ((getSecondsFromFrames(frames) > 410
                || snake.getLength() > 36) && stage == 5) {
            stage++;
        }
    }

    private Direction getRandomDir(Direction... options) {
        int rand = (int) (options.length * Math.random());
        return options[rand];
    }

    private PointInt getRandomPoint() {
        int randomX = (int) (width * Math.random());
        int randomY = (int) (height * Math.random());
        return new PointInt(randomX, randomY);
    }

    public void setQueuedDirection(Direction direction) {
        this.queuedDirection = direction;
    }

    public long getScore() {
        return score;
    }

    public double getScoreMultiplier() {
        double snakeMultiplier = Math.pow(1.05, snake.getLength() - 4);
        return (int) (snakeMultiplier / 0.05) * 0.05;
    }

    public ParticleManager getSnakeParticles() {
        return snakeParticles;
    }

    public void makeBladeExplode(SpinBlade blade) {
        score += 100;
        blade.makeExplode();
    }

    public void makeBladesExplode() {
        for (SpinBlade blade : blades) {
            blade.makeExplode();
            makeBladeExplode(blade);
        }
    }

    public void makeSnakeExplode() {
        SNAKE_EXPLODE_EVENT.start(frames);
        int pixelsPerTile =
                Sprite.TILE_WIDTH_PIXELS * Sprite.TILE_WIDTH_PIXELS;
        int pixelCount =
                pixelsPerTile * snake.getLength();

        snakeParticles = new ParticleManager(pixelCount,
                GameLoop.PLAYABLE_AREA_WIDTH, GameLoop.PLAYABLE_AREA_HEIGHT,
                0.025);

        int j = 0;
        for (SnakeCell sc : snake.getBody()) {
            PointInt p = sc.getPos();

            for (int i = 0; i < pixelsPerTile; i++) {
                int id = j * pixelsPerTile + i;
                snakeParticles.xPos[id] =
                        p.x() * Sprite.TILE_WIDTH_ACTUAL + (i % Sprite.TILE_WIDTH_PIXELS) * Sprite.PIXEL_WIDTH;
                snakeParticles.yPos[id] =
                        p.y() * Sprite.TILE_WIDTH_ACTUAL + (i / Sprite.TILE_WIDTH_PIXELS) * Sprite.PIXEL_WIDTH;

                double randomAngleRange = 80;
                double angle =
                        (sc.getDir().getAngle() - randomAngleRange / 2)
                                + (int) (Math.random() * randomAngleRange);
                double speed =
                        2.5 + (4 / snake.speed()) + (Math.random() * 0.4 - 0.2);
                snakeParticles.setVelocity(id, angle, speed);
            }
            j++;
        }
        Audio.DEATH_SOUND.play();
    }

    public boolean isGameOver() {
        return GAME_OVER_EVENT.inProgress(frames);
    }

    public int getStage() {
        return stage;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean isStarted) {
        this.isStarted = isStarted;
    }

    private void setInitialCrumbPattern() {
        int mazeSize = (width + 1) / 2;
        Maze maze = new Maze(mazeSize);
        Maze.MazeCell[][] cells = maze.getMaze();

        for (int i = 1; i < width; i += 2) {
            for (int j = 1; j < height; j += 2) {
                PointInt p = new PointInt(j, i);
                crumbsToDraw.add(p);
            }
        }
        for (int i = 1; i < cells.length - 1; i++) {
            for (int j = 1; j < cells.length - 1; j++) {
                Maze.MazeCell cell = cells[i][j];
                PointInt p = new PointInt(2 * (j - 1), 2 * (i - 1));

                if (cell.north && p.y() > 0) {
                    crumbsToDraw.add(p.go(Direction.UP));
                }
                if (cell.east && p.x() < width - 1) {
                    crumbsToDraw.add(p.go(Direction.RIGHT));
                }
                if (cell.south && p.y() < height - 1) {
                    crumbsToDraw.add(p.go(Direction.DOWN));
                }
                if (cell.west && p.x() > 0) {
                    crumbsToDraw.add(p.go(Direction.LEFT));
                }
                if (getFood(p) != null) {
                    removeFood(p);
                }
            }
        }
        Collections.shuffle(crumbsToDraw);
    }

    // FOR TESTING
    public void removeAllCrumbs() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                removeFood(new PointInt(i, j));
            }
        }
    }

    public int getHealth() {
        return health;
    }

    private void takeDamage() {
        health -= 4;
        snake.resetLength(Math.max(snake.getLength() - 3,
                Snake.INITIAL_SIZE));
        if (health < 0) {
            makeSnakeExplode();
        } else {
            INVULNERABLE_EVENT.start(frames);
        }
        Audio.DAMAGE_SOUND.play();
    }

    private boolean bladeParticlesStillMoving() {
        for (SpinBlade blade : blades) {
            if (blade.getParticles().isMoving()) {
                return true;
            }
        }
        return false;
    }

    public Circle getBombRadius() {
        if (!BOMB_POWER_UP_EVENT.inProgress(frames)) {
            throw new IllegalStateException();
        }
        return bombRadius;
    }

    public int getBladeCount() {
        return blades.size();
    }
}
