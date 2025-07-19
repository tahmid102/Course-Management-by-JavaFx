package files.Controllers;

import files.Classes.Course;
import files.Classes.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ViewStudentCoursesController {

    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course,String> colCourseID;
    @FXML private TableColumn<Course,String> colCourseName;
    @FXML private TableColumn<Course,Double> colCredit;
    @FXML private Label studentNameLabel;

    @FXML public void initialize() {
        courseTable.getColumns().forEach(col -> col.setReorderable(false));
        studentNameLabel.setText("");
        courseTable.setItems(FXCollections.observableArrayList());
    }

    public void setStudent(Student student) {
        courseTable.getItems().clear();
        studentNameLabel.setText("Courses Taken by: " + student.getName());

        colCourseID.setCellValueFactory(new PropertyValueFactory<>("courseID"));
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colCredit.setCellValueFactory(new PropertyValueFactory<>("credit"));

        ObservableList<Course> courseList = FXCollections.observableArrayList(student.getCourses());
        courseTable.setItems(courseList);
    }
}
