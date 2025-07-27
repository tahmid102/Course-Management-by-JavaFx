package files.Controllers;

import files.Classes.Course;
import files.Classes.Teacher;
import files.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TeacherDasahboardController {
    public Button logoutButton;
    public Button homeButton;
    public Button assignedCoursesButton;
    public Button profileButton;
    public VBox mainContentBox;
    public Label welcomeLabel;
    public Label Name;
    Teacher teacher;
    List<Course> courses=new ArrayList<>();

    public void setTeacher(Teacher teacher){
        this.teacher=teacher;
        this.courses=teacher.getCoursesAssigned();
        Name.setText(teacher.getName());
        welcomeLabel.setText("Welcome "+teacher.getName());
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

    public void onAssignedCoursesClicked(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/AssignedCourses.fxml"));
        Scene scene= null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        AssignedCourses controller=fxmlLoader.getController();
        controller.setTeacher(this.teacher);
        controller.displayCoursesd();

        Stage stage = (Stage) assignedCoursesButton.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Course");
        stage.show();
    }

    public void onProfileClicked(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/TeacherProfile.fxml"));
        Scene scene= null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TeacherProfileController controller=fxmlLoader.getController();
        controller.setTeacher(this.teacher);
        //controller.displayCoursesd();

        Stage stage = (Stage) assignedCoursesButton.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Course");
        stage.show();

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

}
