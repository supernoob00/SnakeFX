package com.somerdin.snake;

import java.util.Collection;

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
        this.path = new BladePath(direction, new Point((int) position.x(),
                (int) position.y()));
    }

    public double speed() {
        return speed;
    }

    public boolean containsPointDouble(PointDouble p) {
        return (Math.abs(p.x() - position.x()) <= size)
                && (Math.abs(p.y() - position.y()) <= size);
    }

    public boolean containsPointInt(Point p) {
        return (Math.abs(p.x() - position.x()) <= size)
                && (Math.abs(p.y() - position.y()) <= size);
    }

    public boolean containsAnyPoint(Collection<Point> points) {
        for (Point p : points) {
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
        double epsilon = 0.01;
        Direction next;
        if (Math.abs(distTraveled % 1 - 1) < epsilon) {
            next = path.getPath().removeFirst();
            System.out.println("Size: " + path.getPath().size());
        } else {
            next = path.getPath().getFirst();
        }
        position = position.go(next, speed);
        distTraveled += speed;
    }


    public PointDouble getPos() {
        return position;
    }

    public PointDouble getNextPos() {
        return position.go(direction, speed);
    }
}
