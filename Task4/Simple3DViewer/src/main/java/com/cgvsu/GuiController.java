package com.cgvsu;

import com.cgvsu.normal.FindNormals;
import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.render_engine.Cameras;
import com.cgvsu.render_engine.RenderEngine;
import com.cgvsu.triangulation.Triangulation;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.scene.control.Button;


import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.cgvsu.math.Vector3f;
import javafx.scene.control.*;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;

public class GuiController {

    @FXML
    private CheckBox useLight;

    @FXML
    private CheckBox drawLines;

    private Cameras cameras = new Cameras();

    private boolean drawWireframe = true; // Флаг для отрисовки полигональной сетки
    private boolean useLighting = false; //Флаг для включения освещения

    @FXML
    private void onCanvasClick(MouseEvent mouseEvent) {
        canvas.requestFocus();
    }


    @FXML
    private ListView<String> listOfModels;

    @FXML
    private ListView<CheckBox> listOfModelsCheckBoxes;


    @FXML
    ScrollPane scrollPaneMain;

    List<Model> moreModels = new ArrayList<>();

    final private float TRANSLATION = 0.5F;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    private Model mesh = null;

    private Camera camera = new Camera(
            new Vector3f(0, 00, 100),
            new Vector3f(0, 0, 0),
            1.0F, 1, 0.01F, 100);

    private Timeline timeline;

    @FXML
    private void initialize() {
        // Добавляем несколько камер(Я не придумал как задавать положение камер в интерфейсе), методы для удаления добавления и текущей камеры находятся в классе Cameras в папке render_engine
        cameras.addCamera(new Vector3f(0, 0, 100), new Vector3f(0, 0, 0), 1.0F, 1, 0.01F, 100);
        cameras.addCamera(new Vector3f(50, 0, 100), new Vector3f(0, 0, 0), 1.0F, 1, 0.01F, 100);
        cameras.addCamera(new Vector3f(-50, 0, 100), new Vector3f(0, 0, 0), 1.0F, 1, 0.01F, 100);

        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue() - scrollPaneMain.getWidth()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        drawLines.setSelected(drawWireframe);

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);

            // Используем текущую камеру для рендеринга
            Camera currentCamera = cameras.getCurrentCamera();
            if (currentCamera != null) {
                currentCamera.setAspectRatio((float) (width / height)); // Обновляем соотношение сторон для текущей камеры

                for (int i = 0; i < moreModels.size(); i++) {
                    RenderEngine.render(
                            canvas.getGraphicsContext2D(),
                            currentCamera, // Используем текущую камеру
                            moreModels.get(i),
                            (int) width,
                            (int) height,
                            drawWireframe,
                            useLighting
                    );
                }
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }
    @FXML
    private void checkBoxUseLight(){
        useLighting = !useLighting;
        useLight.setSelected(useLighting);
    }
    @FXML
    private void checkBoxDrawLine(){
        drawWireframe = !drawWireframe;
        drawLines.setSelected(drawWireframe);
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            Model model = ObjReader.read(fileContent);

            Triangulation.triangulate(model);

            FindNormals.findNormals(model);

            moreModels.add(model);
        } catch (Exception exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Ошибка в чтении файла");
            alert.setContentText(exception.getMessage());
            alert.showAndWait();
        }

        listOfModels.getItems().add(file.getName());
        listOfModelsCheckBoxes.getItems().add(createCheckBox());
    }

    private CheckBox createCheckBox() {
        CheckBox checkBox = new CheckBox("Modified");
        checkBox.setFocusTraversable(false);
        checkBox.setSelected(true);
        return checkBox;
    }

    @FXML
    private void handleListOfModelsClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.SECONDARY) && !moreModels.isEmpty()) {
            int index = listOfModels.getSelectionModel().getSelectedIndex();
            moreModels.remove(index);
            listOfModels.getItems().remove(index);
            listOfModelsCheckBoxes.getItems().remove(index);
        }
    }

    private boolean[] getCheckBoxesInfo(ListView<CheckBox> listOfModelsCheckBoxes) {
        boolean[] isSelected = new boolean[listOfModelsCheckBoxes.getItems().size()];
        for (int i = 0; i < listOfModelsCheckBoxes.getItems().size(); i++) {
            CheckBox checkBox = listOfModelsCheckBoxes.getItems().get(i);
            isSelected[i] = checkBox.isSelected();
        }
        return isSelected;
    }

    @FXML
    private void onSaveModelAsMenuItemClick() {
        if (moreModels.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Ошибка в записи файла");
            alert.showAndWait();
        } else {
            FileChooser saveChooser = new FileChooser();
            saveChooser.setTitle("Save file as");
            saveChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("OBJ file", "*.obj"));
            File file = saveChooser.showSaveDialog(canvas.getScene().getWindow());
            String modelInfo = ObjWriter.write(moreModels, getCheckBoxesInfo(listOfModelsCheckBoxes));
            if (file != null) {
                try {
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(modelInfo);
                    fileWriter.close();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Ошибка в записи файла");
                    alert.showAndWait();
                }
            }
        }
    }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        Camera currentCamera = cameras.getCurrentCamera();
        if (currentCamera != null) {
            currentCamera.movePosition(new Vector3f(0, 0, -TRANSLATION));
        }
    }@FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        Camera currentCamera = cameras.getCurrentCamera();
        if (currentCamera != null) {
            currentCamera.movePosition(new Vector3f(0, 0, TRANSLATION));
        }
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        Camera currentCamera = cameras.getCurrentCamera();
        if (currentCamera != null) {
            currentCamera.movePosition(new Vector3f(TRANSLATION, 0, 0));
        }
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        Camera currentCamera = cameras.getCurrentCamera();
        if (currentCamera != null) {
            currentCamera.movePosition(new Vector3f(-TRANSLATION, 0, 0));
        }
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        Camera currentCamera = cameras.getCurrentCamera();
        if (currentCamera != null) {
            currentCamera.movePosition(new Vector3f(0, TRANSLATION, 0));
        }
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        Camera currentCamera = cameras.getCurrentCamera();
        if (currentCamera != null) {
            currentCamera.movePosition(new Vector3f(0, -TRANSLATION, 0));
        }
    }
    @FXML
    private void handleRemoveCamera(ActionEvent event) {
        int index = cameras.getCurrentCameraIndex();
        cameras.removeCamera(index);
        System.out.println("Camera at index " + index + " removed.");
    }

    @FXML
    private void handleSwitchToNextCamera(ActionEvent event) {
        cameras.switchToNextCamera();
        System.out.println("Switched to camera at index " + cameras.getCurrentCameraIndex());
    }

    @FXML
    private void handleGetCurrentCamera(ActionEvent event) {
        Camera currentCamera = cameras.getCurrentCamera();
        if (currentCamera != null) {
            System.out.println("Current camera: " + currentCamera);
        } else {
            System.out.println("No cameras available.");
        }
    }

    private boolean isDarkTheme = false; // Флаг для отслеживания текущей темы

    @FXML
    private void handleThemeToggle() {
        isDarkTheme = !isDarkTheme;

        // Переключаем тему
        if (isDarkTheme) {
            applyTheme("dark_style.css");
        } else {
            applyTheme("light_style.css");
        }
    }

    private void applyTheme(String theme) {
        // Удаляем текущие стили и добавляем новые
        anchorPane.getStylesheets().clear();
        anchorPane.getStylesheets().add(getClass().getResource("/com.cgvsu.styles/" + theme).toExternalForm());
    }
}