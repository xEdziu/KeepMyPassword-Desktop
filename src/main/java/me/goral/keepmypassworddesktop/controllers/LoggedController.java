package me.goral.keepmypassworddesktop.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import me.goral.keepmypassworddesktop.MainApp;
import me.goral.keepmypassworddesktop.database.DatabaseHandler;
import me.goral.keepmypassworddesktop.util.AESUtil;
import me.goral.keepmypassworddesktop.util.AlertsUtil;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class LoggedController {

    @FXML private Label loggedAsLabel;
    @FXML private TableView<PasswordRow> contentTable;
    @FXML private TableColumn<PasswordRow, String> idColumn = new TableColumn<>("id");//NON-NLS
    @FXML private TableColumn<PasswordRow, String> descColumn = new TableColumn<>(MainApp.lang.getString("description-table-desc"));
    @FXML private TableColumn<PasswordRow, String> loginColumn = new TableColumn<>(MainApp.lang.getString("login-table-desc"));
    @FXML private TableColumn<PasswordRow, String> pwdColumn = new TableColumn<>(MainApp.lang.getString("password-table-desc"));
    @FXML private TableColumn<PasswordRow, String> ivColumn = new TableColumn<>("IV");//NON-NLS
    @FXML private Label unameLabel;
    @FXML private Button showBtn;
    @FXML private Button addButton;
    @FXML private Button removeButton;
    @FXML private Button genPwd;
//    @FXML private Button settingsButton;
    @FXML private Button tool1;
    @FXML private Button tool2;
    @FXML private Button tool3;
    @FXML private Button tool4;

    private SecretKey key;
    private boolean showed = false;

    /**
     * The function creates a new TableRow object and sets the onMouseClicked event handler to the event.
     * If the user double-clicks on the row, the function will show the update password dialog
     */
    @FXML
    private void initialize() {

        tool1.setText(MainApp.lang.getString("change.language"));
        tool1.getStyleClass().add("toolbarBtn");
        tool1.setOnMouseClicked(mouseEvent -> {
            AlertsUtil.showChangeLanguageDialog();
        });

        tool2.setText(MainApp.lang.getString("logout"));
        tool2.getStyleClass().add("toolbarBtn");
        tool2.setOnMouseClicked(mouseEvent -> {
            AlertsUtil.showLogoutDialog();
        });

        tool3.setText(MainApp.lang.getString("delete.data"));
        tool3.getStyleClass().add("toolbarBtn");
        tool3.setOnMouseClicked(mouseEvent -> {
            AlertsUtil.showDeleteDataDialog(contentTable, showed);
        });

        tool4.setText(MainApp.lang.getString("delete.account"));
        tool4.getStyleClass().add("toolbarBtn");
        tool4.setOnMouseClicked(mouseEvent -> {
            AlertsUtil.showDeleteAccountDialog();
        });

        descColumn.setText(MainApp.lang.getString("description-table-desc"));
        loginColumn.setText(MainApp.lang.getString("login-table-desc"));
        pwdColumn.setText(MainApp.lang.getString("password-table-desc"));

        addButton.setText(MainApp.lang.getString("add"));
        addButton.getStyleClass().add("btnLogin");
        addButton.setWrapText(true);

        genPwd.setText(MainApp.lang.getString("generate"));
        genPwd.getStyleClass().add("btnLogin");
        genPwd.setWrapText(true);

        removeButton.setText(MainApp.lang.getString("remove"));
        removeButton.getStyleClass().add("btnQuit");
        removeButton.setWrapText(true);

        contentTable.setPlaceholder(new Label(MainApp.lang.getString("no.content.in.table")));
        loggedAsLabel.setText(MainApp.lang.getString("logged.as"));
        showBtn.getStyleClass().add("show"); //NON-NLS


        idColumn.setCellValueFactory(
                p -> new SimpleStringProperty(p.getValue().getId())
        );
        descColumn.setCellValueFactory(
                p -> new SimpleStringProperty(p.getValue().getDesc())
        );
        loginColumn.setCellValueFactory(
                p -> new SimpleStringProperty(p.getValue().getLogin())
        );
        pwdColumn.setCellValueFactory(
                p -> new SimpleStringProperty(p.getValue().getActivePwd())
        );
        ivColumn.setCellValueFactory(
                p -> new SimpleStringProperty(p.getValue().getIv())
        );
        idColumn.setVisible(false);
        ivColumn.setVisible(false);

        idColumn.setResizable(false);
        descColumn.setResizable(false);
        loginColumn.setResizable(false);
        pwdColumn.setResizable(false);
        ivColumn.setResizable(false);

        idColumn.setReorderable(false);
        descColumn.setReorderable(false);
        loginColumn.setReorderable(false);
        pwdColumn.setReorderable(false);
        ivColumn.setReorderable(false);

        descColumn.setCellFactory(c -> new TableCell<>() {

            private final Text text = new Text();
            {
                prefWidthProperty().bind(descColumn.widthProperty());
                text.wrappingWidthProperty().bind(widthProperty().subtract(2));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    text.setText(item);
                    text.getStyleClass().add("txt");//NON-NLS
                    setGraphic(text);
                }
            }
        });

        loginColumn.setCellFactory(c -> new TableCell<>() {

            private final Text text = new Text();
            {
                prefWidthProperty().bind(loginColumn.widthProperty());
                text.wrappingWidthProperty().bind(widthProperty().subtract(2));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    text.setText(item);
                    text.getStyleClass().add("txt");//NON-NLS
                    setGraphic(text);
                }
            }
        });

        pwdColumn.setCellFactory(c -> new TableCell<>() {
            private final Text text = new Text();
            {
                prefWidthProperty().bind(pwdColumn.widthProperty().subtract(12));
                text.wrappingWidthProperty().bind(widthProperty());
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    text.setText(item);
                    text.getStyleClass().add("txt");//NON-NLS
                    setGraphic(text);
                }
            }
        });


        contentTable.setRowFactory( tv -> {
            TableRow<PasswordRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())){
                    PasswordRow rowData = row.getItem();
                    int id = Integer.parseInt(rowData.getId());
                    AlertsUtil.showUpdatePasswordDialog(id, rowData.getDesc(),rowData.getLogin(),
                            rowData.getPwd(), key);
                    refreshContentTable();
                }
            });
            return row;
        });
    }

    /**
     * The function is called when the user clicks the "Generate Password" button.
     *
     * It shows a dialog that allows the user to generate custom password.
     */
    @FXML
    private void onGenPwdClick(){
        AlertsUtil.showGeneratePasswordDialog();
    }

    /**
     * This function is called when the user clicks the add button.
     * It shows the add password dialog and refreshes the content table.
     */
    @FXML
    private void onAddClick(){
        AlertsUtil.showAddPasswordDialog(key);
        refreshContentTable();
    }

    /**
     * This function is called when the user clicks the "Remove" button
     */
    @FXML
    private void onRemoveClick(){
        PasswordRow row = contentTable.getSelectionModel().getSelectedItem();
        if (row != null){
            String id = row.getId();
            try {
                if (DatabaseHandler.deletePassword(id)){
                    refreshContentTable();
                } else {
                    AlertsUtil.showErrorDialog(MainApp.lang.getString("error.alert"),
                            MainApp.lang.getString("error-deleting-pwd"),
                            MainApp.lang.getString("info-send-issue-to-gh"));
                }

            } catch (SQLException e) {
                AlertsUtil.showExceptionStackTraceDialog(e);
            }
        } else {
            AlertsUtil.showInformationDialog(MainApp.lang.getString("no.item.selected"), MainApp.lang.getString("wait.a.minute.err"),
                    MainApp.lang.getString("no-select-error"));
        }
    }

    /**
     * If the passwords are hidden, show them.
     * If the passwords are visible, hide them.
     * Refresh the table.
     */
    @FXML
    private void onShowBtnClick(){
        if (!showed){
            showBtn.getStyleClass().clear();
            showBtn.getStyleClass().addAll("button","hide");//NON-NLS
        } else {
            showBtn.getStyleClass().clear();
            showBtn.getStyleClass().addAll("button","show");//NON-NLS
        }
        showed = !showed;
        refreshContentTable();
    }

    /**
     * It takes the passwords list and parses it into a list of Password objects
     */
    private void refreshContentTable() {
        contentTable.getItems().setAll(parsePasswordsList(showed));
    }

    /**
     * It takes a table view and a boolean as parameters. It then parses the passwords list and returns a list of
     * PasswordRow objects
     *
     * @param tv the TableView to refresh
     * @param s boolean
     */
    public void refreshContentTable(TableView<PasswordRow> tv, boolean s) {
        tv.getItems().setAll(parsePasswordsList(s));
    }

    /**
     * Set the secret key to be used for encryption and decryption
     *
     * @param k The key to use for encryption/decryption.
     */
    public void setSecretKey(SecretKey k){
        key = k;
        refreshContentTable();
    }

    /**
     * It sets the text of the label to the value of the parameter.
     *
     * @param uname The username of the user.
     */
    public void setUnameLabel(String uname) {
        unameLabel.setText(uname);
    }

    /**
     * It takes a list of passwords from the database, decrypts them and adds them to a list of PasswordRow objects
     *
     * @param visible boolean
     * @return A list of PasswordRow objects.
     */
    private List<PasswordRow> parsePasswordsList(boolean visible) {
        List<PasswordRow> list = new ArrayList<>();

        List<List<String>> passwordsFromDb = DatabaseHandler.selectPasswords();
        try {
            if (!passwordsFromDb.isEmpty()) {
                for (List<String> r : passwordsFromDb){
                    String id = r.get(0);
                    String descEnc = r.get(1);
                    String loginEnc = r.get(2);
                    String pwdEnc = r.get(3);
                    String ivEnc = r.get(4);
                    IvParameterSpec iv = new IvParameterSpec(Base64.getDecoder().decode(ivEnc));

                    //decrypt data from database
                    String descDec = AESUtil.decrypt("AES/CBC/PKCS5Padding", new String(Base64.getDecoder().decode(descEnc)), key, iv);//NON-NLS
                    String loginDec = AESUtil.decrypt("AES/CBC/PKCS5Padding", new String(Base64.getDecoder().decode(loginEnc)), key, iv);//NON-NLS
                    String pwdDec = AESUtil.decrypt("AES/CBC/PKCS5Padding", new String(Base64.getDecoder().decode(pwdEnc)), key, iv);//NON-NLS

                    //add decrypted things to new PasswordRow
                    PasswordRow pr = new PasswordRow(id, descDec, loginDec, pwdDec, ivEnc, visible);

                    //new PasswordRow to list
                    list.add(pr);
                }
            }
        } catch (Exception e){
            AlertsUtil.showExceptionStackTraceDialog(e);
        }

        return list;
    }

    /**
     * This class is used to store the password information
     */
    public static class PasswordRow {
        private final SimpleStringProperty id;
        private final SimpleStringProperty desc;
        private final SimpleStringProperty login;
        private final SimpleStringProperty pwd;
        private final SimpleStringProperty iv;
        private final SimpleStringProperty activePwd;

        // Creating a new PasswordRow object.
        private PasswordRow(String id, String desc, String login, String pwd, String iv, boolean showed){
            this.id = new SimpleStringProperty(id);
            this.desc = new SimpleStringProperty(desc);
            this.login = new SimpleStringProperty(login);
            this.pwd = new SimpleStringProperty(pwd);
            this.iv = new SimpleStringProperty(iv);
            SimpleStringProperty hiddenPwd = new SimpleStringProperty("*".repeat(10));
            this.activePwd = showed ? this.pwd : hiddenPwd;
        }

        /**
         * Get the value of the id property
         *
         * @return The id of the object.
         */
        public String getId() {
            return id.get();
        }

        /**
         * It sets the id of the question to the id passed in.
         *
         * @param id The id of the parameter.
         */
        public void setId(String id) {
            this.id.set(id);
        }

        /**
         * It returns the description of the item.
         *
         * @return The getter method returns the value of the private field.
         */
        public String getDesc() {
            return desc.get();
        }

        /**
         * It sets the description of the question.
         *
         * @param desc The description of the parameter.
         */
        public void setDesc(String desc) {
            this.desc.set(desc);
        }

        /**
         * Get the value of the login property
         *
         * @return The getter method returns the value of the login field.
         */
        public String getLogin() {
            return login.get();
        }

        /**
         * It sets the login of the user.
         *
         * @param login The name of the parameter.
         */
        public void setLogin(String login) {
            this.login.set(login);
        }

        /**
         * Get the value of the password field
         *
         * @return The password.
         */
        public String getPwd() {
            return pwd.get();
        }

        /**
         * It sets the password.
         *
         * @param pwd The password to be set.
         */
        public void setPwd(String pwd) {
            this.pwd.set(pwd);
        }

        /**
         * It returns the iv value.
         *
         * @return The iv field is a volatile field.
         *         The get method returns the value of the field.
         */
        public String getIv() {
            return iv.get();
        }

        /**
         * Returns the iv property
         *
         * @return A property object.
         */
        public SimpleStringProperty ivProperty() {
            return iv;
        }

        /**
         * It sets the iv variable to the value of the iv parameter.
         *
         * @param iv The initialization vector.
         */
        public void setIv(String iv) {
            this.iv.set(iv);
        }


        /**
         * This function returns the value of the activePwd variable
         *
         * @return The value of the activePwd field.
         */
        public String getActivePwd() {
            return activePwd.get();
        }

    }
}
