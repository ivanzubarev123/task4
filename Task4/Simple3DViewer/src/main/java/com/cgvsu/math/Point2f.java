package com.cgvsu.math;

public class Point2f {
    public float x, y;

    // Конструктор по умолчанию (создает точку (0, 0))
    public Point2f() {
        this.x = 0;
        this.y = 0;
    }

    // Конструктор с координатами
    public Point2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // Конструктор копирования
    public Point2f(Point2f other) {
        this.x = other.x;
        this.y = other.y;
    }

    // Сложение с другой точкой
    public Point2f add(Point2f other) {
        return new Point2f(this.x + other.x, this.y + other.y);
    }

    // Вычитание другой точки
    public Point2f sub(Point2f other) {
        return new Point2f(this.x - other.x, this.y - other.y);
    }

    // Умножение на скаляр
    public Point2f mul(float scalar) {
        return new Point2f(this.x * scalar, this.y * scalar);
    }

    // Деление на скаляр
    public Point2f div(float scalar) {
        if (Math.abs(scalar) < Global.EPS) {
            throw new ArithmeticException("Division by zero is not allowed.");
        }
        return new Point2f(this.x / scalar, this.y / scalar);
    }

    // Длина вектора от начала координат до точки
    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    // Нормализация точки (приведение к единичной длине)
    public Point2f normalize() {
        float length = this.length();
        if (length < Global.EPS) {
            throw new ArithmeticException("Normalization of a zero vector is not allowed.");
        }
        return this.div(length);
    }

    // Скалярное произведение с другой точкой
    public float dot(Point2f other) {
        return this.x * other.x + this.y * other.y;
    }

    // Проверка на равенство с другой точкой
    public boolean equals(Point2f other) {
        return Math.abs(this.x - other.x) < Global.EPS &&
                Math.abs(this.y - other.y) < Global.EPS;
    }

    @Override
    public String toString() {
        return "Point2f(" + x + ", " + y + ")";
    }
}