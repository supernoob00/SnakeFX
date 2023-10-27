package com.somerdin.snake;

import java.util.Objects;
import java.util.*;

public record Point(int x, int y) {
    public static double interX(Point p1, Point p2, double d) {
        if (d < 0 || d > 1) {
            throw new IllegalArgumentException();
        }
        return (double) p1.x + (d * (p2.x - p1.x));
    }

    public static double interY(Point p1, Point p2, double d) {
        if (d < 0 || d > 1) {
            throw new IllegalArgumentException();
        }
        return (double) p1.y + (d * (p2.y - p1.y));
    }

    public Point add(int x, int y) {
        return new Point(this.x + x, this.y + y);
    }

    public Point add(Point p) {
        return add(p.x, p.y);
    }

    public Point go(Direction direction) {
        return add(direction.dx, direction.dy);
    }

    public Point goFollowPath(Collection<Direction> path) {
        Point p = this;
        for (Direction d : path) {
            p = p.go(d);
        }
        return p;
    }

    public boolean inBounds(int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < width;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
