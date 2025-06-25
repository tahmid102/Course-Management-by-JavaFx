package src.drimjavafxproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {


        public Button submitButton;
        @FXML
        private TextField usernameField;
        @FXML
        private PasswordField passwordField;
        @FXML private Label errorLabel;
        public Label welcomeLabel;
        @FXML
        public ComboBox<String> roleBox;

        @FXML
        public void initialize() {
                roleBox.getItems().addAll("Student", "Teacher");
        }




        public void onSubmit(ActionEvent actionEvent) {

        }


}
