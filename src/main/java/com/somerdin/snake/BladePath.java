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

        int turnsLeft = 3;
        int movesSinceLastTurn = 5;
        Point current = start.goFollowPath(path);
        System.out.println("Here:" + current);

        while (current.inBounds(GameState.WIDTH, GameState.HEIGHT)) {
            Direction lastDir = path.getLast();

            Direction[] randDirs;
            // path continues straight if there was a turn too recently,
            // or if RNG says so
            if (movesSinceLastTurn < 4 || turnsLeft == 0) {
                System.out.println(movesSinceLastTurn);
                System.out.println(turnsLeft);
                path.addLast(lastDir);
                movesSinceLastTurn++;
            } else if ((randDirs = possibleRandomDirs(initialDirection,
                    lastDir)).length == 3) {
                System.out.println(2);
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
                System.out.println(3);
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
            System.out.println(path.getLast());
            current = current.go(path.getLast());
        }
        System.out.println("Created size: " + path.size());
    }

    public Point getStart() {
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
}
