package files.Classes;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Student extends Person implements Serializable{
    //personal Student class fields
    private final List<Course>courses;
    private boolean coursesLoaded = false;
    private String imagePath=null;

    //Student class constructor
    public Student(String studentName,int studentId,String stdPass){
        super(studentName,studentId,stdPass);
        courses=new ArrayList<>();
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Student)) return false;
        Student p=(Student) o;
        return p.getID()==this.getID();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getID());
    }

    //Student methods
    public void addCourses(Course c){
        if(!courses.contains(c)) {
            courses.add(c);
        }
    }
    public void removeCourse(Course c){
        courses.remove(c);
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Student{Name='%s', ID=%d}", getName(), getID()));

        sb.append(", Courses=[");
        for (Course c : courses) {
            sb.append(String.format("(%s, %s), ", c.getCourseID(), c.getCourseName()));
        }
        if (!courses.isEmpty()) sb.setLength(sb.length() - 2);
        sb.append("]");

        return sb.toString();
    }


    public List<Course> getCourses() {
        return courses;
    }

}
