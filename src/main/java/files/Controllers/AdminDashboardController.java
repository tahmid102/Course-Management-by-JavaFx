package files.Controllers;

import files.Classes.Course;
import files.Classes.StudentList;
import files.Classes.Teacher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import files.Classes.Student;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {
    @FXML public Label adminTitle;

    //TODO:Student Table Components
    @FXML private TableView<Student> ADstudentTable;
    @FXML private TableColumn<Student, String> ADstudentNameColumn;
    @FXML private TableColumn<Student, String> ADstudentIdColumn;

    //TODO:Teacher Table Components
    @FXML private TableView<Teacher> ADteacherTable;
    @FXML private TableColumn<Teacher, String> ADteacherNameColumn;
    @FXML private TableColumn<Teacher, String> ADteacherEmailColumn;
    @FXML private TableColumn<Teacher, String> ADteacherIdColumn;
    @FXML private TableColumn<Teacher, String> ADteacherDepartmentColumn;

    //TODO:Course Display Components
    @FXML private ListView<Course> ADcourseListView;
    @FXML private TextArea ADcourseDetailsArea;

    //TODO:Action Buttons
    @FXML private Button ADaddStudentButton;
    @FXML private Button ADaddTeacherButton;
    @FXML private Button ADaddCourseButton;
    @FXML private Button ADsignOutButton;
    @FXML private Button ADrefreshButton;

    //TODO:Status Labels
    @FXML private Label ADstudentCountLabel;
    @FXML private Label ADteacherCountLabel;
    @FXML private Label ADcourseCountLabel;


    //TODO:File paths relative to resources
    private final String STUDENTS_FILE = "src/main/resources/database/StudentCredentials.txt";
    private final String TEACHERS_FILE = "src/main/resources/database/TeachersCredentials.txt";
    private final String COURSES_FILE = "src/main/resources/database/Courses.txt";
    private final String PENDING_STUDENTS_FILE = "src/main/resources/database/PendingStudentCredentials.txt";
    private final String PENDING_TEACHERS_FILE = "src/main/resources/database/PendingTeacherCredentials.txt";

    //TODO:Data
    private final StudentList studentList = new StudentList();


    //TODO:Loader files
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupStudentTable();
        ADaddStudentButton.setOnAction(event -> openStudentApprovalWindow());

    }

    private void openStudentApprovalWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddStudentApproval.fxml"));
            URL url = getClass().getResource("/fxml/AddStudentApproval.fxml");
            System.out.println("FXML URL: " + url); // Is it null?

            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Approve Students");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println(e);
        }
    }


    private void setupStudentTable() {
        ADstudentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ADstudentIdColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));

        studentList.initializeStudents();

        ObservableList<Student> studentData = FXCollections.observableArrayList(studentList.getStudents());
        ADstudentTable.setItems(studentData);

        ADstudentCountLabel.setText("Total Students: " + studentData.size());
    }
    // In AdminDashboardController.java
    public void refreshStudentTable() {
        studentList.initializeStudents(); // <- your custom method
        ObservableList<Student> students = FXCollections.observableArrayList(studentList.getStudents());
        ADstudentTable.setItems(students);
    }

}
