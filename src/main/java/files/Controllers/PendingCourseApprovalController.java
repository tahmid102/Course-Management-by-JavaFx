package files.Controllers;

import files.Classes.Course;
import files.Classes.Loader;
import files.Classes.Student;
import files.Classes.Writer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class PendingCourseApprovalController {
    @FXML private TableView<Student> pendingStudentTable;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, Integer> idColumn;

    private final ObservableList<Student> pendingStudents = FXCollections.observableArrayList();
    private Course currentCourse;

    public void setCourse(Course course) {
        this.currentCourse = course;
        loadPendingStudents(course);
    }

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        pendingStudentTable.setItems(pendingStudents);
    }

    private void loadPendingStudents(Course course) {
        pendingStudents.clear();
        Path path = Paths.get("database/PendingEnrollment.txt");
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    int studentId = Integer.parseInt(parts[0].trim());
                    String courseId = parts[1].trim();
                    if (courseId.equals(course.getCourseID())) {
                        Student student = Loader.studentList.searchStudent(studentId);

                        if (student != null) {
                            pendingStudents.add(student);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading pending enrollments: " + e.getMessage());
        }
    }

    @FXML
    private void approveSelected() {
        Student selected = pendingStudentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            approveEnrollment(selected);
            pendingStudents.remove(selected);
        }
    }

    @FXML
    private void approveAll() {
        for (Student student : new ArrayList<>(pendingStudents)) {
            approveEnrollment(student);
        }
        pendingStudents.clear();
    }

    private void approveEnrollment(Student student) {
        Loader.courseList.searchCourse(currentCourse.getCourseID()).addStudent(student);
        removeFromPendingFile(student.getID(), currentCourse.getCourseID());
    }

    @FXML private void deleteSelected() {
        Student selected = pendingStudentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            removeFromPendingFile(selected.getID(), currentCourse.getCourseID());
            pendingStudents.remove(selected);
        }
    }

    @FXML private void deleteAll() {
        for (Student student : new ArrayList<>(pendingStudents)) {
            removeFromPendingFile(student.getID(), currentCourse.getCourseID());
        }
        pendingStudents.clear();
    }

    private void removeFromPendingFile(int studentId, String courseId) {
        Path path = Paths.get("database/PendingEnrollment.txt");
        try {
            List<String> lines = Files.readAllLines(path);
            List<String> updated = new ArrayList<>();
            for (String line : lines) {
                if (!line.equals(studentId + "," + courseId)) {
                    updated.add(line);
                }
                else{
                    adjustInEnrollments(line);
                }
            }
            Files.write(path, updated, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println("Error updating pending enrollments: " + e.getMessage());
        }
    }
    private void adjustInEnrollments(String line){
        try{
            Writer.writeToFile(line,"database/enrollments.txt");
        } catch (Exception e) {
            System.out.println("Problem in adjusting enrollments "+e.getMessage());
        }
    }
}
