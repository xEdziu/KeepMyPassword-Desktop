package me.goral.keepmypassworddesktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import me.goral.keepmypassworddesktop.controllers.MainAppController;
import me.goral.keepmypassworddesktop.util.AlertsUtil;
import me.goral.keepmypassworddesktop.util.ConfUtil;

import java.util.Locale;
import java.util.ResourceBundle;

public class MainApp extends Application {

    private static Stage guiStage;

    public static Stage getStage() {
        return guiStage;
    }

    public static void setStage(Stage stage) {
        guiStage = stage;
    }

    public static Locale loc = setLocale();
    public static ResourceBundle lang = setLanguageBundle(loc);

    /**
     * The function returns a Locale object that is set to the language specified in the configuration file
     *
     * @return The locale object.
     */
    public static Locale setLocale(){
        String lang = ConfUtil.getConfigLanguage();
        return new Locale(lang);
    }

    /**
     * This function returns a ResourceBundle object that contains the localized strings for the given locale
     *
     * @param loc The locale of the language you want to use.
     * @return The ResourceBundle object.
     */
    public static ResourceBundle setLanguageBundle(Locale loc){
        return ResourceBundle
                .getBundle("language", loc); //NON-NLS
    }

    @Override
    public void start(Stage stage) {

        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("layouts/main-app-view.fxml"));
            guiStage = stage;
            Parent root = loader.load();
            Scene scene = new Scene(root);

            if (ConfUtil.setWorkingDirectory() == 0) throw new Exception("Error while reading os directory");

            String css = MainApp.class.getResource("styles/main.css").toExternalForm();
            MainAppController mainController = loader.getController();
            scene.getStylesheets().add(css);
//            guiStage.initStyle(StageStyle.TRANSPARENT);
            guiStage.setResizable(false);
            guiStage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));
            guiStage.setWidth(750);
            guiStage.setHeight(500);
            guiStage.setScene(scene);
            guiStage.show();

            // center stage on screen
            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            guiStage.setX((primScreenBounds.getWidth() - guiStage.getWidth()) / 2);
            guiStage.setY((primScreenBounds.getHeight() - guiStage.getHeight()) / 2);
            mainController.handleAppRun();
        } catch (Exception e){
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}