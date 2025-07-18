package files.Controllers;

import files.Classes.Course;
import files.Classes.Teacher;
import files.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

public class TeacherCoursePage {
    public Label Name;
    public Button homeButton;
    public Button logoutButton;
    public VBox mainContentBox;
    public Label welcomeLabel;
    public TextArea t;
    public Button post;
    Teacher teacher;
    Course course;
    public void setTeacher(Teacher teacher){
        this.teacher=teacher;
        Name.setText(teacher.getName());
    }

    public void setCourse(Course course) {
        this.course = course;
        welcomeLabel.setText(course.getCourseID()+" "+course.getCourseName());
    }

    public void display() {

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

    public void onPost(ActionEvent actionEvent) {
        String content = t.getText().trim();
        if (content.isEmpty()) return;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("CourseAnnouncements.txt", true))) {
            bw.write(course.getCourseID() + "|" + content + "|" + LocalDate.now());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        t.clear();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Announcement Posted!");
        alert.showAndWait();
    }
}
