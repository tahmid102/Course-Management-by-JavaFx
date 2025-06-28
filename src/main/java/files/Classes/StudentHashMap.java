package files.Classes;

import java.util.HashMap;

public class StudentHashMap {
    private final HashMap<Integer, Student> students;
    public StudentHashMap(){
        students=new HashMap<>();
    }
    public boolean addStudent(Student s){
        if(students.containsKey(s.getID())) return false;

        students.put(s.getID(),s);
        return true;
    }
    public boolean removeStudent(Student s){
        return students.remove(s.getID())!=null;
    }
    public boolean isStudentAvailable(int ID){
        return students.containsKey(ID);
    }

    public Student searchStudent(int enteredId) {
        return students.get(enteredId);
    }
}
