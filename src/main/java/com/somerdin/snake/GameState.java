package com.somerdin.snake;

import java.util.*;

public class GameState {
    public static final int WIDTH = 24;
    public static final int HEIGHT = 24;

    Point TOP_LEFT = new Point(0, 0);
    Point BOTTOM_LEFT = new Point(0, HEIGHT - 1);
    Point BOTTOM_RIGHT = new Point(HEIGHT - 1, HEIGHT - 1);
    Point TOP_RIGHT = new Point(HEIGHT - 1, 0);

    public final Food[][] food;
    public final int width;
    public final int height;

    private final Snake snake;
    private final Deque<SpinBlade> blades = new ArrayDeque<>();

    private Direction queuedDirection;
    private long totalTime = 30_000_000_000L;
    private long timeRemaining = 30_000_000_000L;
    private long score = 0;

    // TODO: decide what to do about out of bounds errors
    public GameState(int height, int width) {
        this.food = new Food[height][width];
        this.width = width;
        this.height = height;

        snake = new Snake(new SnakeCell(Direction.RIGHT, new Point(3, 3), false), 3);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                food[i][j] = Food.CRUMB;
            }
        }
        // TODO: food should be at random location
        food[5][5] = Food.RED_APPLE;
        blades.add(new SpinBlade(new PointDouble(5, -1), Direction.DOWN,
                SpinBlade.SLOW_BLADE_SPEED, 0.75));
    }

    public Snake getSnake() {
        return this.snake;
    }

    public Food getFood(Point p) {
        return food[(int) p.y()][(int) p.x()];
    }

    public void placeFood(Point p, Food food) {
        this.food[(int) p.y()][(int) p.x()] = food;
    }

    private void removeFood(Point p) {
        this.food[(int) p.y()][p.x()] = null;
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
            System.exit(1);
        }

        Food food = getFood(snake.getHead().getPos());
        if (food != null && food.isFruit()) {
            snake.grow();
            removeFood(snake.getHead().getPos());
            totalTime += food.getTimeAdd();
            score += food.getScore();
            System.out.println(food.getTimeAdd());
            System.out.println(score);
            spawnFood();
        } else if (food == Food.CRUMB) {
            removeFood(snake.getHead().getPos());
        }
    }

    public void spawnFood() {
        // max amount of attempts to spawn food in a random location,
        // in case something has gone horribly awry
        int maxTriesLeft = 400;
        Point random = getRandomPoint();
        Food existing = getFood(random);
        while (snake.containsPoint(random)
                || (existing != null && existing.isFruit())
                && maxTriesLeft > 0) {
            random = getRandomPoint();
            existing = getFood(random);
            maxTriesLeft--;
        }
        placeFood(random, Food.RED_APPLE);
    }

    public Iterable<SpinBlade> getBlades() {
        return blades;
    }

    public void update(long updateCount) {
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
            sb.move();
            PointDouble p = sb.getPos();
            // remove from deque if spin-blade is out of bounds
            if (p.x() < -1 || p.x() > WIDTH + 1
                    || p.y() < -1 || p.y() > WIDTH + 1) {
                it.remove();
                System.out.println("Removed!");
            }
        }

        // TODO: consolidate into collisions method
        for (SpinBlade sb : blades) {
            if (sb.containsAnyPoint(snake.getPoints())) {
                System.exit(1);
            }
        }
    }

    private Direction getRandomDir(Direction... options) {
        int rand = (int) (options.length * Math.random());
        return options[rand];
    }

    private Point getRandomPoint() {
        int randomX = (int) (width * Math.random());
        int randomY = (int) (height * Math.random());
        return new Point(randomX, randomY);
    }

    public Direction getQueuedDirection() {
        return queuedDirection;
    }

    public void setQueuedDirection(Direction direction) {
        this.queuedDirection = direction;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTimeRemaining(long time) {
        this.timeRemaining = time;
    }
}
