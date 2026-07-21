<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "Quản lý khách hàng");
    request.setAttribute("activeMenu", "customer");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý khách hàng - RIOR Admin</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/danhmuc.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/customer-management.css">
    <script src="${pageContext.request.contextPath}/Admin/js/customer-management.js" defer></script>

    <style>
        body {
            background-color: #f8fafc;
            margin: 0;
            padding: 0;
        }

        .main-content {
            margin: 0 !important;
            padding: 0 !important;
            width: 100% !important;
        }

        .dashboard-container {
            margin-left: 260px !important;
            padding: 24px 32px !important;
            min-height: 100vh;
            box-sizing: border-box !important;
            transition: all 0.3s ease;
        }

        .category-section {
            width: 100% !important;
            max-width: 100% !important;
            box-sizing: border-box !important;
        }

        .filter-section {
            background: #ffffff;
            border: 1px solid #e5e7eb;
            border-radius: 12px;
            padding: 20px;
            margin-bottom: 24px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
        }

        .filter-header {
            margin-bottom: 16px;
            padding-bottom: 12px;
            border-bottom: 1px solid #f3f4f6;
        }

        .filter-title {
            font-size: 16px;
            font-weight: 700;
            color: #1f2937;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .filter-title i {
            color: #b4975a;
        }

        .filter-grid-kh {
            display: grid;
            grid-template-columns: 1fr;
            gap: 16px;
        }

        .filter-group {
            display: flex;
            flex-direction: column;
            gap: 6px;
        }

        .filter-label {
            font-size: 13px;
            font-weight: 600;
            color: #4b5563;
        }

        .filter-input, .filter-select {
            width: 100%;
            height: 40px;
            padding: 8px 12px;
            font-size: 14px;
            border: 1px solid #d1d5db;
            border-radius: 8px;
            background-color: #ffffff;
            color: #1f2937;
            box-sizing: border-box;
            transition: all 0.2s ease;
        }

        .filter-input:focus, .filter-select:focus {
            border-color: #b4975a !important;
            outline: none;
            box-shadow: 0 0 0 3px rgba(180, 151, 90, 0.15);
        }

        .filter-action-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 16px;
            padding-top: 16px;
            border-top: 1px dashed #e5e7eb;
        }

        .filter-action-right {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .add-new-btn {
            background-color: #b4975a !important;
            color: #ffffff !important;
            border: none !important;
            border-radius: 8px;
            padding: 10px 20px;
            font-weight: 600;
            font-size: 14px;
            cursor: pointer;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: background-color 0.2s ease;
            text-decoration: none;
        }

        .add-new-btn:hover {
            background-color: #9a8048 !important;
        }

        .btn-secondary-outline {
            background: #ffffff !important;
            color: #4b5563 !important;
            border: 1px solid #d1d5db !important;
            padding: 10px 20px;
            border-radius: 8px;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            font-weight: 600;
            font-size: 14px;
            cursor: pointer;
            transition: all 0.2s ease;
        }

        .btn-secondary-outline:hover {
            background: #f9fafb !important;
            border-color: #9ca3af !important;
            color: #1f2937 !important;
        }

        .table-toolbar {
            position: sticky;
            top: 70px;
            background: #ffffff;
            z-index: 90;
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 14px 16px;
            margin-top: 24px;
            border: 1px solid #e5e7eb;
            border-bottom: none;
            border-radius: 8px 8px 0 0;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
        }

        .toolbar-left-results {
            font-size: 14px;
            font-weight: 500;
            color: #4b5563;
        }

        .table-wrapper {
            width: 100% !important;
            max-width: 100% !important;
            overflow-x: auto !important;
            background: #ffffff;
            border: 1px solid #e5e7eb;
            border-top: none;
            border-radius: 0 0 8px 8px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
            box-sizing: border-box !important;
        }

        .category-table,
        .customer-management-table {
            margin-top: 0 !important;
            border: none !important;
            border-radius: 0 !important;
            width: 100% !important;
            min-width: 1000px !important;
            table-layout: fixed !important;
            border-collapse: collapse !important;
        }

        .category-table th,
        .customer-management-table th {
            padding: 12px 10px !important;
            font-size: 13px !important;
            white-space: nowrap !important;
            background-color: #f9fafb !important;
            color: #374151 !important;
            font-weight: 700 !important;
            border-bottom: 1px solid #e5e7eb !important;
            text-align: left !important;
        }

        .category-table td,
        .customer-management-table td {
            padding: 12px 10px !important;
            font-size: 13px !important;
            white-space: nowrap !important;
            overflow: hidden !important;
            text-overflow: ellipsis !important;
            border-bottom: 1px solid #f3f4f6 !important;
            color: #374151 !important;
        }

        .col-id { width: 5%; }
        .col-code { width: 11%; }
        .col-name { width: 16%; }
        .col-email { width: 18%; }
        .col-phone { width: 12%; }
        .col-dob { width: 10%; }
        .col-gender { width: 8%; }
        .col-status { width: 10%; }
        .col-action { width: 10%; }

        .action-buttons {
            display: flex;
            align-items: center;
            gap: 6px;
        }

        .toast-custom {
            position: fixed;
            top: 25px;
            right: 25px;
            padding: 16px 24px;
            border-radius: 8px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
            z-index: 1000000;
            display: flex;
            align-items: center;
            gap: 12px;
            font-weight: 600;
            font-size: 15px;
        }

        .toast-success-style {
            background-color: #e8f5e9;
            color: #2e7d32;
            border: 1px solid #c8e6c9;
        }

        .toast-error-style {
            background-color: #fdecea;
            color: #b3261e;
            border: 1px solid #fad2cf;
        }

        .customer-field-error {
            color: #dc2626;
            font-size: 12px;
            margin-top: 4px;
            display: block;
        }

        @media (max-width: 992px) {
            .dashboard-container {
                margin-left: 0 !important;
                padding: 16px !important;
            }
        }
    </style>
</head>
<body>
<%@ include file="../Admin/layout/sidebar.jsp" %>

<div class="dashboard-container">
    <%@ include file="../Admin/layout/header.jsp" %>

    <!-- ĐÃ THÊM CLASS customer-page ĐỂ KẾT NỐI VỚI JS -->
    <div class="category-section customer-page">
        <c:choose>
            <c:when test="${customerEditMode}">
                <c:url var="customerFormAction" value="/khach-hang/sua" />
            </c:when>
            <c:otherwise>
                <c:url var="customerFormAction" value="/khach-hang/add" />
            </c:otherwise>
        </c:choose>

        <c:set var="customerFormMustOpen"
               value="${customerEditMode
                        or not empty hoTenError
                        or not empty emailError
                        or not empty soDienThoaiError
                        or not empty ngaySinhError
                        or not empty gioiTinhError}" />

        <div class="category-header">
            <h2 class="category-title">Quản lý khách hàng</h2>
        </div>

        <!-- THÔNG BÁO TOAST -->
        <div id="toast-container"></div>
        <c:if test="${not empty successMessage}">
            <div id="toast-msg" class="toast-custom toast-success-style">
                <i class="fa-solid fa-circle-check"></i>
                <span><c:out value="${successMessage}" /></span>
            </div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div id="toast-msg" class="toast-custom toast-error-style">
                <i class="fa-solid fa-circle-exclamation"></i>
                <span><c:out value="${errorMessage}" /></span>
            </div>
        </c:if>

        <!-- FORM THÊM / CHỈNH SỬA KHÁCH HÀNG -->
        <div id="customer-form-panel"
             class="customer-form-panel"
             data-open="${customerFormMustOpen ? 'true' : 'false'}"
             data-server-error="${not empty hoTenError
                          or not empty emailError
                          or not empty soDienThoaiError
                          or not empty ngaySinhError
                          or not empty gioiTinhError ? 'true' : 'false'}"
             data-edit-mode="${customerEditMode ? 'true' : 'false'}"
             data-cancel-url="${pageContext.request.contextPath}/khach-hang/hien-thi"
        ${customerFormMustOpen ? '' : 'hidden'}>
            <section class="customer-card customer-form-card" aria-labelledby="customer-form-title" style="margin-bottom: 24px;">
                <div class="customer-card-heading">
                    <div>
                        <h2 id="customer-form-title">
                            ${customerEditMode ? 'Chỉnh sửa khách hàng' : 'Thêm khách hàng'}
                        </h2>
                        <p>
                            ${customerEditMode
                                    ? 'Cập nhật thông tin khách hàng, không thay đổi mã và trạng thái.'
                                    : 'Nhập thông tin để tạo khách hàng mới.'}
                        </p>
                    </div>
                </div>

                <c:if test="${customerEditMode}">
                    <div class="customer-edit-summary" aria-label="Thông tin không thể chỉnh sửa">
                        <div class="customer-summary-item">
                            <span class="customer-summary-label">Mã khách hàng</span>
                            <strong><c:out value="${editKhachHang.maKhachHang}" /></strong>
                        </div>
                        <div class="customer-summary-item">
                            <span class="customer-summary-label">Trạng thái</span>
                            <c:choose>
                                <c:when test="${editKhachHang.trangThai == 1}">
                                    <span class="category-status status-active">Hoạt động</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="category-status status-inactive">Không hoạt động</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </c:if>

                <form method="post"
                      action="${customerFormAction}"
                      class="customer-management-form"
                      data-customer-form
                      novalidate>
                    <c:if test="${customerEditMode}">
                        <input type="hidden" name="id" value="<c:out value='${editKhachHang.id}' />">
                    </c:if>

                    <div class="customer-field">
                        <label for="customer-full-name">Họ tên <span aria-hidden="true">*</span></label>
                        <input id="customer-full-name"
                               class="filter-input"
                               type="text"
                               name="hoTen"
                               value="<c:out value='${formHoTen}' />"
                               maxlength="250"
                               autocomplete="name"
                               required>
                        <span id="customer-full-name-server-error" data-customer-client-error="hoTen" class="customer-field-error"><c:out value="${hoTenError}" /></span>
                    </div>

                    <div class="customer-field">
                        <label for="customer-email">Email</label>
                        <input id="customer-email"
                               class="filter-input"
                               type="email"
                               name="email"
                               value="<c:out value='${formEmail}' />"
                               maxlength="150">
                        <span id="customer-email-server-error" data-customer-client-error="email" class="customer-field-error"><c:out value="${emailError}" /></span>
                    </div>

                    <div class="customer-field">
                        <label for="customer-phone">Số điện thoại <span aria-hidden="true">*</span></label>
                        <input id="customer-phone"
                               class="filter-input"
                               type="tel"
                               name="soDienThoai"
                               value="<c:out value='${formSoDienThoai}' />"
                               maxlength="10"
                               required>
                        <span id="customer-phone-server-error" data-customer-client-error="soDienThoai" class="customer-field-error"><c:out value="${soDienThoaiError}" /></span>
                    </div>

                    <div class="customer-field">
                        <label for="customer-birth-date">Ngày sinh</label>
                        <input id="customer-birth-date"
                               class="filter-input"
                               type="date"
                               name="ngaySinh"
                               value="<c:out value='${formNgaySinh}' />">
                        <span id="customer-birth-date-server-error" data-customer-client-error="ngaySinh" class="customer-field-error"><c:out value="${ngaySinhError}" /></span>
                    </div>

                    <div class="customer-field">
                        <label for="customer-gender">Giới tính <span aria-hidden="true">*</span></label>
                        <select id="customer-gender"
                                name="gioiTinh"
                                class="filter-select"
                                required>
                            <option value="">-- Chọn giới tính --</option>
                            <option value="1" ${formGioiTinh == 1 ? 'selected' : ''}>Nam</option>
                            <option value="0" ${formGioiTinh == 0 ? 'selected' : ''}>Nữ</option>
                        </select>
                        <span id="customer-gender-server-error" data-customer-client-error="gioiTinh" class="customer-field-error"><c:out value="${gioiTinhError}" /></span>
                    </div>

                    <div class="customer-form-actions" style="margin-top: 16px; display: flex; gap: 12px;">
                        <button type="submit" class="add-new-btn">
                            <i class="fas ${customerEditMode ? 'fa-floppy-disk' : 'fa-plus'}"></i>
                            <span>${customerEditMode ? 'Cập nhật' : 'Thêm'}</span>
                        </button>
                        <button type="button" id="customer-close-form" class="btn-secondary-outline">
                            Hủy
                        </button>
                    </div>
                </form>
            </section>
        </div>

        <!-- BỘ LỌC TÌM KIẾM -->
        <div class="filter-section">
            <div class="filter-header">
                <div class="filter-title">
                    <i class="fas fa-filter"></i>
                    Bộ lọc tìm kiếm
                </div>
            </div>

            <form class="customer-search-form" data-customer-search-form role="search">
                <div class="filter-grid-kh">
                    <div class="filter-group">
                        <label class="filter-label" for="customer-search">Tìm kiếm khách hàng</label>
                        <input id="customer-search"
                               type="search"
                               class="filter-input"
                               placeholder="Tìm kiếm theo mã, họ tên, sđt, email..."
                               autocomplete="off"
                               data-customer-search-input>
                    </div>
                </div>

                <div class="filter-action-row">
                    <div class="filter-action-left"></div>
                    <div class="filter-action-right">
                        <button type="submit" class="add-new-btn" style="padding: 10px 24px;">
                            <i class="fas fa-search"></i> Tìm kiếm
                        </button>
                        <button type="button" class="btn-secondary-outline" data-clear-customer-search hidden>
                            <i class="fas fa-times"></i> Xóa bộ lọc
                        </button>
                    </div>
                </div>
            </form>
        </div>

        <!-- TOOLBAR CỐ ĐỊNH -->
        <div class="table-toolbar">
            <div class="toolbar-left-results">
                Danh sách khách hàng
            </div>
            <div>
                <button type="button"
                        id="customer-toggle-form"
                        class="add-new-btn customer-open-form-button"
                        aria-expanded="${customerFormMustOpen ? 'true' : 'false'}"
                        style="padding: 10px 20px;">
                    <i class="fas fa-plus"></i> Thêm khách hàng
                </button>
            </div>
        </div>

        <!-- BẢNG DỮ LIỆU KHÁCH HÀNG -->
        <div class="table-wrapper">
            <table class="category-table customer-management-table">
                <thead>
                <tr>
                    <th scope="col" class="col-id">ID</th>
                    <th scope="col" class="col-code">Mã khách hàng</th>
                    <th scope="col" class="col-name">Họ tên</th>
                    <th scope="col" class="col-email">Email</th>
                    <th scope="col" class="col-phone">Số điện thoại</th>
                    <th scope="col" class="col-dob">Ngày sinh</th>
                    <th scope="col" class="col-gender">Giới tính</th>
                    <th scope="col" class="col-status">Trạng thái</th>
                    <th scope="col" class="col-action">Thao tác</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${not empty listKhachHang}">
                        <c:forEach var="kh" items="${listKhachHang}">
                            <tr data-customer-search-row>
                                <td><span class="category-id">#${kh.id}</span></td>
                                <td><strong style="color: #1f2937;"><c:out value="${kh.maKhachHang}" /></strong></td>
                                <td><strong><c:out value="${kh.hoTen}" /></strong></td>
                                <td title="${kh.email}"><c:out value="${empty kh.email ? '-' : kh.email}" /></td>
                                <td><c:out value="${empty kh.soDienThoai ? '-' : kh.soDienThoai}" /></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty kh.ngaySinh}"><c:out value="${kh.ngaySinh}" /></c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${kh.gioiTinh == 1}">Nam</c:when>
                                        <c:when test="${kh.gioiTinh == 0}">Nữ</c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${kh.trangThai == 1}">
                                            <span class="category-status status-active">Hoạt động</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="category-status status-inactive">Không hoạt động</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="action-buttons">
                                        <a class="btn-icon-circle btn-view"
                                           href="${pageContext.request.contextPath}/khach-hang/sua?id=${kh.id}"
                                           title="Sửa khách hàng">
                                            <i class="fas fa-pen"></i>
                                        </a>
                                        <a class="btn-icon-circle"
                                           href="${pageContext.request.contextPath}/dia-chi-khach-hang/hien-thi?idKhachHang=${kh.id}"
                                           title="Quản lý địa chỉ"
                                           style="background: #e0f2fe; color: #0284c7;">
                                            <i class="fas fa-location-dot"></i>
                                        </a>
                                        <form method="post"
                                              action="${pageContext.request.contextPath}/khach-hang/doi-trang-thai"
                                              style="display: inline;"
                                              data-customer-status-form>
                                            <input type="hidden" name="id" value="${kh.id}">
                                            <button type="submit"
                                                    class="btn-icon-circle"
                                                    data-current-status="${kh.trangThai}"
                                                    title="${kh.trangThai == 1 ? 'Ngừng hoạt động' : 'Kích hoạt'}"
                                                    style="border: none; cursor: pointer; background: ${kh.trangThai == 1 ? '#fee2e2' : '#dcfce7'}; color: ${kh.trangThai == 1 ? '#dc2626' : '#16a34a'};">
                                                <i class="fas ${kh.trangThai == 1 ? 'fa-ban' : 'fa-circle-check'}"></i>
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <tr class="customer-search-empty-row" data-customer-search-empty hidden>
                            <td colspan="9" style="text-align: center; padding: 30px; color: #888;">
                                <i class="fas fa-inbox" style="font-size: 24px; margin-bottom: 10px; display: block;"></i>
                                Không tìm thấy khách hàng phù hợp.
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="9" style="text-align: center; padding: 30px; color: #888;">
                                <i class="fas fa-inbox" style="font-size: 24px; margin-bottom: 10px; display: block;"></i>
                                Chưa có khách hàng nào.
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', () => {
        const toast = document.getElementById("toast-msg");
        if (toast) {
            setTimeout(function () {
                toast.style.opacity = "0";
                toast.style.transform = "translateY(-10px)";
                setTimeout(function () { toast.remove(); }, 500);
            }, 3000);
        }
    });
</script>
</body>
</html>