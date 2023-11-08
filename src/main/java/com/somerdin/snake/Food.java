package com.somerdin.snake;

public enum Food {
    // fruit
    RED_APPLE(500, 1, 0),
    CHERRY(1000, 2, 0),
    COOKIE(2000, 4, 0),

    // power ups
    SHIELD(500, 4, 0),
    INVINCIBLE(500, 4, 0),
    MAGNET(500, 4, 0),
    BOMB(1000, 8, 0),

    // represent different crumb colors
    CRUMB_1(10, 0, 1),
    CRUMB_2(10, 0, 2),
    CRUMB_3(10, 0, 3),
    CRUMB_4(10, 0, 4);

    public static Food getRandomCrumb() {
        return switch ((int) (4 * Math.random()) + 1) {
            case 1 -> CRUMB_1;
            case 2 -> CRUMB_2;
            case 3 -> CRUMB_3;
            default -> CRUMB_4;
        };
    }

    public static final Food[] FRUITS = new Food[] {
            RED_APPLE,
            CHERRY,
            COOKIE
    };

    private int score;
    private int healthValue;
    private int colorId;

    private Food(int score, int healthValue, int colorId) {
        this.score = score;
        this.healthValue = healthValue;
        this.colorId = colorId;
    }

    public boolean isFruit() {
        return this == RED_APPLE || this == CHERRY || this == COOKIE;
    }

    public boolean isPowerUp() {
        return this == SHIELD || this == INVINCIBLE || this == MAGNET || this == BOMB;
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
}
