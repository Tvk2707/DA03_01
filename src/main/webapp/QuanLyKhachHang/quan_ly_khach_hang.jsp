<%@ page import="QuanLySanPham.Entity.KhachHang" %>
<%@ page import="java.util.List" %>
<%
    request.setAttribute("pageTitle", "Quản lý khách hàng");
    request.setAttribute("activeMenu", "khachhang");
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý khách hàng - RIOR Admin</title>

    <!-- FontAwesome từ CDN -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <!-- Đường dẫn bộ CSS tuyệt đối -->
    <link rel="stylesheet" href="<%= request.getContextPath() %>/Admin/css/layout.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/Admin/css/sidebar.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/Admin/css/header.css">

    <style>
        .customer-page .page-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            gap: 16px;
            margin-bottom: 24px;
        }

        .customer-page .page-title {
            font-size: 24px;
            font-weight: 700;
            color: var(--text-dark);
            margin-bottom: 4px;
        }

        .customer-page .page-subtitle {
            font-size: 14px;
            color: var(--text-light);
        }

        .customer-card {
            background-color: var(--white);
            border: 1px solid var(--border-color);
            border-radius: 12px;
            box-shadow: var(--shadow);
            padding: 24px;
            margin-bottom: 24px;
        }

        .customer-card h3 {
            font-size: 18px;
            color: var(--text-dark);
            margin-bottom: 18px;
        }

        .customer-form {
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

        .form-group input:focus,
        .form-group select:focus {
            border-color: var(--primary-color);
        }

        .form-actions {
            display: flex;
            align-items: end;
        }

        .btn-add,
        .customer-table a {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            border: none;
            border-radius: 8px;
            text-decoration: none;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
        }

        .btn-add {
            padding: 11px 18px;
            background-color: var(--primary-color);
            color: var(--white);
        }

        .customer-table a {
            padding: 8px 12px;
            border: 1px solid var(--primary-color);
            color: var(--primary-color);
            background-color: var(--white);
        }

        .action-links {
            display: flex;
            gap: 8px;
        }

        .table-wrap {
            overflow-x: auto;
        }

        .customer-table {
            width: 100%;
            border-collapse: collapse;
        }

        .customer-table th {
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

        .customer-table td {
            padding: 14px;
            font-size: 14px;
            color: var(--text-dark);
            border-bottom: 1px solid #f5f3ef;
            vertical-align: middle;
            white-space: nowrap;
        }

        .customer-table tr:hover {
            background-color: #faf8f4;
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
            .customer-form {
                grid-template-columns: repeat(2, 1fr);
            }
        }

        @media (max-width: 768px) {
            .customer-page .page-header {
                flex-direction: column;
            }

            .customer-form {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
<%@ include file="../Admin/layout/sidebar.jsp" %>

<div class="main-content">
    <%@ include file="../Admin/layout/header.jsp" %>

    <div id="page-content" class="customer-page">
        <%
            List<KhachHang> list = (List<KhachHang>) request.getAttribute("listKhachHang");
        %>

        <div class="page-header">
            <div>
                <h1 class="page-title">Quản lý khách hàng</h1>
                <p class="page-subtitle">Thêm và theo dõi trạng thái khách hàng</p>
            </div>
        </div>

        <div class="customer-card">
            <h3>Thêm khách hàng</h3>
            <form method="post" action="<%= request.getContextPath() %>/khach-hang/add" class="customer-form">
                <div class="form-group">
                    <label>Mã khách hàng</label>
                    <input type="text" name="maKhachHang" required>
                </div>
                <div class="form-group">
                    <label>Họ tên</label>
                    <input type="text" name="hoTen" required>
                </div>
                <div class="form-group">
                    <label>Email</label>
                    <input type="email" name="email">
                </div>
                <div class="form-group">
                    <label>Số điện thoại</label>
                    <input type="text" name="soDienThoai">
                </div>
                <div class="form-group">
                    <label>Mật khẩu</label>
                    <input type="password" name="matKhau">
                </div>
                <div class="form-group">
                    <label>Ngày sinh</label>
                    <input type="date" name="ngaySinh">
                </div>
                <div class="form-group">
                    <label>Giới tính</label>
                    <select name="gioiTinh">
                        <option value="">-- Chọn giới tính --</option>
                        <option value="1">Nam</option>
                        <option value="0">Nữ</option>
                    </select>
                </div>
                <div class="form-actions">
                    <button class="btn-add" type="submit">
                        <i class="fas fa-plus"></i>
                        <span>Thêm</span>
                    </button>
                </div>
            </form>
        </div>

        <div class="customer-card">
            <h3>Danh sách khách hàng</h3>
            <div class="table-wrap">
                <table class="customer-table">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Mã khách hàng</th>
                        <th>Họ tên</th>
                        <th>Email</th>
                        <th>Số điện thoại</th>
                        <th>Ngày sinh</th>
                        <th>Giới tính</th>
                        <th>Trạng thái</th>
                        <th>Hành động</th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                        if (list != null && !list.isEmpty()) {
                            for (KhachHang kh : list) {
                    %>
                    <tr>
                        <td><%= kh.getId() %></td>
                        <td><%= kh.getMaKhachHang() %></td>
                        <td><%= kh.getHoTen() %></td>
                        <td><%= kh.getEmail() == null ? "" : kh.getEmail() %></td>
                        <td><%= kh.getSoDienThoai() == null ? "" : kh.getSoDienThoai() %></td>
                        <td><%= kh.getNgaySinh() == null ? "" : kh.getNgaySinh() %></td>
                        <td>
                            <%
                                if (Integer.valueOf(1).equals(kh.getGioiTinh())) {
                                    out.print("Nam");
                                } else if (Integer.valueOf(0).equals(kh.getGioiTinh())) {
                                    out.print("Nữ");
                                } else {
                                    out.print("");
                                }
                            %>
                        </td>
                        <td>
                            <%
                                if (Integer.valueOf(1).equals(kh.getTrangThai())) {
                            %>
                            <span class="status status-active">Hoạt động</span>
                            <%
                            } else {
                            %>
                            <span class="status status-inactive">Ngừng hoạt động</span>
                            <%
                                }
                            %>
                        </td>
                        <td>
                            <div class="action-links">
                                <!-- Đã sửa chuẩn cú pháp gộp URL sạch sẽ -->
                                <a href="<%= request.getContextPath() %>/khach-hang/doi-trang-thai?id=<%= kh.getId() %>">Đổi trạng thái</a>
                                <a href="<%= request.getContextPath() %>/dia-chi-khach-hang/hien-thi?idKhachHang=<%= kh.getId() %>">Địa chỉ</a>
                            </div>
                        </td>
                    </tr>
                    <%
                        }
                    } else {
                    %>
                    <tr>
                        <td class="empty-row" colspan="9">Không có khách hàng</td>
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