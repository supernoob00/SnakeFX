package com.somerdin.snake;

public interface Mover {
    int speed();

    boolean containsPoint(Point p);

    void setDirection(Direction newDir);

    void move();
}
