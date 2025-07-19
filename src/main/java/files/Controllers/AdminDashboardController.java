package files.Controllers;

import files.Classes.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    @FXML private TableColumn<Teacher, String> ADteacherIdColumn;

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
    private final TeacherList teacherList = new TeacherList();
    private final CourseList courseList = new CourseList();


    //TODO:Loader files
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        studentList.initializeStudents();
        teacherList.initializeTeachers();
        courseList.loadCourses();
        setupStudentTable();
        setupTeacherTable();
        ADstudentTable.getColumns().forEach(col -> col.setReorderable(false));
        ADteacherTable.getColumns().forEach(col -> col.setReorderable(false));
        //TODO: STUDENT BUTTON EVENT
        ADaddStudentButton.setOnAction(event -> openStudentApprovalWindow());
        ADstudentTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Student selectedStudent = ADstudentTable.getSelectionModel().getSelectedItem();
                if (selectedStudent != null) {
                    openStudentCoursesWindow(selectedStudent.getID());
                }
            }
        });
        //TODO: TEACHER BUTTON EVENT
        ADaddTeacherButton.setOnAction(event -> openTeacherApprovalWindow());
        ADteacherTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Teacher selectedTeacher = ADteacherTable.getSelectionModel().getSelectedItem();
                if (selectedTeacher != null) {
                    openTeacherCoursesWindow(selectedTeacher.getID());
                }
            }
        });
        ADsignOutButton.setOnAction(event -> signOut());
        ADrefreshButton.setOnAction(event -> refreshTeacherTable());
    }
    //TODO:STUDENT FUNCTIONALITIES
    private void openStudentCoursesWindow(int studentID) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewStudentCourses.fxml"));
            Scene scene = new Scene(loader.load());
            Student student=studentList.searchStudent(studentID);
            if (student == null) {
                System.out.println("Student not found in StudentList: " + studentID);
                return;
            }
            student.loadCoursesForStudent(courseList);
            ViewStudentCoursesController controller = loader.getController();
            controller.setStudent(student);

            Stage stage = new Stage();
            stage.setTitle("Courses for " + student.getName());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error opening student course window: " + e.getMessage());
        }
    }

    private void openStudentApprovalWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddStudentApproval.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Approve Students");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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
    public void refreshStudentTable() {
        studentList.initializeStudents();
        ObservableList<Student> students = FXCollections.observableArrayList(studentList.getStudents());
        ADstudentTable.setItems(students);
    }
    //TODO: TEACHER FUNCTIONALITIES
    private void openTeacherCoursesWindow(int teacherID) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewTeacherCourses.fxml"));
            Scene scene = new Scene(loader.load());

            Teacher teacher = teacherList.searchTeacher(teacherID);
            if (teacher == null) {
                System.out.println("Teacher not found in TeacherList: " + teacherID);
                return;
            }
            // Load courses for teacher (assuming similar method exists)
            teacher.loadCoursesForTeacher(courseList);
            ViewTeacherCoursesController controller = loader.getController();
            controller.setTeacher(teacher);

            Stage stage = new Stage();
            stage.setTitle("Courses for " + teacher.getName());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error opening teacher course window: " + e.getMessage());
        }
    }
    private void openTeacherApprovalWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddTeacherApproval.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Approve Teachers");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error opening teacher approval window: " + e.getMessage());
        }
    }
    private void setupTeacherTable() {
        ADteacherNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ADteacherIdColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));

        teacherList.initializeTeachers();
        ObservableList<Teacher> teacherData = FXCollections.observableArrayList(teacherList.getTeachers());
        ADteacherTable.setItems(teacherData);
        ADteacherCountLabel.setText("Total Teachers: " + teacherData.size());
    }
    public void refreshTeacherTable() {
        teacherList.initializeTeachers();
        ObservableList<Teacher> teachers = FXCollections.observableArrayList(teacherList.getTeachers());
        ADteacherTable.setItems(teachers);
        ADteacherCountLabel.setText("Total Teachers: " + teachers.size());
    }
    public void refreshAllTables() {
        refreshStudentTable();
        refreshTeacherTable();
    }
    //TODO:SIGN OUT
    public void signOut(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Scene loginScene = new Scene(loader.load());
            Stage currentStage = (Stage) ADsignOutButton.getScene().getWindow();
            currentStage.setScene(loginScene);
            currentStage.setTitle("Login");
        } catch (IOException e) {
            System.out.println("Error during sign out: " + e.getMessage());
        }
    }

}
