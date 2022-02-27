package me.goral.keepmypassworddesktop.util;

import me.goral.keepmypassworddesktop.database.DatabaseHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfUtil {

    public static boolean checkIfConfigExists(){
        File tmp = new File("conf.conf");
        return tmp.exists();
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

    public static void deleteConfFile() {
        //will be useful in future if user want to wipe their data
        File f = new File("conf.conf");
        if (f.delete()){
            System.out.println("File deleted");
            //do something here, like alert
        } else System.out.println("Failed to delete conf file");
    }
}
