package edu.istu.achipiga.services;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewsService {
    private static ViewsService instance;
    private Stage primaryStage;

    private ViewsService() {}

    public static ViewsService getInstance() {
        if (instance == null) {
            instance = new ViewsService();
        }
        return instance;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void loadView(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent view = loader.load();
        primaryStage.getScene().setRoot(view);
    }

    public <T> T loadViewAndGetController(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.load();
        return loader.getController();
    }

    public <T> T loadViewAndGetController(String fxmlPath, Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent view = loader.load();
        stage.getScene().setRoot(view);
        return loader.getController();
    }

    public Parent loadViewAndGetRoot(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        return loader.load();
    }
} 