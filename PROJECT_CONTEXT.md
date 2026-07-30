# PROJECT CONTEXT

## 1. Kiến trúc (Architecture)
- **Mô hình kiến trúc**: MVC (Model - View - Controller).
- **Backend Framework**: Java Servlet (Jakarta EE 6.0), JSTL, JSP.
- **ORM / Database Access**: Hibernate Core 6.4 (JPA) và có sử dụng JDBC thuần ở một số module nhỏ gọn.
- **Frontend**: JSP kết hợp HTML, CSS, JavaScript thuần và có thể thư viện ngoài (như biểu đồ, barcode zxing).
- **Cơ chế đóng gói**: Maven (`war`), chạy trên web server như Tomcat (hoặc tương tự).
- **Công cụ hỗ trợ**: Lombok (giảm boilerplate code), Log4j (ghi log), Gson (JSON).

## 2. Package (Cấu trúc thư mục)
Dự án được chia module theo chức năng (Feature-based), với một số package chính nằm trong `src/main/java`:
- `BanHangTaiQuay`: Chứa mã nguồn cho chức năng bán hàng tại quầy (Controller/Servlet, Dao, Model, Service).
- `QuanLyHoaDon`: Chức năng quản lý hóa đơn, thanh toán và các báo cáo thống kê (controller, dao, Model, service).
- `QuanLyKhachHang`: Xử lý thông tin khách hàng và địa chỉ.
- `QuanLyNhanVien`: Quản lý danh sách, phân quyền và thông tin nhân viên.
- `QuanLySanPham`: Module lớn nhất bao gồm các thuộc tính của sản phẩm (Kích cỡ, màu sắc, chất liệu,...), giỏ hàng, thông tin khuyến mãi. Module này đồng thời chứa package `Entity` bao gồm tất cả các entity JPA cốt lõi của cả hệ thống như `SanPham`, `HoaDon`, `KhachHang`, `NhanVien`.
- `BE`: Các class tiện ích chung như kết nối DB `DatabaseConnectionManager`, `EntityManagerUtils`.

**Lưu ý:** Có sự không đồng nhất nhẹ về quy ước đặt tên (có chỗ viết hoa chữ cái đầu cho package như `Controller`, `Dao`, có chỗ dùng viết thường `controller`, `dao`).

## 3. Database (Cơ sở dữ liệu)
- **Hệ quản trị CSDL**: SQL Server.
- **JDBC Driver**: `com.microsoft.sqlserver.jdbc.SQLServerDriver`
- **Database Name**: `quan_ly_ban_kinh`
- **Chiến lược tạo bảng**: Sử dụng Hibernate hbm2ddl `update` để đồng bộ Entity vào Database, tích hợp kịch bản khởi tạo sẵn từ `create.sql`.

## 4. Chức năng (Features)
- **Bán hàng tại quầy (POS)**: Cho phép chọn khách hàng, chọn sản phẩm, thêm sản phẩm vào hóa đơn chờ, áp dụng mã giảm giá, thanh toán.
- **Quản lý Sản phẩm & Biến thể**: Hỗ trợ thêm, sửa, xóa, xem sản phẩm và chi tiết sản phẩm. Quản lý các thuộc tính sản phẩm kính mắt như: Gọng kính, Tròng kính, Chất liệu, Hình dáng gọng, Kiểu dáng, Kích cỡ, Màu sắc, Thương hiệu.
- **Quản lý Hóa đơn & Lịch sử**: Xem chi tiết hóa đơn, trạng thái hóa đơn, lịch sử thanh toán, lịch sử mua hàng.
- **Thống kê (Dashboard)**: Thống kê doanh thu, khách hàng, nhân viên bán hàng, sản phẩm bán chạy.
- **Quản lý Khách hàng**: Quản lý thông tin và địa chỉ khách hàng.
- **Quản lý Nhân viên**: Quản lý tài khoản, thông tin nhân viên bán hàng, ca làm việc.
- **Quản lý Giảm giá (Voucher / PhieuGiamGia)**: Các đợt giảm giá, mã khuyến mãi cá nhân và đại trà.
- **Authentication**: Login/Logout và phân quyền cơ bản.

## 5. Quy ước code (Coding Conventions)
- **Tầng DAO/Repository**: Các interface thường kết thúc bằng `Dao` hoặc `Repository`, class triển khai kết thúc bằng `DaoImpl` hoặc `ServiceImpl` (ví dụ `BanHangDAOImpl`).
- **Tầng Service**: Interface `Service` và class triển khai `ServiceImpl`.
- **Tầng Controller**: Dùng đuôi `Servlet` (ví dụ `ChatLieuServlet`, `SanPhamServlet`) hoặc `Controller` (như `HoaDonController`, `BanHangController`) để đón Request và trả về View / JSON.
- **DTO / Model truyền dữ liệu**: Sử dụng các hậu tố `Request` (như `ThanhToanRequest`, `HoaDonCreateRequest`) cho dữ liệu từ View gửi lên, và `View` (như `HoaDonView`, `ThongKeProduct`) cho dữ liệu đọc và hiển thị.
- **Entities**: Đặt trong package `QuanLySanPham.Entity`, ánh xạ (Map) trực tiếp tới các bảng trong DB thông qua JPA Annotation (`@Entity`, `@Table`, `@Column`, ...).

## 6. Luồng xử lý (Processing Flow)
- **Web request (JSP/AJAX/HTML)** gửi lên server, chạm vào các **Servlet / Controller**.
- Các Servlet này trích xuất tham số, đóng gói thành đối tượng Request (nếu cần), rồi gọi tới các phương thức của **Service**.
- **Service** thực thi các nghiệp vụ kinh doanh (business logic), tính toán, kiểm tra dữ liệu, sau đó gọi xuống **DAO / Repository**.
- **DAO** sử dụng `EntityManagerUtils` (JPA) hoặc `DatabaseConnectionManager` (JDBC thuần) tương tác với SQL Server và trả về Entity / Model.
- Server trả về **JSP View** thông qua `RequestDispatcher.forward` hoặc chuyển hướng `sendRedirect`, hoặc trả JSON thông qua thư viện `Gson` nếu là AJAX call.

## 7. Quan hệ giữa các class (Class relationships)
- Các Entity có cấu trúc quan hệ chặt chẽ xoay quanh nghiệp vụ bán kính:
  - **`SanPham` & Các thuộc tính**: `SanPhamChiTiet` liên kết Nhiều-Một tới `SanPham` và tới các bảng từ điển như `MauSac`, `KichCo`, `ChatLieu`, `TrongKinh`, `GongKinh`, `HinhDangGong`, `KieuDang`, `KieuQuaiKinh`, `ThuongHieu`.
  - **Giao dịch**: `HoaDon` chứa danh sách `ChiTietHoaDon` (Nhiều-Nhiều giữa Hóa đơn và Sản phẩm chi tiết). Mỗi `HoaDon` lại có tham chiếu tới người tạo (`NhanVien`) và người mua (`KhachHang`).
  - **Khuyến mãi**: `PhieuGiamGia` có thể áp dụng vào `HoaDon` hoặc gán riêng cho `KhachHang` thông qua bảng phụ `KhachHangPhieuGiamGia`.
  - **Thanh toán & Lịch sử**: `ThanhToanHoaDon` (hoặc `HinhThucThanhToan`), `LichSuHoaDon`, `LichSuThanhToan` liên kết với `HoaDon` để theo dõi vòng đời giao dịch.
