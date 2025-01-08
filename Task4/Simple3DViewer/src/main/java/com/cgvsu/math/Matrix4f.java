package com.cgvsu.math;

public class Matrix4f {
    public float[][] mat;

    // Конструктор для создания матрицы из двумерного массива
    public Matrix4f(float[][] mat) {
        if (mat.length != 4 || mat[0].length != 4) {
            throw new IllegalArgumentException("Matrix must be 4x4");
        }
        this.mat = new float[4][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(mat[i], 0, this.mat[i], 0, 4);
        }
    }

    // Конструктор по умолчанию (создает нулевую матрицу)
    public Matrix4f() {
        this.mat = new float[4][4];
    }

    // Конструктор для создания диагональной матрицы
    public Matrix4f(float numeric) {
        this.mat = new float[4][4];
        for (int i = 0; i < 4; i++) {
            this.mat[i][i] = numeric;
        }
    }

    // Конструктор копирования
    public Matrix4f(Matrix4f other) {
        this.mat = new float[4][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(other.mat[i], 0, this.mat[i], 0, 4);
        }
    }

    // Сложение двух матриц
    public static Matrix4f add(Matrix4f m1, Matrix4f m2) {
        Matrix4f res = new Matrix4f();
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                res.mat[y][x] = m1.mat[y][x] + m2.mat[y][x];
            }
        }
        return res;
    }

    // Вычитание двух матриц
    public static Matrix4f sub(Matrix4f m1, Matrix4f m2) {
        Matrix4f res = new Matrix4f();
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                res.mat[y][x] = m1.mat[y][x] - m2.mat[y][x];
            }
        }
        return res;
    }

    // Умножение двух матриц
    public static Matrix4f multiply(Matrix4f m1, Matrix4f m2) {
        Matrix4f res = new Matrix4f();
        for (int m1y = 0; m1y < 4; m1y++) {
            for (int m2x = 0; m2x < 4; m2x++) {
                float a = 0;
                for (int i = 0; i < 4; i++) {
                    a += m1.mat[m1y][i] * m2.mat[i][m2x];
                }
                res.mat[m1y][m2x] = a;
            }
        }
        return res;
    }

    // Транспонирование матрицы
    public void transpose() {
        for (int y = 0; y < 4; y++) {
            for (int x = y + 1; x < 4; x++) {
                float a = this.mat[y][x];
                this.mat[y][x] = this.mat[x][y];
                this.mat[x][y] = a;
            }
        }
    }

    public Matrix4f(float[] array) {
        if (array.length != 16) {
            throw new IllegalArgumentException("Array must have exactly 16 elements.");
        }
        this.mat = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.mat[i][j] = array[i * 4 + j];
            }
        }
    }

    // Вычисление определителя матрицы
    public float determinant() {
        float det = 0;
        for (int i = 0; i < 4; i++) {
            float[][] minor = getMinor(0, i);
            Matrix3f minorMatrix = new Matrix3f(minor);
            det += (float) (Math.pow(-1, i) * mat[0][i] * minorMatrix.determinant());
        }
        return det;
    }

    // Получение минора матрицы
    private float[][] getMinor(int row, int col) {
        float[][] minor = new float[3][3];
        int minorRow = 0;
        for (int i = 0; i < 4; i++) {
            if (i == row) continue;
            int minorCol = 0;
            for (int j = 0; j < 4; j++) {
                if (j == col) continue;
                minor[minorRow][minorCol] = mat[i][j];
                minorCol++;
            }
            minorRow++;
        }
        return minor;
    }
}