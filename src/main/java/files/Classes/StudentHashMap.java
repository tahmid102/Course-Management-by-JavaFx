package files.Classes;



import java.io.*;
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
    public void initializeStudents() {
        try {
            InputStream is=getClass().getResourceAsStream("/database/StudentCredentials.txt");
            assert is != null;
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String data;
            while ((data=br.readLine())!=null){
                String[] creds=data.split(",");
                if(creds.length==3){
                    int stdID=Integer.parseInt(creds[0].trim());
                    String stdName=creds[1].trim();
                    String stdPass=creds[2].trim();

                    Student std=new Student(stdName,stdID,stdPass);
                    addStudent(std);
                }
            }
        } catch (IOException e) {
            System.out.println("Student cred not found "+e.getMessage());
        }

    }
}

