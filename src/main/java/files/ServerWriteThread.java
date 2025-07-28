package files;

import files.Classes.*;
import files.Server.SocketWrapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Queue;

public class ServerWriteThread implements Runnable{
    private Thread writeThread;
    private final SocketWrapper wrappedClientSocket;
    private final Queue<Object> messageQueue;

    private CourseList courseList;
    private StudentList studentList;
    private TeacherList teacherList;

    ServerWriteThread(SocketWrapper socketWrapper, Queue<Object> messageQueue){
        wrappedClientSocket = socketWrapper;
        this.messageQueue = messageQueue;
        writeThread = new Thread(this);
        writeThread.setName("ServerWriteThread");
        writeThread.start();

        courseList = new CourseList();
        studentList = new StudentList();
        teacherList = new TeacherList();

        System.out.println("ServerWriteThread started successfully");
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object o = null;

                synchronized (messageQueue) {
                    while ((o = messageQueue.poll()) == null) {
                        try {
                            messageQueue.wait();
                        } catch (InterruptedException e) {
                            System.out.println("ServerWriteThread interrupted");
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                }

                if (o instanceof Request request) {
                    System.out.println("Processing request in WriteThread: " + request.getRequestType());

                    switch (request.getRequestType()) {
                        case GET_ALL_COORDINATED_DATA -> {
                            System.out.println("Loading all data and coordinating...");

                            // Load all data
                            loadStudents();
                            loadTeachers();
                            loadCourses();

                            // Coordinate
                            coordinateStudentCourse();
                            coordinateTeacherCourse();

                            // Send exactly 3 objects in a predictable order
                            wrappedClientSocket.write(studentList);
                            wrappedClientSocket.write(teacherList);
                            wrappedClientSocket.write(courseList);

                            System.out.println("All coordinated data sent successfully");
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Unexpected error in ServerWriteThread: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void loadCourses(){
        courseList.getCourses().clear();
        try (BufferedReader reader = new BufferedReader(new FileReader("database/Courses.txt"))) {
            String line;
            while((line = reader.readLine()) != null){
                String[] words = line.split(",");
                if(words.length == 3){
                    Course course = new Course(words[0].trim(), words[1].trim(), Double.parseDouble(words[2].trim()));
                    courseList.addCourse(course);
                } else {
                    System.out.println("Invalid course line format: " + line);
                }
            }
            System.out.println("Loaded " + courseList.getCourses().size() + " courses");
        } catch (Exception e) {
            System.err.println("Error loading courses: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadTeachers(){
        teacherList.getTeachers().clear();
        try(BufferedReader br = new BufferedReader(new FileReader("database/TeacherCredentials.txt"))) {
            String data;
            while ((data = br.readLine()) != null) {
                String[] creds = data.split(",");
                if (creds.length == 4) {
                    int id = Integer.parseInt(creds[0].trim());
                    String name = creds[1].trim();
                    String pass = creds[2].trim();
                    Teacher teacher = new Teacher(name, id, pass);
                    boolean approved = Boolean.parseBoolean(creds[3].trim());
                    if(approved) {
                        teacherList.addTeacher(teacher);
                    }
                } else {
                    System.out.println("Invalid teacher line format: " + data);
                }
            }
            System.out.println("Loaded " + teacherList.getTeachers().size() + " teachers");
        } catch (IOException e) {
            System.err.println("Teacher credentials file not found: " + e.getMessage());
        }
    }

    private void loadStudents(){
        studentList.getStudents().clear();
        try(BufferedReader br = new BufferedReader(new FileReader("database/StudentCredentials.txt"))) {
            String data;
            while ((data = br.readLine()) != null) {
                String[] creds = data.split(",");
                if (creds.length == 4) {
                    int stdID = Integer.parseInt(creds[0].trim());
                    String stdName = creds[1].trim();
                    String stdPass = creds[2].trim();
                    Student std = new Student(stdName, stdID, stdPass);
                    boolean approved = Boolean.parseBoolean(creds[3].trim());
                    if(approved){
                        studentList.addStudent(std);
                    }
                } else {
                    System.out.println("Invalid student line format: " + data);
                }
            }
            System.out.println("Loaded " + studentList.getStudents().size() + " students");
        } catch (IOException e) {
            System.err.println("Student credentials file not found: " + e.getMessage());
        }
    }

    private void coordinateStudentCourse(){
        try(BufferedReader br = new BufferedReader(new FileReader("database/enrollments.txt"))) {
            String line;
            int enrollments = 0;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if(parts.length == 2){
                    Student student = studentList.searchStudent(Integer.parseInt(parts[0].trim()));
                    Course course = courseList.searchCourse(parts[1].trim());
                    if(student == null){
                        System.out.println("Student not found for enrollment: " + parts[0]);
                        continue;
                    }
                    if(course == null){
                        System.out.println("Course not found for enrollment: " + parts[1]);
                        continue;
                    }
                    courseList.addStudentToCourse(course, student);
                    enrollments++;
                } else {
                    System.out.println("Invalid enrollment line format: " + line);
                }
            }
            System.out.println("Processed " + enrollments + " student enrollments");
        } catch (IOException e){
            System.err.println("Problem coordinating student-course: " + e.getMessage());
        }
    }

    private void coordinateTeacherCourse() {
        try(BufferedReader br = new BufferedReader(new FileReader("database/AssignedCoursesTeacher.txt"))) {
            String line;
            int assignments = 0;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if(parts.length == 2){
                    Teacher teacher = teacherList.searchTeacher(Integer.parseInt(parts[0].trim()));
                    Course course = courseList.searchCourse(parts[1].trim());
                    if(teacher == null){
                        System.out.println("Teacher not found for assignment: " + parts[0]);
                        continue;
                    }
                    if(course == null){
                        System.out.println("Course not found for assignment: " + parts[1]);
                        continue;
                    }
                    courseList.addTeacherToCourse(course, teacher);
                    assignments++;
                } else {
                    System.out.println("Invalid assignment line format: " + line);
                }
            }
            System.out.println("Processed " + assignments + " teacher assignments");
        } catch (IOException e){
            System.err.println("Problem coordinating teacher-course: " + e.getMessage());
        }
    }
}