package me.goral.keepmypassworddesktop.util;

import me.goral.keepmypassworddesktop.database.DatabaseHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static me.goral.keepmypassworddesktop.util.AlertsUtil.showErrorDialog;
import static me.goral.keepmypassworddesktop.util.AlertsUtil.showInformationDialog;

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
                writeConfFile(init);
                DatabaseHandler.createDatabase();
                DatabaseHandler.createMainTable();
            } else {
                AlertsUtil.showErrorDialog("Error Dialog", "Whoops!", "Configuration file already exists");
            }
        } catch (IOException e) {
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
    }

    public static void writeConfFile(String s) {
        try {
            FileWriter fw = new FileWriter("conf.conf");
            fw.write(s);
            fw.close();
        } catch (IOException e){
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
    }

    public static String readConfigFile() {
        try {
            return Files.readString(Paths.get("conf.conf"));
        } catch (IOException e){
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
        return null;
    }

    public static void deleteConfFiles() {
        try {
            File f = new File("conf.conf");
            if (f.delete()){
                File db = new File("database.db");
                if (db.delete()){
                    showInformationDialog("Information Dialog", "Your account is now deleted", "Have a great day!");
                }
            } else showErrorDialog("Something went wrong", "Whoops!", "Sorry, but something went wrong. " +
                    "Please, raise an issue on github and describe what happened.");
        } catch (Exception e){
            AlertsUtil.showExceptionStackTraceDialog(e);
        }

    }
}
