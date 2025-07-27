package files.Server;

import files.Classes.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseServer {
    private static final int PORT = 44444;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private boolean isRunning = false;

    // Data storage (loaded from files)
    private final CourseList courseList = new CourseList();
    private final StudentList studentList = new StudentList();
    private final TeacherList teacherList = new TeacherList();
    private final PendingStudentsList pendingStudentsList = new PendingStudentsList();
    private final PendingTeachersList pendingTeachersList = new PendingTeachersList();

    public DatabaseServer() {
        threadPool = Executors.newFixedThreadPool(10);
        loadAllData();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            isRunning = true;
            System.out.println("Database Server started on port " + PORT);

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            if (isRunning) {
                System.err.println("Server error: " + e.getMessage());
            }
        }
    }

    public void stop() {
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (threadPool != null) {
                threadPool.shutdown();
            }
        } catch (IOException e) {
            System.err.println("Error stopping server: " + e.getMessage());
        }
    }

    private void loadAllData() {
        try {
            Loader.loadAll(); // Use existing loader to populate static lists
            
            // Copy data to local instances
            courseList.getCourses().clear();
            courseList.getCourses().addAll(Loader.courseList.getCourses());
            
            studentList.getStudents().clear();
            studentList.getStudents().addAll(Loader.studentList.getStudents());
            
            teacherList.getTeachers().clear();
            teacherList.getTeachers().addAll(Loader.teacherList.getTeachers());
            
            System.out.println("Server data loaded: " + 
                courseList.getCourses().size() + " courses, " +
                studentList.getStudents().size() + " students, " +
                teacherList.getTeachers().size() + " teachers");
        } catch (Exception e) {
            System.err.println("Error loading server data: " + e.getMessage());
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private SocketWrapper socketWrapper;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                socketWrapper = new SocketWrapper(clientSocket);
                System.out.println("Client connected: " + socketWrapper.getRemoteAddress());

                while (socketWrapper.isConnected()) {
                    try {
                        Object received = socketWrapper.read();
                        
                        if (received instanceof DataPacket) {
                            handleDataPacket((DataPacket) received);
                        } else if (received instanceof FilePacket) {
                            handleFilePacket((FilePacket) received);
                        } else if (received instanceof Notification) {
                            handleNotification((Notification) received);
                        }
                    } catch (ClassNotFoundException e) {
                        System.err.println("Unknown object type received: " + e.getMessage());
                        break;
                    } catch (IOException e) {
                        // Client disconnected
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                try {
                    if (socketWrapper != null) {
                        socketWrapper.closeConnection();
                    }
                    System.out.println("Client disconnected");
                } catch (IOException e) {
                    System.err.println("Error closing client connection: " + e.getMessage());
                }
            }
        }

        private void handleDataPacket(DataPacket packet) {
            try {
                switch (packet.getType()) {
                    case STUDENT_DATA_REQUEST:
                        handleStudentDataRequest();
                        break;
                    case TEACHER_DATA_REQUEST:
                        handleTeacherDataRequest();
                        break;
                    case COURSE_DATA_REQUEST:
                        handleCourseDataRequest();
                        break;
                    case ENROLLMENT_DATA_REQUEST:
                        handleEnrollmentDataRequest();
                        break;
                    case PENDING_REGISTRATION_REQUEST:
                        if (packet.getData() instanceof RegistrationRequest) {
                            handleRegistrationSubmission((RegistrationRequest) packet.getData());
                        } else if (packet.getData() instanceof String) {
                            handlePendingDataRequest((String) packet.getData());
                        }
                        break;
                    case REGISTRATION_APPROVAL_REQUEST:
                        handleRegistrationApproval((String) packet.getData());
                        break;
                    case SYNC_REQUEST:
                        handleSyncRequest();
                        break;
                    default:
                        sendErrorResponse("Unknown request type: " + packet.getType());
                }
            } catch (Exception e) {
                System.err.println("Error handling data packet: " + e.getMessage());
                sendErrorResponse("Server error: " + e.getMessage());
            }
        }

        private void handleStudentDataRequest() throws IOException {
            List<Student> students = new ArrayList<>(studentList.getStudents());
            DataPacket response = new DataPacket(DataPacket.PacketType.STUDENT_DATA_RESPONSE, students);
            socketWrapper.sendResponse(response);
        }

        private void handleTeacherDataRequest() throws IOException {
            List<Teacher> teachers = new ArrayList<>(teacherList.getTeachers());
            DataPacket response = new DataPacket(DataPacket.PacketType.TEACHER_DATA_RESPONSE, teachers);
            socketWrapper.sendResponse(response);
        }

        private void handleCourseDataRequest() throws IOException {
            List<Course> courses = new ArrayList<>(courseList.getCourses());
            DataPacket response = new DataPacket(DataPacket.PacketType.COURSE_DATA_RESPONSE, courses);
            socketWrapper.sendResponse(response);
        }

        private void handleEnrollmentDataRequest() throws IOException {
            // Return enrollment data (could be enhanced to return actual enrollment objects)
            DataPacket response = new DataPacket(DataPacket.PacketType.ENROLLMENT_DATA_RESPONSE, 
                "Enrollment data loaded", "Enrollment data available", true);
            socketWrapper.sendResponse(response);
        }

        private void handlePendingDataRequest(String userType) throws IOException {
            if ("student".equals(userType)) {
                List<Student> pending = loadPendingStudentsFromFile();
                DataPacket response = new DataPacket(DataPacket.PacketType.REGISTRATION_RESPONSE, pending);
                socketWrapper.sendResponse(response);
            } else if ("teacher".equals(userType)) {
                List<Teacher> pending = loadPendingTeachersFromFile();
                DataPacket response = new DataPacket(DataPacket.PacketType.REGISTRATION_RESPONSE, pending);
                socketWrapper.sendResponse(response);
            }
        }

        private void handleRegistrationSubmission(RegistrationRequest request) throws IOException {
            boolean success = false;
            String message = "";

            try {
                if (request.getUserType() == RegistrationRequest.UserType.STUDENT) {
                    // Check if student already exists
                    if (studentList.isStudentAvailable(request.getId())) {
                        message = "Student ID already exists";
                    } else {
                        // Add to pending students file
                        addPendingStudent(request);
                        success = true;
                        message = "Student registration submitted for approval";
                    }
                } else if (request.getUserType() == RegistrationRequest.UserType.TEACHER) {
                    // Check if teacher already exists
                    if (teacherList.isTeacherAvailable(request.getId())) {
                        message = "Teacher ID already exists";
                    } else {
                        // Add to pending teachers file
                        addPendingTeacher(request);
                        success = true;
                        message = "Teacher registration submitted for approval";
                    }
                }
            } catch (Exception e) {
                message = "Error processing registration: " + e.getMessage();
            }

            DataPacket response = new DataPacket(DataPacket.PacketType.REGISTRATION_RESPONSE, 
                null, message, success);
            socketWrapper.sendResponse(response);
        }

        private void handleRegistrationApproval(String approvalData) throws IOException {
            String[] parts = approvalData.split(",");
            if (parts.length != 2) {
                sendErrorResponse("Invalid approval data format");
                return;
            }

            try {
                int userId = Integer.parseInt(parts[0]);
                String userType = parts[1];

                boolean success = false;
                String message = "";

                if ("student".equals(userType)) {
                    success = approveStudent(userId);
                    message = success ? "Student approved successfully" : "Failed to approve student";
                } else if ("teacher".equals(userType)) {
                    success = approveTeacher(userId);
                    message = success ? "Teacher approved successfully" : "Failed to approve teacher";
                }

                DataPacket response = new DataPacket(DataPacket.PacketType.REGISTRATION_RESPONSE, 
                    null, message, success);
                socketWrapper.sendResponse(response);
            } catch (NumberFormatException e) {
                sendErrorResponse("Invalid user ID format");
            }
        }

        private void handleSyncRequest() throws IOException {
            loadAllData(); // Reload all data from files
            DataPacket response = new DataPacket(DataPacket.PacketType.SYNC_RESPONSE, 
                null, "Data synchronized successfully", true);
            socketWrapper.sendResponse(response);
        }

        private void handleFilePacket(FilePacket packet) {
            // Handle file uploads (existing functionality)
            try {
                String dirPath = "uploaded_files/" + packet.getCourseId();
                File dir = new File(dirPath);
                dir.mkdirs();

                String filePath = dirPath + "/" + packet.getFileName();
                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                    fos.write(packet.getFileData());
                }

                System.out.println("File saved: " + filePath);
            } catch (IOException e) {
                System.err.println("Error saving file: " + e.getMessage());
            }
        }

        private void handleNotification(Notification notification) {
            System.out.println("Received notification: " + notification.getNotification());
        }

        private void sendErrorResponse(String errorMessage) {
            try {
                DataPacket errorResponse = new DataPacket(DataPacket.PacketType.REGISTRATION_RESPONSE, 
                    null, errorMessage, false);
                socketWrapper.sendResponse(errorResponse);
            } catch (IOException e) {
                System.err.println("Error sending error response: " + e.getMessage());
            }
        }

        // Helper methods for file operations
        private List<Student> loadPendingStudentsFromFile() {
            List<Student> pending = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader("database/pendingStudentCredentials.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        int id = Integer.parseInt(parts[0].trim());
                        String name = parts[1].trim();
                        String pass = parts[2].trim();
                        pending.add(new Student(name, id, pass));
                    }
                }
            } catch (IOException e) {
                System.err.println("Error loading pending students: " + e.getMessage());
            }
            return pending;
        }

        private List<Teacher> loadPendingTeachersFromFile() {
            List<Teacher> pending = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader("database/pendingTeacherCredentials.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        int id = Integer.parseInt(parts[0].trim());
                        String name = parts[1].trim();
                        String pass = parts[2].trim();
                        pending.add(new Teacher(name, id, pass));
                    }
                }
            } catch (IOException e) {
                System.err.println("Error loading pending teachers: " + e.getMessage());
            }
            return pending;
        }

        private void addPendingStudent(RegistrationRequest request) throws IOException {
            Path filePath = Paths.get("database/pendingStudentCredentials.txt");
            try (BufferedWriter writer = Files.newBufferedWriter(filePath, 
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                writer.write(request.getId() + "," + request.getName() + "," + request.getPassword());
                writer.newLine();
            }
        }

        private void addPendingTeacher(RegistrationRequest request) throws IOException {
            Path filePath = Paths.get("database/pendingTeacherCredentials.txt");
            try (BufferedWriter writer = Files.newBufferedWriter(filePath, 
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                writer.write(request.getId() + "," + request.getName() + "," + request.getPassword());
                writer.newLine();
            }
        }

        private boolean approveStudent(int studentId) {
            try {
                // Find student in pending file
                Student pendingStudent = findAndRemovePendingStudent(studentId);
                if (pendingStudent != null) {
                    // Add to main student file
                    try (BufferedWriter writer = new BufferedWriter(
                            new FileWriter("database/StudentCredentials.txt", true))) {
                        writer.write(pendingStudent.getID() + "," + 
                                   pendingStudent.getName() + "," + 
                                   pendingStudent.getPassword());
                        writer.newLine();
                    }
                    
                    // Add to in-memory list
                    studentList.addStudent(pendingStudent);
                    return true;
                }
            } catch (IOException e) {
                System.err.println("Error approving student: " + e.getMessage());
            }
            return false;
        }

        private boolean approveTeacher(int teacherId) {
            try {
                // Find teacher in pending file
                Teacher pendingTeacher = findAndRemovePendingTeacher(teacherId);
                if (pendingTeacher != null) {
                    // Add to main teacher file
                    try (BufferedWriter writer = new BufferedWriter(
                            new FileWriter("database/TeacherCredentials.txt", true))) {
                        writer.write(pendingTeacher.getID() + "," + 
                                   pendingTeacher.getName() + "," + 
                                   pendingTeacher.getPassword());
                        writer.newLine();
                    }
                    
                    // Add to in-memory list
                    teacherList.addTeacher(pendingTeacher);
                    return true;
                }
            } catch (IOException e) {
                System.err.println("Error approving teacher: " + e.getMessage());
            }
            return false;
        }

        private Student findAndRemovePendingStudent(int studentId) throws IOException {
            Path path = Paths.get("database/pendingStudentCredentials.txt");
            List<String> lines = Files.readAllLines(path);
            List<String> updated = new ArrayList<>();
            Student found = null;

            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 3 && Integer.parseInt(parts[0].trim()) == studentId) {
                    found = new Student(parts[1].trim(), studentId, parts[2].trim());
                } else {
                    updated.add(line);
                }
            }

            Files.write(path, updated);
            return found;
        }

        private Teacher findAndRemovePendingTeacher(int teacherId) throws IOException {
            Path path = Paths.get("database/pendingTeacherCredentials.txt");
            List<String> lines = Files.readAllLines(path);
            List<String> updated = new ArrayList<>();
            Teacher found = null;

            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 3 && Integer.parseInt(parts[0].trim()) == teacherId) {
                    found = new Teacher(parts[1].trim(), teacherId, parts[2].trim());
                } else {
                    updated.add(line);
                }
            }

            Files.write(path, updated);
            return found;
        }
    }

    public static void main(String[] args) {
        DatabaseServer server = new DatabaseServer();
        
        // Add shutdown hook for graceful server shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        
        server.start();
    }
}