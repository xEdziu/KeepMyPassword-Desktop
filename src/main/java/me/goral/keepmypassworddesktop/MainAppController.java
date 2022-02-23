package me.goral.keepmypassworddesktop;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import me.goral.keepmypassworddesktop.util.ArgonUtil;
import me.goral.keepmypassworddesktop.util.HandleConfFile;

import java.util.Optional;

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

        username.textProperty().addListener(((observableValue, oldV, newV) -> {
            regBtn.setDisable(newV.trim().isEmpty());
        }));

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
            System.out.println("Username: " + result.getKey() + ", Password: " + result.getValue());
            System.out.println("\n");
            String hash = ArgonUtil.encrypt(result.getValue());
            System.out.println("Hashed password: " + hash);
            System.out.println("Verify: " + (ArgonUtil.verify(hash, result.getValue()) ? "Correct" : "Incorrect"));
        });
    }

    public void changeBtnText() {
        if (!HandleConfFile.checkIfConfigExists()) {
            btnLogin.setText("Rejestracja");
            HandleConfFile.createConfFile();
        } else {
            btnLogin.setText("Logowanie");
            login = true;
        }
    }
}