package files.Classes;

import java.io.*;
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
    public List<Student> getStudents() {
        return students;
    }
    
    // CRITICAL FIX: Add method to clear all students
    public void clear() {
        students.clear();
    }

    @Override
    public String toString() {
        return "StudentList{" +
                "students=" + students +
                '}';
    }
}
