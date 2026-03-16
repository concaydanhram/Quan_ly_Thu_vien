📚 Hệ Thống Quản Lý Thư Viện (Library Management System)
Dự án này là một ứng dụng quản lý thư viện hoàn chỉnh được phát triển bằng ngôn ngữ Java, kết hợp với giao diện JavaFX và cơ sở dữ liệu MySQL. Ứng dụng hỗ trợ các nghiệp vụ quản lý sách, độc giả và lịch sử mượn trả một cách trực quan và hiệu quả.

🚀 Tính Năng Chính
📖 Quản lý Kho Sách
Thêm sách mới: Đăng ký đầu sách mới vào hệ thống.

Cập nhật số lượng: Tự động nhận diện ID trùng để cộng dồn số lượng sách vào kho.

Xóa sách: Gỡ bỏ sách khỏi hệ thống (nếu không có người đang mượn).

Tìm kiếm thông minh: Tìm kiếm sách nhanh theo Mã sách hoặc Tên sách ngay khi đang gõ.

Nhập kho từ file: Nhập hàng loạt sách từ file .txt theo định dạng quy ước.

👥 Quản lý Độc Giả
Quản lý thông tin: Thêm, xóa và xem danh sách độc giả.

Hồ sơ chi tiết: Xem danh sách các cuốn sách độc giả đang mượn và toàn bộ lịch sử giao dịch trong quá khứ.

Tìm kiếm độc giả: Lọc danh sách độc giả theo tên hoặc mã số.

🔄 Nghiệp Vụ Mượn & Trả
Mượn sách: Tự động trừ số lượng trong kho và ghi nhận vào hồ sơ độc giả.

Trả sách: Hoàn lại số lượng sách vào kho và cập nhật trạng thái trả.

Lịch sử giao dịch: Ghi lại chi tiết thời gian, hành động (Mượn/Trả), ID và tên sách.

📄 Xuất Báo Cáo
In danh sách sách: Xuất toàn bộ kho sách ra file .txt định dạng bảng.

In chi tiết độc giả: Xuất biên nhận mượn/trả và lịch sử của từng cá nhân ra file .txt.

🛠 Công Nghệ Sử Dụng
Ngôn ngữ: Java 17+

Giao diện: JavaFX 21 (với FXML)

Cơ sở dữ liệu: MySQL 8.0

Thư viện kết nối: JDBC (MySQL Connector/J)

Kiến trúc: Model - View - Controller (MVC)