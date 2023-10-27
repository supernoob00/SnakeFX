package com.somerdin.snake;

public class SnakeCell {
    private Direction dir;
    private Point position;
    // corner cells are animated differently
    private boolean isCorner;

    public SnakeCell(Direction dir, Point position, boolean isCorner) {
        this.dir = dir;
        this.position = position;
    }

    public Direction setDir(Direction newDir) {
        Direction old = dir;
        dir = newDir;
        return old;
    }

    public Direction getDir() {
        return dir;
    }

    public Point getPos() {
        return position;
    }

    public void setPos(Point newPos) {
        position = newPos;
    }

    public boolean isCorner() {
        return isCorner;
    }

    public void setCorner(boolean corner) {
        isCorner = corner;
    }

    public Point getNextPos() {
        return position.go(dir);
    }

    @Override
    public String toString() {
        return "Direction: " + dir + " | Corner: " + isCorner();
    }
}
