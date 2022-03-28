package me.goral.keepmypassworddesktop;

import me.goral.keepmypassworddesktop.util.ConfUtil;

public class MainClass {
    public static void main(String[] args) {
        ConfUtil.setWorkingDirectory();
        MainApp.main(args);
    }
}
