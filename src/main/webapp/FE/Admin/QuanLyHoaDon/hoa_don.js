(function () {
    const statusClassMap = {
        'Chờ xác nhận': 'invoice-status--waiting',
        'Đã xác nhận': 'invoice-status--confirmed',
        'Đang xử lý': 'invoice-status--processing',
        'Đang giao': 'invoice-status--shipping',
        'Đã giao': 'invoice-status--shipped',
        'Hoàn thành': 'invoice-status--done',
        'Đã hủy': 'invoice-status--cancelled'
    };

    function showToast(message) {
        const toast = document.getElementById('invoiceToast');
        const toastMessage = document.getElementById('toastMessage');

        if (!toast || !toastMessage) {
            return;
        }

        toastMessage.textContent = message;
        toast.classList.add('is-visible');
        window.clearTimeout(showToast.timer);
        showToast.timer = window.setTimeout(() => toast.classList.remove('is-visible'), 2200);
    }

    function setText(id, value) {
        const element = document.getElementById(id);
        if (element) {
            element.textContent = value;
        }
    }

    function setStatusBadge(element, status) {
        if (!element) {
            return;
        }

        element.textContent = status || '-';
        element.className = 'invoice-status ' + (statusClassMap[status] || '');
    }

    function openModal(modal) {
        if (modal) {
            modal.classList.add('is-open');
            modal.setAttribute('aria-hidden', 'false');
        }
    }

    function closeModal(modal) {
        if (modal) {
            modal.classList.remove('is-open');
            modal.setAttribute('aria-hidden', 'true');
        }
    }

    function initFilterToggle() {
        const filterCard = document.querySelector('.invoice-filter-card');
        const filterToggle = document.getElementById('filterToggle');

        if (filterCard && filterToggle) {
            filterToggle.addEventListener('click', () => {
                filterCard.classList.toggle('is-collapsed');
            });
        }
    }

    function getOrderRows() {
        return Array.from(document.querySelectorAll('#ordersTable tbody tr'));
    }

    function updateOrderCount(visibleRows, totalRows) {
        const orderCount = document.getElementById('orderCount');
        if (orderCount) {
            orderCount.textContent = 'Hiển thị ' + visibleRows + ' / tổng ' + totalRows + ' bản ghi';
        }
    }

    function applyFilters() {
        const rows = getOrderRows();
        const searchInput = document.getElementById('orderSearch');
        const typeFilter = document.getElementById('orderTypeFilter');
        const fromDateFilter = document.getElementById('fromDateFilter');
        const toDateFilter = document.getElementById('toDateFilter');
        const activeTab = document.querySelector('#statusTabs .invoice-tab.is-active');
        const emptyState = document.getElementById('emptyState');

        const keyword = searchInput ? searchInput.value.trim().toLowerCase() : '';
        const type = typeFilter ? typeFilter.value : 'all';
        const fromDate = fromDateFilter ? fromDateFilter.value : '';
        const toDate = toDateFilter ? toDateFilter.value : '';
        const status = activeTab ? activeTab.dataset.status : 'all';

        let visible = 0;
        rows.forEach(row => {
            const rowSearch = (row.dataset.search || '').toLowerCase();
            const rowType = row.dataset.type || '';
            const rowStatus = row.dataset.status || '';
            const rowDate = row.dataset.date || '';

            const matchedKeyword = !keyword || rowSearch.includes(keyword);
            const matchedType = type === 'all' || rowType === type;
            const matchedStatus = status === 'all' || rowStatus === status;
            const matchedFromDate = !fromDate || rowDate >= fromDate;
            const matchedToDate = !toDate || rowDate <= toDate;
            const matched = matchedKeyword && matchedType && matchedStatus && matchedFromDate && matchedToDate;

            row.hidden = !matched;
            if (matched) {
                visible += 1;
            }
        });

        if (emptyState) {
            emptyState.classList.toggle('is-visible', visible === 0);
        }
        updateOrderCount(visible, rows.length);
    }

    function initListPage() {
        const table = document.getElementById('ordersTable');
        if (!table) {
            return;
        }

        initFilterToggle();

        ['orderSearch', 'orderTypeFilter', 'fromDateFilter', 'toDateFilter'].forEach(id => {
            const element = document.getElementById(id);
            if (element) {
                element.addEventListener('input', applyFilters);
                element.addEventListener('change', applyFilters);
            }
        });

        document.querySelectorAll('#statusTabs .invoice-tab').forEach(tab => {
            tab.addEventListener('click', () => {
                document.querySelectorAll('#statusTabs .invoice-tab').forEach(item => item.classList.remove('is-active'));
                tab.classList.add('is-active');
                applyFilters();
            });
        });

        const resetButton = document.getElementById('btnResetFilters');
        if (resetButton) {
            resetButton.addEventListener('click', () => {
                ['orderSearch', 'fromDateFilter', 'toDateFilter'].forEach(id => {
                    const element = document.getElementById(id);
                    if (element) {
                        element.value = '';
                    }
                });

                const typeFilter = document.getElementById('orderTypeFilter');
                if (typeFilter) {
                    typeFilter.value = 'all';
                }

                document.querySelectorAll('#statusTabs .invoice-tab').forEach(tab => {
                    tab.classList.toggle('is-active', tab.dataset.status === 'all');
                });
                applyFilters();
            });
        }

        const exportButton = document.getElementById('btnExportOrders');
        if (exportButton) {
            exportButton.addEventListener('click', () => {
                const rows = getOrderRows().filter(row => !row.hidden);
                if (rows.length === 0) {
                    showToast('Không có dữ liệu để xuất');
                    return;
                }

                const header = ['STT', 'Mã hóa đơn', 'Nhân viên', 'Khách hàng', 'Số điện thoại', 'Loại hóa đơn', 'Tổng tiền', 'Ngày tạo', 'Trạng thái'];
                const lines = [header.join(',')];
                rows.forEach(row => {
                    const cells = Array.from(row.querySelectorAll('td')).slice(0, 9)
                        .map(cell => '"' + cell.textContent.trim().replaceAll('"', '""') + '"');
                    lines.push(cells.join(','));
                });

                const blob = new Blob(['\ufeff' + lines.join('\n')], { type: 'text/csv;charset=utf-8;' });
                const url = URL.createObjectURL(blob);
                const link = document.createElement('a');
                link.href = url;
                link.download = 'hoa-don.csv';
                link.click();
                URL.revokeObjectURL(url);
            });
        }

        applyFilters();
    }

    function setTableEmpty(tbodyId, colSpan, message) {
        const tbody = document.getElementById(tbodyId);
        if (tbody) {
            tbody.innerHTML = '<tr><td colspan="' + colSpan + '" class="invoice-empty-cell">' + message + '</td></tr>';
        }
    }

    function initDetailPage() {
        const timeline = document.getElementById('invoiceTimeline');
        if (!timeline) {
            return;
        }

        setStatusBadge(document.getElementById('summaryStatus'), '-');
        setStatusBadge(document.getElementById('modalCurrentStatus'), '-');
        setStatusBadge(document.getElementById('modalNextStatus'), '-');

        [
            'summaryCode', 'summaryCreated', 'summaryStaff', 'summaryType',
            'customerName', 'customerPhone', 'customerEmail', 'customerAddress',
            'receiverName', 'receiverPhone', 'receiverAddress', 'shippingFee', 'orderNote',
            'rawTotal', 'discountTotal', 'valueShipping', 'currentStatusText'
        ].forEach(id => setText(id, '-'));

        ['summaryTotal', 'summaryPaid', 'mustPay', 'orderValue', 'remainingAmount'].forEach(id => setText(id, '0 đ'));
        setText('productCount', '0');
        setText('modalOrderCode', '-');

        timeline.innerHTML = '<div class="invoice-empty is-visible"><i class="fas fa-receipt"></i> Chưa có dữ liệu hóa đơn từ database</div>';
        setTableEmpty('paymentRows', 5, 'Chưa có lịch sử thanh toán');
        setTableEmpty('productRows', 6, 'Chưa có sản phẩm trong hóa đơn');
        setTableEmpty('historyRows', 6, 'Chưa có lịch sử hóa đơn');

        ['btnChangeStatus', 'btnCancelOrder', 'btnPrintDetail', 'btnConfirmStatus'].forEach(id => {
            const button = document.getElementById(id);
            if (button) {
                button.disabled = true;
            }
        });

        const historyModal = document.getElementById('historyModal');
        const statusModal = document.getElementById('statusModal');

        const historyButton = document.getElementById('btnHistory');
        if (historyButton) {
            historyButton.addEventListener('click', () => openModal(historyModal));
        }

        document.querySelectorAll('[data-close-modal]').forEach(element => {
            element.addEventListener('click', () => closeModal(statusModal));
        });

        document.querySelectorAll('[data-close-history]').forEach(element => {
            element.addEventListener('click', () => closeModal(historyModal));
        });
    }

    document.addEventListener('DOMContentLoaded', () => {
        initListPage();
        initDetailPage();
    });
})();
