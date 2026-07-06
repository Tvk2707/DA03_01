(function () {
    const STORAGE_KEY = 'rior_invoice_statuses';
    const statusOrder = ['Chờ xác nhận', 'Đã xác nhận', 'Đang xử lý', 'Đang giao', 'Đã giao', 'Hoàn thành'];
    const statusClassMap = {
        'Chờ xác nhận': 'invoice-status--waiting',
        'Đã xác nhận': 'invoice-status--confirmed',
        'Đang xử lý': 'invoice-status--processing',
        'Đang giao': 'invoice-status--shipping',
        'Đã giao': 'invoice-status--shipped',
        'Hoàn thành': 'invoice-status--done',
        'Đã hủy': 'invoice-status--cancelled'
    };

    const invoices = {
        HD26042981918: {
            code: 'HD26042981918',
            status: 'Hoàn thành',
            type: 'Tại quầy',
            created: '19:18 29/04/2026',
            staff: 'NV001 - Admin TBT',
            customer: {
                name: 'Duy Quyết',
                phone: '0868219136',
                email: 'duyquyet@gmail.com',
                address: 'Thanh Xuân, Hà Nội'
            },
            shipping: {
                receiver: 'Duy Quyết',
                phone: '0868219136',
                address: 'Nhận tại quầy RIOR Eyewear',
                fee: '0 đ',
                note: 'Khách đã kiểm tra độ cận tại quầy'
            },
            values: {
                raw: '6.200.000 đ',
                discount: '200.000 đ',
                shipping: '0 đ',
                total: '6.000.000 đ',
                paid: '6.000.000 đ',
                remain: '0 đ'
            },
            payments: [
                ['6.000.000 đ', '19:26 29/04/2026', 'RIOR81918', 'Tiền mặt', 'Khách thanh toán tại quầy']
            ],
            products: [
                ['Gọng kính Titanium Flex Amber', 'Đen - 52-18-145', 1, '4.500.000 đ', '4.500.000 đ'],
                ['Tròng kính chống ánh sáng xanh 1.60', 'Blue Cut - HMC', 1, '1.700.000 đ', '1.700.000 đ']
            ]
        },
        HD26042925243: {
            code: 'HD26042925243',
            status: 'Hoàn thành',
            type: 'Tại quầy',
            created: '19:20 29/04/2026',
            staff: 'NV001 - Admin TBT',
            customer: {
                name: 'Duy Quyết',
                phone: '0868219136',
                email: 'duyquyet@gmail.com',
                address: 'Thanh Xuân, Hà Nội'
            },
            shipping: {
                receiver: 'Duy Quyết',
                phone: '0868219136',
                address: 'Nhận tại quầy RIOR Eyewear',
                fee: '0 đ',
                note: 'Khách chọn gọng acetate màu cà phê'
            },
            values: {
                raw: '2.950.000 đ',
                discount: '150.000 đ',
                shipping: '0 đ',
                total: '2.800.000 đ',
                paid: '2.800.000 đ',
                remain: '0 đ'
            },
            payments: [
                ['2.800.000 đ', '19:29 29/04/2026', 'RIOR25243', 'Chuyển khoản', 'Thanh toán QR tại quầy']
            ],
            products: [
                ['Gọng Acetate Classic Cafe', 'Nâu - 49-21-145', 1, '2.950.000 đ', '2.950.000 đ']
            ]
        },
        HD26042908574: {
            code: 'HD26042908574',
            status: 'Đã giao',
            type: 'Tại quầy - giao hàng',
            created: '19:22 29/04/2026',
            staff: 'NV001 - Admin TBT',
            customer: {
                name: 'Duy Quyết',
                phone: '0868219136',
                email: 'duyquyet@gmail.com',
                address: 'Thanh Xuân, Hà Nội'
            },
            shipping: {
                receiver: 'Duy Quyết',
                phone: '0868219136',
                address: 'Thanh Xuân, Hà Nội',
                fee: '25.000 đ',
                note: 'Giao kính râm kèm hộp và khăn lau'
            },
            values: {
                raw: '2.925.000 đ',
                discount: '150.000 đ',
                shipping: '25.000 đ',
                total: '2.800.000 đ',
                paid: '2.800.000 đ',
                remain: '0 đ'
            },
            payments: [
                ['2.800.000 đ', '19:30 29/04/2026', 'RIOR08574', 'Chuyển khoản QR', 'Khách thanh toán trước khi giao']
            ],
            products: [
                ['Kính râm Polarized Aviator', 'Xám khói - UV400', 1, '2.925.000 đ', '2.925.000 đ']
            ]
        },
        HD26042972295: {
            code: 'HD26042972295',
            status: 'Hoàn thành',
            type: 'Tại quầy',
            created: '19:23 29/04/2026',
            staff: 'NV001 - Admin TBT',
            customer: {
                name: 'Khách lẻ',
                phone: '-',
                email: '-',
                address: 'Nhận tại quầy'
            },
            shipping: {
                receiver: 'Khách lẻ',
                phone: '-',
                address: 'Nhận tại quầy RIOR Eyewear',
                fee: '0 đ',
                note: 'Cắt kính lấy trong ngày'
            },
            values: {
                raw: '4.900.000 đ',
                discount: '200.000 đ',
                shipping: '0 đ',
                total: '4.700.000 đ',
                paid: '4.700.000 đ',
                remain: '0 đ'
            },
            payments: [
                ['4.700.000 đ', '19:31 29/04/2026', 'RIOR72295', 'Tiền mặt', 'Thanh toán đủ tại quầy']
            ],
            products: [
                ['Gọng Oval Beta Titanium', 'Vàng champagne - 50-19-145', 1, '3.400.000 đ', '3.400.000 đ'],
                ['Tròng kính đổi màu 1.56', 'Photochromic Brown', 1, '1.500.000 đ', '1.500.000 đ']
            ]
        },
        HD26042977687: {
            code: 'HD26042977687',
            status: 'Đang xử lý',
            type: 'Online',
            created: '19:25 29/04/2026',
            staff: 'SYSTEM - System',
            customer: {
                name: 'Quyết',
                phone: '0868219136',
                email: 'quyet.eyewear@gmail.com',
                address: 'Thanh Xuân, Hà Nội'
            },
            shipping: {
                receiver: 'Quyết',
                phone: '0868219136',
                address: 'Thanh Xuân, Hà Nội',
                fee: '25.000 đ',
                note: 'Khách cần tư vấn thêm độ tròng'
            },
            values: {
                raw: '1.500.000 đ',
                discount: '150.000 đ',
                shipping: '25.000 đ',
                total: '1.375.000 đ',
                paid: '1.375.000 đ',
                remain: '0 đ'
            },
            payments: [
                ['1.375.000 đ', '19:29 29/04/2026', '15517637', 'Chuyển khoản QR', 'Khách thanh toán VNPAY']
            ],
            products: [
                ['Tròng kính chống ánh sáng xanh RIOR 1.60', 'HMC - Blue Cut', 1, '1.500.000 đ', '1.500.000 đ']
            ]
        },
        HD26042916162: {
            code: 'HD26042916162',
            status: 'Chờ xác nhận',
            type: 'Online',
            created: '19:28 29/04/2026',
            staff: 'SYSTEM - System',
            customer: {
                name: 'Quyết',
                phone: '0868219136',
                email: 'quyet.eyewear@gmail.com',
                address: 'Thanh Xuân, Hà Nội'
            },
            shipping: {
                receiver: 'Quyết',
                phone: '0868219136',
                address: 'Thanh Xuân, Hà Nội',
                fee: '25.000 đ',
                note: 'Khách đặt gọng titanium màu đen'
            },
            values: {
                raw: '1.500.000 đ',
                discount: '150.000 đ',
                shipping: '25.000 đ',
                total: '1.375.000 đ',
                paid: '1.375.000 đ',
                remain: '0 đ'
            },
            payments: [
                ['1.375.000 đ', '19:29 29/04/2026', '15517638', 'Chuyển khoản QR', 'Khách thanh toán VNPAY']
            ],
            products: [
                ['Gọng kính Titanium Round RIOR', 'Đen - 48-18-145', 1, '1.500.000 đ', '1.500.000 đ']
            ]
        }
    };

    function readSavedStatuses() {
        try {
            return JSON.parse(localStorage.getItem(STORAGE_KEY) || '{}');
        } catch (error) {
            return {};
        }
    }

    function saveStatus(code, status) {
        const statuses = readSavedStatuses();
        statuses[code] = status;
        localStorage.setItem(STORAGE_KEY, JSON.stringify(statuses));
    }

    function getStatus(code, fallback) {
        return readSavedStatuses()[code] || fallback;
    }

    function setStatusBadge(element, status) {
        if (!element) {
            return;
        }

        element.textContent = status;
        element.className = 'invoice-status ' + (statusClassMap[status] || '');
    }

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

    function initListPage() {
        const table = document.getElementById('ordersTable');
        if (!table) {
            return;
        }

        const rows = Array.from(table.querySelectorAll('tbody tr'));
        const searchInput = document.getElementById('orderSearch');
        const typeFilter = document.getElementById('orderTypeFilter');
        const fromDate = document.getElementById('fromDateFilter');
        const toDate = document.getElementById('toDateFilter');
        const tabs = Array.from(document.querySelectorAll('#statusTabs .invoice-tab'));
        const orderCount = document.getElementById('orderCount');
        const emptyState = document.getElementById('emptyState');
        const resetButton = document.getElementById('btnResetFilters');
        const exportButton = document.getElementById('btnExportOrders');
        const filterToggle = document.getElementById('filterToggle');
        const filterCard = document.querySelector('.invoice-filter-card');
        let selectedStatus = 'all';

        rows.forEach((row) => {
            const savedStatus = getStatus(row.dataset.id, row.dataset.status);
            row.dataset.status = savedStatus;
            setStatusBadge(row.querySelector('.invoice-status'), savedStatus);
        });

        function filterRows() {
            const keyword = (searchInput.value || '').trim().toLowerCase();
            const selectedType = typeFilter.value;
            const start = fromDate.value;
            const end = toDate.value;
            let visibleCount = 0;

            rows.forEach((row) => {
                const matchesKeyword = !keyword || row.dataset.search.toLowerCase().includes(keyword);
                const matchesType = selectedType === 'all' || row.dataset.type === selectedType;
                const matchesStatus = selectedStatus === 'all' || row.dataset.status === selectedStatus;
                const matchesStart = !start || row.dataset.date >= start;
                const matchesEnd = !end || row.dataset.date <= end;
                const isVisible = matchesKeyword && matchesType && matchesStatus && matchesStart && matchesEnd;

                row.style.display = isVisible ? '' : 'none';
                if (isVisible) {
                    visibleCount += 1;
                }
            });

            orderCount.textContent = 'Hiển thị ' + visibleCount + ' / tổng ' + rows.length + ' bản ghi';
            emptyState.classList.toggle('is-visible', visibleCount === 0);
            table.style.display = visibleCount === 0 ? 'none' : 'table';
        }

        [searchInput, typeFilter, fromDate, toDate].forEach((element) => {
            element.addEventListener('input', filterRows);
            element.addEventListener('change', filterRows);
        });

        tabs.forEach((tab) => {
            tab.addEventListener('click', () => {
                tabs.forEach((item) => item.classList.remove('is-active'));
                tab.classList.add('is-active');
                selectedStatus = tab.dataset.status;
                filterRows();
            });
        });

        resetButton.addEventListener('click', () => {
            searchInput.value = '';
            typeFilter.value = 'all';
            fromDate.value = '2026-04-29';
            toDate.value = '2026-04-29';
            selectedStatus = 'all';
            tabs.forEach((item) => item.classList.toggle('is-active', item.dataset.status === 'all'));
            filterRows();
            showToast('Đã đặt lại bộ lọc hóa đơn');
        });

        exportButton.addEventListener('click', () => exportVisibleRows(rows));

        filterToggle.addEventListener('click', () => {
            filterCard.classList.toggle('is-collapsed');
        });

        document.querySelectorAll('[data-print]').forEach((button) => {
            button.addEventListener('click', () => {
                showToast('Đã gửi hóa đơn ' + button.dataset.print + ' đến hàng đợi in');
            });
        });

        filterRows();
    }

    function exportVisibleRows(rows) {
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

    function initDetailPage() {
        const timeline = document.getElementById('invoiceTimeline');
        if (!timeline) {
            return;
        }

        const params = new URLSearchParams(window.location.search);
        const code = params.get('id') || 'HD26042916162';
        const invoice = invoices[code] || invoices.HD26042916162;
        let currentStatus = getStatus(invoice.code, invoice.status);
        let pendingNextStatus = null;
        let modalMode = 'next';
        const history = buildInitialHistory(currentStatus);

        function render() {
            currentStatus = getStatus(invoice.code, currentStatus);
            renderSummary(invoice, currentStatus);
            renderTimeline(currentStatus);
            renderRows('paymentRows', invoice.payments, (payment) => payment);
            renderProducts(invoice.products);
            renderHistory(history);
            updateActionState(currentStatus);
        }

        function renderTimeline(status) {
            timeline.innerHTML = '';

            if (status === 'Đã hủy') {
                const step = document.createElement('div');
                step.className = 'timeline-step is-current';
                step.innerHTML = '<div class="timeline-step__name">Đã hủy</div><div class="timeline-step__date">19:29 29/04/2026</div><div class="timeline-step__dot"><i class="fas fa-xmark"></i></div>';
                timeline.appendChild(step);
                return;
            }

            const activeIndex = Math.max(statusOrder.indexOf(status), 0);
            statusOrder.forEach((item, index) => {
                const step = document.createElement('div');
                step.className = 'timeline-step' + (index < activeIndex ? ' is-done' : '') + (index === activeIndex ? ' is-current' : '');
                step.innerHTML =
                    '<div class="timeline-step__name">' + item + '</div>' +
                    '<div class="timeline-step__date">' + (index <= activeIndex ? '19:29 29/04/2026' : '') + '</div>' +
                    '<div class="timeline-step__dot"><i class="fas fa-check"></i></div>';
                timeline.appendChild(step);
            });
        }

        function openStatusModal(mode) {
            modalMode = mode;
            const nextStatus = mode === 'cancel' ? 'Đã hủy' : getNextStatus(currentStatus);
            if (!nextStatus) {
                showToast('Hóa đơn không còn trạng thái tiếp theo');
                return;
            }

            pendingNextStatus = nextStatus;
            document.getElementById('statusModalTitle').innerHTML = mode === 'cancel'
                ? '<i class="fas fa-triangle-exclamation"></i> Xác nhận hủy đơn'
                : '<i class="fas fa-triangle-exclamation"></i> Xác nhận đổi trạng thái';
            document.getElementById('modalOrderCode').textContent = invoice.code;
            setStatusBadge(document.getElementById('modalCurrentStatus'), currentStatus);
            setStatusBadge(document.getElementById('modalNextStatus'), nextStatus);
            document.getElementById('statusNote').value = mode === 'cancel'
                ? 'Hủy đơn theo yêu cầu xử lý của cửa hàng'
                : 'Chuyển trạng thái: ' + currentStatus + ' -> ' + nextStatus;
            openModal('statusModal');
        }

        document.getElementById('btnChangeStatus').addEventListener('click', () => openStatusModal('next'));
        document.getElementById('btnCancelOrder').addEventListener('click', () => openStatusModal('cancel'));
        document.getElementById('btnHistory').addEventListener('click', () => openModal('historyModal'));
        document.getElementById('btnPrintDetail').addEventListener('click', () => {
            window.print();
            showToast('Đã mở cửa sổ in hóa đơn');
        });

        document.getElementById('btnConfirmStatus').addEventListener('click', () => {
            if (!pendingNextStatus) {
                return;
            }

            const previousStatus = currentStatus;
            currentStatus = pendingNextStatus;
            saveStatus(invoice.code, currentStatus);
            history.unshift([
                currentStatus,
                '19:30 29/04/2026',
                'NV001',
                'Admin TBT',
                modalMode === 'cancel' ? 'Hủy đơn' : getActionName(currentStatus),
                document.getElementById('statusNote').value || ('Chuyển trạng thái: ' + previousStatus + ' -> ' + currentStatus)
            ]);
            closeModal('statusModal');
            render();
            showToast('Cập nhật trạng thái thành công');
        });

        document.querySelectorAll('[data-close-modal]').forEach((element) => {
            element.addEventListener('click', () => closeModal('statusModal'));
        });

        document.querySelectorAll('[data-close-history]').forEach((element) => {
            element.addEventListener('click', () => closeModal('historyModal'));
        });

        render();
    }

    function renderSummary(invoice, status) {
        const productCount = invoice.products.reduce((sum, product) => sum + Number(product[2]), 0);

        setText('summaryCode', invoice.code);
        setText('summaryCreated', invoice.created);
        setText('summaryStaff', invoice.staff);
        setText('summaryType', invoice.type);
        setStatusBadge(document.getElementById('summaryStatus'), status);
        setText('summaryTotal', invoice.values.total);
        setText('summaryPaid', invoice.values.paid);
        setText('customerName', invoice.customer.name);
        setText('customerPhone', invoice.customer.phone);
        setText('customerEmail', invoice.customer.email);
        setText('customerAddress', invoice.customer.address);
        setText('receiverName', invoice.shipping.receiver);
        setText('receiverPhone', invoice.shipping.phone);
        setText('receiverAddress', invoice.shipping.address);
        setText('shippingFee', invoice.shipping.fee);
        setText('orderNote', invoice.shipping.note);
        setText('rawTotal', invoice.values.raw);
        setText('discountTotal', invoice.values.discount);
        setText('valueShipping', invoice.values.shipping);
        setText('mustPay', invoice.values.total);
        setText('currentStatusText', status);
        setText('productCount', productCount);
        setText('orderValue', invoice.values.raw);
        setText('remainingAmount', invoice.values.remain);
    }

    function renderRows(targetId, rows, mapper) {
        const tbody = document.getElementById(targetId);
        tbody.innerHTML = '';

        rows.forEach((row) => {
            const values = mapper(row);
            const tr = document.createElement('tr');
            tr.innerHTML = values.map((value) => '<td>' + value + '</td>').join('');
            tbody.appendChild(tr);
        });
    }

    function renderProducts(products) {
        const tbody = document.getElementById('productRows');
        tbody.innerHTML = '';

        products.forEach((product, index) => {
            const tr = document.createElement('tr');
            tr.innerHTML =
                '<td>' + (index + 1) + '</td>' +
                '<td><span class="invoice-thumb"><i class="fas fa-glasses"></i></span></td>' +
                '<td><div class="invoice-product"><div><strong>' + product[0] + '</strong><small>' + product[1] + '</small></div></div></td>' +
                '<td>' + product[2] + '</td>' +
                '<td class="invoice-money">' + product[3] + '</td>' +
                '<td class="invoice-money">' + product[4] + '</td>';
            tbody.appendChild(tr);
        });
    }

    function renderHistory(history) {
        const tbody = document.getElementById('historyRows');
        tbody.innerHTML = '';

        history.forEach((item) => {
            const tr = document.createElement('tr');
            tr.innerHTML =
                '<td><span class="invoice-status ' + (statusClassMap[item[0]] || '') + '">' + item[0] + '</span></td>' +
                '<td>' + item[1] + '</td>' +
                '<td>' + item[2] + '</td>' +
                '<td>' + item[3] + '</td>' +
                '<td>' + item[4] + '</td>' +
                '<td>' + item[5] + '</td>';
            tbody.appendChild(tr);
        });
    }

    function buildInitialHistory(status) {
        if (status === 'Đã hủy') {
            return [
                ['Đã hủy', '19:29 29/04/2026', 'NV001', 'Admin TBT', 'Hủy đơn', 'Đơn được hủy sau khi kiểm tra tồn kho'],
                ['Chờ xác nhận', '19:28 29/04/2026', 'SYSTEM', 'System', 'Tạo đơn', 'Khách tạo đơn online']
            ];
        }

        const activeIndex = Math.max(statusOrder.indexOf(status), 0);
        return statusOrder.slice(0, activeIndex + 1).reverse().map((item) => {
            const previous = statusOrder[Math.max(statusOrder.indexOf(item) - 1, 0)];
            return [
                item,
                '19:29 29/04/2026',
                item === 'Chờ xác nhận' ? 'SYSTEM' : 'NV001',
                item === 'Chờ xác nhận' ? 'System' : 'Admin TBT',
                getActionName(item),
                item === 'Chờ xác nhận' ? 'Khách tạo đơn online' : 'Chuyển trạng thái: ' + previous + ' -> ' + item
            ];
        });
    }

    function getNextStatus(status) {
        const index = statusOrder.indexOf(status);
        if (index === -1 || index >= statusOrder.length - 1) {
            return null;
        }
        return statusOrder[index + 1];
    }

    function getActionName(status) {
        const map = {
            'Chờ xác nhận': 'Tạo đơn',
            'Đã xác nhận': 'Xác nhận đơn',
            'Đang xử lý': 'Chuyển sang xử lý',
            'Đang giao': 'Chuyển sang đang giao',
            'Đã giao': 'Xác nhận đã giao',
            'Hoàn thành': 'Hoàn thành đơn',
            'Đã hủy': 'Hủy đơn'
        };
        return map[status] || 'Cập nhật đơn';
    }

    function updateActionState(status) {
        const changeButton = document.getElementById('btnChangeStatus');
        const cancelButton = document.getElementById('btnCancelOrder');
        const isClosed = status === 'Hoàn thành' || status === 'Đã hủy';

        changeButton.disabled = isClosed;
        cancelButton.disabled = isClosed || status === 'Đã giao';
    }

    function setText(id, value) {
        const element = document.getElementById(id);
        if (element) {
            element.textContent = value;
        }
    }

    function openModal(id) {
        const modal = document.getElementById(id);
        if (modal) {
            modal.classList.add('is-open');
            modal.setAttribute('aria-hidden', 'false');
        }
    }

    function closeModal(id) {
        const modal = document.getElementById(id);
        if (modal) {
            modal.classList.remove('is-open');
            modal.setAttribute('aria-hidden', 'true');
        }
    }

    document.addEventListener('DOMContentLoaded', () => {
        initListPage();
        initDetailPage();
    });
})();
