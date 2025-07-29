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

    @FXML private Label ADpendingStudentCountLabel;
    @FXML private Label ADpendingTeacherCountLabel;
    //TODO: SEARCH COMPONENTS
    @FXML private TextField ADcourseSearchField;
    @FXML private TextField ADstudentSearchField;
    @FXML private TextField ADteacherSearchField;
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
    @FXML private TableColumn<Course,String> ADcourseIDColumn;
    @FXML private TableColumn<Course,String> ADcourseNameColumn;
    @FXML private TableColumn<Course,Double> ADcourseCreditColumn;

    //TODO:Action Buttons
    @FXML private Button ADaddStudentButton;
    @FXML private Button ADaddTeacherButton;
    @FXML private Button ADaddCourseButton;
    @FXML private Button ADsignOutButton;
    @FXML private Button ADrefreshButton;
    @FXML private Button ADassignTeacherButton;
    @FXML private Button ADremoveStudentButton;

    //TODO:Status Labels
    @FXML private Label ADstudentCountLabel;
    @FXML private Label ADteacherCountLabel;
    @FXML private Label ADcourseCountLabel;



    //TODO:Data
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
        ADremoveStudentButton.setOnAction(event -> {
            Student selectedStudent = ADstudentTable.getSelectionModel().getSelectedItem();
            if (selectedStudent == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a student first!", ButtonType.OK);
                alert.showAndWait();
                return;
            }
            removeStudent(selectedStudent);
        });
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

    private void removeStudent(Student selectedStudent) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Remove Student");
        confirmationAlert.setHeaderText("Are you sure you want to remove this student?");
        confirmationAlert.setContentText("Student: " + selectedStudent.getName() + " (ID: " + selectedStudent.getID() + ")\n\nThis action cannot be undone.");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean removedFromList = Loader.studentList.removeStudent(selectedStudent);

                    if (removedFromList) {
                        boolean removedFromFile = removeStudentFromFile(selectedStudent.getID());

                        if (removedFromFile) {
                            removeStudentFromEnrollments(selectedStudent.getID());

                            refreshStudentTable();

                            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                            successAlert.setTitle("Student Removed");
                            successAlert.setHeaderText("Success!");
                            successAlert.setContentText("Student " + selectedStudent.getName() + " has been successfully removed.");
                            successAlert.showAndWait();

                            System.out.println("Student removed successfully: " + selectedStudent.getName());
                        } else {
                            Loader.studentList.addStudent(selectedStudent);

                            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                            errorAlert.setTitle("Error");
                            errorAlert.setHeaderText("Failed to remove student from file");
                            errorAlert.setContentText("The student could not be removed from the database file. Please try again.");
                            errorAlert.showAndWait();
                        }
                    } else {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setHeaderText("Student not found");
                        errorAlert.setContentText("The selected student could not be found in the system.");
                        errorAlert.showAndWait();
                    }
                } catch (Exception e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("An error occurred");
                    errorAlert.setContentText("Failed to remove student: " + e.getMessage());
                    errorAlert.showAndWait();
                    System.err.println("Error removing student: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
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
        System.out.println(Loader.courseList);
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






    private boolean removeStudentFromFile(int studentID) {
        try {
            java.io.File inputFile = new java.io.File("database/StudentCredentials.txt");
            java.io.File tempFile = new java.io.File("database/StudentCredentials_temp.txt");

            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(inputFile));
            java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(tempFile));

            String line;
            boolean studentFound = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    int currentID = Integer.parseInt(parts[0].trim());
                    if (currentID == studentID) {
                        studentFound = true;
                        System.out.println("Removing student from file: " + line);
                        continue;
                    }
                }
                writer.write(line);
                writer.newLine();
            }

            reader.close();
            writer.close();

            if (studentFound) {
                if (inputFile.delete() && tempFile.renameTo(inputFile)) {
                    System.out.println("Student successfully removed from file");
                    return true;
                } else {
                    System.err.println("Failed to replace original file");
                    tempFile.delete();
                    return false;
                }
            } else {
                tempFile.delete();
                System.out.println("Student not found in file");
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error removing student from file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void removeStudentFromEnrollments(int studentID) {
        try {
            java.io.File inputFile = new java.io.File("database/enrollments.txt");
            java.io.File tempFile = new java.io.File("database/enrollments_temp.txt");

            if (!inputFile.exists()) {
                System.out.println("Enrollments file does not exist, skipping enrollment cleanup");
                return;
            }

            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(inputFile));
            java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(tempFile));

            String line;
            int removedEnrollments = 0;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    int enrolledStudentID = Integer.parseInt(parts[0].trim());
                    if (enrolledStudentID == studentID) {
                        removedEnrollments++;
                        System.out.println("Removing enrollment: " + line);
                        continue;
                    }
                }
                writer.write(line);
                writer.newLine();
            }

            reader.close();
            writer.close();

            if (inputFile.delete() && tempFile.renameTo(inputFile)) {
                System.out.println("Removed " + removedEnrollments + " enrollments for student ID: " + studentID);
            } else {
                System.err.println("Failed to update enrollments file");
                tempFile.delete();
            }

        } catch (Exception e) {
            System.err.println("Error removing student enrollments: " + e.getMessage());
            e.printStackTrace();
        }
    }
}