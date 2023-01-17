package me.goral.keepmypassworddesktop.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.stage.FileChooser;
import me.goral.keepmypassworddesktop.MainApp;
import me.goral.keepmypassworddesktop.controllers.MainAppController;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class BackupUtil {

    static LocalDate l = LocalDate.now();
    private static final String date = String.valueOf(l);
    private static String workingDirectory = "";

    private static void setBackupDirectory(){
        int os = ConfUtil.detectOS();
        if (os == 1)
            workingDirectory =  System.getenv("AppData") + "\\KeepMyPasswordBackup\\"+date+"\\";
        else if (os == 2)
            workingDirectory = System.getProperty("user.home") + "/Library/KeepMyPasswordBackup/"+date+"/";
        else if (os == 3)
            workingDirectory = System.getProperty("user.home") + "/.config/KeepMyPasswordBackup/"+date+"/";

        System.out.println(workingDirectory);

        File workingDir = new File(workingDirectory);
        boolean res = workingDir.mkdirs();

        System.out.println(res);
    }

    public static boolean createBackup() {
        try {
            setBackupDirectory();
            Files.copy(
                    ConfUtil.getConfFilePath(), Paths.get(workingDirectory + date+"config.conf"), REPLACE_EXISTING
            );
            Files.copy(
                    ConfUtil.getDatabaseFilePath(), Paths.get(workingDirectory + date+"database.db"), REPLACE_EXISTING
            );
            return true;
        } catch (Exception e){
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
        return false;
    }

    public static void restoreBackup(DialogPane dialogPane) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose backup files");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("KMP-CONFIG", "*.conf", "*.db")
        );
        fileChooser.setInitialDirectory(new File(ConfUtil.getWorkingDirectory()));
        List<File> fileList;
        fileList = fileChooser.showOpenMultipleDialog(dialogPane.getScene().getWindow());
        try {
            if (fileList != null && fileList.size() == 2){
                for (File file : fileList){
                    Files.copy(file.toPath(),
                            Paths.get(ConfUtil.getWorkingDirectory()+file.getName().substring(10)),
                            REPLACE_EXISTING);
                }
            } else {
                AlertsUtil.showErrorDialog("Error", "You have chosen wrong files","Please try again");
                return;
            }
            AlertsUtil.showInformationDialog("Success", "Backup has been restored",
                    "Now you will be logged out");
            try {
                FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("layouts/main-app-view.fxml"));
                Parent root = loader.load();
                Scene sc = new Scene(root);
                String css = MainApp.class.getResource("styles/main.css").toExternalForm();
                sc.getStylesheets().add(css);
                MainApp.getStage().setScene(sc);
                ConfUtil.deleteConfFiles();
                MainAppController controller = loader.getController();
                controller.handleAppRun();

            } catch (Exception e){
                AlertsUtil.showExceptionStackTraceDialog(e);
            }
        } catch (Exception e){
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
    }

}