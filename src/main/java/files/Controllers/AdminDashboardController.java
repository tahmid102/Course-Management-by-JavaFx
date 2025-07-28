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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.transformation.FilteredList;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {


    @FXML public Label ADpendingStudentCountLabel;
    @FXML public Label ADpendingTeacherCountLabel;
    @FXML public Button ADassignTeacherButton;
    //TODO: SEARCH COMPONENTS
    @FXML public TextField ADcourseSearchField;
    @FXML public TextField ADstudentSearchField;
    @FXML public TextField ADteacherSearchField;
    //TODO:Student Table Components
    @FXML private TableView<Student> ADstudentTable;
    @FXML private TableColumn<Student, String> ADstudentNameColumn;
    @FXML private TableColumn<Student, String> ADstudentIdColumn;

    //TODO:Teacher Table Components
    @FXML private TableView<Teacher> ADteacherTable;
    @FXML private TableColumn<Teacher, String> ADteacherNameColumn;
    @FXML private TableColumn<Teacher, String> ADteacherIdColumn;

    //TODO:Course Display Components
    @FXML private TableView<Course> ADcourseTable;
    @FXML public TableColumn<Course,String> ADcourseIDColumn;
    @FXML public TableColumn<Course,String> ADcourseNameColumn;
    @FXML public TableColumn<Course,Double> ADcourseCreditColumn;

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



    //TODO:Data
    // Remove final keyword and get fresh references each time
    private FilteredList<Student> filteredStudentList;
    private FilteredList<Teacher> filteredTeacherList;
    private FilteredList<Course> filteredCourseList;

    //TODO:Loader files
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupStudentTable();
        setupTeacherTable();
        setupCourseTable();

        ADstudentTable.getColumns().forEach(col -> { col.setReorderable(false); col.setResizable(false); });
        ADteacherTable.getColumns().forEach(col -> { col.setReorderable(false); col.setResizable(false); });
        ADcourseTable.getColumns().forEach(col -> { col.setReorderable(false); col.setResizable(false); });

        filteredStudentList = new FilteredList<>(FXCollections.observableArrayList(Loader.studentList.getStudents()), p -> true);
        filteredTeacherList = new FilteredList<>(FXCollections.observableArrayList(Loader.teacherList.getTeachers()), p -> true);
        filteredCourseList = new FilteredList<>(FXCollections.observableArrayList(Loader.courseList.getCourses()), p -> true);

        ADstudentTable.setItems(filteredStudentList);
        ADteacherTable.setItems(filteredTeacherList);
        ADcourseTable.setItems(filteredCourseList);

        ADstudentSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = (newVal == null) ? "" : newVal.toLowerCase();
            filteredStudentList.setPredicate(student -> {
                if (filter.isEmpty()) return true;
                return student.getName().toLowerCase().contains(filter) ||
                        String.valueOf(student.getID()).contains(filter);
            });
        });

        ADteacherSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = (newVal == null) ? "" : newVal.toLowerCase();
            filteredTeacherList.setPredicate(teacher -> {
                if (filter.isEmpty()) return true;
                return teacher.getName().toLowerCase().contains(filter) ||
                        String.valueOf(teacher.getID()).contains(filter);
            });
        });

        ADcourseSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = (newVal == null) ? "" : newVal.toLowerCase();
            filteredCourseList.setPredicate(course -> {
                if (filter.isEmpty()) return true;
                return course.getCourseID().toLowerCase().contains(filter) ||
                        course.getCourseName().toLowerCase().contains(filter);
            });
        });


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
        //TODO:COURSE BUTTONS
        ADaddCourseButton.setOnAction(event -> openCourseWindow());
        ADcourseTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Course selectedCourse = ADcourseTable.getSelectionModel().getSelectedItem();
                if (selectedCourse != null) {
                    openPendingCourseApprovalWindow(selectedCourse);
                }
            }
        });

        //TODO:REMAINING BUTTONS
        ADsignOutButton.setOnAction(event -> signOut());
        ADrefreshButton.setOnAction(event -> refreshAllTables());
        ADassignTeacherButton.setOnAction(event -> {
            Teacher selectedTeacher = ADteacherTable.getSelectionModel().getSelectedItem();
            if (selectedTeacher == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a teacher first!", ButtonType.OK);
                alert.showAndWait();
                return;
            }
            openAssignCourseWindow(selectedTeacher);
        });
    }

    //TODO:STUDENT FUNCTIONALITIES
    private void openStudentCoursesWindow(int studentID) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/ViewStudentCourses.fxml"));
            Scene scene = new Scene(loader.load());
            Student student = Loader.studentList.searchStudent(studentID);
            if (student == null) {
                System.out.println("Student not found in StudentList: " + studentID);
                return;
            }
            ViewStudentCoursesController controller = loader.getController();
            controller.setStudent(student);

            Stage stage = new Stage();
            stage.setTitle("Courses for " + student.getName());
            stage.setScene(scene);
            stage.show();
            stage.setResizable(false);
        } catch (IOException e) {
            System.out.println("Error opening student course window: " + e.getMessage());
        }
    }

    private void openStudentApprovalWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/AddStudentApproval.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Approve Students");
            stage.setScene(scene);
            stage.show();
            stage.setResizable(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setupStudentTable() {
        ADstudentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ADstudentIdColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        ObservableList<Student> studentData = FXCollections.observableArrayList(Loader.studentList.getStudents());
        ADstudentCountLabel.setText("Total Students: " + studentData.size());
    }
    public void refreshStudentTable() {
        ObservableList<Student> sourceList = (ObservableList<Student>) filteredStudentList.getSource();
        sourceList.clear();
        sourceList.addAll(Loader.studentList.getStudents());
        ADstudentCountLabel.setText("Total Students: " + Loader.studentList.getStudents().size());
    }
    //TODO: TEACHER FUNCTIONALITIES
    private void openTeacherCoursesWindow(int teacherID) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/ViewTeacherCourses.fxml"));
            Scene scene = new Scene(loader.load());

            Teacher teacher = Loader.teacherList.searchTeacher(teacherID);
            if (teacher == null) {
                System.out.println("Teacher not found in TeacherList: " + teacherID);
                return;
            }
            ViewTeacherCoursesController controller = loader.getController();
            controller.setTeacher(teacher);
            Stage stage = new Stage();
            stage.setTitle("Courses for " + teacher.getName());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error opening teacher course window: " + e.getMessage());
        }
    }
    private void openTeacherApprovalWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/AddTeacherApproval.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Approve Teachers");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error opening teacher approval window: " + e.getMessage());
        }
    }
    private void setupTeacherTable() {
        ADteacherNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ADteacherIdColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        ObservableList<Teacher> teacherData = FXCollections.observableArrayList(Loader.teacherList.getTeachers());
        ADteacherCountLabel.setText("Total Teachers: " + teacherData.size());
    }
    public void refreshTeacherTable() {
        ObservableList<Teacher> sourceList = (ObservableList<Teacher>) filteredTeacherList.getSource();
        sourceList.clear();
        sourceList.addAll(Loader.teacherList.getTeachers());
        ADteacherCountLabel.setText("Total Teachers: " + Loader.teacherList.getTeachers().size());
    }
    private void openAssignCourseWindow(Teacher selectedTeacher) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/AssignCourseToTeacher.fxml"));
            Scene scene = new Scene(loader.load());

            AssignCourseToTeacherController controller = loader.getController();
            controller.setTeacher(selectedTeacher);

            Stage stage = new Stage();
            stage.setTitle("Assign Courses to " + selectedTeacher.getName());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //TODO:COURSE FUNCTIONALITIES
    private void setupCourseTable() {
        ADcourseIDColumn.setCellValueFactory(new PropertyValueFactory<>("courseID"));
        ADcourseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        ADcourseCreditColumn.setCellValueFactory(new PropertyValueFactory<>("credit"));
        ObservableList<Course> courseData = FXCollections.observableArrayList(Loader.courseList.getCourses());
        ADcourseCountLabel.setText("Total Courses: " + courseData.size());
    }
    private void openCourseWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/AddCourse.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Approve Courses");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error opening course approval window: " + e.getMessage());
        }
    }
    private void openPendingCourseApprovalWindow(Course selectedCourse) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/PendingCourseApprovals.fxml"));
            Scene scene = new Scene(loader.load());

            PendingCourseApprovalController controller = loader.getController();
            controller.setCourse(selectedCourse);

            Stage stage = new Stage();
            stage.setTitle("Pending Approvals for " + selectedCourse.getCourseName());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void refreshCourseTable() {
        ObservableList<Course> sourceList = (ObservableList<Course>) filteredCourseList.getSource();
        sourceList.clear();
        sourceList.addAll(Loader.courseList.getCourses());
        ADcourseCountLabel.setText("Total Courses: " + Loader.courseList.getCourses().size());
    }

    public void refreshAllTables() {
        Loader.reloadAll();
        System.out.println(Loader.toDampString());

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        refreshCourseTable();
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