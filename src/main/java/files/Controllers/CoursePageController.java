package files.Controllers;

import files.Classes.Course;
import files.Classes.Student;
import files.Classes.Teacher;
import files.Main;
import files.Server.Deadline;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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
    public VBox deadlineContainer;
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
        loadDeadlines();
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
                    if (parts.length == 4 && parts[0].equals(course.getCourseID())) {
                        Label announcement = new Label(parts[1]+": "+parts[2]+"  "+parts[3]);
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
                        if (parts.length == 4 && parts[0].equals(course.getCourseID())) {
                            Platform.runLater(() -> {
                                Label label = new Label(parts[1]+": "+parts[2]+"  "+parts[3]);
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
    public void loadDeadlines() {
        new Thread(() -> {
            try {
                socketWrapper.write("GET_DEADLINES;" + course.getCourseID());  // e.g., "GET_DEADLINES;CSE101"
                Object o = socketWrapper.read();  // ⏳ blocking read

                if (o instanceof List<?> list) {
                    List<Deadline> deadlines = (List<Deadline>) list;
                    Platform.runLater(() -> showDeadlines(deadlines));
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                Platform.runLater(() -> showAlert("❌ Failed to load deadlines."));
            }
        }).start();  // ✅ background thread
    }


    private void showDeadlines(List<Deadline> deadlines) {
        deadlineContainer.getChildren().clear();

        for (Deadline d : deadlines) {
            long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), d.getDueDate());

            String status;
            String color;

            if (daysLeft < 0) {
                status = "⛔ Overdue";
                color = "#bdc3c7";
            } else if (daysLeft == 0) {
                status = "🔥 Due Today";
                color = "#e74c3c";
            } else if (daysLeft <= 2) {
                status = "⚠️ Due in " + daysLeft + " day(s)";
                color = "#f39c12";
            } else {
                status = "✅ Due in " + daysLeft + " day(s)";
                color = "#27ae60";
            }

            Label label = new Label(String.format("%s – %s", d.getTaskName(), status));
            label.setStyle("-fx-background-color: " + color +
                    "; -fx-background-radius: 5; -fx-padding: 6px; -fx-text-fill: white; -fx-font-size: 13px;");
            deadlineContainer.getChildren().add(label);
        }
    }
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
