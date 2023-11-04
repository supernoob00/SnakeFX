package com.somerdin.snake;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public final class PixelTile {
    public static final int TILE_WIDTH_PIXELS = 8;
    private static final int SCALE_FACTOR = 3;
    public static final int TILE_WIDTH_ACTUAL = 8 * SCALE_FACTOR;
    public static final int PIXEL_WIDTH = TILE_WIDTH_ACTUAL / TILE_WIDTH_PIXELS;

    private static final Image PRIMARY_TILESET =
            new Image(PixelTile.class.getResourceAsStream("/snake_transparent" +
                    ".png"),
                    128 * SCALE_FACTOR,
                    128 * SCALE_FACTOR, true, false);
    private static final Image BLADE_PATH_TILESET =
            new Image(PixelTile.class.getResourceAsStream("/pixil-frame-0" +
                    ".png"), 32 * SCALE_FACTOR, 32 * SCALE_FACTOR, true, false);
    public static final int PRIMARY_TILESET_WIDTH =
            (int) PRIMARY_TILESET.getWidth();

    private static final ImageView SNAKE_HEAD_IMG =
            new ImageView(PRIMARY_TILESET);
    private static final ImageView SNAKE_BODY_IMG =
            new ImageView(PRIMARY_TILESET);
    private static final ImageView RED_APPLE_IMG = new ImageView(PRIMARY_TILESET);
    private static final ImageView GREEN_APPLE_IMG =
            new ImageView(PRIMARY_TILESET);
    private static final ImageView YELLOW_APPLE_IMG =
            new ImageView(PRIMARY_TILESET);
    private static final ImageView CHERRY_IMG = new ImageView(PRIMARY_TILESET);
    private static final ImageView CRUMB_IMG = new ImageView(PRIMARY_TILESET);
    private static final ImageView EMPTY_IMG = new ImageView(PRIMARY_TILESET);
    // TODO: add separate "absoluteDraw" method to gameloop
    public static final ImageView WALL_IMG = new ImageView(PRIMARY_TILESET);
    public static final ImageView SHURIKEN_IMG = new ImageView(PRIMARY_TILESET);
    private static final ImageView BLADE_PATH_IMG =
            new ImageView(BLADE_PATH_TILESET);


    static {
        SNAKE_HEAD_IMG.setViewport(getViewport(1, 3, TILE_WIDTH_ACTUAL));
        SNAKE_BODY_IMG.setViewport(getViewport(5, 3, TILE_WIDTH_ACTUAL));
        RED_APPLE_IMG.setViewport(getViewport(6, 0, TILE_WIDTH_ACTUAL));
        GREEN_APPLE_IMG.setViewport(getViewport(6, 1, TILE_WIDTH_ACTUAL));
        YELLOW_APPLE_IMG.setViewport(getViewport(6, 4, TILE_WIDTH_ACTUAL));
        CHERRY_IMG.setViewport(getViewport(6, 3, TILE_WIDTH_ACTUAL));
        CRUMB_IMG.setViewport(getViewport(6, 5, TILE_WIDTH_ACTUAL));
        EMPTY_IMG.setViewport(getViewport(5, 6, TILE_WIDTH_ACTUAL));
        WALL_IMG.setViewport(getViewport(12, 0, TILE_WIDTH_ACTUAL));
        SHURIKEN_IMG.setViewport(getViewport(1, 6, TILE_WIDTH_ACTUAL));
    }

    public static final PixelTile SNAKE_HEAD =
            new PixelTile(PixelTile.SNAKE_HEAD_IMG);
    public static final PixelTile SNAKE_BODY =
            new PixelTile(PixelTile.SNAKE_BODY_IMG);
    public static final PixelTile APPLE = new PixelTile(PixelTile.RED_APPLE_IMG);
    public static final PixelTile CRUMB = new PixelTile(PixelTile.CRUMB_IMG);
    public static final PixelTile EMPTY = new PixelTile(PixelTile.EMPTY_IMG);
    public static final PixelTile WALL = new PixelTile(PixelTile.WALL_IMG);
    public static final PixelTile SHURIKEN =
            new PixelTile(PixelTile.SHURIKEN_IMG);
    private static final PixelTile BLADE_PATH =
            new PixelTile(PixelTile.BLADE_PATH_IMG);

    public static PixelTile getBladePathTileById(int colorId) {
        Rectangle2D viewport = switch (colorId) {
            case 0 -> getViewport(0, 0, TILE_WIDTH_ACTUAL);
            case 1 -> getViewport(2, 0, TILE_WIDTH_ACTUAL);
            case 2 -> getViewport(3, 0, TILE_WIDTH_ACTUAL);
            case 3 -> getViewport(4, 0, TILE_WIDTH_ACTUAL);
            case 4 -> getViewport(0, 1, TILE_WIDTH_ACTUAL);
            case 5 -> getViewport(1, 1, TILE_WIDTH_ACTUAL);
            default ->
                    throw new IllegalStateException("Unexpected value: " + (int) (6 * Math.random()));
        };
        BLADE_PATH.imgView.setViewport(viewport);
        return BLADE_PATH;
    }

    private static Rectangle2D getViewport(int x, int y, int tileWidth) {
        return new Rectangle2D(x * tileWidth, y * tileWidth,
                tileWidth,
                tileWidth);
    }

    private ImageView imgView;
    // Direction.UP is right-side up, rotation angle always stored postive
    private double rotateAngle;
    public PixelTile(ImageView imgView, double rotateAngle) {
        this.imgView = imgView;
        if (rotateAngle < 0) {
            rotateAngle = -rotateAngle + 180;
        }
        this.rotateAngle = rotateAngle % 360;
    };
    public PixelTile(ImageView imgView) {
        this(imgView, 0);
    }

    public ImageView getImageView() {
        return imgView;
    }

    public Image getImage() {
        return imgView.getImage();
    }

    public double getRotate() {
        return imgView.getRotate();
    }

    public Direction getDirection() {
        return switch ((int) rotateAngle) {
            case 0 -> Direction.UP;
            case 90 -> Direction.RIGHT;
            case 180 -> Direction.DOWN;
            case 270 -> Direction.LEFT;
            default -> throw new IllegalArgumentException("Current rotation " +
                    "angle is not a multiple of 90.");
        };
    }

    public int width() {
        return (int) imgView.getViewport().getWidth();
    }

    public int height() {
        return (int) imgView.getViewport().getHeight();
    }

    public int getX() {
        return (int) imgView.getViewport().getMinX();
    }

    public int getY() {
        return (int) imgView.getViewport().getMinY();
    }

    public void setOrientation(Direction direction) {
        switch (direction) {
            case UP:
                imgView.setRotate(0);
                break;
            case LEFT:
                imgView.setRotate(-90);
                break;
            case RIGHT:
                imgView.setRotate(90);
                break;
            case DOWN:
                imgView.setRotate(180);
                break;
        }
    }

    public void rotate(double angle) {
        imgView.setRotate(getRotate() + angle);
    }
}
