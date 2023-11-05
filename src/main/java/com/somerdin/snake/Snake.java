package com.somerdin.snake;

import com.somerdin.snake.Point.PointInt;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.stream.Collectors;

public class Snake implements Mover {
    public static final int FRAMES_TO_MOVE = 8;
    public static final int FRAMES_TO_MOVE_BOOSTED = 3;
    public static final int INITIAL_SIZE  = 4;

    private static final int MAX_COOLDOWN = 30;
    private static final int COOLDOWN_DECREMENT = 2;

    private Deque<SnakeCell> cells;
    private int speed;
    private int boostGauge = 100;
    private int boostCooldown = 0;
    private boolean invulnerable = false;

    public Snake(SnakeCell head) {
        this.speed = FRAMES_TO_MOVE;

        cells = new ArrayDeque<>();

        SnakeCell next = head;
        Direction makeDir = head.getDir().opposite();
        for (int i = 0; i < INITIAL_SIZE; i++) {
            cells.addLast(next);
            next = new SnakeCell(head.getDir(), next.getPos().go(makeDir),
                    false);
        }
    }

    public int speed() {
        return speed;
    }

    public void speedUp() {
        speed = Snake.FRAMES_TO_MOVE_BOOSTED;
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

    public boolean headOnBody() {
        Iterator<SnakeCell> iter = cells.iterator();
        iter.next();
        while (iter.hasNext()) {
            PointInt p= iter.next().getPos();
            if (p.equals(getHead().getPos())) {
                return true;
            }
        }
        return false;
     }

    public boolean containsPoint(PointInt p) {
        return cells.stream().anyMatch(sc -> sc.getPos().equals(p));
    }

    public Iterable<SnakeCell> getBody() {
        return cells;
    }

    public Iterable<PointInt> getPoints() {
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
         if (speed == FRAMES_TO_MOVE) {
            if (boostCooldown > 0) {
                boostCooldown--;
            } else if (boostGauge <= 98){
                boostGauge += COOLDOWN_DECREMENT;
            }
        } else if (boostGauge == 0 && boostCooldown == 0) {
            speed = FRAMES_TO_MOVE;
            boostCooldown = MAX_COOLDOWN;
        } else if (boostGauge >= COOLDOWN_DECREMENT
                && speed == FRAMES_TO_MOVE_BOOSTED) {
            boostGauge -= COOLDOWN_DECREMENT;
        }
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
    }

    public int getBoostGauge() {
        return boostGauge;
    }

    public int getSpeed() {
        return speed;
    }

    public void resetCooldown() {
        boostCooldown = MAX_COOLDOWN;
    }

    public boolean hasBoost() {
        return boostGauge >= COOLDOWN_DECREMENT;
    }

    public boolean hasCooldown() {
        return boostCooldown > 0;
    }

    public boolean isInvulnerable() {
        return invulnerable;
    }
}
