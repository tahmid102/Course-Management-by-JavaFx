package src.drimjavafxproject;

import java.util.ArrayList;
import java.util.List;

public class StudentList {
    private List<Student> students;
    public StudentList(){
        students=new ArrayList<>();
    }
    public void addStudent(Student a){
        students.add(a);
    }
    public boolean isStudentAvailable(Student b){
        for(Student a:students){
            if(a.studentId==b.studentId){
                return true;
            }
        }
        return false;
    }
    public void removeStudent(Student a){
        students.remove(a);
    }
    public Student searchStudent(int userId){
        for(Student a:students){
            if(a.studentId==userId){
                return a;
            }
        }
        return null;
    }

}
