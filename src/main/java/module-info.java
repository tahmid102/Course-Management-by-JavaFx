module src.drimjavafxproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens src.drimjavafxproject to javafx.fxml;
    exports src.drimjavafxproject;
}