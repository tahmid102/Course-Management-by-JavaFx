package files.Controllers;

import files.Classes.Course;
import files.Classes.Writer;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddCourseController {

    @FXML private TextField courseIdField;
    @FXML private TextField courseNameField;
    @FXML private TextField courseCreditField;
    @FXML private TextArea courseDescriptionArea;
    @FXML private Label statusLabel;
    @FXML private Button addCourseButton;
    @FXML private Button cancelButton;


    @FXML
    public void initialize() {
        addCourseButton.setOnAction(event -> handleAddCourse());
        cancelButton.setOnAction(event -> closeWindow());
    }

    private void handleAddCourse() {
        String id = courseIdField.getText().trim();
        String name = courseNameField.getText().trim();
        String creditStr = courseCreditField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || creditStr.isEmpty()) {
            statusLabel.setText("Please fill in all required fields.");
            return;
        }

        double credit;
        try {
            credit = Double.parseDouble(creditStr);
            if (credit <= 0 || credit > 5) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            statusLabel.setText("Credit hours must be a number between 0 and 5.");
            return;
        }
        Course course = new Course(id, name, credit);
        writeCourseToFile(course);
    }

    private void writeCourseToFile(Course course) {
            String line = String.format("%s,%s,%.2f",
                    course.getCourseID(),
                    course.getCourseName(),
                    course.getCredit());
        Writer.writeToFile(line,"database/Courses.txt");
        statusLabel.setText("Course added successfully.");
        clearFields();
    }

    private void clearFields() {
        courseIdField.clear();
        courseNameField.clear();
        courseCreditField.clear();
        courseDescriptionArea.clear();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
