package com.somerdin.snake;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public final class PixelTile {
    private static final int SCALE_FACTOR = 3;
    private static final Image PRIMARY_TILESET =
            new Image(PixelTile.class.getResourceAsStream("/snake.png"),
                    128 * SCALE_FACTOR,
                    128 * SCALE_FACTOR, true, false);
    public static final int PRIMARY_TILESET_WIDTH =
            (int) PRIMARY_TILESET.getWidth();
    public static final int TILE_WIDTH = 8 * SCALE_FACTOR;

    private static final ImageView SNAKE_HEAD_IMG =
            new ImageView(PRIMARY_TILESET);
    private static final ImageView SNAKE_BODY_IMG =
            new ImageView(PRIMARY_TILESET);
    private static final ImageView APPLE_IMG = new ImageView(PRIMARY_TILESET);
    private static final ImageView CRUMB_IMG = new ImageView(PRIMARY_TILESET);
    // TODO: add separate "absoluteDraw" method to gameloop
    public static final ImageView WALL_IMG = new ImageView(PRIMARY_TILESET);
    public static final ImageView SHURIKEN_IMG = new ImageView(PRIMARY_TILESET);


    static {
        SNAKE_HEAD_IMG.setViewport(getViewport(1, 3));
        SNAKE_BODY_IMG.setViewport(getViewport(5, 3));
        APPLE_IMG.setViewport(getViewport(6, 0));
        CRUMB_IMG.setViewport(getViewport(6, 5));
        WALL_IMG.setViewport(getViewport(12, 0));
        SHURIKEN_IMG.setViewport(getViewport(1, 6));
    }

    public static final PixelTile SNAKE_HEAD =
            new PixelTile(PixelTile.SNAKE_HEAD_IMG);
    public static final PixelTile SNAKE_BODY =
            new PixelTile(PixelTile.SNAKE_BODY_IMG);
    public static final PixelTile APPLE = new PixelTile(PixelTile.APPLE_IMG);
    public static final PixelTile CRUMB = new PixelTile(PixelTile.CRUMB_IMG);
    public static final PixelTile WALL = new PixelTile(PixelTile.WALL_IMG);
    public static final PixelTile SHURIKEN =
            new PixelTile(PixelTile.SHURIKEN_IMG);
    public static final PixelTile BLADE_PATH =
            new PixelTile(PixelTile.WALL_IMG);

    private static Rectangle2D getViewport(int x, int y) {
        return new Rectangle2D(x * TILE_WIDTH, y * TILE_WIDTH, TILE_WIDTH,
                TILE_WIDTH);
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