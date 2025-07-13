package files.Classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class StudentList {
    private final List<Student> students;

    public StudentList() {
        students = new ArrayList<>();
    }

    public boolean addStudent(Student s) {
        if (isStudentAvailable(s.getID())) return false;
        students.add(s);
        return true;
    }

    public boolean removeStudent(Student s) {
        return students.removeIf(existing -> existing.getID() == s.getID());
    }

    public boolean isStudentAvailable(int ID) {
        for (Student s : students) {
            if (s.getID() == ID) return true;
        }
        return false;
    }

    public Student searchStudent(int enteredId) {
        for (Student s : students) {
            if (s.getID() == enteredId) return s;
        }
        return null;
    }

    public void initializeStudents() {
        try {
            InputStream is = getClass().getResourceAsStream("/database/StudentCredentials.txt");
            assert is != null;
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String data;
            while ((data = br.readLine()) != null) {
                String[] creds = data.split(",");
                if (creds.length == 3) {
                    int stdID = Integer.parseInt(creds[0].trim());
                    String stdName = creds[1].trim();
                    String stdPass = creds[2].trim();
                    Student std = new Student(stdName, stdID, stdPass);
                    addStudent(std);
                }
            }
        } catch (IOException e) {
            System.out.println("Student cred not found: " + e.getMessage());
        }
    }

    public List<Student> getStudents() {
        return students;
    }

    @Override
    public String toString() {
        return "StudentList{" +
                "students=" + students +
                '}';
    }
}
