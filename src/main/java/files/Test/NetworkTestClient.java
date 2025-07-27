package files.Test;

import files.Classes.*;
import files.Server.*;
import java.util.List;

public class NetworkTestClient {
    
    public static void main(String[] args) {
        System.out.println("=== Network Test Client ===");
        
        // Test server availability
        System.out.println("\n1. Testing server availability...");
        boolean serverAvailable = NetworkLoader.isServerAvailable();
        System.out.println("Server available: " + serverAvailable);
        
        if (!serverAvailable) {
            System.out.println("Server is not running. Please start the DatabaseServer first.");
            return;
        }
        
        // Test data loading
        System.out.println("\n2. Testing data loading...");
        NetworkLoader.loadAll();
        
        System.out.println("Students loaded: " + NetworkLoader.studentList.getStudents().size());
        System.out.println("Teachers loaded: " + NetworkLoader.teacherList.getTeachers().size());
        System.out.println("Courses loaded: " + NetworkLoader.courseList.getCourses().size());
        
        // Test registration
        System.out.println("\n3. Testing student registration...");
        RegistrationRequest studentReq = new RegistrationRequest("Test Student", 1234567, "password123", 
            RegistrationRequest.UserType.STUDENT);
        boolean regSuccess = NetworkLoader.submitRegistration(studentReq);
        System.out.println("Student registration success: " + regSuccess);
        
        System.out.println("\n4. Testing teacher registration...");
        RegistrationRequest teacherReq = new RegistrationRequest("Test Teacher", 9876543, "teachpass", 
            RegistrationRequest.UserType.TEACHER);
        boolean teacherRegSuccess = NetworkLoader.submitRegistration(teacherReq);
        System.out.println("Teacher registration success: " + teacherRegSuccess);
        
        // Test approval (commented out to avoid actual approval)
        /*
        System.out.println("\n5. Testing registration approval...");
        boolean approvalSuccess = NetworkLoader.approveRegistration(1234567, "student");
        System.out.println("Student approval success: " + approvalSuccess);
        */
        
        // Test data sync
        System.out.println("\n6. Testing data synchronization...");
        boolean syncSuccess = NetworkLoader.syncDataWithServer();
        System.out.println("Data sync success: " + syncSuccess);
        
        // Display some loaded data
        System.out.println("\n7. Sample loaded data:");
        if (!NetworkLoader.studentList.getStudents().isEmpty()) {
            Student firstStudent = NetworkLoader.studentList.getStudents().get(0);
            System.out.println("First student: " + firstStudent.getName() + " (ID: " + firstStudent.getID() + ")");
        }
        
        if (!NetworkLoader.courseList.getCourses().isEmpty()) {
            Course firstCourse = NetworkLoader.courseList.getCourses().get(0);
            System.out.println("First course: " + firstCourse.getCourseName() + " (" + firstCourse.getCourseID() + ")");
        }
        
        System.out.println("\n=== Test completed ===");
    }
}