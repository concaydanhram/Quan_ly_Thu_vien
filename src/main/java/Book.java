public class Book {
    private String name;
    private String ID;
    private int remaining;

    public Book(String name, String ID){
        this.name = name;
        this.ID = ID;
    }

    public Book(String name, String id, int remaining) {
        this.name = name;
        this.ID = id;
        this.remaining = remaining;
    }

    public String getName() { return name; }
    public String getID() { return ID; }
    public int getRemaining() { return remaining; }

    @Override
    public String toString() {
        return ID + "," + name;
    }
}