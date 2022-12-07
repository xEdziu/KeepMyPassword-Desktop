package me.goral.keepmypassworddesktop.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.zip.ZipOutputStream;

import static me.goral.keepmypassworddesktop.util.ConfUtil.zipFile;

public class BackupUtil {

    LocalDate l = LocalDate.now();
    private String date = l.getDayOfWeek().name() + l.getMonth().name() + l.getYear();
    private String workingDirectory = new String();

    private void setBackupDirectory(){
        int os = ConfUtil.detectOS();
        if (os == 1)
            workingDirectory = System.getenv("AppData") + "\\KeepMyPasswordBackup\\" + date + "\\";
        else if (os == 2)
            workingDirectory = System.getProperty("user.home") + "/Library/KeepMyPasswordBackup/" + date + "/";
        else if (os == 3)
            workingDirectory = System.getProperty("user.home") + "/.config/KeepMyPasswordBackup/" + date + "/";

        File workingDir = new File(workingDirectory);
        workingDir.mkdir();
    }

    public void createBackup(String date) throws IOException {
        try {
            String outZip = date + "Backup.zip";
            String sourceFile = ConfUtil.getWorkingDirectory();
            FileOutputStream fos = new FileOutputStream(outZip);
            ZipOutputStream zipOut = new ZipOutputStream(fos);

            File fileToZip = new File(sourceFile);
            zipFile(fileToZip, fileToZip.getName(), zipOut);
            zipOut.close();
            fos.close();
        } catch (Exception e){
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
    }

    public void restoreBackup() {

    }


}
