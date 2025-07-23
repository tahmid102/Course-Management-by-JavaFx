// AddTeacherApprovalController.java - EXACT same structure as AddStudentApprovalController
package files.Controllers;

import files.Classes.Teacher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class AddTeacherApprovalController {
    @FXML private TableView<Teacher> pendingTeacherTable;
    @FXML private TableColumn<Teacher, String> nameColumn;
    @FXML private TableColumn<Teacher, Integer> idColumn;
    @FXML private Button approveSelectedButton;
    @FXML private Button approveAllButton;

    private final ObservableList<Teacher> pendingTeachers = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        pendingTeacherTable.getColumns().forEach(col -> col.setReorderable(false));
        pendingTeacherTable.getColumns().forEach(col -> col.setResizable(false));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        loadPendingTeachers();
        pendingTeacherTable.setItems(pendingTeachers);
    }

    private void loadPendingTeachers() {
        String PENDING_FILE = "database/pendingTeacherCredentials.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(PENDING_FILE))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    String pass = parts[2].trim();
                    pendingTeachers.add(new Teacher(name, id, pass));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void approveSelected() {
        Teacher selected = pendingTeacherTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            approveTeacher(selected);
            pendingTeachers.remove(selected);
        }
    }

    @FXML
    private void approveAll() {
        for (Teacher teacher : new ArrayList<>(pendingTeachers)) {
            approveTeacher(teacher);
        }
        pendingTeachers.clear();
    }

    private void approveTeacher(Teacher teacher) {
        String TEACHER_FILE = "database/TeacherCredentials.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEACHER_FILE, true))) {
            writer.write(teacher.getID() + "," + teacher.getName() + "," + teacher.getPassword());
            writer.newLine();
        } catch (IOException e) {
            System.out.println(e.getMessage()+" approve teacher e hocche");
        }

        removeTeacherFromPendingFile(teacher.getID());
    }

    private void removeTeacherFromPendingFile(int id) {
        Path path = Paths.get("database/PendingTeacherCredentials.txt");
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
