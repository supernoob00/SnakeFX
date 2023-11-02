package com.somerdin.snake.Point;

import com.somerdin.snake.Direction;

import java.util.Objects;
import java.util.*;

public record PointInt(int x, int y) {
    public static double interX(PointInt p1, PointInt p2, double d) {
        if (d < 0 || d > 1) {
            throw new IllegalArgumentException();
        }
        return (double) p1.x + (d * (p2.x - p1.x));
    }

    public static double interY(PointInt p1, PointInt p2, double d) {
        if (d < 0 || d > 1) {
            throw new IllegalArgumentException();
        }
        return (double) p1.y + (d * (p2.y - p1.y));
    }

    public PointInt add(int x, int y) {
        return new PointInt(this.x + x, this.y + y);
    }

    public PointInt add(PointInt p) {
        return add(p.x, p.y);
    }

    public PointInt go(Direction direction) {
        return add(direction.dx, direction.dy);
    }

    public PointInt goFollowPath(Collection<Direction> path) {
        PointInt p = this;
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
        PointInt pointInt = (PointInt) o;
        return x == pointInt.x && y == pointInt.y;
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
