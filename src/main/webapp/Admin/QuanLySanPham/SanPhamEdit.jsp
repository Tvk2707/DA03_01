<%
    request.setAttribute("pageTitle", "Cập nhật sản phẩm ");
%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cập nhật sản phẩm - ${sanPham.tenSanPham}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/danhmuc.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/sanpham.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<%@include file="../layout/sidebar.jsp" %>
<div class="dashboard-container">
    <%@include file="../layout/header.jsp" %>

    <div class="category-section">
        <div class="category-header">
            <h2 class="category-title">
                <i class="fas fa-edit"></i> Cập nhật sản phẩm
            </h2>
            <a href="${pageContext.request.contextPath}/SanPham" class="btn-reset" style="text-decoration:none;">
                <i class="fas fa-arrow-left"></i> Quay lại danh sách
            </a>
        </div>

        <!-- Thông báo lỗi -->
        <c:if test="${not empty error}">
            <div style="background:#fdecea;color:#b3261e;padding:12px 16px;border-radius:8px;margin-bottom:16px;">
                <i class="fas fa-circle-exclamation"></i> ${error}
            </div>
        </c:if>

        <!-- Thông báo thành công -->
        <c:if test="${not empty success}">
            <div style="background:#e8f5e9;color:#2e7d32;padding:12px 16px;border-radius:8px;margin-bottom:16px;">
                <i class="fas fa-check-circle"></i> ${success}
            </div>
        </c:if>

        <c:choose>
            <c:when test="${empty sanPham}">
                <!-- Trường hợp không tìm thấy sản phẩm -->
                <div style="background:#fdecea;color:#b3261e;padding:24px;border-radius:8px;text-align:center;">
                    <i class="fas fa-exclamation-triangle" style="font-size:32px;margin-bottom:12px;"></i>
                    <h3>Không tìm thấy sản phẩm</h3>
                    <p>Sản phẩm bạn đang tìm kiếm không tồn tại hoặc đã bị xóa.</p>
                    <a href="${pageContext.request.contextPath}/SanPham" class="add-new-btn" style="display:inline-block;margin-top:16px;text-decoration:none;">
                        <i class="fas fa-arrow-left"></i> Quay lại danh sách
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <!-- Form cập nhật sản phẩm -->
                <form action="${pageContext.request.contextPath}/SanPham/update"
                      method="post" enctype="multipart/form-data" id="sanPhamForm" novalidate>

                    <!-- Hidden field cho ID sản phẩm -->
                    <input type="hidden" name="id" value="${sanPham.id}">

                    <!-- Hidden field cho ID biến thể (quan trọng để update) -->
                    <c:if test="${not empty sanPhamChiTiet}">
                        <input type="hidden" name="sanPhamChiTietId" value="${sanPhamChiTiet.id}">
                    </c:if>

                    <div class="sp-form-columns">

                        <!-- CARD: THÔNG TIN SẢN PHẨM -->
                        <div class="filter-section">
                            <div class="filter-header">
                                <div class="filter-title">
                                    <i class="fas fa-box"></i> Thông tin sản phẩm
                                </div>
                            </div>

                            <div class="filter-group" style="margin-bottom:16px;">
                                <label class="filter-label">
                                    Mã sản phẩm <span class="required">*</span>
                                </label>
                                <input type="text" class="filter-input" name="maSanPham"
                                       value="${sanPham.maSanPham}" readonly required>
                                <small class="field-hint">Mã sản phẩm không thể thay đổi</small>
                            </div>

                            <div class="filter-group" style="margin-bottom:16px;">
                                <label class="filter-label">
                                    Tên sản phẩm <span class="required">*</span>
                                </label>
                                <input type="text" class="filter-input" name="tenSanPham"
                                       value="${sanPham.tenSanPham}" required
                                       maxlength="200" placeholder="Nhập tên sản phẩm">
                            </div>

                            <div class="filter-grid" style="grid-template-columns:1fr 1fr;margin-bottom:0;">
                                <div class="filter-group">
                                    <label class="filter-label">
                                        Danh mục <span class="required">*</span>
                                    </label>
                                    <select class="filter-select" name="danhMucId" required>
                                        <option value="">-- Chọn danh mục --</option>
                                        <c:forEach var="dm" items="${danhMucList}">
                                            <option value="${dm.id}"
                                                ${dm.id == sanPham.danhMuc.id ? 'selected' : ''}>
                                                    ${dm.tenDanhMuc}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="filter-group">
                                    <label class="filter-label">
                                        Thương hiệu <span class="required">*</span>
                                    </label>
                                    <select class="filter-select" name="thuongHieuId" required>
                                        <option value="">-- Chọn thương hiệu --</option>
                                        <c:forEach var="th" items="${thuongHieuList}">
                                            <option value="${th.id}"
                                                ${th.id == sanPham.thuongHieu.id ? 'selected' : ''}>
                                                    ${th.tenThuongHieu}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>

                            <div class="filter-grid" style="grid-template-columns:1fr 1fr;margin-bottom:0;margin-top:16px;">
                                <div class="filter-group">
                                    <label class="filter-label">
                                        Chất liệu <span class="required">*</span>
                                    </label>
                                    <select class="filter-select" name="chatLieuId" required>
                                        <option value="">-- Chọn chất liệu --</option>
                                        <c:forEach var="cl" items="${chatLieuList}">
                                            <option value="${cl.id}"
                                                ${cl.id == sanPham.chatLieu.id ? 'selected' : ''}>
                                                    ${cl.tenChatLieu}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="filter-group">
                                    <label class="filter-label">
                                        Kiểu dáng <span class="required">*</span>
                                    </label>
                                    <select class="filter-select" name="kieuDangId" required>
                                        <option value="">-- Chọn kiểu dáng --</option>
                                        <c:forEach var="kd" items="${kieuDangList}">
                                            <option value="${kd.id}"
                                                ${kd.id == sanPham.kieuDang.id ? 'selected' : ''}>
                                                    ${kd.tenKieuDang}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>

                            <div class="filter-grid" style="grid-template-columns:1fr 1fr;margin-bottom:0;margin-top:16px;">
                                <div class="filter-group">
                                    <label class="filter-label">
                                        Gọng kính <span class="required">*</span>
                                    </label>
                                    <select class="filter-select" name="gongKinhId" required>
                                        <option value="">-- Chọn gọng kính --</option>
                                        <c:forEach var="gk" items="${gongKinhList}">
                                            <option value="${gk.id}"
                                                ${gk.id == sanPham.gongKinh.id ? 'selected' : ''}>
                                                    ${gk.hinhDangGong.hinhDang} - ${gk.kieuQuaiKinh.kieuQuai}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="filter-group">
                                    <label class="filter-label">
                                        Tròng kính <span class="required">*</span>
                                    </label>
                                    <select class="filter-select" name="trongKinhId" required>
                                        <option value="">-- Chọn tròng kính --</option>
                                        <c:forEach var="tk" items="${trongKinhList}">
                                            <option value="${tk.id}"
                                                ${tk.id == sanPham.trongKinh.id ? 'selected' : ''}>
                                                    ${tk.loaiTrong}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>

                            <div class="filter-group" style="margin-top:16px;">
                                <label class="filter-label">Mô tả chi tiết</label>
                                <textarea class="filter-input" name="moTaChiTiet" rows="3"
                                          maxlength="1000"
                                          placeholder="Nhập mô tả chi tiết về sản phẩm...">${sanPham.moTaChiTiet}</textarea>
                            </div>

                            <div class="filter-group" style="margin-top:16px;">
                                <label class="filter-label">Trạng thái sản phẩm</label>
                                <select class="filter-select" name="trangThai">
                                    <option value="1" ${sanPham.trangThai == 1 ? 'selected' : ''}>
                                        <i class="fas fa-check-circle"></i> Hoạt động
                                    </option>
                                    <option value="0" ${sanPham.trangThai == 0 ? 'selected' : ''}>
                                        <i class="fas fa-ban"></i> Ngừng bán
                                    </option>
                                </select>
                            </div>
                        </div>

                        <!-- CARD: BIẾN THỂ & HÌNH ẢNH -->
                        <div class="filter-section">
                            <div class="filter-header">
                                <div class="filter-title">
                                    <i class="fas fa-swatchbook"></i> Biến thể & Hình ảnh
                                </div>
                            </div>

                            <!-- Cảnh báo nếu không có biến thể -->
                            <c:if test="${empty sanPhamChiTiet}">
                                <div style="background:#fff3cd;color:#856404;padding:12px 16px;border-radius:8px;margin-bottom:16px;border-left:4px solid #ffc107;">
                                    <i class="fas fa-exclamation-triangle"></i>
                                    <strong>Sản phẩm chưa có biến thể!</strong>
                                    <p style="margin:8px 0 0 0;font-size:14px;">
                                        Vui lòng thêm thông tin biến thể bên dưới để sản phẩm có thể bán được.
                                    </p>
                                </div>
                            </c:if>

                            <div class="filter-group" style="margin-bottom:16px;">
                                <label class="filter-label">
                                    Mã biến thể <span class="required">*</span>
                                </label>
                                <input type="text" class="filter-input" name="ma"
                                       value="${sanPhamChiTiet.ma}" required
                                       placeholder="VD: SP001-DEN-M">
                            </div>

                            <div class="filter-grid" style="grid-template-columns:1fr 1fr;margin-bottom:0;">
                                <div class="filter-group">
                                    <label class="filter-label">
                                        Màu sắc <span class="required">*</span>
                                    </label>
                                    <select class="filter-select" name="mauSacId" required>
                                        <option value="">-- Chọn màu --</option>
                                        <c:forEach var="ms" items="${mauSacList}">
                                            <option value="${ms.id}"
                                                ${ms.id == sanPhamChiTiet.mauSac.id ? 'selected' : ''}>
                                                    ${ms.tenMau}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="filter-group">
                                    <label class="filter-label">
                                        Kích cỡ <span class="required">*</span>
                                    </label>
                                    <select class="filter-select" name="kichCoId" required>
                                        <option value="">-- Chọn kích cỡ --</option>
                                        <c:forEach var="kc" items="${kichCoList}">
                                            <option value="${kc.id}"
                                                ${kc.id == sanPhamChiTiet.kichCo.id ? 'selected' : ''}>
                                                    ${kc.tenKichCo}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>

                            <div class="filter-grid" style="grid-template-columns:1fr 1fr;margin-bottom:0;margin-top:16px;">
                                <div class="filter-group">
                                    <label class="filter-label">
                                        Giá nhập (VNĐ) <span class="required">*</span>
                                    </label>
                                    <input type="number" class="filter-input" name="giaNhap"
                                           value="${sanPhamChiTiet.giaNhap}" min="0" step="1000" required
                                           placeholder="0">
                                </div>
                                <div class="filter-group">
                                    <label class="filter-label">
                                        Giá bán (VNĐ) <span class="required">*</span>
                                    </label>
                                    <input type="number" class="filter-input" name="giaBan"
                                           value="${sanPhamChiTiet.giaBan}" min="0" step="1000" required
                                           placeholder="0">
                                </div>
                            </div>

                            <div class="filter-grid" style="grid-template-columns:1fr 1fr;margin-bottom:0;margin-top:16px;">
                                <div class="filter-group">
                                    <label class="filter-label">
                                        Số lượng tồn <span class="required">*</span>
                                    </label>
                                    <input type="number" class="filter-input" name="soLuongTon"
                                           value="${sanPhamChiTiet.soLuongTon}" min="0" required
                                           placeholder="0">
                                </div>
                                <div class="filter-group">
                                    <label class="filter-label">
                                        Trọng lượng (gram) <span class="required">*</span>
                                    </label>
                                    <input type="number" class="filter-input" name="trongLuong"
                                           value="${sanPhamChiTiet.trongLuong}" min="0" required
                                           placeholder="0">
                                </div>
                            </div>

                            <!-- Phần upload ảnh -->
                            <div class="filter-group" style="margin-top:16px;">
                                <label class="filter-label">Hình ảnh sản phẩm</label>
                                <input type="file" class="filter-input" id="fileAnh" name="fileAnh"
                                       accept="image/*" onchange="spPreviewImage(event)">
                                <input type="hidden" id="hinhAnhHidden" name="hinhAnhCu"
                                       value="${sanPhamChiTiet.hinhAnh}">

                                <small class="field-hint">
                                    <i class="fas fa-info-circle"></i>
                                    Định dạng: JPG, PNG, GIF. Tối đa 10MB.
                                </small>

                                <div class="sp-image-preview-box">
                                    <img id="spImagePreview"
                                         src="${not empty sanPhamChiTiet.hinhAnh ? pageContext.request.contextPath.concat('/File_Anh/images/').concat(sanPhamChiTiet.hinhAnh) : ''}"
                                         class="sp-image-preview"
                                         style="${not empty sanPhamChiTiet.hinhAnh ? 'display:block;' : 'display:none;'}">
                                    <span id="spImagePreviewLabel" class="sp-image-preview-label"
                                          style="${not empty sanPhamChiTiet.hinhAnh ? 'display:none;' : 'display:block;'}">
                                        <i class="fas fa-image"></i><br>Xem trước ảnh tại đây
                                    </span>
                                </div>

                                <!-- Nút xóa ảnh (chỉ hiện khi có ảnh) -->
                                <c:if test="${not empty sanPhamChiTiet.hinhAnh}">
                                    <button type="button" id="btnXoaAnh" class="btn-xoa-anh"
                                            onclick="xoaAnh()" style="margin-top:8px;">
                                        <i class="fas fa-trash"></i> Xóa ảnh hiện tại
                                    </button>
                                </c:if>
                            </div>

                            <div class="filter-group" style="margin-top:16px;">
                                <label class="filter-label">Trạng thái biến thể</label>
                                <select class="filter-select" name="trangThaiChiTiet">
                                    <option value="1" ${sanPhamChiTiet.trangThai == 1 ? 'selected' : ''}>
                                        Hoạt động
                                    </option>
                                    <option value="0" ${sanPhamChiTiet.trangThai == 0 ? 'selected' : ''}>
                                        Ngừng bán
                                    </option>
                                </select>
                            </div>
                        </div>

                    </div>

                    <!-- Các nút hành động -->
                    <div style="display:flex;justify-content:flex-end;gap:12px;margin-top:24px;">
                        <a href="${pageContext.request.contextPath}/SanPham" class="btn-reset"
                           style="text-decoration:none;" onclick="return confirmHuy()">
                            <i class="fas fa-times"></i> Hủy
                        </a>
                        <button type="submit" class="add-new-btn" id="btnSubmit">
                            <i class="fas fa-save"></i> Cập nhật
                        </button>
                    </div>

                </form>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<style>
    .sp-form-columns {
        display: grid;
        grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
        gap: 20px;
        margin-top: 8px;
        width: 100%;
    }
    .sp-form-columns > .filter-section { min-width: 0; }
    .sp-form-columns .filter-grid { min-width: 0; }
    .sp-form-columns .filter-grid > .filter-group { min-width: 0; }
    .sp-form-columns .filter-input,
    .sp-form-columns .filter-select,
    .sp-form-columns textarea.filter-input {
        width: 100%; min-width: 0; box-sizing: border-box;
    }
    .sp-form-columns input[type="file"].filter-input {
        width: 100%; min-width: 0; box-sizing: border-box; padding: 8px 10px;
    }
    .sp-image-preview-box {
        margin-top: 10px; width: 160px; height: 160px;
        border: 1px solid #e5e7eb; border-radius: 8px;
        display: flex; align-items: center; justify-content: center;
        overflow: hidden; background: #faf8f5;
    }
    .sp-image-preview { width: 100%; height: 100%; object-fit: cover; }
    .sp-image-preview-label {
        color: var(--text-light); text-align: center; font-size: 13px;
    }

    /* [MỚI] Style cho field hint */
    .field-hint {
        display: block;
        margin-top: 4px;
        font-size: 12px;
        color: #6b7280;
        font-style: italic;
    }

    /* [MỚI] Style cho dấu * bắt buộc */
    .required {
        color: #dc2626;
        font-weight: bold;
    }

    /* [MỚI] Style cho nút xóa ảnh */
    .btn-xoa-anh {
        background: #fee2e2;
        color: #dc2626;
        border: 1px solid #fecaca;
        padding: 6px 12px;
        border-radius: 6px;
        cursor: pointer;
        font-size: 13px;
        transition: all 0.2s;
    }
    .btn-xoa-anh:hover {
        background: #fecaca;
        border-color: #dc2626;
    }

    /* [MỚI] Style cho input khi có lỗi */
    .filter-input.error,
    .filter-select.error {
        border-color: #dc2626 !important;
        background-color: #fef2f2;
    }

    /* [MỚI] Style cho thông báo lỗi inline */
    .error-message {
        color: #dc2626;
        font-size: 12px;
        margin-top: 4px;
        display: none;
    }
    .error-message.show {
        display: block;
    }

    @media (max-width: 1024px) {
        .sp-form-columns { grid-template-columns: 1fr; }
    }
</style>

<script>
    // Preview ảnh khi chọn file
    function spPreviewImage(event) {
        const file = event.target.files[0];
        if (!file) return;

        // Kiểm tra kích thước file (tối đa 10MB)
        if (file.size > 10 * 1024 * 1024) {
            alert('Kích thước file không được vượt quá 10MB!');
            event.target.value = ''; // Reset input
            return;
        }

        // Kiểm tra định dạng file
        const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
        if (!allowedTypes.includes(file.type)) {
            alert('Chỉ chấp nhận file ảnh định dạng JPG, PNG, GIF, WEBP!');
            event.target.value = ''; // Reset input
            return;
        }

        const preview = document.getElementById('spImagePreview');
        const label = document.getElementById('spImagePreviewLabel');

        const reader = new FileReader();
        reader.onload = function (e) {
            preview.src = e.target.result;
            preview.style.display = 'block';
            label.style.display = 'none';
        };
        reader.readAsDataURL(file);
    }

    // [MỚI] Hàm xóa ảnh hiện tại
    function xoaAnh() {
        if (confirm('Bạn có chắc muốn xóa ảnh hiện tại?')) {
            const preview = document.getElementById('spImagePreview');
            const label = document.getElementById('spImagePreviewLabel');
            const hiddenInput = document.getElementById('hinhAnhHidden');
            const fileInput = document.getElementById('fileAnh');

            // Ẩn preview, hiện label
            preview.style.display = 'none';
            preview.src = '';
            label.style.display = 'block';

            // Xóa giá trị hidden và file input
            hiddenInput.value = '';
            fileInput.value = '';

            // Ẩn nút xóa
            document.getElementById('btnXoaAnh').style.display = 'none';
        }
    }

    // [MỚI] Xác nhận khi hủy
    function confirmHuy() {
        return confirm('Bạn có chắc muốn hủy? Các thay đổi sẽ không được lưu.');
    }

    // [MỚI] Validation form khi submit
    document.getElementById('sanPhamForm').addEventListener('submit', function(e) {
        let isValid = true;
        const requiredFields = this.querySelectorAll('[required]');

        // Xóa các lỗi cũ
        this.querySelectorAll('.error').forEach(el => el.classList.remove('error'));
        this.querySelectorAll('.error-message').forEach(el => el.classList.remove('show'));

        // Kiểm tra các field bắt buộc
        requiredFields.forEach(field => {
            if (!field.value || field.value.trim() === '') {
                isValid = false;
                field.classList.add('error');

                // Hiển thị thông báo lỗi
                let errorMsg = field.parentElement.querySelector('.error-message');
                if (!errorMsg) {
                    errorMsg = document.createElement('div');
                    errorMsg.className = 'error-message';
                    errorMsg.textContent = 'Trường này là bắt buộc';
                    field.parentElement.appendChild(errorMsg);
                }
                errorMsg.classList.add('show');
            }
        });

        // Kiểm tra giá bán >= giá nhập
        const giaNhap = parseFloat(this.querySelector('[name="giaNhap"]').value);
        const giaBan = parseFloat(this.querySelector('[name="giaBan"]').value);

        if (!isNaN(giaNhap) && !isNaN(giaBan) && giaBan < giaNhap) {
            isValid = false;
            const giaBanField = this.querySelector('[name="giaBan"]');
            giaBanField.classList.add('error');

            let errorMsg = giaBanField.parentElement.querySelector('.error-message');
            if (!errorMsg) {
                errorMsg = document.createElement('div');
                errorMsg.className = 'error-message';
                errorMsg.textContent = 'Giá bán phải lớn hơn hoặc bằng giá nhập';
                giaBanField.parentElement.appendChild(errorMsg);
            }
            errorMsg.classList.add('show');
        }

        if (!isValid) {
            e.preventDefault();
            // Scroll đến field lỗi đầu tiên
            const firstError = this.querySelector('.error');
            if (firstError) {
                firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
                firstError.focus();
            }
        }
    });

    // [MỚI] Xóa lỗi khi người dùng bắt đầu nhập
    document.querySelectorAll('.filter-input, .filter-select').forEach(field => {
        field.addEventListener('input', function() {
            this.classList.remove('error');
            const errorMsg = this.parentElement.querySelector('.error-message');
            if (errorMsg) {
                errorMsg.classList.remove('show');
            }
        });
    });
</script>
</body>
</html>