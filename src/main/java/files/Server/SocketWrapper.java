package files.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class SocketWrapper {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public SocketWrapper(String s, int port) throws IOException { // used by the client
        this.socket = new Socket(s, port);
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
    }

    public SocketWrapper(Socket s) throws IOException { // used by the server
        this.socket = s;
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
    }

    public Object read() throws IOException, ClassNotFoundException {
        return ois.readObject();
    }

    public void write(Object o) throws IOException {
        oos.writeObject(o);
        oos.flush(); // Ensure data is sent immediately
    }

    public void closeConnection() throws IOException {
        try {
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error closing socket connection: " + e.getMessage());
        }
    }

    // Enhanced methods for better communication
    public DataPacket sendRequest(DataPacket request) throws IOException, ClassNotFoundException {
        write(request);
        return (DataPacket) read();
    }

    public void sendResponse(DataPacket response) throws IOException {
        write(response);
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public void setTimeout(int timeout) throws IOException {
        if (socket != null) {
            socket.setSoTimeout(timeout);
        }
    }

    public String getRemoteAddress() {
        if (socket != null) {
            return socket.getRemoteSocketAddress().toString();
        }
        return "Unknown";
    }
}