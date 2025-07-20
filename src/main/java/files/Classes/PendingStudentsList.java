package files.Classes;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class PendingStudentsList {
    private final List<Student> pendingStudents;
    public PendingStudentsList(){
        pendingStudents=new ArrayList<>();
    }
    public void addToPending(Student student){
        pendingStudents.add(student);
        Path dataDir = Paths.get("database");
        if (!Files.exists(dataDir)) {
            try {
                Files.createDirectory(dataDir);
            } catch (IOException e) {
                System.out.println("Could not create data directory");
                return;
            }
        }

        Path filePath = dataDir.resolve("pendingStudentCredentials.txt");

        try (BufferedWriter writer = Files.newBufferedWriter(filePath,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(student.getID() + "," + student.getName() + "," + student.getPassword());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving student: " + e.getMessage());
        }
    }
    public boolean isDuplicate(int stdID){
        for(Student student:pendingStudents){
            if(student.getID()==stdID){
                return true;
            }
        }
        return false;
    }
    public void loadFromFile(){
        try(BufferedReader br = new BufferedReader(new FileReader("database/pendingStudentCredentials.txt"))){
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String name = parts[0];
                    int id = Integer.parseInt(parts[1]);
                    String pass = parts[2];
                    pendingStudents.add(new Student(name, id, pass));
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
