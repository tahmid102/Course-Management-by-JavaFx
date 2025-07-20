package files.Classes;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class PendingTeachersList {
    private final List<Teacher>pendingTeachers;
    public PendingTeachersList(){
        pendingTeachers=new ArrayList<>();
    }
    public void addToPending(Teacher teacher){
        pendingTeachers.add(teacher);
        Path dataDir = Paths.get("database");
        if (!Files.exists(dataDir)) {
            try {
                Files.createDirectory(dataDir);
            } catch (IOException e) {
                System.out.println("Could not create data directory");
                return;
            }
        }

        Path filePath = dataDir.resolve("pendingTeacherCredentials.txt");

        try (BufferedWriter writer = Files.newBufferedWriter(filePath,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(teacher.getID() + "," + teacher.getName() + "," + teacher.getPassword());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving teacher: " + e.getMessage());
        }
    }
    public boolean isDuplicate(int teacherID){
        for(Teacher teacher:pendingTeachers){
            if(teacher.getID()==teacherID){
                return true;
            }
        }
        return false;
    }
    public void loadFromFile(){
         try(BufferedReader reader = new BufferedReader(new FileReader("database/pendingTeacherCredentials.txt"))){
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String name = parts[1];
                    int id = Integer.parseInt(parts[0]);
                    String pass = parts[2];
                    pendingTeachers.add(new Teacher(name, id, pass));
                }
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
