package me.goral.keepmypassworddesktop.util;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import static me.goral.keepmypassworddesktop.MainApp.loc;
import static me.goral.keepmypassworddesktop.util.PasswordGeneratorUtil.checkPasswordComplexity;

public class AlertsUtil {

    static LanguageConverter langProcess = new LanguageConverter();

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
        confirm.getStyleClass().add("btnConfirm");//NON-NLS
        alert.showAndWait();
    }

    /**
     * Show a dialog to confirm the deletion of all data
     */
    public static void showDeleteDataDialog(TableView<LoggedController.PasswordRow> tv, boolean s) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(MainApp.lang.getString("deleting.all.data"));
        alert.setHeaderText(MainApp.lang.getString("wiping-data-header"));
        alert.setContentText(MainApp.lang.getString("confirmation-question"));
        alert.getButtonTypes().clear();
        alert.getDialogPane().getStylesheets().add(MainApp.class.getResource("styles/dialog.css").toExternalForm());
        alert.setGraphic(new ImageView(MainApp.class.getResource("/me/goral/keepmypassworddesktop/images/warning-64.png").toString()));
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));

        ButtonType confirm = new ButtonType(MainApp.lang.getString("wipe.data.button"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType(MainApp.lang.getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(confirm, cancel);

        Node btnConfirm = alert.getDialogPane().lookupButton(confirm);
        Node btnCancel = alert.getDialogPane().lookupButton(cancel);

        btnConfirm.getStyleClass().add("btnConfirm");//NON-NLS
        btnCancel.getStyleClass().add("btnCancel");//NON-NLS

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == confirm) {
            DatabaseHandler.truncateData();
            LoggedController lc = new LoggedController();
            lc.refreshContentTable(tv, s);
            showInformationDialog(MainApp.lang.getString("information.dialog"), MainApp.lang.getString("data.cleared"),
                    MainApp.lang.getString("deleted-passwords-greeting"));
        }
    }

    /**
     * Show a dialog to confirm logout
     */
    public static void showLogoutDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(MainApp.lang.getString("logging.out"));
        alert.setHeaderText(MainApp.lang.getString("you.are.about.to.log.out"));
        alert.setContentText(MainApp.lang.getString("confirmation-question"));
        alert.getButtonTypes().clear();
        alert.getDialogPane().getStylesheets().add(MainApp.class.getResource("styles/dialog.css").toExternalForm());
        alert.setGraphic(new ImageView(MainApp.class.getResource("/me/goral/keepmypassworddesktop/images/logout-64.png").toString()));
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));

        ButtonType confirm = new ButtonType(MainApp.lang.getString("log.out"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType(MainApp.lang.getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(confirm, cancel);

        Node btnConfirm = alert.getDialogPane().lookupButton(confirm);
        Node btnCancel = alert.getDialogPane().lookupButton(cancel);

        btnConfirm.getStyleClass().add("btnConfirm");//NON-NLS
        btnCancel.getStyleClass().add("btnCancel");//NON-NLS

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
        confirm.getStyleClass().add("btnConfirm");//NON-NLS

        alert.showAndWait();
    }

    /**
     * This function shows a dialog that allows the user to change the language of the application
     */
    public static void showChangeLanguageDialog(){
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(MainApp.lang.getString("change.language"));
        dialog.setHeaderText(MainApp.lang.getString("change.language.of.application"));
        dialog.setContentText("");
        dialog.getDialogPane().getButtonTypes().clear();
        dialog.getDialogPane().getStylesheets().add(MainApp.class.getResource("styles/dialog.css").toExternalForm());
        dialog.setGraphic(new ImageView(MainApp.class.getResource("/me/goral/keepmypassworddesktop/images/information-64.png").toString()));
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));

        ButtonType save = new ButtonType(MainApp.lang.getString("save"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType(MainApp.lang.getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().setAll(save, cancel);

        Node btnSave = dialog.getDialogPane().lookupButton(save);
        Node btnCancel = dialog.getDialogPane().lookupButton(cancel);

        btnSave.getStyleClass().add("btnConfirm");//NON-NLS
        btnCancel.getStyleClass().add("btnCancel");//NON-NLS

        Label label = new Label(MainApp.lang.getString("choose-your-language-prompt"));

        ObservableList<String> options = ConfUtil.readLanguages();
        //TODO: options are locale-codes, there is a need to convert them to language
        ObservableList<String> optionsLanguage = FXCollections.observableArrayList();
        for (String locale : options){
            optionsLanguage.add(langProcess.convertToLanguage(locale));
        }
        final ComboBox<String> languageBox = new ComboBox<>(optionsLanguage);
        languageBox.getSelectionModel().selectFirst();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(label, 0, 0);
        grid.add(languageBox, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == save){
                return languageBox.getValue();
            }
            return null;
        });

        Optional<String> res = dialog.showAndWait();

        res.ifPresent(result -> {
            //TODO: change language to locale-code
            String locale = langProcess.convertToLocale(result);
            ConfUtil.changeLanguage(locale);
            loc = MainApp.setLocale();
            MainApp.lang = MainApp.setLanguageBundle(loc);

            showInformationDialog(MainApp.lang.getString("success"), MainApp.lang.getString("changed-lang-success"),
                    MainApp.lang.getString("have-a-great-day"));

            try {
                FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("layouts/main-app-view.fxml"));
                Parent root = loader.load();

                Scene sc = new Scene(root);
                String css = MainApp.class.getResource("styles/main.css").toExternalForm();
                sc.getStylesheets().add(css);
                MainApp.getStage().setScene(sc);
                MainAppController controller = loader.getController();
                controller.handleAppRun();

            } catch (Exception e){
                showExceptionStackTraceDialog(e);
            }
        });

    }

    /**
     * Show a dialog to confirm the deletion of the account
     */
    public static void showDeleteAccountDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(MainApp.lang.getString("deleting.account"));
        alert.setHeaderText(MainApp.lang.getString("deleting-account-confirmation"));
        alert.setContentText(MainApp.lang.getString("confirmation-question"));
        alert.getButtonTypes().clear();
        alert.getDialogPane().getStylesheets().add(MainApp.class.getResource("styles/dialog.css").toExternalForm());
        alert.setGraphic(new ImageView(MainApp.class.getResource("/me/goral/keepmypassworddesktop/images/warning-64.png").toString()));
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));

        ButtonType confirm = new ButtonType(MainApp.lang.getString("delete.account"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType(MainApp.lang.getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(confirm, cancel);

        Node btnConfirm = alert.getDialogPane().lookupButton(confirm);
        Node btnCancel = alert.getDialogPane().lookupButton(cancel);

        btnConfirm.getStyleClass().add("btnConfirm");//NON-NLS
        btnCancel.getStyleClass().add("btnCancel");//NON-NLS

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
        alert.setTitle(MainApp.lang.getString("exception.dialog.title"));
        alert.setHeaderText(MainApp.lang.getString("oh.no.error"));
        alert.setContentText(MainApp.lang.getString("info-send-issue-to-gh"));
        alert.getButtonTypes().clear();
        alert.setGraphic(new ImageView(MainApp.class.getResource("/me/goral/keepmypassworddesktop/images/error-64.png").toString()));
        alert.getDialogPane().getStylesheets().add(MainApp.class.getResource("styles/dialog.css").toExternalForm());

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getDialogPane().getButtonTypes().add(okButtonType);
        Node okBtn = alert.getDialogPane().lookupButton(okButtonType);
        okBtn.getStyleClass().add("btnConfirm");//NON-NLS

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label(MainApp.lang.getString("exception.stacktrace"));

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
        dialog.setTitle(MainApp.lang.getString("generating.new.password"));
        dialog.setHeaderText(MainApp.lang.getString("provide.needed.parameters"));
        dialog.setGraphic(new ImageView(MainApp.class.getResource("/me/goral/keepmypassworddesktop/images/add-key-64.png").toString()));
        dialog.getDialogPane().getStylesheets().add(MainApp.class.getResource("styles/dialog.css").toExternalForm());
        dialog.getDialogPane().getButtonTypes().clear();

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));

        ButtonType generateButtonType = new ButtonType(MainApp.lang.getString("generate"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType(MainApp.lang.getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().setAll(generateButtonType, cancelButtonType);
        Node addBtn = dialog.getDialogPane().lookupButton(generateButtonType);
        Node cancelBtn = dialog.getDialogPane().lookupButton(cancelButtonType);
        addBtn.getStyleClass().add("btnConfirm");//NON-NLS
        cancelBtn.getStyleClass().add("btnCancel");//NON-NLS

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20,150,30,25));

        TextField length = new TextField();
        length.setText("5");
        length.setPrefSize(350.0, 42.0);
        TextField lowerNum = new TextField();
        lowerNum.setText("1");
        lowerNum.setPrefSize(175.0, 42.0);
        TextField upperNum = new TextField();
        upperNum.setText("1");
        upperNum.setPrefSize(175.0, 42.0);
        TextField digitNum = new TextField();
        digitNum.setText("1");
        digitNum.setPrefSize(350.0, 42.0);
        TextField specialNum = new TextField();
        specialNum.setText("1");
        specialNum.setPrefSize(350.0, 42.0);

        grid.add(new Label(MainApp.lang.getString("length")), 0, 0, 2, 1);
        grid.add(length, 0, 1, 2, 1);
        grid.add(new Label(MainApp.lang.getString("lower.case.num")),0, 2);
        grid.add(lowerNum, 0,3);
        grid.add(new Label(MainApp.lang.getString("upper.case.num")), 1, 2);
        grid.add(upperNum, 1, 3);
        grid.add(new Label(MainApp.lang.getString("digits.case.num")),0, 4, 2, 1);
        grid.add(digitNum, 0, 5, 2, 1);
        grid.add(new Label(MainApp.lang.getString("special.chars.num")), 0, 6, 2, 1);
        grid.add(specialNum, 0, 7, 2, 1);

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
                showErrorDialog(MainApp.lang.getString("error"), MainApp.lang.getString("invalid.input"),
                        MainApp.lang.getString("len-err-empty"));
                return;
            }
            if (len.length() >= 6) {
                showErrorDialog(MainApp.lang.getString("error"), MainApp.lang.getString("too-long-pwd"),
                        MainApp.lang.getString("too-long-password-error-desc"));
                return;
            }
            String lower = result.get(1);
            if (lower.length() == 0) {
                showErrorDialog(MainApp.lang.getString("error"), MainApp.lang.getString("invalid.input"),
                        MainApp.lang.getString("lower-case-empty-err"));
                return;
            }
            String upper = result.get(2);
            if (upper.length() == 0) {
                showErrorDialog(MainApp.lang.getString("error"), MainApp.lang.getString("invalid.input"),
                        MainApp.lang.getString("upper-case-empty-err"));
                return;
            }
            String digit = result.get(3);
            if (digit.length() == 0) {
                showErrorDialog(MainApp.lang.getString("error"), MainApp.lang.getString("invalid.input"),
                        MainApp.lang.getString("digits-number-empty-err"));
                return;
            }
            String special = result.get(4);
            if (special.length() == 0) {
                showErrorDialog(MainApp.lang.getString("error"), MainApp.lang.getString("invalid.input"),
                        MainApp.lang.getString("special-chars-empty-err"));
                return;
            }

            int intLen, intLower, intUpper, intDigit, intSpecial;

            if (isInteger(len)) intLen = Integer.parseInt(len);
            else {
                showErrorDialog(MainApp.lang.getString("error.dialog.title"), MainApp.lang.getString("whoops"),
                        MainApp.lang.getString("length-not-int-err"));
                return;
            }

            if (isInteger(lower)) intLower = Integer.parseInt(lower);
            else {
                showErrorDialog(MainApp.lang.getString("error.dialog.title"), MainApp.lang.getString("whoops"),
                        MainApp.lang.getString("lower-case-not-int-err"));
                return;
            }

            if (isInteger(upper)) intUpper = Integer.parseInt(upper);
            else {
                showErrorDialog(MainApp.lang.getString("error.dialog.title"), MainApp.lang.getString("whoops"),
                        MainApp.lang.getString("upper-case-not-int-err"));
                return;
            }

            if (isInteger(digit)) intDigit = Integer.parseInt(digit);
            else {
                showErrorDialog(MainApp.lang.getString("error.dialog.title"), MainApp.lang.getString("whoops"),
                        MainApp.lang.getString("digits-not-int-err"));
                return;
            }


            if (isInteger(special)) intSpecial = Integer.parseInt(special);
            else {
                showErrorDialog(MainApp.lang.getString("error.dialog.title"), MainApp.lang.getString("whoops"),
                        MainApp.lang.getString("special-chars-not-int-err"));
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
        alert.setTitle(MainApp.lang.getString("new.password.dialog"));
        alert.setHeaderText(MainApp.lang.getString("here.is.your.new.password"));
        alert.getButtonTypes().clear();
        alert.getDialogPane().getStylesheets().add(MainApp.class.getResource("styles/dialog.css").toExternalForm());
        alert.setGraphic(new ImageView(MainApp.class.getResource("/me/goral/keepmypassworddesktop/images/information-64.png").toString()));

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));

        ButtonType btnConfirm = new ButtonType("OK", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType btnCopy = new ButtonType(MainApp.lang.getString("copy"));
        alert.getDialogPane().getButtonTypes().addAll(btnCopy, btnConfirm);

        Node confirm = alert.getDialogPane().lookupButton(btnConfirm);
        Node copy = alert.getDialogPane().lookupButton(btnCopy);
        confirm.getStyleClass().add("btnConfirm");//NON-NLS
        copy.getStyleClass().add("btnAction");//NON-NLS

        TextArea textArea = new TextArea(pwd);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxHeight(Double.MAX_VALUE);

        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.setMaxHeight(Double.MAX_VALUE);
        expContent.add(new Label(MainApp.lang.getString("your.new.password.info")), 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpanded(true);
        alert.getDialogPane().setExpandableContent(expContent);

        alert.setResultConverter(dialogButton -> {
            if (dialogButton == btnCopy){
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putString(pwd);
                clipboard.setContent(clipboardContent);
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
        dialog.setTitle(MainApp.lang.getString("adding.new.password"));
        dialog.setHeaderText(MainApp.lang.getString("fulfill-form-info"));
        dialog.setGraphic(new ImageView(MainApp.class.getResource("/me/goral/keepmypassworddesktop/images/add-key-64.png").toString()));
        dialog.getDialogPane().getStylesheets().add(MainApp.class.getResource("styles/dialog.css").toExternalForm());
        dialog.getDialogPane().getButtonTypes().clear();

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));

        ButtonType addButtonType = new ButtonType(MainApp.lang.getString("add"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType(MainApp.lang.getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(addButtonType, cancelButtonType);
        Node addBtn = dialog.getDialogPane().lookupButton(addButtonType);
        Node cancelBtn = dialog.getDialogPane().lookupButton(cancelButtonType);
        addBtn.getStyleClass().add("btnConfirm");//NON-NLS
        cancelBtn.getStyleClass().add("btnCancel");//NON-NLS

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 50, 10, 50));

        TextField description = new TextField();
        description.setPromptText(MainApp.lang.getString("description"));
        description.setPrefSize(300.0, 42.0);
        TextField username = new TextField();
        username.setPromptText(MainApp.lang.getString("username"));
        username.setPrefSize(300.0, 42.0);
        TextField password = new TextField();
        password.setPromptText(MainApp.lang.getString("password"));
        password.setPrefSize(300.0, 42.0);
        Label pwdCheck = new Label(MainApp.lang.getString("no-password-info"));
        pwdCheck.setTextFill(Color.web("#b3b3b3"));//NON-NLS

        password.textProperty().addListener((observable, oldValue, newValue) -> {
            Pair<String, Color> res = checkPasswordComplexity(newValue);
            pwdCheck.setText(res.getKey());
            pwdCheck.setTextFill(res.getValue());
        });

        grid.add(new Label(MainApp.lang.getString("description")), 0, 0);
        grid.add(description, 0, 1);
        grid.add(new Label(MainApp.lang.getString("username")),0, 2);
        grid.add(username, 0,3);
        grid.add(new Label(MainApp.lang.getString("password")), 0, 4);
        grid.add(password, 0, 5);
        grid.add(pwdCheck, 0, 6);

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
            String alg = "AES/CBC/PKCS5Padding";//NON-NLS

            if (descPlain.isEmpty() && unamePlain.isEmpty() && passPlain.isEmpty()){
                showErrorDialog(MainApp.lang.getString("error.dialog"), MainApp.lang.getString("wait.a.minute.err"),
                        MainApp.lang.getString("all.inputs.are.empty"));
                return;
            }


            IvParameterSpec iv = AESUtil.generateIv();

            try {
                String descEnc = Base64.getEncoder().encodeToString(AESUtil.encrypt(alg, descPlain, key, iv).getBytes());
                String unameEnc = Base64.getEncoder().encodeToString(AESUtil.encrypt(alg, unamePlain, key, iv).getBytes());
                String passEnc = Base64.getEncoder().encodeToString(AESUtil.encrypt(alg, passPlain, key, iv).getBytes());

                String ivString = Base64.getEncoder().encodeToString(iv.getIV());

                if (DatabaseHandler.insertPassword(descEnc, unameEnc, passEnc, ivString)) {
                    showInformationDialog(MainApp.lang.getString("confirmation.dialog"), MainApp.lang.getString("password.added.info"),
                            MainApp.lang.getString("password-added-body"));
                } else {
                    showErrorDialog(MainApp.lang.getString("error.dialog"), MainApp.lang.getString("unknown-error"),
                            MainApp.lang.getString("info-send-issue-to-gh"));
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
     */
    public static void showUpdatePasswordDialog(int id, String desc, String login, String pwd, SecretKey key) {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle(MainApp.lang.getString("updating.password"));
        dialog.setHeaderText(MainApp.lang.getString("update-alert-header"));
        dialog.setGraphic(new ImageView(MainApp.class.getResource("/me/goral/keepmypassworddesktop/images/add-key-64.png").toString()));
        dialog.getDialogPane().getStylesheets().add(MainApp.class.getResource("styles/dialog.css").toExternalForm());
        dialog.getDialogPane().getButtonTypes().clear();

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));

        ButtonType addButtonType = new ButtonType(MainApp.lang.getString("update-button"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType(MainApp.lang.getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(addButtonType, cancelButtonType);
        Node addBtn = dialog.getDialogPane().lookupButton(addButtonType);
        Node cancelBtn = dialog.getDialogPane().lookupButton(cancelButtonType);
        addBtn.getStyleClass().add("btnConfirm");//NON-NLS
        cancelBtn.getStyleClass().add("btnCancel");//NON-NLS

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 50, 10, 50));

        TextField description = new TextField();
        description.setText(desc);
        description.setPrefSize(300.0, 42.0);
        TextField username = new TextField();
        username.setText(login);
        username.setPrefSize(300.0, 42.0);
        TextField password = new TextField();
        password.setText(pwd);
        password.setPrefSize(300.0, 42.0);
        Label pwdCheck = new Label();

        password.textProperty().addListener((observable, oldValue, newValue) -> {
            Pair<String, Color> res = checkPasswordComplexity(newValue);
            pwdCheck.setText(res.getKey());
            pwdCheck.setTextFill(res.getValue());
        });

        grid.add(new Label(MainApp.lang.getString("description")), 0, 0);
        grid.add(description, 0, 1);
        grid.add(new Label(MainApp.lang.getString("username")),0, 2);
        grid.add(username, 0,3);
        grid.add(new Label(MainApp.lang.getString("password")), 0, 4);
        grid.add(password, 0, 5);
        grid.add(pwdCheck, 0, 6);

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
            String alg = "AES/CBC/PKCS5Padding";//NON-NLS

            IvParameterSpec ivSpec = AESUtil.generateIv();

            try {
                String descEnc = Base64.getEncoder().encodeToString(AESUtil.encrypt(alg, descPlain, key, ivSpec).getBytes());
                String unameEnc = Base64.getEncoder().encodeToString(AESUtil.encrypt(alg, unamePlain, key, ivSpec).getBytes());
                String passEnc = Base64.getEncoder().encodeToString(AESUtil.encrypt(alg, passPlain, key, ivSpec).getBytes());

                String newIv = Base64.getEncoder().encodeToString(ivSpec.getIV());

                if (DatabaseHandler.updatePassword(descEnc, unameEnc, passEnc, newIv, id)) {
                    showInformationDialog(MainApp.lang.getString("confirmation.dialog"), MainApp.lang.getString("data.updated"),
                            MainApp.lang.getString("updated-credentials-dialog"));
                } else {
                    showErrorDialog(MainApp.lang.getString("error.dialog"), MainApp.lang.getString("unknown-error"),
                            MainApp.lang.getString("info-send-issue-to-gh"));
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
