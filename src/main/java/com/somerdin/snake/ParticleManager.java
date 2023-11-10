package com.somerdin.snake;

import com.somerdin.snake.Resource.Sprite;

import java.util.Arrays;

public class ParticleManager {
    public double[] xPos;
    public double[] yPos;
    public double[] xSpeed;
    public double[] ySpeed;
    public boolean[] visible;

    private int[] particleVisibility;
    private int visibleIndex;

    private double bottomBounds;
    private double rightBounds;
    private double deceleration;
    private boolean moving = true;

    private static void shuffle(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            int rand = i + (int) (Math.random() * (arr.length - i));
            int temp = arr[i];
            arr[i] = arr[rand];
            arr[rand] = temp;
        }
    }

    public ParticleManager(int count, double rightBounds, double bottomBounds
            , double deceleration) {
        xPos = new double[count];
        yPos = new double[count];
        xSpeed = new double[count];
        ySpeed = new double[count];
        visible = new boolean[count];
        Arrays.fill(visible, true);
        particleVisibility = new int[count];

        for (int i = 0; i < count; i++) {
            particleVisibility[i] = i;
        }

        visibleIndex = count;

        this.rightBounds = rightBounds;
        this.bottomBounds = bottomBounds;
        this.deceleration = deceleration;

        // shuffle visibility
        shuffle(particleVisibility);
    }

    private double[] minSpeed;

    public void updatePos(double frames) {
        boolean motion = false;
        for (int i = 0; i < xPos.length; i++) {
            if (xPos[i] < 0) {
                xPos[i] = 0;
                xSpeed[i] = -xSpeed[i];
            }
            if (yPos[i] < 0) {
                yPos[i] = 0;
                ySpeed[i] = -ySpeed[i];
            }
            if (xPos[i] > rightBounds - Sprite.PIXEL_WIDTH) {
                xPos[i] = rightBounds - Sprite.PIXEL_WIDTH;
                xSpeed[i] = -xSpeed[i];
            }
            if (yPos[i] > bottomBounds - Sprite.PIXEL_WIDTH) {
                yPos[i] = bottomBounds - Sprite.PIXEL_WIDTH;
                ySpeed[i] = -ySpeed[i];
            }
            if (xSpeed[i] != 0 || ySpeed[i] != 0) {
                motion = true;
            }
            xPos[i] += xSpeed[i] / frames;
            yPos[i] += ySpeed[i] / frames;
            decelerate(i);
        }
        moving = motion;
    }

    public boolean isMoving() {
        return moving;
    }

    private void decelerate(int id) {
        if (xSpeed[id] < 0) {
            xSpeed[id] += deceleration;
            if (xSpeed[id] > 0) {
                xSpeed[id] = 0;
            }
        }
        if (xSpeed[id] > 0) {
            xSpeed[id] -= deceleration;
            if (xSpeed[id] < 0) {
                xSpeed[id] = 0;
            }
        }
        if (ySpeed[id] < 0) {
            ySpeed[id] += deceleration;
            if (ySpeed[id] > 0) {
                ySpeed[id] = 0;
            }
        }
        if (ySpeed[id] > 0) {
            ySpeed[id] -= deceleration;
            if (ySpeed[id] < 0) {
                ySpeed[id] = 0;
            }
        }
    }

    public void changeSpeed(double factor) {
        for (int i = 0; i < xSpeed.length; i++) {
            xSpeed[i] *= factor;
            ySpeed[i] *= factor;
        }
    }

    public void setVelocity(int id, double angle, double speed) {
        double angleRadians = Math.toRadians(angle);
        xSpeed[id] = speed * Math.cos(angleRadians);
        ySpeed[id] = speed * Math.sin(angleRadians);
    }

    public int getCount() {
        return xPos.length;
    }

    public boolean setRandomParticleInvisible() {
        if (visibleIndex == 0) {
            return false;
        }

        int id = particleVisibility[--visibleIndex];
        visible[id] = false;
        return true;
    }

    public boolean allParticlesInvisible() {
        return visibleIndex == 0;
    }
}
