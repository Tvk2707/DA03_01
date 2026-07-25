<%@ page import="QuanLySanPham.Entity.DiaChiKhachHang" %>
<%@ page import="java.util.List" %>
<%
    request.setAttribute("pageTitle", "Quản lý địa chỉ khách hàng");
    request.setAttribute("activeMenu", "customer");
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý địa chỉ khách hàng</title>

    <!-- FontAwesome từ CDN -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <!-- Sửa đường dẫn CSS tuyệt đối dùng getContextPath() -->
    <link rel="stylesheet" href="<%= request.getContextPath() %>/Admin/css/layout.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/Admin/css/sidebar.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/Admin/css/header.css">

    <style>
        .address-page .page-title {
            font-size: 24px;
            font-weight: 700;
            color: var(--text-dark);
            margin-bottom: 16px;
        }

        .address-card {
            background-color: var(--white);
            border: 1px solid var(--border-color);
            border-radius: 12px;
            box-shadow: var(--shadow);
            padding: 24px;
            margin-bottom: 24px;
        }

        .address-card h3 {
            font-size: 18px;
            color: var(--text-dark);
            margin-bottom: 18px;
        }

        .address-form {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 16px;
        }

        .form-group label {
            display: block;
            font-size: 13px;
            font-weight: 600;
            color: var(--text-dark);
            margin-bottom: 6px;
        }

        .form-group input,
        .form-group select {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid var(--border-color);
            border-radius: 8px;
            background-color: var(--white);
            font-size: 14px;
            outline: none;
        }

        .btn,
        .address-table a {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 9px 14px;
            border-radius: 8px;
            text-decoration: none;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
        }

        .btn-primary {
            border: none;
            background-color: var(--primary-color);
            color: var(--white);
        }

        .btn-outline,
        .address-table a {
            border: 1px solid var(--primary-color);
            background-color: var(--white);
            color: var(--primary-color);
        }

        .form-actions {
            display: flex;
            align-items: end;
        }

        .table-wrap {
            overflow-x: auto;
        }

        .address-table {
            width: 100%;
            border-collapse: collapse;
        }

        .address-table th {
            text-align: left;
            padding: 12px 14px;
            font-size: 12px;
            font-weight: 700;
            color: var(--text-light);
            text-transform: uppercase;
            background-color: #faf8f4;
            border-bottom: 1px solid var(--border-color);
            white-space: nowrap;
        }

        .address-table td {
            padding: 14px;
            font-size: 14px;
            color: var(--text-dark);
            border-bottom: 1px solid #f5f3ef;
            vertical-align: middle;
            white-space: nowrap;
        }

        .status {
            display: inline-block;
            padding: 4px 10px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 700;
        }

        .status-active {
            background-color: #e8f5e9;
            color: #2e7d32;
        }

        .status-inactive {
            background-color: #fff3e0;
            color: #e65100;
        }

        .empty-row {
            text-align: center;
            color: var(--text-light);
        }

        @media (max-width: 1024px) {
            .address-form {
                grid-template-columns: repeat(2, 1fr);
            }
        }

        @media (max-width: 768px) {
            .address-form {
                grid-template-columns: 1fr;
            }
        }
    </style>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/Admin/css/customer-management.css">
</head>
<body>
<%@ include file="../Admin/layout/sidebar.jsp" %>

<div class="dashboard-container">
    <%@ include file="../Admin/layout/header.jsp" %>

    <div class="category-section address-page">
        <%
            List<DiaChiKhachHang> listDiaChi = (List<DiaChiKhachHang>) request.getAttribute("listDiaChi");
            Integer idKhachHang = (Integer) request.getAttribute("idKhachHang");
        %>

        <div class="address-page-heading">
            <h2 class="category-title">Quản lý địa chỉ khách hàng</h2>
            <a class="btn btn-outline" href="<%= request.getContextPath() %>/khach-hang/hien-thi">
                <i class="fas fa-arrow-left"></i> Quay lại danh sách khách hàng
            </a>
        </div>

        <div class="address-card">
            <h3>Thêm địa chỉ</h3>
            <form method="post" action="<%= request.getContextPath() %>/dia-chi-khach-hang/add" class="address-form">
                <input type="hidden" name="idKhachHang" value="<%= idKhachHang %>">

                <div class="form-group">
                    <label>Tên người nhận</label>
                    <input type="text" name="tenNguoiNhan" required>
                </div>
                <div class="form-group">
                    <label>SĐT người nhận</label>
                    <input type="text" name="sdtNguoiNhan" required>
                </div>
                <div class="form-group">
                    <label>Tỉnh thành</label>
                    <input type="text" name="tinhThanh" required>
                </div>
                <div class="form-group">
                    <label>Quận huyện</label>
                    <input type="text" name="quanHuyen" required>
                </div>
                <div class="form-group">
                    <label>Phường xã</label>
                    <input type="text" name="phuongXa" required>
                </div>
                <div class="form-group">
                    <label>Địa chỉ cụ thể</label>
                    <input type="text" name="diaChiCuThe" required>
                </div>
                <div class="form-group">
                    <label>Loại địa chỉ</label>
                    <select name="loaiDiaChi">
                        <option value="">-- Chọn loại địa chỉ --</option>
                        <option value="1">Nhà riêng</option>
                        <option value="2">Công ty</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Mặc định</label>
                    <select name="isMacDinh">
                        <option value="0">Không</option>
                        <option value="1">Có</option>
                    </select>
                </div>
                <div class="form-actions">
                    <button class="btn btn-primary" type="submit">
                        <i class="fas fa-plus"></i> Thêm
                    </button>
                </div>
            </form>
        </div>

        <div class="address-card">
            <h3>Danh sách địa chỉ</h3>
            <div class="table-wrap">
                <table class="address-table">
                    <thead>
                    <tr>
                        <th>STT</th>
                        <th>Tên người nhận</th>
                        <th>SĐT người nhận</th>
                        <th>Tỉnh thành</th>
                        <th>Quận huyện</th>
                        <th>Phường xã</th>
                        <th>Địa chỉ cụ thể</th>
                        <th>Loại địa chỉ</th>
                        <th>Mặc định</th>
                        <th>Thao tác</th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                        if (listDiaChi != null && !listDiaChi.isEmpty()) {
                            int stt = 1;
                            for (DiaChiKhachHang dc : listDiaChi) {
                    %>
                    <tr>
                        <td><%= stt++ %></td>
                        <td><%= dc.getTenNguoiNhan() %></td>
                        <td><%= dc.getSdtNguoiNhan() %></td>
                        <td><%= dc.getTinhThanh() %></td>
                        <td><%= dc.getQuanHuyen() %></td>
                        <td><%= dc.getPhuongXa() %></td>
                        <td><%= dc.getDiaChiCuThe() %></td>
                        <td>
                            <%
                                if (Integer.valueOf(1).equals(dc.getLoaiDiaChi())) {
                                    out.print("Nhà riêng");
                                } else if (Integer.valueOf(2).equals(dc.getLoaiDiaChi())) {
                                    out.print("Công ty");
                                } else {
                                    out.print("");
                                }
                            %>
                        </td>
                        <td>
                            <%
                                if (Integer.valueOf(1).equals(dc.getIsMacDinh())) {
                            %>
                            <span class="status status-active">Mặc định</span>
                            <%
                            } else {
                            %>
                            <span class="status status-inactive">Không</span>
                            <%
                                }
                            %>
                        </td>
                        <td>
                            <% if (!Integer.valueOf(1).equals(dc.getIsMacDinh())) { %>
                                <a class="address-action"
                                   href="<%= request.getContextPath() %>/dia-chi-khach-hang/dat-mac-dinh?idDiaChi=<%= dc.getId() %>&idKhachHang=<%= idKhachHang %>">
                                    <i class="fas fa-check"></i> Đặt mặc định
                                </a>
                            <% } else { %>
                                <span aria-label="Địa chỉ đang được chọn">—</span>
                            <% } %>
                        </td>
                    </tr>
                    <%
                        }
                    } else {
                    %>
                    <tr>
                        <td class="empty-row" colspan="10">Không có địa chỉ</td>
                    </tr>
                    <%
                        }
                    %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
</body>
</html>
