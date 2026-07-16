<%
    request.setAttribute("pageTitle", "Thêm sản phẩm");
%>
<%
    String actionAttr = (String) request.getAttribute("action");
    if (actionAttr == null) {
        actionAttr = request.getParameter("action");
    }
    boolean isEdit = "edit".equals(actionAttr);

    request.setAttribute("formActionUrl", isEdit ? "update" : "insert");
    request.setAttribute("pageTitleForm", isEdit ? "Cập nhật sản phẩm" : "Thêm mới sản phẩm");
    request.setAttribute("buttonTextForm", isEdit ? "Cập nhật" : "Thêm mới");
    request.setAttribute("isEdit", isEdit);
%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitleForm}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/FE/Admin/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/FE/Admin/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/FE/Admin/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/FE/Admin/css/danhmuc.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/FE/Admin/css/sanpham.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<%@include file="../layout/sidebar.jsp" %>
<div class="dashboard-container">
    <%@include file="../layout/header.jsp" %>

    <div class="category-section">
        <div class="category-header">
            <h2 class="category-title">${pageTitleForm}</h2>
            <a href="${pageContext.request.contextPath}/SanPham" class="btn-reset" style="text-decoration:none;">
                <i class="fas fa-arrow-left"></i> Quay lại danh sách
            </a>
        </div>

        <c:if test="${not empty error}">
            <div style="background:#fdecea;color:#b3261e;padding:12px 16px;border-radius:8px;margin-bottom:16px;">
                <i class="fas fa-circle-exclamation"></i> ${error}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/SanPham/${formActionUrl}"
              method="post" enctype="multipart/form-data" id="sanPhamForm">

            <c:if test="${isEdit}">
                <input type="hidden" name="id" value="${sanPham.id}">
            </c:if>

            <div class="sp-form-columns">

                <!-- CARD: THÔNG TIN SẢN PHẨM (tái sử dụng style filter-section) -->
                <div class="filter-section">
                    <div class="filter-header">
                        <div class="filter-title">
                            <i class="fas fa-box"></i> Thông tin sản phẩm
                        </div>
                    </div>

                    <div class="filter-group" style="margin-bottom:16px;">
                        <label class="filter-label">Mã sản phẩm</label>
                        <input type="text" class="filter-input" name="maSanPham"
                               value="${sanPham.maSanPham}" ${isEdit ? 'readonly' : ''} required>
                    </div>

                    <div class="filter-group" style="margin-bottom:16px;">
                        <label class="filter-label">Tên sản phẩm</label>
                        <input type="text" class="filter-input" name="tenSanPham"
                               value="${sanPham.tenSanPham}" required>
                    </div>

                    <div class="filter-grid" style="grid-template-columns:1fr 1fr;margin-bottom:0;">
                        <div class="filter-group">
                            <label class="filter-label">Danh mục</label>
                            <select class="filter-select" name="danhMucId" required>
                                <option value="">-- Chọn --</option>
                                <c:forEach var="dm" items="${danhMucList}">
                                    <option value="${dm.id}" ${dm.id == sanPham.danhMuc.id ? 'selected' : ''}>
                                            ${dm.tenDanhMuc}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="filter-group">
                            <label class="filter-label">Thương hiệu</label>
                            <select class="filter-select" name="thuongHieuId" required>
                                <option value="">-- Chọn --</option>
                                <c:forEach var="th" items="${thuongHieuList}">
                                    <option value="${th.id}" ${th.id == sanPham.thuongHieu.id ? 'selected' : ''}>
                                            ${th.tenThuongHieu}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="filter-grid" style="grid-template-columns:1fr 1fr;margin-bottom:0;margin-top:16px;">
                        <div class="filter-group">
                            <label class="filter-label">Chất liệu</label>
                            <select class="filter-select" name="chatLieuId" required>
                                <option value="">-- Chọn --</option>
                                <c:forEach var="cl" items="${chatLieuList}">
                                    <option value="${cl.id}" ${cl.id == sanPham.chatLieu.id ? 'selected' : ''}>
                                            ${cl.tenChatLieu}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="filter-group">
                            <label class="filter-label">Kiểu dáng</label>
                            <select class="filter-select" name="kieuDangId" required>
                                <option value="">-- Chọn --</option>
                                <c:forEach var="kd" items="${kieuDangList}">
                                    <option value="${kd.id}" ${kd.id == sanPham.kieuDang.id ? 'selected' : ''}>
                                            ${kd.tenKieuDang}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="filter-grid" style="grid-template-columns:1fr 1fr;margin-bottom:0;margin-top:16px;">
                        <div class="filter-group">
                            <label class="filter-label">Gọng kính</label>
                            <select class="filter-select" name="gongKinhId" required>
                                <option value="">-- Chọn --</option>
                                <c:forEach var="gk" items="${gongKinhList}">
                                    <option value="${gk.id}" ${gk.id == sanPham.gongKinh.id ? 'selected' : ''}>
                                            ${gk.hinhDangGong.hinhDang} - ${gk.kieuQuaiKinh.kieuQuai}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="filter-group">
                            <label class="filter-label">Tròng kính</label>
                            <select class="filter-select" name="trongKinhId" required>
                                <option value="">-- Chọn --</option>
                                <c:forEach var="tk" items="${trongKinhList}">
                                    <option value="${tk.id}" ${tk.id == sanPham.trongKinh.id ? 'selected' : ''}>
                                            ${tk.loaiTrong}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="filter-group" style="margin-top:16px;">
                        <label class="filter-label">Mô tả chi tiết</label>
                        <textarea class="filter-input" name="moTaChiTiet" rows="3">${sanPham.moTaChiTiet}</textarea>
                    </div>

                    <div class="filter-group" style="margin-top:16px;">
                        <label class="filter-label">Trạng thái</label>
                        <select class="filter-select" name="trangThai">
                            <option value="1" ${sanPham.trangThai == 1 ? 'selected' : ''}>Hoạt động</option>
                            <option value="0" ${sanPham.trangThai == 0 ? 'selected' : ''}>Ngừng bán</option>
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

                    <div class="filter-group" style="margin-bottom:16px;">
                        <label class="filter-label">Mã biến thể</label>
                        <input type="text" class="filter-input" name="ma"
                               value="${sanPhamChiTiet.ma}" required>
                    </div>

                    <div class="filter-grid" style="grid-template-columns:1fr 1fr;margin-bottom:0;">
                        <div class="filter-group">
                            <label class="filter-label">Màu sắc</label>
                            <select class="filter-select" name="mauSacId" required>
                                <option value="">-- Chọn --</option>
                                <c:forEach var="ms" items="${mauSacList}">
                                    <option value="${ms.id}" ${ms.id == sanPhamChiTiet.mauSac.id ? 'selected' : ''}>
                                            ${ms.tenMau}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="filter-group">
                            <label class="filter-label">Kích cỡ</label>
                            <select class="filter-select" name="kichCoId" required>
                                <option value="">-- Chọn --</option>
                                <c:forEach var="kc" items="${kichCoList}">
                                    <option value="${kc.id}" ${kc.id == sanPhamChiTiet.kichCo.id ? 'selected' : ''}>
                                            ${kc.tenKichCo}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="filter-grid" style="grid-template-columns:1fr 1fr;margin-bottom:0;margin-top:16px;">
                        <div class="filter-group">
                            <label class="filter-label">Giá nhập</label>
                            <input type="number" class="filter-input" name="giaNhap"
                                   value="${sanPhamChiTiet.giaNhap}" min="0" required>
                        </div>
                        <div class="filter-group">
                            <label class="filter-label">Giá bán</label>
                            <input type="number" class="filter-input" name="giaBan"
                                   value="${sanPhamChiTiet.giaBan}" min="0" required>
                        </div>
                    </div>

                    <div class="filter-grid" style="grid-template-columns:1fr 1fr;margin-bottom:0;margin-top:16px;">
                        <div class="filter-group">
                            <label class="filter-label">Số lượng tồn</label>
                            <input type="number" class="filter-input" name="soLuongTon"
                                   value="${sanPhamChiTiet.soLuongTon}" min="0" required>
                        </div>
                        <div class="filter-group">
                            <label class="filter-label">Trọng lượng (gram)</label>
                            <input type="number" class="filter-input" name="trongLuong"
                                   value="${sanPhamChiTiet.trongLuong}" min="0" required>
                        </div>
                    </div>

                    <div class="filter-group" style="margin-top:16px;">
                        <label class="filter-label">Hình ảnh sản phẩm</label>
                        <input type="file" class="filter-input" id="fileAnh" name="fileAnh"
                               accept="image/*" onchange="spPreviewImage(event)">
                        <input type="hidden" id="hinhAnhHidden" name="hinhAnhCu" value="${sanPhamChiTiet.hinhAnh}">

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
                    </div>

                    <div class="filter-group" style="margin-top:16px;">
                        <label class="filter-label">Trạng thái biến thể</label>
                        <select class="filter-select" name="trangThaiChiTiet">
                            <option value="1" ${sanPhamChiTiet.trangThai == 1 ? 'selected' : ''}>Hoạt động</option>
                            <option value="0" ${sanPhamChiTiet.trangThai == 0 ? 'selected' : ''}>Ngừng bán</option>
                        </select>
                    </div>
                </div>

            </div>

            <div style="display:flex;justify-content:flex-end;gap:12px;margin-top:24px;">
                <a href="${pageContext.request.contextPath}/SanPham" class="btn-reset" style="text-decoration:none;">
                    <i class="fas fa-times"></i> Hủy
                </a>
                <button type="submit" class="add-new-btn">
                    <i class="fas fa-check"></i> ${buttonTextForm}
                </button>
            </div>

        </form>
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

    /* Fix tràn ngang: grid item mặc định không co nhỏ hơn nội dung của nó */
    .sp-form-columns > .filter-section {
        min-width: 0;
    }

    .sp-form-columns .filter-grid {
        min-width: 0;
    }

    .sp-form-columns .filter-grid > .filter-group {
        min-width: 0;
    }

    .sp-form-columns .filter-input,
    .sp-form-columns .filter-select,
    .sp-form-columns textarea.filter-input {
        width: 100%;
        min-width: 0;
        box-sizing: border-box;
    }

    /* Input file mặc định có độ rộng nội dung cố định, dễ gây tràn nhất */
    .sp-form-columns input[type="file"].filter-input {
        width: 100%;
        min-width: 0;
        box-sizing: border-box;
        padding: 8px 10px;
    }

    .sp-image-preview-box {
        margin-top: 10px;
        width: 160px;
        height: 160px;
        border: 1px solid #e5e7eb;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        overflow: hidden;
        background: #faf8f5;
    }

    .sp-image-preview {
        width: 100%;
        height: 100%;
        object-fit: cover;
    }

    .sp-image-preview-label {
        color: var(--text-light);
        text-align: center;
        font-size: 13px;
    }

    @media (max-width: 1024px) {
        .sp-form-columns {
            grid-template-columns: 1fr;
        }
    }
</style>
<script>
    // Preview ảnh khi chọn file — không dùng localStorage.
    // Ảnh thật gửi lên server qua form multipart/form-data.
    function spPreviewImage(event) {
        const file = event.target.files[0];
        if (!file) return;

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
</script>
</body>
</html>
