package files.Controllers;


import files.Classes.StudentHashMap;
import files.Classes.TeacherHashMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class LoginController {
    //TODO:LOGIN
    @FXML public AnchorPane loginAnchorPane;
    @FXML public TextField userIDField;
    @FXML public PasswordField passwordField;
    @FXML public Button submitButton;
    @FXML public ComboBox<String> roleBox;
    @FXML public Button cancelButton;
    @FXML public Hyperlink registerHyperlink;
    @FXML public Label errorLabel;
    //TODO:REGISTER
    @FXML public PasswordField setPasswordField;
    @FXML public Button signUpButton;
    @FXML public ComboBox<String> roleBoxSetup;
    @FXML public PasswordField confirmPasswordField;
    @FXML public Hyperlink LoginHyperlink;
    @FXML public AnchorPane signUpAnchorPane;
    @FXML public TextField userIDsetField;

    //TODO:MAIN PANE
    @FXML private StackPane loginStackPane;

    //TODO:STUDENT and TEACHER HASHES
    private final StudentHashMap students = new StudentHashMap();
    private final TeacherHashMap teachers = new TeacherHashMap();
    @FXML public void initialize(){
        loginAnchorPane.setVisible(true);
        signUpAnchorPane.setVisible(false);

        roleBox.getItems().addAll("Student", "Teacher","Admin");
        roleBox.setOnAction(e->roleBox.requestFocus());
        userIDField.setOnAction(e-> passwordField.requestFocus());
        passwordField.setOnAction(e-> submitButton.requestFocus());

    }
    @FXML public void onSubmit(ActionEvent actionEvent){
        String role = roleBox.getValue();
        String idText = userIDField.getText().trim();
        String password = passwordField.getText();

        if (role == null || idText.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Fill in all fields");
            return;
        }

        try {
            int id = Integer.parseInt(idText);
            if (role.equals("Student")) {
                students.initializeStudents();
                if (students.isStudentAvailable(id)) {
                    if (students.searchStudent(id).getPassword().equals(password)) {
                        goToDashboard(id);
                    } else {
                        errorLabel.setText("Wrong Password");
                    }
                } else {
                    errorLabel.setText("Student ID not found");
                }
            } else if (role.equals("Teacher")) {
                teachers.initializeTeachers();
                if (teachers.isTeacherAvailable(id)) {
                    if (teachers.searchTeacher(id).getPassword().equals(password)) {
                        goToTeacherDashboard(id); // make this if needed
                    } else {
                        errorLabel.setText("Wrong Password");
                    }
                } else {
                    errorLabel.setText("Teacher ID not found");
                }
            }
            else if(role.equals("Admin")){}

        } catch (NumberFormatException e) {
            errorLabel.setText("User ID must be numeric");
        } catch (IOException e) {
            errorLabel.setText("Something went wrong loading the dashboard");
        }
    }


    //TODO:DASHBOARD
    private void goToDashboard(int enteredId) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
        Scene scene=new Scene(fxmlLoader.load());
        DashboardController controller=fxmlLoader.getController();
        controller.setCurrentStudent(students.searchStudent(enteredId));
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Dashboard");
        stage.show();
    }
    private void goToTeacherDashboard(int enteredID) throws IOException{

    }
    //TODO: NAVIGATION
    @FXML private void onRegisterClick() {
        loginAnchorPane.setVisible(false);
        signUpAnchorPane.setVisible(true);
        userIDField.requestFocus();
    }

    @FXML private void onLoginClick() {
        signUpAnchorPane.setVisible(false);
        loginAnchorPane.setVisible(true);
        userIDField.requestFocus();
    }
    @FXML public void onCancel() {
        Alert cancelAlert = new Alert(Alert.AlertType.CONFIRMATION);
        cancelAlert.setTitle("Quit");
        cancelAlert.setHeaderText("Quitting Application");
        cancelAlert.setContentText("Are you sure you want to continue?");
        Optional<ButtonType> result = cancelAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        }
    }
}
