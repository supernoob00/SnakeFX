package com.somerdin.snake;

public class Game {
    public static final int WIDTH = 40;
    public static final int HEIGHT = 40;

    Point TOP_LEFT = new Point(0, 0);
    Point BOTTOM_LEFT = new Point(0, HEIGHT - 1);
    Point BOTTOM_RIGHT = new Point(HEIGHT - 1, HEIGHT - 1);
    Point TOP_RIGHT = new Point(HEIGHT - 1, 0);

    public final Food[][] food;
    public final int width;
    public final int height;

    public final Snake snake;

    // TODO: decide what to do about out of bounds errors
    public Game(int height, int width) {
        this.food = new Food[height][width];
        this.width = width;
        this.height = height;

        snake = new Snake(new Point(3, 3), 3, Direction.RIGHT);
        food[5][5] = new Food();
    }

    public Food getFood(Point p) {
        return food[p.y()][p.x()];
    }

    public void placeFood(Point p, Food food) {
        this.food[p.y()][p.x()] = food;
    }

    private void removeFood(Point p) {
        this.food[p.y()][p.x()] = null;
    }

    public void moveSnake() {
        int headX = snake.head().x();
        int headY = snake.head().y();

        if (headX == 0 && snake.getDir() == Direction.LEFT) {
            if (headY == 0) {
                snake.setDir(Direction.DOWN);
            } else if (headY == HEIGHT - 1) {
                snake.setDir(Direction.UP);
            } else {
                Direction newDir = getRandomDir(Direction.DOWN, Direction.UP);
                snake.setDir(newDir);
            }
        } else if (headY == 0 && snake.getDir() == Direction.UP) {
            if (headX == 0) {
                snake.setDir(Direction.RIGHT);
            } else if (headX == WIDTH - 1) {
                snake.setDir(Direction.LEFT);
            } else {
                Direction newDir = getRandomDir(Direction.LEFT,
                        Direction.RIGHT);
                snake.setDir(newDir);
            }
        } else if (headX == WIDTH - 1 && snake.getDir() == Direction.RIGHT) {
            if (headY == 0) {
                snake.setDir(Direction.DOWN);
            } else if (headY == HEIGHT - 1) {
                snake.setDir(Direction.UP);
            } else {
                Direction newDir = getRandomDir(Direction.DOWN, Direction.UP);
                snake.setDir(newDir);
            }
        } else if (headY == HEIGHT - 1 && snake.getDir() == Direction.DOWN) {
            if (headX == 0) {
                snake.setDir(Direction.RIGHT);
            } else if (headX == WIDTH - 1) {
                snake.setDir(Direction.LEFT);
            } else {
                Direction newDir = getRandomDir(Direction.LEFT,
                        Direction.RIGHT);
                snake.setDir(newDir);
            }
        }
        snake.move();

        if (getFood(snake.head()) != null) {
            snake.grow();
            removeFood(snake.head());
        }
    }

    public void changeSnakeDir(Direction dir) {
        if (dir == snake.getDir().opposite()) {
            throw new IllegalArgumentException("Invalid direction.");
        }
        snake.setDir(dir);
    }

    public void spawnFood() {
        // max amount of attempts to spawn food in a random location,
        // in case something has gone horribly awry
        int maxTriesLeft = 50;
        Point randomPoint;
        while (getFood(randomPoint = getRandomPoint()) != null
                && maxTriesLeft > 0) {
            maxTriesLeft--;
        }
        placeFood(randomPoint, new Food());
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
}
