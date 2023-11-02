package com.somerdin.snake;

import com.somerdin.snake.Point.PointInt;

public interface Mover {
    int speed();

    boolean containsPoint(PointInt p);

    void setDirection(Direction newDir);

    void move();
}
