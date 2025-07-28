package files.Controllers;

import files.Classes.Course;
import files.Classes.Student;
import files.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.*;
import java.util.*;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;


public class CoursesController {
    public VBox allCourseVbox;
    public Button backButton;
    public ComboBox filterBox;
    public TextField SearchBox;
    public Button logout;
    List<Course> Courses=new ArrayList<>();
    Student currentStudent;
    List<Course> EnrolledCourse=new ArrayList<>();
    List<Course> PendingEnrollment=new ArrayList<>();
    Map<String,String> requirements=new HashMap<>();

    public void passStudent(Student student){
        currentStudent=student;
        this.EnrolledCourse=student.getCourses();
        loadCourses();
        try {
            loadPending();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            loadRequirements();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public void loadCourses(){
        Courses.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader("database/Courses.txt"))) {
            String line;
            while((line= reader.readLine())!=null){
                String[] words=line.split(",");
                if(words.length==3){
                    Course course=new Course(words[0].trim(),words[1].trim(),Double.parseDouble(words[2].trim()));
                    Courses.add(course);
                }
            }
        } catch (Exception e) {
            System.out.println("Error");
        }

    }
    public void loadPending() throws FileNotFoundException {
        try(BufferedReader reader=new BufferedReader(new FileReader("database/PendingEnrollment.txt"))){
            String line;
            while ((line= reader.readLine())!=null){
                String parts[]=line.split(",");
                if(parts.length==2){
                    String studentId=parts[0].trim();
                    String courseId=parts[1].trim();
                    if(studentId.equals(String.valueOf(currentStudent.getID()))){
                        for(Course c: Courses){
                            if(courseId.equals(c.getCourseID())){
                                PendingEnrollment.add(c);
                                break;
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e){
            System.out.println("Pending not loaded");
        }
    }
    public void loadRequirements() throws FileNotFoundException {
        try(BufferedReader reader=new BufferedReader(new FileReader("database/CourseRequirement.txt"))){
            String line;
            while((line=reader.readLine())!=null){
                String parts[]=line.split(",");
                if(parts.length==2){
                    requirements.put(parts[0].trim(),parts[1].trim());
                }
            }

        } catch (Exception e) {
            System.out.println("Vul hoye gelo");
        }
    }

    private HBox createCourseRow(Course a){
        HBox CourseRow = getHBox();
        CourseRow.setFillHeight(true);
        CourseRow.setPrefWidth(Region.USE_COMPUTED_SIZE);
        CourseRow.setStyle("-fx-padding: 10;" +
                "-fx-alignment: center-left;" +
                "-fx-background-color: #f9f9f9;" +
                "-fx-border-color: #cccccc;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5;");

        Label label =new Label(a.getCourseID());
        label.setStyle("-fx-font-size: 14; -fx-padding: 5;");label.setPrefWidth(300);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(label, Priority.ALWAYS);
        label.setMaxWidth(Region.USE_PREF_SIZE);

        Button addButton=new Button("Apply");
        addButton.setPrefWidth(60);
        addButton.setMinWidth(Region.USE_PREF_SIZE);
        addButton.setMaxWidth(Region.USE_PREF_SIZE);
        addButton.setStyle(
                "-fx-background-color: #19FF19;" +
                        "-fx-border-color: #cccccc;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;"
        );
        addButton.setOnAction(e->{

            //EnrolledCourse.add(a);

            String courseID=a.getCourseID();
            String requiredCourse=requirements.get(courseID);
            if(requiredCourse!=null){
                boolean found=false;
                for(Course c:EnrolledCourse){
                    if(c.getCourseID().trim().equals(requiredCourse.trim())) found=true;
                }
                if(!found){
                    Alert alert=new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Not Eligible");
                    alert.setHeaderText(null);
                    alert.setContentText("To apply for " + courseID + ", you must be enrolled in " + requiredCourse);
                    alert.showAndWait();
                    return;
                }
            }
            PendingEnrollment.add(a);
                    try (FileWriter writer = new FileWriter("database/PendingEnrollment.txt", true)) {
                        writer.write(currentStudent.getID() + "," + a.getCourseID() + "\n");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
            display(Courses);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("You Applied for" + a.getCourseID());
                    alert.showAndWait();
        }
        );

        CourseRow.getChildren().addAll(label,addButton);
        return CourseRow;

    }

    public void display(List<Course> Courses){
        allCourseVbox.getChildren().clear();
        for(Course a: Courses){
            if(EnrolledCourse.contains(a)) continue;
            if(PendingEnrollment.contains(a)) {
                continue;
            }
            HBox courseRow=createCourseRow(a);
            allCourseVbox.getChildren().add(courseRow);
        }
    }

    private static @NotNull HBox getHBox() {
        HBox CourseRow=new HBox();
        CourseRow.setFillHeight(true);
        CourseRow.setPrefWidth(Region.USE_COMPUTED_SIZE);
        CourseRow.setSpacing(20);
        CourseRow.setPrefWidth(500);
        CourseRow.setStyle(
                "-fx-padding: 10;" +
                        "-fx-alignment: center-left;" +"-fx-alignment: center-left;" +
                        "-fx-background-color: #f9f9f9;" +
                        "-fx-border-color: #cccccc;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;"
        );
        return CourseRow;
    }
    @FXML
    public void initialize(){
        loadCourses();
        Set<String> departments = new HashSet<>();

        for (Course course : Courses) {
            String deptCode = course.getCourseID().replaceAll("[^A-Za-z]", "").toUpperCase();
            departments.add(deptCode);
        }

        filterBox.getItems().add("All"); // optional default
        filterBox.getItems().addAll(departments);
        filterBox.setValue("All"); // default selected
        filterBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            onSearchAndFilter();
        });
        SearchBox.textProperty().addListener((obs, oldVal, newVal) -> onSearchAndFilter());



        display(Courses);
    }

    public void onBack(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
        Scene scene= null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DashboardController controller=fxmlLoader.getController();
        controller.setCurrentStudent(currentStudent);
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Dashboard");
        stage.show();
    }
    @FXML
    public void onSearchAndFilter() {
        String keyword = SearchBox.getText().trim().toLowerCase();
        String selectedDept = (filterBox.getValue() != null) ? filterBox.getValue().toString() : "All";
        //String selectedDept = filterBox.getValue().toString();

        List<Course> filtered = Courses.stream()
                .filter(course -> {
                    boolean matchesSearch = course.getCourseID().toLowerCase().contains(keyword)
                            || course.getCourseName().toLowerCase().contains(keyword);
                    String deptCode = course.getCourseID().replaceAll("[^A-Za-z]", "").toUpperCase();
                    boolean matchesFilter = selectedDept == null || selectedDept.equals("All") || deptCode.equals(selectedDept);
                    return matchesSearch && matchesFilter;
                })
                .toList();

        display(filtered);
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
}
