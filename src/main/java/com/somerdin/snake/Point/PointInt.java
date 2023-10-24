package com.somerdin.snake.Point;

import com.somerdin.snake.Direction;

import java.util.Objects;

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
