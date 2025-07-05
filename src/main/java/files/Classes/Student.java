package files.Classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Student extends Person{
    //personal Student class fields
    private final List<Course>courses;

    //Student class constructor
    public Student(String studentName,int studentId,String stdPass){
        super(studentName,studentId,stdPass);
        courses=new ArrayList<>();
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
        if(!courses.contains(c))  courses.add(c);
    }
    public void removeCourse(Course c){
        courses.remove(c);
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder(super.toString());
        sb.append(", AssignedCourses=[");
        for(Course c: courses){
            sb.append(c.getCourseName()).append(",");
        }
        if(!courses.isEmpty()) sb.setLength(sb.length()-1);
        sb.append("]");
        return sb.toString();
    }
}
