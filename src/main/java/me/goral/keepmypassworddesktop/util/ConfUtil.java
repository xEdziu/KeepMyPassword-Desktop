package me.goral.keepmypassworddesktop.util;

import me.goral.keepmypassworddesktop.database.DatabaseHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;


public class ConfUtil {


    private static final String confFileName = "config.conf";
    private static final String databaseFileName = "database.db";
    private static String workingDirectory;

    /**
     * The function will detect the operating system and set the working directory to the appropriate
     * location
     * 
     * @return The OS number (1 => Windows, 2 => Mac, 3 => Unix).
     */
    public static int setWorkingDirectory(){
        int os = detectOS();
        switch (os) {
            case 1 -> workingDirectory = System.getenv("AppData") + "\\KeepMyPassword\\";//NON-NLS
            case 2 -> workingDirectory = System.getProperty("user.home") + "/Library/KeepMyPassword/";//NON-NLS
            case 3 -> workingDirectory = System.getProperty("user.home") + "/KeepMyPassword/";//NON-NLS
            default -> {
            }
        }
        File workingDir = new File(workingDirectory);
        workingDir.mkdir();
        return os;
    }

    /**
     * Returns the current working directory
     * 
     * @return The working directory.
     */
    public static String getWorkingDirectory() {
        return workingDirectory;
    }

    /**
     * Check if the config file exists
     * 
     * @return The method returns a boolean value.
     */
    public static boolean checkIfConfigExists(){
        File tmp = new File(workingDirectory + confFileName);
        return tmp.exists();
    }

    /**
     * Check if the database exists
     * 
     * @return The method returns a boolean value.
     */
    public static boolean checkIfDatabaseExists(){
        File database = new File(workingDirectory + databaseFileName);
        return database.exists();
    }

    /**
     * Creates a new configuration file if it doesn't exist, otherwise throws an error
     * 
     * @param init The initial configuration String.
     */
    public static void createConfFiles(String init) {
        try {
            File f = new File(workingDirectory + confFileName);
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

    /**
     * Write the given string to the conf file
     * 
     * @param s The configuration string to write to the file.
     */
    public static void writeConfFile(String s) {
        try {
            FileWriter fw = new FileWriter(workingDirectory + confFileName);
            fw.write(s);
            fw.close();
        } catch (IOException e){
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
    }

    /**
     * Reads the configuration file and returns the contents as a string.
     * 
     * @return The configuration file as a string.
     */
    public static String readConfigFile() {
        try {
            String confString = Files.readString(Paths.get(workingDirectory + confFileName));
            if (confString.isEmpty() ){
                deleteConfFiles();
                AlertsUtil.showErrorDialog("Error", "Configuration file is empty.", "Deleting account. Please restart program.");
            } else {
                return confString;
            }
        } catch (Exception e){
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
        return null;
    }

    /**
     * Delete the conf file and the database file
     */
    public static void deleteConfFiles() {
        try {
            File f = new File(workingDirectory + confFileName);
            File db = new File(workingDirectory + databaseFileName);
            if (f.exists()){
                if (!f.delete()) throw new Exception("Configuration file could not be deleted");
            }
            if (db.exists()){
                if (!db.delete()) throw new Exception("Database could not be deleted");
            }

        } catch (Exception e){
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
    }

    /**
     * Detects the operating system and returns an integer
     * 
     * @return The number of the OS (1 => Windows, 2 => Mac, 3 => Unix).
     */
    public static int detectOS() {
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (os.contains("win")) return 1;//NON-NLS
        else if (os.contains("osx")) return 2;//NON-NLS
        else if (os.contains("nix") || os.contains("aix") || os.contains("nux")) return 3;//NON-NLS
        return 0;
    }
}
