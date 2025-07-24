package files.Controllers;

import files.Classes.Course;
import files.Classes.Student;
import files.Classes.Teacher;
import files.Main;
import files.Server.Notification;
import files.Server.ReadThread;
import files.Server.SocketWrapper;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Hyperlink;

public class CoursePageController {
    public Label courseName;
    public Label creditLOabel;
    public Button logout;
    public Button courses;
    public Button home;
    public VBox announcementContainer;
    public TableView participantsTable;
    public TableColumn studentIdColumn;
    public TableColumn studentNameColumn;
    Course course;
    Student student;
    public Button uploadFileButton;
    public VBox fileListBox;
    private SocketWrapper socketWrapper;
    private ReadThread readThread;
    List<Student> enrolledStudents=new ArrayList<>();
    List<Teacher> assignedTechers=new ArrayList<>();

    public void setCourse(Course course) {
        this.course = course;
        this.enrolledStudents=course.getCourseStudents();
        this.assignedTechers=course.getCourseTeachers();
    }

    public void setStudent(Student student) {
        this.student = student;
    }
    public void setSocketWrapper(SocketWrapper socketWrapper) {
        this.socketWrapper = socketWrapper;
        startListening();  // Start listening for server broadcasts
    }

    public void display(){
        courseName.setText(course.getCourseName());
        creditLOabel.setText("Total Credits: "+ course.getCredit());
        System.out.println(course.getCourseStudents());
        ObservableList<Student> students= FXCollections.observableArrayList(enrolledStudents);
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        participantsTable.setItems(students);
        loadUploadedFiles();

    }




    public void Initialize(){
        display();
    }
    public VBox announcementBox; // From FXML

    public void loadAnnouncements() {

            announcementBox.getChildren().clear();
            try (BufferedReader br = new BufferedReader(new FileReader("database/CourseAnnouncements.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(";");
                    if (parts.length == 3 && parts[0].equals(course.getCourseID())) {
                        Label announcement = new Label(parts[2]);
                        announcement.setStyle("-fx-font-size: 12; -fx-padding: 5;");
                        announcementBox.getChildren().add(announcement);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

    }
    private void startListening() {
        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    Object o = socketWrapper.read();
                    if (o instanceof Notification) {
                        Notification notification = (Notification) o;
                        String[] parts = notification.getNotification().split(";");
                        if (parts.length == 3 && parts[0].equals(course.getCourseID())) {
                            Platform.runLater(() -> {
                                Label label = new Label(parts[2]);
                                label.setStyle("-fx-font-size: 12; -fx-padding: 5;");
                                announcementBox.getChildren().add(label);
                            });
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Client read error: " + e.getMessage());
            }
        });
        thread.setDaemon(true);
        thread.start();
    }


    public void onHome(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            Parent root = loader.load();

            DashboardController controller = loader.getController();
            controller.setCurrentStudent(student);

            Stage stage = (Stage) home.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Oncourses(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Student/StudentCourses.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) courses.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Student Courses");
            StudentCoursesController controller=loader.getController();
            controller.passStudent(student);
            stage.setResizable(true);

            stage.show();
        } catch (Exception ex) {
            java.awt.Label errorLabel = new java.awt.Label();
            errorLabel.setText("Try again.");

            ex.printStackTrace();
        }
    }

    public void onLogout(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/login.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = (Stage) logout.getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("Course Management System");
        stage.setScene(scene);
        stage.show();
    }
    public void loadUploadedFiles() {
        fileListBox.getChildren().clear();

        File record = new File("database/UploadedFiles.txt");
        if (!record.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(record))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";", 2);
                if (parts.length == 2 && parts[0].equals(course.getCourseID())) {
                    String filename = parts[1];

                    Hyperlink fileLink = new Hyperlink(filename);
                    fileLink.setOnAction(e -> openFile(new File("uploaded_files/" + course.getCourseID() + "/" + filename)));

                    fileListBox.getChildren().add(fileLink);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void openFile(File file) {
        try {
            if (file.exists()) {
                java.awt.Desktop.getDesktop().open(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
