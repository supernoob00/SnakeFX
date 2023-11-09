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
    private static final int BOOST_GAUGE_USAGE = 2;
    private static final int BOOST_GAUGE_RECHARGE = 2;

    private Deque<SnakeCell> cells;
    private boolean isBoosting;
    private int speed;
    private int boostGauge = 100;
    private int boostCooldown = 0;
    private int largest = INITIAL_SIZE;

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

    private void speedUp() {
        if (speed > FRAMES_TO_MOVE_BOOSTED) {
            speed -= 5;
        }
    }

    private void slowDown() {
        if (speed < FRAMES_TO_MOVE) {
            speed += 5;
        }
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
        // conditions if snake is currently moving at normal speed
         if (!isBoosting) {
             slowDown();
            if (boostCooldown > 0) {
                boostCooldown--;
            } else if (boostGauge < 100){
                // set boost gauge to 100 directly to avoid overflow
                if (boostGauge > 100 - BOOST_GAUGE_RECHARGE) {
                    boostGauge = 100;
                } else {
                    boostGauge += BOOST_GAUGE_RECHARGE;
                }
            }
        }
         // conditions if snake is currently boosted
         else {
             // snake just ran out of boost juice
             if (boostGauge == 0) {
                 if (boostGauge < 25) {
                     resetCooldown();
                 }
                 slowDown();
             }
             // snake still has boost juice
             else {
                 speedUp();
                 boostGauge -= BOOST_GAUGE_USAGE;
             }
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
        if (getLength() > largest) {
            largest = getLength();
        }
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
        return boostGauge >= BOOST_GAUGE_RECHARGE;
    }

    public boolean hasCooldown() {
        return boostCooldown > 0;
    }

    public boolean isBoosting() {
        return isBoosting;
    }

    public void setBoosting(boolean boost) {
        isBoosting = boost;
    }

    public boolean canBoost() {
        return boostGauge >= BOOST_GAUGE_USAGE && boostCooldown == 0;
    }

    public void resetLength(int length) {
        while (cells.size() > length) {
            cells.removeLast();
        }
    }

    public int getLargest() {
        return largest;
    }
}
