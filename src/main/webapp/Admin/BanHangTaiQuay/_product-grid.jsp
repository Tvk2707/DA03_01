<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%-- 1. Nếu không tìm thấy sản phẩm nào --%>
<c:if test="${empty sanPhams}">
    <div style="grid-column: span 3; text-align: center; padding: 40px 0; color: var(--text-sub); font-size: 14px;">
        <i class="fas fa-box-open" style="font-size: 24px; margin-bottom: 10px; color: #ccc; display: block;"></i>
        Không tìm thấy sản phẩm nào phù hợp!
    </div>
</c:if>

<%-- 2. Vòng lặp in ra danh sách sản phẩm tìm được --%>
<c:forEach var="sp" items="${sanPhams}">
    <div class="p-card" data-spct="${sp.id}">

            <%-- Khung chứa ảnh sản phẩm --%>
        <div class="p-thumb">
            <c:choose>
                <c:when test="${not empty sp.hinhAnh && sp.hinhAnh != 'null'}">
                    <img src="${pageContext.request.contextPath}/File_Anh/images/${sp.hinhAnh}" style="width: 100%; height: 100%; object-fit: cover; border-radius: 9px;" onerror="this.style.display='none';"/>
                </c:when>
                <c:otherwise>
                    <svg width="34" height="34" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><circle cx="6.5" cy="12" r="3.2"/><circle cx="17.5" cy="12" r="3.2"/><path d="M9.7 12h4.6M3 12l-1.5-1M21 12l1.5-1"/></svg>
                </c:otherwise>
            </c:choose>
        </div>

            <%-- Tên sản phẩm + Mã biến thể --%>
        <div class="p-name">${sp.sanPham.tenSanPham} (${sp.ma})</div>

            <%-- Số lượng và Thuộc tính (Màu, Size) --%>
        <div class="p-meta ${sp.soLuongTon <= 3 ? 'stock-low' : ''}">
            Còn ${sp.soLuongTon} · ${sp.mauSac.tenMau} - ${sp.kichCo.tenKichCo}
        </div>

            <%-- Giá bán và nút Thêm --%>
        <div class="p-bottom">
            <div class="p-price"><fmt:formatNumber value="${sp.giaBan}" pattern="#,##0"/> đ</div>
            <div class="p-add" ${sp.soLuongTon <= 0 ? 'data-disabled="true"' : ''} title="Thêm vào giỏ">+</div>
        </div>

    </div>
</c:forEach>