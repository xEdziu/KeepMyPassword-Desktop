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
import me.goral.keepmypassworddesktop.util.ConfUtil;

import java.util.Locale;
import java.util.ResourceBundle;

public class MainApp extends Application {

    private static Stage guiStage;

    public static Stage getStage() {
        return guiStage;
    }

    public static Locale loc = setLocale();
    public static ResourceBundle lang = setLanguageBundle(setLocale());

    /**
     * The function returns a Locale object that is set to the language specified in the configuration file
     *
     * @return The locale object.
     */
    public static Locale setLocale(){
        return new Locale(ConfUtil.getConfigLanguage());
    }

    /**
     * This function returns a ResourceBundle object that contains the localized strings for the given locale
     *
     * @param loc The locale of the language you want to use.
     * @return The ResourceBundle object.
     */
    public static ResourceBundle setLanguageBundle(Locale loc){
        return ResourceBundle
                .getBundle("/me/goral/keepmypassworddesktop/language.language", loc); //NON-NLS
    }

    @Override
    public void start(Stage stage) {

        System.out.println(ConfUtil.getConfigLanguage());
        System.out.println();
        System.out.println(lang.getLocale());

        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("layouts/main-app-view.fxml"));
            guiStage = stage;
            Parent root = loader.load();
            Scene scene = new Scene(root);

            if (ConfUtil.setWorkingDirectory() == 0) throw new Exception("Error while reading os directory");

            String css = MainApp.class.getResource("styles/main.css").toExternalForm();
            MainAppController mainController = loader.getController();
            scene.getStylesheets().add(css);
            guiStage.initStyle(StageStyle.DECORATED);
            guiStage.setTitle(lang.getString("appName"));
            guiStage.setResizable(false);
            guiStage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));
            guiStage.setWidth(750);
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