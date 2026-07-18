(function () {
<<<<<<< HEAD
    // Hiện thông báo nhỏ trên giao diện sau mỗi thao tác.
=======
    const statusClassMap = {
        'Chờ xác nhận': 'invoice-status--waiting',
        'Đã xác nhận': 'invoice-status--confirmed',
        'Đang xử lý': 'invoice-status--processing',
        'Đang giao': 'invoice-status--shipping',
        'Đã giao': 'invoice-status--shipped',
        'Hoàn thành': 'invoice-status--done',
        'Đã hủy': 'invoice-status--cancelled'
    };

>>>>>>> THONG_KE
    function showToast(message) {
        const toast = document.getElementById('invoiceToast');
        const toastMessage = document.getElementById('toastMessage');

        if (!toast || !toastMessage) {
            return;
        }

        toastMessage.textContent = message;
        toast.classList.add('is-visible');
        window.clearTimeout(showToast.timer);
<<<<<<< HEAD
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
        const formCard = document.getElementById('invoiceFormCard');
        const showFormButton = document.getElementById('btnShowForm');
        const cancelFormButton = document.getElementById('btnCancelForm');
        const formTitle = document.getElementById('formTitle');
        const invoiceCodeInput = document.getElementById('maHoaDon');
        const productLines = document.getElementById('invoiceProductLines');
        const addProductButton = document.getElementById('addInvoiceProduct');
        const productPicker = document.querySelector('.invoice-product-picker');
        let selectedTabStatus = 'all';

        if (invoiceCodeInput) {
            invoiceCodeInput.required = false;
            invoiceCodeInput.placeholder = 'HD001';
        }

        function updateProductTotal() {
            let total = 0;
            productLines.querySelectorAll('.invoice-product-line').forEach((line) => {
                const select = line.querySelector('.invoice-product-select');
                const quantity = line.querySelector('.invoice-product-quantity');
                const option = select.options[select.selectedIndex];
                const price = Number(option && option.dataset.price) || 0;
                const stock = Number(option && option.dataset.stock) || 0;
                let amount = Number(quantity.value) || 0;

                if (stock > 0 && amount > stock) {
                    amount = stock;
                    quantity.value = stock;
                }

                total += price * amount;
            });

            document.getElementById('tongTienThanhToan').value = total > 0 ? total : '';
        }

        function bindProductLine(line) {
            line.querySelector('.invoice-product-select').addEventListener('change', updateProductTotal);
            line.querySelector('.invoice-product-quantity').addEventListener('input', updateProductTotal);
            line.querySelector('.invoice-product-remove').addEventListener('click', () => {
                const allLines = productLines.querySelectorAll('.invoice-product-line');
                if (allLines.length > 1) {
                    line.remove();
                } else {
                    line.querySelector('.invoice-product-select').value = '';
                    line.querySelector('.invoice-product-quantity').value = '1';
                }
                updateProductTotal();
            });
        }

        bindProductLine(productLines.querySelector('.invoice-product-line'));
        addProductButton.addEventListener('click', () => {
            const firstLine = productLines.querySelector('.invoice-product-line');
            const newLine = firstLine.cloneNode(true);
            newLine.querySelector('.invoice-product-select').value = '';
            newLine.querySelector('.invoice-product-quantity').value = '1';
            productLines.appendChild(newLine);
            bindProductLine(newLine);
        });

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

        // Đưa form về trạng thái ban đầu để thêm hóa đơn mới.
        function resetForm() {
            formTitle.textContent = 'Thêm hóa đơn';
            document.getElementById('maHoaDon').value = '';
            document.getElementById('tenNguoiNhan').value = '';
            document.getElementById('soDienThoai').value = '';
            document.getElementById('idNhanVien').value = '';
            document.getElementById('tongTienThanhToan').value = '';
            document.getElementById('trangThai').value = '1';
            document.getElementById('ghiChu').value = '';
            productLines.innerHTML = productLines.querySelector('.invoice-product-line').outerHTML;
            bindProductLine(productLines.querySelector('.invoice-product-line'));
            productPicker.style.display = '';
            formCard.classList.remove('is-visible');
            updateProductTotal();
        }

        // Xuất các dòng đang hiển thị ra file CSV.
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

        // Mở form thêm mới hóa đơn.
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
=======
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
>>>>>>> THONG_KE
    });
})();
