package files.Classes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FilterReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

// this class is completely useless, will be deleted later, but i did it here for mky sake in some other file
//DO NOT DELETE THE FILE THAMID - muf
public class CourseList {
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
            if(c.equals(course)) c.addTeacher(teacher);
        }
    }

    @Override
    public String toString() {
        return "CourseList{" +
                "Courses=\n" + Courses +
                '}';
    }
}
