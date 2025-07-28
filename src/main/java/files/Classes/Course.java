package files.Classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Course implements Serializable {
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
        }
    }

    public void addTeacher(Teacher t){
        if(!courseTeachers.contains(t)){
            courseTeachers.add(t);
            t.assignCourse(this);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Course{ID='%s', Name='%s', Credit=%.2f}", courseID, courseName, credit));

        sb.append("\n  Students: [");
        for (Student s : courseStudents) {
            sb.append(String.format("(%d, %s), ", s.getID(), s.getName()));
        }
        if (!courseStudents.isEmpty()) sb.setLength(sb.length() - 2);
        sb.append("]");

        sb.append("\n  Teachers: [");
        for (Teacher t : courseTeachers) {
            sb.append(String.format("(%d, %s), ", t.getID(), t.getName()));
        }
        if (!courseTeachers.isEmpty()) sb.setLength(sb.length() - 2);
        sb.append("]");

        return sb.toString();
    }


    public void dropStudent(Student s){
        if(courseStudents.contains(s)){
            courseStudents.remove(s);
            s.removeCourse(this);

        }
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Course other = (Course) obj;
        return this.courseID.equals(other.courseID);
    }

    @Override
    public int hashCode() {
        return courseID.hashCode();
    }

    public List<Student> getCourseStudents() {
        return courseStudents;
    }

    public List<Teacher> getCourseTeachers() {
        return courseTeachers;
    }
}
