<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 7/20/2024
  Time: 10:00 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
    request.setAttribute("pageTitle", "Bán hàng tại quầy");
    request.setAttribute("activeMenu", "pos");
%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bán Hàng Tại Quầy</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/FE/Admin/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/FE/Admin/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/FE/Admin/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sales.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<div class="admin-layout">
    <jsp:include page="/Admin/layout/sidebar.jsp"/>
    <div class="main-content">
        <jsp:include page="/Admin/layout/header.jsp"/>
        <div class="page-content" style="padding: 20px;">
            <div class="pos-layout">
                <div class="left-panel">
                    <div class="filter-section" style="margin-bottom: 16px;">
                        <input type="text" class="filter-input" placeholder="Tìm kiếm sản phẩm..." oninput="timSanPham(this.value)">
                    </div>
                    <div id="product-grid-container" class="product-grid">
                        <%-- Product cards will be loaded here via JS --%>
                    </div>
                </div>
                <div class="right-panel">
                    <div class="cart-header">
                        <h3 style="margin: 0;">Hóa đơn #<span id="currentInvoiceId"></span></h3>
                        <button onclick="huyHoaDon(currentInvoiceId)">Hủy</button>
                    </div>
                    <div id="cart-items-container" class="cart-items">
                        <%-- Cart items will be rendered here --%>
                    </div>
                    <div class="cart-footer">
                        <div class="summary-row">
                            <span>Khách hàng:</span>
                            <button onclick="document.getElementById('customerModal').style.display='block'">Chọn</button>
                        </div>
                        <div class="summary-row">
                            <span>Voucher:</span>
                            <input type="text" id="voucherInput" placeholder="Nhập mã voucher">
                            <button onclick="apVoucher(currentInvoiceId, document.getElementById('voucherInput').value)">Áp dụng</button>
                        </div>
                        <div class="summary-row">
                            <span>Tạm tính</span>
                            <span id="subtotalAmount">0 đ</span>
                        </div>
                        <div class="summary-row">
                            <span>Giảm giá</span>
                            <span id="discountAmount">0 đ</span>
                        </div>
                        <div class="summary-row" style="font-weight: 700; font-size: 18px;">
                            <span>Tổng cộng</span>
                            <span id="totalAmount">0 đ</span>
                        </div>
                        <button class="checkout-btn" onclick="document.getElementById('paymentModal').style.display='block'">Thanh toán</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="chon-khach-hang.jsp"/>
<jsp:include page="xac-nhan-thanh-toan.jsp"/>
<div id="invoice-print-area"></div>
<div id="toast-container"></div>

<script src="${pageContext.request.contextPath}/assets/js/ban-hang.js"></script>
<script>
    let currentInvoiceId = null;

    document.addEventListener('DOMContentLoaded', async () => {
        const hoaDonCho = await layHoaDonCho(); // Assuming this function is in ban-hang.js
        if (hoaDonCho && hoaDonCho.length > 0) {
            // Logic to render tabs for waiting invoices
        } else {
            const newInvoice = await taoHoaDon();
            if (newInvoice) {
                currentInvoiceId = newInvoice.id;
                document.getElementById('currentInvoiceId').innerText = newInvoice.maHoaDon;
            }
        }
        timSanPham(''); // Initial product load
    });
</script>
</body>
</html>
