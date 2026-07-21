<%
    request.setAttribute("pageTitle", "Thêm nhân viên");
    request.setAttribute("activeMenu", "employee");
    String actionAttr = (String) request.getAttribute("action");
    if (actionAttr == null) {
        actionAttr = request.getParameter("action");
    }
    boolean isEdit = "edit".equals(actionAttr);
    request.setAttribute("formActionUrl", isEdit ? "update" : "insert");
    request.setAttribute("pageTitleForm", isEdit ? "Cập nhật nhân viên" : "Thêm mới nhân viên");
    request.setAttribute("buttonTextForm", isEdit ? "Cập nhật" : "Thêm mới");
    request.setAttribute("isEdit", isEdit);
%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitleForm}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/danhmuc.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        .form-card {
            background: #fff;
            border-radius: 8px;
            padding: 24px;
            margin-bottom: 20px;
            border: 1px solid #f0ebe3;
            box-shadow: 0 2px 8px rgba(0,0,0,0.06);
        }

        .card-header {
            display: flex;
            align-items: center;
            margin-bottom: 20px;
            padding-bottom: 16px;
            border-bottom: 2px solid #b8956a;
        }

        .card-header i {
            font-size: 18px;
            color: #b8956a;
            margin-right: 10px;
        }

        .card-header h3 {
            margin: 0;
            color: #b8956a;
            font-size: 16px;
            font-weight: 600;
            letter-spacing: 0.5px;
        }

        .form-row {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 20px;
            margin-bottom: 20px;
        }

        .form-row.full {
            grid-template-columns: 1fr;
        }

        .form-group {
            display: flex;
            flex-direction: column;
        }

        .form-group label {
            font-weight: 600;
            color: #2c2c2c;
            font-size: 13px;
            margin-bottom: 8px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .form-group input,
        .form-group select,
        .form-group textarea {
            padding: 12px 14px;
            border: 1px solid #e5e7eb;
            border-radius: 6px;
            font-size: 14px;
            font-family: inherit;
            outline: none;
            transition: border-color 0.2s, box-shadow 0.2s;
        }

        .form-group input:focus,
        .form-group select:focus,
        .form-group textarea:focus {
            border-color: #b8956a;
            box-shadow: 0 0 0 3px rgba(184, 149, 106, 0.1);
        }

        .form-group input[readonly] {
            background-color: #f5f5f5;
            color: #999;
            cursor: not-allowed;
        }

        .password-wrapper {
            position: relative;
        }

        .password-wrapper input {
            width: 100%;
            padding-right: 42px;
            box-sizing: border-box;
        }

        .password-toggle-btn {
            position: absolute;
            top: 50%;
            right: 12px;
            transform: translateY(-50%);
            background: none;
            border: none;
            cursor: pointer;
            color: #999;
            font-size: 15px;
            padding: 4px;
            display: flex;
            align-items: center;
        }

        .password-toggle-btn:hover {
            color: #b8956a;
        }

        .form-actions {
            display: flex;
            justify-content: flex-end;
            gap: 12px;
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #f0ebe3;
        }

        .btn-cancel, .btn-submit {
            padding: 12px 28px;
            border-radius: 6px;
            font-weight: 600;
            font-size: 14px;
            border: none;
            cursor: pointer;
            transition: all 0.2s;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            text-decoration: none;
        }

        .btn-cancel {
            background-color: #f0ebe3;
            color: #4a4a4a;
        }

        .btn-cancel:hover {
            background-color: #e6dfd3;
        }

        .btn-submit {
            background-color: #b8956a;
            color: white;
        }

        .btn-submit:hover {
            background-color: #a38058;
        }

        .back-link {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 10px 16px;
            color: #b8956a;
            text-decoration: none;
            transition: all 0.2s;
        }

        .back-link:hover {
            color: #a38058;
        }

        .error-message {
            background: #fdecea;
            color: #b3261e;
            padding: 12px 16px;
            border-radius: 8px;
            margin-bottom: 16px;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .error-message i {
            font-size: 16px;
        }

        @media (max-width: 768px) {
            .form-row {
                grid-template-columns: 1fr;
            }

            .form-actions {
                flex-direction: column;
            }
        }
    </style>
</head>
<body>
<%@include file="../layout/sidebar.jsp" %>
<div class="dashboard-container">
    <%@include file="../layout/header.jsp" %>

    <div class="category-section">
        <div class="category-header">
            <h2 class="category-title">${pageTitleForm}</h2>
            <a href="${pageContext.request.contextPath}/NhanVien" class="back-link">
                <i class="fas fa-arrow-left"></i> Quay lại danh sách
            </a>
        </div>

        <c:if test="${not empty error}">
            <div class="error-message">
                <i class="fas fa-circle-exclamation"></i> ${error}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/NhanVien/${formActionUrl}" method="post" id="nhanVienForm">

            <c:if test="${isEdit}">
                <input type="hidden" name="id" value="${nhanVien.id}">
            </c:if>

            <!-- SECTION 1: THÔNG TIN CÁ NHÂN -->
            <div class="form-card">
                <div class="card-header">
                    <i class="fas fa-user"></i>
                    <h3>Thông tin cá nhân</h3>
                </div>

                <div class="form-row full">
                    <div class="form-group">
                        <label>Mã nhân viên <span style="color:#d32f2f;">*</span></label>
                        <input type="text" name="maNhanVien" value="${nhanVien.maNhanVien}" ${isEdit ? 'readonly' : ''} required>
                    </div>
                </div>

                <div class="form-row full">
                    <div class="form-group">
                        <label>Họ và tên <span style="color:#d32f2f;">*</span></label>
                        <input type="text" name="hoTen" value="${nhanVien.hoTen}" required>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label>Chức vụ</label>
                        <input type="text" name="chucVu" value="${nhanVien.chucVu}">
                    </div>
                    <div class="form-group">
                        <label>Giới tính</label>
                        <select name="gioiTinh">
                            <option value="">-- Chọn --</option>
                            <option value="1" ${nhanVien.gioiTinh == 1 ? 'selected' : ''}>Nam</option>
                            <option value="0" ${nhanVien.gioiTinh == 0 ? 'selected' : ''}>Nữ</option>
                        </select>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label>Số điện thoại</label>
                        <input type="text" name="soDienThoai" value="${nhanVien.soDienThoai}">
                    </div>
                    <div class="form-group">
                        <label>Email liên hệ</label>
                        <input type="email" name="email" value="${nhanVien.email}">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label>Ngày sinh</label>
                        <input type="date" name="ngaySinh" value="${nhanVien.ngaySinh}">
                    </div>
                    <div class="form-group">
                        <label>Mật khẩu</label>
                        <div class="password-wrapper">
                            <input type="password" name="matKhau" id="matKhauInput" value="${nhanVien.matKhau}">
                            <button type="button" class="password-toggle-btn" id="toggleMatKhauBtn" onclick="toggleMatKhau()" title="Hiện/Ẩn mật khẩu">
                                <i class="fas fa-eye" id="toggleMatKhauIcon"></i>
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- SECTION 2: ĐỊA CHỈ LIÊN HỆ -->
            <div class="form-card">
                <div class="card-header">
                    <i class="fas fa-map-marker-alt"></i>
                    <h3>Địa chỉ liên hệ</h3>
                </div>

                <div class="form-row full">
                    <div class="form-group">
                        <label>Địa chỉ (Tỉnh/Thành phố)</label>
                        <select name="diaChi">
                            <option value="">-- Chọn tỉnh/thành phố --</option>
                            <option value="Tuyên Quang" ${nhanVien.diaChi == 'Tuyên Quang' ? 'selected' : ''}>Tuyên Quang</option>
                            <option value="Cao Bằng" ${nhanVien.diaChi == 'Cao Bằng' ? 'selected' : ''}>Cao Bằng</option>
                            <option value="Lai Châu" ${nhanVien.diaChi == 'Lai Châu' ? 'selected' : ''}>Lai Châu</option>
                            <option value="Lào Cai" ${nhanVien.diaChi == 'Lào Cai' ? 'selected' : ''}>Lào Cai</option>
                            <option value="Thái Nguyên" ${nhanVien.diaChi == 'Thái Nguyên' ? 'selected' : ''}>Thái Nguyên</option>
                            <option value="Điện Biên" ${nhanVien.diaChi == 'Điện Biên' ? 'selected' : ''}>Điện Biên</option>
                            <option value="Lạng Sơn" ${nhanVien.diaChi == 'Lạng Sơn' ? 'selected' : ''}>Lạng Sơn</option>
                            <option value="Sơn La" ${nhanVien.diaChi == 'Sơn La' ? 'selected' : ''}>Sơn La</option>
                            <option value="Phú Thọ" ${nhanVien.diaChi == 'Phú Thọ' ? 'selected' : ''}>Phú Thọ</option>
                            <option value="TP. Hà Nội" ${nhanVien.diaChi == 'TP. Hà Nội' ? 'selected' : ''}>TP. Hà Nội</option>
                            <option value="TP. Hải Phòng" ${nhanVien.diaChi == 'TP. Hải Phòng' ? 'selected' : ''}>TP. Hải Phòng</option>
                            <option value="Bắc Ninh" ${nhanVien.diaChi == 'Bắc Ninh' ? 'selected' : ''}>Bắc Ninh</option>
                            <option value="Quảng Ninh" ${nhanVien.diaChi == 'Quảng Ninh' ? 'selected' : ''}>Quảng Ninh</option>
                            <option value="Hưng Yên" ${nhanVien.diaChi == 'Hưng Yên' ? 'selected' : ''}>Hưng Yên</option>
                            <option value="Ninh Bình" ${nhanVien.diaChi == 'Ninh Bình' ? 'selected' : ''}>Ninh Bình</option>
                            <option value="Thanh Hóa" ${nhanVien.diaChi == 'Thanh Hóa' ? 'selected' : ''}>Thanh Hóa</option>
                            <option value="Nghệ An" ${nhanVien.diaChi == 'Nghệ An' ? 'selected' : ''}>Nghệ An</option>
                            <option value="Hà Tĩnh" ${nhanVien.diaChi == 'Hà Tĩnh' ? 'selected' : ''}>Hà Tĩnh</option>
                            <option value="Quảng Trị" ${nhanVien.diaChi == 'Quảng Trị' ? 'selected' : ''}>Quảng Trị</option>
                            <option value="TP. Huế" ${nhanVien.diaChi == 'TP. Huế' ? 'selected' : ''}>TP. Huế</option>
                            <option value="TP. Đà Nẵng" ${nhanVien.diaChi == 'TP. Đà Nẵng' ? 'selected' : ''}>TP. Đà Nẵng</option>
                            <option value="Quảng Ngãi" ${nhanVien.diaChi == 'Quảng Ngãi' ? 'selected' : ''}>Quảng Ngãi</option>
                            <option value="Gia Lai" ${nhanVien.diaChi == 'Gia Lai' ? 'selected' : ''}>Gia Lai</option>
                            <option value="Đắk Lắk" ${nhanVien.diaChi == 'Đắk Lắk' ? 'selected' : ''}>Đắk Lắk</option>
                            <option value="Khánh Hòa" ${nhanVien.diaChi == 'Khánh Hòa' ? 'selected' : ''}>Khánh Hòa</option>
                            <option value="Lâm Đồng" ${nhanVien.diaChi == 'Lâm Đồng' ? 'selected' : ''}>Lâm Đồng</option>
                            <option value="Đồng Nai" ${nhanVien.diaChi == 'Đồng Nai' ? 'selected' : ''}>Đồng Nai</option>
                            <option value="Tây Ninh" ${nhanVien.diaChi == 'Tây Ninh' ? 'selected' : ''}>Tây Ninh</option>
                            <option value="TP. Hồ Chí Minh" ${nhanVien.diaChi == 'TP. Hồ Chí Minh' ? 'selected' : ''}>TP. Hồ Chí Minh</option>
                            <option value="Đồng Tháp" ${nhanVien.diaChi == 'Đồng Tháp' ? 'selected' : ''}>Đồng Tháp</option>
                            <option value="An Giang" ${nhanVien.diaChi == 'An Giang' ? 'selected' : ''}>An Giang</option>
                            <option value="Vĩnh Long" ${nhanVien.diaChi == 'Vĩnh Long' ? 'selected' : ''}>Vĩnh Long</option>
                            <option value="TP. Cần Thơ" ${nhanVien.diaChi == 'TP. Cần Thơ' ? 'selected' : ''}>TP. Cần Thơ</option>
                            <option value="Cà Mau" ${nhanVien.diaChi == 'Cà Mau' ? 'selected' : ''}>Cà Mau</option>
                        </select>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label>Trạng thái</label>
                        <input type="text" value="Hoạt động" readonly style="background-color:#f5f5f5;color:#4caf50;font-weight:600;cursor:not-allowed;">
                        <input type="hidden" name="trangThai" value="1">
                    </div>
                </div>
            </div>

            <!-- FORM ACTIONS -->
            <div class="form-actions">
                <a href="${pageContext.request.contextPath}/NhanVien" class="btn-cancel">
                    <i class="fas fa-times"></i> Hủy
                </a>
                <button type="submit" class="btn-submit">
                    <i class="fas fa-check"></i> ${buttonTextForm}
                </button>
            </div>

        </form>

    </div>
</div>

<script>
    function toggleMatKhau() {
        var input = document.getElementById('matKhauInput');
        var icon = document.getElementById('toggleMatKhauIcon');
        if (input.type === 'password') {
            input.type = 'text';
            icon.classList.remove('fa-eye');
            icon.classList.add('fa-eye-slash');
        } else {
            input.type = 'password';
            icon.classList.remove('fa-eye-slash');
            icon.classList.add('fa-eye');
        }
    }
</script>

</body>
</html>
