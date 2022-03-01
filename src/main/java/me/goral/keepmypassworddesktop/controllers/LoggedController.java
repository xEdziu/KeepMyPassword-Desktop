package me.goral.keepmypassworddesktop.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import me.goral.keepmypassworddesktop.database.DatabaseHandler;

import javax.crypto.spec.IvParameterSpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class LoggedController {

    @FXML private TableView<PasswordRow> contentTable;
    @FXML private TableColumn<PasswordRow, String> descColumn;
    @FXML private TableColumn<PasswordRow, String> loginColumn;
    @FXML private TableColumn<PasswordRow, String> pwdColumn;

    @FXML
    private void initialize(){
        descColumn.setCellValueFactory(new PropertyValueFactory<PasswordRow, String>("Description"));
        loginColumn.setCellValueFactory(new PropertyValueFactory<PasswordRow, String>("Login"));
        pwdColumn.setCellValueFactory(new PropertyValueFactory<PasswordRow, String>("Password"));

        contentTable.getItems().setAll(parsePasswordsList());
    }

    private List<PasswordRow> parsePasswordsList() {
        List<PasswordRow> list = new ArrayList<>();

        List<List<String>> passwordsFromDb = DatabaseHandler.selectPasswords();
        if (!passwordsFromDb.isEmpty()) {
            for (List<String> r : passwordsFromDb){
                String desc = r.get(0);
                String login = r.get(1);
                String pwd = r.get(2);

                //TODO get key from first instance
                //TODO decrypt data from database
                //TODO add decrypted things to new PasswordRow
                //TODO new PasswordRow to list

                IvParameterSpec iv = new IvParameterSpec(Base64.getDecoder().decode(r.get(3)));
            }
        }


        return list;
    }

    public static class PasswordRow {
        private final SimpleStringProperty desc;
        private final SimpleStringProperty login;
        private final SimpleStringProperty pwd;

        private PasswordRow(String desc, String login, String pwd){
            this.desc = new SimpleStringProperty(desc);
            this.login = new SimpleStringProperty(login);
            this.pwd = new SimpleStringProperty(pwd);
        }

        public String getDesc() {
            return desc.get();
        }

        public void setDesc(String desc) {
            this.desc.set(desc);
        }

        public String getLogin() {
            return login.get();
        }

        public void setLogin(String login) {
            this.login.set(login);
        }

        public String getPwd() {
            return pwd.get();
        }

        public void setPwd(String pwd) {
            this.pwd.set(pwd);
        }
    }
}
