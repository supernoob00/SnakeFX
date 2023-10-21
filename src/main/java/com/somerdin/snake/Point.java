package com.somerdin.snake;

public record Point(int x, int y) {

    public Point add(int x, int y) {
        return new Point(this.x + x, this.y + y);
    }

    public Point add(Point p) {
        return add(p.x, p.y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
