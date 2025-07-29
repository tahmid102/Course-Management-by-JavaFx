package files.Controllers;

import files.Classes.Student;
import files.Main;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class ProfileController {
    public ImageView profileImage;
    public Label nameLabel;
    public Label courseCountLabel;
    public Label idLabel;
    public AnchorPane editProfile;
    Student currentStudent;

    public Button logout;
    public Button home;
    public Label clockLabel;

    public void passStuddent(Student a){
        this.currentStudent=a;
        loadProfile();
    }

    public void loadProfile(){

        nameLabel.setText(currentStudent.getName());
        idLabel.setText( currentStudent.getID()+" ");
        courseCountLabel.setText(currentStudent.getCourses().size()+"");
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalTime currentTime = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            clockLabel.setText(currentTime.format(formatter));
        }));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        try {
            String path = currentStudent.getImagePath();

            if (path != null && !path.isEmpty()) {
                Image img = new Image(getClass().getResource(path).toExternalForm());
                profileImage.setImage(img);
            } else {
                Image fallback = new Image(getClass().getResource("/default pp.jpg").toExternalForm());
                profileImage.setImage(fallback);
            }
        } catch (Exception e) {
            e.printStackTrace(); // So you can see the real error
            Image fallback = new Image(getClass().getResource("/Images/muffed.jpg").toExternalForm());
            profileImage.setImage(fallback);
        }
    }

    public void onHome(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
        Scene scene= null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DashboardController controller=fxmlLoader.getController();
        controller.setCurrentStudent(currentStudent);
        Stage stage = (Stage) logout.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Dashboard");
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

    public void onEditProfile(MouseEvent mouseEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
        Scene scene= null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DashboardController controller=fxmlLoader.getController();
        controller.setCurrentStudent(currentStudent);
        Stage stage = (Stage) logout.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Dashboard");
        stage.show();

    }
}
