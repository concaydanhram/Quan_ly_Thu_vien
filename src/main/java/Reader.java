public class Reader extends Person implements BookAction {

    public Reader(String name, String ID){
        super(name, ID);
    }

    @Override
    public void hienThiThongTin(LibraryManager manager) {
//        manager.layThongTinDocGiaDB(this.getID(), this.getName());
    }

    @Override
    public void muonSach(Book book, LibraryManager manager) {
        System.out.print("Đang xử lý yêu cầu mượn '" + book.getName() + "' cho " + getName() + "... ");
        if(manager.xuLyMuonSachDB(book.getID(), this.getID())){
            System.out.println("-> THÀNH CÔNG!");
        }
    }

    @Override
    public void traSach(Book book, LibraryManager manager){
        System.out.print("Đang xử lý yêu cầu trả '" + book.getName() + "' cho " + getName() + "... ");
        if(manager.xuLyTraSachDB(book.getID(), this.getID())) {
            System.out.println("-> THÀNH CÔNG!");
        }
    }
}