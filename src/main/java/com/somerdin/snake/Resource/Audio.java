package com.somerdin.snake.Resource;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;

public class Audio {
    public static final Media MUSIC =
            new Media(Audio.class.getResource("/music/Theme_1.mp3").toExternalForm());
    public static final AudioClip EAT_CRUMB_SOUND =
            new AudioClip(Audio.class.getResource("/sound/sfx_coin_single3" +
                    ".wav").toExternalForm());
    public static final AudioClip EAT_FRUIT_SOUND =
            new AudioClip(Audio.class.getResource("/sound" +
                    "/sfx_sounds_powerup17.wav").toExternalForm());
    public static final AudioClip POWER_UP_SOUND =
            new AudioClip(Audio.class.getResource("/sound" +
                    "/sfx_sounds_powerup17.wav").toExternalForm());
    public static final AudioClip DEATH_SOUND = new AudioClip(Audio.class.getResource(
            "/sound" +
            "/sfx_deathscream_alien2.wav").toExternalForm());
    public static final AudioClip DAMAGE_SOUND =
            new AudioClip(Audio.class.getResource("/sound" +
                    "/sfx_deathscream_alien2.wav").toExternalForm());
    public static final AudioClip BLADE_SOUND =
            new AudioClip(Audio.class.getResource("/sound" +
                    "/sfx_vehicle_plainloop.wav").toExternalForm());

    static {
        EAT_CRUMB_SOUND.setVolume(0.5);
        EAT_FRUIT_SOUND.setVolume(0.25);
        POWER_UP_SOUND.setVolume(0.3);
        DEATH_SOUND.setVolume(0.8);
        DAMAGE_SOUND.setVolume(0.6);
        BLADE_SOUND.setVolume(0.3);
        BLADE_SOUND.setCycleCount(AudioClip.INDEFINITE);
    }
}
