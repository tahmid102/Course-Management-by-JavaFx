package files.Server;

import java.io.Serializable;

public class RegistrationRequest implements Serializable {
    public enum UserType {
        STUDENT, TEACHER
    }

    private String name;
    private int id;
    private String password;
    private UserType userType;
    private long timestamp;

    public RegistrationRequest(String name, int id, String password, UserType userType) {
        this.name = name;
        this.id = id;
        this.password = password;
        this.userType = userType;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { this.userType = userType; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "RegistrationRequest{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", userType=" + userType +
                ", timestamp=" + timestamp +
                '}';
    }
}