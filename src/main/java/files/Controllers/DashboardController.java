package files.Controllers;

import files.Classes.Student;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;

public class DashboardController {
    public Button ProfileButton;
    @FXML
    public javafx.scene.control.Label welcomeText;
    private Stage stage;
    private Scene scene;
    private Parent root;
    public HBox hboxAll;
    public Text topText;
    public VBox vBox;
    public Button homeButton;
    public Button myCoursesButton;
    public Button addCourseButton;



    private Student currentStudent;

    public void setCurrentStudent(Student student){
        this.currentStudent=student;
        if (welcomeText != null) {
            welcomeText.setText("Welcome, " + student.getName());
            System.out.println("Welcome text set for " + student.getName());
        } else {
            System.out.println("welcomeText is null!");
        }
    }

    public void onHomeButton(ActionEvent actionEvent) {

    }

    public void onMyCourses(ActionEvent actionEvent) {
        changeToCourses(actionEvent);
    }

    public void onAdd(ActionEvent actionEvent) {

    }
    public void onProfile(ActionEvent actionEvent){
        changeToProfile(actionEvent);
    }

    public void changeToProfile(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Profile.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Student Profile");
            stage.show();
        } catch (Exception ex) {
            Label errorLabel = new Label();
            errorLabel.setText("Try again.");

            ex.printStackTrace();
        }
    }

    public void changeToCourses(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Profile.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Student Courses");
            stage.show();
        } catch (Exception ex) {
            Label errorLabel = new Label();
            errorLabel.setText("Try again.");

            ex.printStackTrace();
        }
    }

}
