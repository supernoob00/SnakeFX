package com.somerdin.snake;

import com.somerdin.snake.Point.PointDouble;
import com.somerdin.snake.Point.PointInt;

public class SpinBlade {
    public static final double SLOW_BLADE_SPEED = 0.05;

    private PointDouble position;
    private Direction direction;
    private BladePath path;
    private double distTraveled;
    private double speed;
    private double size;

    public SpinBlade(PointDouble position, Direction direction, double speed,
                     double size) {
        this.position = position;
        this.direction = direction;
        this.speed = speed;
        this.size = size;
        this.distTraveled = 0;
        this.path = new BladePath(direction, new PointInt((int) position.x(),
                (int) position.y()));
    }

    public double speed() {
        return speed;
    }

    public boolean containsPointDouble(PointDouble p) {
        return (Math.abs(p.x() - position.x()) <= size)
                && (Math.abs(p.y() - position.y()) <= size);
    }

    public boolean containsPointInt(PointInt p) {
        return (Math.abs(p.x() - position.x()) <= size)
                && (Math.abs(p.y() - position.y()) <= size);
    }

    public boolean containsAnyPoint(Iterable<PointInt> points) {
        for (PointInt p : points) {
            if (containsPointInt(p)) {
                return true;
            }
        }
        return false;
    }

    public void setDirection(Direction newDir) {
        direction = newDir;
    }

    public void move() {
        if (!isMoving()) {
            return;
        }
        double epsilon = 0.01;
        Direction next;
        if (Math.abs(distTraveled - 1) < epsilon) {
            next = path.removeFirst();
            distTraveled = 0;
        } else {
            next = path.getFirst();
        }
        System.out.println(path.size());
        position = position.go(next, speed);
        distTraveled += speed;
    }

    public boolean isMoving() {
        return path.getDrawn() >= path.size();
    }


    public PointDouble getPos() {
        return position;
    }

    public PointDouble getNextPos() {
        return position.go(direction, speed);
    }

    public BladePath getBladePath() {
        return this.path;
    }
}
