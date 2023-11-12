package com.somerdin.snake;

import java.util.Arrays;

public class Maze {
    // for direction booleans, false is if wall in that direction is broken
    public static class MazeCell {
        public boolean north = true;
        public boolean west = true;
        public boolean east = true;
        public boolean south = true;
        public boolean traveled = false;

        @Override
        public String toString() {
            char[] letters = new char[]{'*', '*', '*', '*'};
            if (!north) {
                letters[0] = 'N';
            }
            if (!west) {
                letters[1] = 'W';
            }
            if (!east) {
                letters[3] = 'E';
            }
            if (!south) {
                letters[2] = 'S';
            }
            return new String(letters);
        }
    }

    private final MazeCell[][] maze;

    public Maze(int size) {
        maze = new MazeCell[size + 2][size + 2];
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze.length; j++) {
                maze[i][j] = new MazeCell();
            }
        }

        // set sentinel values for outer cells
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze.length; j++) {
                if (i == 0 || i == maze.length - 1 || j == 0 || j == maze.length - 1) {
                    maze[i][j].traveled = true;
                }
            }
        }
        generate();
    }

    private void generate() {
        generate(1, 1);
    }

    private void generate(int x, int y) {
        maze[y][x].traveled = true;
        while (true) {
            int[] direction = direction(x, y);
            if (direction[0] == -1 && direction[1] == -1) {
                return;
            }
            if (direction[0] == 1 && direction[1] == 0) {
                // EAST
                maze[y][x].east = false;
                maze[y][x + 1].west = false;
                x++;
            } else if (direction[0] == 0 && direction[1] == -1) {
                // NORTH
                maze[y][x].north = false;
                maze[y - 1][x].south = false;
                y--;
            } else if (direction[0] == -1 && direction[1] == 0) {
                // WEST
                maze[y][x].west = false;
                maze[y][x - 1].east = false;
                x--;
            } else if (direction[0] == 0 && direction[1] == 1) {
                // SOUTH
                maze[y][x].south = false;
                maze[y + 1][x].north = false;
                y++;
            }
            generate(x, y);
        }
    }

    private int[] direction(int x, int y) {
        double denominator = 1;
        int[] direction = new int[]{-1, -1};
        if (!maze[y][x + 1].traveled && Math.random() < 1 / denominator) {
            denominator++;
            direction[0] = 1;
            direction[1] = 0;
        }
        if (!maze[y + 1][x].traveled && Math.random() < 1 / denominator) {
            denominator++;
            direction[0] = 0;
            direction[1] = 1;
        }
        if (!maze[y][x - 1].traveled && Math.random() < 1 / denominator) {
            denominator++;
            direction[0] = -1;
            direction[1] = 0;
        }
        if (!maze[y - 1][x].traveled && Math.random() < 1 / denominator) {
            direction[0] = 0;
            direction[1] = -1;
        }
        return direction;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (MazeCell[] mazeCells : maze) {
            sb.append(Arrays.toString(mazeCells));
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    public MazeCell[][] getMaze() {
        return maze;
    }
}
