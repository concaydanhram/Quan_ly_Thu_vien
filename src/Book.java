public class Book {
    private String name;
    private String ID;

    public Book(String name, String ID){
        this.name = name;
        this.ID = ID;
    }

    public String getName() { return name; }
    public String getID() { return ID; }

    @Override
    public String toString() {
        return ID + "," + name;
    }
}