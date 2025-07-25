package files.Controllers;

import files.Classes.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ViewTeacherCoursesController {

    @FXML public TableView<Student> studentTable;
    @FXML public TableColumn<Student,Integer> colStudentID;
    @FXML public TableColumn<Student,String> colStudentName;
    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course,String> colCourseID;
    @FXML private TableColumn<Course,String> colCourseName;
    @FXML private TableColumn<Course,Double> colCredit;
    @FXML private Label teacherNameLabel;

    private final CourseList courseList=new CourseList();
    @FXML public void initialize() {
        courseTable.getColumns().forEach(col -> col.setReorderable(false));
        teacherNameLabel.setText("");
        courseTable.setItems(FXCollections.observableArrayList());

        courseTable.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getClickCount()==2) {
                Course selectedCourse=courseTable.getSelectionModel().getSelectedItem();
                loadStudentUnderCourses(selectedCourse);
            }
        });
    }
    public void setTeacher(Teacher teacher) {
        courseTable.getItems().clear();
        teacherNameLabel.setText("Courses Taught by: " + teacher.getName());

        colCourseID.setCellValueFactory(new PropertyValueFactory<>("courseID"));
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colCredit.setCellValueFactory(new PropertyValueFactory<>("credit"));

        ObservableList<Course> courseList = FXCollections.observableArrayList(teacher.getCoursesAssigned());
        courseTable.setItems(courseList);
    }
    private void loadStudentUnderCourses(Course course) {
        courseList.loadCourses();
        //TODO STUDENT BAKI BHAI


        studentTable.getItems().clear();
        colStudentID.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colStudentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        ObservableList<Student> studentsList = FXCollections.observableArrayList(course.getCourseStudents());
        studentTable.setItems(studentsList);
    }
}

