package files.Classes;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Teacher extends Person implements Serializable{
    private final List<Course> courseAssigned;
    private boolean coursesLoaded=false;
    public Teacher(String name, int ID, String password) {
        super(name, ID, password);
        courseAssigned=new ArrayList<>();
    }

    @Override
    public int getID() {
        return super.getID();
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }
    @Override
    public void setID(int ID) {
        super.setID(ID);
    }

    @Override
    public void setPassword(String password) {
        super.setPassword(password);
    }

    public void assignCourse(Course c){
        if(!courseAssigned.contains(c)){
            courseAssigned.add(c);
        }
    }
    public List<Course> getCoursesAssigned() {
        return courseAssigned;
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj)return true;
        if(!(obj instanceof Teacher))return false;
        return ((Teacher) obj).getID()==this.getID();

    }@Override
    public int hashCode(){
        return Objects.hashCode(getID());
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Teacher{Name='%s', ID=%d}", getName(), getID()));

        sb.append(", AssignedCourses=[");
        for (Course c : courseAssigned) {
            sb.append(String.format("(%s, %s), ", c.getCourseID(), c.getCourseName()));
        }
        if (!courseAssigned.isEmpty()) sb.setLength(sb.length() - 2);
        sb.append("]");

        return sb.toString();
    }
    
}
