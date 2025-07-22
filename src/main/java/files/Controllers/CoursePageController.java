package files.Controllers;

import files.Classes.Course;
import files.Classes.Student;
import files.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CoursePageController {
    public Label courseName;
    public Label creditLOabel;
    public Button logout;
    public Button courses;
    public Button home;
    Course course;
    Student student;

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void display(){
        courseName.setText(course.getCourseName());
        creditLOabel.setText("Total Credits: "+ course.getCredit());
    }



    public void Initialize(){
        display();
    }
    public VBox announcementBox; // From FXML

    public void loadAnnouncements() {
        announcementBox.getChildren().clear();
        try (BufferedReader br = new BufferedReader(new FileReader("database/CourseAnnouncements.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 3 && parts[0].equals(course.getCourseID())) {
                    Label announcement = new Label(parts[2] + ": " + parts[1]);  // date: message
                    announcement.setStyle("-fx-font-size: 12; -fx-padding: 5;");
                    announcementBox.getChildren().add(announcement);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onHome(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            Parent root = loader.load();

            DashboardController controller = loader.getController();
            controller.setCurrentStudent(student);

            Stage stage = (Stage) home.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Oncourses(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Student/StudentCourses.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) courses.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Student Courses");
            StudentCoursesController controller=loader.getController();
            controller.passStudent(student);
            stage.setResizable(true);

            stage.show();
        } catch (Exception ex) {
            java.awt.Label errorLabel = new java.awt.Label();
            errorLabel.setText("Try again.");

            ex.printStackTrace();
        }
    }

    public void onLogout(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/login.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = (Stage) logout.getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("Course Management System");
        stage.setScene(scene);
        stage.show();
    }
}
