package files;

import files.Server.SocketWrapper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class RealServer {
    private ServerSocket serverSocket;

    public RealServer(){
        try {
            serverSocket = new ServerSocket(55555);
            System.out.println("Server started successfully on port 55555");
            startNotificationServer();
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from: " + clientSocket.getInetAddress());
                serve(clientSocket);
            }
        } catch (Exception e) {
            System.err.println("Server startup error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    void serve(Socket clientSocket){
        try {
            // Use regular LinkedList Queue
            Queue<Object> messageQueue = new LinkedList<>();
            SocketWrapper wrappedClientSocket = new SocketWrapper(clientSocket);

            System.out.println("Starting server threads for client...");
            new ServerReadThread(wrappedClientSocket, messageQueue);
            new ServerWriteThread(wrappedClientSocket, messageQueue);
            System.out.println("Server threads started successfully");

        } catch (IOException e) {
            System.err.println("Error setting up client connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void startNotificationServer() {
        new Thread(() -> {
            try {
                new files.Server.NotificationServer(); // adjust package if needed
            } catch (Exception e) {
                System.err.println("holona NotificationServer: " + e.getMessage());
            }
        }).start();
        System.out.println(" Notification Server kaj kore");
    }

    public static void main(String[] args) {
        System.out.println("Starting RealServer...");
        new RealServer();
    }
}