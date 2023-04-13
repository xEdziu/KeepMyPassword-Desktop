package me.goral.keepmypassworddesktop;

import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.goral.keepmypassworddesktop.controllers.MainAppController;
import me.goral.keepmypassworddesktop.util.AlertsUtil;
import me.goral.keepmypassworddesktop.util.ConfUtil;

public class MainApp extends Application {

    private Stage guiStage;

    private static final int WINDOW_WIDTH = 750;
    private static final int WINDOW_HEIGHT = 500;

    @Override
    public void start(Stage stage) {
        try {
            guiStage = stage;

            if (ConfUtil.setWorkingDirectory() == 0) {
                throw new Exception("Error while reading os directory");
            }
            Locale locale = loadLocale();
            ResourceBundle resourceBundle = loadResourceBundle(locale);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/main-app-view.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            String css = getClass().getResource("styles/main.css").toExternalForm();
            scene.getStylesheets().add(css);

            guiStage.setTitle(resourceBundle.getString("appName"));
            guiStage.getIcons().add(new Image(getClass().getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));
            guiStage.setResizable(false);
            guiStage.initStyle(StageStyle.DECORATED);
            guiStage.setScene(scene);
            guiStage.setWidth(WINDOW_WIDTH);
            guiStage.setHeight(WINDOW_HEIGHT);

            MainAppController mainController = loader.getController();
            mainController.setResourceBundle(resourceBundle);
            mainController.handleAppRun();

            guiStage.show();
        } catch (Exception e) {
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
    }

    private Locale loadLocale() {
        String lang = ConfUtil.getConfigLanguage();
        return new Locale(lang);
    }

    private ResourceBundle loadResourceBundle(Locale locale) {
        return ResourceBundle.getBundle("language", locale, new UTF8Control());
    }

    public static void main(String[] args) {
        launch();
    }
}
