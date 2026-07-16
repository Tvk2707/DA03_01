<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "Kiểu quai kính");
    request.setAttribute("activeMenu", "attribute");
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
<%@ include file="../layout/sidebar.jsp" %>
<div class="dashboard-container">
    <%@ include file="../layout/header.jsp" %>
    <main class="category-section">
        <div class="category-header"><h1 class="category-title">Kiểu quai kính</h1></div>
        <form action="${pageContext.request.contextPath}/KieuQuaiKinh/${empty kieuQuaiKinh ? 'insert' : 'update'}" method="post" class="form-grid">
            <c:if test="${not empty kieuQuaiKinh}"><input type="hidden" name="id" value="${kieuQuaiKinh.id}"></c:if>
            <label><input type="checkbox" name="quaiThang" ${kieuQuaiKinh.quaiThang == 1 ? 'checked' : ''}> Quai thẳng</label>
            <label><input type="checkbox" name="quaiGap" ${kieuQuaiKinh.quaiGap == 1 ? 'checked' : ''}> Quai gập</label>
            <label><input type="checkbox" name="quaiLoxo" ${kieuQuaiKinh.quaiLoxo == 1 ? 'checked' : ''}> Quai lò xo</label>
            <button type="submit" class="add-new-btn">Lưu</button>
            <a class="btn-reset" href="${pageContext.request.contextPath}/KieuQuaiKinh">Đặt lại</a>
        </form>
        <table class="category-table"><thead><tr><th>ID</th><th>Kiểu quai</th><th>Trạng thái</th><th>Thao tác</th></tr></thead>
            <tbody><c:forEach var="item" items="${items}"><tr><td>${item.id}</td><td>
                <c:if test="${item.quaiThang == 1}">Quai thẳng </c:if><c:if test="${item.quaiGap == 1}">Quai gập </c:if><c:if test="${item.quaiLoxo == 1}">Quai lò xo</c:if>
            </td><td>${item.trangThai == 1 ? 'Hoạt động' : 'Ngừng hoạt động'}</td><td>
                <a href="${pageContext.request.contextPath}/KieuQuaiKinh/edit?id=${item.id}">Sửa</a>
                <a href="${pageContext.request.contextPath}/KieuQuaiKinh/delete?id=${item.id}" onclick="return confirm('Bạn có chắc muốn xóa không?')">Xóa</a>
            </td></tr></c:forEach></tbody>
        </table>
    </main>
</div>
</body>
</html>
