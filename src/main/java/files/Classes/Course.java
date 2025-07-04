package files.Classes;

import java.util.ArrayList;
import java.util.List;

class Course {
    String courseName;
    String courseID;
    double credit;
    List<Student> courseStudents;
    List<Teacher>courseTeachers;
    public Course(String courseID,String courseName,double credit){
        this.courseID=courseID;
        this.courseName=courseName;
        this.credit=credit;
        courseStudents=new ArrayList<>();
        courseTeachers=new ArrayList<>();
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getCourseName() {
        return courseName;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public void addStudent(Student a){
        courseStudents.add(a);
        a.addCourses(this);
    }

    public void addTeacher(Teacher a){
        courseTeachers.add(a);
        a.assignCourse(this);
    }
    public void dropStudent(Student a){
        courseStudents.remove(a);
        a.removeCourse(this);
    }
}
