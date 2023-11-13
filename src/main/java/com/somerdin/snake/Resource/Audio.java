package com.somerdin.snake.Resource;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;

public class Audio {
    public static final Media MUSIC =
            new Media(Audio.class.getResource("/music/fighter-nes.wav").toExternalForm());
    public static final AudioClip EAT_CRUMB_SOUND =
            new AudioClip(Audio.class.getResource("/sound/coin_single" +
                    ".wav").toExternalForm());
    public static final AudioClip EAT_FRUIT_SOUND =
            new AudioClip(Audio.class.getResource("/sound" +
                    "/powerup.wav").toExternalForm());
    public static final AudioClip POWER_UP_SOUND =
            new AudioClip(Audio.class.getResource("/sound" +
                    "/powerup.wav").toExternalForm());
    public static final AudioClip DEATH_SOUND = new AudioClip(Audio.class.getResource(
            "/sound" +
                    "/death_scream.wav").toExternalForm());
    public static final AudioClip DAMAGE_SOUND =
            new AudioClip(Audio.class.getResource("/sound/damage_sound.wav").toExternalForm());
    public static final AudioClip BLADE_SOUND =
            new AudioClip(Audio.class.getResource("/sound/sfx_vehicle_plainloop.wav").toExternalForm());
    public static final AudioClip BLADE_EXPLODE_SOUND =
            new AudioClip(Audio.class.getResource("/sound/blade_explode.wav").toExternalForm());
    public static final AudioClip BOMB_SOUND =
            new AudioClip(Audio.class.getResource("/sound/bomb.wav").toExternalForm());
    public static final AudioClip MENU_SOUND =
            new AudioClip(Audio.class.getResource("/sound/menu_select.wav").toExternalForm());
    public static final AudioClip INVINCIBLE_SOUND =
            new AudioClip(Audio.class.getResource("/sound/invincible.wav").toExternalForm());
    public static final AudioClip CRUMBS_CLEARED_SOUND =
            new AudioClip(Audio.class.getResource("/sound/crumbs_cleared.wav").toExternalForm());
    public static final AudioClip GAME_OVER_SOUND =
            new AudioClip(Audio.class.getResource("/sound/sfx_sound_shutdown2.wav").toExternalForm());
    public static final AudioClip START_SOUND =
            new AudioClip(Audio.class.getResource("/sound/start_sound.wav").toExternalForm());
    public static final AudioClip LOW_BOOST_SOUND =
            new AudioClip(Audio.class.getResource("/sound/low_boost_alarm" +
                    ".wav").toExternalForm());

    static {
        EAT_CRUMB_SOUND.setVolume(0.1);
        EAT_FRUIT_SOUND.setVolume(0.5);
        POWER_UP_SOUND.setVolume(0.3);
        DEATH_SOUND.setVolume(0.6);
        DAMAGE_SOUND.setVolume(0.7);
        BLADE_SOUND.setVolume(0.3);
        BLADE_SOUND.setCycleCount(AudioClip.INDEFINITE);
        BLADE_EXPLODE_SOUND.setVolume(0.9);
        BOMB_SOUND.setVolume(1.0  );
        CRUMBS_CLEARED_SOUND.setVolume(0.8);
        START_SOUND.setVolume(0.7);
        LOW_BOOST_SOUND.setVolume(0.7);
    }
}
