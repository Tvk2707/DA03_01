<%
    request.setAttribute("pageTitle", "Quản lý nhân viên");
    request.setAttribute("activeMenu", "employee");
%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý nhân viên</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/danhmuc.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<%@ include file="../layout/sidebar.jsp" %>
<div class="dashboard-container">
    <%@ include file="../layout/header.jsp" %>

    <div class="category-section">

        <!-- Filter Section moved to top as requested -->
        <div class="filter-top" style="margin-bottom:20px;display:flex;align-items:flex-start;gap:16px;">
            <form action="${pageContext.request.contextPath}/NhanVien/search" method="post" id="filterForm" style="flex:1;">
                <div style="display:flex;gap:16px;align-items:center;">
                    <div style="flex:1;">
                        <label class="filter-label">Tìm kiếm</label>
                        <input type="text" name="hoTen" value="${param.hoTen}" class="filter-input" placeholder="Tên, mã, email...">
                    </div>

                    <div style="width:260px;">
                        <label class="filter-label">Trạng thái</label>
                        <select class="filter-select" name="trangThai">
                            <option value="">Tất cả</option>
                            <option value="1">Đang hoạt động</option>
                            <option value="0">Ngừng hoạt động</option>
                        </select>
                    </div>

                    <div style="width:220px;">
                        <label class="filter-label">Giới tính</label>
                        <div style="display:flex;align-items:center;gap:12px;padding-top:6px;">
                            <label style="font-size:13px;"><input type="radio" name="gioiTinh" value="" checked> Tất cả</label>
                            <label style="font-size:13px;"><input type="radio" name="gioiTinh" value="1"> Nam</label>
                            <label style="font-size:13px;"><input type="radio" name="gioiTinh" value="0"> Nữ</label>
                        </div>
                    </div>

                    <div style="display:flex;align-items:flex-end;gap:12px;">
                        <button type="submit" class="add-new-btn" style="padding:10px 18px;">
                            <i class="fas fa-search"></i> Tìm kiếm
                        </button>
                        <button type="button" class="add-new-btn" style="padding:10px 18px;background-color:#f0ebe3;color:#4a4a4a;" onmouseover="this.style.backgroundColor='#e6dfd3';" onmouseout="this.style.backgroundColor='#f0ebe3';" onclick="document.getElementById('filterForm').reset(); window.location='${pageContext.request.contextPath}/NhanVien';">Xóa bộ lọc</button>
                    </div>
                </div>
            </form>

            <!-- action buttons removed from top-right here and placed below search -->
        </div>

        <!-- Action buttons placed below the search (aligned right) -->
        <div style="display:flex;justify-content:flex-end;margin-bottom:12px;">
            <div style="display:flex;gap:12px;align-items:center;">
                <a href="${pageContext.request.contextPath}/NhanVien/new" class="top-action" title="Thêm">
                    <i class="fas fa-plus"></i>
                </a>
                <a href="#" class="top-action" title="Xuất">
                    <i class="fas fa-file-export"></i>
                </a>
                <a href="#" class="top-action" title="In">
                    <i class="fas fa-print"></i>
                </a>
                <a href="#" class="top-action" title="Làm mới" onclick="window.location='${pageContext.request.contextPath}/NhanVien'">
                    <i class="fas fa-sync-alt"></i>
                </a>
            </div>
        </div>

        <div class="category-header">
            <h2 class="category-title">Quản lý nhân viên</h2>
            <div style="display:flex;gap:12px;align-items:center;">
                <a href="${pageContext.request.contextPath}/NhanVien" class="btn-reset" title="Refresh" style="text-decoration:none;display:inline-flex;align-items:center;justify-content:center;width:40px;height:40px;border-radius:50%;background:#f0f0f0;color:#666;cursor:pointer;transition:all 0.2s;">
                    <i class="fas fa-redo" style="font-size:16px;"></i>
                </a>
            </div>
        </div>

        <c:if test="${not empty success || param.success == '1'}">
            <div id="successBanner" style="background:#eaf7ed;color:#1e7e34;padding:12px 16px;border-radius:8px;margin-bottom:16px;display:flex;align-items:center;gap:8px;">
                <i class="fas fa-circle-check"></i>
                <c:choose>
                    <c:when test="${not empty success}">${success}</c:when>
                    <c:otherwise>Thêm nhân viên thành công!</c:otherwise>
                </c:choose>
            </div>
            <script>
                setTimeout(function() {
                    var el = document.getElementById('successBanner');
                    if (el) {
                        el.style.transition = 'opacity 0.4s';
                        el.style.opacity = '0';
                        setTimeout(function() { el.remove(); }, 400);
                    }
                }, 3000);
            </script>
        </c:if>

        <c:if test="${not empty error}">
            <div style="background:#fdecea;color:#b3261e;padding:12px 16px;border-radius:8px;margin-bottom:16px;">
                <i class="fas fa-circle-exclamation"></i> ${error}
            </div>
        </c:if>

        <!-- Table Section -->
        <div class="nv-table-wrapper">
        <table class="nv-table">
            <thead>
            <tr>
                <th>STT</th>
                <th>MÃ NV</th>
                <th>TÊN NHÂN VIÊN</th>
                <th>CHỨC VỤ</th>
                <th>GIỚI TÍNH</th>
                <th>SĐT</th>
                <th>EMAIL</th>
                <th>ĐỊA CHỈ</th>
                <th>TRẠNG THÁI</th>
                <th>HÀNH ĐỘNG</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="nv" items="${items}" varStatus="status">
                <tr>
                    <td>${status.index + 1 + (currentPage - 1) * 10}</td>
                    <td>${nv.maNhanVien}</td>
                    <td class="truncate" title="${nv.hoTen}"><strong>${nv.hoTen}</strong></td>
                    <td class="truncate" title="${nv.chucVu}">${nv.chucVu}</td>
                    <td>${nv.gioiTinh == 1 ? 'Nam' : 'Nữ'}</td>
                    <td class="truncate">
                        <c:if test="${not empty nv.soDienThoai}">
                            <i class="fas fa-phone" style="color:#999;margin-right:4px;"></i>${nv.soDienThoai}
                        </c:if>
                    </td>
                    <td class="truncate" title="${nv.email}">
                        <c:if test="${not empty nv.email}">
                            <i class="fas fa-envelope" style="color:#999;margin-right:4px;"></i>${nv.email}
                        </c:if>
                    </td>
                    <td class="truncate" title="${nv.diaChi}">${nv.diaChi}</td>
                    <td>
                        <c:if test="${nv.trangThai == 1}">
                            <span style="display:inline-block;width:12px;height:12px;background:#4caf50;border-radius:50%;margin-right:6px;vertical-align:middle;"></span><span>Đang hoạt động</span>
                        </c:if>
                        <c:if test="${nv.trangThai != 1}">
                            <span style="display:inline-block;width:12px;height:12px;background:#ccc;border-radius:50%;margin-right:6px;vertical-align:middle;"></span><span>Ngừng hoạt động</span>
                        </c:if>
                    </td>
                    <td>
                        <div style="display:flex;gap:6px;align-items:center;justify-content:flex-end;flex-wrap:nowrap;">
                            <a href="${pageContext.request.contextPath}/NhanVien/edit?id=${nv.id}"
                               class="nv-action-btn edit"
                               title="Sửa"
                               style="text-decoration:none;display:inline-flex;align-items:center;justify-content:center;width:28px;height:28px;border-radius:6px;background:#fff;color:#666;border:1px solid #e6e6e6;cursor:pointer;transition:all 0.15s;flex-shrink:0;">
                                <i class="fas fa-edit" style="font-size:12px;"></i>
                            </a>

                            <form action="${pageContext.request.contextPath}/NhanVien/delete" method="post"
                                  style="display:inline;"
                                  onsubmit="return confirm('Bạn có chắc chắn muốn xóa nhân viên này?');">
                                <input type="hidden" name="id" value="${nv.id}">
                                <button type="submit"
                                        class="nv-action-btn delete"
                                        title="Xóa"
                                        style="border:none;background:#fff;color:#666;cursor:pointer;width:28px;height:28px;border-radius:6px;border:1px solid #e6e6e6;display:inline-flex;align-items:center;justify-content:center;transition:all 0.15s;flex-shrink:0;">
                                    <i class="fas fa-trash-alt" style="font-size:12px;"></i>
                                </button>
                            </form>

                            <!-- Toggle switch (UI only) -->
                            <label class="switch" title="Trạng thái" style="flex-shrink:0;">
                                <input type="checkbox" ${nv.trangThai == 1 ? 'checked' : ''} disabled>
                                <span class="slider"></span>
                            </label>
                        </div>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty items}">
                <tr>
                    <td colspan="10" style="text-align: center; padding: 50px; color: #999;">
                        <i class="fas fa-inbox" style="font-size: 48px; margin-bottom: 16px; display: block; color:#ddd;"></i>
                        <strong>Không tìm thấy dữ liệu nào</strong>
                    </td>
                </tr>
            </c:if>
            </tbody>
        </table>
        </div>

        <style>
            .dashboard-container {
                width: 100% !important;
                max-width: 100% !important;
                box-sizing: border-box;
            }

            .category-section {
                width: 100% !important;
                max-width: 100% !important;
                box-sizing: border-box;
                padding-left: 0 !important;
                padding-right: 0 !important;
            }

            .filter-label {
                display: block;
                margin-bottom: 6px;
                font-size: 13px;
                font-weight: 600;
                color: #4a4a4a;
            }

            .filter-input,
            .filter-select {
                width: 100%;
                padding: 10px 14px;
                border: 1px solid #ddd;
                border-radius: 8px;
                font-size: 14px;
                color: #333;
                background: #fff;
                box-sizing: border-box;
                transition: border-color 0.2s, box-shadow 0.2s;
            }

            .filter-input:focus,
            .filter-select:focus {
                outline: none;
                border-color: #a38058;
                box-shadow: 0 0 0 3px rgba(163, 128, 88, 0.15);
            }

            .filter-select {
                cursor: pointer;
                appearance: none;
                -webkit-appearance: none;
                background-image: url("data:image/svg+xml;charset=UTF-8,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23999' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E");
                background-repeat: no-repeat;
                background-position: right 12px center;
                background-size: 16px;
                padding-right: 36px;
            }

            .add-new-btn {
                display: inline-flex;
                align-items: center;
                gap: 8px;
                border: none;
                border-radius: 8px;
                background: #a38058;
                color: #fff;
                font-size: 14px;
                font-weight: 600;
                cursor: pointer;
                transition: background 0.2s;
            }

            .add-new-btn:hover {
                background: #8b6744;
            }

            .nv-table-wrapper {
                width: 100%;
                max-width: 100%;
                overflow-x: hidden;
                border-radius: 8px;
                box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                box-sizing: border-box;
            }

            .nv-table {
                width: 100%;
                table-layout: fixed;
                border-collapse: collapse;
                background: white;
                border-radius: 8px;
                overflow: hidden;
            }

            .nv-table thead {
                background: #a38058;
                color: white;
            }

            .nv-table thead th {
                padding: 12px 8px;
                text-align: left;
                font-weight: 600;
                font-size: 12px;
                letter-spacing: 0.3px;
                border-bottom: 2px solid #8b6744;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }

            .nv-table tbody td {
                padding: 12px 8px;
                border-bottom: 1px solid #f0f0f0;
                font-size: 13px;
                color: #333;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }

            .nv-table tbody td.truncate {
                max-width: 0;
            }

            .nv-table th:nth-child(1), .nv-table td:nth-child(1) { width: 5%; }
            .nv-table th:nth-child(2), .nv-table td:nth-child(2) { width: 8%; }
            .nv-table th:nth-child(3), .nv-table td:nth-child(3) { width: 13%; }
            .nv-table th:nth-child(4), .nv-table td:nth-child(4) { width: 10%; }
            .nv-table th:nth-child(5), .nv-table td:nth-child(5) { width: 7%; }
            .nv-table th:nth-child(6), .nv-table td:nth-child(6) { width: 11%; }
            .nv-table th:nth-child(7), .nv-table td:nth-child(7) { width: 16%; }
            .nv-table th:nth-child(8), .nv-table td:nth-child(8) { width: 10%; }
            .nv-table th:nth-child(9), .nv-table td:nth-child(9) { width: 10%; }
            .nv-table th:nth-child(10), .nv-table td:nth-child(10) { width: 10%; }

            .nv-table tbody tr:hover {
                background: #fafafa;
            }

            .nv-table tbody tr:last-child td {
                border-bottom: none;
            }

            .nv-action-btn:hover {
                background: #e0e0e0 !important;
            }

            .nv-action-btn.delete:hover {
                background: #ffe0e0 !important;
                color: #8b4513 !important;
            }
            /* Toggle switch style */
            .switch { position: relative; display: inline-block; width:38px; height:22px; }
            .switch input { opacity: 0; width: 0; height: 0; }
            .slider { position: absolute; cursor: pointer; top: 0; left: 0; right: 0; bottom: 0; background-color: #ccc; transition: .2s; border-radius: 22px; }
            .slider:before { position: absolute; content: ""; height: 16px; width: 16px; left: 3px; bottom: 3px; background-color: white; transition: .2s; border-radius: 50%; }
            input:checked + .slider { background-color: #a38058; }
            input:checked + .slider:before { transform: translateX(16px); }

            /* Style for action buttons (Thêm/Xuất/In/Làm mới) */
            .top-action {
                display: inline-flex;
                width: 48px;
                height: 48px;
                border-radius: 8px;
                align-items: center;
                justify-content: center;
                background: #b8956a;
                color: white;
                text-decoration: none;
                transition: background 0.2s;
            }
            .top-action:hover {
                background: #a38058;
            }
        </style>

        <c:if test="${totalPages > 1}">
            <div style="display:flex;justify-content:space-between;align-items:center;margin-top:20px;padding:16px;background:#fafafa;border-radius:8px;">
                <div style="font-size:14px;color:#666;">
                    Hiển thị <strong>${items.size()}</strong> trong tổng cộ <strong>${totalCount}</strong> bản ghi
                </div>
                <div class="pagination" style="display:flex;gap:6px;align-items:center;">
                    <c:forEach begin="1" end="${totalPages}" var="p">
                        <a href="${pageContext.request.contextPath}/NhanVien?page=${p}"
                           style="padding:6px 10px;border-radius:4px;border:1px solid #ddd;text-decoration:none;font-size:13px;
                                   color:${p == currentPage ? '#fff' : '#333'};
                                   background:${p == currentPage ? '#a38058' : '#fff'};
                                   ${p == currentPage ? 'color:#fff;' : ''}">
                            ${p}
                        </a>
                    </c:forEach>
                </div>
            </div>
        </c:if>

    </div>
</div>
</body>
</html>
