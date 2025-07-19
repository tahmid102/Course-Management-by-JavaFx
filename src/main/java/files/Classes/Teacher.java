package files.Classes;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Teacher extends Person{
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
        return Objects.hashCode(getID());
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
    public void loadCoursesForTeacher(CourseList courseList) {
        if(coursesLoaded) return;
        try {
            InputStream is= getClass().getResourceAsStream("/database/AssignedCoursesTeacher.txt");
            assert is!=null;
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                int fileTeacherID = Integer.parseInt(tokens[0].trim());
                String courseID = tokens[1].trim();

                if (fileTeacherID == this.getID()) {
                    Course course = courseList.searchCourse(courseID);
                    if (course != null) {
                        courseAssigned.add(course);
                    }
                }
            }
            coursesLoaded=true;
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
    }
}
