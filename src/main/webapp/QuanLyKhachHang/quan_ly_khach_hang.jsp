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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/customer-management.css">
    <script src="${pageContext.request.contextPath}/Admin/js/customer-management.js" defer></script>
</head>
<body>
<%@ include file="../Admin/layout/sidebar.jsp" %>

<div class="main-content">
    <%@ include file="../Admin/layout/header.jsp" %>

    <main id="page-content" class="customer-page">
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

        <section class="customer-section" aria-labelledby="customer-page-title">
            <div class="customer-header">
                <div>
                    <h1 id="customer-page-title" class="customer-title">Quản lý khách hàng</h1>
                    <p class="customer-subtitle">Theo dõi thông tin và trạng thái của từng khách hàng.</p>
                </div>
            </div>

            <c:if test="${not empty successMessage}">
                <div class="customer-alert customer-alert-success" role="status">
                    <i class="fa-solid fa-circle-check" aria-hidden="true"></i>
                    <span><c:out value="${successMessage}" /></span>
                </div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="customer-alert customer-alert-error" role="alert">
                    <i class="fa-solid fa-circle-exclamation" aria-hidden="true"></i>
                    <span><c:out value="${errorMessage}" /></span>
                </div>
            </c:if>
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
                <section class="customer-card customer-form-card" aria-labelledby="customer-form-title">
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
                                        <span class="customer-status-badge customer-status-active">Hoạt động</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="customer-status-badge customer-status-inactive">Không hoạt động</span>
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
                                   class="customer-field-control"
                                   type="text"
                                   name="hoTen"
                                   value="<c:out value='${formHoTen}' />"
                                   maxlength="250"
                                   autocomplete="name"
                                   required
                                   aria-invalid="${not empty hoTenError ? 'true' : 'false'}"
                                   aria-describedby="customer-full-name-server-error customer-full-name-client-error">
                            <span id="customer-full-name-server-error" class="customer-field-error"><c:out value="${hoTenError}" /></span>
                            <span id="customer-full-name-client-error" class="customer-field-error" data-customer-client-error="hoTen" role="alert" hidden></span>
                        </div>

                        <div class="customer-field">
                            <label for="customer-email">Email</label>
                            <input id="customer-email"
                                   class="customer-field-control"
                                   type="email"
                                   name="email"
                                   value="<c:out value='${formEmail}' />"
                                   maxlength="150"
                                   autocomplete="email"
                                   aria-invalid="${not empty emailError ? 'true' : 'false'}"
                                   aria-describedby="customer-email-server-error customer-email-client-error">
                            <span id="customer-email-server-error" class="customer-field-error"><c:out value="${emailError}" /></span>
                            <span id="customer-email-client-error" class="customer-field-error" data-customer-client-error="email" role="alert" hidden></span>
                        </div>

                        <div class="customer-field">
                            <label for="customer-phone">Số điện thoại <span aria-hidden="true">*</span></label>
                            <input id="customer-phone"
                                   class="customer-field-control"
                                   type="tel"
                                   name="soDienThoai"
                                   value="<c:out value='${formSoDienThoai}' />"
                                   inputmode="numeric"
                                   maxlength="10"
                                   autocomplete="tel"
                                   required
                                   aria-invalid="${not empty soDienThoaiError ? 'true' : 'false'}"
                                   aria-describedby="customer-phone-server-error customer-phone-client-error">
                            <span id="customer-phone-server-error" class="customer-field-error"><c:out value="${soDienThoaiError}" /></span>
                            <span id="customer-phone-client-error" class="customer-field-error" data-customer-client-error="soDienThoai" role="alert" hidden></span>
                        </div>

                        <div class="customer-field">
                            <label for="customer-birth-date">Ngày sinh</label>
                            <input id="customer-birth-date"
                                   class="customer-field-control"
                                   type="date"
                                   name="ngaySinh"
                                   value="<c:out value='${formNgaySinh}' />"
                                   aria-invalid="${not empty ngaySinhError ? 'true' : 'false'}"
                                   aria-describedby="customer-birth-date-server-error customer-birth-date-client-error">
                            <span id="customer-birth-date-server-error" class="customer-field-error"><c:out value="${ngaySinhError}" /></span>
                            <span id="customer-birth-date-client-error" class="customer-field-error" data-customer-client-error="ngaySinh" role="alert" hidden></span>
                        </div>

                        <div class="customer-field">
                            <label for="customer-gender">Giới tính <span aria-hidden="true">*</span></label>
                            <select id="customer-gender"
                                    name="gioiTinh"
                                    class="customer-field-control"
                                    required
                                    aria-invalid="${not empty gioiTinhError ? 'true' : 'false'}"
                                    aria-describedby="customer-gender-server-error customer-gender-client-error">
                                <option value="">-- Chọn giới tính --</option>
                                <option value="1"
                                ${formGioiTinh == 1 ? 'selected' : ''}>
                                    Nam
                                </option>
                                <option value="0"
                                ${formGioiTinh == 0 ? 'selected' : ''}>
                                    Nữ
                                </option>
                            </select>
                            <span id="customer-gender-server-error" class="customer-field-error"><c:out value="${gioiTinhError}" /></span>
                            <span id="customer-gender-client-error" class="customer-field-error" data-customer-client-error="gioiTinh" role="alert" hidden></span>
                        </div>

                        <div class="customer-form-actions">
                            <button type="submit" class="customer-primary-button">
                                <i class="fas ${customerEditMode ? 'fa-floppy-disk' : 'fa-plus'}" aria-hidden="true"></i>
                                <span>${customerEditMode ? 'Cập nhật' : 'Thêm'}</span>
                            </button>
                            <button type="button"
                                    id="customer-close-form"
                                    class="customer-secondary-button">
                                Hủy
                            </button>
                        </div>
                    </form>
                </section>
            </div>


            <div class="customer-search-card">
                <form class="customer-search-form" data-customer-search-form role="search">
                    <label class="customer-visually-hidden" for="customer-search">Tìm kiếm khách hàng</label>
                    <div class="customer-search-input-wrapper">
                        <i class="fas fa-search customer-search-icon" aria-hidden="true"></i>
                        <input id="customer-search"
                               type="search"
                               placeholder="Tìm kiếm khách hàng..."
                               autocomplete="off"
                               data-customer-search-input>
                    </div>
                    <button type="submit" class="customer-search-btn">
                        <i class="fas fa-search" aria-hidden="true"></i>
                        Tìm kiếm
                    </button>
                    <button type="button" class="customer-clear-btn" data-clear-customer-search hidden>
                        <i class="fas fa-times" aria-hidden="true"></i>
                        Xóa bộ lọc
                    </button>
                </form>
                <div class="customer-add-form-action">
                    <button type="button"
                            id="customer-toggle-form"
                            class="customer-open-form-button customer-add-new-btn"
                            aria-expanded="${customerFormMustOpen ? 'true' : 'false'}">
                        <i class="fas fa-plus" aria-hidden="true"></i>
                        Thêm khách hàng
                    </button>
                </div>
            </div>

            <div class="customer-management-table-wrap"
                 tabindex="0"
                 role="region"
                 aria-label="Danh sách khách hàng">
                <table class="customer-management-table">
                    <thead>
                    <tr>
                        <th scope="col">ID</th>
                        <th scope="col">Mã khách hàng</th>
                        <th scope="col">Họ tên</th>
                        <th scope="col">Email</th>
                        <th scope="col">Số điện thoại</th>
                        <th scope="col">Ngày sinh</th>
                        <th scope="col">Giới tính</th>
                        <th scope="col">Trạng thái</th>
                        <th scope="col">Thao tác</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${not empty listKhachHang}">
                            <c:forEach var="kh" items="${listKhachHang}">
                                <tr data-customer-search-row>
                                    <td><span class="customer-id">#${kh.id}</span></td>
                                    <td><span class="customer-code"><c:out value="${kh.maKhachHang}" /></span></td>
                                    <td><span class="customer-name"><c:out value="${kh.hoTen}" /></span></td>
                                    <td><c:out value="${empty kh.email ? '-' : kh.email}" /></td>
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
                                                <span class="customer-status-badge customer-status-active">Hoạt động</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="customer-status-badge customer-status-inactive">Không hoạt động</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="customer-action-buttons">
                                            <a class="customer-action-btn customer-edit-btn"
                                               href="${pageContext.request.contextPath}/khach-hang/sua?id=${kh.id}"
                                               title="Sửa khách hàng"
                                               aria-label="Sửa khách hàng">
                                                <i class="fas fa-edit" aria-hidden="true"></i>
                                            </a>
                                            <a class="customer-action-btn customer-address-btn"
                                               href="${pageContext.request.contextPath}/dia-chi-khach-hang/hien-thi?idKhachHang=${kh.id}"
                                               title="Quản lý địa chỉ"
                                               aria-label="Quản lý địa chỉ">
                                                <i class="fas fa-location-dot" aria-hidden="true"></i>
                                            </a>
                                            <form method="post"
                                                  action="${pageContext.request.contextPath}/khach-hang/doi-trang-thai"
                                                  class="customer-action-form"
                                                  data-customer-status-form>
                                                <input type="hidden" name="id" value="${kh.id}">
                                                <button type="submit"
                                                        class="customer-action-btn customer-toggle-btn ${kh.trangThai == 1 ? 'customer-deactivate-btn' : 'customer-activate-btn'}"
                                                        data-current-status="${kh.trangThai}"
                                                        title="${kh.trangThai == 1 ? 'Ngừng hoạt động' : 'Kích hoạt'}"
                                                        aria-label="${kh.trangThai == 1 ? 'Ngừng hoạt động khách hàng' : 'Kích hoạt khách hàng'}">
                                                    <i class="fas ${kh.trangThai == 1 ? 'fa-ban' : 'fa-circle-check'}" aria-hidden="true"></i>
                                                </button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <tr class="customer-search-empty-row" data-customer-search-empty hidden>
                                <td colspan="9">
                                    <i class="fas fa-inbox" aria-hidden="true"></i>
                                    Không tìm thấy khách hàng phù hợp.
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td class="customer-empty-row" colspan="9">
                                    <i class="fas fa-inbox" aria-hidden="true"></i>
                                    Chưa có khách hàng nào.
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </section>

    </main>
</div>
</body>
</html>
