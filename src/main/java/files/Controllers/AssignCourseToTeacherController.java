package files.Controllers;

import files.Classes.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AssignCourseToTeacherController implements Initializable {

    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> courseIdCol;
    @FXML private TableColumn<Course, String> courseNameCol;
    @FXML private TableColumn<Course, Double> courseCreditCol;

    @FXML private Button approveButton;
    @FXML private Button cancelButton;

    private Teacher teacher;
    private ObservableList<Course> allCourses;

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
        loadCourses();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        courseIdCol.setCellValueFactory(new PropertyValueFactory<>("courseID"));
        courseNameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        courseCreditCol.setCellValueFactory(new PropertyValueFactory<>("credit"));

        approveButton.setOnAction(event -> approveSelectedCourse());
        cancelButton.setOnAction(event -> ((Stage) cancelButton.getScene().getWindow()).close());
    }

    private void loadCourses() {
        List<Course>applicableCourseList=new ArrayList<>();
        for(Course course:Loader.courseList.getCourses()){
            if(!teacher.getCoursesAssigned().contains(course)){
                applicableCourseList.add(course);
            }
        }
        allCourses = FXCollections.observableArrayList(applicableCourseList);
        courseTable.setItems(allCourses);
    }

    private void approveSelectedCourse() {
        courseTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        Course selectedCourse =courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a course to assign.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        if (!teacher.getCoursesAssigned().contains(selectedCourse)) {
            Loader.courseList.addTeacherToCourse(selectedCourse,teacher);
            appendTeacherCourseAssignment(String.valueOf(teacher.getID()), selectedCourse.getCourseID());

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Course assigned successfully!", ButtonType.OK);
            alert.showAndWait();

            ((Stage) approveButton.getScene().getWindow()).close();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Teacher already assigned to this course.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void appendTeacherCourseAssignment(String teacherID, String courseID) {
        String line = teacherID + "," + courseID;
        try{
            Writer.writeToFile(line,"database/AssignedCoursesTeacher.txt");
        }catch (Exception e) {
            System.out.println("Problem appending teacher-course assignment: " + e.getMessage());
        }
    }
}

