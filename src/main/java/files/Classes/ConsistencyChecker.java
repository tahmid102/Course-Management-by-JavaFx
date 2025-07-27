package files.Classes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CRITICAL DEBUG UTILITY: This class checks consistency between 
 * loaded objects in memory and actual file data
 */
public class ConsistencyChecker {
    
    public static class InconsistencyReport {
        public List<String> missingFromMemory = new ArrayList<>();
        public List<String> extraInMemory = new ArrayList<>();
        public List<String> approvalMismatches = new ArrayList<>();
        public boolean isConsistent = true;
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== CONSISTENCY REPORT ===\n");
            sb.append("Overall Status: ").append(isConsistent ? "CONSISTENT" : "INCONSISTENT").append("\n\n");
            
            if (!missingFromMemory.isEmpty()) {
                sb.append("MISSING FROM MEMORY (in file but not loaded):\n");
                missingFromMemory.forEach(item -> sb.append("  - ").append(item).append("\n"));
                sb.append("\n");
            }
            
            if (!extraInMemory.isEmpty()) {
                sb.append("EXTRA IN MEMORY (loaded but approval=false in file):\n");
                extraInMemory.forEach(item -> sb.append("  - ").append(item).append("\n"));
                sb.append("\n");
            }
            
            if (!approvalMismatches.isEmpty()) {
                sb.append("APPROVAL MISMATCHES:\n");
                approvalMismatches.forEach(item -> sb.append("  - ").append(item).append("\n"));
                sb.append("\n");
            }
            
            return sb.toString();
        }
    }
    
    public static InconsistencyReport checkStudentConsistency() {
        InconsistencyReport report = new InconsistencyReport();
        Set<Integer> memoryStudents = new HashSet<>();
        Set<Integer> approvedFileStudents = new HashSet<>();
        Set<Integer> allFileStudents = new HashSet<>();
        
        // Get students from memory
        for (Student s : Loader.studentList.getStudents()) {
            memoryStudents.add(s.getID());
        }
        
        // Get students from file
        try (BufferedReader br = new BufferedReader(new FileReader("database/StudentCredentials.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    boolean approved = Boolean.parseBoolean(parts[3].trim());
                    
                    allFileStudents.add(id);
                    if (approved) {
                        approvedFileStudents.add(id);
                    }
                    
                    // Check for inconsistencies
                    if (approved && !memoryStudents.contains(id)) {
                        report.missingFromMemory.add("Student ID " + id + " (" + name + ") - approved in file but not in memory");
                        report.isConsistent = false;
                    }
                    
                    if (!approved && memoryStudents.contains(id)) {
                        report.extraInMemory.add("Student ID " + id + " (" + name + ") - in memory but not approved in file");
                        report.isConsistent = false;
                    }
                }
            }
        } catch (IOException e) {
            report.approvalMismatches.add("Error reading student file: " + e.getMessage());
            report.isConsistent = false;
        }
        
        // Check for students in memory but not in file at all
        for (Integer id : memoryStudents) {
            if (!allFileStudents.contains(id)) {
                Student s = Loader.studentList.searchStudent(id);
                report.extraInMemory.add("Student ID " + id + " (" + (s != null ? s.getName() : "unknown") + ") - in memory but not in file at all");
                report.isConsistent = false;
            }
        }
        
        return report;
    }
    
    public static InconsistencyReport checkTeacherConsistency() {
        InconsistencyReport report = new InconsistencyReport();
        Set<Integer> memoryTeachers = new HashSet<>();
        Set<Integer> approvedFileTeachers = new HashSet<>();
        Set<Integer> allFileTeachers = new HashSet<>();
        
        // Get teachers from memory
        for (Teacher t : Loader.teacherList.getTeachers()) {
            memoryTeachers.add(t.getID());
        }
        
        // Get teachers from file
        try (BufferedReader br = new BufferedReader(new FileReader("database/TeacherCredentials.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    boolean approved = Boolean.parseBoolean(parts[3].trim());
                    
                    allFileTeachers.add(id);
                    if (approved) {
                        approvedFileTeachers.add(id);
                    }
                    
                    // Check for inconsistencies
                    if (approved && !memoryTeachers.contains(id)) {
                        report.missingFromMemory.add("Teacher ID " + id + " (" + name + ") - approved in file but not in memory");
                        report.isConsistent = false;
                    }
                    
                    if (!approved && memoryTeachers.contains(id)) {
                        report.extraInMemory.add("Teacher ID " + id + " (" + name + ") - in memory but not approved in file");
                        report.isConsistent = false;
                    }
                }
            }
        } catch (IOException e) {
            report.approvalMismatches.add("Error reading teacher file: " + e.getMessage());
            report.isConsistent = false;
        }
        
        // Check for teachers in memory but not in file at all
        for (Integer id : memoryTeachers) {
            if (!allFileTeachers.contains(id)) {
                Teacher t = Loader.teacherList.searchTeacher(id);
                report.extraInMemory.add("Teacher ID " + id + " (" + (t != null ? t.getName() : "unknown") + ") - in memory but not in file at all");
                report.isConsistent = false;
            }
        }
        
        return report;
    }
    
    public static void printFullConsistencyReport() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("RUNNING CONSISTENCY CHECK");
        System.out.println("=".repeat(50));
        
        InconsistencyReport studentReport = checkStudentConsistency();
        System.out.println("STUDENTS:");
        System.out.println(studentReport);
        
        InconsistencyReport teacherReport = checkTeacherConsistency();
        System.out.println("TEACHERS:");
        System.out.println(teacherReport);
        
        boolean overallConsistent = studentReport.isConsistent && teacherReport.isConsistent;
        System.out.println("OVERALL SYSTEM STATUS: " + (overallConsistent ? "CONSISTENT" : "INCONSISTENT"));
        System.out.println("=".repeat(50) + "\n");
    }
    
    // Quick check method for debugging
    public static boolean isSystemConsistent() {
        return checkStudentConsistency().isConsistent && checkTeacherConsistency().isConsistent;
    }
}