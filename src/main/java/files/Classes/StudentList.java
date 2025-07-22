package files.Classes;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StudentList {
    private final List<Student> students;

    public StudentList() {
        students = new ArrayList<>();
    }

    public boolean addStudent(Student s) {
        if (isStudentAvailable(s.getID())) return false;
        students.add(s);
        return true;
    }

    public boolean removeStudent(Student s) {
        return students.removeIf(existing -> existing.getID() == s.getID());
    }

    public boolean isStudentAvailable(int ID) {
        for (Student s : students) {
            if (s.getID() == ID) return true;
        }
        return false;
    }

    public Student searchStudent(int enteredId) {
        for (Student s : students) {
            if (s.getID() == enteredId) return s;
        }
        return null;
    }

    public void initializeStudents() {
        students.clear();
        try(BufferedReader br = new BufferedReader(new FileReader("database/StudentCredentials.txt"))) {
            String data;
            while ((data = br.readLine()) != null) {
                String[] creds = data.split(",");
                if (creds.length == 3) {
                    int stdID = Integer.parseInt(creds[0].trim());
                    String stdName = creds[1].trim();
                    String stdPass = creds[2].trim();
                    Student std = new Student(stdName, stdID, stdPass);
                    addStudent(std);
                }
            }
        } catch (IOException e) {
            System.out.println("Student cred not found: " + e.getMessage());
        }

    }
    public void LoadCourse(){
        try(BufferedReader br= new BufferedReader(new FileReader("database/enrollments.txt"))){
            CourseList courses=new CourseList();
            courses.loadCourses();
            String line;
            while((line=br.readLine())!=null){
                String[] crs=line.split(",");
                if(crs.length==2){
                    int stdId=Integer.parseInt(crs[0].trim());
                    String CourseID=crs[1].trim();
                    Student student=searchStudent(stdId);
                    if(student!=null){
                        Course course=courses.searchCourse(CourseID);
                        if(course!=null) student.addCourses(course);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Courses didnt load");
        }

    }

    public List<Student> getStudents() {
        return students;
    }

    @Override
    public String toString() {
        return "StudentList{" +
                "students=" + students +
                '}';
    }
}
