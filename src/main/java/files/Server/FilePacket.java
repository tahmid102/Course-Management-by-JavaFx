package files.Server;

import java.io.Serializable;

public class FilePacket implements Serializable {
    private String courseId;
    private String fileName;
    private byte[] fileData;

    public FilePacket(String courseId, String fileName, byte[] fileData) {
        this.courseId = courseId;
        this.fileName = fileName;
        this.fileData = fileData;
    }

    public String getCourseId() { return courseId; }
    public String getFileName() { return fileName; }
    public byte[] getFileData() { return fileData; }
}
