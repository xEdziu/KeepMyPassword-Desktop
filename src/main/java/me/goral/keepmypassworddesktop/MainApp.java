package me.goral.keepmypassworddesktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(MainApp.class.getResource("main-app-view.fxml"));
            Scene scene = new Scene(root);
            String css = MainApp.class.getResource("main.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.initStyle(StageStyle.DECORATED);
            stage.setTitle("Keep My Password");
            stage.setResizable(false);
            stage.setWidth(700);
            stage.setHeight(500);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch();
    }
}