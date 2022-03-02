package me.goral.keepmypassworddesktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.goral.keepmypassworddesktop.controllers.MainAppController;
import me.goral.keepmypassworddesktop.util.AlertsUtil;

public class MainApp extends Application {

    private static Stage guiStage;

    public static Stage getStage() {
        return guiStage;
    }

    @Override
    public void start(Stage stage) {

        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("layouts/main-app-view.fxml"));
            guiStage = stage;
            Parent root = loader.load();
            Scene scene = new Scene(root);

            String css = MainApp.class.getResource("styles/main.css").toExternalForm();
            MainAppController mainController = loader.getController();
            scene.getStylesheets().add(css);
            guiStage.initStyle(StageStyle.DECORATED);
            guiStage.setTitle("Keep My Password");
            guiStage.setResizable(false);
            guiStage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));
            guiStage.setWidth(700);
            guiStage.setHeight(500);
            guiStage.setScene(scene);
            guiStage.show();
            mainController.handleAppRun();
        } catch (Exception e){
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}