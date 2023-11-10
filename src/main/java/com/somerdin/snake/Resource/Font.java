package com.somerdin.snake.Resource;

import java.io.IOException;
import java.io.InputStream;

public class Font {
    private static javafx.scene.text.Font getFont(double size) {
        try (InputStream in = Font.class.getResourceAsStream("/AtariGames.ttf")) {
            return javafx.scene.text.Font.loadFont(in, size);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load resource", e);
        }
    }

    public static final javafx.scene.text.Font ATARI_16 = getFont(16);
    public static final javafx.scene.text.Font ATARI_24 = getFont(24);
    public static final javafx.scene.text.Font ATARI_36 = getFont(36);
    public static final javafx.scene.text.Font ATARI_80 = getFont(80);
    public static final javafx.scene.text.Font ATARI_160 = getFont(160);
}
