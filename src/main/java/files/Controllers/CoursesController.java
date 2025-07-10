package files.Controllers;

import files.Classes.Course;
import files.Classes.Student;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;


public class CoursesController {
    public VBox allCourseVbox;
    public Button backButton;
    List<Course> Courses=new ArrayList<>();
    Student currentStudent;
    List<Course> EnrolledCourse=new ArrayList<>();

    public void passStudent(Student student){
        currentStudent=student;
        this.EnrolledCourse=student.getCourses();
    }

    public void loadCourses(){
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/database/Courses.txt")))) {
            String line;
            while((line= reader.readLine())!=null){
                String[] words=line.split(",");
                if(words.length==3){
                    Course course=new Course(words[0].trim(),words[1].trim(),Double.parseDouble(words[2].trim()));
                    Courses.add(course);
                }
            }
        } catch (Exception e) {
            System.out.println("Error");
        }
    }

    private HBox createCourseRow(Course a){
        HBox CourseRow = getHBox();
        CourseRow.setFillHeight(true);
        CourseRow.setPrefWidth(Region.USE_COMPUTED_SIZE);
        CourseRow.setStyle("-fx-padding: 10;" +
                "-fx-alignment: center-left;" +
                "-fx-background-color: #f9f9f9;" +
                "-fx-border-color: #cccccc;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5;");

        Label label =new Label(a.getCourseID());
        label.setStyle("-fx-font-size: 14; -fx-padding: 5;");label.setPrefWidth(300);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(label, Priority.ALWAYS);
        label.setMaxWidth(Region.USE_PREF_SIZE);

        Button addButton=new Button("Add");
        addButton.setPrefWidth(60);
        addButton.setMinWidth(Region.USE_PREF_SIZE);
        addButton.setMaxWidth(Region.USE_PREF_SIZE);
        addButton.setStyle(
                "-fx-background-color: #19FF19;" +
                        "-fx-border-color: #cccccc;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;"
        );
        addButton.setOnAction(e->currentStudent.addCourses(a));
        CourseRow.getChildren().addAll(label,addButton);
        return CourseRow;

    }

    public void display(List<Course> Courses){
        allCourseVbox.getChildren().clear();
        for(Course a: Courses){
            if(EnrolledCourse.contains(a)) continue;
            HBox courseRow=createCourseRow(a);
            allCourseVbox.getChildren().add(courseRow);
        }
    }

    private static @NotNull HBox getHBox() {
        HBox CourseRow=new HBox();
        CourseRow.setFillHeight(true);
        CourseRow.setPrefWidth(Region.USE_COMPUTED_SIZE);
        CourseRow.setSpacing(20);
        CourseRow.setPrefWidth(500);
        CourseRow.setStyle(
                "-fx-padding: 10;" +
                        "-fx-alignment: center-left;" +"-fx-alignment: center-left;" +
                        "-fx-background-color: #f9f9f9;" +
                        "-fx-border-color: #cccccc;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;"
        );
        return CourseRow;
    }
    @FXML
    public void initialize(){
        loadCourses();
        display(Courses);
    }

    public void onBack(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
        Scene scene= null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DashboardController controller=fxmlLoader.getController();
        controller.setCurrentStudent(currentStudent);
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Dashboard");
        stage.show();
    }
}
