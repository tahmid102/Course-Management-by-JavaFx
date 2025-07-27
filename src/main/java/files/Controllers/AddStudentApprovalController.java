package files.Controllers;

import files.Classes.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class AddStudentApprovalController {
    @FXML private TableView<Student> pendingStudentTable;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, Integer> idColumn;
    @FXML private Button approveSelectedButton;
    @FXML private Button approveAllButton;

    private final ObservableList<Student> pendingStudents = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        pendingStudentTable.getColumns().forEach(col -> col.setReorderable(false));
        pendingStudentTable.getColumns().forEach(col -> col.setResizable(false));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        loadPendingStudents();
        pendingStudentTable.setItems(pendingStudents);
    }


    private void loadPendingStudents() {
        String CRED_FILE = "database/StudentCred.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(CRED_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    boolean approved = Boolean.parseBoolean(parts[3].trim());
                    if (!approved) {
                        int id = Integer.parseInt(parts[0].trim());
                        String name = parts[1].trim();
                        String pass = parts[2].trim();
                        pendingStudents.add(new Student(name, id, pass));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void approveSelected() {
        Student selected = pendingStudentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            approveStudent(selected);
            pendingStudents.remove(selected);
        }
    }

    @FXML
    private void approveAll() {
        for (Student student : new ArrayList<>(pendingStudents)) {
            approveStudent(student);
        }
        pendingStudents.clear();
    }

    private void approveStudent(Student student) {
        Path credPath = Paths.get("database/StudentCred.txt");
        try {
            List<String> lines = Files.readAllLines(credPath);
            List<String> updated = new ArrayList<>();

            for (String line : lines) {
                if (line.startsWith(student.getID() + ",")) {
                    // Update approval flag to true
                    String[] parts = line.split(",");
                    if (parts.length < 4) {
                        // Ensure array has 4 elements
                        parts = Arrays.copyOf(parts, 4);
                        parts[3] = "true";
                    } else {
                        parts[3] = "true";
                    }
                    updated.add(String.join(",", parts));
                } else {
                    updated.add(line);
                }
            }

            Files.write(credPath, updated, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.out.println("Error approving student: " + e.getMessage());
        }
    }
    private AdminDashboardController dashboardController;

    public void setDashboardController(AdminDashboardController controller) {
        this.dashboardController = controller;
    }

}
