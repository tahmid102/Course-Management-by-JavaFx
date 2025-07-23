module src.drimjavafxproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.dynalink;
    requires org.jetbrains.annotations;
    requires java.desktop;
    requires java.xml.crypto;


    opens files to javafx.fxml;
    exports files;
    exports files.Classes;
    opens files.Classes to javafx.fxml;
    exports files.Controllers;
    opens files.Controllers to javafx.fxml;
}