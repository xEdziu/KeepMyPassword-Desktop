package me.goral.keepmypassworddesktop.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Pair;
import me.goral.keepmypassworddesktop.MainApp;
import me.goral.keepmypassworddesktop.database.DatabaseHandler;
import me.goral.keepmypassworddesktop.util.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.*;

import static me.goral.keepmypassworddesktop.MainApp.lang;
import static me.goral.keepmypassworddesktop.util.AlertsUtil.showErrorDialog;
import static me.goral.keepmypassworddesktop.util.ConfUtil.createConfFiles;
import static me.goral.keepmypassworddesktop.util.PasswordGeneratorUtil.checkPasswordComplexity;

public class MainAppController {

    private Boolean login = false;

    @FXML
    Button btnLogin;
    @FXML
    Button btnQuit;
    @FXML
    Label dateLabel;
    @FXML
    Label appTitleLabel;

    /**
     * This function sets the text of the dateLabel to the current year
     * sets the auth button text, quit button text, and the app name.
     */
    @FXML
    private void initialize(){
        LocalDate l = LocalDate.now();
        dateLabel.setText("\u00a9" + "Adrian Goral " + String.valueOf(l.getYear()));
        btnLogin.setText(lang.getString("login-button"));
        appTitleLabel.setText(lang.getString("appName"));
    }

    @FXML
    /**
     * Show confirmation alert if the user decides to quit the app
     */
    protected void confirmExit(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Quit");
        alert.setHeaderText("Really want to quit?");
        Stage currentStage = (Stage)((Node) event.getSource()).getScene().getWindow();
        alert.initOwner(currentStage);
        alert.showAndWait().filter(r -> r == ButtonType.OK).ifPresent(r->Platform.exit());
    }

    /**
     * If the user is logging in, we check if the username and password are correct. If they are, we show the logged in
     * screen. If they are not, we show an error dialog
     */
    @FXML
    protected void onLoginButtonClick() {
        Dialog<List<String>> dialog = new Dialog<>();
        String dialogType = login ? lang.getString("login") : lang.getString("register");
        dialog.setTitle(MessageFormat.format(lang.getString("dialog"), dialogType));
        dialog.setHeaderText(login ? lang.getString("loginPrompt") : lang.getString("registerPrompt"));
        dialog.setGraphic(new ImageView(MainApp.class.getResource("/me/goral/keepmypassworddesktop/images/login-64.png").toString()));
        dialog.getDialogPane().getStylesheets().add(MainApp.class.getResource("styles/dialog.css").toExternalForm());

        ButtonType registerButtonType = new ButtonType(dialogType, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType(lang.getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(registerButtonType, cancelButtonType);
        Node regBtn = dialog.getDialogPane().lookupButton(registerButtonType);
        Node canBtn = dialog.getDialogPane().lookupButton(cancelButtonType);
        regBtn.getStyleClass().add("btn");//NON-NLS
        canBtn.getStyleClass().add("btn");//NON-NLS
        regBtn.setDisable(true);

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20,150,10,10));

        ObservableList<String> options = ConfUtil.readLanguages();
        final ComboBox<String> languageBox = new ComboBox<>(options);
        languageBox.getSelectionModel().selectFirst();

        TextField username = new TextField();
        username.setPromptText(lang.getString("username"));
        TextField tf = new TextField();
        tf.setManaged(false);
        tf.setVisible(false);
        PasswordField password = new PasswordField();
        password.setPromptText(lang.getString("password"));

        CheckBox checkBox = new CheckBox(lang.getString("show-hide-pwd"));

        tf.managedProperty().bind(checkBox.selectedProperty());
        tf.visibleProperty().bind(checkBox.selectedProperty());

        password.managedProperty().bind(checkBox.selectedProperty().not());
        password.visibleProperty().bind(checkBox.selectedProperty().not());

        tf.textProperty().bindBidirectional(password.textProperty());

        Label pwdCheck = new Label(lang.getString("no-password-info"));
        pwdCheck.setTextFill(Color.web("#b3b3b3"));//NON-NLS

        password.textProperty().addListener((observable, oldValue, newValue) -> {
            Pair<String, Color> res = checkPasswordComplexity(newValue);
            pwdCheck.setText(res.getKey());
            pwdCheck.setTextFill(res.getValue());
        });

        grid.add(new Label(lang.getString("username")), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label(lang.getString("password")), 0, 1);
        grid.add(password, 1, 1);
        grid.add(tf, 1,1);
        grid.add(checkBox,2,1);
        if (!login) {
            grid.add(pwdCheck, 1, 2);
            grid.add(new Label(lang.getString("choose-your-language-prompt")), 0, 3);
            grid.add(languageBox,  1, 3);
        }

        username.textProperty().addListener(((observableValue, oldV, newV) -> regBtn.setDisable(newV.trim().isEmpty())));

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(username::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == registerButtonType){

                String newUname = username.getText();
                String newPwd = password.getText();

                Pair<String, Color> checker = checkPasswordComplexity(newPwd);
                List<String> res = new ArrayList<>();

                if (password.getText().isEmpty() && !login){
                    AlertsUtil.showErrorDialog(lang.getString("error"), lang.getString("there-is-a-problem"), lang.getString("you.can.t.register.with.empty.password"));
                    res.add("err"+newUname);//NON-NLS
                    res.add(newPwd);
                    if(!login) res.add(languageBox.getValue());
                    return res;
                } else if ((!checker.getKey().equals(lang.getString("strong.password")) && !checker.getKey().equals(lang.getString("medium.password"))) && !login){
                    AlertsUtil.showErrorDialog(lang.getString("error"), lang.getString("there-is-a-problem"), lang.getString("password.is.not.strong.enough"));
                    res.add("err"+newUname);//NON-NLS
                    res.add(newPwd);
                    if(!login) res.add(languageBox.getValue());
                    return res;
                } else {
                    res.add(newUname);
                    res.add(newPwd);
                    if(!login) res.add(languageBox.getValue());
                    return res;
                }
            }
            return null;
        });

        Optional<List<String>> res = dialog.showAndWait();
        res.ifPresent(result -> {

            String uname = result.get(0);
            String plain = result.get(1);

            if (uname.startsWith("err")){//NON-NLS
                restartLoginForm(uname.substring(3), plain);
                return;
            }

            if (login){
                //login
                try {
                    String config = ConfUtil.readConfigFile();
                    if (config == null) return;
                    String[] configArr = config.split(":");
                    String unameFromString = configArr[0];
                    String encryptedInitial = configArr[1];
                    String ivString = configArr[2];
                    String salt = configArr[3];
                    String argon = ArgonUtil.encrypt(plain, salt);
                    SecretKey key = AESUtil.generateKey(argon);

                    if (SHAUtil.hashSHA(uname).equals(unameFromString)){
                        boolean authorized = AuthUtil.authorize(encryptedInitial, ivString, key);
                        if (!authorized){
                            showErrorDialog(lang.getString("error"), lang.getString("invalid.username.or.password"),
                                    lang.getString("please.provide.correct.credentials"));
                            onLoginButtonClick();
                        } else {
                            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("layouts/logged.fxml"));
                            Parent root = loader.load();

                            LoggedController loggedController = loader.getController();
                            loggedController.setSecretKey(key);
                            loggedController.setUnameLabel(uname);

                            Scene sc = new Scene(root);
                            String css = MainApp.class.getResource("styles/main.css").toExternalForm();
                            sc.getStylesheets().add(css);
                            MainApp.getStage().setScene(sc);
                        }
                    } else {
                        showErrorDialog(lang.getString("error"), lang.getString("invalid.username.or.password"),
                                lang.getString("please.provide.correct.credentials"));
                        onLoginButtonClick();
                    }
                } catch (Exception e) {
                    AlertsUtil.showExceptionStackTraceDialog(e);
                }
            } else {

                //register
                try {
                    IvParameterSpec iv = AESUtil.generateIv();
                    String salt = Base64.getEncoder().encodeToString(ArgonUtil.generateSalt());
                    String argon = ArgonUtil.encrypt(plain, salt);
                    SecretKey key = AESUtil.generateKey(argon);
                    String init = AuthUtil.encryptInitial(key, iv);
                    String lang = result.get(2);

                    String output = SHAUtil.hashSHA(uname) + ":" + init + ":" + salt + ":" + lang;
                    createConfFiles(output);
                    login = true;
                    MainApp.loc = MainApp.setLocale();
                    MainApp.lang = MainApp.setLanguageBundle(MainApp.loc);
                    handleAppRun();
                    onLoginButtonClick();

                } catch (Exception e){
                    AlertsUtil.showExceptionStackTraceDialog(e);
                }
            }
        });
    }

    protected void restartLoginForm(String u, String p){
        onLoginButtonClick(u, p);
    }

    @FXML
    protected void onLoginButtonClick(String unameString, String passwordString) {
        Dialog<List<String>> dialog = new Dialog<>();
        String dialogType = login ? lang.getString("login") : lang.getString("register");
        dialog.setTitle(MessageFormat.format(lang.getString("dialog"), dialogType));
        dialog.setHeaderText(login ? lang.getString("loginPrompt") : lang.getString("registerPrompt"));
        dialog.setGraphic(new ImageView(MainApp.class.getResource("/me/goral/keepmypassworddesktop/images/login-64.png").toString()));
        dialog.getDialogPane().getStylesheets().add(MainApp.class.getResource("styles/dialog.css").toExternalForm());

        ButtonType registerButtonType = new ButtonType(dialogType, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType(lang.getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(registerButtonType, cancelButtonType);
        Node regBtn = dialog.getDialogPane().lookupButton(registerButtonType);
        Node canBtn = dialog.getDialogPane().lookupButton(cancelButtonType);
        regBtn.getStyleClass().add("btn");//NON-NLS
        canBtn.getStyleClass().add("btn");//NON-NLS
        regBtn.setDisable(true);

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20,150,10,10));

        ObservableList<String> options = ConfUtil.readLanguages();
        final ComboBox<String> languageBox = new ComboBox<>(options);
        languageBox.getSelectionModel().selectFirst();

        TextField username = new TextField();
        username.setText(unameString);
        TextField tf = new TextField();
        tf.setManaged(false);
        tf.setVisible(false);
        PasswordField password = new PasswordField();
        password.setText(passwordString);

        CheckBox checkBox = new CheckBox(lang.getString("show-hide-pwd"));

        tf.managedProperty().bind(checkBox.selectedProperty());
        tf.visibleProperty().bind(checkBox.selectedProperty());

        password.managedProperty().bind(checkBox.selectedProperty().not());
        password.visibleProperty().bind(checkBox.selectedProperty().not());

        tf.textProperty().bindBidirectional(password.textProperty());

        Label pwdCheck = new Label(lang.getString("no-password-info"));
        pwdCheck.setTextFill(Color.web("#b3b3b3"));//NON-NLS

        password.textProperty().addListener((observable, oldValue, newValue) -> {
            Pair<String, Color> res = checkPasswordComplexity(newValue);
            pwdCheck.setText(res.getKey());
            pwdCheck.setTextFill(res.getValue());
        });

        grid.add(new Label(lang.getString("username")), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label(lang.getString("password")), 0, 1);
        grid.add(password, 1, 1);
        grid.add(tf, 1,1);
        grid.add(checkBox,2,1);
        if (!login) {
            grid.add(pwdCheck, 1, 2);
            grid.add(new Label(lang.getString("choose-your-language-prompt")), 0, 3);
            grid.add(languageBox,  1, 3);
        }


        username.textProperty().addListener(((observableValue, oldV, newV) -> regBtn.setDisable(newV.trim().isEmpty())));
        regBtn.setDisable(unameString.trim().isEmpty());

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(username::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == registerButtonType){

                String newUname = username.getText();
                String newPwd = password.getText();

                Pair<String, Color> checker = checkPasswordComplexity(newPwd);
                List<String> res = new ArrayList<>();

                if (password.getText().isEmpty() && !login){
                    AlertsUtil.showErrorDialog(lang.getString("error"), lang.getString("there-is-a-problem"),
                            lang.getString("you.can.t.register.with.empty.password"));
                    res.add("err"+newUname);//NON-NLS
                    res.add(newPwd);
                    if(!login) res.add(languageBox.getValue());
                    return res;
                } else if ((!checker.getKey().equals(lang.getString("strong.password"))
                        && !checker.getKey().equals(lang.getString("medium.password"))) && !login){
                    AlertsUtil.showErrorDialog(lang.getString("error"),
                            lang.getString("there-is-a-problem"), lang.getString("password.is.not.strong.enough"));
                    res.add("err"+newUname);//NON-NLS
                    res.add(newPwd);
                    if(!login) res.add(languageBox.getValue());
                    return res;
                } else {
                    res.add(newUname);
                    res.add(newPwd);
                    if(!login) res.add(languageBox.getValue());
                    return res;
                }
            }
            return null;
        });

        Optional<List<String>> res = dialog.showAndWait();
        res.ifPresent(result -> {

            String uname = result.get(0);
            String plain = result.get(1);

            if (uname.startsWith("err")){//NON-NLS
                restartLoginForm(uname.substring(3), plain);
                return;
            }

            if (login){
                //login
                try {
                    String config = ConfUtil.readConfigFile();
                    if (config == null) return;
                    String[] configArr = config.split(":");
                    String unameFromString = configArr[0];
                    String encryptedInitial = configArr[1];
                    String ivString = configArr[2];
                    String salt = configArr[3];
                    String argon = ArgonUtil.encrypt(plain, salt);
                    SecretKey key = AESUtil.generateKey(argon);

                    if (SHAUtil.hashSHA(uname).equals(unameFromString)){
                        boolean authorized = AuthUtil.authorize(encryptedInitial, ivString, key);
                        if (!authorized){
                            showErrorDialog(lang.getString("error"), lang.getString("invalid.username.or.password"),
                                    lang.getString("please.provide.correct.credentials"));
                            onLoginButtonClick();
                        } else {
                            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("layouts/logged.fxml"));
                            Parent root = loader.load();

                            LoggedController loggedController = loader.getController();
                            loggedController.setSecretKey(key);
                            loggedController.setUnameLabel(uname);

                            Scene sc = new Scene(root);
                            String css = MainApp.class.getResource("styles/main.css").toExternalForm();
                            sc.getStylesheets().add(css);
                            MainApp.getStage().setScene(sc);
                        }
                    } else {
                        showErrorDialog(lang.getString("error"), lang.getString("invalid.username.or.password"),
                                lang.getString("please.provide.correct.credentials"));
                        onLoginButtonClick();
                    }
                } catch (Exception e) {
                    AlertsUtil.showExceptionStackTraceDialog(e);
                }
            } else {

                //register
                try {
                    IvParameterSpec iv = AESUtil.generateIv();
                    String salt = Base64.getEncoder().encodeToString(ArgonUtil.generateSalt());
                    String argon = ArgonUtil.encrypt(plain, salt);
                    SecretKey key = AESUtil.generateKey(argon);
                    String init = AuthUtil.encryptInitial(key, iv);
                    String lang = result.get(2);

                    String output = SHAUtil.hashSHA(uname) + ":" + init + ":" + salt + ":" + lang;
                    createConfFiles(output);
                    login = true;
                    MainApp.loc = MainApp.setLocale();
                    MainApp.lang = MainApp.setLanguageBundle(MainApp.loc);
                    handleAppRun();
                    onLoginButtonClick();

                } catch (Exception e){
                    AlertsUtil.showExceptionStackTraceDialog(e);
                }
            }
        });
    }

    public void setIsLogged() {
        login = true;
    }


    public void handleAppRun() {
        try {
            if (!ConfUtil.checkIfConfigExists() && !ConfUtil.checkIfDatabaseExists()) {
                btnLogin.setText(lang.getString("register"));
            } else if (ConfUtil.checkIfDatabaseExists() && !ConfUtil.checkIfConfigExists()) {
                ConfUtil.deleteConfFiles();
                btnLogin.setText(lang.getString("register"));
            } else if (ConfUtil.checkIfConfigExists() && !ConfUtil.checkIfDatabaseExists()){
                DatabaseHandler.createDatabase();
                btnLogin.setText(lang.getString("log.in"));
                login = true;
            } else {
                btnLogin.setText(lang.getString("log.in"));
                login = true;
            }
        } catch (Exception e){
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
    }
}