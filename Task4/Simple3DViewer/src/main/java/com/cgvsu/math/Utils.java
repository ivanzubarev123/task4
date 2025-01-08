package com.cgvsu.math;

public class Utils {
    public static float[] calculateBarycentric(int x, int y, Point2f p1, Point2f p2, Point2f p3) {
        // Векторы сторон треугольника
        float v0x = p2.x - p1.x;
        float v0y = p2.y - p1.y;
        float v1x = p3.x - p1.x;
        float v1y = p3.y - p1.y;
        float v2x = x - p1.x;
        float v2y = y - p1.y;

        // Вычисляем скалярные произведения
        float dot00 = v0x * v0x + v0y * v0y;
        float dot01 = v0x * v1x + v0y * v1y;
        float dot02 = v0x * v2x + v0y * v2y;
        float dot11 = v1x * v1x + v1y * v1y;
        float dot12 = v1x * v2x + v1y * v2y;

        // Вычисляем барицентрические координаты
        float invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
        float beta = (dot11 * dot02 - dot01 * dot12) * invDenom;
        float gamma = (dot00 * dot12 - dot01 * dot02) * invDenom;
        float alpha = 1 - beta - gamma;

        return new float[]{alpha, beta, gamma};
    }
}