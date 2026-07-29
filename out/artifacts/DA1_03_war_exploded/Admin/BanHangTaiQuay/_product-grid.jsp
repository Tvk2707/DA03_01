<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div class="product-table-wrap">
    <table class="product-table">
        <thead>
            <tr>
                <th>Sản phẩm</th>
                <th>Thuộc tính và tồn kho</th>
                <th>Giá bán</th>
                <th>Thêm</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${empty sanPhams}">
                    <tr>
                        <td class="product-table__empty" colspan="4">
                            <i class="fas fa-box-open" aria-hidden="true"></i>
                            Không tìm thấy sản phẩm nào phù hợp!
                        </td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="sp" items="${sanPhams}">
                        <tr class="p-card" data-spct="${sp.id}">
                            <td>
                                <div class="product-table__product">
                                    <div class="p-thumb">
                                        <c:choose>
                                            <c:when test="${not empty sp.hinhAnhHienThi && sp.hinhAnhHienThi != 'null'}">
                                                <img src="${pageContext.request.contextPath}/${sp.hinhAnhHienThi}" alt="${sp.sanPham.tenSanPham}" onerror="this.style.display='none';"/>
                                            </c:when>
                                            <c:otherwise>
                                                <svg width="34" height="34" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"><circle cx="6.5" cy="12" r="3.2"/><circle cx="17.5" cy="12" r="3.2"/><path d="M9.7 12h4.6M3 12l-1.5-1M21 12l1.5-1"/></svg>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div>
                                        <div class="p-name" title="${sp.sanPham.tenSanPham} (SKU: ${sp.ma})">${sp.sanPham.tenSanPham}</div>
                                        <div class="p-sku" title="SKU: ${sp.ma}">SKU: ${sp.ma}</div>
                                    </div>
                                </div>
                            </td>
                            <td>
                                <div class="p-meta ${sp.soLuongTon <= 3 ? 'stock-low' : ''}" data-tonkho title="Còn ${sp.soLuongTon} · ${sp.mauSac.tenMau} - ${sp.kichCo.tenKichCo}">
                                    Còn ${sp.soLuongTon} · ${sp.mauSac.tenMau} - ${sp.kichCo.tenKichCo}
                                </div>
                            </td>
                            <td>
                                <div class="p-bottom">
                                    <div class="p-price"><fmt:formatNumber value="${sp.giaBan}" pattern="#,##0"/> đ</div>
                                </div>
                            </td>
                            <td style="text-align: center;">
                                <button type="button" class="p-add" ${sp.soLuongTon <= 0 ? 'data-disabled="true"' : ''}
                                        title="Thêm vào giỏ" aria-label="Thêm ${sp.sanPham.tenSanPham} vào giỏ">+</button>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
</div>
