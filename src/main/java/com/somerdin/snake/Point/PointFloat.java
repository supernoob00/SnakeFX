package com.somerdin.snake.Point;

public record PointFloat(float x, float y) {
    public static PointFloat interpolate(PointFloat p1, PointFloat p2,
                                         float f) {
        float newX = p1.x + f * (p2.x - p1.x);
        float newY = p1.y + f * (p2.y - p1.y);
        return new PointFloat(newX, newY);
    }

    public PointFloat add(float x, float y) {
        return new PointFloat(this.x + x, this.y + y);
    }

    public PointFloat add(PointFloat p) {
        return add(p.x, p.y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
