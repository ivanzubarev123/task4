package com.cgvsu.render_engine;

import com.cgvsu.math.*;
public class GraphicConveyor {

    public static Matrix4f rotateScaleTranslate() {
        return new Matrix4f(1.0F); // Единичная матрица
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target) {
        return lookAt(eye, target, new Vector3f(0F, 1.0F, 0F));
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target, Vector3f up) {
        Vector3f resultZ = Vector3f.subtraction(target, eye).normal();
        Vector3f resultX = Vector3f.crossProduct(up, resultZ).normal();
        Vector3f resultY = Vector3f.crossProduct(resultZ, resultX).normal();

        float[] matrix = new float[]{
                resultX.x, resultY.x, resultZ.x, 0,
                resultX.y, resultY.y, resultZ.y, 0,
                resultX.z, resultY.z, resultZ.z, 0,
                -Vector3f.dotProduct(resultX, eye),
                -Vector3f.dotProduct(resultY, eye),
                -Vector3f.dotProduct(resultZ, eye),
                1
        };
        return new Matrix4f(matrix);
    }

    public static Matrix4f perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        Matrix4f result = new Matrix4f();
        float tangentMinusOnDegree = (float) (1.0F / (Math.tan(fov * 0.5F)));
        result.mat[0][0] = tangentMinusOnDegree / aspectRatio;
        result.mat[1][1] = tangentMinusOnDegree;
        result.mat[2][2] = (farPlane + nearPlane) / (farPlane - nearPlane);
        result.mat[2][3] = 1.0F;
        result.mat[3][2] = 2 * (nearPlane * farPlane) / (nearPlane - farPlane);
        return result;
    }

    public static Vector3f multiplyMatrix4ByVector3(final Matrix4f matrix, final Vector3f vertex) {
        final float x = (vertex.x * matrix.mat[0][0]) + (vertex.y * matrix.mat[1][0]) + (vertex.z * matrix.mat[2][0]) + matrix.mat[3][0];
        final float y = (vertex.x * matrix.mat[0][1]) + (vertex.y * matrix.mat[1][1]) + (vertex.z * matrix.mat[2][1]) + matrix.mat[3][1];
        final float z = (vertex.x * matrix.mat[0][2]) + (vertex.y * matrix.mat[1][2]) + (vertex.z * matrix.mat[2][2]) + matrix.mat[3][2];
        final float w = (vertex.x * matrix.mat[0][3]) + (vertex.y * matrix.mat[1][3]) + (vertex.z * matrix.mat[2][3]) + matrix.mat[3][3];
        return new Vector3f(x / w, y / w, z / w);
    }

    public static Point2f vertexToPoint(final Vector3f vertex, final int width, final int height) {
        float x = vertex.x * width + width / 2.0F;
        float y = -vertex.y * height + height / 2.0F;
        return new Point2f(x, y);
    }
}