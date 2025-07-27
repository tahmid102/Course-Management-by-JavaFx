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
                else if (obj instanceof String s && s.startsWith("GET_DEADLINES;")) {
                    String courseId = s.split(";", 2)[1];
                    List<Deadline> deadlines = new ArrayList<>();
                    File file = new File("database/deadlines.txt");
                    if (file.exists()) {
                        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                            String line;
                            while ((line = br.readLine()) != null) {
                                String[] parts = line.split(";", 4);
                                if (parts.length == 4 && parts[0].equals(courseId)) {
                                    deadlines.add(new Deadline(parts[0], parts[1], parts[2], LocalDate.parse(parts[3])));
                                }
                            }
                        }
                    }
                    socketWrapper.write(deadlines);  // ✅ send list
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
