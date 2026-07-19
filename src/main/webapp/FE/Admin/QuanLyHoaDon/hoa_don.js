(function () {
    // Hiện thông báo nhỏ trên giao diện sau mỗi thao tác.
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

    // Khởi tạo trang danh sách hóa đơn: lọc, thêm và xuất file.
    function initInvoiceList() {
        const table = document.getElementById('ordersTable');

        if (!table) {
            // Không tìm thấy bảng danh sách nghĩa là đang ở trang chi tiết.
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
        let selectedTabStatus = 'all';

        // Mặc định bộ lọc "Đến ngày" là ngày hiện tại mỗi khi mở trang.
        if (toDate && !toDate.value) {
            const today = new Date();
            const localDate = new Date(today.getTime() - today.getTimezoneOffset() * 60000)
                .toISOString()
                .slice(0, 10);
            toDate.value = localDate;
        }

        // Lọc các dòng trong bảng theo từ khóa, loại, trạng thái và khoảng ngày.
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

        // Xuất các dòng đang hiển thị ra file CSV.
        function exportVisibleRows() {
            const visibleRows = rows.filter((row) => row.style.display !== 'none');
            const header = ['STT', 'Mã hóa đơn', 'Nhân viên', 'Khách hàng', 'Số điện thoại', 'Loại hóa đơn', 'Tổng tiền', 'Ngày tạo', 'Trạng thái'];
            const lines = [header.join(',')];

            visibleRows.forEach((row, index) => {
                const cells = Array.from(row.children).slice(0, 9).map((cell, cellIndex) => {
                    if (cellIndex === 0) {
                        return String(index + 1);
                    }
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

        searchInput.addEventListener('input', filterRows);
        typeFilter.addEventListener('change', filterRows);
        statusFilter.addEventListener('change', filterRows);
        fromDate.addEventListener('change', filterRows);
        toDate.addEventListener('change', filterRows);
        exportButton.addEventListener('click', exportVisibleRows);

        filterToggle.addEventListener('click', () => {
            filterCard.classList.toggle('is-collapsed');
        });

        // Xóa tất cả điều kiện lọc và hiện lại toàn bộ hóa đơn.
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

        // Lọc nhanh theo các tab trạng thái.
        tabs.forEach((tab) => {
            tab.addEventListener('click', () => {
                tabs.forEach((item) => item.classList.remove('is-active'));
                tab.classList.add('is-active');
                selectedTabStatus = tab.dataset.status;
                filterRows();
            });
        });

        // Nút in ở danh sách mở trang chi tiết và tự bật hộp thoại in.
        document.querySelectorAll('[data-print]').forEach((button) => {
            button.addEventListener('click', () => {
                if (button.dataset.printUrl) {
                    window.open(button.dataset.printUrl, '_blank', 'noopener');
                    return;
                }

                showToast('Không tìm thấy đường dẫn in hóa đơn ' + button.dataset.print);
            });
        });

        filterRows();
    }

    // Khởi tạo trang chi tiết hóa đơn: in và modal cập nhật trạng thái.
    function initDetailPage() {
        const printButton = document.getElementById('btnPrintDetail');

        if (!printButton) {
            return;
        }

        // Gọi chức năng in của trình duyệt.
        printButton.addEventListener('click', () => {
            window.print();
            showToast('Đã mở cửa sổ in hóa đơn');
        });

        if (new URLSearchParams(window.location.search).get('print') === '1') {
            window.setTimeout(() => {
                window.print();
            }, 500);
        }

        // Mở modal khi bấm nút có data-open-modal.
        document.querySelectorAll('[data-open-modal]').forEach((button) => {
            button.addEventListener('click', () => {
                const modal = document.getElementById(button.dataset.openModal);
                if (modal) {
                    modal.classList.add('is-open');
                    modal.setAttribute('aria-hidden', 'false');
                }
            });
        });

        // Đóng modal khi bấm nút có data-close-modal.
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

    // Đợi HTML tải xong rồi mới gắn sự kiện.
    document.addEventListener('DOMContentLoaded', () => {
        initInvoiceList();
    });
})();
