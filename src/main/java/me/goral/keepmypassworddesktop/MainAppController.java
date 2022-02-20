package me.goral.keepmypassworddesktop;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import me.goral.keepmypassworddesktop.util.handleConfFile;

public class MainAppController {

    @FXML
    Button btnLogin;

    @FXML
    protected void onLoginButtonClick() {
        System.out.println("Test test test test");
    }

    public void changeBtnText() {
        if (!handleConfFile.checkIfConfigExists()) {
            btnLogin.setText("Rejestracja");
            handleConfFile.createConfFile();
        } else btnLogin.setText("Logowanie");
    }
}