package com.somerdin.snake;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Snake implements Mover {
    // how many tiles the snake moves per second
    public static final int FRAMES_TO_MOVE = 5;
    public static final int BOOSTED_FRAMES_TO_MOVE = 3;

    private Deque<SnakeCell> cells;
    private int length;
    private int speed;

    public Snake(SnakeCell head, int length) {
        if (length <= 0) {
            throw new IllegalArgumentException();
        }

        this.length = length;
        this.speed = FRAMES_TO_MOVE;

        cells = new ArrayDeque<>(length);

        SnakeCell next = head;
        Direction makeDir = head.getDir().opposite();
        for (int i = 0; i < length; i++) {
            cells.addLast(next);
            next = new SnakeCell(head.getDir(), next.getPos().go(makeDir),
                    false);
        }
    }

    public int speed() {
        return speed;
    }

    public void speedUp() {
        speed = Snake.BOOSTED_FRAMES_TO_MOVE;
    }

    public void slowDown() {
        speed = Snake.FRAMES_TO_MOVE;
    }

    public SnakeCell getHead() {
        return cells.getFirst();
    }

    public SnakeCell getTail() {
        return cells.getLast();
    }

    public int length() {
        return length;
    }

    public boolean headOnBody() {
        Iterator<SnakeCell> iter = cells.iterator();
        iter.next();
        while (iter.hasNext()) {
            Point p= iter.next().getPos();
            if (p.equals(getHead().getPos())) {
                return true;
            }
        }
        return false;
     }

    public boolean containsPoint(Point p) {
        return cells.stream().anyMatch(sc -> sc.getPos().equals(p));
    }

    public SnakeCell[] getBody() {
        return cells.toArray(new SnakeCell[0]);
    }

    public List<Point> getPoints() {
        return cells.stream()
                .map(SnakeCell::getPos)
                .collect(Collectors.toList());
    }

    public int getLength() {
        return cells.size();
    }

    public void setDirection(Direction newDir) {
        SnakeCell head = getHead();
        head.setDir(newDir);
        cells.getFirst().setCorner(true);
    }

    public void move() {
        cells.addFirst(new SnakeCell(getHead().getDir(), getHead().getNextPos(), false));
        cells.removeLast();
        getTail().setCorner(false);
    }

    public void grow() {
        Direction tailDir = getTail().getDir();
        SnakeCell newTail = new SnakeCell(
                tailDir,
                getTail().getPos().go(tailDir.opposite()),
                false);
        cells.addLast(newTail);
        length++;
    }
}
