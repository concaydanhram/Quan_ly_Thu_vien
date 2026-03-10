public abstract class Person {
    private String name;
    private String ID;

    public Person(String name, String ID){
        this.name = name;
        this.ID = ID;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getID() { return ID; }
    public void setID(String ID) { this.ID = ID; }

    // Phương thức abstract bắt buộc các class con phải implement
    public abstract void hienThiThongTin();
}