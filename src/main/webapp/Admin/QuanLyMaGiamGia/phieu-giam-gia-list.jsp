<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%
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
</head>
<body class="coupon-admin-page">
<%@ include file="../layout/sidebar.jsp" %>
<div class="dashboard-container">
    <%@ include file="../layout/header.jsp" %>

    <main class="coupon-page">
        <div class="coupon-header">
            <div>
                <h2>Quản lý phiếu giảm giá</h2>
                <p>Quản lý mã giảm giá công khai và cá nhân.</p>
            </div>
            <div class="coupon-header__actions">
                <a class="coupon-btn coupon-btn--secondary" href="${exportUrl}">
                    <i class="fas fa-file-excel"></i> Xuất Excel
                </a>
                <a class="coupon-btn coupon-btn--primary" href="${pageContext.request.contextPath}/PhieuGiamGia/new">
                    <i class="fas fa-plus"></i> Thêm mới
                </a>
            </div>
        </div>

        <c:if test="${not empty successMessage}">
            <div class="coupon-alert coupon-alert--success">${successMessage}</div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="coupon-alert coupon-alert--error">${errorMessage}</div>
        </c:if>

        <section class="coupon-card">
            <div class="coupon-card__title">
                <i class="fas fa-filter"></i>
                <span>Bộ lọc tìm kiếm</span>
            </div>
            <form action="${pageContext.request.contextPath}/PhieuGiamGia" method="get" class="coupon-filter">
                <label class="coupon-field coupon-field--wide">
                    <span>Tìm theo mã hoặc tên</span>
                    <input type="text" name="keyword" value="${keyword}" placeholder="Nhập mã hoặc tên phiếu...">
                </label>
                <label class="coupon-field">
                    <span>Mã giảm giá</span>
                    <input type="text" name="maVoucher" value="${maVoucher}" placeholder="VD: VC001">
                </label>
                <label class="coupon-field">
                    <span>Tên giảm giá</span>
                    <input type="text" name="tenVoucher" value="${tenVoucher}" placeholder="Tên phiếu">
                </label>
                <label class="coupon-field">
                    <span>Loại phiếu</span>
                    <select name="loaiPhieu">
                        <option value="">Tất cả</option>
                        <option value="public" <c:if test="${loaiPhieu == 'public'}">selected</c:if>>Công khai</option>
                        <option value="personal" <c:if test="${loaiPhieu == 'personal'}">selected</c:if>>Cá nhân</option>
                    </select>
                </label>
                <label class="coupon-field">
                    <span>Loại giảm</span>
                    <select name="loaiGiamGia">
                        <option value="">Tất cả</option>
                        <option value="percent" <c:if test="${loaiGiamGia == 'percent'}">selected</c:if>>Giảm phần trăm</option>
                        <option value="amount" <c:if test="${loaiGiamGia == 'amount'}">selected</c:if>>Giảm tiền</option>
                    </select>
                </label>
                <label class="coupon-field">
                    <span>Trạng thái</span>
                    <select name="trangThai">
                        <option value="">Tất cả</option>
                        <option value="active" <c:if test="${trangThai == 'active'}">selected</c:if>>Đang áp dụng</option>
                        <option value="upcoming" <c:if test="${trangThai == 'upcoming'}">selected</c:if>>Chưa bắt đầu</option>
                        <option value="expired" <c:if test="${trangThai == 'expired'}">selected</c:if>>Kết thúc</option>
                        <option value="inactive" <c:if test="${trangThai == 'inactive'}">selected</c:if>>Ngừng áp dụng</option>
                    </select>
                </label>
                <label class="coupon-field">
                    <span>Đến ngày kết thúc</span>
                    <input type="date" name="denNgay" value="${denNgay}">
                </label>
                <label class="coupon-field">
                    <span>Số bản ghi/trang</span>
                    <select name="size">
                        <option value="5" <c:if test="${pageSize == 5}">selected</c:if>>5 bản ghi</option>
                        <option value="10" <c:if test="${pageSize == 10}">selected</c:if>>10 bản ghi</option>
                        <option value="20" <c:if test="${pageSize == 20}">selected</c:if>>20 bản ghi</option>
                    </select>
                </label>
                <div class="coupon-filter__actions">
                    <button class="coupon-btn coupon-btn--primary" type="submit">
                        <i class="fas fa-search"></i> Tìm kiếm
                    </button>
                    <a class="coupon-btn coupon-btn--light" href="${pageContext.request.contextPath}/PhieuGiamGia">
                        <i class="fas fa-rotate-left"></i> Đặt lại
                    </a>
                </div>
            </form>
        </section>

        <section class="coupon-card">
            <div class="coupon-table-heading">
                <div>
                    <h3>Danh sách phiếu giảm giá</h3>
                    <p>Tổng số bản ghi: <strong>${totalRecords}</strong></p>
                </div>
            </div>

            <div class="coupon-table-wrapper">
                <table class="coupon-table">
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
                    <c:forEach var="coupon" items="${items}" varStatus="loop">
                        <tr>
                            <td>${startIndex + loop.count}</td>
                            <td><strong>${coupon.maVoucher}</strong></td>
                            <td>${coupon.tenVoucher}</td>
                            <td><span class="coupon-badge coupon-badge--type">${coupon.loaiPhieuText}</span></td>
                            <td>
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
                                <span class="coupon-status ${coupon.trangThaiCssClass}">
                                    ${coupon.trangThaiHienThi}
                                </span>
                            </td>
                            <td>
                                <div class="coupon-actions">
                                    <a class="coupon-icon-btn" href="${pageContext.request.contextPath}/PhieuGiamGia/edit?id=${coupon.id}" title="Sửa">
                                        <i class="fas fa-pen"></i>
                                    </a>
                                    <form action="${pageContext.request.contextPath}/PhieuGiamGia/change-status" method="post"
                                          onsubmit="return confirm('Bạn có chắc muốn đổi trạng thái phiếu này không?');">
                                        <input type="hidden" name="id" value="${coupon.id}">
                                        <input type="hidden" name="currentStatus" value="${coupon.trangThai}">
                                        <input type="hidden" name="returnQuery" value="${currentQueryString}">
                                        <label class="coupon-switch" title="${coupon.dangBat ? 'Tắt phiếu' : 'Bật phiếu'}">
                                            <input type="checkbox" <c:if test="${coupon.dangBat}">checked</c:if> onchange="this.form.submit()">
                                            <span></span>
                                        </label>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
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
                <a class="coupon-page-link ${currentPage <= 1 ? 'disabled' : ''}"
                   href="${currentPage <= 1 ? '#' : pageUrlPrefix}${currentPage <= 1 ? '' : currentPage - 1}">
                    Trước
                </a>
                <span>Trang ${currentPage} / ${totalPages}</span>
                <a class="coupon-page-link ${currentPage >= totalPages ? 'disabled' : ''}"
                   href="${currentPage >= totalPages ? '#' : pageUrlPrefix}${currentPage >= totalPages ? '' : currentPage + 1}">
                    Sau
                </a>
            </div>
        </section>
    </main>
</div>
</body>
</html>
