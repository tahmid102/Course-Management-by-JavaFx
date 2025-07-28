package files.Controllers;

import files.Classes.Student;
import files.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class DashboardController {
    public Button ProfileButton;
    @FXML
    public javafx.scene.control.Label welcomeText;
    public Button logout;
    public Button backButton;
    private Stage stage;
    private Scene scene;
    private Parent root;
    public HBox hboxAll;
    public Text topText;
    public VBox vBox;
    public Button homeButton;
    public Button myCoursesButton;
    public Button addCourseButton;
    private ImageView gifImageView;



    protected Student currentStudent;

    public void setCurrentStudent(Student student){
        this.currentStudent=student;
        if (welcomeText != null) {
            welcomeText.setText("Welcome, " + student.getName());
            System.out.println("Welcome text set for " + student.getName());
        } else {
            System.out.println("welcomeText is null!");
        }
      //  Image gif = new Image(getClass().getResourceAsStream("/student.gif"));
       // gifImageView.setImage(gif);
    }

    public void onHomeButton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            Parent root = loader.load();

            DashboardController controller = loader.getController();
            controller.setCurrentStudent(currentStudent);  // Pass student info back

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onMyCourses(ActionEvent actionEvent) {
        changeToCourses(actionEvent);
    }

    public void onAdd(ActionEvent actionEvent) {
        changeTOAdd(actionEvent);

    }
    public void onProfile(ActionEvent actionEvent){
        changeToProfile(actionEvent);
    }

    public void changeToProfile(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Student/Profile.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Student Profile");
            ProfileController controller=loader.getController();
            controller.passStuddent(currentStudent);
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/buet_logo.png"))));
            stage.setResizable(true);
            stage.show();
        } catch (Exception ex) {
            Label errorLabel = new Label();
            errorLabel.setText("Try again.");

            ex.printStackTrace();
        }
    }

    public void changeToCourses(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Student/StudentCourses.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Student Courses");
            StudentCoursesController controller=loader.getController();
            controller.passStudent(currentStudent);
            stage.setResizable(true);

            stage.show();
        } catch (Exception ex) {
            Label errorLabel = new Label();
            errorLabel.setText("Try again.");

            ex.printStackTrace();
        }
    }

    public void changeTOAdd(ActionEvent e){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Courses.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("All Courses");
            CoursesController controller=loader.getController();
            controller.passStudent(currentStudent);
            controller.initialize();
            try {
                stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/buet_logo.png"))));
            } catch (Exception ex) {
                System.out.println("Image Didn't Load");
            }
            stage.setResizable(true);

            stage.show();
        } catch (Exception ex) {
            Label errorLabel = new Label();
            errorLabel.setText("Try again.");

            ex.printStackTrace();
        }
    }

    public void onlogout(ActionEvent actionEvent) {
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
