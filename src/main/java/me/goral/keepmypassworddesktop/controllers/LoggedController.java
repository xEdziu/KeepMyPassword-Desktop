package me.goral.keepmypassworddesktop.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import me.goral.keepmypassworddesktop.MainApp;
import me.goral.keepmypassworddesktop.database.DatabaseHandler;
import me.goral.keepmypassworddesktop.util.AESUtil;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class LoggedController {

    @FXML private TableView<PasswordRow> contentTable;
    @FXML private TableColumn<PasswordRow, String> descColumn;
    @FXML private TableColumn<PasswordRow, String> loginColumn;
    @FXML private TableColumn<PasswordRow, String> pwdColumn;
    @FXML private Label unameLabel;
    private SecretKey key;

    @FXML
    private void initialize() throws Exception {
        descColumn.setCellValueFactory(new PropertyValueFactory<PasswordRow, String>("Description"));
        loginColumn.setCellValueFactory(new PropertyValueFactory<PasswordRow, String>("Login"));
        pwdColumn.setCellValueFactory(new PropertyValueFactory<PasswordRow, String>("Password"));

        refreshContentTable();
    }

    @FXML
    private void onLogoutButtonClick() throws IOException {
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

            key = null;
            unameLabel.setText("");

            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("layouts/main-app-view.fxml"));
            Parent root = loader.load();

            MainAppController controller = loader.getController();
            controller.setIsLogged();
            Scene sc = new Scene(root);
            String css = MainApp.class.getResource("styles/main.css").toExternalForm();
            sc.getStylesheets().add(css);
            MainApp.getStage().setScene(sc);

        }
    }

    private void refreshContentTable() throws Exception {
        contentTable.getItems().setAll(parsePasswordsList());
    }

    public void setSecretKey(SecretKey k){
        key = k;
    }

    public void setUnameLabel(String uname) {
        unameLabel.setText(uname);
    }

    private List<PasswordRow> parsePasswordsList() throws Exception {
        List<PasswordRow> list = new ArrayList<>();

        List<List<String>> passwordsFromDb = DatabaseHandler.selectPasswords();
        if (!passwordsFromDb.isEmpty()) {
            for (List<String> r : passwordsFromDb){
                String descEnc = r.get(0);
                String loginEnc = r.get(1);
                String pwdEnc = r.get(2);
                IvParameterSpec iv = new IvParameterSpec(Base64.getDecoder().decode(r.get(3)));

                //decrypt data from database
                String descDec = AESUtil.decrypt("AES/CBC/PKCS5Padding", new String(Base64.getDecoder().decode(descEnc)), key, iv);
                String loginDec = AESUtil.decrypt("AES/CBC/PKCS5Padding", new String(Base64.getDecoder().decode(loginEnc)), key, iv);
                String pwdDec = AESUtil.decrypt("AES/CBC/PKCS5Padding", new String(Base64.getDecoder().decode(pwdEnc)), key, iv);

                //add decrypted things to new PasswordRow
                PasswordRow pr = new PasswordRow(descDec, loginDec, pwdDec);

                //new PasswordRow to list
                list.add(pr);
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
