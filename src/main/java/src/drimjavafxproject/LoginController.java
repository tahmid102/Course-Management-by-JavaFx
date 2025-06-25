package src.drimjavafxproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Locale;

public class LoginController {


        public Button submitButton;
        @FXML
        private TextField usernameField;
        @FXML
        private PasswordField passwordField;
        @FXML public Label errorLabel;
        public Label welcomeLabel;
        @FXML
        public ComboBox<String> roleBox;

        @FXML
        public void initialize() {
                roleBox.getItems().addAll("Student", "Teacher");
        }




        public void onSubmit(ActionEvent actionEvent) {
                String username=usernameField.getText().trim().toLowerCase();
                String password=passwordField.getText();
                usernameField.setDisable(true);
                passwordField.setDisable(true);
                usernameField.setVisible(false);
                passwordField.setVisible(false);
                roleBox.setVisible(false);
                roleBox.setVisible(false);
                if (username.equals("Mufeed") && password.equals("1234")) {

                        errorLabel.setText("Login successful!");
                } else {
                        errorLabel.setText("Invalid credentials");
                }

        }

}
