package files.Controllers;


import files.Classes.Course;
import files.Classes.Loader;
import files.Classes.Student;
import files.Main;
import files.Server.SocketWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class StudentCoursesController extends DashboardController{
    public Button backButton;
    public Label course1;
    public VBox courseVbox;
    public Button logoutButton;
    private Stage stage;
    private Scene scene;
    private Parent root;
    public Button refresh;
    Student student=currentStudent;

    List<Course> courses;


    public void passStudent(Student student) {
        this.student = student;
        int studentId=student.getID();
        //this.courses=student.getCourses();
        courses=Loader.studentList.searchStudent(studentId).getCourses();

        displayCoursesd();
    }

    public void displayCoursesd(){
        courseVbox.getChildren().clear();
        System.out.println("Loaded courses for student: " + student.getID());
        for (Course c : courses) {
            System.out.println("Course: " + c.getCourseID() + " - " + c.getCourseName());
        }

        if(courses.isEmpty()){
            Label label =new Label("You Haven't Enrolled in any course");
            label.setStyle("-fx-font-size: 10; -fx-padding: 5;");
            courseVbox.getChildren().add(label);
        }
        else{
            for(Course course:courses){
                //Label label =new Label(course.getCourseName());
                Hyperlink hyperlinlk=new Hyperlink(course.getCourseID()+" "  +course.getCourseName());
                hyperlinlk.setStyle("-fx-font-size: 15; -fx-padding: 5;");
                courseVbox.getChildren().add(hyperlinlk);
                hyperlinlk.setOnAction(e->{
                    try {
                        onCoursePage(course);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });

            }
        }
    }







    public void toDash(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
        Scene scene= null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DashboardController controller=fxmlLoader.getController();
        controller.setCurrentStudent(student);
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Dashboard");
        stage.show();
    }

    public void onCoursePage(Course course) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Student/CoursePage.fxml"));
        Scene scene= null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CoursePageController controller=fxmlLoader.getController();
        controller.setSocketWrapper(new SocketWrapper(new Socket("127.0.0.1",44444)));

        controller.setCourse(course);
        controller.setStudent(student);

        controller.display();
        controller.loadAnnouncements();
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("My Courses");
        stage.show();
    }


    public void onLogout(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/login.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("Course Management System");
        stage.setScene(scene);
        stage.show();
    }
    public void onRefresh(ActionEvent actionEvent){
        System.out.println("🔁 Refresh button clicked");

        Loader.reloadAll(); // Reload from server

        // Update student reference with new list data
        student = Loader.studentList.searchStudent(student.getID());
        if (student == null) {
            System.out.println("⚠️ Student not found after reload");
            return;
        }

        courses = student.getCourses();
        displayCoursesd(); // Re-render UI
        System.out.println("✅ Refreshed courses for: " + student.getID());
    }
}
