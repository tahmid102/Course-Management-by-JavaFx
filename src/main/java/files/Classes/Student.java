package files.Classes;

import java.util.ArrayList;
import java.util.List;

public class Student {
    String studentName;
    int studentId;
    String studentPassword;
    List<Course> courses=new ArrayList<>();

    public Student(String studentName,int studentId){
        this.studentId=studentId;
        this.studentName=studentName;

    }

    public void setStudentPassword(String studentPassword) {
        this.studentPassword = studentPassword;
    }

    public String getStudentName() {
        return studentName;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getStudentPassword() {
        return studentPassword;
    }
    public void addCourses(Course a){
        courses.add(a);
    }
    public void removeCourse(Course b){
        courses.remove(b);
    }

}
