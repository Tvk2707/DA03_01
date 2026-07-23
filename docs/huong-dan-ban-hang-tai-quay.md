# Tài Liệu Hướng Dẫn Bán Hàng Tại Quầy

## 1. Mục đích

Tài liệu này hướng dẫn nhân viên thao tác trên màn hình **Bán hàng tại quầy (POS)**, từ lúc tạo hóa đơn, tìm sản phẩm, chọn khách hàng, áp voucher, thanh toán đến in hóa đơn.

Đường dẫn chức năng: `/ban-hang`

## 2. Nguyên tắc chung

- Khi mới vào trang, hệ thống **không hiển thị sản phẩm nào**.
- Nhân viên cần **tạo hóa đơn** hoặc chọn một hóa đơn chờ trước khi bán.
- Nếu nhân viên không chọn khách hàng, hóa đơn mặc định là **Khách lẻ**.
- Các thao tác chính dùng AJAX, không reload toàn trang trừ một số thao tác cần làm mới dữ liệu như đổi số lượng, xóa sản phẩm, áp voucher, hủy hóa đơn.
- Một nhân viên có tối đa **10 hóa đơn chờ**.

## 3. Tạo Và Chọn Hóa Đơn

### Tạo hóa đơn mới

1. Bấm nút **Tạo đơn hàng**.
2. Hệ thống tạo hóa đơn chờ thanh toán.
3. Sau khi tạo xong, trang tự chuyển sang hóa đơn vừa tạo.
4. Hóa đơn mới mặc định là **Khách lẻ**.

### Chọn hóa đơn chờ

1. Các hóa đơn chờ nằm ở dãy tab phía trên khu vực bán hàng.
2. Bấm vào tab hóa đơn để chuyển sang hóa đơn đó.
3. URL sẽ đổi theo hóa đơn đang chọn, ví dụ: `/ban-hang?id=27`.

### Hủy hóa đơn chờ

1. Bấm nút `x` trên tab hóa đơn.
2. Hệ thống mở popup xác nhận hủy.
3. Bấm **Xác nhận hủy hóa đơn** để hủy.
4. Nếu hóa đơn đã áp voucher, hệ thống hoàn lại lượt voucher khi hủy.

## 4. Tìm Và Thêm Sản Phẩm

### Tìm sản phẩm

1. Sau khi có hóa đơn đang chọn, nhập tên sản phẩm hoặc mã SKU vào ô tìm kiếm.
2. Hệ thống hiển thị các sản phẩm/biến thể phù hợp.
3. Mỗi card sản phẩm hiển thị:
   - Tên sản phẩm
   - Mã biến thể
   - Số lượng tồn
   - Màu sắc
   - Kích cỡ
   - Giá bán

Nếu chưa có hóa đơn hoặc ô tìm kiếm trống, danh sách sản phẩm sẽ để trống.

### Thêm sản phẩm vào giỏ

1. Bấm nút `+` trên card sản phẩm.
2. Hệ thống gọi API thêm sản phẩm bằng AJAX.
3. Đồng thời có hiệu ứng sản phẩm bay vào giỏ hàng.
4. Giỏ hàng cập nhật ngay sau khi thêm thành công.

Lưu ý:

- Nếu sản phẩm hết tồn, nút `+` bị vô hiệu hóa.
- Cùng một biến thể sản phẩm thì hệ thống cộng dồn số lượng.
- Khác biến thể, ví dụ khác màu hoặc kích cỡ, sẽ hiển thị thành dòng riêng trong giỏ hàng.

## 5. Quét QR Sản Phẩm

1. Bấm nút **Quét QR**.
2. Hệ thống mở popup quét QR.
3. Có thể quét bằng camera hoặc nhập mã QR thủ công.
4. Khi tìm thấy sản phẩm theo mã QR, hệ thống thêm sản phẩm vào hóa đơn đang chọn.

Điều kiện:

- Phải có hóa đơn đang chọn.
- Mã QR phải trùng với mã biến thể sản phẩm đang hoạt động.

## 6. Quản Lý Giỏ Hàng

Giỏ hàng nằm ở cột bên phải.

Mỗi dòng sản phẩm hiển thị:

- Ảnh hoặc icon sản phẩm
- Tên sản phẩm
- Mã biến thể
- Màu sắc
- Kích cỡ
- Số lượng
- Thành tiền

### Tăng số lượng

Bấm nút `+` trong dòng giỏ hàng.

### Giảm số lượng

Bấm nút `-` trong dòng giỏ hàng.

Nếu số lượng đang là 1 và bấm giảm, hệ thống sẽ xóa sản phẩm khỏi giỏ.

### Xóa sản phẩm

Bấm nút **Xóa** ở dòng sản phẩm.

## 7. Chọn Khách Hàng

### Mặc định Khách lẻ

Nếu nhân viên không chọn khách hàng, hóa đơn luôn là **Khách lẻ**.

### Tìm khách hàng thành viên

1. Bấm nút **Tìm khách** có icon kính lúp.
2. Nhập một trong các thông tin:
   - Số điện thoại
   - Mã khách hàng
   - Tên khách hàng
3. Hệ thống hiển thị danh sách khách phù hợp.
4. Bấm vào khách muốn chọn để gán vào hóa đơn.

### Thêm khách hàng mới

1. Bấm nút **Thêm mới**.
2. Hệ thống mở popup **Thêm khách hàng**.
3. Nhập thông tin:
   - Họ tên, bắt buộc
   - Email, không bắt buộc
   - Số điện thoại, bắt buộc
   - Ngày sinh, không bắt buộc
   - Giới tính, bắt buộc
4. Bấm **Thêm**.
5. Hệ thống lưu khách vào bảng `khach_hang`, sau đó tự gán khách đó vào hóa đơn hiện tại.

### Chuyển lại Khách lẻ

Nếu hóa đơn đã gán khách thành viên, bấm nút gỡ khách hàng để chuyển hóa đơn về **Khách lẻ**.

## 8. Voucher

### Áp dụng voucher

1. Nhập mã voucher vào ô voucher.
2. Bấm **Áp dụng**.
3. Nếu voucher hợp lệ, tổng tiền được cập nhật.

Điều kiện voucher được kiểm tra:

- Mã voucher tồn tại.
- Voucher đang hoạt động.
- Chưa hết hạn hoặc chưa tới ngày bắt đầu.
- Còn lượt sử dụng.
- Hóa đơn đạt giá trị tối thiểu.
- Hóa đơn chưa áp voucher khác.
- Với voucher cá nhân, hóa đơn phải gắn đúng khách hàng được cấp voucher.

## 9. Thanh Toán

Hiện màn hình POS hỗ trợ:

- **Tiền mặt**
- **Chuyển khoản**

Phương thức **Thẻ ngân hàng** đã được bỏ khỏi giao diện thanh toán.

### Thanh toán tiền mặt

1. Chọn **Tiền mặt**.
2. Bấm nút **Thanh toán**.
3. Hệ thống mở popup xác nhận đã nhận đủ tiền.
4. Bấm **Xác nhận thanh toán**.

### Thanh toán chuyển khoản

1. Chọn **Chuyển khoản**.
2. Bấm nút **Thanh toán**.
3. Hệ thống mở popup QR chuyển khoản.
4. Sau khi xác nhận đã nhận tiền, bấm **Xác nhận thanh toán**.

Sau thanh toán thành công:

- Hóa đơn chuyển sang trạng thái đã thanh toán.
- Tồn kho sản phẩm được trừ.
- Doanh thu ca làm việc được cộng.
- Hệ thống mở popup hỏi có in hóa đơn không.
- Chọn **In hóa đơn** để mở trang chi tiết/in hóa đơn.
- Chọn **Không in** để quay lại màn hình bán hàng.

## 10. Các Trạng Thái Và Ràng Buộc Nghiệp Vụ

- Hóa đơn mới tạo có trạng thái **Chờ thanh toán**.
- Không thể thêm sản phẩm vào hóa đơn đã thanh toán hoặc đã hủy.
- Không thể thanh toán hóa đơn trống.
- Không thể thanh toán nếu sản phẩm trong hóa đơn đã ngừng kinh doanh.
- Không thể thanh toán nếu tồn kho hiện tại không đủ.
- Không thể hủy hóa đơn đã thanh toán.
- Hủy hóa đơn chờ sẽ chuyển trạng thái hóa đơn sang đã hủy.

## 11. Ghi Chú Kỹ Thuật

Các file chính:

- `src/main/webapp/Admin/BanHangTaiQuay/ban-hang.jsp`: giao diện POS.
- `src/main/webapp/Admin/BanHangTaiQuay/_product-grid.jsp`: danh sách sản phẩm trả về khi tìm kiếm.
- `src/main/webapp/assets/js/banhang.js`: xử lý AJAX và tương tác giao diện.
- `src/main/java/BanHangTaiQuay/Controller/BanHangController.java`: controller chính của POS.
- `src/main/java/BanHangTaiQuay/Service/BanHangServiceImpl.java`: xử lý nghiệp vụ hóa đơn, giỏ hàng, thanh toán.
- `src/main/java/BanHangTaiQuay/Service/VoucherServiceImpl.java`: xử lý voucher.

Các route chính:

- `GET /ban-hang`: mở màn hình POS.
- `POST /ban-hang/tao-hoa-don`: tạo hóa đơn chờ.
- `GET /ban-hang/tim-san-pham`: tìm sản phẩm.
- `GET /ban-hang/quet-qr`: tìm sản phẩm theo mã QR.
- `POST /ban-hang/them-san-pham`: thêm sản phẩm vào giỏ.
- `POST /ban-hang/xoa-san-pham`: xóa sản phẩm khỏi giỏ.
- `POST /ban-hang/cap-nhat-so-luong`: cập nhật số lượng.
- `GET /ban-hang/tra-cuu-khach-hang`: tìm khách hàng.
- `POST /ban-hang/tra-cuu-khach-hang`: tìm hoặc tạo khách hàng mới.
- `POST /ban-hang/gan-khach-hang`: gán khách hàng vào hóa đơn.
- `POST /ban-hang/chon-khach-le`: chuyển hóa đơn về Khách lẻ.
- `POST /ban-hang/ap-voucher`: áp dụng voucher.
- `POST /ban-hang/huy-hoa-don`: hủy hóa đơn chờ.
- `POST /thanh-toan/thanh-toan`: xác nhận thanh toán.

## 12. Quy Trình Bán Hàng Chuẩn

1. Vào màn hình **Bán hàng tại quầy**.
2. Bấm **Tạo đơn hàng**.
3. Tìm sản phẩm theo tên hoặc mã SKU.
4. Bấm `+` để thêm sản phẩm vào giỏ.
5. Điều chỉnh số lượng hoặc xóa sản phẩm nếu cần.
6. Nếu là khách thành viên, tìm hoặc thêm mới khách hàng.
7. Nếu không chọn khách, để mặc định **Khách lẻ**.
8. Nhập voucher nếu có.
9. Chọn **Tiền mặt** hoặc **Chuyển khoản**.
10. Bấm **Thanh toán**.
11. Xác nhận thanh toán.
12. In hóa đơn hoặc quay lại bán hàng.
