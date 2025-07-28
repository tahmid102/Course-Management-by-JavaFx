// AddTeacherApprovalController.java - EXACT same structure as AddStudentApprovalController
package files.Controllers;

import files.Classes.Loader;
import files.Classes.Student;
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
    @FXML private Button deleteSelectedButton;
    @FXML private Button deleteAllButton;

    private final ObservableList<Teacher> pendingTeachers = FXCollections.observableArrayList();

    @FXML public void initialize() {
        pendingTeacherTable.getColumns().forEach(col -> col.setReorderable(false));
        pendingTeacherTable.getColumns().forEach(col -> col.setResizable(false));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        loadPendingTeachers();
        pendingTeacherTable.setItems(pendingTeachers);
    }

    private void loadPendingTeachers() {
        String PATH = "database/TeacherCredentials.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    boolean approved= Boolean.parseBoolean(parts[3].trim());
                    if(!approved){
                        int id = Integer.parseInt(parts[0].trim());
                        String name = parts[1].trim();
                        String pass = parts[2].trim();
                        pendingTeachers.add(new Teacher(name, id, pass));
                    }
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
        Path path = Paths.get("database/TeacherCredentials.txt");
        try {
            List<String> lines = Files.readAllLines(path);
            List<String> updated = new ArrayList<>();

            for (String line : lines) {
                if (line.startsWith((teacher.getID() + ","))) {
                    String[] parts = line.split(",");
                    parts[3] = "true";
                    updated.add(String.join(",", parts));
                } else {
                    updated.add(line);
                }
            }
            Files.write(path, updated,StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.CREATE);
            Loader.teacherList.addTeacher(teacher);
        } catch (IOException e) {
            System.out.println("Error approving Teacher: "+e.getMessage());;
        }
    }
    @FXML private void deleteSelected() {
        Teacher selected = pendingTeacherTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            deleteTeacher(selected);
            pendingTeachers.remove(selected);
        }
    }

    @FXML private void deleteAll() {
        for (Teacher teacher : new ArrayList<>(pendingTeachers)) {
            deleteTeacher(teacher);
        }
        pendingTeachers.clear();
    }

    private void deleteTeacher(Teacher teacher) {
        Path path = Paths.get("database/TeacherCredentials.txt");
        try {
            List<String> lines = Files.readAllLines(path);
            List<String> updated = new ArrayList<>();

            for (String line : lines) {
                if(!line.startsWith((teacher.getID()+","))) {
                    updated.add(line);
                }
            }
            Files.write(path, updated,StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.CREATE);
            Loader.teacherList.removeTeacher(teacher);
        } catch (IOException e) {
            System.out.println("Error deleting student: "+e.getMessage());
        }
    }

        private AdminDashboardController dashboardController;

    public void setDashboardController(AdminDashboardController controller) {
        this.dashboardController = controller;
    }
}
