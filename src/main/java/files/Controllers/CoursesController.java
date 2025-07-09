package files.Controllers;

import files.Classes.Course;
import javafx.scene.control.Label;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;


public class CoursesController {
    public VBox allCourseVbox;
    List<Course> Courses=new ArrayList<>();

    public void loadCourses(){
        try (BufferedReader reader = new BufferedReader(new FileReader("/database/Courses.txt"))) {
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
            Label label =new Label(a.getCourseName());
            label.setStyle("-fx-font-size: 14; -fx-padding: 5;");
            allCourseVbox.getChildren().add(label);
        }
    }
    public void Initialize(){
        loadCourses();
        display();
    }
}
