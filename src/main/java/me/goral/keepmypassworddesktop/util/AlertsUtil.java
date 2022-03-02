package me.goral.keepmypassworddesktop.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import me.goral.keepmypassworddesktop.MainApp;
import me.goral.keepmypassworddesktop.controllers.MainAppController;
import me.goral.keepmypassworddesktop.database.DatabaseHandler;

import java.io.IOException;
import java.util.Optional;

public class AlertsUtil {

    public static void showErrorDialog(String errTitle, String errHeader, String errBody){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(errTitle);
        alert.setHeaderText(errHeader);
        alert.setContentText(errBody);
        alert.getButtonTypes().clear();
        alert.setGraphic(new ImageView(MainApp.class.getResource("/me/goral/keepmypassworddesktop/images/error-64.png").toString()));
        alert.getDialogPane().getStylesheets().add(MainApp.class.getResource("styles/dialog.css").toExternalForm());

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getDialogPane().getButtonTypes().add(okButtonType);
        Node okBtn = alert.getDialogPane().lookupButton(okButtonType);
        okBtn.getStyleClass().add("btn");

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));

        alert.showAndWait();
    }

    public static void showDeleteDataDialog() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting all data");
        alert.setHeaderText("You are about to wipe out all your data");
        alert.setContentText("Are you sure?");
        alert.getButtonTypes().clear();
        alert.getDialogPane().getStylesheets().add(MainApp.class.getResource("styles/dialog.css").toExternalForm());
        alert.setGraphic(new ImageView(MainApp.class.getResource("/me/goral/keepmypassworddesktop/images/warning-64.png").toString()));
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));

        ButtonType confirm = new ButtonType("Wipe data", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(confirm, cancel);

        Node btnConfirm = alert.getDialogPane().lookupButton(confirm);
        Node btnCancel = alert.getDialogPane().lookupButton(cancel);

        btnConfirm.getStyleClass().add("btn");
        btnCancel.getStyleClass().add("btn");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == confirm) {
            DatabaseHandler.truncateData();
            showInformationDialog("Information Dialog", "Data cleared", "All your passwords have been deleted.\n" +
                    "Have a great day!");
        }
    }


    public static void showLogoutDialog() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logging out");
        alert.setHeaderText("You are about to log out");
        alert.setContentText("Are you sure?");
        alert.getButtonTypes().clear();
        alert.getDialogPane().getStylesheets().add(MainApp.class.getResource("styles/dialog.css").toExternalForm());
        alert.setGraphic(new ImageView(MainApp.class.getResource("/me/goral/keepmypassworddesktop/images/logout-64.png").toString()));
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));

        ButtonType confirm = new ButtonType("Log out", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(confirm, cancel);

        Node btnConfirm = alert.getDialogPane().lookupButton(confirm);
        Node btnCancel = alert.getDialogPane().lookupButton(cancel);

        btnConfirm.getStyleClass().add("btn");
        btnCancel.getStyleClass().add("btn");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == confirm){

            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("layouts/main-app-view.fxml"));
            Parent root = loader.load();

            MainAppController controller = loader.getController();
            controller.setIsLogged();
            Scene sc = new Scene(root);
            String css = MainApp.class.getResource("styles/main.css").toExternalForm();
            sc.getStylesheets().add(css);
            MainApp.getStage().setScene(sc);

        }
    }
    
    public static void showInformationDialog(String infTitle, String infHeader, String infBody){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(infTitle);
        alert.setHeaderText(infHeader);
        alert.setContentText(infBody);
        alert.getButtonTypes().clear();
        alert.getDialogPane().getStylesheets().add(MainApp.class.getResource("styles/dialog.css").toExternalForm());
        alert.setGraphic(new ImageView(MainApp.class.getResource("/me/goral/keepmypassworddesktop/images/information-64.png").toString()));

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));

        ButtonType btnConfirm = new ButtonType("Great");
        alert.getDialogPane().getButtonTypes().add(btnConfirm);

        Node confirm = alert.getDialogPane().lookupButton(btnConfirm);
        confirm.getStyleClass().add("btn");

        alert.showAndWait();
    }

    public static void showDeleteAccountDialog() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting account");
        alert.setHeaderText("You are about to delete your whole account");
        alert.setContentText("Are you sure?");
        alert.getButtonTypes().clear();
        alert.getDialogPane().getStylesheets().add(MainApp.class.getResource("styles/dialog.css").toExternalForm());
        alert.setGraphic(new ImageView(MainApp.class.getResource("/me/goral/keepmypassworddesktop/images/warning-64.png").toString()));
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));

        ButtonType confirm = new ButtonType("Delete account", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(confirm, cancel);

        Node btnConfirm = alert.getDialogPane().lookupButton(confirm);
        Node btnCancel = alert.getDialogPane().lookupButton(cancel);

        btnConfirm.getStyleClass().add("btn");
        btnCancel.getStyleClass().add("btn");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == confirm){

            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("layouts/main-app-view.fxml"));
            Parent root = loader.load();

            Scene sc = new Scene(root);
            String css = MainApp.class.getResource("styles/main.css").toExternalForm();
            sc.getStylesheets().add(css);
            MainApp.getStage().setScene(sc);
            ConfUtil.deleteConfFiles();
            MainAppController controller = loader.getController();
            controller.handleAppRun();
        }
    }
}
