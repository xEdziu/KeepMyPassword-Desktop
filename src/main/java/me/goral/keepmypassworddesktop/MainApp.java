package me.goral.keepmypassworddesktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.goral.keepmypassworddesktop.util.ArgonUtil;
import me.goral.keepmypassworddesktop.util.AuthUtil;

import java.util.Arrays;
import java.util.Base64;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("main-app-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            String css = MainApp.class.getResource("styles/main.css").toExternalForm();
            MainAppController controller = loader.getController();
            scene.getStylesheets().add(css);
            stage.initStyle(StageStyle.DECORATED);
            stage.setTitle("Keep My Password");
            stage.setResizable(false);
            stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));
            stage.setWidth(700);
            stage.setHeight(500);
            stage.setScene(scene);
            stage.show();
            controller.changeBtnText();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch();
    }
}