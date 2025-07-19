package files.Controllers;

import files.Classes.Course;
import files.Classes.Teacher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ViewTeacherCoursesController {

    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course,String> colCourseID;
    @FXML private TableColumn<Course,String> colCourseName;
    @FXML private TableColumn<Course,Double> colCredit;
    @FXML private Label teacherNameLabel;

    @FXML public void initialize() {
        courseTable.getColumns().forEach(col -> col.setReorderable(false));
        teacherNameLabel.setText("");
        courseTable.setItems(FXCollections.observableArrayList());
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
}
