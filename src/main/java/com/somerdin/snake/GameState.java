package com.somerdin.snake;

import com.somerdin.snake.Point.PointDouble;
import com.somerdin.snake.Point.PointInt;

import java.util.*;

public class GameState {
    public static final int WIDTH = 25;
    public static final int HEIGHT = 25;

    private static final int INVUNERABLE_TIME = 150;

    PointInt TOP_LEFT = new PointInt(0, 0);
    PointInt BOTTOM_LEFT = new PointInt(0, HEIGHT - 1);
    PointInt BOTTOM_RIGHT = new PointInt(HEIGHT - 1, HEIGHT - 1);
    PointInt TOP_RIGHT = new PointInt(HEIGHT - 1, 0);

    public final Food[][] food;
    public final long[][] markedForSuckTimestamp;
    public int crumbCount;
    public final int width;
    public final int height;

    private final Snake snake;
    // particle coordinates are actual coordinates on canvas
    private ParticleManager snakeParticles;
    private ParticleManager bladeParticles;

    private int health = 0;
    private int shields = 0;

    private boolean isStarted;
    private boolean isExploding;
    private boolean bladesExploding;
    private boolean isGameOver;
    private boolean isMagnetized;

    private final Deque<SpinBlade> blades = new ArrayDeque<>();
    private final List<PointInt> crumbsToDraw = new ArrayList<>();

    private long snakeExplodeTimestamp;
    private long bladeExplodeTimestamp;
    public long snakeInvulnerableTimestamp;
    public long gameOverTimestamp;


    private Direction queuedDirection;
    private long totalTime = 30_000_000_000L;
    private long timeRemaining = 30_000_000_000L;
    private long score = 0;
    private int stage = 1;
    private long frames = 0;

    // TODO: decide what to do about out of bounds errors
    public GameState(int height, int width) {
        this.food = new Food[height][width];
        this.markedForSuckTimestamp = new long[height][width];
        this.crumbCount = 0;
        this.width = width;
        this.height = height;

        snake = new Snake(new SnakeCell(Direction.RIGHT, new PointInt(14, 12),
                false));
        setInitialCrumbPattern();
        spawnBlade();
        placeFood(new PointInt(0, 0), Food.MAGNET);
    }

    public Snake getSnake() {
        return this.snake;
    }

    public Food getFood(PointInt p) {
        return food[p.y()][p.x()];
    }

    public void placeFood(PointInt p, Food food) {
        Food existing = this.food[p.y()][p.x()];
        if (existing != null && existing.isCrumb() && food.isFruit()) {
            crumbCount--;
        } else if (food.isCrumb() && (existing == null || !existing.isCrumb())) {
            crumbCount++;
        }
        this.food[p.y()][p.x()] = food;
    }

    private void removeFood(PointInt p) {
        Food food = this.food[p.y()][p.x()];
        this.food[p.y()][p.x()] = null;
        if (food != null && food.isCrumb()) {
            crumbCount--;
            if (crumbCount == 0) {
                stage++;
                makeBladesExplode();
                setInitialCrumbPattern();
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

        if (snake.headOnBody()) {
            takeDamage();
        }

        Food foodAtHead = getFood(snake.getHead().getPos());
        if (foodAtHead != null) {
            eatFood(snake.getHead().getPos());
        }

        // mark all surrounding food for suck if magnetized
        if (isMagnetized) {
            System.out.println("SUCKING...");

            markSurroundingFoodForSuck();
        }
    }

    private void eatFood(PointInt p) {
        Food foodAtHead = getFood(p);
        removeFood(p);
        if (foodAtHead.isFruit()) {
            spawnFruit();
            snake.grow();
            health += foodAtHead.getHealthValue();
            score += foodAtHead.getScore() * getScoreMultiplier();
            Audio.EAT_FRUIT_SOUND.play();
        } else if (foodAtHead.isPowerUp()) {
            switch (foodAtHead) {
                case SHIELD:
                    if (shields == 3) {
                        health += foodAtHead.getHealthValue();
                        score += foodAtHead.getScore() * getScoreMultiplier();
                    } else {
                        shields++;
                    }
                    break;
                case MAGNET:
                    if (isMagnetized) {
                        health += foodAtHead.getHealthValue();
                        score += foodAtHead.getScore() * getScoreMultiplier();
                    } else {
                        System.out.println("MAGNETIZED");
                        isMagnetized = true;
                    }
                    break;
                case INVINCIBLE:
                    break;
            }
        } else {
            score += foodAtHead.getScore() * getScoreMultiplier();
        }
    }

    private void markSurroundingFoodForSuck() {
        PointInt headPos = snake.getHead().getPos();
        PointInt topLeft = new PointInt(Math.max(headPos.x() - 1, 0),
                Math.max(headPos.y() - 1, 0));
        PointInt bottomRight = new PointInt(Math.min(headPos.x() + 1,
                width - 1),
                Math.min(headPos.y() + 1, height - 1));
        for (int i = topLeft.y(); i <= bottomRight.y(); i++) {
            for (int j = topLeft.x(); j <= bottomRight.x(); j++) {
                if (i == headPos.y() && j == headPos.x()) {
                    continue;
                }
                if (food[i][j] != null) {
                    markedForSuckTimestamp[i][j] = frames;
                    System.out.println("I: " + frames);
                    System.out.println("J: " + frames);
                }
            }
        }
    }

    public void spawnFruit() {
        // max amount of attempts to spawn food in a random location,
        // in case something has gone horribly awry
        PointInt random = getRandomPoint();
        Food existing = getFood(random);
        while (snake.containsPoint(random)
                || existing != null) {
            random = getRandomPoint();
            existing = getFood(random);
        }
        Food foodToPlace;
        // player should have better spawn rates for better fruit the longer
        // they are
        // TODO: adjust fruit spawn rates
        double typeOfFruitProbability = Math.random();
        double chance;
        switch (stage) {
            case 1:
                chance = 1 - (snake.getLength() - snake.INITIAL_SIZE) * 0.025;
                if (chance < 0.6) {
                    chance = 0.6;
                }
                if (typeOfFruitProbability < chance) {
                    foodToPlace = Food.RED_APPLE;
                } else {
                    foodToPlace = Food.CHERRY;
                }
                break;
            case 2:
                chance = 1 - (snake.getLength() - Snake.INITIAL_SIZE) * 0.025;
                if (chance < 0.3) {
                    chance = 0.3;
                }
                if (typeOfFruitProbability < chance) {
                    foodToPlace = Food.RED_APPLE;
                } else {
                    foodToPlace = Food.CHERRY;
                }
                break;
            case 3:
                chance = 1 - (snake.getLength() - Snake.INITIAL_SIZE) * 0.025;
                if (chance < 0.3) {
                    chance = 0.3;
                }
                if (typeOfFruitProbability < chance) {
                    foodToPlace = Food.RED_APPLE;
                } else if (typeOfFruitProbability < chance + (1 - chance)* 0.8) {
                    foodToPlace = Food.CHERRY;
                } else {
                    foodToPlace = Food.COOKIE;
                }
                break;
            default:
                chance = 1 - (snake.getLength() - Snake.INITIAL_SIZE) * 0.025;
                if (chance < 0.2) {
                    chance = 0.2;
                }
                if (typeOfFruitProbability < chance) {
                    foodToPlace = Food.RED_APPLE;
                } else if (typeOfFruitProbability < chance + (1 - chance)* 0.8) {
                    foodToPlace = Food.CHERRY;
                } else {
                    foodToPlace = Food.COOKIE;
                }
                break;
        }
        placeFood(random, foodToPlace);
    }

    public void spawnBlade() {
        int spawnPlacement = (int) (Math.random() * width);
        int spawnSide = (int) (Math.random() * 4);
        double speed = SpinBlade.SLOW_BLADE_SPEED;
        double size = 0.75;

        PointDouble start;
        SpinBlade blade = null;
        switch (spawnSide) {
            case 0:
                start = new PointDouble(spawnPlacement, -1);
                blade = new SpinBlade(start, Direction.DOWN, speed, size);
                break;
            case 1:
                start = new PointDouble(spawnPlacement, width + 1);
                blade = new SpinBlade(start, Direction.UP, speed, size);
                break;
            case 2:
                start = new PointDouble(-2, spawnPlacement);
                blade = new SpinBlade(start, Direction.RIGHT, speed, size);
                break;
            case 3:
                start = new PointDouble(width + 1, spawnPlacement);
                blade = new SpinBlade(start, Direction.LEFT, speed, size);
                break;
        }
        blades.add(blade);
    }

    public Iterable<SpinBlade> getBlades() {
        return blades;
    }

    public void spawnBlades() {
        if (!Audio.BLADE_SOUND.isPlaying()) {
            Audio.BLADE_SOUND.play();
        }
        int bladeCount = switch (stage) {
            case 1 -> 3;
            case 2 -> 4;
            case 3 -> 5;
            case 4 -> 8;
            case 5 -> 12;
            default -> 15;
        };
        while (blades.size() < bladeCount) {
            spawnBlade();
        }
    }

    public void update(long updateCount) {
        frames = updateCount;
        if (snakeIsExploding()) {
            if (snakeParticles.isMoving()) {
                snakeParticles.updatePos(1);
            }
            if (System.nanoTime() - snakeExplodeTimestamp > 3_000_000_000L) {
                 isGameOver = true;
                 gameOverTimestamp = updateCount;
            }
            if (updateCount % 1000 == 0) {
                int x = 1;
            }
        } else {
            if (isMagnetized) {
                // check all sucked fruits
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        if (updateCount - markedForSuckTimestamp[i][j] == 15) {
                            markedForSuckTimestamp[i][j] = 0;
                            PointInt p = new PointInt(j, i);
                            if (getFood(p) != null) {
                                eatFood(new PointInt(j, i));
                            }
                        }
                    }
                }
            }

            if (updateCount - snakeInvulnerableTimestamp > INVUNERABLE_TIME) {
                snake.setInvulnerable(false);
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

            if (bladesExploding) {
                if (bladeParticles.isMoving()) {
                    bladeParticles.updatePos(1);
                } else if (updateCount % 2 == 0) {
                    if (!bladeParticles.setRandomParticleInvisible()) {
                        bladesExploding = false;
                    }
                }
            }

            Iterator<SpinBlade> it = blades.iterator();
            while (it.hasNext()) {
                SpinBlade sb = it.next();
                if (updateCount % BladePath.PATH_DRAW_SPEED == 0) {
                    sb.getBladePath().addDrawn();
                }
                if (sb.isMoving()) {
                    sb.move();
                }
                PointDouble p = sb.getPos();
                // remove from deque if spin-blade is out of bounds
                if (sb.getBladePath().size() == 0) {
                    it.remove();
                    spawnBlade();
                }
            }

            // TODO: consolidate into collisions method
            for (SpinBlade sb : blades) {
                if (sb.containsAnyPoint(snake.getPoints())) {
                    takeDamage();
                }
            }
            spawnBlades();

            if (crumbsToDraw.size() > 0) {
                for (int i = 0; i < 6; i++) {
                    PointInt p = crumbsToDraw.remove(crumbsToDraw.size() - 1);
                    placeFood(p, Food.getRandomCrumb());
                }
                if (crumbsToDraw.isEmpty()) {
                    spawnFruit();
                }
            }
        }
        if (blades.size() == 0) {
            Audio.BLADE_SOUND.stop();
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

    public Direction getQueuedDirection() {
        return queuedDirection;
    }

    public void setQueuedDirection(Direction direction) {
        this.queuedDirection = direction;
    }

    public long getScore() {
        return score;
    }

    public double getScoreMultiplier() {
        double stageMultiplier = switch (stage) {
            case 1 -> 1;
            case 2 -> 1.2;
            case 3 -> 1.5;
            case 4 -> 2;
            case 5 -> 3;
            default -> 5;
        };
        double snakeMultiplier = snake.getLength() / 3 * stageMultiplier;
        return stageMultiplier * snakeMultiplier;
    }

    public ParticleManager getSnakeParticles() {
        return snakeParticles;
    }

    public boolean snakeIsExploding() {
        return isExploding;
    }

    public void makeBladesExplode() {
        bladesExploding = true;
        bladeExplodeTimestamp = System.nanoTime();
        int pixelsPerTile = Sprite.TILE_WIDTH_PIXELS * Sprite.TILE_WIDTH_PIXELS;
        int pixelCount = pixelsPerTile * blades.size();

        bladeParticles = new ParticleManager(pixelCount,
                GameLoop.PLAYABLE_AREA_WIDTH, GameLoop.PLAYABLE_AREA_HEIGHT,
                0.05);

        int j = 0;
        for (SpinBlade blade : blades) {
            PointDouble p = blade.getPos();

            for (int i = 0; i < pixelsPerTile; i++) {
                int id = j * pixelsPerTile + i;

                int xUnits = i % Sprite.TILE_WIDTH_PIXELS;
                int yUnits = i / Sprite.TILE_WIDTH_PIXELS;
                double x =
                        p.x() * Sprite.TILE_WIDTH_ACTUAL + xUnits * Sprite.PIXEL_WIDTH;
                double y =
                        p.y() * Sprite.TILE_WIDTH_ACTUAL + yUnits * Sprite.PIXEL_WIDTH;
                double dist = Math.sqrt(Math.pow(xUnits - 4, 2) + Math.pow(yUnits - 4, 2));
                bladeParticles.xPos[id] = x;
                bladeParticles.yPos[id] = y;

                // double randomAngleRange = 30;
                // from -range / 2 to +range / 2
                // double variance =
                        // Math.random() * randomAngleRange - randomAngleRange
                // / 2;
                double angle =
                        Math.toDegrees(Math.atan((double) yUnits - 4 / (xUnits - 4D)));
                double factor = dist / Math.sqrt(32);
                double calcXSpeed = (yUnits - 4);
                double calcYSpeed = (-xUnits + 4);

                if (calcXSpeed < 0) {
                    calcXSpeed = 0.5 * Math.max(calcXSpeed,
                            -4);
                } else {
                    calcXSpeed = 0.5 * Math.min(calcXSpeed,
                            4);
                }

                if (calcYSpeed < 0) {
                    calcYSpeed = 0.5 * Math.max(calcYSpeed,
                        -4);
                } else {
                    calcYSpeed = 0.5 * Math.min(calcYSpeed,
                            4);
                }
                bladeParticles.xSpeed[id] =
                        4 * calcXSpeed * (Math.random() * 0.5 + 0.5);
                bladeParticles.ySpeed[id] =
                        4 * calcYSpeed * (Math.random() * 0.5 + 0.5);
            }
            j++;
        }
        blades.clear();
    }

    public void makeSnakeExplode() {
        snakeExplodeTimestamp = System.nanoTime();
        isExploding = true;
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
                        2.5 + (4 * 1 / snake.speed()) + (Math.random() * 0.4 - 0.2);
                snakeParticles.setVelocity(id, angle, speed);
            }
            j++;
        }
        Audio.DEATH_SOUND.play();
    }

    public boolean isGameOver() {
        return isGameOver;
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

    public boolean bladesAreExploding() {
        return bladesExploding;
    }

    public ParticleManager getBladeParticles() {
        return bladeParticles;
    }

    public int getHealth() {
        return health;
    }

    private void takeDamage() {
        if (snake.isInvulnerable()) {
            return;
        }
        // TODO: play sound for shield
        if (shields > 0) {
            shields--;
        } else {
            health -= 4;
            snake.resetLength(Snake.INITIAL_SIZE);
        }
        if (health < 0) {
            makeSnakeExplode();
        } else {
            snake.setInvulnerable(true);
            snakeInvulnerableTimestamp = frames;
        }
    }

    private void disableAllPowerUps() {
        shields = 0;
        isMagnetized = false;
    }
}
