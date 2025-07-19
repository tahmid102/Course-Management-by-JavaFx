package files.Classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Student extends Person{
    //personal Student class fields
    private final List<Course>courses;
    private boolean coursesLoaded = false;

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

    public List<Course> getCourses() {
        return courses;
    }

    public void loadCoursesForStudent(CourseList courseList) {
        if(coursesLoaded) return;
        try {
            InputStream is= Student.class.getResourceAsStream("/database/enrollments.txt");
            assert is!=null;
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                int fileStudentID = Integer.parseInt(tokens[0].trim());
                String courseID = tokens[1].trim();

                if (fileStudentID == this.getID()) {
                    Course course = courseList.searchCourse(courseID);
                    if (course != null) {
                        courses.add(course);
                    }
                }
            }
            coursesLoaded=true;
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
    }
}
