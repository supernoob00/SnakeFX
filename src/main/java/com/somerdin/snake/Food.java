package com.somerdin.snake;

public enum Food {
    // fruit
    RED_APPLE(250, 1, 200, 0),
    CHERRY(500, 2, 200, 0),
    COOKIE(1000, 4, 200, 0),

    // power ups
    INVINCIBLE(250, 4, 200, 0),
    BOMB(500, 8, 200, 0),

    // represent different crumb colors
    CRUMB_1(10, 0, -1, 1),
    CRUMB_2(10, 0, -1, 2),
    CRUMB_3(10, 0, -1, 3),
    CRUMB_4(10, 0, -1, 4);

    public static Food getRandomCrumb() {
        return switch ((int) (4 * Math.random()) + 1) {
            case 1 -> CRUMB_1;
            case 2 -> CRUMB_2;
            case 3 -> CRUMB_3;
            default -> CRUMB_4;
        };
    }

    private final int score;
    private final int healthValue;
    private final int frames;
    private final int colorId;

    Food(int score, int healthValue, int frames, int colorId) {
        this.score = score;
        this.healthValue = healthValue;
        this.frames = frames;
        this.colorId = colorId;
    }

    public boolean isFruit() {
        return this == RED_APPLE || this == CHERRY || this == COOKIE;
    }

    public boolean isPowerUp() {
        return this == INVINCIBLE || this == BOMB;
    }

    public boolean isCrumb() {
        return this == CRUMB_1 || this == CRUMB_2
                || this == CRUMB_3 || this == CRUMB_4;
    }

    public int getScore() {
        return score;
    }

    public int getHealthValue() {
        return healthValue;
    }

    public int getColorId() {
        return colorId;
    }

    public int getFrames() {
        return frames;
    }
}
