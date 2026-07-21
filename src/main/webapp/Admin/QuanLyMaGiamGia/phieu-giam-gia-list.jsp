<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- PHAN 0: Khai bao JSTL de dung c:if, c:forEach, c:choose va fmt:formatNumber. --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%
    // PHAN 4: Neu Servlet chua truyen pageTitle thi JSP dat tieu de mac dinh.
    if (request.getAttribute("pageTitle") == null) {
        request.setAttribute("pageTitle", "Quản lý phiếu giảm giá");
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý phiếu giảm giá</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/phieu-giam-gia.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <!-- CSS tùy chỉnh cho nút Switch Trạng thái -->
    <style>
        .coupon-actions {
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .coupon-status-switch {
            position: relative;
            display: inline-block;
            width: 40px;
            height: 22px;
            margin: 0;
            vertical-align: middle;
        }

        .coupon-status-switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .coupon-status-slider {
            position: absolute;
            cursor: pointer;
            top: 0; left: 0; right: 0; bottom: 0;
            background-color: #dc3545; /* Màu đỏ khi ngưng áp dụng/tắt */
            transition: .3s;
            border-radius: 22px;
        }

        .coupon-status-slider:before {
            position: absolute;
            content: "";
            height: 16px;
            width: 16px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            transition: .3s;
            border-radius: 50%;
            box-shadow: 0 1px 3px rgba(0,0,0,0.3);
        }

        .coupon-status-switch input:checked + .coupon-status-slider {
            background-color: #28a745; /* Màu xanh khi đang áp dụng/bật */
        }

        .coupon-status-switch input:checked + .coupon-status-slider:before {
            transform: translateX(18px);
        }

        .coupon-status-switch input:disabled + .coupon-status-slider {
            opacity: 0.5;
            cursor: wait;
        }
    </style>
</head>
<body class="coupon-admin-page">
<%@ include file="../layout/sidebar.jsp" %>
<div class="dashboard-container">
    <%@ include file="../layout/header.jsp" %>

    <main class="coupon-page">
        <!-- PHẦN 4: JSP nhận dữ liệu từ Servlet qua request.setAttribute -->
        <div class="coupon-header">
            <div>
                <h2>Quản lý phiếu giảm giá</h2>
                <p>Quản lý mã giảm giá đang áp dụng trên hệ thống.</p>
            </div>
            <div class="coupon-header__actions">
                <%-- PHAN 1: Nut xuat Excel dung exportUrl do Servlet tao kem cac tham so loc hien tai. --%>
                <a class="coupon-btn coupon-btn--secondary" href="${exportUrl}">
                    <i class="fas fa-file-excel"></i> Xuất Excel
                </a>
                <%-- PHAN 1: Nut them moi goi GET /PhieuGiamGia/new de Servlet mo form them. --%>
                <a class="coupon-btn coupon-btn--primary" href="${pageContext.request.contextPath}/PhieuGiamGia/new">
                    <i class="fas fa-plus"></i> Thêm mới
                </a>
            </div>
        </div>

        <%-- PHAN 5: Hien thi thong bao thanh cong duoc Servlet dua vao request. --%>
        <c:if test="${not empty successMessage}">
            <div class="coupon-alert coupon-alert--success">${successMessage}</div>
        </c:if>
        <%-- PHAN 5: Hien thi thong bao loi neu Servlet set errorMessage. --%>
        <c:if test="${not empty errorMessage}">
            <div class="coupon-alert coupon-alert--error">${errorMessage}</div>
        </c:if>

        <section class="coupon-card">
            <!-- PHẦN 1: Form lọc/tìm kiếm gửi dữ liệu bằng GET về /PhieuGiamGia -->
            <div class="coupon-card__title">
                <i class="fas fa-filter"></i>
                <span>Bộ lọc tìm kiếm</span>
            </div>
            <form action="${pageContext.request.contextPath}/PhieuGiamGia" method="get" class="coupon-filter">
                <label class="coupon-field coupon-field--wide">
                    <span>Tìm theo mã hoặc tên</span>
                    <%-- PHAN 1: name=keyword khop voi request.getParameter("keyword") trong showList(). --%>
                    <input type="text" name="keyword" value="${keyword}" placeholder="Nhập mã hoặc tên phiếu...">
                </label>
                <label class="coupon-field">
                    <span>Mã giảm giá</span>
                    <%-- PHAN 1: name=maVoucher gui ma phieu can loc ve Servlet. --%>
                    <input type="text" name="maVoucher" value="${maVoucher}" placeholder="VD: VC001">
                </label>
                <label class="coupon-field">
                    <span>Tên giảm giá</span>
                    <%-- PHAN 1: name=tenVoucher gui ten phieu can loc ve Servlet. --%>
                    <input type="text" name="tenVoucher" value="${tenVoucher}" placeholder="Tên phiếu">
                </label>
                <label class="coupon-field">
                    <span>Loại giảm</span>
                    <%-- PHAN 1: loaiGiamGia nhan percent/amount de DAO loc loai giam. --%>
                    <select name="loaiGiamGia">
                        <option value="">Tất cả</option>
                        <option value="percent" <c:if test="${loaiGiamGia == 'percent'}">selected</c:if>>Giảm phần trăm</option>
                        <option value="amount" <c:if test="${loaiGiamGia == 'amount'}">selected</c:if>>Giảm tiền</option>
                    </select>
                </label>
                <label class="coupon-field">
                    <span>Trạng thái</span>
                    <%-- PHAN 1: trangThai gui active/upcoming/expired/inactive cho Servlet loc trang thai. --%>
                    <select name="trangThai">
                        <option value="">Tất cả</option>
                        <option value="active" <c:if test="${trangThai == 'active'}">selected</c:if>>Đang áp dụng</option>
                        <option value="upcoming" <c:if test="${trangThai == 'upcoming'}">selected</c:if>>Chưa bắt đầu</option>
                        <option value="expired" <c:if test="${trangThai == 'expired'}">selected</c:if>>Kết thúc</option>
                        <option value="inactive" <c:if test="${trangThai == 'inactive'}">selected</c:if>>Ngừng áp dụng</option>
                    </select>
                </label>
                <label class="coupon-field">
                    <span>Từ ngày</span>
                    <%-- PHAN 1: tuNgay la moc bat dau khoang loc ngay, Servlet parse sang LocalDate. --%>
                    <input type="date" name="tuNgay" value="${tuNgay}">
                </label>
                <label class="coupon-field">
                    <span>Đến ngày</span>
                    <%-- PHAN 1: denNgay la moc ket thuc khoang loc ngay, Servlet parse sang LocalDate. --%>
                    <input type="date" name="denNgay" value="${denNgay}">
                </label>
                <div class="coupon-filter__actions">
                    <%-- PHAN 1: Submit form GET, Servlet showList() nhan toan bo parameter loc. --%>
                    <button class="coupon-btn coupon-btn--primary" type="submit">
                        <i class="fas fa-search"></i> Tìm kiếm
                    </button>
                    <%-- PHAN 1: Dat lai bo loc bang cach quay ve /PhieuGiamGia khong co query string. --%>
                    <a class="coupon-btn coupon-btn--light" href="${pageContext.request.contextPath}/PhieuGiamGia">
                        <i class="fas fa-rotate-left"></i> Đặt lại
                    </a>
                </div>
            </form>
        </section>

        <section class="coupon-card">
            <!-- PHẦN 4: Hiển thị danh sách items do Servlet lấy từ DAO -->
            <div class="coupon-table-heading">
                <div>
                    <h3>Danh sách phiếu giảm giá</h3>
                    <p>Tổng số bản ghi: <strong>${totalRecords}</strong></p>
                </div>
            </div>

            <div class="coupon-table-wrapper">
                <table class="coupon-table">
                    <colgroup>
                        <col class="coupon-col-stt">
                        <col class="coupon-col-code">
                        <col class="coupon-col-name">
                        <col class="coupon-col-type">
                        <col class="coupon-col-discount">
                        <col class="coupon-col-money">
                        <col class="coupon-col-quantity">
                        <col class="coupon-col-date">
                        <col class="coupon-col-date">
                        <col class="coupon-col-status">
                        <col class="coupon-col-action">
                    </colgroup>
                    <thead>
                    <tr>
                        <th>STT</th>
                        <th>Mã giảm giá</th>
                        <th>Tên giảm giá</th>
                        <th>Loại phiếu</th>
                        <th>Giá trị giảm</th>
                        <th>Đơn hàng tối thiểu</th>
                        <th>Số lượng</th>
                        <th>Ngày bắt đầu</th>
                        <th>Ngày kết thúc</th>
                        <th>Trạng thái</th>
                        <th>Hành động</th>
                    </tr>
                    </thead>
                    <tbody>
                    <%-- PHAN 4: Lap qua items do Servlet setAttribute("items", coupons). --%>
                    <c:forEach var="coupon" items="${items}" varStatus="loop">
                        <tr>
                                <%-- PHAN 4: STT tinh theo startIndex de dung khi phan trang. --%>
                            <td>${startIndex + loop.count}</td>
                                <%-- PHAN 4: Cac dong duoi lay du lieu tu getter cua Entity PhieuGiamGia. --%>
                            <td><strong>${coupon.maVoucher}</strong></td>
                            <td>${coupon.tenVoucher}</td>
                            <td>
                                <span class="coupon-type-badge">
                                        ${coupon.loaiPhieu == 1 ? 'Cá nhân' : 'Công khai'}
                                </span>
                            </td>
                            <td>
                                    <%-- PHAN 4: Neu loai giam la percent thi hien thi %, nguoc lai hien thi tien. --%>
                                <c:choose>
                                    <c:when test="${coupon.loaiGiamGiaFilterValue == 'percent'}">
                                        <fmt:formatNumber value="${coupon.giaTriGiam}" maxFractionDigits="0"/>%
                                    </c:when>
                                    <c:otherwise>
                                        <fmt:formatNumber value="${coupon.giaTriGiam}" type="number" maxFractionDigits="0"/> đ
                                    </c:otherwise>
                                </c:choose>
                                <div class="coupon-small-text">${coupon.loaiGiamGiaText}</div>
                            </td>
                            <td><fmt:formatNumber value="${coupon.donToiThieu}" type="number" maxFractionDigits="0"/> đ</td>
                            <td>${coupon.soLuongDaDung == null ? 0 : coupon.soLuongDaDung}/${coupon.soLuong}</td>
                            <td>${coupon.ngayBatDauText}</td>
                            <td>${coupon.ngayKetThucText}</td>
                            <td>
                                    <%-- PHAN 4: Trang thai hien thi lay tu getTrangThaiHienThi() va getTrangThaiCssClass(). --%>
                                <span class="coupon-status ${coupon.trangThaiCssClass}">
                                    <c:choose>
                                        <c:when test="${coupon.trangThaiCssClass == 'status-upcoming'}">Chưa diễn ra</c:when>
                                        <c:when test="${coupon.trangThaiCssClass == 'status-active'}">Đang diễn ra</c:when>
                                        <c:when test="${coupon.trangThaiCssClass == 'status-expired'}">Đã kết thúc</c:when>
                                        <c:otherwise>Ngừng hoạt động</c:otherwise>
                                    </c:choose>
                                </span>
                            </td>
                            <td>
                                <div class="coupon-actions">
                                        <%-- Nút sửa mở đúng dữ liệu phiếu theo ID --%>
                                    <a class="coupon-icon-btn coupon-edit-btn"
                                       href="${pageContext.request.contextPath}/PhieuGiamGia/edit?id=${coupon.id}"
                                       title="Chỉnh sửa"
                                       aria-label="Chỉnh sửa">
                                        <svg class="coupon-edit-btn__icon" viewBox="0 0 24 24" aria-hidden="true" focusable="false">
                                            <path d="M5 16.8 15.8 6l2.2 2.2L7.2 19H5v-2.2Z"></path>
                                            <path d="M17 4.8a1.4 1.4 0 0 1 2 0l.2.2a1.4 1.4 0 0 1 0 2L18 8.2 15.8 6 17 4.8Z"></path>
                                        </svg>
                                    </a>

                                        <%-- Nút Switch Cập Nhật Trạng Thái Thời Gian Thực --%>
                                    <label class="coupon-status-switch" title="Chuyển trạng thái">
                                        <input type="checkbox"
                                               class="js-coupon-status-toggle"
                                               data-id="${coupon.id}"
                                            ${coupon.trangThai == 1 ? 'checked' : ''}>
                                        <span class="coupon-status-slider"></span>
                                    </label>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <%-- PHAN 5: Neu DAO tra ve danh sach rong thi hien thi thong bao khong co du lieu. --%>
                    <c:if test="${empty items}">
                        <tr>
                            <td colspan="11" class="coupon-empty">
                                <i class="fas fa-inbox"></i>
                                Không có dữ liệu phù hợp.
                            </td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>

            <div class="coupon-pagination">
                <!-- PHẦN 4: Phân trang dùng currentPage, totalPages và pageUrlPrefix từ Servlet -->
                <div class="coupon-pagination__summary">
                    Hiển thị ${fn:length(items)} / tổng ${totalRecords} bản ghi
                </div>
                <div class="coupon-pagination__nav">
                    <a class="coupon-page-link ${currentPage <= 1 ? 'disabled' : ''}"
                       href="${currentPage <= 1 ? '#' : pageUrlPrefix}${currentPage <= 1 ? '' : currentPage - 1}"
                       aria-label="Trang trước">
                        <i class="fas fa-chevron-left"></i>
                    </a>
                    <span class="coupon-page-current">Trang ${currentPage} / ${totalPages}</span>
                    <a class="coupon-page-link ${currentPage >= totalPages ? 'disabled' : ''}"
                       href="${currentPage >= totalPages ? '#' : pageUrlPrefix}${currentPage >= totalPages ? '' : currentPage + 1}"
                       aria-label="Trang sau">
                        <i class="fas fa-chevron-right"></i>
                    </a>
                </div>
                <form class="coupon-pagination__size" action="${pageContext.request.contextPath}/PhieuGiamGia" method="get">
                    <input type="hidden" name="keyword" value="${keyword}">
                    <input type="hidden" name="maVoucher" value="${maVoucher}">
                    <input type="hidden" name="tenVoucher" value="${tenVoucher}">
                    <input type="hidden" name="loaiGiamGia" value="${loaiGiamGia}">
                    <input type="hidden" name="trangThai" value="${trangThai}">
                    <input type="hidden" name="tuNgay" value="${tuNgay}">
                    <input type="hidden" name="denNgay" value="${denNgay}">
                    <label>
                        <span>Số bản ghi/trang</span>
                        <select name="size" onchange="this.form.submit()">
                            <option value="5" ${pageSize == 5 ? 'selected' : ''}>5</option>
                            <option value="10" ${pageSize == 10 ? 'selected' : ''}>10</option>
                            <option value="20" ${pageSize == 20 ? 'selected' : ''}>20</option>
                        </select>
                    </label>
                </form>
            </div>
        </section>
    </main>
</div>

<!-- JavaScript xử lý cập nhật trạng thái thời gian thực -->
<script>
    document.addEventListener('DOMContentLoaded', () => {
        const couponSwitches = document.querySelectorAll('.js-coupon-status-toggle');

        couponSwitches.forEach(switchInput => {
            switchInput.addEventListener('change', function () {
                const couponId = this.dataset.id;
                const isChecked = this.checked;
                const newStatus = isChecked ? 1 : 0;
                const currentSwitch = this;

                // Vô hiệu hóa switch tạm thời khi đang xử lý request
                currentSwitch.disabled = true;

                const params = new URLSearchParams();
                params.append('id', couponId);
                params.append('trangThai', newStatus);

                fetch('${pageContext.request.contextPath}/PhieuGiamGia/change-status', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                    },
                    body: params
                })
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('Cập nhật thất bại');
                        }

                        // Cập nhật nhãn Trạng thái ở cột bên cạnh ngay lập tức
                        const row = currentSwitch.closest('tr');
                        const statusBadge = row.querySelector('.coupon-status');

                        if (statusBadge) {
                            if (newStatus === 1) {
                                statusBadge.className = 'coupon-status status-active';
                                statusBadge.textContent = 'Đang diễn ra';
                            } else {
                                statusBadge.className = 'coupon-status status-inactive';
                                statusBadge.textContent = 'Ngừng hoạt động';
                            }
                        }
                    })
                    .catch(error => {
                        alert('Lỗi cập nhật trạng thái phiếu giảm giá!');
                        // Hoàn tác công tắc về vị trí cũ nếu lỗi
                        currentSwitch.checked = !isChecked;
                    })
                    .finally(() => {
                        currentSwitch.disabled = false;
                    });
            });
        });
    });
</script>
</body>
</html>