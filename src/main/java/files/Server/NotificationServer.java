package files.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NotificationServer {
    private ServerSocket serverSocket;
    private List<SocketWrapper> clientList = new ArrayList<>();
    private final String FILE_PATH = "database/CourseAnnouncements.txt";

    public NotificationServer() {
        try {
            serverSocket = new ServerSocket(44444);
            System.out.println("Notification Server started on port 44444...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                SocketWrapper socketWrapper = new SocketWrapper(clientSocket);
                synchronized (clientList) {
                    clientList.add(socketWrapper);
                }
                new Thread(() -> listenToClient(socketWrapper)).start();
            }
        } catch (IOException e) {
            System.out.println("Server failed: " + e.getMessage());
        }
    }

    private void listenToClient(SocketWrapper socketWrapper) {
        try {
            while (true) {
                Object obj = socketWrapper.read();
                if (obj instanceof Notification) {
                    Notification n = (Notification) obj;
                    saveToFile(n);
                    broadcast(n);
                }else if (obj instanceof Deadline deadline) {
                    File file = new File("database/deadlines.txt");
                    file.getParentFile().mkdirs();

                    synchronized (this) {
                        try (FileWriter fw = new FileWriter(file, true)) {
                            fw.write(deadline.toString() + "\n");
                            socketWrapper.write("DEADLINE_SAVED"); // ✅ must reply to unblock client
                        } catch (IOException e) {
                            e.printStackTrace();
                            socketWrapper.write("DEADLINE_SAVE_FAILED");
                        }
                    }
                }
                else if (obj instanceof FilePacket) {
                    FilePacket packet = (FilePacket) obj;

                    // Create a directory for the course if it doesn't exist
                    String dirPath = "uploaded_files/" + packet.getCourseId();
                    File dir = new File(dirPath);
                    dir.mkdirs();  // ✅ create if not exists

                    // Full path to save the uploaded file
                    String filePath = dirPath + "/" + packet.getFileName();
                    FileOutputStream fos = new FileOutputStream(filePath);
                    fos.write(packet.getFileData());
                    fos.close();

                    System.out.println("File saved: " + filePath);
                }
                else if (obj instanceof GetDeadlinesRequest request) {
                    System.out.println("📩 Received GetDeadlinesRequest for: [" + request.getCourseId() + "]");
                    String courseId = request.getCourseId().trim();
                    List<Deadline> deadlines = new ArrayList<>();
                    File file = new File("database/deadlines.txt");

                    if (file.exists()) {
                        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                            String line;
                            while ((line = br.readLine()) != null) {
                                String[] parts = line.split(";", 4);
                                if (parts.length == 4 && parts.length == 4 && parts[0].trim().equalsIgnoreCase(courseId.trim())) {
                                    deadlines.add(new Deadline(parts[0].trim(), parts[1], parts[2], LocalDate.parse(parts[3])));
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("not sent no client");
                        }
                    }

                    socketWrapper.write(deadlines);
                    System.out.println(deadlines);
                }


            }
        } catch (Exception e) {
            System.out.println("Client disconnected: " + e.getMessage());
        } finally {
            try {
                socketWrapper.closeConnection();
                synchronized (clientList) {
                    clientList.remove(socketWrapper);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveToFile(Notification notification) {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        try (FileWriter fw = new FileWriter(file, true)) {
            fw.write(notification.getNotification() + "\n");
        } catch (IOException e) {
            System.out.println("Error saving to file: " + e.getMessage());
        }
    }

    private void broadcast(Notification notification) {
        synchronized (clientList) {
            for (SocketWrapper client : clientList) {
                try {
                    client.write(notification);
                } catch (IOException e) {
                    System.out.println("Broadcast failed: " + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        new NotificationServer();
    }
}
