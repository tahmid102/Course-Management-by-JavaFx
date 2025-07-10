package files.Classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class TeacherHashMap {
    private final HashMap<Integer, Teacher> teachers;

    public TeacherHashMap() {
        teachers = new HashMap<>();
    }

    public boolean addTeacher(Teacher t) {
        if (teachers.containsKey(t.getID())) return false;

        teachers.put(t.getID(), t);
        return true;
    }

    public boolean removeTeacher(Teacher t) {
        return teachers.remove(t.getID()) != null;
    }

    public boolean isTeacherAvailable(int ID) {
        return teachers.containsKey(ID);
    }

    public Teacher searchTeacher(int enteredId) {
        return teachers.get(enteredId);
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
                    int stdID = Integer.parseInt(creds[0].trim());
                    String stdName = creds[1].trim();
                    String stdPass = creds[2].trim();

                    Teacher std = new Teacher(stdName, stdID, stdPass);
                    addTeacher(std);
                }
            }
        } catch (IOException e) {
            System.out.println("Teacher cred not found " + e.getMessage());
        }
    }
}
