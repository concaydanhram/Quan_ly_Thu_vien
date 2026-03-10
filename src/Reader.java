import java.util.LinkedList;
import java.util.List;

public class Reader extends Person implements BookAction {
    private List<Book> books;

    public Reader(String name, String ID){
        super(name, ID);
        this.books = new LinkedList<>();
    }

    @Override
    public void hienThiThongTin() {
        System.out.println("Độc giả: " + getName() + " (ID: " + getID() + ")");
    }

    @Override
    public void muonSach(Book book) {
        if(books.contains(book)) {
            System.out.println("Khách hàng " + getName() + " đã mượn cuốn này rồi!");
        } else {
            if(book.setRemaining(-1)){
                System.out.println("Khách hàng " + getName() + " mượn thành công: " + book.getName());
                books.add(book);
            } else {
                System.out.println("Sách " + book.getName() + " đã hết!");
            }
        }
    }

    @Override
    public void traSach(Book book){
        if(books.contains(book)) {
            System.out.println("Khách hàng " + getName() + " trả thành công " + book.getName());
            books.remove(book);
            book.setRemaining(1);
        } else {
            System.out.println("Khách hàng " + getName() + " chưa mượn cuốn " + book.getName() + "!");
        }
    }

    public void getListOfBooks(){
        for(Book book : books){
            System.out.println("- " + book.getName() + " (" + book.getID() + ")");
        }
    }
}