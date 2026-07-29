<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Hóa Đơn Bán Hàng</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sales.css">
    <style>
        body { font-family: 'Courier New', Courier, monospace; font-size: 10pt; margin: 0; padding: 20px; background: #f8f9fa; }
        .invoice-box { width: 100%; max-width: 80mm; margin: auto; padding: 5mm; background: white; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
        .header { text-align: center; }
        .header h1 { font-size: 14pt; margin: 0; }
        .header p { margin: 2px 0; }
        .info { margin-top: 10px; }
        .info p { margin: 2px 0; }
        table { width: 100%; border-collapse: collapse; margin-top: 10px; }
        th, td { border-bottom: 1px dashed #ccc; padding: 5px 0; }
        th { text-align: left; }
        .text-right { text-align: right; }
        .total { font-weight: bold; }
        .footer { text-align: center; margin-top: 20px; }
        .print-button {
            display: block;
            width: 80mm;
            margin: 20px auto;
            padding: 10px;
            background-color: #b4975a;
            color: white;
            text-align: center;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 12pt;
        }
    </style>
</head>
<body>

<button class="print-button no-print" onclick="window.print()">In Hóa Đơn</button>

<div class="invoice-box">
    <div class="header">
        <h1>RIOR - Fine Eyewear</h1>
        <p>Địa chỉ: 123 Đường ABC, Quận 1, TP. HCM</p>
        <p>Hotline: 0987.654.321</p>
        <h2>HÓA ĐƠN BÁN HÀNG</h2>
    </div>

    <div class="info">
        <p>Số HĐ: ${hoaDon.maHoaDon}</p>
        <p>Ngày: <fmt:formatDate value="${hoaDon.ngayTao}" pattern="dd/MM/yyyy HH:mm:ss"/></p>
        <p>Nhân viên: ${hoaDon.tenNhanVien}</p>
        <p>Khách hàng: ${not empty hoaDon.tenKhachHang ? hoaDon.tenKhachHang : 'Khách lẻ'}</p>
    </div>

    <table>
        <thead>
        <tr>
            <th>Sản phẩm</th>
            <th class="text-right">SL</th>
            <th class="text-right">Đơn giá</th>
            <th class="text-right">Thành tiền</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="item" items="${hoaDon.chiTiet}">
            <tr>
                <td colspan="4">${item.tenSanPham}</td>
            </tr>
            <tr>
                <td>(${item.mauSac} - ${item.kichCo})</td>
                <td class="text-right">${item.soLuong}</td>
                <td class="text-right"><fmt:formatNumber value="${item.donGia}" type="number"/></td>
                <td class="text-right"><fmt:formatNumber value="${item.thanhTien}" type="number"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <div class="info" style="margin-top: 15px;">
        <p class="total">Tạm tính: <span class="text-right"><fmt:formatNumber value="${hoaDon.tongTienHang}" type="number"/> đ</span></p>
        <p>Giảm giá: <span class="text-right">-<fmt:formatNumber value="${hoaDon.tienGiam}" type="number"/> đ</span></p>
        <p class="total">Tổng cộng: <span class="text-right"><fmt:formatNumber value="${hoaDon.tongThanhToan}" type="number"/> đ</span></p>
        <hr>
        <p>Tiền khách đưa: <span class="text-right"><fmt:formatNumber value="${hoaDon.tienKhachDua}" type="number"/> đ</span></p>
        <p>Tiền thối lại: <span class="text-right"><fmt:formatNumber value="${hoaDon.tienThoi}" type="number"/> đ</span></p>
    </div>

    <div class="footer">
        <p>Cảm ơn quý khách!</p>
    </div>
</div>
</body>
</html>
