package com.somerdin.snake;

public enum Direction {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    public final int dx;
    public final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public Direction opposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }

    public Direction[] orthogonal() {
        return switch (this) {
            case UP, DOWN -> new Direction[]{LEFT, RIGHT};
            case LEFT, RIGHT -> new Direction[]{UP, DOWN};
        };
    }

    public double getAngle() {
        return switch (this) {
            case UP -> -90;
            case DOWN -> 90;
            case LEFT -> 180;
            case RIGHT -> 0;
        };
    }
}
