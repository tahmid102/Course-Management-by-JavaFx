package src.drimjavafxproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
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

        public Button cancelButton;

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


                usernameField.setOnAction(e-> passwordField.requestFocus());
                passwordField.setOnAction(e-> submitButton.requestFocus());

        }




        public void onSubmit(ActionEvent actionEvent) throws IOException {
                String username=usernameField.getText().trim().toLowerCase();
                String password=passwordField.getText();

                int enteredId=Integer.parseInt(username);
                if(A.isStudentAvailable(enteredId)){
                        if(A.searchStudent(enteredId).getStudentPassword().equals(password)){
                                errorLabel.setText("Login successful!");
                                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Dashboard.fxml"));
                                Scene scene=new Scene(fxmlLoader.load(),1000,1000);
                                Stage stage = (Stage) submitButton.getScene().getWindow();
                                stage.setScene(scene);
                                stage.setTitle("Dash");
                                stage.show();

                        }
                        else{
                                errorLabel.setText("Wrong Password");
                        }
                }
                else{
                        errorLabel.setText("Invalid credentials");
                }
                usernameField.setDisable(true);
                passwordField.setDisable(true);
                usernameField.setVisible(false);
                passwordField.setVisible(false);
                roleBox.setVisible(false);
                roleBox.setDisable(false);


        }
        public void onCancel(){
                Stage stage = (Stage) cancelButton.getScene().getWindow();
                stage.close();
        }

}
