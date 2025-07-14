package files.Classes;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

// this class is completely useless, will be deleted later, but i did it here for mky sake in some other file

public class CourseList {
    List<Course> Courses=new ArrayList<>();

    public void loadCourses(){
        try (BufferedReader reader = new BufferedReader(new FileReader("Courses.txt"))) {
            String line;
            while((line= reader.readLine())!=null){
                String[] words=line.split(",");
                if(words.length==3){
                    Course course=new Course(words[0].trim(),words[1].trim(),Double.parseDouble(words[2].trim()));
                    Courses.add(course);
                }
            }
        } catch (Exception e) {
            System.out.println("Error");
        }
    }
    public Course searchCourse(String courseId) {
        for (Course course : Courses) {
            if (course.getCourseID().equalsIgnoreCase(courseId)) {
                return course;
            }
        }
        return null;
    }
}
