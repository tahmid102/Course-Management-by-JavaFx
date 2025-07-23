package files.Server;

import java.io.Serializable;

public class Notification implements Serializable {

    private String notification;

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }
}