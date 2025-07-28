package files.Controllers;

import files.Classes.Loader;
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
    @FXML private Button deleteSelectedButton;
    @FXML private Button deleteAllButton;

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
        String PENDING_FILE = "database/StudentCredentials.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(PENDING_FILE))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    boolean approved= Boolean.parseBoolean(parts[3].trim());
                    if(!approved){
                        int id = Integer.parseInt(parts[0].trim());
                        String name = parts[1].trim();
                        String pass = parts[2].trim();
                        pendingStudents.add(new Student(name, id, pass));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Student approval failed in controller "+e.getMessage());
        }
    }

    @FXML private void approveSelected() {
        Student selected = pendingStudentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            approveStudent(selected);
            pendingStudents.remove(selected);
        }
    }

    @FXML private void approveAll() {
        for (Student student : new ArrayList<>(pendingStudents)) {
            approveStudent(student);
        }
        pendingStudents.clear();
    }

    private void approveStudent(Student student) {
        Path path = Paths.get("database/StudentCredentials.txt");
        try {
            List<String> lines = Files.readAllLines(path);
            List<String> updated = new ArrayList<>();

            for (String line : lines) {
                if(line.startsWith((student.getID()+","))){
                    String []parts=line.split(",");
                    parts[3]="true";
                    updated.add(String.join(",",parts));
                }else{
                    updated.add(line);
                }
            }

            Files.write(path, updated,StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.CREATE);
            Loader.studentList.addStudent(student);
        } catch (IOException e) {
            System.out.println("Error approving student: "+e.getMessage());
        }
    }
    @FXML private void deleteSelected() {
        Student selected = pendingStudentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            deleteStudent(selected);
            pendingStudents.remove(selected);
        }
    }

    @FXML private void deleteAll() {
        for (Student student : new ArrayList<>(pendingStudents)) {
            deleteStudent(student);
        }
        pendingStudents.clear();
    }

    private void deleteStudent(Student student) {
        Path path = Paths.get("database/StudentCredentials.txt");
        try {
            List<String> lines = Files.readAllLines(path);
            List<String> updated = new ArrayList<>();

            for (String line : lines) {
                if(!line.startsWith((student.getID()+","))) {
                    updated.add(line);
                }
            }
            Files.write(path, updated,StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.CREATE);
            Loader.studentList.removeStudent(student);
        } catch (IOException e) {
            System.out.println("Error deleting student: "+e.getMessage());
        }
    }

}
