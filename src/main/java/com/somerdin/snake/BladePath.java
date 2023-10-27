package com.somerdin.snake;

import java.util.ArrayDeque;
import java.util.Deque;

public class BladePath {
    private Point start;
    private Deque<Direction> path = new ArrayDeque<>();

    public BladePath(Direction initialDirection, Point start) {
        for (int i = 0; i < 5; i++) {
            path.addLast(initialDirection);
        }
        this.start = start;

        int turnCount = 3;
        int movesSinceLastTurn = 0;
        Point current = start;

        while (current.inBounds(GameState.WIDTH, GameState.HEIGHT) || path.size() < 6) {
            System.out.println(current);
            Direction last = path.getLast();
            // TODO: refactor this crap
            if (turnCount > 0
                    && Math.random() < 0.1
                    && movesSinceLastTurn > 4) {
                double rand = Math.random();
                if (rand < 0.5) {
                    if (last == initialDirection) {
                        path.addLast(last.orthogonal()[0]);
                        turnCount--;
                    } else {
                        path.addLast(initialDirection);
                    }
                } else {
                    if (last == initialDirection) {
                        path.addLast(last.orthogonal()[1]);
                        turnCount--;
                    } else {
                        path.addLast(initialDirection);
                    }
                }
                movesSinceLastTurn = 0;
            } else {
                path.addLast(path.getLast());
            }
            current = current.go(path.getLast());
            movesSinceLastTurn++;
        }
        System.out.println("Created size: " + path.size());
    }

    public Point getStart() {
        return start;
    }

    public Deque<Direction> getPath() {
        return path;
    }
}
