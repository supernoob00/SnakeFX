package com.somerdin.snake;

import com.somerdin.snake.Point.PointInt;

import java.util.Arrays;

public class GameState {
    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;

    PointInt TOP_LEFT = new PointInt(0, 0);
    PointInt BOTTOM_LEFT = new PointInt(0, HEIGHT - 1);
    PointInt BOTTOM_RIGHT = new PointInt(HEIGHT - 1, HEIGHT - 1);
    PointInt TOP_RIGHT = new PointInt(HEIGHT - 1, 0);

    public final Food[][] food;
    public final int width;
    public final int height;

    private final Snake snake;

    // TODO: decide what to do about out of bounds errors
    public GameState(int height, int width) {
        this.food = new Food[height][width];
        this.width = width;
        this.height = height;

        snake = new Snake(new SnakeCell(Direction.RIGHT, new PointInt(3, 3), false), 3);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int rand = (int) (Food.CRUMBS.length * Math.random());
                food[i][j] = Food.CRUMBS[rand];
            }
        }
        food[5][5] = Food.APPLE;
    }

    public Snake getSnake() {
        return this.snake;
    }

    public Food getFood(PointInt p) {
        return food[p.y()][p.x()];
    }

    public void placeFood(PointInt p, Food food) {
        this.food[p.y()][p.x()] = food;
    }

    private void removeFood(PointInt p) {
        this.food[p.y()][p.x()] = null;
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
            spawnFood();
        } else if (food != null && food.isCrumbs()) {
            removeFood(snake.getHead().getPos());
        }
    }

    public void changeSnakeDir(Direction dir) {
        if (dir == snake.getHead().getDir().opposite()) {
            throw new IllegalArgumentException("Invalid direction.");
        }
        snake.setDirection(dir);
    }

    public void spawnFood() {
        // max amount of attempts to spawn food in a random location,
        // in case something has gone horribly awry
        int maxTriesLeft = 400;
        PointInt random = getRandomPoint();
        Food existing = getFood(random);
        while (existing != null
                && !existing.isCrumbs()
                && maxTriesLeft > 0) {
            random = getRandomPoint();
            existing = getFood(random);
            maxTriesLeft--;
        }
        placeFood(random, Food.APPLE);
    }

    public void update(long updateCount) {
        // TODO: unexpected behavior if game state update fps is not
        //  divisible by snake speed
        if (updateCount % snake.speed() == 0) {
            moveSnake();
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
}
