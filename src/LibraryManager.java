import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibraryManager {
    private Map<String, Book> khoSach;

    private final String DB_URL = "jdbc:mysql://localhost:3306/library_db";
    private final String DB_USER = "root";
    private final String DB_PASS = "123456";

    public LibraryManager() {
        khoSach = new HashMap<>();
    }

    public void layThongTinDocGiaDB(String readerID, String readerName) {
        System.out.println("=====================================");
        System.out.println("Thông tin độc giả: " + readerName + " (ID: " + readerID + ")");
        System.out.println("Các sách đang mượn:");

        String query = "SELECT b.id, b.name FROM Loans l JOIN Books b ON l.bookID = b.id WHERE l.readerID = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, readerID);
            ResultSet rs = pstmt.executeQuery();

            boolean hasBooks = false;
            while (rs.next()) {
                hasBooks = true;
                System.out.println("- " + rs.getString("name") + " (Mã: " + rs.getString("id") + ")");
            }
            if (!hasBooks) {
                System.out.println("- (Hiện chưa mượn cuốn sách nào)");
            }
            System.out.println("=====================================\n");

        } catch (SQLException e) {
            System.out.println("[Lỗi truy vấn thông tin] " + e.getMessage());
        }
    }

    public boolean xuLyMuonSachDB(String bookID, String readerID) {
        String checkLoanQuery = "SELECT 1 FROM Loans WHERE readerID = ? AND bookID = ?";
        String updateBookQuery = "UPDATE Books SET remaining = remaining - 1 WHERE id = ? AND remaining > 0";
        String insertLoanQuery = "INSERT INTO Loans (readerID, bookID) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            //Kiểm tra xem đã mượn cuốn này chưa
            try (PreparedStatement checkStmt = conn.prepareStatement(checkLoanQuery)) {
                checkStmt.setString(1, readerID);
                checkStmt.setString(2, bookID);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    System.out.println("-> [Từ chối] Độc giả đã mượn cuốn sách này rồi!");
                    return false;
                }
            }

            //Trừ sách và Ghi vào bảng Loans
            try (PreparedStatement updateStmt = conn.prepareStatement(updateBookQuery)) {
                updateStmt.setString(1, bookID);
                int rowsAffected = updateStmt.executeUpdate();

                if (rowsAffected > 0) {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertLoanQuery)) {
                        insertStmt.setString(1, readerID);
                        insertStmt.setString(2, bookID);
                        insertStmt.executeUpdate();
                    }
                    return true;
                } else {
                    System.out.println("-> [Từ chối] Sách đã hết hàng trong kho!");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("[Lỗi DB Mượn sách] " + e.getMessage());
            return false;
        }
    }

    public boolean xuLyTraSachDB(String bookID, String readerID) {
        String deleteLoanQuery = "DELETE FROM Loans WHERE readerID = ? AND bookID = ?";
        String updateBookQuery = "UPDATE Books SET remaining = remaining + 1 WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteLoanQuery)) {
                deleteStmt.setString(1, readerID);
                deleteStmt.setString(2, bookID);
                int rowsAffected = deleteStmt.executeUpdate();

                if (rowsAffected > 0) {
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateBookQuery)) {
                        updateStmt.setString(1, bookID);
                        updateStmt.executeUpdate();
                    }
                    return true;
                } else {
                    System.out.println("-> [Từ chối] Độc giả chưa mượn cuốn sách này!");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("[Lỗi DB Trả sách] " + e.getMessage());
            return false;
        }
    }

    public List<Reader> layTatCaDocGia() {
        List<Reader> danhSachDocGia = new ArrayList<>();
        String query = "SELECT id, name FROM Readers";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            // Vòng lặp duyệt qua từng dòng trong bảng Readers
            while (rs.next()) {
                // Tự động gọi Constructor để tạo Object và nhét vào Danh sách
                Reader r = new Reader(rs.getString("name"), rs.getString("id"));
                danhSachDocGia.add(r);
            }

        } catch (SQLException e) {
            System.out.println("Lỗi lấy danh sách độc giả: " + e.getMessage());
        }

        return danhSachDocGia;
    }

    public List<Book> layTatCaSach() {
        List<Book> danhSachSach = new ArrayList<>();
        // Truy vấn lấy id và name từ bảng Books
        String query = "SELECT id, name FROM Books";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            // Duyệt qua từng dòng kết quả trả về từ cơ sở dữ liệu
            while (rs.next()) {
                // Tự động gọi Constructor để tạo Object Book
                Book b = new Book(rs.getString("name"), rs.getString("id"));
                danhSachSach.add(b);
            }

        } catch (SQLException e) {
            System.out.println("Lỗi lấy danh sách sách: " + e.getMessage());
        }

        return danhSachSach;
    }

    public Reader layDocGiaTuDB(String readerID) {
        String query = "SELECT id, name FROM Readers WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, readerID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Reader(rs.getString("name"), rs.getString("id"));
            }
        } catch (SQLException e) {
            System.out.println("Lỗi lấy độc giả: " + e.getMessage());
        }
        return null;
    }

    public Book laySachTuDB(String bookID) {
        String query = "SELECT id, name FROM Books WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, bookID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Book(rs.getString("name"), rs.getString("id"));
            }
        } catch (SQLException e) {
            System.out.println("Lỗi lấy sách: " + e.getMessage());
        }
        return null;
    }

    public boolean themDocGiaDB(Reader reader) {
        String query = "INSERT INTO Readers (id, name) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, reader.getID());
            pstmt.setString(2, reader.getName());
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi thêm độc giả (Có thể ID đã tồn tại): " + e.getMessage());
            return false;
        }
    }

    public boolean themSachDB(Book book, int soLuong) {
        String query = "INSERT INTO Books (id, name, remaining) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, book.getID());
            pstmt.setString(2, book.getName());
            pstmt.setInt(3, soLuong);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi thêm sách (Có thể ID đã tồn tại): " + e.getMessage());
            return false;
        }
    }
}