package files.Server;

import files.Classes.*;
import java.io.Serializable;
import java.util.List;

public class DataPacket implements Serializable {
    public enum PacketType {
        STUDENT_DATA_REQUEST,
        TEACHER_DATA_REQUEST,
        COURSE_DATA_REQUEST,
        STUDENT_DATA_RESPONSE,
        TEACHER_DATA_RESPONSE,
        COURSE_DATA_RESPONSE,
        ENROLLMENT_DATA_REQUEST,
        ENROLLMENT_DATA_RESPONSE,
        PENDING_REGISTRATION_REQUEST,
        REGISTRATION_APPROVAL_REQUEST,
        REGISTRATION_RESPONSE,
        FILE_DATA_REQUEST,
        FILE_DATA_RESPONSE,
        SYNC_REQUEST,
        SYNC_RESPONSE
    }

    private PacketType type;
    private Object data;
    private String message;
    private boolean success;

    public DataPacket(PacketType type, Object data) {
        this.type = type;
        this.data = data;
        this.success = true;
        this.message = "";
    }

    public DataPacket(PacketType type, Object data, String message, boolean success) {
        this.type = type;
        this.data = data;
        this.message = message;
        this.success = success;
    }

    // Getters and setters
    public PacketType getType() { return type; }
    public void setType(PacketType type) { this.type = type; }
    
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    @Override
    public String toString() {
        return "DataPacket{" +
                "type=" + type +
                ", message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}