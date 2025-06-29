package files.Controllers;


import files.Classes.Student;
import files.Classes.StudentHashMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

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

        StudentHashMap A=new StudentHashMap();

        @FXML
        public void initialize(){
                roleBox.getItems().addAll("Student", "Teacher");
                A.initializeStudents();

                usernameField.setOnAction(e-> passwordField.requestFocus());
                passwordField.setOnAction(e-> submitButton.requestFocus());

        }




        public void onSubmit(ActionEvent actionEvent){
                String selectedRole=roleBox.getValue();
                if(selectedRole == null || selectedRole.isBlank()){
                        errorLabel.setText("Please select a role");
                }
                String username=usernameField.getText().trim().toLowerCase();
                String password=passwordField.getText();


            try {
                int enteredId=Integer.parseInt(username);
                if(A.isStudentAvailable(enteredId)){
                        if(A.searchStudent(enteredId).getPassword().equals(password)){
                                errorLabel.setText("Login successful!");
                                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
                                Scene scene=new Scene(fxmlLoader.load());
                                Stage stage = (Stage) submitButton.getScene().getWindow();
                                stage.setScene(scene);
                                stage.setTitle("Dash");
                                stage.show();

                                usernameField.setDisable(true);
                                passwordField.setDisable(true);
                                usernameField.setVisible(false);
                                passwordField.setVisible(false);
                                roleBox.setVisible(false);
                                roleBox.setDisable(false);

                        }
                        else{
                                errorLabel.setText("Wrong Password");
                        }
                }
                else{
                        errorLabel.setText("Invalid credentials");
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("Please enter numeric ID");
            } catch (IOException e) {
                errorLabel.setText("An error occurred !");
            }


        }
        public void onCancel(){
                Alert cancelAlert = new Alert(Alert.AlertType.CONFIRMATION);
                cancelAlert.setTitle("Quit");
                cancelAlert.setHeaderText("Quitting Application");
                cancelAlert.setContentText("Are you sure you want to continue?");
                Optional<ButtonType> result = cancelAlert.showAndWait();
                if( result.isPresent() && result.get()==ButtonType.OK){
                        Stage stage = (Stage) cancelButton.getScene().getWindow();
                        stage.close();
                }

        }

}
