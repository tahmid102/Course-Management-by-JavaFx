package files.Classes;

abstract class Person {
    private String name;
    private int ID;
    private String password;

    public Person(String name, int ID) {
        this.name = name;
        this.ID = ID;
        this.password = "";
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
