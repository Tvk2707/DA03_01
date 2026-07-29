<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "Hình dạng gọng");
    request.setAttribute("activeSubMenu", "frame-shape");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hình dạng gọng</title>
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
            <h2 class="category-title">Hình dạng gọng</h2>
            <a class="add-new-btn" href="${pageContext.request.contextPath}/HinhDangGong/new">+ Thêm mới</a>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="error-message">${errorMessage}</div>
        </c:if>

        <c:if test="${not empty hinhDangGong}">
            <form action="${pageContext.request.contextPath}/HinhDangGong/${empty hinhDangGong.id ? 'insert' : 'update'}" method="post" class="form-grid">
                <c:if test="${not empty hinhDangGong.id}">
                    <input type="hidden" name="id" value="${hinhDangGong.id}">
                </c:if>
                <label>Hình dạng
                    <input type="text" name="hinhDang" value="${hinhDangGong.hinhDang}" required>
                </label>
                <button type="submit" class="add-new-btn">Lưu</button>
                <a class="btn-reset" href="${pageContext.request.contextPath}/HinhDangGong">Đặt lại</a>
            </form>
        </c:if>

        <table class="category-table">
            <thead><tr><th>ID</th><th>Hình dạng</th><th>Trạng thái</th><th>Thao tác</th></tr></thead>
            <tbody>
            <c:forEach var="item" items="${items}">
                <tr>
                    <td>${item.id}</td>
                    <td>${item.hinhDang}</td>
                    <td>${item.trangThai == 1 ? 'Hoạt động' : 'Ngừng hoạt động'}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/HinhDangGong/edit?id=${item.id}">Sửa</a>
                        <form action="${pageContext.request.contextPath}/HinhDangGong/delete" method="post" style="display:inline">
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
