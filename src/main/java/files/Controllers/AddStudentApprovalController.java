package files.Controllers;

import files.Classes.*;
import files.Server.*;
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
        // Try to load from network first
        if (NetworkLoader.isServerAvailable()) {
            try {
                NetworkLoader.loadAll();
                // The NetworkLoader would have loaded pending students
                // For now, we'll still read from local file as a fallback
                loadPendingStudentsFromLocal();
            } catch (Exception e) {
                System.err.println("Network loading failed, using local data: " + e.getMessage());
                loadPendingStudentsFromLocal();
            }
        } else {
            loadPendingStudentsFromLocal();
        }
    }

    private void loadPendingStudentsFromLocal() {
        String PENDING_FILE = "database/pendingStudentCredentials.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(PENDING_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    String pass = parts[2].trim();
                    pendingStudents.add(new Student(name, id, pass));
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
        // Try network approval first
        if (NetworkLoader.isServerAvailable()) {
            boolean success = NetworkLoader.approveRegistration(student.getID(), "student");
            if (success) {
                System.out.println("Student approved via network: " + student.getName());
                return;
            }
        }
        
        // Fallback to local approval
        String STUDENT_FILE = "database/StudentCredentials.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STUDENT_FILE, true))) {
            writer.write(student.getID() + "," + student.getName() + "," + student.getPassword());
            writer.newLine();
        } catch (IOException e) {
            System.out.println(e.getMessage()+" approve student e hocche");
        }
        removeStudentFromPendingFile(student.getID());
    }

    private void removeStudentFromPendingFile(int id) {
        Path path = Paths.get("database/pendingStudentCredentials.txt");
        try {
            List<String> lines = Files.readAllLines(path);
            List<String> updated = new ArrayList<>();

            for (String line : lines) {
                if (!line.startsWith(id + ",")) {
                    updated.add(line);
                }
            }

            Files.write(path, updated);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private AdminDashboardController dashboardController;

    public void setDashboardController(AdminDashboardController controller) {
        this.dashboardController = controller;
    }

}
