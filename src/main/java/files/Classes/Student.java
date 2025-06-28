package files.Classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Student extends Person{
    //personal Student class fields
    List<Course>courses;

    //Student class constructor
    public Student(String studentName,int studentId){
        super(studentName,studentId);
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
    public void addCourses(Course a){
        courses.add(a);
    }
    public void removeCourse(Course b){
        courses.remove(b);
    }
}
