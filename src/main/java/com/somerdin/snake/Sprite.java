package com.somerdin.snake;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.io.InputStream;

public final class Sprite {
    public static final int BOTTOM_LEFT_CORNER = 1;
    public static final int BOTTOM_RIGHT_CORNER = 2;
    public static final int TOP_LEFT_CORNER = 3;
    public static final int TOP_RIGHT_CORNER = 4;

    public static final int TILE_WIDTH_PIXELS = 8;
    public static final int SCALE_FACTOR = 3;
    public static final int TILE_WIDTH_ACTUAL = 8 * SCALE_FACTOR;
    public static final int PIXEL_WIDTH = TILE_WIDTH_ACTUAL / TILE_WIDTH_PIXELS;

    private static final Image PRIMARY_SPRITE_SHEET;
    private static final Image BLADE_PATH_SPRITE_SHEET;
    private static final Image HEART_SPRITESHEET;

    static {
        try (InputStream in = Sprite.class.getResourceAsStream("/pixil-layer" +
                "-Background" +
                ".png");
        InputStream in2 = Sprite.class.getResourceAsStream("/blade-path.png");
        InputStream in3 = Sprite.class.getResourceAsStream("/pixil-frame-heart.png");) {
            PRIMARY_SPRITE_SHEET = new Image(in, 128 * SCALE_FACTOR, 128 * SCALE_FACTOR, true, false);
            BLADE_PATH_SPRITE_SHEET = new Image(in2, 32 * SCALE_FACTOR, 32 * SCALE_FACTOR, true, false);
            HEART_SPRITESHEET = new Image(in3, 32 * SCALE_FACTOR, 32 * SCALE_FACTOR, true, false);
        } catch (IOException e) {
            throw new RuntimeException("Unable to find resource", e);
        }
    }

    private static final ImageView SNAKE_HEAD_IMG =
            new ImageView(PRIMARY_SPRITE_SHEET);
    private static final ImageView SNAKE_BODY_IMG =
            new ImageView(PRIMARY_SPRITE_SHEET);
    private static final ImageView RED_APPLE_IMG = new ImageView(PRIMARY_SPRITE_SHEET);
    private static final ImageView GREEN_APPLE_IMG =
            new ImageView(PRIMARY_SPRITE_SHEET);
    private static final ImageView YELLOW_APPLE_IMG =
            new ImageView(PRIMARY_SPRITE_SHEET);
    private static final ImageView CHERRY_IMG = new ImageView(PRIMARY_SPRITE_SHEET);
    private static final ImageView CRUMB_IMG_1 =
            new ImageView(PRIMARY_SPRITE_SHEET);
    private static final ImageView CRUMB_IMG_2 =
            new ImageView(PRIMARY_SPRITE_SHEET);
    private static final ImageView CRUMB_IMG_3 =
            new ImageView(PRIMARY_SPRITE_SHEET);
    private static final ImageView CRUMB_IMG_4 =
            new ImageView(PRIMARY_SPRITE_SHEET);
    private static final ImageView EMPTY_IMG = new ImageView(PRIMARY_SPRITE_SHEET);
    // TODO: add separate "absoluteDraw" method to gameloop
    public static final ImageView WALL_IMG = new ImageView(PRIMARY_SPRITE_SHEET);
    public static final ImageView WALL_CORNER_IMG =
            new ImageView(PRIMARY_SPRITE_SHEET);
    public static final ImageView SHURIKEN_IMG = new ImageView(PRIMARY_SPRITE_SHEET);
    private static final ImageView BLADE_PATH_IMG =
            new ImageView(BLADE_PATH_SPRITE_SHEET);
    private static final ImageView HEART_EMPTY_IMG =
            new ImageView(HEART_SPRITESHEET);
    private static final ImageView HEART_QUARTER_IMG =
            new ImageView(HEART_SPRITESHEET);
    private static final ImageView HEART_HALF_IMG =
            new ImageView(HEART_SPRITESHEET);
    private static final ImageView HEART_THREE_QUARTERS_IMG =
            new ImageView(HEART_SPRITESHEET);
    private static final ImageView HEART_FULL_IMG =
            new ImageView(HEART_SPRITESHEET);

    static {
        SNAKE_HEAD_IMG.setViewport(getViewport(1, 3, TILE_WIDTH_ACTUAL));
        SNAKE_BODY_IMG.setViewport(getViewport(5, 3, TILE_WIDTH_ACTUAL));
        RED_APPLE_IMG.setViewport(getViewport(6, 0, TILE_WIDTH_ACTUAL));
        GREEN_APPLE_IMG.setViewport(getViewport(6, 1, TILE_WIDTH_ACTUAL));
        YELLOW_APPLE_IMG.setViewport(getViewport(6, 4, TILE_WIDTH_ACTUAL));
        CHERRY_IMG.setViewport(getViewport(6, 3, TILE_WIDTH_ACTUAL));
        CRUMB_IMG_1.setViewport(getViewport(7, 5, TILE_WIDTH_ACTUAL));
        CRUMB_IMG_2.setViewport(getViewport(8, 5, TILE_WIDTH_ACTUAL));
        CRUMB_IMG_3.setViewport(getViewport(9, 5, TILE_WIDTH_ACTUAL));
        CRUMB_IMG_4.setViewport(getViewport(10, 5, TILE_WIDTH_ACTUAL));
        EMPTY_IMG.setViewport(getViewport(2, 6, TILE_WIDTH_ACTUAL));
        WALL_IMG.setViewport(getViewport(3, 6, TILE_WIDTH_ACTUAL));
        WALL_CORNER_IMG.setViewport(getViewport(4, 6, TILE_WIDTH_ACTUAL));
        SHURIKEN_IMG.setViewport(getViewport(1, 6, TILE_WIDTH_ACTUAL));
        HEART_EMPTY_IMG.setViewport(getViewport(0, 0, TILE_WIDTH_ACTUAL));
        HEART_QUARTER_IMG.setViewport(getViewport(1, 0, TILE_WIDTH_ACTUAL));
        HEART_HALF_IMG.setViewport(getViewport(2, 0, TILE_WIDTH_ACTUAL));
        HEART_THREE_QUARTERS_IMG.setViewport(getViewport(3, 0 ,
                TILE_WIDTH_ACTUAL));
        HEART_FULL_IMG.setViewport(getViewport(0, 1, TILE_WIDTH_ACTUAL));
    }

    public static final Sprite SNAKE_HEAD =
            new Sprite(Sprite.SNAKE_HEAD_IMG);
    public static final Sprite SNAKE_BODY =
            new Sprite(Sprite.SNAKE_BODY_IMG);
    public static final Sprite APPLE = new Sprite(Sprite.RED_APPLE_IMG);
    public static final Sprite EMPTY = new Sprite(Sprite.EMPTY_IMG);
    public static final Sprite WALL = new Sprite(Sprite.WALL_IMG);
    public static final Sprite WALL_CORNER = new Sprite(Sprite.WALL_CORNER_IMG);
    public static final Sprite SHURIKEN =
            new Sprite(Sprite.SHURIKEN_IMG);
    private static final Sprite BLADE_PATH =
            new Sprite(Sprite.BLADE_PATH_IMG);
    public static final Sprite EMPTY_HEART =
            new Sprite(Sprite.HEART_EMPTY_IMG);
    public static final Sprite QUARTER_HEART =
            new Sprite(Sprite.HEART_QUARTER_IMG);
    public static final Sprite HALF_HEART =
            new Sprite(Sprite.HEART_HALF_IMG);
    public static final Sprite THREE_QUARTERS_HEART =
            new Sprite(Sprite.HEART_THREE_QUARTERS_IMG);
    public static final Sprite FULL_HEART = new Sprite(Sprite.HEART_FULL_IMG);
    public static final Sprite CRUMB_1 = new Sprite(Sprite.CRUMB_IMG_1);
    public static final Sprite CRUMB_2 = new Sprite(Sprite.CRUMB_IMG_2);
    public static final Sprite CRUMB_3 = new Sprite(Sprite.CRUMB_IMG_3);
    public static final Sprite CRUMB_4 = new Sprite(Sprite.CRUMB_IMG_4);

    public static Sprite getBladePathTileById(int colorId, int whichCorner) {
        Rectangle2D viewport;
        if (whichCorner == -1) {
            switch (colorId) {
                case 1 -> viewport = getViewport(0, 0, TILE_WIDTH_ACTUAL);
                case 2 -> viewport = getViewport(1, 0, TILE_WIDTH_ACTUAL);
                default -> viewport = getViewport(2, 0, TILE_WIDTH_ACTUAL);
            };
        } else {
            switch (colorId) {
                case 1 -> viewport = getViewport(2, 1, TILE_WIDTH_ACTUAL);
                case 2 -> viewport = getViewport(3, 1, TILE_WIDTH_ACTUAL);
                default -> viewport = getViewport(0, 2, TILE_WIDTH_ACTUAL);
            }
        }
        switch (whichCorner) {
            case BOTTOM_LEFT_CORNER -> BLADE_PATH.imgView.setRotate(0);
            case BOTTOM_RIGHT_CORNER -> BLADE_PATH.imgView.setRotate(-90);
            case TOP_LEFT_CORNER -> BLADE_PATH.imgView.setRotate(90);
            case TOP_RIGHT_CORNER -> BLADE_PATH.imgView.setRotate(180);
            default -> throw new IllegalArgumentException("Invalid angle " +
                    "argument");
        }
        BLADE_PATH.imgView.setViewport(viewport);
        return BLADE_PATH;
    }

    public static Sprite getCrumbById(int id) {
        return switch (id) {
            case 1 -> CRUMB_1;
            case 2 -> CRUMB_2;
            case 3 -> CRUMB_3;
            default -> CRUMB_4;
        };
    }

    private static Rectangle2D getViewport(int x, int y, int tileWidth) {
        return new Rectangle2D(x * tileWidth, y * tileWidth,
                tileWidth,
                tileWidth);
    }

    private ImageView imgView;
    // Direction.UP is right-side up, rotation angle always stored postive
    private double rotateAngle;
    public Sprite(ImageView imgView, double rotateAngle) {
        this.imgView = imgView;
        if (rotateAngle < 0) {
            rotateAngle = -rotateAngle + 180;
        }
        this.rotateAngle = rotateAngle % 360;
    };
    public Sprite(ImageView imgView) {
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

    public void setRotateAngle(double angle) {
        imgView.setRotate(angle);
    }
}
