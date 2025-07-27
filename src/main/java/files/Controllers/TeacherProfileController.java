package files.Controllers;

import files.Classes.Teacher;
import files.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class TeacherProfileController {
    public Button home;
    public Button logout;
    public ImageView profileImage;
    public Label nameLabel;
    public Label idLabel;
    public Label courseCountLabel;
    Teacher teacher;
    public void setTeacher(Teacher teacher) {
        this.teacher=teacher;
        nameLabel.setText("Name: "+teacher.getName());
        idLabel.setText("ID "+teacher.getID());
        courseCountLabel.setText(teacher.getCoursesAssigned().size()+"");
    }

    public void onHome(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/TeacherDashboard.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TeacherDasahboardController controller=fxmlLoader.getController();
        controller.setTeacher(teacher);
        Stage stage = (Stage) home.getScene().getWindow();
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
        Stage stage = (Stage) logout.getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("Course Management System");
        stage.setScene(scene);
        stage.show();
    }


}
