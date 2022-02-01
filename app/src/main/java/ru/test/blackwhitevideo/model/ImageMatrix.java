package ru.test.blackwhitevideo.model;

public class ImageMatrix {
    private short[] matrix;
    private int width;
    private int height;
    private int[] filter;

    public ImageMatrix(short[] matrix, int width, int height) {
        this.matrix = matrix;
        this.width = width;
        this.height = height;
    }

    public void colorMatrixToBlackAndWhite() {
        if (filter == null) {
            throw new NullPointerException("You must set filter for image matrix");
        }
        int newWidth = width - 2;
        int newHeight = height - 2;

        if (newWidth < 1 || newHeight < 1) {
            throw new IllegalArgumentException("Matrix must have width and height larger than 2");
        }

        short[] newMatrix = new short[newWidth * newHeight];
        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                int currentRow = y * width;
                int newColorValue = 0;
                for (int i = 0; i < 9; i++) {
                    int countOfNextRow = i / 3;
                    int countOfNextColumn = i % 3;
                    newColorValue += filter[i] * matrix[currentRow + width * countOfNextRow + x + countOfNextColumn];
                }

                newMatrix[y * newWidth + x] = (short) fixOutOfRangeRGBValue(newColorValue);
            }
        }
        setImageMatrix(newMatrix, newWidth, newHeight);
    }

    private int fixOutOfRangeRGBValue(int value) {
        if (value < 0) {
            value = -value;
        }
        if (value > 255) {
            value = 255;
        }

        return value;
    }

    public void setImageMatrix(short[] matrix, int width, int height) {
        setMatrix(matrix);
        setWidth(width);
        setHeight(height);
    }
    public short[] getMatrix() {
        return matrix;
    }

    private void setMatrix(short[] matrix) {
        if (matrix.length > 0)
            this.matrix = matrix;
    }

    public int getWidth() {
        return width;
    }

    private void setWidth(int width) {
        if (width > 0)
            this.width = width;
    }

    public int getHeight() {
        return height;
    }

    private void setHeight(int height) {
        if (height > 0)
            this.height = height;
    }

    public int[] getFilter() {
        return filter;
    }

    public void setFilter(int[] filter) {
        this.filter = filter;
    }
}
