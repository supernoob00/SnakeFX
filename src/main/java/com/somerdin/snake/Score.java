package com.somerdin.snake;

import java.io.*;

public class Score {
    public static final String WORKING_DIRECTORY = System.getProperty("user.dir");
    public static final String TEXT_FILE_DIRECTORY =
            WORKING_DIRECTORY + "/snake_game_highscore.txt";

    private static int highScore;
    private static boolean isScoreSaved;

    public static int getHighScore() {
        return highScore;
    }

    public static void setHighScore(int newHighScore) {
        highScore = newHighScore;
    }

    public static boolean isScoreSaved() {
        return isScoreSaved;
    }

    public static void setScoreSaved(boolean save) {
        isScoreSaved = save;
    }

    public static int loadHighScore() {
        try (BufferedReader reader =
                     new BufferedReader(new FileReader(TEXT_FILE_DIRECTORY));){
            String score = reader.readLine();
            int val = Integer.parseInt(score);
            if (val < 0 || val > 999_999_999) {
                return -1;
            }
            return val;
        } catch (IOException | NumberFormatException e) {
            return -1;
        }
    }

    public static void writeHighScore(int score) {
        try (PrintWriter writer = new PrintWriter(TEXT_FILE_DIRECTORY);) {
            writer.println(score);
        } catch (FileNotFoundException e) {
        }
    }
}
