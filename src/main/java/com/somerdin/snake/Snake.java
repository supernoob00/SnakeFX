package com.somerdin.snake;

import java.util.ArrayDeque;
import java.util.Deque;

public class Snake {
    // how many tiles the snake moves per second
    public static final int MOVE_SPEED = 2;

    private Deque<Point> cells;
    private Direction dir;
    private int length;

    public Snake(Point head, int length, Direction dir) {
        if (length <= 0) {
            throw new IllegalArgumentException();
        }

        this.dir = dir;
        this.length = length;

        cells = new ArrayDeque<>(length);

        Point next = head;
        for (int i = 0; i < length; i++) {
            cells.addFirst(next);
            next = next.add(dir.dx, dir.dy);
        }
    }

    public Point head() {
        return cells.getFirst();
    }

    public Point tail() {
        return cells.getLast();
    }

    public Direction getDir() {
        return dir;
    }

    public Iterable<Point> body() {
        return cells;
    }

    public void setDir(Direction newDir) {
        dir = newDir;
    }

    public void move() {
        cells.addFirst(head().add(dir.dx, dir.dy));
        cells.removeLast();
        System.out.println(head());
    }

    public void grow() {
        Direction opposite = dir.opposite();
        Point newTail = tail().add(opposite.dx, opposite.dy);
        cells.addLast(newTail);
        length++;
    }
}
