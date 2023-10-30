package com.somerdin.snake;

public class MatrixUtil {
    public static void hilbert(Object[][] arr, Object fillVal, int x, int y,
                                int size) {
        if (size == 0) {
            return;
        }
        for (int i = 0; i < size; i++) {
            arr[size / 2][i + x] = fillVal;
        }
        for (int j = 0; j < size / 2 + 1; j++) {
            arr[size / 2 + j][x + size / 2] = fillVal;
        }
        hilbert(arr, fillVal, x, y, size / 2);
        hilbert(arr, fillVal, x + size / 2 + 1, y, size / 2);
        if (size > 1) {
            Object[][] bottomLeft = copyFromArray(arr, y, x, size / 2);
            Object[][] bottomRight = copyFromArray(arr, y, x, size / 2);
            bottomLeft = rotateCW(bottomLeft);
            invert(bottomLeft, null, fillVal);
            bottomRight = rotateCCW(bottomRight);
            invert(bottomRight, null, fillVal);
            copy2dArray(arr, bottomLeft, y + size / 2 + 1, x);
            copy2dArray(arr, bottomRight, y + size / 2 + 1, x + size / 2 + 1);
        }
    }

    private static void invert(Object[][] array, Object o1, Object o2) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length; j++) {
                if (array[i][j] == o1) {
                    array[i][j] = o2;
                } else {
                    array[i][j] = o1;
                }
            }
        }
    }

    private static Object[][] copyFromArray(Object[][] array, int i, int j,
                                         int size) {
        Object[][] copy = new Object[size][size];
        for (int m = 0; m < size; m++) {
            for (int n = 0; n < size; n++) {
                copy[m][n] = array[i + m][j + n];
            }
        }
        return copy;
    }

    private static void copy2dArray(Object[][] receiver, Object[][] toCopy, int i,
                                    int j) {
        for (int m = 0; m < toCopy.length; m++) {
            for (int n = 0; n < toCopy.length; n++) {
                receiver[i + m][j + n] = toCopy[m][n];
            }
        }
    }

    private static Object[][] rotateCW(Object[][] mat) {
        final int M = mat.length;
        final int N = mat[0].length;
        Object[][] ret = new Object[N][M];
        for (int r = 0; r < M; r++) {
            for (int c = 0; c < N; c++) {
                ret[c][M-1-r] = mat[r][c];
            }
        }
        return ret;
    }

    private static Object[][] rotateCCW(Object[][] mat) {
        final int M = mat.length;
        final int N = mat[0].length;
        Object[][] ret = new Object[N][M];
        for (int r = 0; r < M; r++) {
            for (int c = 0; c < N; c++) {
                ret[N - 1 - c][r] = mat[r][c];
            }
        }
        return ret;
    }
}
