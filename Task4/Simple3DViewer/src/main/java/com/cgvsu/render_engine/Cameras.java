package com.cgvsu.render_engine;

import com.cgvsu.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Cameras {

    private List<Camera> cameras = new ArrayList<>(); // Список камер
    private int currentCameraIndex = 0; // Индекс текущей камеры

    //параметры: 1) тоска положения камеры, 2)куда смотрит камера, 3)угол обзора, 4)соотношение сторон, 5)ближняя плоскость отсечения, 6)дальняя плоскость отсечения
    public void addCamera(Vector3f position, Vector3f target, float fov, float aspectRatio, float nearPlane, float farPlane) {
        Camera newCamera = new Camera(position, target, fov, aspectRatio, nearPlane, farPlane);
        cameras.add(newCamera);
    }

    // Метод для удаления камеры по индексу
    public void removeCamera(int index) {
        if (index >= 0 && index < cameras.size()) {
            cameras.remove(index);
            if (currentCameraIndex >= cameras.size()) {
                currentCameraIndex = cameras.size() - 1;
            }
        }
    }

    // Метод для переключения на следующую камеру
    public void switchToNextCamera() {
        if (cameras.size() > 0) {
            currentCameraIndex = (currentCameraIndex + 1) % cameras.size();
        }
    }

    // Метод для получения текущей камеры
    public Camera getCurrentCamera() {
        if (cameras.isEmpty()) {
            return null;
        }
        return cameras.get(currentCameraIndex);
    }

    public int getCurrentCameraIndex() {
        return currentCameraIndex;
    }
}
