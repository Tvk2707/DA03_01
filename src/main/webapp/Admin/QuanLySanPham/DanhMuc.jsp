<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Set activeMenu cho sidebar
    request.setAttribute("activeMenu", "product");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Danh mục - RIOR Admin</title>

    <!-- CSS Sidebar & Header (đã có) -->
    <link rel="stylesheet" href="../css/sidebar.css">
    <link rel="stylesheet" href="../css/header.css">
    <link rel="stylesheet" href="../css/layout.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <!-- CSS Main Content -->
    <link rel="stylesheet" href="../css/quanlydanhmuc.css">
</head>
<body>
<!-- Include Sidebar -->
<%@ include file="../layout/sidebar.jsp" %>

<!-- Main Wrapper -->
<div class="main-wrapper">
    <!-- Include Header -->
    <%@ include file="../layout/header.jsp" %>

    <!-- Main Content -->
    <main class="main-content">
        <!-- Page Header -->
        <div class="page-header">
            <div>
                <h1 class="page-title">Quản lý Danh mục</h1>
                <p class="page-subtitle">Quản lý danh mục sản phẩm kính mắt</p>
            </div>
            <button class="btn-add" onclick="openModal()">
                <i class="fas fa-plus"></i>
                <span>Thêm danh mục</span>
            </button>
        </div>

        <!-- Card Container -->
        <div class="card-custom">
            <!-- Card Header -->
            <div class="card-header-custom">
                <h3 class="card-header-title">Danh sách danh mục</h3>
                <div class="stats-row">
                    <span>Tổng: <strong id="totalCount">0</strong></span>
                    <span>Hoạt động: <strong id="activeCount">0</strong></span>
                </div>
            </div>

            <!-- Toolbar -->
            <div class="toolbar">
                <div class="search-box">
                    <input type="text" placeholder="Tìm kiếm danh mục..." id="searchInput">
                    <i class="fas fa-search"></i>
                </div>
                <select class="filter-select" id="statusFilter">
                    <option value="">Tất cả trạng thái</option>
                    <option value="active">Hoạt động</option>
                    <option value="inactive">Không hoạt động</option>
                </select>
            </div>

            <!-- Table -->
            <div class="table-wrapper">
                <table class="table-custom">
                    <thead>
                    <tr>
                        <th style="width: 80px;">STT</th>
                        <th>Mã danh mục</th>
                        <th>Tên danh mục</th>
                        <th style="width: 150px;">Trạng thái</th>
                        <th style="width: 120px;">Thao tác</th>
                    </tr>
                    </thead>
                    <tbody id="tableBody">
                    <!-- Dữ liệu sẽ được render bằng JavaScript, lấy từ Servlet/SQL -->
                    </tbody>
                </table>
            </div>

            <!-- Pagination -->
            <div class="pagination-custom">
                <div class="pagination-info">
                    Hiển thị <strong id="startCount">1</strong> - <strong id="endCount">10</strong>
                    của <strong id="totalItems">0</strong> kết quả
                </div>
                <div class="pagination-controls">
                    <button class="page-btn" id="prevBtn" onclick="changePage(-1)">
                        <i class="fas fa-chevron-left"></i>
                    </button>
                    <div id="pageNumbers"></div>
                    <button class="page-btn" id="nextBtn" onclick="changePage(1)">
                        <i class="fas fa-chevron-right"></i>
                    </button>
                </div>
            </div>
        </div>
    </main>
</div>

<!-- Modal Thêm/Sửa -->
<div class="modal-overlay" id="modal">
    <div class="modal-content">
        <div class="modal-header-custom">
            <h3 class="modal-title" id="modalTitle">Thêm danh mục</h3>
            <button class="modal-close" onclick="closeModal()">
                <i class="fas fa-times"></i>
            </button>
        </div>
        <div class="modal-body">
            <form id="form">
                <div class="form-group">
                    <label class="form-label required">Mã danh mục</label>
                    <input type="text" class="form-input" id="code" required placeholder="Nhập mã danh mục">
                </div>
                <div class="form-group">
                    <label class="form-label required">Tên danh mục</label>
                    <input type="text" class="form-input" id="name" required placeholder="Nhập tên danh mục">
                </div>
                <div class="form-group">
                    <label class="form-label">Trạng thái</label>
                    <select class="form-select" id="status">
                        <option value="active">Hoạt động</option>
                        <option value="inactive">Không hoạt động</option>
                    </select>
                </div>
            </form>
        </div>
        <div class="modal-footer-custom">
            <button class="btn-cancel" onclick="closeModal()">Hủy</button>
            <button class="btn-save" onclick="saveData()">
                <i class="fas fa-save"></i> Lưu
            </button>
        </div>
    </div>
</div>

<!-- Modal Xác nhận Xóa -->
<div class="modal-overlay confirm-dialog" id="confirmModal">
    <div class="modal-content">
        <div class="modal-body" style="padding: 32px 24px;">
            <div class="confirm-icon">
                <i class="fas fa-trash-alt"></i>
            </div>
            <p class="confirm-message">Bạn có chắc chắn muốn xóa danh mục này?</p>
            <div style="display: flex; gap: 12px; justify-content: center;">
                <button class="btn-cancel" onclick="closeConfirmModal()">Hủy</button>
                <button class="btn-confirm-delete" onclick="confirmDelete()">
                    <i class="fas fa-trash"></i> Xóa
                </button>
            </div>
        </div>
    </div>
</div>

<!-- JavaScript -->
<script>
    // ==========================================================
    // [DB-CONNECT] CẤU HÌNH ĐƯỜNG DẪN SERVLET
    // Đổi lại đường dẫn cho đúng với @WebServlet bạn khai báo phía Java.
    // Gợi ý servlet: DanhMucServlet xử lý các action: list, add, update, delete
    // ==========================================================
    const API_URL = "${pageContext.request.contextPath}/admin/danhmuc";
    // Ví dụ Servlet map: @WebServlet("/admin/danhmuc")
    // doGet  -> action=list   : trả về JSON toàn bộ danh sách (hoặc có phân trang/search phía server)
    // doPost -> action=add    : thêm mới (nhận code, name, status)
    // doPost -> action=update : sửa (nhận id, code, name, status)
    // doPost -> action=delete : xóa (nhận id)

    // Data management
    let currentPage = 1;
    const itemsPerPage = 10;
    let dataList = [];      // Danh sách đang hiển thị (sau khi search/filter)
    let fullDataList = [];  // Toàn bộ danh sách gốc lấy từ SQL
    let editingId = null;
    let deletingId = null;

    // Initialize
    document.addEventListener('DOMContentLoaded', function() {
        loadDataFromServer();
    });

    // ==========================================================
    // [DB-CONNECT] LẤY DANH SÁCH DANH MỤC TỪ DATABASE
    // Servlet cần trả về JSON dạng:
    // [ { "id":1, "code":"DM001", "name":"Kính cận", "status":"active" }, ... ]
    // Trong Servlet: DAO.getAllDanhMuc() -> chạy SELECT * FROM danh_muc -> convert sang JSON (Gson/Jackson)
    // ==========================================================
    function loadDataFromServer() {
        fetch(`${API_URL}?action=list`)
            .then(response => {
                if (!response.ok) throw new Error('Lỗi khi tải dữ liệu danh mục');
                return response.json();
            })
            .then(data => {
                fullDataList = data;
                dataList = [...fullDataList];
                currentPage = 1;
                renderTable();
                updateStats();
            })
            .catch(error => {
                console.error(error);
                alert('Không thể tải danh sách danh mục từ máy chủ!');
            });
    }

    // Render table
    function renderTable() {
        const tbody = document.getElementById('tableBody');
        const start = (currentPage - 1) * itemsPerPage;
        const end = start + itemsPerPage;
        const pageData = dataList.slice(start, end);

        tbody.innerHTML = pageData.map((item, index) => `
                <tr>
                    <td class="stt-cell">${start + index + 1}</td>
                    <td class="code-cell">${item.code}</td>
                    <td>${item.name}</td>
                    <td>
                        <span class="status-badge ${item.status}">
                            ${item.status =='active' ? 'Hoạt động' : 'Không hoạt động'}
                        </span>
                    </td>
                    <td>
                        <div class="action-btns">
                            <button class="btn-action edit" onclick="editItem(${item.id})" title="Sửa">
                                <i class="fas fa-pen"></i>
                            </button>
                            <button class="btn-action delete" onclick="deleteItem(${item.id})" title="Xóa">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            `).join('');

        renderPagination();
        updatePaginationInfo();
    }

    // Render pagination
    function renderPagination() {
        const totalPages = Math.ceil(dataList.length / itemsPerPage) || 1;
        const pageNumbers = document.getElementById('pageNumbers');

        let html = '';
        for (let i = 1; i <= totalPages; i++) {
            html += `
                    <button class="page-btn ${i == currentPage ? 'active' : ''}" onclick="goToPage(${i})">
                        ${i}
                    </button>
                `;
        }
        pageNumbers.innerHTML = html;

        document.getElementById('prevBtn').disabled = currentPage === 1;
        document.getElementById('nextBtn').disabled = currentPage === totalPages;
    }

    // Update pagination info
    function updatePaginationInfo() {
        const start = (currentPage - 1) * itemsPerPage + 1;
        const end = Math.min(currentPage * itemsPerPage, dataList.length);

        document.getElementById('startCount').textContent = dataList.length > 0 ? start : 0;
        document.getElementById('endCount').textContent = end;
        document.getElementById('totalItems').textContent = dataList.length;
    }

    // Update stats
    function updateStats() {
        const activeCount = fullDataList.filter(item => item.status === 'active').length;
        document.getElementById('totalCount').textContent = fullDataList.length;
        document.getElementById('activeCount').textContent = activeCount;
    }

    // Change page
    function changePage(direction) {
        currentPage += direction;
        renderTable();
    }

    // Go to specific page
    function goToPage(page) {
        currentPage = page;
        renderTable();
    }

    // Open modal
    function openModal() {
        editingId = null;
        document.getElementById('modalTitle').textContent = 'Thêm danh mục';
        document.getElementById('form').reset();
        document.getElementById('modal').classList.add('active');
    }

    // Close modal
    function closeModal() {
        document.getElementById('modal').classList.remove('active');
    }

    // Edit item
    function editItem(id) {
        const item = fullDataList.find(i => i.id === id);
        if (!item) return;
        editingId = id;

        document.getElementById('modalTitle').textContent = 'Sửa danh mục';
        document.getElementById('code').value = item.code;
        document.getElementById('name').value = item.name;
        document.getElementById('status').value = item.status;

        document.getElementById('modal').classList.add('active');
    }

    // Delete item
    function deleteItem(id) {
        deletingId = id;
        document.getElementById('confirmModal').classList.add('active');
    }

    // Close confirm modal
    function closeConfirmModal() {
        document.getElementById('confirmModal').classList.remove('active');
        deletingId = null;
    }

    // ==========================================================
    // [DB-CONNECT] XÓA DANH MỤC TRONG DATABASE
    // Servlet nhận action=delete, id=... -> DAO.deleteDanhMuc(id)
    // -> chạy DELETE FROM danh_muc WHERE id = ?
    // (Nếu có ràng buộc khóa ngoại với bảng sản phẩm thì kiểm tra trước khi xóa)
    // ==========================================================
    function confirmDelete() {
        if (!deletingId) return;

        const formData = new URLSearchParams();
        formData.append('action', 'delete');
        formData.append('id', deletingId);

        fetch(API_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: formData
        })
            .then(response => {
                if (!response.ok) throw new Error('Xóa thất bại');
                return response.json(); // Servlet nên trả về { "success": true }
            })
            .then(result => {
                if (result.success) {
                    closeConfirmModal();
                    loadDataFromServer(); // Tải lại danh sách mới nhất từ DB
                } else {
                    alert(result.message || 'Xóa danh mục thất bại!');
                }
            })
            .catch(error => {
                console.error(error);
                alert('Có lỗi xảy ra khi xóa danh mục!');
            });
    }

    // ==========================================================
    // [DB-CONNECT] THÊM MỚI / CẬP NHẬT DANH MỤC
    // - Nếu editingId có giá trị -> action=update -> DAO.updateDanhMuc(id, code, name, status)
    //   -> UPDATE danh_muc SET code=?, name=?, status=? WHERE id=?
    // - Nếu editingId = null -> action=add -> DAO.insertDanhMuc(code, name, status)
    //   -> INSERT INTO danh_muc (code, name, status) VALUES (?, ?, ?)
    // Servlet nên kiểm tra trùng mã danh mục (code) trước khi insert/update.
    // ==========================================================
    function saveData() {
        const code = document.getElementById('code').value.trim();
        const name = document.getElementById('name').value.trim();
        const status = document.getElementById('status').value;

        if (!code || !name) {
            alert('Vui lòng nhập đầy đủ thông tin bắt buộc!');
            return;
        }

        const formData = new URLSearchParams();
        formData.append('code', code);
        formData.append('name', name);
        formData.append('status', status);

        if (editingId) {
            formData.append('action', 'update');
            formData.append('id', editingId);
        } else {
            formData.append('action', 'add');
        }

        fetch(API_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: formData
        })
            .then(response => {
                if (!response.ok) throw new Error('Lưu thất bại');
                return response.json(); // Servlet nên trả về { "success": true } hoặc { "success": false, "message": "..." }
            })
            .then(result => {
                if (result.success) {
                    closeModal();
                    loadDataFromServer(); // Tải lại danh sách mới nhất từ DB
                } else {
                    alert(result.message || 'Lưu danh mục thất bại!');
                }
            })
            .catch(error => {
                console.error(error);
                alert('Có lỗi xảy ra khi lưu danh mục!');
            });
    }

    // ==========================================================
    // [DB-CONNECT] TÌM KIẾM / LỌC THEO TRẠNG THÁI
    // Cách 1 (đơn giản, đang dùng): lọc trên dữ liệu đã tải sẵn ở client (fullDataList).
    // Cách 2 (khuyên dùng khi dữ liệu lớn): gửi searchTerm/status lên server,
    //   Servlet build câu SQL kiểu:
    //   SELECT * FROM danh_muc WHERE (code LIKE ? OR name LIKE ?) AND status = ?
    //   rồi trả JSON tương tự loadDataFromServer(). Khi đó gọi:
    //   fetch(`${API_URL}?action=list&search=${searchTerm}&status=${statusValue}`)
    // ==========================================================
    document.getElementById('searchInput').addEventListener('input', function(e) {
        const searchTerm = e.target.value.toLowerCase();
        const statusValue = document.getElementById('statusFilter').value;
        applyFilter(searchTerm, statusValue);
    });

    document.getElementById('statusFilter').addEventListener('change', function(e) {
        const statusValue = e.target.value;
        const searchTerm = document.getElementById('searchInput').value.toLowerCase();
        applyFilter(searchTerm, statusValue);
    });

    function applyFilter(searchTerm, statusValue) {
        dataList = fullDataList.filter(item => {
            const matchSearch =
                item.code.toLowerCase().includes(searchTerm) ||
                item.name.toLowerCase().includes(searchTerm);
            const matchStatus = statusValue === '' || item.status === statusValue;
            return matchSearch && matchStatus;
        });
        currentPage = 1;
        renderTable();
    }

    // Close modal when clicking outside
    document.getElementById('modal').addEventListener('click', function(e) {
        if (e.target === this) {
            closeModal();
        }
    });

    document.getElementById('confirmModal').addEventListener('click', function(e) {
        if (e.target === this) {
            closeConfirmModal();
        }
    });
</script>
</body>
</html>
