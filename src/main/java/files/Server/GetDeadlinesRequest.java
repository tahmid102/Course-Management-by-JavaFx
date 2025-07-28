package files.Server;

import java.io.Serializable;

public class GetDeadlinesRequest implements Serializable {
    private String courseId;

    public GetDeadlinesRequest(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseId() {
        return courseId;
    }
}