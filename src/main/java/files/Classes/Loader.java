package files.Classes;

import files.Request;
import files.Server.SocketWrapper;

import java.io.IOException;

public class Loader {

    private static SocketWrapper wrappedServer;
    public static CourseList courseList = new CourseList();
    public static StudentList studentList = new StudentList();
    public static TeacherList teacherList = new TeacherList();

    public static void loadAll() {
        try {
            System.out.println("Connecting to server at 127.0.0.1:55555...");
            wrappedServer = new SocketWrapper("127.0.0.1", 55555);
            System.out.println("Connection established successfully");

            sendCoordinatedDataRequest();
            getCoordinatedInformation();

            wrappedServer.closeConnection();

            System.out.println("Loader completed successfully");

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during loading: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void reloadAll() {
        try {
            sendCoordinatedDataRequest();
            getCoordinatedInformation();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Reload failed: " + e.getMessage());
        }
    }

    private static void sendCoordinatedDataRequest() throws IOException {
        wrappedServer.write(Request.RequestType.GET_ALL_COORDINATED_DATA);
        System.out.println("Sent request for all coordinated data");
    }

    private static void getCoordinatedInformation() throws IOException, ClassNotFoundException {
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

    public static String toDampString() {
        return "Loader{" +
                "courseList=" + courseList +
                ",\n studentList=" + studentList +
                ",\n teacherList=" + teacherList +
                '}';
    }
}
