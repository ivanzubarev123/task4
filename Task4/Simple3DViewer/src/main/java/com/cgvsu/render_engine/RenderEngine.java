package com.cgvsu.render_engine;

import com.cgvsu.math.*;
import javafx.scene.canvas.GraphicsContext;
import com.cgvsu.model.Model;

import static com.cgvsu.RasterizeTriangle.RasterizeTriangle.rasterizeTriangle;
import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {

    public static float[][] zBuffer;

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height,
            boolean drawWireframe,
            boolean useLighting) {

        Matrix4f modelMatrix = rotateScaleTranslate();
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        Matrix4f modelViewProjectionMatrix = new Matrix4f(modelMatrix);
        modelViewProjectionMatrix = Matrix4f.multiply(modelViewProjectionMatrix, viewMatrix);
        modelViewProjectionMatrix = Matrix4f.multiply(modelViewProjectionMatrix, projectionMatrix);

        initializeZBuffer(width, height);

        final int nPolygons = mesh.polygons.size();
        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            final int nVerticesInPolygon = mesh.polygons.get(polygonInd).getVertexIndices().size();

            for (int i = 1; i < nVerticesInPolygon - 1; i++) {

                Vector3f v1 = mesh.vertices.get(mesh.polygons.get(polygonInd).getVertexIndices().get(0));
                Vector3f v2 = mesh.vertices.get(mesh.polygons.get(polygonInd).getVertexIndices().get(i));
                Vector3f v3 = mesh.vertices.get(mesh.polygons.get(polygonInd).getVertexIndices().get(i + 1));

                Vector3f n1 = mesh.normals.get(mesh.polygons.get(polygonInd).getVertexIndices().get(0));
                Vector3f n2 = mesh.normals.get(mesh.polygons.get(polygonInd).getVertexIndices().get(i));
                Vector3f n3 = mesh.normals.get(mesh.polygons.get(polygonInd).getVertexIndices().get(i + 1));

                Vector3f v1Transformed = multiplyMatrix4ByVector3(modelViewProjectionMatrix, v1);
                Vector3f v2Transformed = multiplyMatrix4ByVector3(modelViewProjectionMatrix, v2);
                Vector3f v3Transformed = multiplyMatrix4ByVector3(modelViewProjectionMatrix, v3);

                if (useLighting) {
                    rasterizeTriangle(
                            graphicsContext,
                            v1Transformed,
                            v2Transformed,
                            v3Transformed,
                            n1,
                            n2,
                            n3,
                            camera.getLightPosition(),
                            width,
                            height
                    );
                } else {
                    // Рендеринг без освещения (статический цвет)
                    rasterizeTriangle(
                            graphicsContext,
                            v1Transformed,
                            v2Transformed,
                            v3Transformed,
                            new Vector3f(1, 1, 1), // Нормали не используются
                            new Vector3f(1, 1, 1),
                            new Vector3f(1, 1, 1),
                            camera.getLightPosition(),
                            width,
                            height
                    );
                }

                if (drawWireframe) {
                    // Отрисовка полигональной сетки
                    drawWireframe(graphicsContext, v1Transformed, v2Transformed, v3Transformed, width, height);
                }
            }
        }

        clearZBuffer(width, height);
    }

    private static void drawWireframe(GraphicsContext graphicsContext, Vector3f v1, Vector3f v2, Vector3f v3, int width, int height) {
        Point2f p1 = GraphicConveyor.vertexToPoint(v1, width, height);
        Point2f p2 = GraphicConveyor.vertexToPoint(v2, width, height);
        Point2f p3 = GraphicConveyor.vertexToPoint(v3, width, height);

        graphicsContext.setStroke(javafx.scene.paint.Color.BLACK);
        graphicsContext.strokeLine(p1.x, p1.y, p2.x, p2.y);
        graphicsContext.strokeLine(p2.x, p2.y, p3.x, p3.y);
        graphicsContext.strokeLine(p3.x, p3.y, p1.x, p1.y);
    }

    public static void initializeZBuffer(int width, int height) {
        zBuffer = new float[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                zBuffer[x][y] = Float.POSITIVE_INFINITY;
            }
        }
    }

    public static void clearZBuffer(int width, int height) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                zBuffer[x][y] = Float.POSITIVE_INFINITY;
            }
        }
    }
}