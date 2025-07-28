package files.Classes;

import files.Request;
import files.Server.SocketWrapper;

import java.io.IOException;

public class Loader implements Runnable {

    private static SocketWrapper wrappedServer;
    public static CourseList courseList = new CourseList();
    public static StudentList studentList = new StudentList();
    public static TeacherList teacherList = new TeacherList();

    @Override
    public void run() {
        try {
            System.out.println("Loader thread started");
            loadAll();
            System.out.println("Loader completed successfully");
        } catch (Exception e) {
            System.err.println("Exception in Loader thread: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (wrappedServer != null) {
                try {
                    wrappedServer.closeConnection();
                } catch (Exception e) {
                    System.err.println("Error closing socket: " + e.getMessage());
                }
            }
        }
    }

    public static void reloadAll() {
        courseList.getCourses().clear();
        studentList.getStudents().clear();
        teacherList.getTeachers().clear();
    }

    public void loadAll() {
        wrappedServer = null;
        try {
            System.out.println("Connecting to server at 127.0.0.1:55555...");
            wrappedServer = new SocketWrapper("127.0.0.1", 55555);
            System.out.println("Connection established successfully");

            if (!testConnection()) {
                throw new IOException("Connection test failed");
            }

            // OPTION 1: Send one special request for coordinated data
            sendCoordinatedDataRequest();
            getCoordinatedInformation();

        } catch (IOException e) {
            System.err.println("IO Error in Loader: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in Loader: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendCoordinatedDataRequest() throws IOException {
        // Send a single request for fully coordinated data
        wrappedServer.write(Request.RequestType.GET_ALL_COORDINATED_DATA);
        System.out.println("Sent request for all coordinated data");
    }

    private void getCoordinatedInformation() throws IOException, ClassNotFoundException {
        // Server will send exactly 3 objects in this order
        System.out.println("Reading student list...");
        Object studentObj = wrappedServer.read();
        if (studentObj instanceof StudentList) {
            studentList = (StudentList) studentObj;
            System.out.println("Received student list with " + studentList.getStudents().size() + " students");
        }

        System.out.println("Reading teacher list...");
        Object teacherObj = wrappedServer.read();
        if (teacherObj instanceof TeacherList) {
            teacherList = (TeacherList) teacherObj;
            System.out.println("Received teacher list with " + teacherList.getTeachers().size() + " teachers");
        }

        System.out.println("Reading course list...");
        Object courseObj = wrappedServer.read();
        if (courseObj instanceof CourseList) {
            courseList = (CourseList) courseObj;
            System.out.println("Received course list with " + courseList.getCourses().size() + " courses");
        }
    }

    private static boolean testConnection() {
        try {
            System.out.println("Testing connection...");
            return true;
        } catch (Exception e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
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