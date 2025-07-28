package files.Classes;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// this class is completely useless, will be deleted later, but i did it here for mky sake in some other file
//DO NOT DELETE THE FILE THAMID - muf
public class CourseList implements Serializable{
    List<Course> Courses=new ArrayList<>();
    public Course searchCourse(String courseId) {
        for (Course course : Courses) {
            if (course.getCourseID().equals(courseId)) {
                return course;
            }
        }
        return null;
    }
    public List<Course> getCourses() {
        return Courses;
    }
    public boolean addCourse(Course c) {
        if (isCourseAvailable(c.getCourseID())) return false;
        Courses.add(c);
        return true;
    }
    public boolean isCourseAvailable(String cID) {
        for (Course c:Courses) {
            if (c.getCourseID().equals(cID)) return true;
        }
        return false;
    }
    public void addStudentToCourse(Course course, Student student) {
        for(Course c:Courses){
            if(c.equals(course)) {
                c.addStudent(student);
                break;
            }
        }
    }public void addTeacherToCourse(Course course, Teacher teacher) {
        for(Course c:Courses){
            if(c.equals(course)) {
                c.addTeacher(teacher);
                break;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CourseList:\n");
        for (Course c : Courses) {
            sb.append("  ").append(c).append("\n");
        }
        return sb.toString();
    }

}
