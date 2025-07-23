package files.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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
        new Server();
    }
}
