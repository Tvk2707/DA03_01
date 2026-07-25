# Tổng hợp validate trong project

Tài liệu này tổng hợp các rule validate hiện đang có trong code sau khi rà các module Java/JSP/JS của project. Project hiện chưa dùng annotation Bean Validation kiểu `@NotBlank`, `@Size`, `@Email`; validation chủ yếu nằm trong servlet, service, repository và một số file JavaScript phía giao diện.

## 1. Helper validate dùng chung

Nguồn chính: `src/main/java/QuanLySanPham/Utils/ValidationUtils.java`

| Helper | Rule |
| --- | --- |
| `normalizeString(input)` | Trim, gom nhiều khoảng trắng thành 1 khoảng trắng, null/blank thành chuỗi rỗng. |
| `validateTen(errors, fieldName, ten)` | Bắt buộc nhập; sau normalize phải dài 2-100 ký tự; không chứa ký tự ngoài chữ/số/khoảng trắng/dấu `-`/`/`; không được toàn số. |
| `validateMa(errors, fieldName, ma)` | Bắt buộc nhập; tối đa 50 ký tự; chỉ chứa chữ, số, `-`, `_`. |
| `validateTenSanPham(errors, fieldName, ten)` | Bắt buộc nhập; sau normalize phải dài 2-200 ký tự. |
| `checkNull(errors, fieldName, value, message)` | Bắt buộc chọn/không được null. |

## 2. Quản lý sản phẩm

Nguồn chính: `src/main/java/QuanLySanPham/service/impl/SanPhamServiceImpl.java`, `SanPhamServlet.java`, `SanPhamAdd.jsp`

### Sản phẩm

| Trường | Rule backend |
| --- | --- |
| `maSanPham` | Dùng `validateMa`: bắt buộc, tối đa 50 ký tự, chỉ chữ/số/`-`/`_`, trim trước khi lưu. |
| `tenSanPham` | Dùng `validateTenSanPham`: bắt buộc, 2-200 ký tự, normalize khoảng trắng, không được trùng tên sản phẩm khác. |
| `danhMuc` | Bắt buộc chọn. |
| `thuongHieu` | Bắt buộc chọn. |
| `chatLieu` | Bắt buộc chọn. |
| `kieuDang` | Bắt buộc chọn. |
| `trongKinh` | Bắt buộc chọn. |
| `id` khi cập nhật/xóa/tìm | Không được null, cập nhật yêu cầu `sanPham.id` hợp lệ. |
| Phân trang | `pageNumber` và `pageSize` phải >= 1. |

### Biến thể sản phẩm

Nguồn chính: `src/main/java/QuanLySanPham/service/impl/SanPhamChiTietServiceImpl.java`

| Trường | Rule backend |
| --- | --- |
| Danh sách biến thể | Khi thêm hàng loạt không được null/rỗng. |
| `id` | Khi tìm/xóa/cập nhật phải tồn tại; cập nhật bắt buộc có ID. |
| `sanPham.id` | Bắt buộc có sản phẩm cha. |
| `mauSac.id` | Bắt buộc chọn màu sắc. |
| `kichCo.id` | Bắt buộc chọn kích cỡ. |
| Tổ hợp màu + kích cỡ | Không được trùng trong cùng request; không được trùng biến thể đã có trong DB. |
| `giaNhap` | Nếu có thì không được âm. |
| `giaBan` | Nếu có thì không được âm. |
| `soLuongTon` | Nếu null khi thêm sẽ mặc định 0; nếu có thì không được âm. |
| `trongLuong` | Nếu có thì không được âm. |
| Cập nhật tồn kho | `sanPhamChiTietId` và `soLuongThayDoi` không được null; tồn kho sau thay đổi không được âm. |
| Tìm theo ID | ID phải > 0; biến thể đã soft delete trả về null. |

### Validate phía giao diện sản phẩm

Nguồn chính: `src/main/webapp/Admin/QuanLySanPham/SanPhamAdd.jsp`, `QuanLySanPhamChiTiet.jsp`

- Form sản phẩm đánh dấu `required` cho mã, tên, danh mục, thương hiệu, chất liệu, kiểu dáng, gọng kính, tròng kính.
- Thêm sản phẩm yêu cầu chọn ít nhất 1 màu và 1 kích cỡ trước khi sinh biến thể.
- Khi lưu sản phẩm phải có ít nhất 1 biến thể.
- Các input biến thể phía giao diện có `required` và `min="0"` cho tồn kho/giá/trọng lượng.
- Lọc giá sản phẩm có kiểm tra `giaTu`/`giaDen` ở JS để tránh khoảng giá đảo ngược.

## 3. Danh mục và thuộc tính sản phẩm

Nguồn chính: `src/main/java/QuanLySanPham/service/impl/LookupServiceImpl.java`

Các nhóm dùng chung `validateTen`: danh mục, thương hiệu, chất liệu, kiểu dáng, màu sắc, kích cỡ, tròng kính, hình dáng gọng, kiểu quai kính.

| Nhóm | Field chính | Rule |
| --- | --- | --- |
| Danh mục | `tenDanhMuc` | Bắt buộc, 2-100 ký tự, không ký tự đặc biệt, không toàn số, normalize, không trùng tên. Cập nhật yêu cầu ID hợp lệ. |
| Thương hiệu | `tenThuongHieu` | Như trên, không trùng tên. Cập nhật yêu cầu ID hợp lệ. |
| Chất liệu | `tenChatLieu` | Như trên, không trùng tên. Cập nhật yêu cầu ID hợp lệ. |
| Kiểu dáng | `tenKieuDang` | Như trên, không trùng tên. Cập nhật yêu cầu ID hợp lệ. |
| Màu sắc | `tenMau` | Như trên, không trùng tên. Cập nhật yêu cầu ID hợp lệ. |
| Kích cỡ | `tenKichCo` | Như trên, không trùng tên. Cập nhật yêu cầu ID hợp lệ. |
| Tròng kính | `loaiTrong` | Như trên, không trùng tên. Cập nhật yêu cầu ID hợp lệ. |
| Gọng kính | `hinhDangGong`, `kieuQuaiKinh` | Bắt buộc chọn cả hình dáng gọng và kiểu quai kính. Cập nhật yêu cầu ID hợp lệ. |
| Hình dáng gọng | `hinhDang` | Dùng `validateTen`, không trùng tên. Cập nhật yêu cầu ID hợp lệ. |
| Kiểu quai kính | `kieuQuai` | Dùng `validateTen`. Cập nhật yêu cầu ID hợp lệ. |

Lưu ý: một số servlet lookup vẫn đọc thêm field mã/trạng thái từ request, nhưng rule bắt buộc chính đang nằm trong service theo các field ở bảng trên.

## 4. Phiếu giảm giá / voucher

Nguồn chính: `src/main/java/QuanLySanPham/controller/PhieuGiamGiaServlet.java`, `src/main/java/BanHangTaiQuay/Service/VoucherServiceImpl.java`

### Khi tạo/cập nhật phiếu giảm giá

| Trường | Rule |
| --- | --- |
| `id` khi cập nhật | Bắt buộc có ID phiếu cần cập nhật. |
| `maVoucher` | Tạo mới bắt buộc, tối đa 50 ký tự, không được trùng. Khi edit có nhánh bỏ lỗi mã nếu mã rỗng. |
| `tenVoucher` | Bắt buộc, tối đa 250 ký tự. |
| `soLuong` | Bắt buộc là số nguyên > 0. |
| `ngayBatDau` | Bắt buộc, parse được theo định dạng `yyyy-MM-dd`. |
| `ngayKetThuc` | Bắt buộc, parse được theo định dạng `yyyy-MM-dd`, không được trước ngày bắt đầu. |
| `loaiGiamGia` | Chỉ nhận `percent` hoặc `amount`. |
| `giaTriGiam` | Với giảm tiền: parse được tiền hợp lệ và không âm. Với giảm phần trăm: phải > 0 và <= 100. |
| `giamToiDa` | Không bắt buộc; nếu nhập thì phải là tiền hợp lệ và không âm. Với giảm tiền, backend set về 0. |
| `donToiThieu` | Không bắt buộc; nếu nhập thì phải là tiền hợp lệ và không âm. |
| `loaiPhieu` | Chỉ nhận `public` hoặc `personal`. |
| `trangThai` | Chỉ nhận `1` hoặc `0`. |

### Khi áp dụng/gỡ voucher tại quầy

| Nghiệp vụ | Rule |
| --- | --- |
| Áp voucher | `idHoaDon` > 0, `maVoucher` không rỗng, hóa đơn tồn tại và đang chờ thanh toán, hóa đơn đã có sản phẩm, hóa đơn chưa áp voucher. |
| Trạng thái voucher | Voucher tồn tại, đang hoạt động, đã đến ngày bắt đầu, chưa hết hạn, còn lượt sử dụng. |
| Điều kiện đơn hàng | Tổng tiền hàng phải đạt `donToiThieu`; voucher phải có loại giảm và giá trị giảm hợp lệ. |
| Voucher cá nhân | Hóa đơn phải gắn khách hàng; voucher phải thuộc khách hàng đó và chưa dùng. |
| Gỡ voucher | `idHoaDon` > 0, hóa đơn tồn tại và đang chờ thanh toán, hóa đơn đã áp voucher. |

## 5. Bán hàng tại quầy

Nguồn chính: `src/main/java/BanHangTaiQuay/Controller/BanHangController.java`, `src/main/java/BanHangTaiQuay/Service/BanHangServiceImpl.java`, `src/main/java/BanHangTaiQuay/Service/CaLamViecServiceImpl.java`

### Helper request

| Helper | Rule |
| --- | --- |
| `requireText(req, name)` | Parameter bắt buộc có và không rỗng sau trim. |
| `requirePositiveInt(req, name)` | Parameter bắt buộc là số nguyên > 0. |
| `parseOptionalPositiveInt(value)` | Rỗng/sai số/<=0 trả `null`. |
| `parseOptionalInteger(value)` | Rỗng/sai số trả `null`. |
| `parseOptionalDate(value)` | Rỗng trả `null`; nếu có thì phải parse được `LocalDate`. |

### Hóa đơn tạm và giỏ hàng

| Luồng | Rule |
| --- | --- |
| Tạo hóa đơn | `idNhanVien` và `idCa` phải > 0; nhân viên tồn tại khi tạo ca; mỗi nhân viên tối đa 10 hóa đơn chờ. |
| Tìm sản phẩm | Nếu không có keyword và không chọn danh mục thì trả danh sách rỗng; chỉ tìm biến thể trạng thái 1. |
| Quét QR | `ma` bắt buộc; phải tìm đúng mã biến thể đang hoạt động. |
| Thêm sản phẩm | `idHoaDon`, `idSanPhamChiTiet`, `soLuong` bắt buộc > 0. |
| Điều kiện thêm sản phẩm | Sản phẩm/biến thể tồn tại, chưa bị xóa, đang kinh doanh; sản phẩm cha đang kinh doanh; hóa đơn tồn tại và đang chờ thanh toán; số lượng trong giỏ + số lượng thêm không vượt tồn kho; sản phẩm phải có giá bán. |
| Xóa sản phẩm khỏi giỏ | `idHoaDon`, `idChiTiet` > 0; hóa đơn tồn tại và đang chờ thanh toán; chi tiết thuộc hóa đơn. |
| Cập nhật số lượng | `idChiTiet`, `soLuongMoi` > 0; chi tiết và hóa đơn tồn tại; hóa đơn đang chờ thanh toán; không vượt tồn kho; chi tiết phải có đơn giá. |
| Chọn khách thành viên | `idHoaDon`, `idKhachHang` > 0; hóa đơn đang chờ thanh toán; khách hàng tồn tại. |
| Chọn khách lẻ | `idHoaDon` > 0; hóa đơn đang chờ thanh toán; set khách hàng của hóa đơn về null. |
| Hủy hóa đơn | Hóa đơn tồn tại; không được hủy nếu đã hủy hoặc đã thanh toán; lý do rỗng được thay bằng “Không nêu lý do”. |
| Lấy hóa đơn theo ID | `idHoaDon` > 0. |

### Thanh toán tại quầy

| Trường/điều kiện | Rule |
| --- | --- |
| `idHoaDon` | Bắt buộc > 0. |
| `maPttt` | Bắt buộc; `TM` được chuẩn hóa thành `PTTT001`; phương thức phải tồn tại và đang hoạt động. |
| Trạng thái hóa đơn | Hóa đơn phải tồn tại; không được thanh toán nếu đã thanh toán hoặc đã hủy. |
| Giỏ hàng | Hóa đơn phải có sản phẩm; từng dòng có số lượng > 0 và liên kết biến thể. |
| Sản phẩm khi thanh toán | Biến thể và sản phẩm cha phải tồn tại, chưa xóa, đang hoạt động; tồn kho đủ để trừ. |
| Voucher khi thanh toán | Nếu có voucher thì kiểm tra lại trạng thái, hạn, lượt dùng, điều kiện đơn hàng và quyền khách hàng. |
| `maGiaoDich` | Không bắt buộc; nếu nhập thì trim, không được trùng mã giao dịch đã có. |
| `ghiChu` | Không bắt buộc; nếu rỗng backend tự sinh ghi chú theo phương thức thanh toán và tổng tiền. |

## 6. Khách hàng

Nguồn chính: `src/main/java/BanHangTaiQuay/Service/KhachHangServiceImpl.java`, `src/main/java/QuanLyKhachHang/servlet/KhachHangServlet.java`, `src/main/webapp/Admin/js/customer-management.js`

### Luồng POS chọn/tạo khách

| Trường | Rule backend |
| --- | --- |
| Từ khóa tìm khách | Không được rỗng; tìm theo số điện thoại, mã khách hàng hoặc họ tên; kết quả tối đa 8 khách đang hoạt động. |
| Tra cứu 1 khách | Nếu có nhiều kết quả phù hợp thì yêu cầu nhập chính xác số điện thoại hoặc mã khách hàng. |
| `soDienThoai` khi tạo | Bắt buộc không rỗng; nếu đã tồn tại thì trả khách cũ. |
| `hoTen` khi tạo mới | Bắt buộc không rỗng nếu số điện thoại chưa tồn tại. |
| `email` | Không bắt buộc, trim, rỗng lưu null. |
| `ngaySinh` | Không bắt buộc; nếu gửi lên thì phải parse được `LocalDate`. |
| `gioiTinh` | Không bắt buộc ở backend POS; giao diện modal đang yêu cầu chọn. |
| `idKhachHang` | Khi gắn vào hóa đơn phải > 0 và khách hàng tồn tại. |

### Quản lý khách hàng

| Nơi validate | Rule |
| --- | --- |
| `customer-management.js` | `hoTen` bắt buộc, >= 2 ký tự, không toàn số, <= 250 ký tự. |
| `customer-management.js` | `email` không bắt buộc, nhưng nếu nhập phải <= 150 ký tự và đúng định dạng email cơ bản. |
| `customer-management.js` | `soDienThoai` bắt buộc, 10 chữ số và bắt đầu bằng 0. |
| `customer-management.js` | `ngaySinh` không bắt buộc, nếu nhập phải đúng date, không lớn hơn hôm nay, tuổi không quá 120. |
| `customer-management.js` | `gioiTinh` phải là `0` hoặc `1`. |
| `KhachHangServlet` | Hiện chủ yếu parse `ngaySinh`, `gioiTinh`, set trạng thái 1 rồi gọi repository; chưa có validate backend chặt tương đương JS. |
| `KhachHangRepository.add` | Object khách hàng không được null; tự sinh `maKhachHang` dạng `KHxxx` nếu chưa có. |

### Địa chỉ khách hàng

Nguồn chính: `src/main/java/QuanLyKhachHang/servlet/DiaChiKhachHangServlet.java`, `dia_chi_khach_hang.jsp`

- JSP đánh dấu `required` cho `tenNguoiNhan`, `sdtNguoiNhan`, `tinhThanh`, `quanHuyen`, `phuongXa`, `diaChiCuThe`.
- Servlet parse `idKhachHang`, `loaiDiaChi`, `isMacDinh`, `idDiaChi`; hiện chưa có validate backend chặt cho rỗng/định dạng số điện thoại.
- Repository gắn địa chỉ với khách hàng theo `idKhachHang`; nếu không tìm thấy khách hàng thì logic hiện tại vẫn persist entity với khách null nếu DB cho phép, nên phần này nên được bổ sung validate khi cần siết dữ liệu.

## 7. Nhân viên, đăng nhập, đăng ký

Nguồn chính: `src/main/java/QuanLyNhanVien/service/impl/NhanVienServiceImpl.java`, `NhanVienServlet.java`, `RegisterServlet.java`, `LoginServlet.java`

| Luồng/trường | Rule |
| --- | --- |
| Thêm nhân viên | Object nhân viên không được null; `hoTen` bắt buộc không rỗng; nếu có `maNhanVien` thì không được trùng. |
| Cập nhật nhân viên | Object và `id` phải hợp lệ; `hoTen` bắt buộc không rỗng. |
| Xóa nhân viên | `id` không được null. |
| Đăng nhập | Tài khoản và mật khẩu bắt buộc; tài khoản tìm theo email hoặc mã nhân viên; tài khoản phải tồn tại, chưa bị vô hiệu hóa; mật khẩu phải đúng. |
| Đăng ký | `hoTen`, `maNhanVien`, `email` bắt buộc; mật khẩu tối thiểu 6 ký tự; xác nhận mật khẩu phải khớp. |
| Form nhân viên | JSP đánh dấu `required` cho mã nhân viên và họ tên; servlet parse ngày sinh, giới tính, trạng thái, nhưng chưa validate định dạng email/số điện thoại ở service. |

## 8. Quản lý hóa đơn và thống kê

Nguồn chính: `src/main/java/QuanLyHoaDon/controller/*`, `src/main/java/QuanLyHoaDon/service/*`

| Luồng | Rule hiện có |
| --- | --- |
| Danh sách hóa đơn | Không có validate input đáng kể. |
| Chi tiết hóa đơn | `id` đọc từ query string; parse lỗi thì mặc định 0; nếu không tìm thấy hóa đơn thì redirect về danh sách. |
| Đổi trạng thái hóa đơn | `id` parse lỗi mặc định 0; `trangThai` parse lỗi mặc định 1; `ghiChu` lấy nguyên text rồi gọi service/DAO. |
| Thống kê | Không nhận filter từ request trong controller hiện tại; chỉ bắt lỗi SQL và set list rỗng khi lỗi. |

## 9. Những điểm nên lưu ý khi test validate

- Validation backend không đồng đều giữa các module. Sản phẩm, voucher và POS đang chặt hơn; quản lý khách hàng/địa chỉ/nhân viên còn nhiều rule chỉ nằm ở giao diện hoặc parse tối thiểu.
- Một số thông báo lỗi trong source đang bị lỗi encoding khi xem bằng terminal, nhưng rule logic vẫn hoạt động.
- Các validate dạng `required` trong JSP chỉ hỗ trợ trình duyệt, không thay thế validate backend.
- Nếu cần siết dữ liệu database, nên ưu tiên bổ sung service validation cho khách hàng, địa chỉ khách hàng, nhân viên trước vì hiện các module này dễ nhận dữ liệu sai nếu request không đi qua form chuẩn.
