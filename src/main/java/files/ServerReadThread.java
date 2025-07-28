package files;

import files.Server.SocketWrapper;

import java.io.IOException;
import java.util.Queue;

public class ServerReadThread implements Runnable {
    private final SocketWrapper wrappedClientSocket;
    private final Queue<Object> messageQueue;

    ServerReadThread(SocketWrapper socketWrapper, Queue<Object> messageQueue){
        wrappedClientSocket = socketWrapper;
        this.messageQueue = messageQueue;
        Thread readThread = new Thread(this);
        readThread.setName("ServerReadThread");
        readThread.start();
        System.out.println("ServerReadThread started successfully");
    }

    @Override
    public void run() {
        try{
            while (true){
                System.out.println("Waiting for client request...");
                Object o = wrappedClientSocket.read();
                System.out.println("Received object: " + o.getClass().getSimpleName());

                if(o instanceof Request request) {
                    System.out.println("Request type: " + request.getRequestType());
                    synchronized (messageQueue) {
                        messageQueue.offer(request);
                        messageQueue.notify();
                    }
                    System.out.println("Request added to message queue and WriteThread notified");
                } else if (o instanceof Request.RequestType requestType) {
                    System.out.println("RequestType received directly: " + requestType);
                    Request request = new Request(requestType);
                    synchronized (messageQueue) {
                        messageQueue.offer(request);
                        messageQueue.notify();
                    }
                    System.out.println("Request created and added to message queue");
                } else {
                    System.out.println("Unknown object type received: " + o.getClass().getName());
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected or IO error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found in server read: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in ServerReadThread: " + e.getMessage());
            e.printStackTrace();
        }
    }
}