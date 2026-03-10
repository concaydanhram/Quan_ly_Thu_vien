public class Book {
    private String name;
    private String ID;
    private int remaining;

    public Book(String name, String ID, int remaining){
        this.name = name;
        this.ID = ID;
        this.remaining = remaining;
    }

    public String getName() { return name; }
    public String getID() { return ID; }
    public int getRemaining() { return remaining; }

    public boolean setRemaining(int i){
        if ((remaining > 0 && i < 0) || (i > 0)) {
            remaining += i;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return ID + "," + name + "," + remaining;
    }
}