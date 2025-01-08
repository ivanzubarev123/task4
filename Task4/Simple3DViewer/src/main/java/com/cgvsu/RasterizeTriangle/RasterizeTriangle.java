package com.cgvsu.RasterizeTriangle;

import com.cgvsu.math.*;
import com.cgvsu.render_engine.GraphicConveyor;
import com.cgvsu.render_engine.RenderEngine;
import javafx.scene.canvas.GraphicsContext;

import static com.cgvsu.math.Utils.calculateBarycentric;

public class RasterizeTriangle {
    public static void rasterizeTriangle(
            GraphicsContext graphicsContext,
            Vector3f v1, Vector3f v2, Vector3f v3,
            Vector3f n1, Vector3f n2, Vector3f n3,
            Vector3f lightPosition, int width, int height) {

        Point2f p1 = GraphicConveyor.vertexToPoint(v1, width, height);
        Point2f p2 = GraphicConveyor.vertexToPoint(v2, width, height);
        Point2f p3 = GraphicConveyor.vertexToPoint(v3, width, height);

        int minX = (int) Math.max(0, Math.min(p1.x, Math.min(p2.x, p3.x)));
        int maxX = (int) Math.min(width - 1, Math.max(p1.x, Math.max(p2.x, p3.x)));
        int minY = (int) Math.max(0, Math.min(p1.y, Math.min(p2.y, p3.y)));
        int maxY = (int) Math.min(height - 1, Math.max(p1.y, Math.max(p2.y, p3.y)));

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (isPointInTriangle(x, y, p1, p2, p3)) {
                    float[] barycentric = calculateBarycentric(x, y, p1, p2, p3);

                    float z = interpolateZ(x, y, v1, v2, v3, p1, p2, p3);

                    if (z < RenderEngine.zBuffer[x][y]) {
                        RenderEngine.zBuffer[x][y] = z;

                        Vector3f normal = new Vector3f(
                                barycentric[0] * n1.x + barycentric[1] * n2.x + barycentric[2] * n3.x,
                                barycentric[0] * n1.y + barycentric[1] * n2.y + barycentric[2] * n3.y,
                                barycentric[0] * n1.z + barycentric[1] * n2.z + barycentric[2] * n3.z
                        );
                        normal.normal();

                        Vector3f lightDir = new Vector3f(
                                lightPosition.x - (barycentric[0] * v1.x + barycentric[1] * v2.x + barycentric[2] * v3.x),
                                lightPosition.y - (barycentric[0] * v1.y + barycentric[1] * v2.y + barycentric[2] * v3.y),
                                lightPosition.z - (barycentric[0] * v1.z + barycentric[1] * v2.z + barycentric[2] * v3.z)
                        );
                        lightDir.normal();

                        float intensity = Math.max(0, Vector3f.dotProduct(normal, lightDir));

                        int baseColor = 0xFFFFFFFF;
                        int shadedColor = applyLighting(baseColor, intensity);


                        graphicsContext.getPixelWriter().setArgb(x, y, shadedColor);
                    }
                }
            }
        }
    }

    private static int applyLighting(int color, float intensity) {
        int a = (color >> 24) & 0xFF;
        int r = (int) (((color >> 16) & 0xFF) * intensity);
        int g = (int) (((color >> 8) & 0xFF) * intensity);
        int b = (int) ((color & 0xFF) * intensity);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static boolean isPointInTriangle(int x, int y, Point2f p1, Point2f p2, Point2f p3) {
        float alpha = ((p2.y - p3.y) * (x - p3.x) + (p3.x - p2.x) * (y - p3.y)) /
                ((p2.y - p3.y) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.y - p3.y));
        float beta = ((p3.y - p1.y) * (x - p3.x) + (p1.x - p3.x) * (y - p3.y)) /
                ((p2.y - p3.y) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.y - p3.y));
        float gamma = 1.0f - alpha - beta;

        return alpha >= 0 && beta >= 0 && gamma >= 0;
    }

    private static float interpolateZ(int x, int y, Vector3f v1, Vector3f v2, Vector3f v3, Point2f p1, Point2f p2, Point2f p3) {
        float alpha = ((p2.y - p3.y) * (x - p3.x) + (p3.x - p2.x) * (y - p3.y)) /
                ((p2.y - p3.y) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.y - p3.y));
        float beta = ((p3.y - p1.y) * (x - p3.x) + (p1.x - p3.x) * (y - p3.y)) /
                ((p2.y - p3.y) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.y - p3.y));
        float gamma = 1.0f - alpha - beta;

        return alpha * v1.z + beta * v2.z + gamma * v3.z;
    }
}