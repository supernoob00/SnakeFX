package com.somerdin.snake;

import com.somerdin.snake.Point.PointDouble;
import com.somerdin.snake.Point.PointInt;

import java.util.*;

public class GameState {
    public static final int WIDTH = 25;
    public static final int HEIGHT = 25;

    PointInt TOP_LEFT = new PointInt(0, 0);
    PointInt BOTTOM_LEFT = new PointInt(0, HEIGHT - 1);
    PointInt BOTTOM_RIGHT = new PointInt(HEIGHT - 1, HEIGHT - 1);
    PointInt TOP_RIGHT = new PointInt(HEIGHT - 1, 0);

    public final Food[][] food;
    public int crumbCount;
    public final int width;
    public final int height;

    private final Snake snake;
    // particle coordinates are actual coordinates on canvas
    private ParticleManager snakeParticles;
    private boolean isStarted;
    private boolean isExploding;
    private boolean isGameOver;
    private final Deque<SpinBlade> blades = new ArrayDeque<>();
    private final List<PointInt> crumbsToDraw = new ArrayList<>();
    private boolean allInitialCrumbsDrawn;
    private long snakeExplodeTimestamp;

    private Direction queuedDirection;
    private long totalTime = 30_000_000_000L;
    private long timeRemaining = 30_000_000_000L;
    private long score = 0;
    private int stage = 1;

    // TODO: decide what to do about out of bounds errors
    public GameState(int height, int width) {
        this.food = new Food[height][width];
        this.crumbCount = 0;
        this.width = width;
        this.height = height;

        snake = new Snake(new SnakeCell(Direction.RIGHT, new PointInt(14, 12),
                false));
        mazePattern();
        spawnBlade();
    }

    public Snake getSnake() {
        return this.snake;
    }

    public Food getFood(PointInt p) {
        return food[(int) p.y()][(int) p.x()];
    }

    public void placeFood(PointInt p, Food food) {
        Food existing = this.food[p.y()][p.x()];
        if (existing == Food.CRUMB && food.isFruit()) {
            crumbCount--;
        } else if (food == Food.CRUMB && existing != Food.CRUMB) {
            crumbCount++;
        }
        this.food[p.y()][p.x()] = food;
    }

    private void removeFood(PointInt p) {
        Food food = this.food[p.y()][p.x()];
        this.food[p.y()][p.x()] = null;
        if (food == Food.CRUMB) {
            crumbCount--;
            if (crumbCount == 0) {
                stage++;
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
            makeSnakeExplode();
        }

        Food foodAtHead = getFood(snake.getHead().getPos());
        if (foodAtHead != null) {
            removeFood(snake.getHead().getPos());
            totalTime += foodAtHead.getTimeAdd();
            score += foodAtHead.getScore();
            System.out.println(score);
            if (foodAtHead.isFruit()) {
                spawnFruit();
                snake.grow();
            }
        }
    }

    public void spawnFruit() {
        // max amount of attempts to spawn food in a random location,
        // in case something has gone horribly awry
        int maxTriesLeft = 1000;
        PointInt random = getRandomPoint();
        Food existing = getFood(random);
        while (snake.containsPoint(random)
                || existing != null
                && maxTriesLeft > 0) {
            random = getRandomPoint();
            existing = getFood(random);
            maxTriesLeft--;
        }
        Food foodToPlace;
        double typeOfFruitProbability = Math.random();
        switch (stage) {
            case 1:
                foodToPlace = Food.RED_APPLE;
                break;
            case 2:
                if (typeOfFruitProbability < 0.5) {
                    foodToPlace = Food.RED_APPLE;
                } else if (typeOfFruitProbability < 0.8) {
                    foodToPlace = Food.GREEN_APPLE;
                } else {
                    foodToPlace = Food.RED_APPLE;
                }
                break;
            case 3:
                if (typeOfFruitProbability < 0.6) {
                    foodToPlace = Food.GREEN_APPLE;
                } else if (typeOfFruitProbability < 0.75) {
                    foodToPlace = Food.YELLOW_APPLE;
                } else if (typeOfFruitProbability < 0.85) {
                    foodToPlace = Food.RED_APPLE;
                } else {
                    foodToPlace = Food.CHERRY;
                }
                break;
            case 4:
                if (typeOfFruitProbability < 0.65) {
                    foodToPlace = Food.YELLOW_APPLE;
                } else if (typeOfFruitProbability < 0.75) {
                    foodToPlace = Food.GREEN_APPLE;
                } else if (typeOfFruitProbability < 0.85) {
                    foodToPlace = Food.RED_APPLE;
                } else {
                    foodToPlace = Food.CHERRY;
                }
                break;
            case 5:
                if (typeOfFruitProbability < 0.7) {
                    foodToPlace = Food.YELLOW_APPLE;
                } else if (typeOfFruitProbability < 0.75) {
                    foodToPlace = Food.GREEN_APPLE;
                } else if (typeOfFruitProbability < 0.85) {
                    foodToPlace = Food.RED_APPLE;
                } else {
                    foodToPlace = Food.CHERRY;
                }
                break;
            default:
                if (typeOfFruitProbability < 0.6) {
                    foodToPlace = Food.YELLOW_APPLE;
                } else if (typeOfFruitProbability < 0.75) {
                    foodToPlace = Food.GREEN_APPLE;
                } else if (typeOfFruitProbability < 0.85) {
                    foodToPlace = Food.RED_APPLE;
                } else {
                    foodToPlace = Food.CHERRY;
                }
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
                start = new PointDouble(-1, spawnPlacement);
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
        if (snakeIsExploding()) {
            if (snakeParticles.isMoving()) {
                snakeParticles.updatePos(1);
                System.out.println("moving");
            }
            if (System.nanoTime() - snakeExplodeTimestamp > 3_000_000_000L) {
                isGameOver = true;
            }
        } else {
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
                if (updateCount % BladePath.PATH_DRAW_SPEED == 0) {
                    sb.getBladePath().addDrawn();
                }
                sb.move();
                PointDouble p = sb.getPos();
                // remove from deque if spin-blade is out of bounds
                if (sb.getBladePath().size() == 0) {
                    it.remove();
                    spawnBlade();
                    System.out.println("Removed!");
                }
            }

            // TODO: consolidate into collisions method
            for (SpinBlade sb : blades) {
                if (sb.containsAnyPoint(snake.getPoints())) {
                    makeSnakeExplode();
                }
            }
            spawnBlades();
            if (crumbsToDraw.size() > 0) {
                for (int i = 0; i < 6; i++) {
                    PointInt p = crumbsToDraw.remove(crumbsToDraw.size() - 1);
                    placeFood(p, Food.CRUMB);
                }
            } else if (!allInitialCrumbsDrawn){
                spawnFruit();
                allInitialCrumbsDrawn = true;
            }
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
        return switch (stage) {
            case 1 -> 1;
            case 2 -> 1.2;
            case 3 -> 1.5;
            case 4 -> 2;
            case 5 -> 3;
            default -> 5;
        };
    }

    public ParticleManager getSnakeParticles() {
        return snakeParticles;
    }

    public boolean snakeIsExploding() {
        return isExploding;
    }

    public void makeSnakeExplode() {
        snakeExplodeTimestamp = System.nanoTime();
        isExploding = true;
        int pixelsPerTile =
                PixelTile.TILE_WIDTH_PIXELS * PixelTile.TILE_WIDTH_PIXELS;
        int pixelCount =
                pixelsPerTile * snake.getLength();

        snakeParticles = new ParticleManager(pixelCount,
                GameLoop.PLAYABLE_AREA_WIDTH, GameLoop.PLAYABLE_AREA_HEIGHT,
                0.01);

        int j = 0;
        for (SnakeCell sc : snake.getBody()) {
            PointInt p = sc.getPos();

            for (int i = 0; i < pixelsPerTile; i++) {
                int id = j * pixelsPerTile + i;
                snakeParticles.xPos[id] =
                        p.x() * PixelTile.TILE_WIDTH_ACTUAL + (i % PixelTile.TILE_WIDTH_PIXELS) * PixelTile.PIXEL_WIDTH;
                snakeParticles.yPos[id] =
                        p.y() * PixelTile.TILE_WIDTH_ACTUAL + (i / PixelTile.TILE_WIDTH_PIXELS) * PixelTile.PIXEL_WIDTH;

                double randomAngleRange = 70;
                double angle =
                        (sc.getDir().getAngle() - randomAngleRange / 2)
                                + (int) (Math.random() * randomAngleRange);
                double speed = 1.8 + 0.8 * Math.random() * snake.speed() * 0.3;
                snakeParticles.setVelocity(id, angle, speed);
            }
            j++;
        }
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

    private void mazePattern() {
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
        System.out.println(crumbsToDraw.size());
    }

    // FOR TESTING
    public void eatAllCrumbs() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                removeFood(new PointInt(i, j));
            }
        }
    }
}
