import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        LibraryManager manager = new LibraryManager();
        Scanner scanner = new Scanner(System.in);

        // Biến điều khiển vòng lặp
        boolean isRunning = true;

        System.out.println("=== HỆ THỐNG QUẢN LÝ THƯ VIỆN ĐÃ KHỞI ĐỘNG ===");

        while (isRunning) {
            // In Menu ra màn hình
            System.out.println("\n=========================================");
            System.out.println("                 MENU                    ");
            System.out.println("=========================================");
            System.out.println("1. Xem danh sách tất cả độc giả");
            System.out.println("2. Xem danh sách tất cả đầu sách");
            System.out.println("3. Xem thông tin & lịch sử mượn của 1 độc giả");
            System.out.println("4. Mượn sách");
            System.out.println("5. Trả sách");
            System.out.println("0. Thoát chương trình");
            System.out.println("=========================================");
            System.out.print("Vui lòng chọn chức năng (0-5): ");

            int choice = -1;
            try {
                // Đọc toàn bộ dòng và ép kiểu sang số nguyên để tránh lỗi trôi dòng
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Lựa chọn không hợp lệ. Vui lòng nhập một số!");
                continue; // Bỏ qua phần dưới, quay lại đầu vòng lặp while
            }

            // Xử lý các lựa chọn
            switch (choice) {
                case 1:
                    List<Reader> docGias = manager.layTatCaDocGia();
                    System.out.println("\n--- DANH SÁCH ĐỘC GIẢ ---");
                    for (Reader r : docGias) {
                        System.out.println("- " + r.getName() + " (Mã: " + r.getID() + ")");
                    }
                    break;

                case 2:
                    List<Book> sachs = manager.layTatCaSach();
                    System.out.println("\n--- DANH SÁCH SÁCH ---");
                    for (Book b : sachs) {
                        System.out.println("- " + b.getName() + " (Mã: " + b.getID() + ")");
                    }
                    break;

                case 3:
                    System.out.print("\nNhập mã độc giả cần xem (VD: r01): ");
                    String xemIdDocGia = scanner.nextLine();

                    // Nhờ Manager lấy dữ liệu từ DB để tạo Object
                    Reader xemDocGia = manager.layDocGiaTuDB(xemIdDocGia);
                    if (xemDocGia != null) {
                        xemDocGia.hienThiThongTin(manager);
                    }
                    break;

                case 4:
                    System.out.println("\n--- CHỨC NĂNG MƯỢN SÁCH ---");
                    System.out.print("Nhập mã độc giả: ");
                    String mIdDocGia = scanner.nextLine();
                    System.out.print("Nhập mã sách muốn mượn: ");
                    String mIdSach = scanner.nextLine();

                    // Load thông tin từ DB lên
                    Reader mDocGia = manager.layDocGiaTuDB(mIdDocGia);
                    Book mSach = manager.laySachTuDB(mIdSach);

                    // Kiểm tra xem ID người dùng nhập có thật sự tồn tại trong CSDL không
                    if (mDocGia != null && mSach != null) {
                        mDocGia.muonSach(mSach, manager);
                    } else {
                        System.out.println("-> [LỖI] Không tìm thấy độc giả hoặc sách tương ứng trong CSDL!");
                    }
                    break;

                case 5:
                    System.out.println("\n--- CHỨC NĂNG TRẢ SÁCH ---");
                    System.out.print("Nhập mã độc giả: ");
                    String tIdDocGia = scanner.nextLine();
                    System.out.print("Nhập mã sách muốn trả: ");
                    String tIdSach = scanner.nextLine();

                    Reader tDocGia = manager.layDocGiaTuDB(tIdDocGia);
                    Book tSach = manager.laySachTuDB(tIdSach);

                    if (tDocGia != null && tSach != null) {
                        tDocGia.traSach(tSach, manager);
                    } else {
                        System.out.println("-> [LỖI] Không tìm thấy độc giả hoặc sách tương ứng trong CSDL!");
                    }
                    break;

                case 6:
                    System.out.println("\n--- THÊM ĐỘC GIẢ MỚI ---");
                    System.out.print("Nhập mã độc giả mới: ");
                    String newReaderID = scanner.nextLine();
                    System.out.print("Nhập tên độc giả: ");
                    String newReaderName = scanner.nextLine();

                    Reader newReader = new Reader(newReaderName, newReaderID);
                    if (manager.themDocGiaDB(newReader)) {
                        System.out.println("-> THÊM THÀNH CÔNG!");
                    }
                    break;

                case 7:
                    System.out.println("\n--- THÊM SÁCH MỚI ---");
                    System.out.print("Nhập mã sách mới: ");
                    String newBookID = scanner.nextLine();
                    System.out.print("Nhập tên sách: ");
                    String newBookName = scanner.nextLine();
                    System.out.print("Nhập số lượng nhập kho: ");
                    int newQuantity = Integer.parseInt(scanner.nextLine());

                    Book newBook = new Book(newBookName, newBookID);
                    if (manager.themSachDB(newBook, newQuantity)) {
                        System.out.println("-> THÊM THÀNH CÔNG!");
                    }
                    break;

                case 0:
                    System.out.println("\nĐang đóng kết nối... Tạm biệt!");
                    isRunning = false; // Phá vỡ vòng lặp while
                    break;

                default:
                    System.out.println("\nChức năng không tồn tại. Vui lòng chọn số từ 0 đến 5.");
                    break;
            }
        }

        // Đóng luồng quét dữ liệu khi chương trình kết thúc
        scanner.close();
    }
}