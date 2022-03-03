package me.goral.keepmypassworddesktop.util;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import me.goral.keepmypassworddesktop.MainApp;
import me.goral.keepmypassworddesktop.controllers.MainAppController;
import me.goral.keepmypassworddesktop.database.DatabaseHandler;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
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
        alert.showAndWait();
    }

    public static void showDeleteDataDialog() {
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

    public static void showLogoutDialog() {
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

            try {
                FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("layouts/main-app-view.fxml"));
                Parent root = loader.load();

                MainAppController controller = loader.getController();
                controller.setIsLogged();
                Scene sc = new Scene(root);
                String css = MainApp.class.getResource("styles/main.css").toExternalForm();
                sc.getStylesheets().add(css);
                MainApp.getStage().setScene(sc);
            } catch (Exception e){
                showExceptionStackTraceDialog(e);
            }
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

    public static void showDeleteAccountDialog() {
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

            try {

                FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("layouts/main-app-view.fxml"));
                Parent root = loader.load();

                Scene sc = new Scene(root);
                String css = MainApp.class.getResource("styles/main.css").toExternalForm();
                sc.getStylesheets().add(css);
                MainApp.getStage().setScene(sc);
                ConfUtil.deleteConfFiles();
                MainAppController controller = loader.getController();
                controller.handleAppRun();

            } catch (Exception e){
                showExceptionStackTraceDialog(e);
            }

        }
    }

    public static void showExceptionStackTraceDialog(Exception e){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText("Oh no! Error!");
        alert.setContentText("Please report that error to github, so that developer can repair it as soon as possible:\n" +
                "https://github.com/xEdziu/KeepMyPassword-Desktop/issues/new/choose");
        alert.getButtonTypes().clear();
        alert.setGraphic(new ImageView(MainApp.class.getResource("/me/goral/keepmypassworddesktop/images/error-64.png").toString()));
        alert.getDialogPane().getStylesheets().add(MainApp.class.getResource("styles/dialog.css").toExternalForm());

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getDialogPane().getButtonTypes().add(okButtonType);
        Node okBtn = alert.getDialogPane().lookupButton(okButtonType);
        okBtn.getStyleClass().add("btn");

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("Exception stacktrace: ");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    public static void showAddPasswordDialog(SecretKey key) {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Adding new password");
        dialog.setHeaderText("Fulfill form to add password to your database:");
        dialog.setGraphic(new ImageView(MainApp.class.getResource("/me/goral/keepmypassworddesktop/images/add-key-64.png").toString()));
        dialog.getDialogPane().getStylesheets().add(MainApp.class.getResource("styles/dialog.css").toExternalForm());
        dialog.getDialogPane().getButtonTypes().clear();

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(addButtonType, cancelButtonType);
        Node addBtn = dialog.getDialogPane().lookupButton(addButtonType);
        Node cancelBtn = dialog.getDialogPane().lookupButton(cancelButtonType);
        addBtn.getStyleClass().add("btn");
        cancelBtn.getStyleClass().add("btn");
        addBtn.setDisable(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField description = new TextField();
        description.setPromptText("Description");
        TextField username = new TextField();
        username.setPromptText("Username");
        TextField password = new TextField();
        password.setPromptText("Password");

        grid.add(new Label("Description"), 0, 0);
        grid.add(description, 1, 0);
        grid.add(new Label("Username"),0, 1);
        grid.add(username, 1,1);
        grid.add(new Label("Password"), 0, 2);
        grid.add(password, 1, 2);

        username.textProperty().addListener(((observable, oldV, newV) -> addBtn.setDisable(newV.trim().isEmpty())));

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(description::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType){
                List<String> r = new ArrayList<>();
                r.add(description.getText());
                r.add(username.getText());
                r.add(password.getText());
                return r;
            }
            return null;
        });

        Optional<List<String>> res = dialog.showAndWait();
        res.ifPresent(result -> {
            String descPlain = result.get(0);
            String unamePlain = result.get(1);
            String passPlain = result.get(2);
            String alg = "AES/CBC/PKCS5Padding";

            IvParameterSpec iv = AESUtil.generateIv();

            try {
                String descEnc = Base64.getEncoder().encodeToString(AESUtil.encrypt(alg, descPlain, key, iv).getBytes());
                String unameEnc = Base64.getEncoder().encodeToString(AESUtil.encrypt(alg, unamePlain, key, iv).getBytes());
                String passEnc = Base64.getEncoder().encodeToString(AESUtil.encrypt(alg, passPlain, key, iv).getBytes());

                String ivString = Base64.getEncoder().encodeToString(iv.getIV());

                if (DatabaseHandler.insertPassword(descEnc, unameEnc, passEnc, ivString)) {
                    showInformationDialog("Confirmation Dialog", "Password added",
                            "Your password has been added to database");
                } else {
                    showErrorDialog("Error dialog", "Something wrong happened",
                            "Please report that error to github, so that developer can repair it as soon as possible");
                }
            } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException e) {
                showExceptionStackTraceDialog(e);
            }
        });
    }
}
