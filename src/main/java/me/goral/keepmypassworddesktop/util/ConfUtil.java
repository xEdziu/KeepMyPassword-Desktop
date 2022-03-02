package me.goral.keepmypassworddesktop.util;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import me.goral.keepmypassworddesktop.MainApp;
import me.goral.keepmypassworddesktop.controllers.MainAppController;
import me.goral.keepmypassworddesktop.database.DatabaseHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfUtil {

    public static boolean checkIfConfigExists(){
        File tmp = new File("conf.conf");
        File database = new File("database.db");
        return tmp.exists() && database.exists();
    }

    public static void createConfFile(String init) {
        try {
            File f = new File("conf.conf");
            if(f.createNewFile()){
                System.out.println("File created");
                writeConfFile(init);
                DatabaseHandler.createDatabase();
                DatabaseHandler.createMainTable();
            } else {
                System.out.println("File already exists");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeConfFile(String s) throws IOException {
        FileWriter fw = new FileWriter("conf.conf");
        fw.write(s);
        fw.close();
    }

    public static String readConfigFile() throws IOException {
        return Files.readString(Paths.get("conf.conf"));
    }

    public static void deleteConfFiles() {
        try {
            File f = new File("conf.conf");
            if (f.delete()){
                System.out.println("Conf File deleted");
                File db = new File("database.db");
                if (db.delete()){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText("Your account is now deleted");
                    alert.setContentText("Have a great day!");
                    alert.getButtonTypes().clear();
                    alert.getDialogPane().getStylesheets().add(MainApp.class.getResource("styles/dialog.css").toExternalForm());
                    alert.setGraphic(new ImageView(MainApp.class.getResource("/me/goral/keepmypassworddesktop/images/information-64.png").toString()));

                    Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                    stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/me/goral/keepmypassworddesktop/images/access-32.png")));

                    ButtonType btnConfirm = new ButtonType("Great");
                    alert.getDialogPane().getButtonTypes().add(btnConfirm);

                    Node confirm = alert.getDialogPane().lookupButton(btnConfirm);
                    confirm.getStyleClass().add("btn");

                    alert.showAndWait();
                }
            } else System.out.println("Failed to delete conf file");
        } catch (Exception e){
            MainAppController.showErrorDialog("Something went wrong", "Whoops!", "Sorry, but something went wrong. " +
                    "Please, raise an issue on github and describe what happened.");
        }

    }
}
