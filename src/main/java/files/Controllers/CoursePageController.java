package files.Controllers;

import files.Classes.Course;
import files.Classes.Student;
import javafx.scene.control.Label;

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
}
