package com.somerdin.snake;

import com.somerdin.snake.Point.PointDouble;
import com.somerdin.snake.Point.PointInt;
import com.somerdin.snake.Resource.Sprite;

import java.util.Objects;

public class SpinBlade {
    public static final double SLOW_BLADE_SPEED = 0.05;

    private ParticleManager particles;
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
        double epsilon = 0.01;
        Direction next;
        if (Math.abs(distTraveled - 1) < epsilon) {
            next = path.removeFirst();
            distTraveled = 0;
        } else {
            next = path.getFirst();
        }
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

    public void speedUp() {
        speed = SLOW_BLADE_SPEED * 3;
    }

    public void makeExplode() {
        System.out.println("PARTICLES SET");
        int pixelsPerTile = Sprite.TILE_WIDTH_PIXELS * Sprite.TILE_WIDTH_PIXELS;
        particles = new ParticleManager(pixelsPerTile,
                GameLoop.PLAYABLE_AREA_WIDTH, GameLoop.PLAYABLE_AREA_HEIGHT,
                0.05);

            for (int id = 0; id < pixelsPerTile; id++) {
                int xUnits = id % Sprite.TILE_WIDTH_PIXELS;
                int yUnits = id / Sprite.TILE_WIDTH_PIXELS;
                double x = position.x() * Sprite.TILE_WIDTH_ACTUAL + xUnits * Sprite.PIXEL_WIDTH;
                double y = position.y() * Sprite.TILE_WIDTH_ACTUAL + yUnits * Sprite.PIXEL_WIDTH;
                double dist = Math.sqrt(Math.pow(xUnits - 4, 2) + Math.pow(yUnits - 4, 2));
                particles.xPos[id] = x;
                particles.yPos[id] = y;

                double angle =
                        Math.toDegrees(Math.atan((double) yUnits - 4 / (xUnits - 4D)));
                double factor = dist / Math.sqrt(32);
                double calcXSpeed = (yUnits - 4);
                double calcYSpeed = (-xUnits + 4);

                if (calcXSpeed < 0) {
                    calcXSpeed = 0.5 * Math.max(calcXSpeed,
                            -4);
                } else {
                    calcXSpeed = 0.5 * Math.min(calcXSpeed,
                            4);
                }
                if (calcYSpeed < 0) {
                    calcYSpeed = 0.5 * Math.max(calcYSpeed,
                            -4);
                } else {
                    calcYSpeed = 0.5 * Math.min(calcYSpeed,
                            4);
                }
                particles.xSpeed[id] =
                        4 * calcXSpeed * (Math.random() * 0.5 + 0.5);
                particles.ySpeed[id] =
                        4 * calcYSpeed * (Math.random() * 0.5 + 0.5);
            }
    }

    public boolean isExploding() {
        return particles != null;
    }

    public ParticleManager getParticles() {
        return Objects.requireNonNull(particles);
    }
}
