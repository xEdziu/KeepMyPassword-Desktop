package me.goral.keepmypassworddesktop.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import me.goral.keepmypassworddesktop.MainApp;
import me.goral.keepmypassworddesktop.database.DatabaseHandler;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class ConfUtil {


    private static final String confFileName = "config.conf";
    private static final String databaseFileName = "database.db";
    private static String workingDirectory;

    public static Path getConfFilePath() {
        return Path.of(workingDirectory + confFileName);
    }

    public static Path getDatabaseFilePath(){
        return Path.of(workingDirectory + databaseFileName);
    }

    /**
     * The function will detect the operating system and set the working directory to the appropriate
     * location
     * 
     * @return The OS number (1 => Windows, 2 => Mac, 3 => Unix).
     */
    public static int setWorkingDirectory(){
        int os = detectOS();
        switch (os) {
            case 1 -> {
                workingDirectory = System.getenv("AppData") + "\\KeepMyPassword\\";//NON-NLS
                if (!checkIfWorkingDirExists())
                    createWorkingDir(workingDirectory);
            }
            case 2 -> workingDirectory = System.getProperty("user.home") + "/Library/KeepMyPassword/";//NON-NLS
            case 3 -> workingDirectory = System.getProperty("user.home") + "/.config/KeepMyPassword/";//NON-NLS
            default -> {
            }
        }
        File workingDir = new File(workingDirectory);
        workingDir.mkdir();
        return os;
    }

    /**
     * Check if the working directory exists
     *
     * @return The method returns a boolean value.
     */
    public static boolean checkIfWorkingDirExists(){
        File tmp = new File(workingDirectory);
        return tmp.exists();
    }

    /**
     * Creates a new working directory if it doesn't exist, otherwise prints an error
     *
     * @param workDir The working directory.
     */
    public static void createWorkingDir(String workDir) {
        File f = new File(workingDirectory);
        if (!f.mkdir()) {
            System.out.println("Directory cannot be created");
        }
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
                AlertsUtil.showErrorDialog(MainApp.lang.getString("error.dialog.title"), MainApp.lang.getString("whoops"),
                        MainApp.lang.getString("conf-file-exists-err"));
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
                AlertsUtil.showErrorDialog(MainApp.lang.getString("error"), MainApp.lang.getString("conf-file-empty-err"),
                        MainApp.lang.getString("del-acc-restart-program"));
            } else {
                return confString;
            }
        } catch (Exception e){
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
        return null;
    }

    /**
     * If the config file exists, read the config file and return the language. If the config file doesn't exist, return
     * the default language
     *
     * @return The last part of the config file, which is the language.
     */
    public static String getConfigLanguage() {
        if(!checkIfConfigExists()){
            return "en-US";//NON-NLS
        } else {
            try {
                return readConfigFile().split(":")[readConfigFile().split(":").length - 1].trim();
            } catch (Exception e){
                AlertsUtil.showExceptionStackTraceDialog(e);
            }
        }
        return "en-US";//NON-NLS
    }

    /**
     * This function changes the language of the application
     *
     * @param lang The language you want to change to.
     */
    public static void changeLanguage(String lang) {
        if(checkIfConfigExists()){
            String[] conf = readConfigFile().split(":");
            int confLength = conf.length;
            conf[confLength - 1] = lang;
            String newConfig = String.join(":", conf);
            writeConfFile(newConfig);
        }
    }

    /**
     * Delete the conf file and the database file
     */
    public static void deleteConfFiles() {
        try {
            File f = new File(workingDirectory + confFileName);
            File db = new File(workingDirectory + databaseFileName);
            if (f.exists()){
                if (!f.delete()) AlertsUtil.showExceptionStackTraceDialog(new Exception("Configuration file could not be deleted"));
            }
            if (db.exists()){
                if (!db.delete()) AlertsUtil.showExceptionStackTraceDialog(new Exception("Database could not be deleted"));
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
        else if (os.contains("os x") || os.contains("osx")) return 2;//NON-NLS
        else if (os.contains("nix") || os.contains("aix") || os.contains("nux")) return 3;//NON-NLS
        return 0;
    }


    /**
     * Given a directory, return a set of all the files in that directory
     *
     * @param dir The directory to list files from.
     * @return A set of strings.
     */
    private static Set<String> listFiles(String dir){
        Set<String> listed = new HashSet<>();
        File directory = new File(dir);
        if (!directory.isDirectory()) return null;
        File[] fileList = directory.listFiles();
        for (File f : fileList){
            listed.add(f.getName());
        }
        return listed;
    }

    /**
     * Reads the languages from the jar file / ide scope project
     *
     * @return An ObservableList of languages names.
     */
    public static ObservableList<String> readLanguages(){
        try {
            CodeSource src = MainApp.class.getProtectionDomain().getCodeSource();

            URI uri = MainApp.class.getResource("").toURI();
            URL jar = src.getLocation();
            if (uri.getScheme().equals("jar")){//NON-NLS
                ZipInputStream zip = new ZipInputStream(jar.openStream());
                ObservableList<String> options = FXCollections.observableArrayList();
                while (true){
                    ZipEntry e = zip.getNextEntry();
                    if (e == null) break;
                    String name = e.getName();
                    if (name.startsWith("language_")){//NON-NLS
                        String[] tmp = name.split("\\.");
                        String actual = tmp[0].split("_")[1];
                        options.add(actual);
                    }
                }
                return options;
            } else {
                String path = MainApp.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "/";//NON-NLS
                Set<String> files = listFiles(path);//NON-NLS
                ObservableList<String> options = FXCollections.observableArrayList();

                if (files != null) {
                    for (String file : files){
                        if (file.startsWith("language")){//NON-NLS
                            String[] tmp = file.split("\\.");
                            String actual = tmp[0].split("_")[1];
                            options.add(actual);
                        }
                    }
                }
                return options;
            }
        } catch (Exception e){
            AlertsUtil.showExceptionStackTraceDialog(e);
        }

        return null;
    }
}
