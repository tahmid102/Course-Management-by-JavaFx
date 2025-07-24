package files.Controllers;

import files.Classes.Course;
import files.Classes.Teacher;
import files.Main;
import files.Server.FilePacket;
import files.Server.Notification;
import files.Server.SocketWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

public class TeacherCoursePage {
    public Label Name;
    public Button homeButton;
    public Button logoutButton;
    public VBox mainContentBox;
    public Label welcomeLabel;
    public Label attachedFileName;
    public TextArea t;
    public Button post;
    public Button filePost;
    Teacher teacher;
    private File selectedFile;

    Course course;
    private SocketWrapper socketWrapper;
    public void setSocketWrapper(SocketWrapper socketWrapper) {
        this.socketWrapper = socketWrapper;
    }
    public void setTeacher(Teacher teacher){
        this.teacher=teacher;
        Name.setText(teacher.getName());
    }

    public void setCourse(Course course) {
        this.course = course;
        welcomeLabel.setText(course.getCourseID()+" "+course.getCourseName());
    }

    public void display() {

    }

    public void onHomeClicked(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/TeacherDashboard.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TeacherDasahboardController controller=fxmlLoader.getController();
        controller.setTeacher(teacher);
        Stage stage = (Stage) homeButton.getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("Dashboard");
        stage.setScene(scene);
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

    public void onPost(ActionEvent actionEvent) throws IOException {
        String courseId = course.getCourseID();
        int teacherId = teacher.getID();
        String message = t.getText().trim();

        if (message.isEmpty()) return;

        try {
            // Save to file
            File file = new File("database/CourseAnnouncements.txt");
            file.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(file, true)) {
                writer.write(courseId + ";" + teacherId + ";" + message + "\n");
            }
            if(selectedFile!=null) {
                try (FileWriter writer = new FileWriter("database/UploadedFiles.txt", true)) {
                    writer.write(course.getCourseID() + ";" + selectedFile.getName() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Send to server via socket
            Notification notification = new Notification();
            notification.setNotification(courseId + ";" + teacherId + ";" + message);
            if (socketWrapper != null) {
                socketWrapper.write(notification);
            }

            t.clear();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Announcement Posted!");
            alert.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (selectedFile != null) {
            // 1. Send the post text (already done)
            // 2. Send file separately
            FileInputStream fis = new FileInputStream(selectedFile);
            byte[] fileData = fis.readAllBytes();
            try {
                fis.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            FilePacket packet = new FilePacket(course.getCourseID(), selectedFile.getName(), fileData);
            try {
                socketWrapper.write(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void onFilepost(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            attachedFileName.setText(selectedFile.getName());
        }
    }
}
