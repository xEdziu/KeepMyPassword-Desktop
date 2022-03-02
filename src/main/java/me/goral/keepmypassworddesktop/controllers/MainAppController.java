package me.goral.keepmypassworddesktop.controllers;

import javafx.application.Platform;
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
import javafx.stage.Stage;
import javafx.util.Pair;
import me.goral.keepmypassworddesktop.MainApp;
import me.goral.keepmypassworddesktop.util.AESUtil;
import me.goral.keepmypassworddesktop.util.ArgonUtil;
import me.goral.keepmypassworddesktop.util.AuthUtil;
import me.goral.keepmypassworddesktop.util.ConfUtil;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;
import java.util.Optional;

import static me.goral.keepmypassworddesktop.util.AlertsUtil.showErrorDialog;
import static me.goral.keepmypassworddesktop.util.ConfUtil.createConfFile;

public class MainAppController {

    private Boolean login = false;

    @FXML
    Button btnLogin;


    @FXML
    protected void onLoginButtonClick() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        String dialogType = login ? "Login" :  "Register";
        dialog.setTitle(dialogType + " Dialog");
        dialog.setHeaderText(login ? "Log in to your account" : "Set up your account");
        dialog.setGraphic(new ImageView(MainApp.class.getResource("/me/goral/keepmypassworddesktop/images/login-64.png").toString()));
        dialog.getDialogPane().getStylesheets().add(MainApp.class.getResource("styles/dialog.css").toExternalForm());

        ButtonType registerButtonType = new ButtonType(dialogType, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(registerButtonType, cancelButtonType);
        Node regBtn = dialog.getDialogPane().lookupButton(registerButtonType);
        Node canBtn = dialog.getDialogPane().lookupButton(cancelButtonType);
        regBtn.getStyleClass().add("btn");
        canBtn.getStyleClass().add("btn");
        regBtn.setDisable(true);

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20,150,10,10));

        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        grid.add(new Label("Username"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password"), 0, 1);
        grid.add(password, 1, 1);

        username.textProperty().addListener(((observableValue, oldV, newV) -> regBtn.setDisable(newV.trim().isEmpty())));

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(username::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == registerButtonType){
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> res = dialog.showAndWait();
        res.ifPresent(result -> {

            String uname = result.getKey();
            String plain = result.getValue();

            if (login){
                //login
                try {
                    String config = ConfUtil.readConfigFile();
                    String[] configArr = config.split(":");
                    String unameFromString = configArr[0];
                    String encryptedInitial = configArr[2];
                    String ivString = configArr[3];
                    String salt = configArr[4];
                    String argon = ArgonUtil.encrypt(plain, salt);
                    SecretKey key = AESUtil.generateKey(argon);

                    if (uname.equals(unameFromString)){
                        boolean authorized = AuthUtil.authorize(encryptedInitial, ivString, key);
                        if (!authorized){
                            showErrorDialog("Error", "Invalid username or password",
                                    "Please provide correct credentials.");
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
                        showErrorDialog("Error", "Invalid username or password",
                                "Please provide correct credentials.");
                        onLoginButtonClick();
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            } else {

                //register
                try {
                    IvParameterSpec iv = AESUtil.generateIv();
                    String salt = Base64.getEncoder().encodeToString(ArgonUtil.generateSalt());
                    String argon = ArgonUtil.encrypt(plain, salt);
                    SecretKey key = AESUtil.generateKey(argon);
                    String init = AuthUtil.encryptInitial(key, iv);

                    String output = uname + ":" + init + ":" + salt;
                    createConfFile(output);
                    System.out.println("Finished registration");
                    login = true;
                    handleAppRun();
                    onLoginButtonClick();

                } catch (Exception e){
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    public void setIsLogged() {
        login = true;
    }

    public void handleAppRun() {
        if (!ConfUtil.checkIfConfigExists()) {
            btnLogin.setText("Register");
        } else {
            btnLogin.setText("Log in");
            login = true;
        }
    }
}