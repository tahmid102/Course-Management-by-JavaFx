package files.Classes;

import java.io.Serializable;

abstract class Person implements Serializable {
    private String name;
    private int ID;
    private String password;


    public Person(String name, int ID,String password) {
        this.name = name;
        this.ID = ID;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getID() {
        return ID;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", ID=" + ID +
                ", password='" + password + '\'' +
                '}';
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
