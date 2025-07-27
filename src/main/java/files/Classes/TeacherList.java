package files.Classes;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherList {
    private final List<Teacher> teachers;

    public TeacherList() {
        teachers = new ArrayList<>();
    }

    public boolean addTeacher(Teacher t) {
        if (isTeacherAvailable(t.getID())) return false;
        teachers.add(t);
        return true;
    }

    public boolean removeTeacher(Teacher t) {
        return teachers.removeIf(existing -> existing.getID() == t.getID());
    }

    public boolean isTeacherAvailable(int ID) {
        for (Teacher t : teachers) {
            if (t.getID() == ID) return true;
        }
        return false;
    }

    public Teacher searchTeacher(int enteredId) {
        for (Teacher t : teachers) {
            if (t.getID() == enteredId) return t;
        }
        return null;
    }
    public List<Teacher> getTeachers() {
        return teachers;
    }
    
    // CRITICAL FIX: Add method to clear all teachers
    public void clear() {
        teachers.clear();
    }

    @Override
    public String toString() {
        return "TeacherList{" +
                "teachers=" + teachers +
                '}';
    }
}
