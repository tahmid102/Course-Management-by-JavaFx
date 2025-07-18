package files.Controllers;

import files.Classes.Course;
import files.Classes.CourseList;
import files.Classes.Teacher;
import files.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AssignedCourses {

    public Button homeButton;
    public Button logoutButton;
    public VBox mainContentBox;
    public Label welcomeLabel;
    ;
    public VBox CourseVBox;
    public Label Name;
    Teacher teacher;
    List<Course> courses=new ArrayList<>();

    public void setTeacher(Teacher teacher){
        this.teacher=teacher;
        this.courses=teacher.getCourseAssigned();
        Name.setText(teacher.getName());
        System.out.println("Assigned teacher: " + teacher.getName());
        System.out.println("Courses: " + courses);
    }
    public void onLogout(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/login.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("Course Management System");
        stage.setScene(scene);
        stage.show();
    }

    public void onProfile(ActionEvent actionEvent) {
    }

    public void onDashboard(ActionEvent actionEvent) {

    }

    public void onBack(ActionEvent actionEvent) {
    }

    public void displayCoursesd(){
        CourseVBox.getChildren().clear();
        for (Course c : courses) {
            System.out.println("Course: " + c.getCourseID() + " - " + c.getCourseName());
        }

        if(courses.isEmpty()){
            Label label =new Label("You Haven't been Assigned for any course");
            label.setStyle("-fx-font-size: 10; -fx-padding: 5;");
            CourseVBox.getChildren().add(label);
        }
        else{
            for(Course course:courses){
                //Label label =new Label(course.getCourseName());
                Hyperlink hyperlinlk=new Hyperlink(course.getCourseID()+course.getCourseName());
                hyperlinlk.setStyle("-fx-font-size: 11; -fx-padding: 5;");
                CourseVBox.getChildren().add(hyperlinlk);
                hyperlinlk.setOnAction(e->{

                });

            }
        }
    }

    public void onHomeClicked(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/TeacherDashboard.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TeacherDasahboardController controller=fxmlLoader.getController();
        controller.setTeacher(teacher);
        Stage stage = (Stage) homeButton.getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("Dashboard");
        stage.setScene(scene);
        stage.show();
    }
}
