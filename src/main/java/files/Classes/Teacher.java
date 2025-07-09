package files.Classes;


import java.util.ArrayList;
import java.util.List;

public class Teacher extends Person{
    List<Course> courseAssigned;
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
    public List<Course> getCourseAssigned() {
        return courseAssigned;
    }
    public void addStudentToCourse(Student a,Course b){
        a.addCourses(b);
        b.addStudent(a);
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj)return true;
        if(!(obj instanceof Teacher))return false;
        return ((Teacher) obj).getID()==this.getID();

    }@Override
    public int hashCode(){
        return this.getID();
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(", AssignedCourses=[");
        for (Course c : courseAssigned) {
            sb.append(c.getCourseID()).append(",");
        }
        if (!courseAssigned.isEmpty()) sb.setLength(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }
}
