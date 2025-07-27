package files.Classes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Loader {
    public static final CourseList courseList=new CourseList();
    public static final StudentList studentList=new StudentList();
    public static final TeacherList teacherList=new TeacherList();
    public static void reloadAll(){
        courseList.getCourses().clear();
        studentList.getStudents().clear();
        teacherList.getTeachers().clear();
        loadAll();
    }
    public static void loadAll(){
        loadCourses();
        loadStudents();
        loadTeachers();
        coordinateStudentCourse();
        coordinateTeacherCourse();
    }
    private static void loadCourses(){
        try (BufferedReader reader = new BufferedReader(new FileReader("database/Courses.txt"))) {
            String line;
            while((line= reader.readLine())!=null){
                String[] words=line.split(",");
                if(words.length==3){
                    Course course=new Course(words[0].trim(),words[1].trim(),Double.parseDouble(words[2].trim()));
                    courseList.addCourse(course);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading courses in CourseList");
            e.printStackTrace();
        }
    }
    private static void loadStudents(){
        try(BufferedReader br = new BufferedReader(new FileReader("database/StudentCredentials.txt"))) {
            String data;
            while ((data = br.readLine()) != null) {
                String[] creds = data.split(",");
                if (creds.length == 4) {
                    int stdID = Integer.parseInt(creds[0].trim());
                    String stdName = creds[1].trim();
                    String stdPass = creds[2].trim();
                    Student std = new Student(stdName, stdID, stdPass);
                    boolean approved=Boolean.parseBoolean(creds[3].trim());
                    if(approved){
                        studentList.addStudent(std);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Student cred not found: " + e.getMessage());
        }
    }
    private static void loadTeachers(){
        try(BufferedReader br= new BufferedReader(new FileReader("database/TeacherCredentials.txt"))) {
            String data;
            while ((data = br.readLine()) != null) {
                String[] creds = data.split(",");
                if (creds.length == 4) {
                    int id = Integer.parseInt(creds[0].trim());
                    String name = creds[1].trim();
                    String pass = creds[2].trim();
                    Teacher teacher = new Teacher(name, id, pass);
                    boolean approved=Boolean.parseBoolean(creds[3].trim());
                    if(approved) {
                        teacherList.addTeacher(teacher);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Teacher credentials not found: " + e.getMessage());
        }
    }
    private static void coordinateStudentCourse()  {
        try(BufferedReader br = new BufferedReader(new FileReader("database/enrollments.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if(parts.length==2){
                    Student student=studentList.searchStudent(Integer.parseInt(parts[0].trim()));
                    Course course=courseList.searchCourse(parts[1].trim());
                    if(student==null){
                        System.out.println("Student nai");
                        continue;
                    }if(course==null){
                        System.out.println("Course nai");
                        continue;
                    }
                    courseList.addStudentToCourse(course,student);
                }
            }
        }catch (IOException e){
            System.out.println("Problem in coordinating");
        }
    }
    private static void coordinateTeacherCourse()  {
        try(BufferedReader br = new BufferedReader(new FileReader("database/AssignedCoursesTeacher.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if(parts.length==2){
                    Teacher teacher=teacherList.searchTeacher(Integer.parseInt(parts[0].trim()));
                    Course course=courseList.searchCourse(parts[1].trim());
                    courseList.addTeacherToCourse(course,teacher);
                }
            }
        }catch (IOException e){
            System.out.println("Problem in coordinating");
        }
    }


    public static String toDampString() {
        return "Loader{" +
                "courseList=" + courseList +
                ",\n studentList=" + studentList +
                ",\n teacherList=" + teacherList +
                '}';
    }
}
