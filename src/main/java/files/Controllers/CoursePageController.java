package files.Controllers;

import files.Classes.Course;
import files.Classes.Student;
import files.Classes.Teacher;
import files.Main;
import files.Server.*;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
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
    public Label announcementToggle;
    Course course;
    Student student;
    public Button uploadFileButton;
    public VBox fileListBox;
    private SocketWrapper socketWrapper;
    private ReadThread readThread;
    List<Student> enrolledStudents=new ArrayList<>();
    List<Teacher> assignedTechers=new ArrayList<>();
    List<Course> allCourse=new ArrayList<>();

    public void setCourse(Course course) throws FileNotFoundException {
        this.course = course;
        this.enrolledStudents=course.getCourseStudents();
        this.assignedTechers=course.getCourseTeachers();
        loadDeadlines();
        //loadUpcomingDeadlines();


    }

    public void setStudent(Student student) {
        this.student = student;
        allCourse=student.getCourses();
    }
    public void setSocketWrapper(SocketWrapper socketWrapper) {
        this.socketWrapper = socketWrapper;
        startListening();  // Start listening for server broadcasts
    }

    public void display() throws FileNotFoundException {
        courseName.setText(course.getCourseName());
        creditLOabel.setText("Total Credits: "+ course.getCredit());
        System.out.println(course.getCourseStudents());
        ObservableList<Student> students= FXCollections.observableArrayList(enrolledStudents);
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        participantsTable.setItems(students);
        loadUploadedFiles();



    }




    public void Initialize() throws FileNotFoundException {
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
    public void loadDeadlines()  {
        new Thread(() -> {
            try {
                socketWrapper.write(new GetDeadlinesRequest(course.getCourseID()));
                System.out.println("Sent GetDeadlinesRequest for: " + course.getCourseID().trim());

                Object response = socketWrapper.read();
                System.out.println("Received response: " + response);

                if (response instanceof List<?> list) {
                    if (!list.isEmpty() && list.get(0) instanceof Deadline) {
                        List<Deadline> deadlines = (List<Deadline>) list;
                        Platform.runLater(() -> showDeadlines(deadlines,course));
                    } else if (list.isEmpty()) {
                        // Empty list - no deadlines found
                        Platform.runLater(() -> showAlert("No deadlines found for this course."));
                    } else {
                        Platform.runLater(() -> showAlert("Invalid deadline data received."));
                    }
                } else {
                    //Platform.runLater(() -> showAlert("Unexpected response type: " + response.getClass()));
                }

            } catch (Exception e) {
                e.printStackTrace();
                //Platform.runLater(() -> showAlert("Failed to load deadlines: " + e.getMessage()));
            }
        }).start();
    }
    public void loadUpcomingDeadlines() throws FileNotFoundException {
        try(BufferedReader reader=new BufferedReader(new FileReader("database/deadlines.txt"))){
            String line;
            List<Deadline> deadlines=new ArrayList<>();
            while((line=reader.readLine())!=null){
                String parts[]=line.split(";");
                if(parts.length==4){
                    String courseId=parts[0].trim();
                    String taskname=parts[1].trim();
                    String type=parts[2].trim();
                    LocalDate dueDate= LocalDate.parse(parts[3].trim());

                    if(course.getCourseID().equals(courseId)){
                        deadlines.add(new Deadline(courseId,taskname,type,dueDate));

                    }
                }

            }
            showDeadlines(deadlines,course);

            //showDeadlines(deadlines);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void showDeadlines(List<Deadline> deadlines, Course course) {
        deadlineContainer.getChildren().clear();

        if (deadlines.isEmpty()) {
            Label noDeadlineLabel = new Label("No upcoming deadlines found.");
            noDeadlineLabel.setStyle("-fx-text-fill: gray; -fx-font-style: italic; -fx-font-size: 13px;");
            deadlineContainer.getChildren().add(noDeadlineLabel);
            return;
        }

        for (Deadline d : deadlines) {
            if(this.course!=course)continue;
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

    public void toggleAnnouncements(MouseEvent mouseEvent) { boolean nowVisible = !announcementBox.isVisible();
        announcementBox.setVisible(nowVisible);
        announcementBox.setManaged(nowVisible);
        announcementToggle.setText(nowVisible ? "📢 Hide Announcements" : "📢 Show Announcements");
    }
}
