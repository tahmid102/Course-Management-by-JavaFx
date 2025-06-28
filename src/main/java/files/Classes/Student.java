package files.Classes;

import java.util.ArrayList;
import java.util.List;

public class Student extends Person{
    //personal Student class fields
    List<Course>courses;

    //Student class constructor
    public Student(String studentName,int studentId){
        super(studentName,studentId);
        courses=new ArrayList<>();
    }

    //Student methods
    public void addCourses(Course a){
        courses.add(a);
    }
    public void removeCourse(Course b){
        courses.remove(b);
    }
}
