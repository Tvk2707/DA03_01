<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "Kiểu quai kính");
    request.setAttribute("activeSubMenu", "temple");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kiểu quai kính</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/FE/Admin/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/FE/Admin/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/FE/Admin/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/FE/Admin/css/danhmuc.css">
</head>
<body>
<%@include file="../../FE/Admin/layout/sidebar.jsp"%>
<div class="dashboard-container">
    <%@include file="../../FE/Admin/layout/header.jsp"%>
    <main class="category-section">
        <div class="category-header">
            <h2 class="category-title">Kiểu quai kính</h2>
            <a class="add-new-btn" href="${pageContext.request.contextPath}/KieuQuaiKinh/new">+ Thêm mới</a>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="error-message">${errorMessage}</div>
        </c:if>

        <c:if test="${not empty kieuQuaiKinh}">
            <form action="${pageContext.request.contextPath}/KieuQuaiKinh/${empty kieuQuaiKinh.id ? 'insert' : 'update'}" method="post" class="form-grid">
                <c:if test="${not empty kieuQuaiKinh.id}">
                    <input type="hidden" name="id" value="${kieuQuaiKinh.id}">
                </c:if>
                <label>Kiểu quai kính
                    <input type="text" name="tenKieuQuaiKinh" value="${kieuQuaiKinh.kieuQuai}" required>
                </label>
                <button type="submit" class="add-new-btn">Lưu</button>
                <a class="btn-reset" href="${pageContext.request.contextPath}/KieuQuaiKinh">Đặt lại</a>
            </form>
        </c:if>

        <table class="category-table">
            <thead><tr><th>ID</th><th>Kiểu quai kính</th><th>Trạng thái</th><th>Thao tác</th></tr></thead>
            <tbody>
            <c:forEach var="item" items="${items}">
                <tr>
                    <td>${item.id}</td>
                    <td>${item.kieuQuai}</td>
                    <td>${item.trangThai == 1 ? 'Hoạt động' : 'Ngừng hoạt động'}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/KieuQuaiKinh/edit?id=${item.id}">Sửa</a>
                        <form action="${pageContext.request.contextPath}/KieuQuaiKinh/delete" method="post" style="display:inline">
                            <input type="hidden" name="id" value="${item.id}">
                            <button type="submit" onclick="return confirm('Bạn có chắc muốn xóa không?')">Xóa</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </main>
</div>
</body>
</html>
