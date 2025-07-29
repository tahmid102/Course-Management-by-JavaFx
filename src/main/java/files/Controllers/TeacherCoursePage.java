package files.Controllers;

import files.Classes.Course;
import files.Classes.Student;
import files.Classes.Teacher;
import files.Main;
import files.Server.FilePacket;
import files.Server.Notification;
import files.Server.SocketWrapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import files.Server.Deadline;

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
    public TextField taskField;
    public ComboBox typeBox;
    public DatePicker dueDatePicker;
    Teacher teacher;
    @FXML
    public TableView participantsTable;
    public TableColumn studentIdColumn;
    public TableColumn studentNameColumn;
    private File selectedFile;

    Course course;
    List<Student> students=new ArrayList<>();
    private SocketWrapper socketWrapper;
    public void setSocketWrapper(SocketWrapper socketWrapper) {
        this.socketWrapper = socketWrapper;
    }
    public void setTeacher(Teacher teacher){
        this.teacher=teacher;
        Name.setText(teacher.getName());
        typeBox.getItems().addAll("Assignment", "Quiz", "Lab Report", "CT", "Project");
    }

    public void setCourse(Course course) {
        this.course = course;
        students=course.getCourseStudents();
        welcomeLabel.setText(course.getCourseID()+" "+course.getCourseName());

        welcomeLabel.setText(course.getCourseID() + " " + course.getCourseName());

        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        ObservableList<Student> stdList = FXCollections.observableArrayList(students);

        participantsTable.setItems(stdList);

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
        Notification notification = new Notification();
        LocalDateTime now= LocalDateTime.now();

        if (message.isEmpty()) return;

        try {
            File file = new File("database/CourseAnnouncements.txt");
            file.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(file, true)) {
                writer.write(courseId + ";" + teacher.getName() + ";" + message +";"+now+ "\n");
            }
            if(selectedFile!=null) {
                try (FileWriter writer = new FileWriter("database/UploadedFiles.txt", true)) {
                    writer.write(course.getCourseID() + ";" + selectedFile.getName() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Send to server via socket

            notification.setNotification(courseId + ";" + teacher.getName() + ";" + message+";"+now);
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

    public void uploadDeadline(ActionEvent actionEvent) {
        String task = taskField.getText().trim();
        String type = (String) typeBox.getValue();
        LocalDate dueDate = dueDatePicker.getValue();

        if (task.isEmpty() || type == null || dueDate == null) {
            showAlert("❗ Please fill in all fields before uploading.");
            return;
        }

        String courseId = course.getCourseID();
        Deadline deadline = new Deadline(courseId, task, type, dueDate);

        new Thread(() -> {
            try {
                socketWrapper.write(deadline); // send Deadline object
                Object response = socketWrapper.read(); // read reply (blocking, but not on UI thread)

                if (response instanceof String res) {
                    Platform.runLater(() -> {
                        if (res.equals("DEADLINE_SAVED")) {
                            showAlert("✅ Deadline uploaded successfully!");
                            taskField.clear();
                            typeBox.getSelectionModel().clearSelection();
                            dueDatePicker.setValue(null);
                        } else {
                            showAlert("⚠️ Server failed to save deadline.");
                        }
                    });
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                Platform.runLater(() -> showAlert("❌ Error sending deadline to server."));
            }
        }).start(); // 🔁 RUN IN BACKGROUND THREAD
    }
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
