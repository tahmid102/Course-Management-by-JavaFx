package files.Classes;

import files.Server.*;
import java.io.IOException;
import java.util.List;

public class NetworkLoader {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 44444;
    private static SocketWrapper socketWrapper;
    
    // Static lists for data storage (same as Loader)
    public static final CourseList courseList = new CourseList();
    public static final StudentList studentList = new StudentList();
    public static final TeacherList teacherList = new TeacherList();
    public static final PendingStudentsList pendingStudentsList = new PendingStudentsList();
    public static final PendingTeachersList pendingTeachersList = new PendingTeachersList();

    // Network connection management
    public static boolean connectToServer() {
        try {
            socketWrapper = new SocketWrapper(SERVER_HOST, SERVER_PORT);
            socketWrapper.setTimeout(10000); // 10 second timeout
            System.out.println("Connected to server at " + SERVER_HOST + ":" + SERVER_PORT);
            return true;
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            return false;
        }
    }

    public static void disconnectFromServer() {
        if (socketWrapper != null) {
            try {
                socketWrapper.closeConnection();
                System.out.println("Disconnected from server");
            } catch (IOException e) {
                System.err.println("Error disconnecting from server: " + e.getMessage());
            }
        }
    }

    // Enhanced loading methods - try network first, fallback to local files
    public static void loadAll() {
        if (connectToServer()) {
            try {
                loadFromNetwork();
                System.out.println("Data loaded from network successfully");
            } catch (Exception e) {
                System.err.println("Network loading failed, falling back to local files: " + e.getMessage());
                loadFromLocalFiles();
            } finally {
                disconnectFromServer();
            }
        } else {
            System.out.println("Server not available, loading from local files");
            loadFromLocalFiles();
        }
    }

    // Load data from network
    private static void loadFromNetwork() throws IOException, ClassNotFoundException {
        loadStudentsFromNetwork();
        loadTeachersFromNetwork();
        loadCoursesFromNetwork();
        loadEnrollmentsFromNetwork();
        loadPendingRegistrationsFromNetwork();
    }

    @SuppressWarnings("unchecked")
    private static void loadStudentsFromNetwork() throws IOException, ClassNotFoundException {
        DataPacket request = new DataPacket(DataPacket.PacketType.STUDENT_DATA_REQUEST, null);
        DataPacket response = socketWrapper.sendRequest(request);
        
        if (response.isSuccess() && response.getData() != null) {
            List<Student> students = (List<Student>) response.getData();
            studentList.getStudents().clear();
            for (Student student : students) {
                studentList.addStudent(student);
            }
            System.out.println("Loaded " + students.size() + " students from network");
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadTeachersFromNetwork() throws IOException, ClassNotFoundException {
        DataPacket request = new DataPacket(DataPacket.PacketType.TEACHER_DATA_REQUEST, null);
        DataPacket response = socketWrapper.sendRequest(request);
        
        if (response.isSuccess() && response.getData() != null) {
            List<Teacher> teachers = (List<Teacher>) response.getData();
            teacherList.getTeachers().clear();
            for (Teacher teacher : teachers) {
                teacherList.addTeacher(teacher);
            }
            System.out.println("Loaded " + teachers.size() + " teachers from network");
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadCoursesFromNetwork() throws IOException, ClassNotFoundException {
        DataPacket request = new DataPacket(DataPacket.PacketType.COURSE_DATA_REQUEST, null);
        DataPacket response = socketWrapper.sendRequest(request);
        
        if (response.isSuccess() && response.getData() != null) {
            List<Course> courses = (List<Course>) response.getData();
            courseList.getCourses().clear();
            for (Course course : courses) {
                courseList.addCourse(course);
            }
            System.out.println("Loaded " + courses.size() + " courses from network");
        }
    }

    private static void loadEnrollmentsFromNetwork() throws IOException, ClassNotFoundException {
        DataPacket request = new DataPacket(DataPacket.PacketType.ENROLLMENT_DATA_REQUEST, null);
        DataPacket response = socketWrapper.sendRequest(request);
        
        if (response.isSuccess() && response.getData() != null) {
            // Handle enrollment data - this would be a list of enrollment records
            System.out.println("Loaded enrollment data from network");
        }
    }

    private static void loadPendingRegistrationsFromNetwork() throws IOException, ClassNotFoundException {
        // Load pending students
        DataPacket studentRequest = new DataPacket(DataPacket.PacketType.PENDING_REGISTRATION_REQUEST, "student");
        DataPacket studentResponse = socketWrapper.sendRequest(studentRequest);
        
        if (studentResponse.isSuccess() && studentResponse.getData() != null) {
            @SuppressWarnings("unchecked")
            List<Student> pendingStudents = (List<Student>) studentResponse.getData();
            for (Student student : pendingStudents) {
                pendingStudentsList.addToPending(student);
            }
        }

        // Load pending teachers
        DataPacket teacherRequest = new DataPacket(DataPacket.PacketType.PENDING_REGISTRATION_REQUEST, "teacher");
        DataPacket teacherResponse = socketWrapper.sendRequest(teacherRequest);
        
        if (teacherResponse.isSuccess() && teacherResponse.getData() != null) {
            @SuppressWarnings("unchecked")
            List<Teacher> pendingTeachers = (List<Teacher>) teacherResponse.getData();
            for (Teacher teacher : pendingTeachers) {
                pendingTeachersList.addToPending(teacher);
            }
        }
    }

    // Fallback to original Loader functionality
    private static void loadFromLocalFiles() {
        Loader.loadAll();
    }

    // Network-based registration
    public static boolean submitRegistration(RegistrationRequest registrationRequest) {
        if (connectToServer()) {
            try {
                DataPacket request = new DataPacket(DataPacket.PacketType.PENDING_REGISTRATION_REQUEST, registrationRequest);
                DataPacket response = socketWrapper.sendRequest(request);
                
                if (response.isSuccess()) {
                    System.out.println("Registration submitted successfully: " + response.getMessage());
                    return true;
                } else {
                    System.err.println("Registration failed: " + response.getMessage());
                    return false;
                }
            } catch (Exception e) {
                System.err.println("Error submitting registration: " + e.getMessage());
                return false;
            } finally {
                disconnectFromServer();
            }
        }
        return false;
    }

    // Network-based approval
    public static boolean approveRegistration(int userId, String userType) {
        if (connectToServer()) {
            try {
                String approvalData = userId + "," + userType;
                DataPacket request = new DataPacket(DataPacket.PacketType.REGISTRATION_APPROVAL_REQUEST, approvalData);
                DataPacket response = socketWrapper.sendRequest(request);
                
                if (response.isSuccess()) {
                    System.out.println("Registration approved successfully: " + response.getMessage());
                    return true;
                } else {
                    System.err.println("Registration approval failed: " + response.getMessage());
                    return false;
                }
            } catch (Exception e) {
                System.err.println("Error approving registration: " + e.getMessage());
                return false;
            } finally {
                disconnectFromServer();
            }
        }
        return false;
    }

    // Sync data with server
    public static boolean syncDataWithServer() {
        if (connectToServer()) {
            try {
                DataPacket request = new DataPacket(DataPacket.PacketType.SYNC_REQUEST, null);
                DataPacket response = socketWrapper.sendRequest(request);
                
                if (response.isSuccess()) {
                    System.out.println("Data synchronized with server: " + response.getMessage());
                    return true;
                } else {
                    System.err.println("Data synchronization failed: " + response.getMessage());
                    return false;
                }
            } catch (Exception e) {
                System.err.println("Error syncing data: " + e.getMessage());
                return false;
            } finally {
                disconnectFromServer();
            }
        }
        return false;
    }

    // Check server connection
    public static boolean isServerAvailable() {
        boolean connected = connectToServer();
        if (connected) {
            disconnectFromServer();
        }
        return connected;
    }
}