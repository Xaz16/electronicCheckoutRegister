package edu.istu.achipiga;

import edu.istu.achipiga.services.ViewsService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class ElectronicCheckoutRegister extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        DatabaseInitializer.initializeDatabase();
        
        ViewsService.getInstance().setPrimaryStage(stage);

        Parent root = FXMLLoader.load(getClass().getResource("views/Main.fxml"));
        Scene scene = new Scene(root, 1200, 1000);
        stage.setTitle("Электронная касса");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}