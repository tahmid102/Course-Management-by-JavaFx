package files.Classes;

import files.Request;
import files.Server.SocketWrapper;

import java.io.IOException;

public class Writer {
    private static SocketWrapper wrappedServer;

    public static void writeToFile(String line, String path) {
        try {
            System.out.println("Connecting to server at 127.0.0.1:55555...");
            wrappedServer = new SocketWrapper("127.0.0.1", 55555);
            System.out.println("Connection established successfully");

            wrappedServer.write(new Request(Request.RequestType.WRITE_TO_FILE,line,path));
            wrappedServer.closeConnection();

            System.out.println("Writer completed successfully");

        } catch (IOException e) {
            System.err.println("Error during loading: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
