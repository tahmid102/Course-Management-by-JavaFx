package files.Server;

import java.io.Serializable;
import java.time.LocalDate;

public class Deadline implements Serializable {
    private String courseId;
    private String taskName;
    private String type;
    private LocalDate dueDate;

    public Deadline(String courseId, String taskName, String type, LocalDate dueDate) {
        this.courseId = courseId;
        this.taskName = taskName;
        this.type = type;
        this.dueDate = dueDate;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getType() {
        return type;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    @Override
    public String toString() {
        return courseId + ";" + taskName + ";" + type + ";" + dueDate;
    }
}
