package com.somerdin.snake.Point;

import com.somerdin.snake.Direction;

public record PointDouble(double x, double y) {

    public PointDouble add(double x, double y) {
        return new PointDouble(this.x + x, this.y + y);
    }

    public PointDouble add(PointDouble p) {
        return add(p.x, p.y);
    }

    public PointDouble go(Direction dir, double dist) {
        return new PointDouble(x + dir.dx * dist, y + dir.dy * dist);
    }

    public boolean inBounds(double width, double height) {
        return x >= 0 && x < width && y >= 0 && y < width;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
