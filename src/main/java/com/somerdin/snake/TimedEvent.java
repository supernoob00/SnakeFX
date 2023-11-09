package com.somerdin.snake;

/**
 * Generic class for any game event with a timestamp.
 */
// TODO: be careful of bug with indefinite duration
public class TimedEvent {
    public static final int INDEFINITE_DURATION = -1;
    public static final int INVULNERABLE_TIME = 150; // 2.5 seconds
    public static final int MAGNET_ACTIVE_TIME = 300 ;
    public static final int POWER_UP_ACTIVE_TIME = 300; // 5 seconds

    public static enum TimedEventType {
        SNAKE_EXPLODE(INDEFINITE_DURATION),
        BLADES_EXPLODE(INDEFINITE_DURATION),
        INVULNERABLE(INVULNERABLE_TIME),
        MAGNETIZED_POWER_UP(MAGNET_ACTIVE_TIME),
        INVINCIBLE_POWER_UP(POWER_UP_ACTIVE_TIME),
        BOMB_POWER_UP(INDEFINITE_DURATION),
        GAME_OVER(INDEFINITE_DURATION);

        public final int duration;

        private TimedEventType(int duration) {
            this.duration = duration;
        }
    }

    private TimedEventType type;
    private long timestamp = 0;

    public TimedEvent(TimedEventType type) {
        this.type = type;
    }

    public long framesPassed(long frameCount) {
        if (timestamp == 0) {
            throw new IllegalStateException("Event not yet started.");
        }
        return frameCount - timestamp;
    }

    public boolean inProgress(long frameCount) {
        if (timestamp == 0) {
            return false;
        }
        if (type.duration == INDEFINITE_DURATION) {
            return true;
        }
        return frameCount - timestamp < type.duration;
    }

    public void start(long frameCount) {
        timestamp = frameCount;
    }
}
