package files.Controllers;

import files.Classes.Course;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;


public class CoursesController {
    public VBox allCourseVbox;
    List<Course> Courses=new ArrayList<>();

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

    public void display(){
        for(Course a: Courses){
            HBox CourseRow = getHBox();
            CourseRow.setFillHeight(true);
            CourseRow.setPrefWidth(Region.USE_COMPUTED_SIZE);




            Label label =new Label(a.getCourseID());
            label.setStyle("-fx-font-size: 14; -fx-padding: 5;");label.setPrefWidth(300);
            label.setWrapText(true);
            //HBox.setHgrow(label, Priority.ALWAYS);
            label.setMaxWidth(Double.MAX_VALUE);
            label.setMinWidth(200);
            label.setPrefWidth(250); // Increase if needed
            label.setWrapText(false); // Prevent unnecessary wrapping
            label.setMaxWidth(Region.USE_PREF_SIZE); // Ensure it doesn't shrink

            Button addButton=new Button("Add");
            addButton.setPrefWidth(60);
            addButton.setMinWidth(Region.USE_PREF_SIZE); // Prevents shrinking
            addButton.setMaxWidth(Region.USE_PREF_SIZE);
            addButton.setStyle(
                    "-fx-background-color: #19FF19;" +
                            "-fx-border-color: #cccccc;" +
                            "-fx-border-radius: 5;" +
                            "-fx-background-radius: 5;" +
                            "-fx-cursor: hand;"
            );

            CourseRow.getChildren().addAll(label,addButton);
            allCourseVbox.getChildren().add(CourseRow);
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

    public void Initialize(){
        loadCourses();
        display();
    }
}
