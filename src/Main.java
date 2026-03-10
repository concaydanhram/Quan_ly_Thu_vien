public class Main {
    public static void main(String[] args) {
        Book b1 = new Book("Lập trình Java", "b01", 2);
        Book b2 = new Book("Cấu trúc dữ liệu", "b02", 1);
        Book b3 = new Book("Mạng máy tính", "b03", 0); // Cố tình để 0 để test sách hết

        Reader r1 = new Reader("Hoàng", "r01");
        Reader r2 = new Reader("Nam", "r02");

        System.out.println("=== 1. TEST THÔNG TIN ĐỘC GIẢ ===");
        r1.hienThiThongTin();
        r2.hienThiThongTin();

        System.out.println("\n=== 2. TEST LOGIC MƯỢN SÁCH ===");
        r1.muonSach(b1);
        r1.muonSach(b1);
        r1.muonSach(b3);

        System.out.println("\n=== 3. TEST KIỂM TRA SỐ LƯỢNG SAU KHI MƯỢN ===");
        System.out.println("Sách '" + b1.getName() + "' ban đầu có 2, hiện còn: " + b1.getRemaining());

        System.out.println("\n=== 4. TEST DANH SÁCH ĐANG MƯỢN ===");
        System.out.println("Danh sách sách Hoàng đang mượn:");
        r1.getListOfBooks();

        System.out.println("\n=== 5. TEST LOGIC TRẢ SÁCH ===");
        r1.traSach(b2);
        r1.traSach(b1);
        System.out.println("Sách '" + b1.getName() + "' sau khi trả, hiện còn: " + b1.getRemaining());

        System.out.println("\n=== 6. TEST DANH SÁCH SAU KHI TRẢ ===");
        System.out.println("Danh sách sách Hoàng đang mượn:");
        r1.getListOfBooks();
    }
}