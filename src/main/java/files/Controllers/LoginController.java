package files.Controllers;

import files.Classes.*;
import files.Server.*;
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
    @FXML public TextField setUserIDField;
    @FXML public Label registerErrorLabel;
    @FXML public TextField setNameField;

    //TODO:MAIN PANE
    @FXML private StackPane loginStackPane;

    //TODO:STUDENT and TEACHER HASHES
    private final StudentList students = new StudentList();
    private final TeacherList teachers = new TeacherList();
    private final PendingStudentsList pendingStudents = new PendingStudentsList();
    private final PendingTeachersList pendingTeachers = new PendingTeachersList();

    @FXML public void initialize(){
        loginAnchorPane.setVisible(true);
        signUpAnchorPane.setVisible(false);

        roleBox.getItems().addAll("Student", "Teacher","Admin");
        roleBoxSetup.getItems().addAll("Student","Teacher");
        roleBox.setOnAction(e->roleBox.requestFocus());
        userIDField.setOnAction(e-> passwordField.requestFocus());
        passwordField.setOnAction(e-> submitButton.requestFocus());

    }
    //TODO:LOGIN PAGE SUBMISSION
    @FXML public void onSubmit(ActionEvent actionEvent){
        errorLabel.setText("");
        String role = roleBox.getValue();
        String idText = userIDField.getText().trim();
        String password = passwordField.getText();

        if (role == null || idText.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Fill in all fields");
            return;
        }

        try {
            int id = Integer.parseInt(idText);
            switch (role) {
                case "Student" -> {
                    // Try to load data from network first, fallback to local
                    if (NetworkLoader.isServerAvailable()) {
                        NetworkLoader.loadAll();
                        if (NetworkLoader.studentList.isStudentAvailable(id)) {
                            if (NetworkLoader.studentList.searchStudent(id).getPassword().equals(password)) {
                                goToDashboard(id);
                            } else {
                                errorLabel.setText("Wrong Password");
                            }
                        } else {
                            errorLabel.setText("Student ID not found");
                        }
                    } else {
                        // Fallback to local data
                        students.initializeStudents();
                        students.LoadCourse();
                        if (students.isStudentAvailable(id)) {
                            if (students.searchStudent(id).getPassword().equals(password)) {
                                goToDashboard(id);
                            } else {
                                errorLabel.setText("Wrong Password");
                            }
                        } else {
                            errorLabel.setText("Student ID not found");
                        }
                    }
                }
                case "Teacher" -> {
                    // Try to load data from network first, fallback to local
                    if (NetworkLoader.isServerAvailable()) {
                        NetworkLoader.loadAll();
                        if (NetworkLoader.teacherList.isTeacherAvailable(id)) {
                            if (NetworkLoader.teacherList.searchTeacher(id).getPassword().equals(password)) {
                                goToTeacherDashboard(id);
                            } else {
                                errorLabel.setText("Wrong Password");
                            }
                        } else {
                            errorLabel.setText("Teacher ID not found");
                        }
                    } else {
                        // Fallback to local data
                        teachers.initializeTeachers();
                        teachers.loadCourses();
                        if (teachers.isTeacherAvailable(id)) {
                            if (teachers.searchTeacher(id).getPassword().equals(password)) {
                                goToTeacherDashboard(id);
                            } else {
                                errorLabel.setText("Wrong Password");
                            }
                        } else {
                            errorLabel.setText("Teacher ID not found");
                        }
                    }
                }
                case "Admin" -> {
                    if(Admin.getAdminInstance().verifyCredentials(id,password)){
                        System.out.println("Admin logs in");
                        goToAdminDashboard();
                    }
                    else{
                        errorLabel.setText("Admin credentials incorrect");
                    }
                }
            }

        } catch (NumberFormatException e) {
            errorLabel.setText("User ID must be numeric");
        } catch (IOException e) {
            errorLabel.setText("Something went wrong loading the dashboard");
        }
        catch (Exception e){
            e.printStackTrace();
            errorLabel.setText("What te fu");
        }
    }
    //TODO:REG PAGE SUBMISSION
    @FXML
    private void onSignUp() {
        errorLabel.setText("");
        String name = setNameField.getText().trim();
        String idText = setUserIDField.getText().trim();
        String password = setPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String role = roleBoxSetup.getValue();

        if (name.isEmpty() || idText.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || role == null) {
            registerErrorLabel.setText("All fields are required");
            return;
        }

        if (!password.equals(confirmPassword)) {
            registerErrorLabel.setText("Passwords do not match");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            registerErrorLabel.setText("User ID must be numeric");
            return;
        }

        if (role.equals("Student")) {
            if((id+"").length()!=7) {
                registerErrorLabel.setText("Student ID should be a 7 digit integer");
                return;
            }
            if(password.length()<4){
                registerErrorLabel.setText("Password must be 4 characters or more");
                return;
            }
            
            // Try network registration first
            RegistrationRequest request = new RegistrationRequest(name, id, password, 
                RegistrationRequest.UserType.STUDENT);
            
            if (NetworkLoader.isServerAvailable()) {
                boolean success = NetworkLoader.submitRegistration(request);
                if (success) {
                    registerErrorLabel.setText("Student request sent! Awaiting admin approval.");
                } else {
                    registerErrorLabel.setText("Registration failed. Please try again.");
                }
            } else {
                // Fallback to local registration
                students.initializeStudents();
                pendingStudents.loadFromFile();
                if (students.isStudentAvailable(id) || pendingStudents.isDuplicate(id)) {
                    registerErrorLabel.setText("Student ID already exists");
                    return;
                }
                pendingStudents.addToPending(new Student(name, id, password));
                registerErrorLabel.setText("Student request sent! Awaiting admin approval.");
            }
        } else if (role.equals("Teacher")) {
            if(password.length()<4){
                registerErrorLabel.setText("Password must be 4 characters or more");
                return;
            }
            
            // Try network registration first
            RegistrationRequest request = new RegistrationRequest(name, id, password, 
                RegistrationRequest.UserType.TEACHER);
            
            if (NetworkLoader.isServerAvailable()) {
                boolean success = NetworkLoader.submitRegistration(request);
                if (success) {
                    registerErrorLabel.setText("Teacher request sent! Awaiting admin approval.");
                } else {
                    registerErrorLabel.setText("Registration failed. Please try again.");
                }
            } else {
                // Fallback to local registration
                teachers.initializeTeachers();
                pendingTeachers.loadFromFile();
                if (teachers.isTeacherAvailable(id) || pendingTeachers.isDuplicate(id)) {
                    registerErrorLabel.setText("Teacher ID already exists");
                    return;
                }
                pendingTeachers.addToPending(new Teacher(name, id, password));
                registerErrorLabel.setText("Teacher request sent! Awaiting admin approval.");
            }
        } else {
            registerErrorLabel.setText("Admin cannot register here");
        }

        setNameField.clear();
        setUserIDField.clear();
        setPasswordField.clear();
        confirmPasswordField.clear();
        roleBoxSetup.getSelectionModel().clearSelection();
    }

    //TODO:DASHBOARD
    private void goToDashboard(int enteredId) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
        Scene scene=new Scene(fxmlLoader.load());
        DashboardController controller=fxmlLoader.getController();
        
        // Use the appropriate student list (network or local)
        Student student = null;
        if (NetworkLoader.isServerAvailable()) {
            student = NetworkLoader.studentList.searchStudent(enteredId);
        } else {
            student = students.searchStudent(enteredId);
        }
        
        controller.setCurrentStudent(student);
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Dashboard");
        stage.show();
    }
    private void goToTeacherDashboard(int enteredID){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/TeacherDashboard.fxml"));
        Scene scene= null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TeacherDasahboardController controller=fxmlLoader.getController();
        
        // Use the appropriate teacher list (network or local)
        Teacher teacher = null;
        if (NetworkLoader.isServerAvailable()) {
            teacher = NetworkLoader.teacherList.searchTeacher(enteredID);
        } else {
            teacher = teachers.searchTeacher(enteredID);
        }
        
        controller.setTeacher(teacher);

        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Teacher Dashboard");
        stage.show();
    }
    private void goToAdminDashboard() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Admin/AdminDashboard.fxml"));
        Scene scene=new Scene(fxmlLoader.load());
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Admin Dashboard");
        stage.show();
    }
    //TODO: NAVIGATION
    @FXML private void onRegisterClick() {
        loginAnchorPane.setVisible(false);
        signUpAnchorPane.setVisible(true);
        setNameField.requestFocus();
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
