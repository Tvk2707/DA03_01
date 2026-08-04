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

        /* Dấu trường bắt buộc */
        .required { color: #c0392b; margin-left: 2px; }

        /* Thông báo lỗi validate phía client */
        .error-msg {
            display: block;
            font-size: 12px;
            color: #c0392b;
            margin-top: 4px;
            min-height: 16px;
        }

        /* Select bị vô hiệu hoá khi chưa chọn tỉnh */
        .form-group select:disabled {
            background-color: #f5f5f5;
            cursor: not-allowed;
            opacity: 0.7;
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
            <form method="post" action="<%= request.getContextPath() %>/dia-chi-khach-hang/add"
                  class="address-form" id="formThemDiaChi" novalidate>
                <input type="hidden" name="idKhachHang" value="<%= idKhachHang %>">

                <%-- Hidden inputs: lưu tên tỉnh và tên phường/xã để gửi lên server --%>
                <input type="hidden" name="tinhThanh"    id="hiddenTinhThanh">
                <input type="hidden" name="provinceCode" id="hiddenProvinceCode">
                <input type="hidden" name="phuongXa"     id="hiddenPhuongXa">
                <input type="hidden" name="wardCode"     id="hiddenWardCode">

                <div class="form-group">
                    <label>Tên người nhận <span class="required">*</span></label>
                    <input type="text" name="tenNguoiNhan" id="tenNguoiNhan"
                           placeholder="Nhập tên người nhận">
                    <span class="error-msg" id="errTenNguoiNhan"></span>
                </div>

                <div class="form-group">
                    <label>SĐT người nhận <span class="required">*</span></label>
                    <input type="text" name="sdtNguoiNhan" id="sdtNguoiNhan"
                           placeholder="Nhập số điện thoại (10 chữ số)">
                    <span class="error-msg" id="errSdtNguoiNhan"></span>
                </div>

                <div class="form-group">
                    <label>Tỉnh/Thành phố <span class="required">*</span></label>
                    <select id="selectTinhThanh">
                        <option value="">-- Đang tải... --</option>
                    </select>
                    <span class="error-msg" id="errTinhThanh"></span>
                </div>

                <div class="form-group">
                    <label>Phường/Xã <span class="required">*</span></label>
                    <select id="selectPhuongXa" disabled>
                        <option value="">-- Chọn tỉnh trước --</option>
                    </select>
                    <span class="error-msg" id="errPhuongXa"></span>
                </div>

                <div class="form-group">
                    <label>Địa chỉ cụ thể <span class="required">*</span></label>
                    <input type="text" name="diaChiCuThe" id="diaChiCuThe"
                           placeholder="Số nhà, tên đường...">
                    <span class="error-msg" id="errDiaChiCuThe"></span>
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
                        <th>Tỉnh/Thành phố</th>
                        <th>Phường/Xã</th>
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
                        <td class="empty-row" colspan="9">Không có địa chỉ</td>
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
<script>
    /**
     * ============================================================
     * Quản lý địa chỉ khách hàng – tích hợp API provinces v2
     * API: https://provinces.open-api.vn/api/v2
     * Luồng: tải tỉnh → chọn tỉnh → tải phường/xã → submit form
     * ============================================================
     */
    (function () {
        var BASE_API = 'https://provinces.open-api.vn/api/v2';

        // --- Tham chiếu các phần tử DOM ---
        var selTinh        = document.getElementById('selectTinhThanh');
        var selPhuong      = document.getElementById('selectPhuongXa');
        var hiddenTinh     = document.getElementById('hiddenTinhThanh');
        var hiddenProvCode = document.getElementById('hiddenProvinceCode');
        var hiddenPhuong   = document.getElementById('hiddenPhuongXa');
        var hiddenWardCode = document.getElementById('hiddenWardCode');
        var form           = document.getElementById('formThemDiaChi');

        // --- Tải danh sách tỉnh/thành phố khi trang load ---
        function loadProvinces() {
            fetch(BASE_API + '/p/')
                .then(function (res) {
                    if (!res.ok) throw new Error('HTTP ' + res.status);
                    return res.json();
                })
                .then(function (provinces) {
                    selTinh.innerHTML = '<option value="">-- Chọn tỉnh/thành phố --</option>';
                    provinces.forEach(function (p) {
                        var opt = document.createElement('option');
                        opt.value = p.code;
                        opt.textContent = p.name;
                        selTinh.appendChild(opt);
                    });
                })
                .catch(function (err) {
                    console.error('[DiaChi] Lỗi tải danh sách tỉnh:', err);
                    selTinh.innerHTML = '<option value="">-- Không tải được dữ liệu --</option>';
                });
        }

        // --- Tải danh sách phường/xã theo mã tỉnh ---
        function loadWards(provinceCode) {
            selPhuong.innerHTML = '<option value="">-- Đang tải... --</option>';
            selPhuong.disabled = true;

            // depth=2: trả về tỉnh + phường/xã trực tiếp (cơ cấu hành chính 2 cấp)
            fetch(BASE_API + '/p/' + provinceCode + '?depth=2')
                .then(function (res) {
                    if (!res.ok) throw new Error('HTTP ' + res.status);
                    return res.json();
                })
                .then(function (data) {
                    // API v2 có thể trả về "wards" hoặc "communes" tuỳ phiên bản
                    var wards = data.wards || data.communes || [];

                    if (wards.length === 0) {
                        selPhuong.innerHTML = '<option value="">-- Không có dữ liệu --</option>';
                        return;
                    }

                    selPhuong.innerHTML = '<option value="">-- Chọn phường/xã --</option>';
                    wards.forEach(function (w) {
                        var opt = document.createElement('option');
                        opt.value = w.code;
                        opt.textContent = w.name;
                        selPhuong.appendChild(opt);
                    });
                    selPhuong.disabled = false;
                })
                .catch(function (err) {
                    console.error('[DiaChi] Lỗi tải danh sách phường/xã:', err);
                    selPhuong.innerHTML = '<option value="">-- Không tải được dữ liệu --</option>';
                });
        }

        // --- Sự kiện: người dùng chọn tỉnh/thành phố ---
        selTinh.addEventListener('change', function () {
            var code = this.value;
            var name = code ? this.options[this.selectedIndex].text : '';

            // Reset phường/xã trước khi tải mới
            hiddenPhuong.value   = '';
            hiddenWardCode.value = '';

            if (code) {
                hiddenTinh.value     = name;
                hiddenProvCode.value = code;
                loadWards(code);
            } else {
                hiddenTinh.value     = '';
                hiddenProvCode.value = '';
                selPhuong.innerHTML  = '<option value="">-- Chọn tỉnh trước --</option>';
                selPhuong.disabled   = true;
            }
        });

        // --- Sự kiện: người dùng chọn phường/xã ---
        selPhuong.addEventListener('change', function () {
            var code = this.value;
            hiddenWardCode.value = code;
            hiddenPhuong.value   = code ? this.options[this.selectedIndex].text : '';
        });

        // --- Hiển thị / xoá thông báo lỗi ---
        function showError(id, msg) {
            var el = document.getElementById(id);
            if (el) el.textContent = msg;
        }
        function clearErrors() {
            ['errTenNguoiNhan', 'errSdtNguoiNhan', 'errTinhThanh', 'errPhuongXa', 'errDiaChiCuThe']
                .forEach(function (id) { showError(id, ''); });
        }

        // --- Validate trước khi submit ---
        form.addEventListener('submit', function (e) {
            e.preventDefault();
            clearErrors();

            var valid  = true;
            var ten    = document.getElementById('tenNguoiNhan').value.trim();
            var sdt    = document.getElementById('sdtNguoiNhan').value.trim();
            var tinh   = hiddenProvCode.value;
            var phuong = hiddenWardCode.value;
            var diaChi = document.getElementById('diaChiCuThe').value.trim();

            // Validate tên người nhận
            if (!ten) {
                showError('errTenNguoiNhan', 'Vui lòng nhập tên người nhận');
                valid = false;
            }

            // Validate số điện thoại: đúng 10 chữ số
            if (!sdt) {
                showError('errSdtNguoiNhan', 'Vui lòng nhập số điện thoại');
                valid = false;
            } else if (!/^[0-9]{10}$/.test(sdt)) {
                showError('errSdtNguoiNhan', 'Số điện thoại phải gồm đúng 10 chữ số');
                valid = false;
            }

            // Validate tỉnh/thành phố
            if (!tinh) {
                showError('errTinhThanh', 'Vui lòng chọn tỉnh/thành phố');
                valid = false;
            }

            // Validate phường/xã
            if (!phuong) {
                showError('errPhuongXa', 'Vui lòng chọn phường/xã');
                valid = false;
            }

            // Validate địa chỉ cụ thể
            if (!diaChi) {
                showError('errDiaChiCuThe', 'Vui lòng nhập địa chỉ cụ thể');
                valid = false;
            }

            if (valid) {
                // Tất cả hợp lệ → thực sự gửi form
                form.submit();
            }
        });

        // --- Khởi động: tải danh sách tỉnh ngay khi trang mở ---
        loadProvinces();

    })();
</script>
</body>
</html>
