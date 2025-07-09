package files.Controllers;


import files.Classes.Course;
import files.Classes.Student;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class StudentCoursesController extends DashboardController{
    public Button backButton;
    public Label course1;
    public VBox courseVbox;
    private Stage stage;
    private Scene scene;
    private Parent root;
    Student student;

    List<Course> courses;


    public void passStudent(Student student) {
        this.student = student;
        this.courses=student.getCourses();

        displayCoursesd();
    }

    public void displayCoursesd(){
        courseVbox.getChildren().clear();
        if(courses.isEmpty()){
            Label label =new Label("You Haven't Enrolled in any course");
            label.setStyle("-fx-font-size: 10; -fx-padding: 5;");
            courseVbox.getChildren().add(label);
        }
        else{
            for(Course course:courses){
                Label label =new Label(course.getCourseName());
                label.setStyle("-fx-font-size: 14; -fx-padding: 5;");
                courseVbox.getChildren().add(label);

            }
        }
    }







    public void toDash(ActionEvent actionEvent) {
        super.onHomeButton(actionEvent);
    }

    public void onCoursePage(){

    }
}
