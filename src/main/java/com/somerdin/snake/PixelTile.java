package com.somerdin.snake;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public final class PixelTile {
    private static final Image PRIMARY_TILESET =
            new Image(PixelTile.class.getResourceAsStream("/snake.png"), 512,
                    512, true, false);
    public static final int PRIMARY_TILESET_WIDTH =
            (int) PRIMARY_TILESET.getWidth();
    public static final int TILE_WIDTH = 32;

    public static final ImageView SNAKE_HEAD_UP =
            new ImageView(PRIMARY_TILESET);
    public static final ImageView SNAKE_HEAD_LEFT =
            new ImageView(PRIMARY_TILESET);
    public static final ImageView SNAKE_HEAD_DOWN =
            new ImageView(PRIMARY_TILESET);
    public static final ImageView SNAKE_HEAD_RIGHT =
            new ImageView(PRIMARY_TILESET);
    public static final ImageView SNAKE_BODY = new ImageView(PRIMARY_TILESET);
    public static final ImageView APPLE = new ImageView(PRIMARY_TILESET);
    public static final ImageView CRUMBS_1 = new ImageView(PRIMARY_TILESET);
    public static final ImageView CRUMBS_2 = new ImageView(PRIMARY_TILESET);
    public static final ImageView CRUMBS_3 = new ImageView(PRIMARY_TILESET);
    public static final ImageView CRUMBS_4 = new ImageView(PRIMARY_TILESET);
    public static final ImageView CRUMBS_5 = new ImageView(PRIMARY_TILESET);
    public static final ImageView CRUMBS_6 = new ImageView(PRIMARY_TILESET);
    public static final ImageView[] CRUMB_TILES = {CRUMBS_1, CRUMBS_2,
            CRUMBS_3, CRUMBS_4, CRUMBS_5, CRUMBS_6};

    static {
        SNAKE_HEAD_UP.setViewport(getViewport(1, 3));
        SNAKE_HEAD_LEFT.setViewport(getViewport(2, 3));
        SNAKE_HEAD_DOWN.setViewport(getViewport(3, 3));
        SNAKE_HEAD_RIGHT.setViewport(getViewport(4, 3));
        SNAKE_BODY.setViewport(getViewport(5, 3));
        APPLE.setViewport(getViewport(6, 0));
        CRUMBS_1.setViewport(getViewport(9, 0));
        CRUMBS_2.setViewport(getViewport(10, 0));
        CRUMBS_3.setViewport(getViewport(11, 0));
        CRUMBS_4.setViewport(getViewport(9, 1));
        CRUMBS_5.setViewport(getViewport(10, 1));
        CRUMBS_6.setViewport(getViewport(11, 1));
    }

    private static Rectangle2D getViewport(int x, int y) {
        return new Rectangle2D(x * TILE_WIDTH, y * TILE_WIDTH, TILE_WIDTH,
                TILE_WIDTH);
    }

    private PixelTile() {};
}
