package files.Classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    public void initializeTeachers() {
        try {
            InputStream is = getClass().getResourceAsStream("/database/TeacherCredentials.txt");
            assert is != null;
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String data;
            while ((data = br.readLine()) != null) {
                String[] creds = data.split(",");
                if (creds.length == 3) {
                    int id = Integer.parseInt(creds[0].trim());
                    String name = creds[1].trim();
                    String pass = creds[2].trim();

                    Teacher teacher = new Teacher(name, id, pass);
                    addTeacher(teacher);
                }
            }
        } catch (IOException e) {
            System.out.println("Teacher credentials not found: " + e.getMessage());
        }
    }

    public List<Teacher> getAllTeachers() {
        return teachers;
    }

    @Override
    public String toString() {
        return "TeacherList{" +
                "teachers=" + teachers +
                '}';
    }
}
