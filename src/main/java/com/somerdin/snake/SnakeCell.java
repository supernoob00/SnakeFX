package com.somerdin.snake;

public class SnakeCell {
    private Direction dir;

    public SnakeCell(Direction dir) {
        this.dir = dir;
    }

    public Direction setDir(Direction newDir) {
        Direction old = dir;
        dir = newDir;
        return old;
    }

    public Direction getDir() {
        return dir;
    }
}
