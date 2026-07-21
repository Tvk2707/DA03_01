<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:forEach var="sp" items="${sanPhams}">
    <div class="product-card" data-id="${sp.id}" onclick="themSanPham(currentInvoiceId, ${sp.id}, 1)">
        <img src="${pageContext.request.contextPath}/File_Anh/images/${sp.hinhAnh}"
             onerror="this.src='https://images.placeholders.dev/?width=150&height=120&text=No+Image'"
             alt="Product Image">
        <div class="product-name">${sp.sanPham.tenSanPham} - ${sp.mauSac.tenMau}</div>
        <div class="product-price">
            <fmt:formatNumber value="${sp.giaBan}" type="currency" currencySymbol="đ" minFractionDigits="0"/>
        </div>
    </div>
</c:forEach>

<c:if test="${empty sanPhams}">
    <div style="grid-column: 1 / -1; text-align: center; padding: 40px; color: #6b7280;">
        <i class="fas fa-search" style="font-size: 24px; margin-bottom: 10px;"></i>
        <p>Không tìm thấy sản phẩm nào.</p>
    </div>
</c:if>
