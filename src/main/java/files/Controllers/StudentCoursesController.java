package files.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class StudentCoursesController extends DashboardController{
    public Button backButton;
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void toDash(ActionEvent actionEvent) {
        super.onHomeButton(actionEvent);
    }
}
