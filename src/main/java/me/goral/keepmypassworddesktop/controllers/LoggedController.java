package me.goral.keepmypassworddesktop.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;

public class LoggedController {

    @FXML
    private void initialize(){

    }

    public static class PasswordRow {
        private final SimpleStringProperty desc;
        private final SimpleStringProperty login;
        private final SimpleStringProperty pwd;

        private PasswordRow(String desc, String login, String pwd){
            this.desc = new SimpleStringProperty(desc);
            this.login = new SimpleStringProperty(desc);
            this.pwd = new SimpleStringProperty(pwd);
        }
    }
}
