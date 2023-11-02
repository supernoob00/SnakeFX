package com.somerdin.snake;

public class ParticleManager {
    public double[] xPos;
    public double[] yPos;
    public double[] xSpeed;
    public double[] ySpeed;
    private double bottomBounds;
    private double rightBounds;
    private double deceleration;
    private boolean moving = true;

    public ParticleManager(int count, double rightBounds, double bottomBounds
            , double deceleration) {
        xPos = new double[count];
        yPos = new double[count];
        xSpeed = new double[count];
        ySpeed = new double[count];

        this.rightBounds = rightBounds;
        this.bottomBounds = bottomBounds;
        this.deceleration = deceleration;
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
            if (xPos[i] > rightBounds - PixelTile.PIXEL_WIDTH) {
                xPos[i] = rightBounds - PixelTile.PIXEL_WIDTH;
                xSpeed[i] = -xSpeed[i];
            }
            if (yPos[i] > bottomBounds - PixelTile.PIXEL_WIDTH) {
                yPos[i] = bottomBounds - PixelTile.PIXEL_WIDTH;
                ySpeed[i] = -ySpeed[i];
            }
            if (xSpeed[i] != 0 || ySpeed[i] != 0) {
                motion = true;
            }

            if (motion) {
                xPos[i] += xSpeed[i] / frames;
                yPos[i] += ySpeed[i] / frames;
                decelerate(i);
            } else {
                moving = false;
            }
        }
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
}
