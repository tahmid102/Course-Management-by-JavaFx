package files.Classes;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private String courseName;
    private String courseID;
    private double credit;
    private final List<Student> courseStudents;
    private final List<Teacher>courseTeachers;
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
    /*
    Adding student and teachers to courses are bidirectional, meaning we're simultaneously
    updating lists of person in course object and course in person object
    in this Course object we will only call person's method
    Debugger's only in this class
     */
    public void addStudent(Student s){
        if(!courseStudents.contains(s)){
            courseStudents.add(s);
            s.addCourses(this);
            System.out.println(s.getName()+" was added to "+this.courseName);
        }
        else{
            System.out.println(s.getName()+" was NOT added to "+this.courseName);
        }
    }

    public void addTeacher(Teacher t){
        if(!courseTeachers.contains(t)){
            courseTeachers.add(t);
            t.assignCourse(this);
            System.out.println(t.getName()+" was assigned to teach "+courseName);
        }
        else {
            System.out.println(t.getName()+" was FAILED to assign to "+courseName);
        }
    }
    public void dropStudent(Student s){
        if(courseStudents.contains(s)){
            courseStudents.remove(s);
            s.removeCourse(this);
            System.out.println(s.getName()+" was removed from "+this.courseName);
        }
        else{
            System.out.println(s.getName()+" was NOT found for "+this.courseName);
        }
    }
}
