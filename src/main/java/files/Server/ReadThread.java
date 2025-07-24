package files.Server;


import files.Server.SocketWrapper;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ReadThread implements Runnable {
    private Thread thr;
    private SocketWrapper socketWrapper;

    public ReadThread(SocketWrapper socketWrapper) {
        this.socketWrapper = socketWrapper;
        this.thr = new Thread(this);
        thr.start();
    }

    public void run() {
        try {
            while (true) {
                Object o = socketWrapper.read();
                if (o instanceof Notification) {
                    Notification obj = (Notification) o;
                    System.out.println("Received: " + obj.getNotification());
                } else if (o instanceof FilePacket) {
                    FilePacket packet = (FilePacket) o;

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
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                socketWrapper.closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    }