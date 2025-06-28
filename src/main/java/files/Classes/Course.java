package files.Classes;

class Course {
    String courseName;
    String courseID;
    public Course(String courseID,String courseName){
        this.courseID=courseID;
        this.courseName=courseName;
    }

    public String getCourseID() {
        return courseID;
    }

    public String getCourseName() {
        return courseName;
    }
}
