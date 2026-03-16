import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
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
        } catch (SQLException e) {
            return false;
        }
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
    public boolean themSachDB(Book book, int soLuongMoi) {
        String checkQuery = "SELECT 1 FROM Books WHERE id = ?";
        String updateQuery = "UPDATE Books SET remaining = remaining + ? WHERE id = ?";
        String insertQuery = "INSERT INTO Books (id, name, remaining) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, book.getID());
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, soLuongMoi);
                        updateStmt.setString(2, book.getID());
                        return updateStmt.executeUpdate() > 0;
                    }
                } else {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setString(1, book.getID());
                        insertStmt.setString(2, book.getName());
                        insertStmt.setInt(3, soLuongMoi);
                        return insertStmt.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
                    ghiLichSu(conn, rID, bID, "Mượn");
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
                    ghiLichSu(conn, rID, bID, "Trả");
                    conn.commit(); return true;
                }
            } catch (SQLException ex) { conn.rollback(); }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private void ghiLichSu(Connection conn, String readerID, String bookID, String action) throws SQLException {
        String insertHistory = "INSERT INTO TransactionHistory (readerID, bookID, actionType) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertHistory)) {
            ps.setString(1, readerID);
            ps.setString(2, bookID);
            ps.setString(3, action);
            ps.executeUpdate();
        }
    }

    public String layLichSuMuonTraReader(String readerID) {
        StringBuilder sb = new StringBuilder("\n--- LỊCH SỬ GIAO DỊCH ---\n");
        String q = "SELECT h.bookID, b.name, h.actionType, h.transactionDate " +
                "FROM TransactionHistory h " +
                "JOIN Books b ON h.bookID = b.id " +
                "WHERE h.readerID = ? " +
                "ORDER BY h.transactionDate DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(q)) {
            pstmt.setString(1, readerID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                sb.append(String.format("[%s] %s: %s (Mã: %s)\n",
                        rs.getTimestamp("transactionDate"),
                        rs.getString("actionType"),
                        rs.getString("name"),
                        rs.getString("bookID")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return sb.toString();
    }

    public void xuatDanhSachSachTxt(List<Book> danhSach, String fileName) throws IOException {
        try (PrintWriter writer = new PrintWriter(fileName, "UTF-8")) {
            writer.println("===============================================================");
            writer.println("                DANH SÁCH QUẢN LÝ SÁCH TRONG KHO               ");
            writer.println("===============================================================");
            writer.printf("%-10s | %-30s | %-10s\n", "Mã Sách", "Tên Đầu Sách", "Số Lượng");
            writer.println("---------------------------------------------------------------");

            for (Book b : danhSach) {
                writer.printf("%-10s | %-30s | %-10d\n", b.getID(), b.getName(), b.getRemaining());
            }
            writer.println("===============================================================");
        }
    }

    public void xuatChiTietDocGiaTxt(Reader reader, String fileName) throws IOException {
        try (PrintWriter writer = new PrintWriter(fileName, "UTF-8")) {
            String hienTai = layDanhSachSachDangMuon(reader.getID());
            String lichSu = layLichSuMuonTraReader(reader.getID());

            writer.println("===============================================================");
            writer.println("                   CHI TIẾT THÔNG TIN ĐỘC GIẢ                  ");
            writer.println("===============================================================");
            writer.println("Tên độc giả: " + reader.getName());
            writer.println("Mã số:       " + reader.getID());
            writer.println("---------------------------------------------------------------");
            writer.println(hienTai);
            writer.println(lichSu);
            writer.println("===============================================================");
            writer.println("Ngày xuất file: " + new java.util.Date());
        }
    }

    public int nhapSachTuFile(String filePath) throws IOException {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] data = line.split("\\|");
                if (data.length == 3) {
                    try {
                        String id = data[0].trim();
                        String name = data[1].trim();
                        int sl = Integer.parseInt(data[2].trim());

                       if (themSachDB(new Book(name, id), sl)) {
                            count++;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Lỗi định dạng số lượng tại dòng: " + line);
                    }
                }
            }
        }
        return count;
    }
}