package com.somerdin.snake;

public enum Food {
    RED_APPLE(5_000_000_000L, 400, 0),
    GREEN_APPLE(5_000_000_000L, 500, 0),
    YELLOW_APPLE(5_000_000_000L, 800, 0),
    CHERRY(10_000_000_000L, 1000, 0),

    CRUMB_1(0, 10, 1),
    CRUMB_2(0, 10, 2),
    CRUMB_3(0, 10, 3),
    CRUMB_4(0, 10, 4),
    HIDDEN_CRUMB(0, 0, 0);

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
            GREEN_APPLE,
            YELLOW_APPLE,
            CHERRY
    };

    private long timeAdd;
    private int score;
    private int colorId;

    private Food(long timeAdd, int score, int colorId) {
        this.timeAdd = timeAdd;
        this.score = score;
        this.colorId = colorId;
    }

    public boolean isFruit() {
        return this == RED_APPLE;
    }

    public boolean isCrumb() {
        return this == CRUMB_1 || this == CRUMB_2
                || this == CRUMB_3 || this == CRUMB_4;
    }

    public long getTimeAdd() {
        return timeAdd;
    }

    public int getScore() {
        return score;
    }

    public int getColorId() {
        return colorId;
    }
}
