package files.Classes;

class Course {
    String courseName;
    String courseID;
    double credit;
    public Course(String courseID,String courseName,double credit){
        this.courseID=courseID;
        this.courseName=courseName;
        this.credit=credit;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getCourseName() {
        return courseName;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }
}
