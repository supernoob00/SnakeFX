package com.somerdin.snake;

public class Item {
    private Food food;
    private long createTimestamp;

    public Item(Food food, long createTimestamp) {
        this.food = food;
        this.createTimestamp = createTimestamp;
    }

    public Food getFood() {
        return food;
    }

    public boolean isFruit() {
        return food.isFruit();
    }

    public boolean isPowerUp() {
        return food.isPowerUp();
    }

    public boolean isCrumb() {
        return food.isCrumb();
    }

    public int getScore() {
        return food.getScore();
    }

    public int getHealthValue() {
        return food.getHealthValue();
    }

    public int getColorId() {
        return food.getColorId();
    }

    public long framesPassed(long current) {
        return current - createTimestamp;
    }

    public boolean expired(long current) {
        if (food.isCrumb()) {
            return false;
        }
        return current - createTimestamp >= food.getFrames();
    }
}
