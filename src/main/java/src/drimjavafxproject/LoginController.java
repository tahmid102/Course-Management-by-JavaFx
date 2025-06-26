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
        Student a=new Student("tahmid",2305180);
        Student b=new Student("mufeed",2305151);
        StudentList A=new StudentList();

        @FXML
        public void initialize() {
                roleBox.getItems().addAll("Student", "Teacher");
                a.setStudentPassword("tahmid");
                b.setStudentPassword("mufeed");
                A.addStudent(a);
                A.addStudent(b);

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
//                if (username.equals(A.) && password.equals("1234")) {
//
//                        errorLabel.setText("Login successful!");
//                } else {
//                        errorLabel.setText("Invalid credentials");
//                }
                int enteredId=Integer.parseInt(username);
                if(A.isStudentAvailable(enteredId)){
                        if(A.searchStudent(enteredId).getStudentPassword().equals(password)){
                                errorLabel.setText("Login successful!");
                        }
                        else{
                                errorLabel.setText("Wrong Password");
                        }
                }
                else{
                        errorLabel.setText("Invalid credentials");
                }

        }

}
