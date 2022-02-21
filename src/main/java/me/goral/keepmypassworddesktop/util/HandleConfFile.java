package me.goral.keepmypassworddesktop.util;

import me.goral.keepmypassworddesktop.database.DatabaseHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HandleConfFile {

    public static boolean checkIfConfigExists(){
        File tmp = new File("conf.txt");
        return tmp.exists();
    }

    public static void createConfFile() {
        try {
            File f = new File("conf.txt");
            if(f.createNewFile()){
                System.out.println("File created");
                FileWriter fw = new FileWriter("conf.txt");
                String timeStamp = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss").format(new Date());
                fw.write(timeStamp);
                fw.close();
                DatabaseHandler.createDatabase();
            } else {
                System.out.println("File already exists");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteConfFile() {
        //will be useful in future if user want to wipe their data
        File f = new File("conf.txt");
        if (f.delete()){
            System.out.println("File deleted");
            //do something here, like alert
        } else System.out.println("Failed to delete conf file");
    }
}
