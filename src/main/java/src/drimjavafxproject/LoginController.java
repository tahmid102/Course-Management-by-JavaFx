package src.drimjavafxproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {


        public Button submitButton;
        @FXML
        private TextField usernameField;
        @FXML
        private PasswordField passwordField;
        @FXML private Label errorLabel;


        public void onSubmit(ActionEvent actionEvent) {
            submitButton.fire();
        }


}
