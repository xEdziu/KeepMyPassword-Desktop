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
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import me.goral.keepmypassworddesktop.MainApp;
import me.goral.keepmypassworddesktop.controllers.LoggedController;
import me.goral.keepmypassworddesktop.controllers.MainAppController;
import me.goral.keepmypassworddesktop.database.DatabaseHandler;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static me.goral.keepmypassworddesktop.util.PasswordGeneratorUtil.checkPasswordComplexity;

public class AlertsUtil {

    /**
     * Show an error dialog with a title, header, and body
     * 
     * @param errTitle The title of the error dialog.
     * @param errHeader The header text of the error dialog.
     * @param errBody The body of the error message.
     */
    public static void showErrorDialog(String errTitle, String errHeader, String errBody){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(errTitle);
        alert.setHeaderText(errHeader);
        alert.setContentText(errBody);
        alert.getButtonTypes().clear();
        alert.setGraphic(new ImageView(MainApp.class.getResource("/me/goral/keepmypassworddesktop/images/error-64.png").toString()));
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));
        alert.getDialogPane().getStylesheets().add(MainApp.class.getResource("styles/dialog.css").toExternalForm());
        ButtonType btnConfirm = new ButtonType("OK");
        alert.getDialogPane().getButtonTypes().add(btnConfirm);

        Node confirm = alert.getDialogPane().lookupButton(btnConfirm);
        confirm.getStyleClass().add("btn");
        alert.showAndWait();
    }

    /**
     * Show a dialog to confirm the deletion of all data
     */
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

    /**
     * Show a dialog to confirm logout
     */
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
    
    /**
     * Show an information dialog
     * 
     * @param infTitle The title of the dialog box.
     * @param infHeader The header text of the dialog.
     * @param infBody The body of the dialog.
     */
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

        ButtonType btnConfirm = new ButtonType("OK");
        alert.getDialogPane().getButtonTypes().add(btnConfirm);

        Node confirm = alert.getDialogPane().lookupButton(btnConfirm);
        confirm.getStyleClass().add("btn");

        alert.showAndWait();
    }

    /**
     * Show a dialog with the settings options
     */
    public static void showSettingsDialog(TableView<LoggedController.PasswordRow> tv, boolean s) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Settings");
        alert.setHeaderText("Select desired option");
        alert.getDialogPane().getStylesheets().add(MainApp.class.getResource("styles/dialog.css").toExternalForm());
        alert.setGraphic(new ImageView(MainApp.class.getResource("/me/goral/keepmypassworddesktop/images/settings-64.png").toString()));
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));

        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(cancel);

        Node cancelNode = alert.getDialogPane().lookupButton(cancel);

        cancelNode.getStyleClass().add("btn");

        GridPane grid = new GridPane();

        Button delAcc = new Button("Delete account");
        delAcc.getStyleClass().addAll("btn","optionsButton");
        Button delData = new Button("Delete data");
        delData.getStyleClass().addAll("btn","optionsButton");
        Button logout = new Button("Logout");
        logout.getStyleClass().addAll("btn","optionsButton");

        delAcc.setOnMouseClicked(mouseEvent -> {
            showDeleteAccountDialog();
            alert.close();
        });
        delData.setOnMouseClicked(mouseEvent -> {
            showDeleteDataDialog();
            LoggedController lc = new LoggedController();
            lc.refreshContentTable(tv, s);
        });

        logout.setOnMouseClicked(mouseEvent -> {
            showLogoutDialog();
            alert.close();
        });

        GridPane.setColumnIndex(delAcc, 0);
        GridPane.setRowIndex(delAcc, 0);
        grid.getChildren().add(delAcc);

        GridPane.setColumnIndex(delData, 0);
        GridPane.setRowIndex(delData, 1);
        grid.getChildren().add(delData);

        GridPane.setColumnIndex(logout, 0);
        GridPane.setRowIndex(logout, 2);
        grid.getChildren().add(logout);

        alert.getDialogPane().setContent(grid);

        alert.showAndWait();
    }

    /**
     * Show a dialog to confirm the deletion of the account
     */
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

    /**
     * It shows a dialog with the stacktrace of the exception.
     * 
     * @param e The exception that was thrown.
     */
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

    /**
     * It shows a dialog that allows the user to specify the parameters for the password generation.
     */
    public static void showGeneratePasswordDialog(){
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Generating new password");
        dialog.setHeaderText("Provide needed parameters:");
        dialog.setGraphic(new ImageView(MainApp.class.getResource("/me/goral/keepmypassworddesktop/images/add-key-64.png").toString()));
        dialog.getDialogPane().getStylesheets().add(MainApp.class.getResource("styles/dialog.css").toExternalForm());
        dialog.getDialogPane().getButtonTypes().clear();

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));

        ButtonType generateButtonType = new ButtonType("Generate", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().setAll(generateButtonType, cancelButtonType);
        Node addBtn = dialog.getDialogPane().lookupButton(generateButtonType);
        Node cancelBtn = dialog.getDialogPane().lookupButton(cancelButtonType);
        addBtn.getStyleClass().add("btn");
        cancelBtn.getStyleClass().add("btn");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField length = new TextField();
        length.setText("5");
        TextField lowerNum = new TextField();
        lowerNum.setText("1");
        TextField upperNum = new TextField();
        upperNum.setText("1");
        TextField digitNum = new TextField();
        digitNum.setText("1");
        TextField specialNum = new TextField();
        specialNum.setText("1");

        grid.add(new Label("Length"), 0, 0);
        grid.add(length, 1, 0);
        grid.add(new Label("Number of lower case characters"),0, 1);
        grid.add(lowerNum, 1,1);
        grid.add(new Label("Number of upper case characters"), 0, 2);
        grid.add(upperNum, 1, 2);
        grid.add(new Label("Number of digits"),0, 3);
        grid.add(digitNum, 1, 3);
        grid.add(new Label("Number of special characters"), 0, 4);
        grid.add(specialNum, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == generateButtonType){
                List<String> r = new ArrayList<>();
                r.add(length.getText());
                r.add(lowerNum.getText());
                r.add(upperNum.getText());
                r.add(digitNum.getText());
                r.add(specialNum.getText());
                return r;
            }
            return null;
        });

        Optional<List<String>> res = dialog.showAndWait();
        res.ifPresent(result -> {
            String len = result.get(0);
            if (len.length() == 0) {
                showErrorDialog("Error", "Invalid input", "Length parameter can't be empty!");
                return;
            }
            if (len.length() >= 6) {
                showErrorDialog("Error", "You can't generate that long password.", "Why would you need 6-digits long password anyway?");
                return;
            }
            String lower = result.get(1);
            if (lower.length() == 0) {
                showErrorDialog("Error", "Invalid input", "Lowe case number parameter can't be empty!");
                return;
            }
            String upper = result.get(2);
            if (upper.length() == 0) {
                showErrorDialog("Error", "Invalid input", "Upper case number parameter can't be empty!");
                return;
            }
            String digit = result.get(3);
            if (digit.length() == 0) {
                showErrorDialog("Error", "Invalid input", "Number of digits parameter can't be empty!");
                return;
            }
            String special = result.get(4);
            if (special.length() == 0) {
                showErrorDialog("Error", "Invalid input", "Special chars parameter can't be empty!");
                return;
            }

            int intLen, intLower, intUpper, intDigit, intSpecial;

            if (isInteger(len)) intLen = Integer.parseInt(len);
            else {
                showErrorDialog("Error Dialog", "Whoops!", "Length parameter is not an integer!");
                return;
            }

            if (isInteger(lower)) intLower = Integer.parseInt(lower);
            else {
                showErrorDialog("Error Dialog", "Whoops!", "Lower characters parameter is not an integer!");
                return;
            }

            if (isInteger(upper)) intUpper = Integer.parseInt(upper);
            else {
                showErrorDialog("Error Dialog", "Whoops!", "Upper parameter is not an integer!");
                return;
            }

            if (isInteger(digit)) intDigit = Integer.parseInt(digit);
            else {
                showErrorDialog("Error Dialog", "Whoops!", "Digits parameter is not an integer!");
                return;
            }


            if (isInteger(special)) intSpecial = Integer.parseInt(special);
            else {
                showErrorDialog("Error Dialog", "Whoops!", "Special chars parameter is not an integer!");
                return;
            }
            String pwd = PasswordGeneratorUtil.generatePassword(intLen, intLower, intUpper, intDigit, intSpecial);
            if (pwd != null) showGeneratedPasswordDialog(pwd);

        });
    }

    /**
     * It shows a dialog with the generated password.
     * 
     * @param pwd The password to be shown to the user.
     */
    public static void showGeneratedPasswordDialog(String pwd){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("New Password Dialog");
        alert.setHeaderText("Here is your new password!");
        alert.getButtonTypes().clear();
        alert.getDialogPane().getStylesheets().add(MainApp.class.getResource("styles/dialog.css").toExternalForm());
        alert.setGraphic(new ImageView(MainApp.class.getResource("/me/goral/keepmypassworddesktop/images/information-64.png").toString()));

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));

        ButtonType btnConfirm = new ButtonType("OK", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType btnCopy = new ButtonType("Copy");
        alert.getDialogPane().getButtonTypes().addAll(btnCopy, btnConfirm);

        Node confirm = alert.getDialogPane().lookupButton(btnConfirm);
        Node copy = alert.getDialogPane().lookupButton(btnCopy);
        confirm.getStyleClass().add("btn");
        copy.getStyleClass().add("btn");

        TextArea textArea = new TextArea(pwd);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxHeight(Double.MAX_VALUE);

        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.setMaxHeight(Double.MAX_VALUE);
        expContent.add(new Label("Your new password:"), 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpanded(true);
        alert.getDialogPane().setExpandableContent(expContent);

        alert.setResultConverter(dialogButton -> {
            if (dialogButton == btnCopy){
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putString(pwd);
                clipboard.setContent(clipboardContent);
                if (clipboard.hasString()){
                    System.out.println(clipboard.getString());
                }
            }
            return null;
        });

        alert.showAndWait();
    }

    /**
     * It shows a dialog to add new password to the database.
     * 
     * @param key The key to encrypt the password with.
     */
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
        Label pwdCheck = new Label("No password");
        pwdCheck.setTextFill(Color.web("#b3b3b3"));

        password.textProperty().addListener((observable, oldValue, newValue) -> {
            Pair<String, Color> res = checkPasswordComplexity(newValue);
            pwdCheck.setText(res.getKey());
            pwdCheck.setTextFill(res.getValue());
        });

        grid.add(new Label("Description"), 0, 0);
        grid.add(description, 1, 0);
        grid.add(new Label("Username"),0, 1);
        grid.add(username, 1,1);
        grid.add(new Label("Password"), 0, 2);
        grid.add(password, 1, 2);
        grid.add(pwdCheck, 1, 3);

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

    /**
     * It shows a dialog to update password in the database.
     * 
     * @param id id of the password to update
     * @param desc description of the password
     * @param login the username of the account
     * @param pwd the password to be updated
     * @param key The key used to encrypt the data.
     * @param iv the initialization vector used to encrypt the data.
     */
    public static void showUpdatePasswordDialog(int id, String desc, String login, String pwd, SecretKey key, String iv) {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Updating password");
        dialog.setHeaderText("Fulfill form to update password in your database:");
        dialog.setGraphic(new ImageView(MainApp.class.getResource("/me/goral/keepmypassworddesktop/images/add-key-64.png").toString()));
        dialog.getDialogPane().getStylesheets().add(MainApp.class.getResource("styles/dialog.css").toExternalForm());
        dialog.getDialogPane().getButtonTypes().clear();

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));

        ButtonType addButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(addButtonType, cancelButtonType);
        Node addBtn = dialog.getDialogPane().lookupButton(addButtonType);
        Node cancelBtn = dialog.getDialogPane().lookupButton(cancelButtonType);
        addBtn.getStyleClass().add("btn");
        cancelBtn.getStyleClass().add("btn");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField description = new TextField();
        description.setText(desc);
        TextField username = new TextField();
        username.setText(login);
        TextField password = new TextField();
        password.setText(pwd);
        Label pwdCheck = new Label();

        password.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("old: " +oldValue + " changed to: " + newValue);
            Pair<String, Color> res = checkPasswordComplexity(newValue);
            pwdCheck.setText(res.getKey());
            pwdCheck.setTextFill(res.getValue());
        });

        grid.add(new Label("Description"), 0, 0);
        grid.add(description, 1, 0);
        grid.add(new Label("Username"),0, 1);
        grid.add(username, 1,1);
        grid.add(new Label("Password"), 0, 2);
        grid.add(password, 1, 2);
        grid.add(pwdCheck, 1, 3);

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

            IvParameterSpec ivSpec = AESUtil.generateIv();

            try {
                String descEnc = Base64.getEncoder().encodeToString(AESUtil.encrypt(alg, descPlain, key, ivSpec).getBytes());
                String unameEnc = Base64.getEncoder().encodeToString(AESUtil.encrypt(alg, unamePlain, key, ivSpec).getBytes());
                String passEnc = Base64.getEncoder().encodeToString(AESUtil.encrypt(alg, passPlain, key, ivSpec).getBytes());

                String newIv = Base64.getEncoder().encodeToString(ivSpec.getIV());

                if (DatabaseHandler.updatePassword(descEnc, unameEnc, passEnc, newIv, id)) {
                    showInformationDialog("Confirmation Dialog", "Data updated",
                            "Your credentials has been updated");
                } else {
                    showErrorDialog("Error dialog", "Something wrong happened",
                            "Please report that error to github, so that developer can repair it as soon as possible");
                }
            } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException e) {
                showExceptionStackTraceDialog(e);
            }
        });
    }

    /**
     * Check if the string is a valid integer
     * 
     * @param str The string to be checked.
     * @return The method returns true if the string is an integer, false otherwise.
     */
    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

}
