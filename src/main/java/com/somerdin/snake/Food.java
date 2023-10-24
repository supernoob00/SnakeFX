package com.somerdin.snake;

public enum Food {
    APPLE,
    CRUMBS_1,
    CRUMBS_2,
    CRUMBS_3,
    CRUMBS_4,
    CRUMBS_5,
    CRUMBS_6;

    public static Food[] CRUMBS = {CRUMBS_1, CRUMBS_2, CRUMBS_3, CRUMBS_4,
            CRUMBS_5, CRUMBS_6};

    private Food() {

    }

    public boolean isFruit() {
        return this == APPLE;
    }

    public boolean isCrumbs() {
        return this == CRUMBS_1 || this == CRUMBS_2 || this == CRUMBS_3
                || this == CRUMBS_4 || this == CRUMBS_5 || this == CRUMBS_6;
    }
}
