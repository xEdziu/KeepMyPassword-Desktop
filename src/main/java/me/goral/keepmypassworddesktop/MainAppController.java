package me.goral.keepmypassworddesktop;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import me.goral.keepmypassworddesktop.util.AESUtil;
import me.goral.keepmypassworddesktop.util.ArgonUtil;
import me.goral.keepmypassworddesktop.util.AuthUtil;
import me.goral.keepmypassworddesktop.util.ConfUtil;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.Optional;

import static me.goral.keepmypassworddesktop.util.ConfUtil.createConfFile;

public class MainAppController {

    Boolean login = false;

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

        //TODO add icon, finish styling

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
            String argon = ArgonUtil.encrypt(plain);
            SecretKey key = AESUtil.generateKey(argon);

            if (login){
                //login
                try {
                    String config = ConfUtil.readConfigFile();
                    System.out.println(config);
                    String[] configArr = config.split(":");
                    String unameFromString = configArr[0];
                    String encryptedInitial = configArr[2];
                    String ivString = configArr[3];

                    if (uname.equals(unameFromString)){
                        boolean authorized = AuthUtil.authorize(encryptedInitial, ivString, key);
                        System.out.println("Authorized: " + authorized);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            } else {
                //register
                try {

                    IvParameterSpec iv = AESUtil.generateIv();
                    String init = AuthUtil.encryptInitial(key, iv);

                    String output = uname + ":" + init;
                    createConfFile(output);
                    System.out.println("Finished registration");

                } catch (Exception e){
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    public void changeBtnText() {
        if (!ConfUtil.checkIfConfigExists()) {
            btnLogin.setText("Rejestracja");
        } else {
            btnLogin.setText("Logowanie");
            login = true;
        }
    }
}