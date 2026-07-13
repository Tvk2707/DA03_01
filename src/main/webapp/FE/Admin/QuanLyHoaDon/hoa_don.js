(function () {
    // Hien thong bao nho tren giao dien sau moi thao tac.
    function showToast(message) {
        const toast = document.getElementById('invoiceToast');
        const toastMessage = document.getElementById('toastMessage');

        if (!toast || !toastMessage) {
            return;
        }

        toastMessage.textContent = message;
        toast.classList.add('is-visible');
        window.clearTimeout(showToast.timer);
        showToast.timer = window.setTimeout(() => {
            toast.classList.remove('is-visible');
        }, 2600);
    }

    // Khoi tao trang danh sach hoa don: loc, them/sua, xoa mem, xuat file.
    function initInvoiceList() {
        const table = document.getElementById('ordersTable');

        if (!table) {
            // Khong tim thay bang danh sach nghia la dang o trang chi tiet.
            initDetailPage();
            return;
        }

        const rows = Array.from(table.querySelectorAll('tbody tr'));
        const searchInput = document.getElementById('orderSearch');
        const typeFilter = document.getElementById('orderTypeFilter');
        const statusFilter = document.getElementById('statusFilter');
        const fromDate = document.getElementById('fromDateFilter');
        const toDate = document.getElementById('toDateFilter');
        const emptyState = document.getElementById('emptyState');
        const orderCount = document.getElementById('orderCount');
        const filterToggle = document.getElementById('filterToggle');
        const filterCard = document.querySelector('.invoice-filter-card');
        const exportButton = document.getElementById('btnExportOrders');
        const resetButton = document.getElementById('btnResetFilters');
        const tabs = Array.from(document.querySelectorAll('#statusTabs .invoice-tab'));
        const formCard = document.getElementById('invoiceFormCard');
        const showFormButton = document.getElementById('btnShowForm');
        const cancelFormButton = document.getElementById('btnCancelForm');
        const formTitle = document.getElementById('formTitle');
        let selectedTabStatus = 'all';

        // Loc cac dong trong bang theo tu khoa, loai, trang thai va khoang ngay.
        function filterRows() {
            const keyword = (searchInput.value || '').trim().toLowerCase();
            const selectedType = typeFilter.value;
            const selectedStatus = statusFilter.value;
            const start = fromDate.value;
            const end = toDate.value;
            let visibleCount = 0;

            rows.forEach((row) => {
                const rowStatus = row.dataset.status || '';
                const rowType = row.dataset.type || '';
                const rowDate = row.dataset.date || '';
                const matchesKeyword = !keyword || (row.dataset.search || '').includes(keyword);
                const matchesType = selectedType === 'all' || rowType === selectedType;
                const matchesSelectStatus = selectedStatus === 'all' || rowStatus === selectedStatus;
                const matchesTabStatus = selectedTabStatus === 'all' || rowStatus === selectedTabStatus;
                const matchesStart = !start || (rowDate && rowDate >= start);
                const matchesEnd = !end || (rowDate && rowDate <= end);
                const isVisible = matchesKeyword && matchesType && matchesSelectStatus && matchesTabStatus && matchesStart && matchesEnd;

                row.style.display = isVisible ? '' : 'none';

                if (isVisible) {
                    visibleCount += 1;
                }
            });

            emptyState.classList.toggle('is-visible', visibleCount === 0);
            table.style.display = visibleCount === 0 ? 'none' : 'table';
            orderCount.textContent = 'Hiển thị ' + visibleCount + ' / tổng ' + rows.length + ' hóa đơn';
        }

        // Dua form ve trang thai ban dau de them hoa don moi.
        function resetForm() {
            formTitle.textContent = 'Thêm hóa đơn';
            document.getElementById('invoiceId').value = '';
            document.getElementById('maHoaDon').value = '';
            document.getElementById('tenNguoiNhan').value = '';
            document.getElementById('soDienThoai').value = '';
            document.getElementById('tongTienThanhToan').value = '';
            document.getElementById('trangThai').value = '1';
            document.getElementById('ghiChu').value = '';
            formCard.classList.remove('is-visible');
        }

        // Xuat cac dong dang hien thi ra file CSV.
        function exportVisibleRows() {
            const visibleRows = rows.filter((row) => row.style.display !== 'none');
            const header = ['STT', 'Mã hóa đơn', 'Nhân viên', 'Khách hàng', 'Số điện thoại', 'Loại hóa đơn', 'Tổng tiền', 'Ngày tạo', 'Trạng thái'];
            const lines = [header.join(',')];

            visibleRows.forEach((row) => {
                const cells = Array.from(row.children).slice(0, 9).map((cell) => {
                    return '"' + cell.innerText.replace(/\s+/g, ' ').trim().replace(/"/g, '""') + '"';
                });
                lines.push(cells.join(','));
            });

            const blob = new Blob(['\ufeff' + lines.join('\n')], { type: 'text/csv;charset=utf-8;' });
            const link = document.createElement('a');
            link.href = URL.createObjectURL(blob);
            link.download = 'hoa_don_trang_1.csv';
            link.click();
            URL.revokeObjectURL(link.href);
            showToast('Đã xuất danh sách hóa đơn hiện tại');
        }

        // Mo form them moi hoa don.
        showFormButton.addEventListener('click', () => {
            resetForm();
            formCard.classList.add('is-visible');
            document.getElementById('maHoaDon').focus();
        });

        cancelFormButton.addEventListener('click', resetForm);
        searchInput.addEventListener('input', filterRows);
        typeFilter.addEventListener('change', filterRows);
        statusFilter.addEventListener('change', filterRows);
        fromDate.addEventListener('change', filterRows);
        toDate.addEventListener('change', filterRows);
        exportButton.addEventListener('click', exportVisibleRows);

        filterToggle.addEventListener('click', () => {
            filterCard.classList.toggle('is-collapsed');
        });

        // Xoa tat ca dieu kien loc va hien lai toan bo hoa don.
        resetButton.addEventListener('click', () => {
            searchInput.value = '';
            typeFilter.value = 'all';
            statusFilter.value = 'all';
            fromDate.value = '';
            toDate.value = '';
            selectedTabStatus = 'all';
            tabs.forEach((tab) => tab.classList.toggle('is-active', tab.dataset.status === 'all'));
            filterRows();
            showToast('Đã đặt lại bộ lọc hóa đơn');
        });

        // Loc nhanh theo cac tab trang thai.
        tabs.forEach((tab) => {
            tab.addEventListener('click', () => {
                tabs.forEach((item) => item.classList.remove('is-active'));
                tab.classList.add('is-active');
                selectedTabStatus = tab.dataset.status;
                filterRows();
            });
        });

        // Nut in hien dang hien thong bao demo.
        document.querySelectorAll('[data-print]').forEach((button) => {
            button.addEventListener('click', () => {
                showToast('Đã gửi hóa đơn ' + button.dataset.print + ' đến hàng đợi in');
            });
        });

        // Lay du lieu tu dong dang chon va dua len form sua.
        document.querySelectorAll('[data-edit]').forEach((button) => {
            button.addEventListener('click', () => {
                formTitle.textContent = 'Sửa hóa đơn';
                document.getElementById('invoiceId').value = button.dataset.id || '';
                document.getElementById('maHoaDon').value = button.dataset.code || '';
                document.getElementById('tenNguoiNhan').value = button.dataset.name === '-' ? '' : (button.dataset.name || '');
                document.getElementById('soDienThoai').value = button.dataset.phone === '-' ? '' : (button.dataset.phone || '');
                document.getElementById('tongTienThanhToan').value = button.dataset.total || '0';
                document.getElementById('trangThai').value = button.dataset.statusValue || '1';
                document.getElementById('ghiChu').value = button.dataset.note === '-' ? '' : (button.dataset.note || '');
                formCard.classList.add('is-visible');
                formCard.scrollIntoView({ behavior: 'smooth', block: 'start' });
            });
        });

        // Hoi xac nhan truoc khi gui form xoa mem.
        document.querySelectorAll('.invoice-delete-form').forEach((form) => {
            form.addEventListener('submit', (event) => {
                if (!confirm('Bạn có chắc muốn hủy hóa đơn này không?')) {
                    event.preventDefault();
                }
            });
        });

        filterRows();
    }

    // Khoi tao trang chi tiet hoa don: in va modal cap nhat trang thai.
    function initDetailPage() {
        const printButton = document.getElementById('btnPrintDetail');

        if (!printButton) {
            return;
        }

        // Goi chuc nang in cua trinh duyet.
        printButton.addEventListener('click', () => {
            window.print();
            showToast('Đã mở cửa sổ in hóa đơn');
        });

        // Mo modal khi bam nut co data-open-modal.
        document.querySelectorAll('[data-open-modal]').forEach((button) => {
            button.addEventListener('click', () => {
                const modal = document.getElementById(button.dataset.openModal);
                if (modal) {
                    modal.classList.add('is-open');
                    modal.setAttribute('aria-hidden', 'false');
                }
            });
        });

        // Dong modal khi bam nut co data-close-modal.
        document.querySelectorAll('[data-close-modal]').forEach((button) => {
            button.addEventListener('click', () => {
                const modal = button.closest('.invoice-modal');
                if (modal) {
                    modal.classList.remove('is-open');
                    modal.setAttribute('aria-hidden', 'true');
                }
            });
        });
    }

    // Doi HTML tai xong roi moi gan su kien.
    document.addEventListener('DOMContentLoaded', () => {
        initInvoiceList();
    });
})();
