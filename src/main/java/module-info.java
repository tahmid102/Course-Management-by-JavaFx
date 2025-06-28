module src.drimjavafxproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens files to javafx.fxml;
    exports files;
    exports files.Classes;
    opens files.Classes to javafx.fxml;
    exports files.Controllers;
    opens files.Controllers to javafx.fxml;
}