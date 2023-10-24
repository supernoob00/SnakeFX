package com.somerdin.snake;

import com.somerdin.snake.Point.PointInt;
import javafx.scene.image.ImageView;

public class SnakeCell {
    private Direction dir;
    private PointInt position;
    // corner cells are animated differently
    private boolean isCorner;

    public SnakeCell(Direction dir, PointInt position, boolean isCorner) {
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

    public PointInt getPos() {
        return position;
    }

    public void setPos(PointInt newPos) {
        position = newPos;
    }

    public boolean isCorner() {
        return isCorner;
    }

    public void setCorner(boolean corner) {
        isCorner = corner;
    }

    public PointInt getNextPos() {
        return position.go(dir);
    }

    @Override
    public String toString() {
        return "Direction: " + dir + " | Corner: " + isCorner();
    }
}
