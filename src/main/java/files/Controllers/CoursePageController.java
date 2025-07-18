package files.Controllers;

import files.Classes.Course;
import files.Classes.Student;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CoursePageController {
    public Label courseName;
    public Label creditLOabel;
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
        try (BufferedReader br = new BufferedReader(new FileReader("CourseAnnouncements.txt"))) {
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
}
