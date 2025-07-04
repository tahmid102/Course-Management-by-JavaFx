package files.Classes;


import java.util.ArrayList;
import java.util.List;

class Teacher extends Person{
    List<Course> courseAssigned;
    public Teacher(String name, int ID, String password) {

        super(name, ID, password);
        courseAssigned=new ArrayList<>();
    }
    public void assignCourse(Course a){
        courseAssigned.add(a);
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

    public List<Course> getCourseAssigned() {
        return courseAssigned;
    }

    @Override
    public void setID(int ID) {
        super.setID(ID);
    }

    @Override
    public void setPassword(String password) {
        super.setPassword(password);
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

    }
    public int hashCode(){
        return this.getID();
    }
}
