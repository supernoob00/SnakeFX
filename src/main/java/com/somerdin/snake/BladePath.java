package com.somerdin.snake;

import com.somerdin.snake.Point.PointInt;

import java.util.ArrayDeque;
import java.util.Deque;

// TODO: make spawn points away from edges
public class BladePath {
    public static final int PATH_DRAW_SPEED = 5;

    private PointInt start;
    private Deque<Direction> path = new ArrayDeque<>();
    private int drawn;
    private int colorId;
    private int initialSize = 0;

    public BladePath(Direction initialDirection, PointInt start) {
        for (int i = 0; i < 5; i++) {
            path.addLast(initialDirection);
        }
        this.start = start;

        int turnsLeft = 3;
        int movesSinceLastTurn = 5;
        PointInt current = start.goFollowPath(path);

        while (current.inBounds(GameState.WIDTH, GameState.HEIGHT)) {
            Direction lastDir = path.getLast();

            Direction[] randDirs;
            // path continues straight if there was a turn too recently,
            // or if RNG says so
            if (movesSinceLastTurn < 4 || turnsLeft == 0) {
                path.addLast(lastDir);
                movesSinceLastTurn++;
            } else if ((randDirs = possibleRandomDirs(initialDirection,
                    lastDir)).length == 3) {
                // probabilities: 0.9 straight, 0.05 left, 0.05 right
                double rand = Math.random();
                if (rand < 0.9) {
                    path.addLast(randDirs[0]);
                    movesSinceLastTurn++;
                } else if (rand < 0.95) {
                    path.addLast(randDirs[1]);
                    movesSinceLastTurn = 0;
                    turnsLeft--;
                } else {
                    path.addLast(randDirs[2]);
                    movesSinceLastTurn = 0;
                    turnsLeft--;
                }
            } else {
                // probabilities: 0.9 straight, 0.1 other valid direction
                double rand = Math.random();
                if (rand < 0.9) {
                    path.addLast(randDirs[0]);
                    movesSinceLastTurn++;
                } else {
                    path.addLast(randDirs[1]);
                    movesSinceLastTurn = 0;
                    turnsLeft--;
                }
            }
            current = current.go(path.getLast());
            initialSize++;
        }
        colorId = (int) (Math.random() * 6);
    }

    public PointInt getStart() {
        return start;
    }

    public Iterable<Direction> getPath() {
        return path;
    }

    public Direction removeFirst() {
        Direction first = path.removeFirst();
        start = start.go(first);
        return first;
    }

    public Direction getFirst() {
        return path.getFirst();
    }

    public int size() {
        return path.size();
    }

    private Direction[] possibleRandomDirs(Direction start, Direction current) {
        if (start == current) {
            Direction[] orthogonal = start.orthogonal();
            return new Direction[] {start, orthogonal[0], orthogonal[1]};
        } else {
            return new Direction[] {start, current};
        }
    }

    public int getDrawn() {
        return drawn;
    }

    public int addDrawn() {
        drawn++;
        return drawn;
    }

    public int getColorId() {
        return colorId;
    }

    public int getInitialSize() {
        return initialSize;
    }
}
