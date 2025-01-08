package com.cgvsu.render_engine;

import com.cgvsu.math.*;

public class Camera {

    private Vector3f position; // Позиция камеры
    private Vector3f target;   // Цель камеры
    private float fov;         // Угол обзора (в радианах)
    private float aspectRatio; // Соотношение сторон
    private float nearPlane;   // Ближняя плоскость отсечения
    private float farPlane;    // Дальняя плоскость отсечения
    private Vector3f lightPosition; // Позиция источника света

    public Camera(
            final Vector3f position,
            final Vector3f target,
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        this.position = position;
        this.target = target;
        this.fov = fov;
        this.aspectRatio = aspectRatio;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
        this.lightPosition = new Vector3f(0, 0, 0); // Инициализация позиции света
    }

    public void setPosition(final Vector3f position) {
        this.position = position;
    }

    public void setTarget(final Vector3f target) {
        this.target = target;
    }

    public void setAspectRatio(final float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getTarget() {
        return target;
    }

    public void movePosition(final Vector3f translation) {
        this.position.add(translation);
    }

    public void moveTarget(final Vector3f translation) {
        this.target.add(translation); // Исправлено: добавляем translation, а не target
    }

    public Matrix4f getViewMatrix() {
        return GraphicConveyor.lookAt(position, target);
    }

    public Matrix4f getProjectionMatrix() {
        return GraphicConveyor.perspective(fov, aspectRatio, nearPlane, farPlane);
    }

    public Vector3f getLightPosition() {
        return lightPosition;
    }

    public void setLightPosition(Vector3f lightPosition) {
        this.lightPosition = lightPosition;
    }
}