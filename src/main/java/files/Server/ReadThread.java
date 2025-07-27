package files.Server;


import files.Server.SocketWrapper;

import javax.xml.crypto.Data;
import java.io.*;

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
                } else if (o instanceof Deadline deadline) {
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