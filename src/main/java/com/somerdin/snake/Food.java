package com.somerdin.snake;

public enum Food {
    RED_APPLE(2_000_000_000L, 400),
    GREEN_APPLE(3_000_000_000L, 500),
    YELLOW_APPLE(5_000_000_000L, 800),
    CHERRY(10_000_000_000L, 1000),

    CRUMB(0, 10);

    private long timeAdd;
    private int score;

    private Food(long timeAdd, int score) {
        this.timeAdd = timeAdd;
        this.score = score;
    }

    public boolean isFruit() {
        return this == RED_APPLE;
    }

    public long getTimeAdd() {
        return timeAdd;
    }

    public int getScore() {
        return score;
    }
}
