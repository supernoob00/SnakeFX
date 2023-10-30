package com.somerdin.snake;

import java.util.*;

public class GameState {
    public static final int WIDTH = 25;
    public static final int HEIGHT = 25;

    Point TOP_LEFT = new Point(0, 0);
    Point BOTTOM_LEFT = new Point(0, HEIGHT - 1);
    Point BOTTOM_RIGHT = new Point(HEIGHT - 1, HEIGHT - 1);
    Point TOP_RIGHT = new Point(HEIGHT - 1, 0);

    public final Food[][] food;
    public int crumbCount;
    public final int width;
    public final int height;

    private final Snake snake;
    private final Deque<SpinBlade> blades = new ArrayDeque<>();

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

        snake = new Snake(new SnakeCell(Direction.RIGHT, new Point(3, 3), false));
        for (int i = 5; i < 10; i++) {
            for (int j = 5; j < 10; j++) {
                placeFood(new Point(i, j), Food.CRUMB);
            }
        }
        // TODO: food should be at random location
        spawnFruit();
        spawnBlade();
    }

    public Snake getSnake() {
        return this.snake;
    }

    public Food getFood(Point p) {
        return food[(int) p.y()][(int) p.x()];
    }

    public void placeFood(Point p, Food food) {
        Food existing = this.food[p.y()][p.x()];
        if (existing == Food.CRUMB && food.isFruit()) {
            System.out.println("called1");
            crumbCount--;
        } else if (food == Food.CRUMB && existing != Food.CRUMB) {
            crumbCount++;
        }
        this.food[p.y()][p.x()] = food;
    }

    private void removeFood(Point p) {
        Food food = this.food[p.y()][p.x()];
        this.food[p.y()][p.x()] = null;
        if (food == Food.CRUMB) {
            System.out.println("called2");
            crumbCount--;
        }
        if (crumbCount == 0) {
            stage++;
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
            System.exit(0);
        }

        Food foodAtHead = getFood(snake.getHead().getPos());
        if (foodAtHead != null) {
            removeFood(snake.getHead().getPos());
            totalTime += foodAtHead.getTimeAdd();
            score += foodAtHead.getScore();
            System.out.println(foodAtHead.getTimeAdd());
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
        Point random = getRandomPoint();
        Food existing = getFood(random);
        while (snake.containsPoint(random)
                || existing != null
                && maxTriesLeft > 0) {
            random = getRandomPoint();
            existing = getFood(random);
            maxTriesLeft--;
        }
        Food foodToPlace;
        double randomDouble = Math.random();
        switch (stage) {
            case 1:
                foodToPlace = Food.RED_APPLE;
                break;
            case 2:
                if (randomDouble < 0.5) {
                    foodToPlace = Food.RED_APPLE;
                } else if (randomDouble < 0.8) {
                    foodToPlace = Food.GREEN_APPLE;
                } else {
                    foodToPlace = Food.RED_APPLE;
                }
                break;
            case 3:
                if (randomDouble < 0.6) {
                    foodToPlace = Food.GREEN_APPLE;
                } else if (randomDouble < 0.75) {
                    foodToPlace = Food.YELLOW_APPLE;
                } else if (randomDouble < 0.85) {
                    foodToPlace = Food.RED_APPLE;
                } else {
                    foodToPlace = Food.CHERRY;
                }
                break;
            case 4:
                if (randomDouble < 0.65) {
                    foodToPlace = Food.YELLOW_APPLE;
                } else if (randomDouble < 0.75) {
                    foodToPlace = Food.GREEN_APPLE;
                } else if (randomDouble < 0.85) {
                    foodToPlace = Food.RED_APPLE;
                } else {
                    foodToPlace = Food.CHERRY;
                }
                break;
            case 5:
                if (randomDouble < 0.7) {
                    foodToPlace = Food.YELLOW_APPLE;
                } else if (randomDouble < 0.75) {
                    foodToPlace = Food.GREEN_APPLE;
                } else if (randomDouble < 0.85) {
                    foodToPlace = Food.RED_APPLE;
                } else {
                    foodToPlace = Food.CHERRY;
                }
                break;
            default:
                if (randomDouble < 0.6) {
                    foodToPlace = Food.YELLOW_APPLE;
                } else if (randomDouble < 0.75) {
                    foodToPlace = Food.GREEN_APPLE;
                } else if (randomDouble < 0.85) {
                    foodToPlace = Food.RED_APPLE;
                } else {
                    foodToPlace = Food.CHERRY;
                }
        }
        placeFood(random, foodToPlace);
    }

    public void spawnBlade() {
        int rand1 = (int) (Math.random() * width);
        int rand2 = (int) (Math.random() * 4);
        double speed = SpinBlade.SLOW_BLADE_SPEED;
        double size = 0.75;

        PointDouble start;
        SpinBlade blade = null;
        switch (rand2) {
            case 0:
                start = new PointDouble(rand1, -1);
                blade = new SpinBlade(start, Direction.DOWN, speed, size);
                break;
            case 1:
                start = new PointDouble(rand1, width + 1);
                blade = new SpinBlade(start, Direction.UP, speed, size);
                break;
            case 2:
                start = new PointDouble(-1, rand1);
                blade = new SpinBlade(start, Direction.RIGHT, speed, size);
                break;
            case 3:
                start = new PointDouble(width + 1, rand1);
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
                System.exit(1);
            }
        }

        spawnBlades();
        System.out.println("Stage: " + stage);
        System.out.println("Crumbs: " + crumbCount);
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

    public long getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(long time) {
        this.timeRemaining = time;
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

    private void hilbertCrumbPattern() {
        MatrixUtil.hilbert(food, Food.CRUMB, 0, 0, 15);
    }
}
