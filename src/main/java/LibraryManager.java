import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibraryManager {
    private final String DB_URL = "jdbc:mysql://localhost:3306/library_db";
    private final String DB_USER = "root";
    private final String DB_PASS = "123456";

    // --- CÁC HÀM XÓA DỮ LIỆU ---
    public boolean xoaSachDB(String bookID) {
        String query = "DELETE FROM Books WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, bookID);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; } // Lỗi nếu đang có người mượn
    }

    public boolean xoaDocGiaDB(String readerID) {
        String query = "DELETE FROM Readers WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, readerID);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    // --- CÁC HÀM LẤY DỮ LIỆU ---
    public List<Book> layTatCaSach() {
        List<Book> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Books")) {
            while (rs.next()) list.add(new Book(rs.getString("name"), rs.getString("id"), rs.getInt("remaining")));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Reader> layTatCaDocGia() {
        List<Reader> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Readers")) {
            while (rs.next()) list.add(new Reader(rs.getString("name"), rs.getString("id")));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public String layDanhSachSachDangMuon(String readerID) {
        StringBuilder sb = new StringBuilder("Sách đang mượn:\n");
        String q = "SELECT b.name FROM Loans l JOIN Books b ON l.bookID = b.id WHERE l.readerID = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(q)) {
            pstmt.setString(1, readerID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) sb.append("- ").append(rs.getString("name")).append("\n");
        } catch (SQLException e) { e.printStackTrace(); }
        return sb.toString();
    }

    // --- CÁC HÀM NGHIỆP VỤ ---
    public boolean themSachDB(Book b, int sl) {
        String q = "INSERT INTO Books (id, name, remaining) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement p = conn.prepareStatement(q)) {
            p.setString(1, b.getID()); p.setString(2, b.getName()); p.setInt(3, sl);
            return p.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean themDocGiaDB(Reader r) {
        String q = "INSERT INTO Readers (id, name) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement p = conn.prepareStatement(q)) {
            p.setString(1, r.getID()); p.setString(2, r.getName());
            return p.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean xuLyMuonSachDB(String bID, String rID) {
        String u = "UPDATE Books SET remaining = remaining - 1 WHERE id = ? AND remaining > 0";
        String i = "INSERT INTO Loans (readerID, bookID) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            conn.setAutoCommit(false);
            try (PreparedStatement p1 = conn.prepareStatement(u); PreparedStatement p2 = conn.prepareStatement(i)) {
                p1.setString(1, bID);
                if (p1.executeUpdate() > 0) {
                    p2.setString(1, rID); p2.setString(2, bID); p2.executeUpdate();
                    conn.commit(); return true;
                }
            } catch (SQLException ex) { conn.rollback(); }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean xuLyTraSachDB(String bID, String rID) {
        String d = "DELETE FROM Loans WHERE readerID = ? AND bookID = ?";
        String u = "UPDATE Books SET remaining = remaining + 1 WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            conn.setAutoCommit(false);
            try (PreparedStatement p1 = conn.prepareStatement(d); PreparedStatement p2 = conn.prepareStatement(u)) {
                p1.setString(1, rID); p1.setString(2, bID);
                if (p1.executeUpdate() > 0) {
                    p2.setString(1, bID); p2.executeUpdate();
                    conn.commit(); return true;
                }
            } catch (SQLException ex) { conn.rollback(); }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}