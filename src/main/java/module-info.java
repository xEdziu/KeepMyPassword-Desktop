module me.goral.keepmypassworddesktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires de.mkammerer.argon2;
    requires de.mkammerer.argon2.nolibs;
    requires org.bouncycastle.provider;
    requires passay;

    opens me.goral.keepmypassworddesktop to javafx.fxml;
    exports me.goral.keepmypassworddesktop;
    exports me.goral.keepmypassworddesktop.controllers;
    opens me.goral.keepmypassworddesktop.controllers to javafx.fxml;
}