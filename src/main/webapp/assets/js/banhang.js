/* POS client: calls the servlet routes used by BanHangController. */

let idHoaDonHienTai = window.idHoaDonHienTai || null;
let phuongThucThanhToanDangChon = 'PTTT001';
let idHoaDonChoXacNhanHuy = null;
let duongDanInHoaDonSauThanhToan = null;

function posContextPath() {
    const marker = '/ban-hang';
    const index = window.location.pathname.indexOf(marker);
    return index >= 0 ? window.location.pathname.substring(0, index) : '';
}

function duongDanChiTietHoaDon(idHoaDon, inHoaDon = false) {
    const query = new URLSearchParams({ id: String(idHoaDon) });
    if (inHoaDon) query.set('print', '1');
    return `${posContextPath()}/admin/hoa-don/chi-tiet?${query}`;
}

function thongBaoThongKeThayDoi() {
    try {
        localStorage.setItem('rior-statistics-changed', String(Date.now()));
    } catch (error) {
        // Trang thống kê vẫn tự đồng bộ định kỳ nếu trình duyệt chặn localStorage.
    }
}

class BanHangError extends Error {}

const BanHangAPI = {
    routes: {
        taoHoaDon: ['/ban-hang/tao-hoa-don', 'POST'],
        themSanPham: ['/ban-hang/them-san-pham', 'POST'],
        quetQr: ['/ban-hang/quet-qr', 'GET'],
        xoaSanPham: ['/ban-hang/xoa-san-pham', 'POST'],
        capNhatSoLuong: ['/ban-hang/cap-nhat-so-luong', 'POST'],
        traCuuKhachHang: ['/ban-hang/tra-cuu-khach-hang', 'GET'],
        traCuuHoacTaoKhachHang: ['/ban-hang/tra-cuu-khach-hang', 'POST'],
        ganKhachHang: ['/ban-hang/gan-khach-hang', 'POST'],
        chonKhachLe: ['/ban-hang/chon-khach-le', 'POST'],
        timVoucher: ['/ban-hang/tim-voucher', 'GET'],
        apVoucher: ['/ban-hang/ap-voucher', 'POST'],
        goVoucher: ['/ban-hang/go-voucher', 'POST'],
        revalidateVoucher: ['/ban-hang/revalidate-voucher', 'POST'],
        thanhToan: ['/thanh-toan/thanh-toan', 'POST']
    },

    async goi(action, params = {}) {
        const route = this.routes[action];
        if (!route) throw new BanHangError('Tac vu POS khong hop le.');

        const requestParams = { ...params };
        if (action === 'themSanPham' && requestParams.idSpct != null) {
            requestParams.idSanPhamChiTiet = requestParams.idSpct;
            delete requestParams.idSpct;
        }

        const url = posContextPath() + route[0];
        const options = { method: route[1], headers: {} };
        let requestUrl = url;
        if (route[1] === 'GET') {
            requestUrl += '?' + new URLSearchParams(requestParams).toString();
        } else {
            options.headers['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';
            options.body = new URLSearchParams(requestParams);
        }

        try {
            return await this.docJson(await fetch(requestUrl, options));
        } catch (error) {
            if (error instanceof BanHangError) throw error;
            console.error('POS API error:', error);
            throw new BanHangError('Khong the ket noi den may chu.');
        }
    },

    async thanhToan(params) {
        return this.goi('thanhToan', params);
    },

    async docJson(response) {
        let data;
        try {
            data = await response.json();
        } catch (error) {
            throw new BanHangError('May chu tra ve du lieu khong hop le.');
        }
        if (!response.ok || data.success === false) {
            throw new BanHangError(data.message || `Loi HTTP ${response.status}.`);
        }
        return data;
    }
};

function hienThiLoi(message) {
    alert(message);
}

function xoaLuoiSanPham() {
    const grid = document.querySelector('.product-grid');
    if (grid) grid.innerHTML = '';
}

function bayVaoGioHang(button) {
    const target = document.querySelector('.right-col .cart-title') || document.querySelector('.cart-title');
    if (!button || !target) return;

    const startRect = button.getBoundingClientRect();
    const endRect = target.getBoundingClientRect();
    const startX = startRect.left + startRect.width / 2;
    const startY = startRect.top + startRect.height / 2;
    const endX = endRect.left + endRect.width / 2;
    const endY = endRect.top + endRect.height / 2;
    const flyItem = document.createElement('div');
    const productImage = Array.from(button.closest('.p-card')?.querySelectorAll('.p-thumb img') || [])
        .find(img => img.currentSrc && img.offsetParent !== null);

    flyItem.className = 'cart-fly-item';
    flyItem.style.left = startX + 'px';
    flyItem.style.top = startY + 'px';

    if (productImage) {
        const img = document.createElement('img');
        img.src = productImage.currentSrc;
        img.alt = '';
        flyItem.classList.add('has-image');
        flyItem.appendChild(img);
    }

    document.body.appendChild(flyItem);

    const controlX = (endX - startX) * 0.42;
    const controlY = Math.min(-90, (endY - startY) * 0.45 - 70);
    const frames = [
        { transform: 'translate(-50%, -50%) translate(0, 0) scale(1)', opacity: 1 },
        { transform: `translate(-50%, -50%) translate(${controlX}px, ${controlY}px) scale(.78)`, opacity: .88, offset: .52 },
        { transform: `translate(-50%, -50%) translate(${endX - startX}px, ${endY - startY}px) scale(.3)`, opacity: 0 }
    ];

    const finish = () => {
        flyItem.remove();
        target.animate?.([
            { transform: 'scale(1)' },
            { transform: 'scale(1.08)' },
            { transform: 'scale(1)' }
        ], { duration: 200, easing: 'ease-out' });
    };

    if (flyItem.animate) {
        flyItem.animate(frames, {
            duration: 560,
            easing: 'cubic-bezier(.22,.8,.25,1)',
            fill: 'forwards'
        }).finished.then(finish).catch(finish);
    } else {
        setTimeout(finish, 560);
    }
}

function capNhatTrangThaiNutThem(row, nextRow, tonKhoMoi) {
    const currentButton = row.querySelector('.p-add');
    if (!currentButton) return;

    const nextButton = nextRow?.querySelector('.p-add');
    if (nextButton?.dataset.disabled === 'true' || tonKhoMoi <= 0) {
        currentButton.dataset.disabled = 'true';
        currentButton.setAttribute('aria-disabled', 'true');
        currentButton.style.opacity = '0.5';
        currentButton.style.cursor = 'not-allowed';
        return;
    }

    delete currentButton.dataset.disabled;
    currentButton.removeAttribute('aria-disabled');
    currentButton.style.opacity = '';
    currentButton.style.cursor = '';
}

function capNhatTonKhoSanPhamDangHienThi(doc) {
    const nextRows = new Map();
    doc.querySelectorAll('.p-card[data-spct]').forEach(row => {
        nextRows.set(row.dataset.spct, row);
    });

    document.querySelectorAll('.p-card[data-spct]').forEach(row => {
        const nextRow = nextRows.get(row.dataset.spct);
        if (!nextRow) return;

        const currentStock = row.querySelector('[data-tonkho]');
        const nextStock = nextRow.querySelector('[data-tonkho]');
        if (!currentStock || !nextStock) return;

        currentStock.textContent = nextStock.textContent;
        currentStock.title = nextStock.title || currentStock.title;
        currentStock.className = nextStock.className;

        const tonKhoMoi = Number(String(nextStock.textContent || '').replace(/[^\d-]/g, ''));
        if (Number.isFinite(tonKhoMoi)) {
            capNhatTrangThaiNutThem(row, nextRow, tonKhoMoi);
        }
    });
}

async function capNhatGioHangTuServer(idSpctVuaThem) {
    if (!idHoaDonHienTai) return;

    const cartList = document.querySelector('.cart-list');
    const idSpct = Number(idSpctVuaThem);
    const itemSelector = Number.isInteger(idSpct) && idSpct > 0
        ? `.cart-item[data-spct="${idSpct}"]`
        : null;
    const daCoTrongGio = itemSelector ? Boolean(cartList?.querySelector(itemSelector)) : false;

    const response = await fetch(`${posContextPath()}/ban-hang?id=${encodeURIComponent(idHoaDonHienTai)}`);
    if (!response.ok) throw new BanHangError(`Loi HTTP ${response.status}.`);

    const html = await response.text();
    const doc = new DOMParser().parseFromString(html, 'text/html');
    const selectors = ['.cart-list', '#cart-count', '#sum-tamtinh', '#discount-summary', '#sum-tongcong', '#checkout-total'];

    capNhatTonKhoSanPhamDangHienThi(doc);

    selectors.forEach(selector => {
        const current = document.querySelector(selector);
        const next = doc.querySelector(selector);
        if (!current || !next) return;
        if (selector === '.cart-list') current.innerHTML = next.innerHTML;
        else if (selector === '#discount-summary') current.outerHTML = next.outerHTML;
        else {
            current.textContent = next.textContent;
            if (next.dataset.amount != null) {
                current.dataset.amount = next.dataset.amount;
            }
        }
    });

    await tuDongApVoucherTotNhat();

    if (!daCoTrongGio && itemSelector) {
        const dongMoi = document.querySelector(`.cart-list ${itemSelector}`);
        if (dongMoi) {
            dongMoi.classList.add('cart-item--new');
            dongMoi.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
        }
    }

    let totalItems = 0;
    document.querySelectorAll('.cart-list .cart-item .qty-input').forEach(input => {
        totalItems += parseInt(input.value) || 0;
    });
    const badge = document.querySelector(`.tab-badge[data-badge="${idHoaDonHienTai}"]`);
    if (badge) {
        if (totalItems > 0) {
            badge.textContent = totalItems;
            badge.style.display = 'inline-flex';
        } else {
            badge.style.display = 'none';
        }
    }
}

async function withLoading(element, callback) {
    if (!element || element.disabled) return;
    element.disabled = true;
    element.classList.add('is-loading');
    try {
        await callback();
    } catch (error) {
        hienThiLoi(error instanceof BanHangError ? error.message : 'Khong the xu ly yeu cau.');
        console.error(error);
    } finally {
        element.disabled = false;
        element.classList.remove('is-loading');
    }
}

function debounce(callback, delay) {
    let timer;
    return (...args) => {
        clearTimeout(timer);
        return new Promise((resolve, reject) => {
            timer = setTimeout(async () => {
                try {
                    resolve(await callback(...args));
                } catch (error) {
                    reject(error);
                }
            }, delay);
        });
    };
}

function timSanPham(keyword) {
    return timSanPhamDebounced(keyword || '', '');
}

const timSanPhamDebounced = debounce(async (keyword, idDanhMuc) => {
    const normalizedKeyword = String(keyword || '').trim();
    if (!idHoaDonHienTai || (!normalizedKeyword && !idDanhMuc)) {
        xoaLuoiSanPham();
        return;
    }

    const query = new URLSearchParams({ keyword: normalizedKeyword });
    if (idDanhMuc) query.set('idDanhMuc', idDanhMuc);
    const response = await fetch(`${posContextPath()}/ban-hang/tim-san-pham?${query}`);
    if (!response.ok) throw new BanHangError(`Loi HTTP ${response.status}.`);
    const grid = document.querySelector('.product-grid');
    if (grid) grid.innerHTML = await response.text();
}, 300);

let hangDoiThemSanPham = [];
let timerXuLyThemSanPham = null;
let timerTaiLaiGioHang = null;
let dangXuLyThemSanPham = false;

function datLichTaiLaiGioHang() {
    clearTimeout(timerTaiLaiGioHang);
    timerTaiLaiGioHang = setTimeout(() => {
        if (!dangXuLyThemSanPham && hangDoiThemSanPham.length === 0) {
            window.location.reload();
        }
    }, 700);
}

async function xuLyHangDoiThemSanPham() {
    if (dangXuLyThemSanPham) return;
    dangXuLyThemSanPham = true;
    let soSanPhamThemThanhCong = 0;

    try {
        while (hangDoiThemSanPham.length > 0) {
            const item = hangDoiThemSanPham.shift();
            try {
                await BanHangAPI.goi('themSanPham', {
                    idHoaDon: idHoaDonHienTai,
                    idSpct: item.idSpct,
                    soLuong: 1
                });
                soSanPhamThemThanhCong++;
            } catch (error) {
                hienThiLoi(error instanceof BanHangError
                    ? error.message
                    : 'Khong the them san pham vao hoa don.');
            } finally {
                item.button?.classList.remove('is-loading');
                item.button?.removeAttribute('data-add-pending');
            }
        }
    } finally {
        dangXuLyThemSanPham = false;
        if (soSanPhamThemThanhCong > 0) datLichTaiLaiGioHang();
    }
}

function themSanPham(idSpct, button) {
    if (!idHoaDonHienTai) {
        hienThiLoi('Vui lòng tạo hoặc chọn hóa đơn trước khi thêm sản phẩm.');
        return;
    }
    if (button?.dataset.addPending === 'true') return;

    bayVaoGioHang(button);
    button?.classList.add('is-loading');
    button?.setAttribute('data-add-pending', 'true');

    BanHangAPI.goi('themSanPham', {
        idHoaDon: idHoaDonHienTai,
        idSpct,
        soLuong: 1
    })
        .then(() => capNhatGioHangTuServer(idSpct))
        .catch(error => hienThiLoi(error instanceof BanHangError
            ? error.message
            : 'Khong the them san pham vao hoa don.'))
        .finally(() => {
            button?.classList.remove('is-loading');
            button?.removeAttribute('data-add-pending');
        });
}

async function doiSoLuong(idChiTiet, soLuongMoi, button) {
    if (!idHoaDonHienTai) return;
    const cartItem = button?.closest?.('.cart-item');
    const idSpct = cartItem?.dataset.spct || button?.dataset.spct;
    await withLoading(button, async () => {
        await BanHangAPI.goi('capNhatSoLuong', { idChiTiet, soLuongMoi });
        await capNhatGioHangTuServer(idSpct);
    });
}

async function capNhatSoLuongNhap(input) {
    if (!input || input.dataset.updating === 'true') return;
    const idChiTiet = input.dataset.id;
    const soLuongCu = Number(input.dataset.qty) || 1;
    const soLuongMoi = Number(input.value);

    if (!Number.isInteger(soLuongMoi) || soLuongMoi <= 0) {
        input.value = String(soLuongCu);
        hienThiLoi('Số lượng phải là số nguyên lớn hơn 0.');
        return;
    }
    if (soLuongMoi === soLuongCu) {
        input.value = String(soLuongCu);
        return;
    }

    input.dataset.updating = 'true';
    await doiSoLuong(idChiTiet, soLuongMoi, input);
    input.dataset.updating = 'false';
}

const traCuuKhachHangDebounced = debounce(async (tuKhoa) => {
    if (tuKhoa.length < 2) {
        renderKetQuaKhachHang([]);
        return;
    }
    if (!idHoaDonHienTai) {
        hienThiLoi('Vui lòng tạo hoặc chọn hóa đơn trước khi chọn khách hàng.');
        return;
    }
    const data = await BanHangAPI.goi('traCuuKhachHang', { tuKhoa });
    const ketQua = Array.isArray(data.khachHangs)
        ? data.khachHangs
        : (data.khachHang ? [data.khachHang] : []);
    renderKetQuaKhachHang(ketQua, tuKhoa);
}, 400);

function taoVietTatKhachHang(ten) {
    const parts = String(ten || '').trim().split(/\s+/).filter(Boolean);
    if (parts.length === 0) return 'KL';
    if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
    return `${parts[0][0]}${parts[parts.length - 1][0]}`.toUpperCase();
}

function capNhatKhachHangTrenGiaoDien(khachHang) {
    const laKhachThanhVien = Boolean(khachHang);
    const ten = laKhachThanhVien ? (khachHang.hoTen || 'Khách thành viên') : 'Khách lẻ';
    const thongTinPhu = laKhachThanhVien
        ? [khachHang.soDienThoai, khachHang.maKhachHang].filter(Boolean).join(' · ') || 'Khách thành viên'
        : 'Chưa gắn số điện thoại';

    const avatar = document.querySelector('.cust-avatar');
    const name = document.querySelector('.cust-name');
    const sub = document.querySelector('.cust-sub');
    const guestAction = document.querySelector('.cust-guest-action');

    if (avatar) avatar.textContent = laKhachThanhVien ? taoVietTatKhachHang(ten) : 'KL';
    if (name) name.textContent = ten;
    if (sub) sub.textContent = thongTinPhu;
    if (guestAction) {
        guestAction.innerHTML = laKhachThanhVien
            ? '<button type="button" class="cust-remove-btn" data-customer-guest title="Gỡ khách hàng khỏi hóa đơn" aria-label="Gỡ khách hàng khỏi hóa đơn">&times;</button>'
            : '';
    }
}

function renderKetQuaKhachHang(khachHangs, tuKhoa = '') {
    const container = document.getElementById('ket-qua-khach-hang');
    if (!container) return;

    container.innerHTML = '';
    if (!khachHangs || khachHangs.length === 0) {
        if (tuKhoa) {
            const empty = document.createElement('div');
            empty.className = 'customer-result-empty';
            empty.textContent = 'Không tìm thấy khách hàng phù hợp. Bấm Thêm mới để tạo khách hàng.';
            container.appendChild(empty);
            container.classList.remove('hidden');
            return;
        }
        container.classList.add('hidden');
        return;
    }

    khachHangs.forEach(khachHang => {
        const button = document.createElement('button');
        button.type = 'button';
        button.className = 'customer-result';
        button.dataset.customerSelect = String(khachHang.id);
        button.dataset.customerName = khachHang.hoTen || '';
        button.dataset.customerPhone = khachHang.soDienThoai || '';
        button.dataset.customerCode = khachHang.maKhachHang || '';

        const name = document.createElement('span');
        name.className = 'customer-result-name';
        name.textContent = khachHang.hoTen || 'Khách thành viên';

        const meta = document.createElement('span');
        meta.className = 'customer-result-meta';
        meta.textContent = [
            khachHang.soDienThoai ? `SĐT: ${khachHang.soDienThoai}` : null,
            khachHang.maKhachHang ? `Mã: ${khachHang.maKhachHang}` : null
        ].filter(Boolean).join(' · ') || 'Chưa có thông tin liên hệ';

        button.append(name, meta);
        container.appendChild(button);
    });
    container.classList.remove('hidden');
}

async function ganVaChonKhachHang(button) {
    if (!idHoaDonHienTai) {
        hienThiLoi('Vui lòng tạo hoặc chọn hóa đơn trước khi chọn khách hàng.');
        return;
    }

    const khachHang = {
        id: button.dataset.customerSelect,
        hoTen: button.dataset.customerName,
        soDienThoai: button.dataset.customerPhone,
        maKhachHang: button.dataset.customerCode
    };
    await withLoading(button, async () => {
        await BanHangAPI.goi('ganKhachHang', {
            idHoaDon: idHoaDonHienTai,
            idKhachHang: khachHang.id
        });
        capNhatKhachHangTrenGiaoDien(khachHang);
        renderKetQuaKhachHang([]);
        dongPanelKhachHang();
        await tuDongApVoucherTotNhat();
    });
}

function moModalThemKhachHang(tuKhoa = '') {
    const modal = document.getElementById('customer-create-modal');
    const form = document.getElementById('customer-create-form');
    if (!modal || !form) return;

    form.reset();
    const keyword = String(tuKhoa || document.getElementById('input-sdt')?.value || '').trim();
    if (/^\d+$/.test(keyword)) {
        document.getElementById('input-khach-sdt').value = keyword;
    } else if (keyword) {
        document.getElementById('input-khach-ho-ten').value = keyword;
    }
    modal.classList.remove('hidden');
    modal.setAttribute('aria-hidden', 'false');
    document.getElementById('input-khach-ho-ten')?.focus();
}

function dongModalThemKhachHang() {
    const modal = document.getElementById('customer-create-modal');
    if (!modal) return;
    modal.classList.add('hidden');
    modal.setAttribute('aria-hidden', 'true');
}

async function themVaChonKhachHang(button) {
    if (!idHoaDonHienTai) {
        hienThiLoi('Vui lòng tạo hoặc chọn hóa đơn trước khi chọn khách hàng.');
        return;
    }
    const hoTen = document.getElementById('input-khach-ho-ten')?.value.trim();
    const email = document.getElementById('input-khach-email')?.value.trim();
    const soDienThoai = document.getElementById('input-khach-sdt')?.value.trim();
    const ngaySinh = document.getElementById('input-khach-ngay-sinh')?.value;
    const gioiTinh = document.querySelector('input[name="gioiTinh"]:checked')?.value;
    if (!hoTen || !soDienThoai || gioiTinh === undefined || gioiTinh === null || gioiTinh === '') {
        hienThiLoi('Vui lòng nhập họ tên, số điện thoại và chọn giới tính.');
        return;
    }
    await withLoading(button, async () => {
        const data = await BanHangAPI.goi('traCuuHoacTaoKhachHang', {
            soDienThoai,
            hoTen,
            email,
            ngaySinh,
            gioiTinh
        });
        await BanHangAPI.goi('ganKhachHang', {
            idHoaDon: idHoaDonHienTai,
            idKhachHang: data.khachHang.id
        });
        capNhatKhachHangTrenGiaoDien(data.khachHang);
        renderKetQuaKhachHang([]);
        dongModalThemKhachHang();
        dongPanelKhachHang();
        await tuDongApVoucherTotNhat();
    });
}

let productQrScanner = null;
let productQrLastCode = '';
let productQrCurrentCamera = null;

function capNhatTrangThaiQr(message) {
    const status = document.getElementById('product-qr-status');
    if (status) status.textContent = message;
}

async function dungQuetQr() {
    if (!productQrScanner) return;
    try {
        await productQrScanner.stop();
        await productQrScanner.clear();
    } catch (error) {
        console.warn('Không thể dừng camera QR:', error);
    } finally {
        productQrScanner = null;
    }
}

async function themSanPhamTuMaQr(ma) {
    const maQr = String(ma || '').trim();
    if (!maQr) {
        throw new Error('Mã QR sản phẩm không được để trống.');
    }
    if (!idHoaDonHienTai) {
        throw new Error('Vui lòng tạo hoặc chọn hóa đơn trước khi quét sản phẩm.');
    }

    capNhatTrangThaiQr('Đang tìm sản phẩm...');
    const data = await BanHangAPI.goi('quetQr', { ma: maQr });
    await BanHangAPI.goi('themSanPham', {
        idHoaDon: idHoaDonHienTai,
        idSpct: data.idSanPhamChiTiet,
        soLuong: 1
    });
    if (typeof capNhatGioHangTuServer === 'function') {
        await capNhatGioHangTuServer(data.idSanPhamChiTiet);
    } else {
        window.location.reload();
    }
    alert('Đã thêm sản phẩm vào đơn hàng.');
}

async function startScannerWithCamera(cameraId) {
    try {
        await dungQuetQr();
        productQrScanner = new Html5Qrcode('product-qr-reader');
        await productQrScanner.start(
            cameraId,
            { fps: 10, qrbox: { width: 220, height: 220 } },
            async decodedText => {
                if (productQrLastCode === decodedText) return;
                productQrLastCode = decodedText;
                
                try {
                    await themSanPhamTuMaQr(decodedText);
                    dongQuetQr();
                } catch (error) {
                    capNhatTrangThaiQr(error.message || 'Không tìm thấy sản phẩm tương ứng với mã QR.');
                    setTimeout(() => {
                        productQrLastCode = '';
                        capNhatTrangThaiQr('Đang chờ camera hoặc mã QR.');
                    }, 2500);
                }
            },
            () => {}
        );
        capNhatTrangThaiQr('Đang quét...');
    } catch (error) {
        capNhatTrangThaiQr('Lỗi khởi động camera: ' + error.message);
    }
}

async function moQuetQr() {
    const modal = document.getElementById('product-qr-modal');
    if (!modal) return;
    if (!idHoaDonHienTai) {
        hienThiLoi('Vui lòng tạo hoặc chọn hóa đơn trước khi quét sản phẩm.');
        return;
    }

    productQrLastCode = '';
    modal.classList.remove('hidden');
    modal.setAttribute('aria-hidden', 'false');
    capNhatTrangThaiQr('Đang kết nối camera...');

    if (typeof Html5Qrcode === 'undefined') {
        capNhatTrangThaiQr('Camera QR chưa tải được. Bạn có thể nhập mã thủ công.');
        return;
    }

    const cameraSelect = document.getElementById('qr-camera-select');
    
    try {
        const devices = await Html5Qrcode.getCameras();
        if (devices && devices.length > 0) {
            if (cameraSelect) {
                cameraSelect.innerHTML = '';
                devices.forEach(device => {
                    const option = document.createElement('option');
                    option.value = device.id;
                    option.text = device.label || `Camera ${cameraSelect.options.length + 1}`;
                    cameraSelect.appendChild(option);
                });
                cameraSelect.style.display = 'block';
                
                if (!productQrCurrentCamera || !devices.find(d => d.id === productQrCurrentCamera)) {
                    productQrCurrentCamera = devices[0].id;
                }
                cameraSelect.value = productQrCurrentCamera;
                
                cameraSelect.onchange = async () => {
                    productQrCurrentCamera = cameraSelect.value;
                    await startScannerWithCamera(productQrCurrentCamera);
                };
            }
            await startScannerWithCamera(productQrCurrentCamera || devices[0].id);
        } else {
            if (cameraSelect) cameraSelect.style.display = 'none';
            capNhatTrangThaiQr('Không tìm thấy camera trên thiết bị.');
        }
    } catch (error) {
        if (cameraSelect) cameraSelect.style.display = 'none';
        capNhatTrangThaiQr('Lỗi truy cập camera. Vui lòng cấp quyền.');
    }
}

async function dongQuetQr() {
    await dungQuetQr();
    const modal = document.getElementById('product-qr-modal');
    if (!modal) return;
    modal.classList.add('hidden');
    modal.setAttribute('aria-hidden', 'true');
}

async function chonKhachLe(button) {
    if (!idHoaDonHienTai) return;
    await withLoading(button, async () => {
        await BanHangAPI.goi('chonKhachLe', { idHoaDon: idHoaDonHienTai });
        capNhatKhachHangTrenGiaoDien(null);
        renderKetQuaKhachHang([]);
        dongPanelKhachHang();
        await tuDongApVoucherTotNhat();
    });
}

function dinhDangTienVoucher(value) {
    const amount = Number(value);
    return Number.isFinite(amount)
        ? `${new Intl.NumberFormat('vi-VN').format(amount)} đ`
        : '0 đ';
}

function renderKetQuaVoucher(vouchers, tuKhoa = '') {
    const container = document.getElementById('voucher-suggestions');
    if (!container) return;

    container.innerHTML = '';
    if (!vouchers || vouchers.length === 0) {
        if (tuKhoa) {
            const empty = document.createElement('div');
            empty.className = 'voucher-suggestion-empty';
            empty.textContent = 'Mã voucher không hợp lệ, hết hạn hoặc chưa đủ điều kiện áp dụng.';
            container.appendChild(empty);
            container.classList.remove('hidden');
            return;
        }
        container.classList.add('hidden');
        return;
    }

    vouchers.forEach(voucher => {
        const button = document.createElement('button');
        button.type = 'button';
        button.className = 'voucher-suggestion';
        button.dataset.voucherSelect = String(voucher.id || '');
        button.dataset.voucherCode = voucher.maVoucher || '';
        button.title = [voucher.maVoucher, voucher.tenVoucher, voucher.ngayKetThuc]
            .filter(Boolean).join(' · ');

        const head = document.createElement('span');
        head.className = 'voucher-suggestion__head';
        const code = document.createElement('span');
        code.className = 'voucher-suggestion__code';
        code.textContent = voucher.maVoucher || 'Voucher';
        const discount = document.createElement('span');
        discount.className = 'voucher-suggestion__discount';
        discount.textContent = voucher.loaiGiamGia === 'percent'
            ? `Giảm ${voucher.giaTriGiam || 0}%`
            : `Giảm ${dinhDangTienVoucher(voucher.giaTriGiam)}`;
        head.append(code, discount);

        const meta = document.createElement('span');
        meta.className = 'voucher-suggestion__meta';
        const dieuKien = Number(voucher.donToiThieu || 0) > 0
            ? `Đơn từ ${dinhDangTienVoucher(voucher.donToiThieu)}`
            : 'Không yêu cầu giá trị tối thiểu';
        const giamToiDa = voucher.loaiGiamGia === 'percent' && Number(voucher.giamToiDa || 0) > 0
            ? ` · Tối đa ${dinhDangTienVoucher(voucher.giamToiDa)}`
            : '';
        const hanDung = voucher.ngayKetThuc ? ` · HSD ${voucher.ngayKetThuc}` : '';
        meta.textContent = `${voucher.tenVoucher || 'Voucher đang áp dụng'} · ${dieuKien}${giamToiDa}${hanDung}`;

        button.append(head, meta);
        container.appendChild(button);
    });
    container.classList.remove('hidden');
}

const timVoucherDebounced = debounce(async (tuKhoa) => {
    const keyword = String(tuKhoa || '').trim();
    if (keyword.length < 2 || !idHoaDonHienTai) {
        renderKetQuaVoucher([]);
        return;
    }

    try {
        const data = await BanHangAPI.goi('timVoucher', {
            idHoaDon: idHoaDonHienTai,
            tuKhoa: keyword
        });
        renderKetQuaVoucher(data.vouchers || [], keyword);
    } catch (error) {
        console.error('Không thể tìm voucher:', error);
        renderKetQuaVoucher([], keyword);
    }
}, 350);

function chonVoucher(button) {
    const input = document.getElementById('input-voucher');
    if (!input || !button?.dataset.voucherCode) return;
    input.value = button.dataset.voucherCode;
    renderKetQuaVoucher([]);
    apVoucher(document.querySelector('.voucher-apply'));
}

function moPanelKhachHang() {
    const panel = document.getElementById('panel-khach-hang');
    const trigger = document.querySelector('[data-customer-open]');
    if (!panel) return;

    if (!panel.classList.contains('hidden')) {
        dongPanelKhachHang();
        return;
    }

    panel.classList.remove('hidden');
    trigger?.setAttribute('aria-expanded', 'true');
    document.getElementById('input-sdt')?.focus();
}

function dongPanelKhachHang() {
    document.getElementById('panel-khach-hang')?.classList.add('hidden');
    document.querySelector('[data-customer-open]')?.setAttribute('aria-expanded', 'false');
    renderKetQuaKhachHang([]);
}

async function apVoucher(button) {
    if (!idHoaDonHienTai) {
        hienThiLoi('Vui lòng tạo hoặc chọn hóa đơn trước khi áp dụng voucher.');
        return;
    }
    const input = document.getElementById('input-voucher');
    const maVoucher = input?.value.trim();
    if (!maVoucher) {
        hienThiLoi('Vui lòng nhập mã voucher.');
        input?.focus();
        return;
    }
    renderKetQuaVoucher([]);
    await withLoading(button, async () => {
        await BanHangAPI.goi('apVoucher', { idHoaDon: idHoaDonHienTai, maVoucher });
        alert('Áp dụng voucher thành công.');
        window.location.reload();
    });
}

async function goVoucher(button) {
    if (!idHoaDonHienTai) {
        hienThiLoi('Vui lòng tạo hoặc chọn hóa đơn trước.');
        return;
    }
    await withLoading(button, async () => {
        await BanHangAPI.goi('goVoucher', { idHoaDon: idHoaDonHienTai });
        window.location.reload();
    });
}


// ===================== DROPDOWN HELPERS =====================
let _activeDropdown = null;

function toggleDropdown(which) {
    const listEl = document.getElementById(which + '-dropdown-list');
    const btnEl  = document.getElementById(which + '-dropdown-btn');
    const chevron = document.getElementById(which + '-chevron');
    if (!listEl || !btnEl) return;
    if (which === 'voucher' && btnEl.dataset.locked === 'true') return;

    const isOpen = listEl.style.display !== 'none';

    // Đóng tất cả dropdown đang mở
    closeAllDropdowns();

    if (!isOpen) {
        // Chuyển dropdown ra ngoài body để không bị ảnh hưởng bởi layout card (Ant Design style)
        if (listEl.parentNode !== document.body) {
            document.body.appendChild(listEl);
            listEl.style.position = 'fixed';
            listEl.style.zIndex = '99999';
        }

        // Bật display trước để tính toán chiều cao
        listEl.style.display = which === 'voucher' ? 'flex' : 'block';

        // Lấy toạ độ của nút bấm
        const rect = btnEl.getBoundingClientRect();
        listEl.style.width = rect.width + 'px';
        listEl.style.left = rect.left + 'px';
        
        if (which === 'voucher') {
            // Voucher luôn mở xuống; chiều cao kết thúc tại đáy nút xác nhận thanh toán.
            const checkoutBtn = document.getElementById('checkout-btn');
            const dropdownTop = rect.bottom + 6;
            const viewportBottom = window.innerHeight - 12;
            const checkoutBottom = checkoutBtn
                ? checkoutBtn.getBoundingClientRect().bottom
                : viewportBottom;
            const dropdownBottom = Math.min(checkoutBottom, viewportBottom);
            const availableHeight = Math.max(1, dropdownBottom - dropdownTop);

            listEl.style.top = dropdownTop + 'px';
            listEl.style.bottom = 'auto';
            listEl.style.height = availableHeight + 'px';
            listEl.style.maxHeight = availableHeight + 'px';
        } else {
            // Các dropdown còn lại vẫn tự chọn hướng mở theo khoảng trống màn hình.
            listEl.style.height = '';
            listEl.style.maxHeight = '';
            const spaceBelow = window.innerHeight - rect.bottom;
            const dropdownHeight = listEl.offsetHeight || 350;

            if (spaceBelow < dropdownHeight && rect.top > spaceBelow) {
                listEl.style.top = 'auto';
                listEl.style.bottom = (window.innerHeight - rect.top + 5) + 'px';
            } else {
                listEl.style.bottom = 'auto';
                listEl.style.top = (rect.bottom + 5) + 'px';
            }
        }

        if (btnEl) btnEl.style.borderColor = '#c2103a';
        if (chevron) chevron.style.transform = 'rotate(180deg)';
        _activeDropdown = which;

        if (which === 'voucher') {
            const searchInput = document.getElementById('voucher-search-input');
            if (searchInput) searchInput.value = ''; // Reset input tìm kiếm
            loadVoucherDropdown('');
        }
    }
}

function closeAllDropdowns() {
    ['pttt', 'voucher'].forEach(which => {
        const el = document.getElementById(which + '-dropdown-list');
        const btn = document.getElementById(which + '-dropdown-btn');
        const chev = document.getElementById(which + '-chevron');
        if (el) el.style.display = 'none';
        if (btn) btn.style.borderColor = '';
        if (chev) chev.style.transform = '';
    });
    _activeDropdown = null;
}

// Đóng dropdown khi click ra ngoài
document.addEventListener('click', function(e) {
    if (_activeDropdown && !e.target.closest('#pttt-dropdown-btn') && !e.target.closest('#pttt-dropdown-list')
        && !e.target.closest('#voucher-dropdown-btn') && !e.target.closest('#voucher-dropdown-list')) {
        closeAllDropdowns();
    }
});

// Đóng dropdown khi người dùng cuộn trang ngoài vùng dropdown hoặc thay đổi kích thước cửa sổ
window.addEventListener('scroll', function(e) {
    if (!_activeDropdown) return;
    const activeList = document.getElementById(_activeDropdown + '-dropdown-list');
    // Nếu sự kiện scroll xuất phát từ bên trong danh sách dropdown thì bỏ qua
    if (e.target && e.target.nodeType === 1 && activeList && activeList.contains(e.target)) return;
    
    closeAllDropdowns();
}, true); // Use capture to catch scroll events from any container

window.addEventListener('resize', closeAllDropdowns);

// ===================== PHƯƠNG THỨC THANH TOÁN =====================
function chonPhuongThucDropdown(el) {
    const ma = el.dataset.ma;
    phuongThucThanhToanDangChon = ma;
    closeAllDropdowns();

    // Cập nhật label
    const label = document.getElementById('pttt-selected-label');
    if (label) {
        if (ma === 'PTTT001') {
            label.innerHTML = '<i class="fas fa-money-bill-wave" style="color:#10b981;margin-right:7px;"></i>Tiền mặt';
        } else {
            label.innerHTML = '<i class="fas fa-university" style="color:#3b82f6;margin-right:7px;"></i>Chuyển khoản';
        }
    }

    // Cập nhật dấu check
    document.querySelectorAll('.pttt-option').forEach(opt => {
        const check = opt.querySelector('.fa-check');
        if (check) check.remove();
        opt.style.background = '';
        opt.style.color = 'var(--text-main)';
    });
    const checkIcon = document.createElement('i');
    checkIcon.className = 'fas fa-check';
    checkIcon.style.marginLeft = 'auto';
    checkIcon.style.fontSize = '11px';
    el.appendChild(checkIcon);
    if (ma === 'PTTT001') {
        el.style.background = '#f0fdf4';
        el.style.color = '#10b981';
    } else {
        el.style.background = '#eff6ff';
        el.style.color = '#3b82f6';
    }

    closeAllDropdowns();
}

function chonPhuongThucThanhToan(element) {
    // Legacy: giữ lại cho backward compat
    document.querySelectorAll('.pay-chip').forEach(chip => chip.classList.remove('active'));
    element.classList.add('active');
    phuongThucThanhToanDangChon = element.dataset.ma;
}

// ===================== VOUCHER DROPDOWN =====================
let _voucherDangAp = document.getElementById('voucher-ma-hien-tai')?.value || '';
let _tuDongApVoucherPromise = null;

const xuLyTimVoucherDropdown = debounce((tuKhoa) => {
    loadVoucherDropdown(tuKhoa);
}, 300);

async function loadVoucherDropdown(tuKhoa = '') {
    if (!idHoaDonHienTai) {
        renderVoucherDropdown([]);
        return;
    }
    const itemsEl = document.getElementById('voucher-dropdown-items');
    const loadingEl = document.getElementById('voucher-dropdown-loading');
    
    if (itemsEl) itemsEl.style.display = 'none';
    if (loadingEl) loadingEl.style.display = 'block';

    try {
        const data = await BanHangAPI.goi('timVoucher', { idHoaDon: idHoaDonHienTai, tuKhoa: tuKhoa });
        renderVoucherDropdown(data.vouchers || []);
    } catch (e) {
        if (itemsEl) {
            itemsEl.innerHTML = '<div style="padding:14px;color:#ef4444;font-size:13px;">Không tải được danh sách voucher.</div>';
            itemsEl.style.display = 'block';
        }
    } finally {
        if (loadingEl) loadingEl.style.display = 'none';
    }
}

function formatVoucherDiscount(v) {
    if (v.loaiGiamGia === 'percent') {
        let txt = `Giảm ${v.giaTriGiam}%`;
        if (v.giamToiDa) txt += ` (tối đa ${new Intl.NumberFormat('vi-VN').format(v.giamToiDa)}đ)`;
        return txt;
    }
    return `Giảm ${new Intl.NumberFormat('vi-VN').format(v.giaTriGiam)}đ`;
}

function tinhTienGiamVoucherClient(voucher, tamTinh) {
    if (!voucher || !tamTinh) return 0;
    const giaTriGiam = Number(voucher.giaTriGiam) || 0;
    let tienGiam = 0;
    if (voucher.loaiGiamGia === 'percent') {
        tienGiam = Math.round(tamTinh * giaTriGiam / 100);
        const giamToiDa = Number(voucher.giamToiDa) || 0;
        if (giamToiDa > 0) tienGiam = Math.min(tienGiam, giamToiDa);
    } else {
        tienGiam = giaTriGiam;
    }
    return Math.max(0, Math.min(tienGiam, tamTinh));
}

function layTamTinhHienTai() {
    return parseCurrencyVi(document.getElementById('sum-tamtinh')?.textContent || '0');
}

function timVoucherTotNhat(vouchers, tamTinh = layTamTinhHienTai()) {
    return (vouchers || [])
        .map(voucher => ({
            voucher,
            tienGiam: tinhTienGiamVoucherClient(voucher, tamTinh)
        }))
        .filter(item => item.tienGiam > 0)
        .sort((a, b) => {
            if (b.tienGiam !== a.tienGiam) return b.tienGiam - a.tienGiam;
            return (Number(a.voucher.donToiThieu) || 0) - (Number(b.voucher.donToiThieu) || 0);
        })[0]?.voucher || null;
}

function capNhatNhanVoucherDangAp(voucher, laTuDong = false) {
    const label = document.getElementById('voucher-selected-label');
    if (!label || !voucher) return;
    const badge = laTuDong
        ? '<span class="voucher-best-badge">Tốt nhất</span>'
        : '';
    label.innerHTML = `<i class="fas fa-tag" style="color:#f59e0b;margin-right:7px;"></i><strong>${voucher.maVoucher}</strong>${badge}`;
}

function capNhatThongBaoVoucherTotNhat(coVoucher) {
    const message = document.getElementById('best-voucher-message');
    if (message) message.hidden = !coVoucher;
}

function datHienThiKhongCoVoucher() {
    _voucherDangAp = '';
    const label = document.getElementById('voucher-selected-label');
    if (label) {
        label.innerHTML = '<i class="fas fa-tag" style="color:#cbd5e1;margin-right:7px;"></i><span style="color:var(--text-sub);">-- Không áp dụng --</span>';
    }
    const tamTinh = layTamTinhHienTai();
    capNhatHienThiTongTien(tamTinh, 0);
    capNhatMoTaGiamGia(null, 0);
}

function moTaVoucherClient(voucher) {
    if (!voucher?.maVoucher) return '';
    return `(${voucher.maVoucher} · ${formatVoucherDiscount(voucher)})`;
}

function capNhatMoTaGiamGia(voucher, giamGia) {
    const summary = document.getElementById('discount-summary');
    const desc = document.getElementById('discount-description');
    if (!summary || !desc) return;

    if (!voucher || !giamGia || giamGia <= 0) {
        summary.hidden = true;
        desc.textContent = '';
        capNhatThongBaoVoucherTotNhat(false);
        return;
    }

    summary.hidden = false;
    desc.textContent = moTaVoucherClient(voucher);
    capNhatThongBaoVoucherTotNhat(true);
}

async function revalidateVoucherDangAp() {
    if (!idHoaDonHienTai || !_voucherDangAp) return null;

    const data = await BanHangAPI.goi('revalidateVoucher', { idHoaDon: idHoaDonHienTai });
    const result = data.revalidation || {};
    if (result.voucherRemoved || !result.maVoucher) {
        datHienThiKhongCoVoucher();
    }
    return result;
}

function tuDongApVoucherTotNhat(batBuoc = false) {
    if (!idHoaDonHienTai) return Promise.resolve();
    if (_tuDongApVoucherPromise) return _tuDongApVoucherPromise;

    _tuDongApVoucherPromise = (async () => {
        try {
            await revalidateVoucherDangAp();

            const tamTinh = layTamTinhHienTai();
            if (tamTinh <= 0) {
                if (_voucherDangAp) {
                    await BanHangAPI.goi('goVoucher', { idHoaDon: idHoaDonHienTai });
                    datHienThiKhongCoVoucher();
                }
                return;
            }

            const data = await BanHangAPI.goi('timVoucher', { idHoaDon: idHoaDonHienTai, tuKhoa: '' });
            const voucherTotNhat = timVoucherTotNhat(data.vouchers || [], tamTinh);
            if (!voucherTotNhat) {
                if (_voucherDangAp) {
                    await BanHangAPI.goi('goVoucher', { idHoaDon: idHoaDonHienTai });
                }
                datHienThiKhongCoVoucher();
                return;
            }

            if (voucherTotNhat.maVoucher !== _voucherDangAp) {
                if (_voucherDangAp) {
                    await BanHangAPI.goi('goVoucher', { idHoaDon: idHoaDonHienTai });
                    _voucherDangAp = '';
                }
                await BanHangAPI.goi('apVoucher', { idHoaDon: idHoaDonHienTai, maVoucher: voucherTotNhat.maVoucher });
                _voucherDangAp = voucherTotNhat.maVoucher;
            }

            capNhatNhanVoucherDangAp(voucherTotNhat, true);
            capNhatSoTienSauVoucher(voucherTotNhat);
        } catch (error) {
            console.warn('Khong the tu dong ap voucher tot nhat:', error);
            if (batBuoc) throw error;
        }
    })().finally(() => {
        _tuDongApVoucherPromise = null;
    });

    return _tuDongApVoucherPromise;
}

function renderVoucherDropdown(vouchers) {
    const listEl = document.getElementById('voucher-dropdown-items');
    if (!listEl) return;
    listEl.innerHTML = '';
    listEl.style.display = 'block';

    // Option: Không áp dụng
    const noVoucher = document.createElement('div');
    noVoucher.style.cssText = 'display:flex;align-items:center;gap:10px;padding:11px 14px;cursor:pointer;font-size:13px;color:var(--text-sub);border-bottom:1px solid var(--line);';
    noVoucher.innerHTML = '<i class="fas fa-ban"></i> Không áp dụng voucher';
    noVoucher.addEventListener('mouseenter', () => noVoucher.style.background = '#f8f6f3');
    noVoucher.addEventListener('mouseleave', () => noVoucher.style.background = '');
    noVoucher.addEventListener('click', () => goVoucherDropdown());
    listEl.appendChild(noVoucher);

    if (vouchers.length === 0) {
        const empty = document.createElement('div');
        empty.style.cssText = 'padding:16px;text-align:center;color:var(--text-sub);font-size:13px;';
        empty.textContent = 'Không có voucher khả dụng cho đơn này.';
        listEl.appendChild(empty);
        return;
    }

    const voucherTotNhat = timVoucherTotNhat(vouchers);
    const tamTinh = layTamTinhHienTai();
    const vouchersSapXep = [...vouchers].sort((a, b) => {
        const giamB = tinhTienGiamVoucherClient(b, tamTinh);
        const giamA = tinhTienGiamVoucherClient(a, tamTinh);
        return giamB - giamA;
    });

    vouchersSapXep.forEach(v => {
        const item = document.createElement('div');
        const isSelected = v.maVoucher === _voucherDangAp;
        const isBest = voucherTotNhat?.maVoucher === v.maVoucher;
        item.style.cssText = `padding:12px 14px;cursor:pointer;border-bottom:1px solid var(--line);background:${isSelected ? '#fffbeb' : ''};`;

        const discountTxt = formatVoucherDiscount(v);
        const dkTxt = v.donToiThieu ? `Đơn tối thiểu ${new Intl.NumberFormat('vi-VN').format(v.donToiThieu)}đ` : '';

        item.innerHTML = `
            <div style="display:flex;justify-content:space-between;align-items:center;">
                <div>
                    <span style="font-weight:700;font-size:13px;color:#c2103a;">${v.maVoucher}</span>
                    <span style="margin-left:8px;font-size:12px;color:var(--text-sub);">${v.tenVoucher || ''}</span>
                    ${isBest ? '<span class="voucher-best-badge">Tốt nhất</span>' : ''}
                </div>
                ${isSelected ? '<i class="fas fa-check" style="color:#10b981;"></i>' : ''}
            </div>
            <div style="margin-top:4px;font-size:12px;color:#f59e0b;font-weight:600;">${discountTxt}</div>
            ${dkTxt ? `<div style="margin-top:2px;font-size:11px;color:var(--text-sub);">${dkTxt}</div>` : ''}
        `;
        item.addEventListener('mouseenter', () => { if (!isSelected) item.style.background = '#f8f6f3'; });
        item.addEventListener('mouseleave', () => { if (!isSelected) item.style.background = ''; });
        item.addEventListener('click', () => chonVoucherDropdown(v));
        listEl.appendChild(item);
    });
}

async function chonVoucherDropdown(v) {
    if (!idHoaDonHienTai) return;
    if (document.getElementById('voucher-dropdown-btn')?.dataset.locked === 'true') return;
    closeAllDropdowns();
    try {
        await BanHangAPI.goi('apVoucher', { idHoaDon: idHoaDonHienTai, maVoucher: v.maVoucher });
        _voucherDangAp = v.maVoucher;

        // Cập nhật label
        capNhatNhanVoucherDangAp(v);

        // Tính và cập nhật tiền giảm + tổng cộng
        capNhatSoTienSauVoucher(v);
    } catch (e) {
        alert(e.message || 'Không thể áp dụng voucher.');
    }
}

async function goVoucherDropdown() {
    if (document.getElementById('voucher-dropdown-btn')?.dataset.locked === 'true') return;
    closeAllDropdowns();
    if (!_voucherDangAp || !idHoaDonHienTai) {
        // Chưa có voucher → chỉ đóng
        return;
    }
    try {
        await BanHangAPI.goi('goVoucher', { idHoaDon: idHoaDonHienTai });
        datHienThiKhongCoVoucher();
    } catch (e) {
        alert(e.message || 'Không thể gỡ voucher.');
    }
}

function parseCurrencyVi(str) {
    return parseInt((str || '0').replace(/[^0-9]/g, '')) || 0;
}

function dinhDangTienVi(value) {
    return new Intl.NumberFormat('vi-VN').format(Math.max(0, Number(value) || 0)) + 'đ';
}

function capNhatTienThoiModal() {
    const input = document.getElementById('cash-paid-amount');
    const change = document.getElementById('cash-paid-change');
    const error = document.getElementById('cash-paid-error');
    if (!input || !change || !error) return;

    const tongTien = Number(layTongTienHienThi()) || 0;
    const tienKhachTra = parseCurrencyVi(input.value);
    const tienThoi = tienKhachTra - tongTien;

    change.textContent = dinhDangTienVi(tienThoi > 0 ? tienThoi : 0);
    if (!tienKhachTra) {
        error.textContent = '';
        error.hidden = true;
        return;
    }
    error.textContent = tienThoi < 0 ? 'Số tiền khách trả chưa đủ.' : '';
    error.hidden = tienThoi >= 0;
}

function capNhatSoTienSauVoucher(v) {
    const tamTinh = parseCurrencyVi(document.getElementById('sum-tamtinh')?.textContent || '0');
    let giamGia = 0;
    if (v.loaiGiamGia === 'percent') {
        giamGia = Math.round(tamTinh * v.giaTriGiam / 100);
        if (v.giamToiDa && giamGia > v.giamToiDa) giamGia = v.giamToiDa;
    } else {
        giamGia = v.giaTriGiam;
    }
    if (giamGia > tamTinh) giamGia = tamTinh;
    capNhatHienThiTongTien(tamTinh, giamGia);
    capNhatMoTaGiamGia(v, giamGia);
}

function capNhatHienThiTongTien(tamTinh, giamGia) {
    const tongCong = tamTinh - giamGia;
    const fmt = n => new Intl.NumberFormat('vi-VN').format(n) + 'đ';

    const giamEl = document.getElementById('sum-giamgia');
    if (giamEl) giamEl.textContent = '-' + fmt(giamGia);

    const tongEl = document.getElementById('sum-tongcong');
    if (tongEl) tongEl.textContent = fmt(tongCong);

    const totalEl = document.getElementById('checkout-total');
    if (totalEl) {
        totalEl.dataset.amount = tongCong;
        totalEl.textContent = fmt(tongCong);
    }
}

// Legacy stubs (giữ lại để không break các listener cũ)
async function apVoucher(button) {
    const ma = document.getElementById('input-voucher')?.value?.trim();
    if (!ma || !idHoaDonHienTai) return;
    await BanHangAPI.goi('apVoucher', { idHoaDon: idHoaDonHienTai, maVoucher: ma });
    window.location.reload();
}
async function goVoucher(button) {
    if (!idHoaDonHienTai) return;
    await withLoading(button, async () => {
        await BanHangAPI.goi('goVoucher', { idHoaDon: idHoaDonHienTai });
        window.location.reload();
    });
}

function chonPhuongThucThanhToan(element) {

    document.querySelectorAll('.pay-chip').forEach(chip => chip.classList.remove('active'));
    element.classList.add('active');
    phuongThucThanhToanDangChon = element.dataset.ma;
}

function layTongTienHienThi() {
    const totalElement = document.getElementById('checkout-total');
    const rawAmount = totalElement?.dataset.amount;
    if (rawAmount && /^\d+(?:\.\d+)?$/.test(rawAmount)) {
        return rawAmount;
    }
    const text = totalElement?.textContent || '0';
    const normalized = text.replace(/[^\d.,]/g, '');
    const lastComma = normalized.lastIndexOf(',');
    const lastDot = normalized.lastIndexOf('.');
    const decimalIndex = Math.max(lastComma, lastDot);
    if (decimalIndex >= 0 && normalized.length - decimalIndex - 1 <= 2) {
        const integerPart = normalized.substring(0, decimalIndex).replace(/[.,]/g, '');
        const decimalPart = normalized.substring(decimalIndex + 1);
        return `${integerPart || '0'}.${decimalPart || '0'}`;
    }
    return normalized.replace(/[.,]/g, '') || '0';
}

function moModalXacNhanThanhToan() {
    const modal = document.getElementById('transfer-payment-modal');
    const qrImage = document.getElementById('transfer-payment-qr');
    const qrWrap = document.getElementById('transfer-payment-qr-wrap');
    const title = document.getElementById('transfer-payment-title');
    const hint = modal?.querySelector('.transfer-modal__hint');
    const cashFields = document.getElementById('cash-payment-fields');
    const cashInput = document.getElementById('cash-paid-amount');
    const cashError = document.getElementById('cash-paid-error');
    if (!modal || !qrImage || !qrWrap || !title || !hint) {
        throw new BanHangError('Không tải được màn hình xác nhận thanh toán.');
    }

    const laTienMat = phuongThucThanhToanDangChon === 'PTTT001';
    const amount = layTongTienHienThi();
    const amountText = document.getElementById('checkout-total')?.textContent?.trim() || amount;
    if (laTienMat) {
        title.textContent = 'Xác nhận thanh toán tiền mặt';
        hint.textContent = `Xác nhận đã nhận đủ ${amountText}. Chọn Hủy để giữ hóa đơn ở trạng thái chờ.`;
        qrWrap.hidden = true;
        cashFields?.classList.remove('hidden');
        if (cashInput) {
            cashInput.value = '';
            cashInput.disabled = false;
        }
        if (cashError) {
            cashError.textContent = '';
            cashError.hidden = true;
        }
        hint.textContent = `Nhập số tiền khách trả cho hóa đơn ${amountText}, sau đó bấm Xác nhận thanh toán.`;
        capNhatTienThoiModal();
    } else {
        title.textContent = 'Thanh toán chuyển khoản / QR';
        hint.textContent = `Khách quét QR và chuyển ${amountText}. Sau khi nhận tiền, bấm Xác nhận thanh toán. Chọn Hủy để giữ hóa đơn chờ.`;
        const qrContent = `THANH TOAN HOA DON ${idHoaDonHienTai} SO TIEN ${amount} VND`;
        qrImage.src = `https://api.qrserver.com/v1/create-qr-code/?size=260x260&data=${encodeURIComponent(qrContent)}`;
        qrWrap.hidden = false;
        cashFields?.classList.add('hidden');
        if (cashInput) cashInput.value = '';
    }
    modal.classList.remove('hidden');
    modal.setAttribute('aria-hidden', 'false');
    if (laTienMat) cashInput?.focus();
    else document.getElementById('confirm-transfer-payment')?.focus();
}

function dongModalXacNhanThanhToan() {
    const modal = document.getElementById('transfer-payment-modal');
    if (!modal) return;
    modal.classList.add('hidden');
    modal.setAttribute('aria-hidden', 'true');
    const cashInput = document.getElementById('cash-paid-amount');
    if (cashInput) cashInput.value = '';
}

async function xacNhanThanhToanTuModal(button) {
    await withLoading(button, async () => {
        const params = {
            idHoaDon: idHoaDonHienTai,
            maPttt: phuongThucThanhToanDangChon
        };
        if (phuongThucThanhToanDangChon === 'PTTT001') {
            const cashInput = document.getElementById('cash-paid-amount');
            const cashError = document.getElementById('cash-paid-error');
            const tongTien = Number(layTongTienHienThi()) || 0;
            const tienKhachTra = parseCurrencyVi(cashInput?.value || '');
            if (!tienKhachTra) {
                if (cashError) {
                    cashError.textContent = 'Vui lòng nhập số tiền khách trả.';
                    cashError.hidden = false;
                }
                cashInput?.focus();
                return;
            }
            if (tienKhachTra < tongTien) {
                if (cashError) {
                    cashError.textContent = 'Số tiền khách trả chưa đủ.';
                    cashError.hidden = false;
                }
                cashInput?.focus();
                return;
            }
            params.soTienKhachDua = String(tienKhachTra);
        }
        const data = await BanHangAPI.thanhToan(params);
        thongBaoThongKeThayDoi();
        sauThanhToanThanhCong(data);
    });
}

function sauThanhToanThanhCong(data) {
    const modal = document.getElementById('payment-success-modal');
    if (!modal) {
        window.location.assign(`${posContextPath()}/ban-hang`);
        return;
    }

    const idHoaDonDaThanhToan = data.idHoaDon || idHoaDonHienTai;
    duongDanInHoaDonSauThanhToan = data.printUrl
        || duongDanChiTietHoaDon(idHoaDonDaThanhToan, true);
    const message = document.getElementById('payment-success-message');
    if (message) {
        const maHoaDon = data.maHoaDon ? ` ${data.maHoaDon}` : '';
        message.textContent = `Hóa đơn${maHoaDon} đã được thanh toán thành công.`;
    }

    dongModalXacNhanThanhToan();
    modal.classList.remove('hidden');
    modal.setAttribute('aria-hidden', 'false');
    document.getElementById('print-paid-invoice')?.focus();
}

function quayLaiBanHangSauThanhToan() {
    window.location.assign(`${posContextPath()}/ban-hang`);
}

function inHoaDonSauThanhToan() {
    if (!duongDanInHoaDonSauThanhToan) {
        quayLaiBanHangSauThanhToan();
        return;
    }
    window.location.assign(duongDanInHoaDonSauThanhToan);
}

async function xacNhanThanhToan(button) {
    if (!idHoaDonHienTai) {
        hienThiLoi('Chua co hoa don dang mo.');
        return;
    }

    await withLoading(button, async () => {
        await tuDongApVoucherTotNhat(true);
        const tongTien = Number(layTongTienHienThi());
        if (!tongTien) {
            throw new BanHangError('Hóa đơn chưa có số tiền cần thanh toán.');
        }
        moModalXacNhanThanhToan();
    });
}

function chonTab(idHoaDon) {
    window.location.href = `${posContextPath()}/ban-hang?id=${encodeURIComponent(idHoaDon)}`;
}

async function taoHoaDonMoi(button) {
    await withLoading(button, async () => {
        const data = await BanHangAPI.goi('taoHoaDon');
        const idHoaDonMoi = data.idHoaDon || idHoaDonHienTai;
        thongBaoThongKeThayDoi();
        window.location.href = `${posContextPath()}/ban-hang?id=${encodeURIComponent(idHoaDonMoi)}`;
    });
}

function xoaHoaDonCho(event, idHoaDon) {
    event.stopPropagation();
    const modal = document.getElementById('cancel-invoice-modal');
    const message = document.getElementById('cancel-invoice-message');
    if (!modal) {
        hienThiLoi('Không tải được màn hình xác nhận hủy hóa đơn.');
        return;
    }
    idHoaDonChoXacNhanHuy = Number(idHoaDon);
    if (message) {
        message.textContent = `Bạn có chắc muốn hủy hóa đơn #${idHoaDonChoXacNhanHuy}? `
            + 'Nếu bấm Thoát, hóa đơn vẫn giữ trạng thái Chờ thanh toán.';
    }
    modal.classList.remove('hidden');
    modal.setAttribute('aria-hidden', 'false');
    document.getElementById('confirm-cancel-invoice')?.focus();
}

function dongModalHuyHoaDon() {
    const modal = document.getElementById('cancel-invoice-modal');
    if (!modal) return;
    modal.classList.add('hidden');
    modal.setAttribute('aria-hidden', 'true');
    idHoaDonChoXacNhanHuy = null;
}

async function xacNhanHuyHoaDon(button) {
    if (!Number.isInteger(idHoaDonChoXacNhanHuy) || idHoaDonChoXacNhanHuy <= 0) {
        hienThiLoi('Không xác định được hóa đơn cần hủy.');
        return;
    }
    const idHoaDon = idHoaDonChoXacNhanHuy;
    await withLoading(button, async () => {
        const params = new URLSearchParams({
            idHoaDon,
            lyDo: 'Thu ngân xác nhận hủy hóa đơn chờ trên màn hình POS'
        });
        const response = await fetch(`${posContextPath()}/ban-hang/huy-hoa-don?${params}`, {
            method: 'POST'
        });
        await BanHangAPI.docJson(response);
        thongBaoThongKeThayDoi();
        window.location.reload();
    });
}

document.addEventListener('DOMContentLoaded', () => {
    const search = document.getElementById('search-product');
    search?.addEventListener('keydown', event => {
        if (event.key !== 'Enter') return;
        event.preventDefault();
        if (!idHoaDonHienTai) {
            hienThiLoi('Vui lòng tạo hoặc chọn hóa đơn trước khi quét sản phẩm.');
            return;
        }
        const keyword = search.value.trim();
        if (!keyword) return;
        timSanPham(keyword).catch(error => hienThiLoi(error.message));
        search.value = '';
    });
    search?.addEventListener('input', () => {
        timSanPhamDebounced(search.value, '').catch(error => hienThiLoi(error.message));
    });

    const phone = document.getElementById('input-sdt');
    phone?.addEventListener('input', () => {
        traCuuKhachHangDebounced(phone.value.trim()).catch(error => hienThiLoi(error.message));
    });

    const voucherInput = document.getElementById('input-voucher');
    const voucherButton = document.querySelector('.voucher-apply');
    voucherInput?.addEventListener('input', () => {
        timVoucherDebounced(voucherInput.value).catch(error => console.error(error));
    });
    voucherInput?.addEventListener('keydown', event => {
        if (event.key === 'Enter') {
            event.preventDefault();
            apVoucher(voucherButton);
        }
    });

    const createInvoiceButton = document.getElementById('create-invoice-button');
    const addInvoiceTab = document.querySelector('.tab-add');
    createInvoiceButton?.addEventListener('click', () => taoHoaDonMoi(createInvoiceButton));
    addInvoiceTab?.addEventListener('click', () => taoHoaDonMoi(addInvoiceTab));

    document.addEventListener('click', event => {
        const customerPanel = document.getElementById('panel-khach-hang');
        if (customerPanel && !customerPanel.classList.contains('hidden') && !event.target.closest('.cust-box')) {
            dongPanelKhachHang();
        }
        const voucherSuggestions = document.getElementById('voucher-suggestions');
        if (voucherSuggestions && !event.target.closest('.voucher-input-wrap')) {
            renderKetQuaVoucher([]);
        }

        const add = event.target.closest('.p-add');
        if (add && add.dataset.disabled !== 'true') {
            const idSpct = add.closest('.p-card')?.dataset.spct;
            if (idSpct) themSanPham(idSpct, add);
            return;
        }
        const remove = event.target.closest('.ci-remove');
        if (remove) {
            withLoading(remove, async () => {
                await BanHangAPI.goi('xoaSanPham', {
                    idHoaDon: idHoaDonHienTai,
                    idChiTiet: remove.dataset.id
                });
                window.location.reload();
            });
            return;
        }
        const plus = event.target.closest('.qty-plus');
        if (plus) {
            doiSoLuong(plus.dataset.id, Number(plus.dataset.qty) + 1, plus);
            return;
        }
        const minus = event.target.closest('.qty-minus');
        if (minus) {
            const soLuong = Number(minus.dataset.qty);
            if (soLuong <= 1) {
                withLoading(minus, async () => {
                    await BanHangAPI.goi('xoaSanPham', {
                        idHoaDon: idHoaDonHienTai,
                        idChiTiet: minus.dataset.id
                    });
                    window.location.reload();
                });
            } else {
                doiSoLuong(minus.dataset.id, soLuong - 1, minus);
            }
            return;
        }
        const qtyInput = event.target.closest('.qty-input');
        if (qtyInput) {
            qtyInput.select();
            return;
        }
        const guestCustomer = event.target.closest('[data-customer-guest]');
        if (guestCustomer) {
            chonKhachLe(guestCustomer);
            return;
        }
        if (event.target.closest('[data-customer-open]')) {
            moPanelKhachHang();
            return;
        }
        if (event.target.closest('[data-customer-close]')) {
            dongPanelKhachHang();
            return;
        }
        const openAddCustomer = event.target.closest('[data-customer-create-open]');
        if (openAddCustomer) {
            moModalThemKhachHang();
            return;
        }
        const customerResult = event.target.closest('[data-customer-select]');
        if (customerResult) {
            ganVaChonKhachHang(customerResult);
            return;
        }
        const voucherSuggestion = event.target.closest('[data-voucher-select]');
        if (voucherSuggestion) {
            chonVoucher(voucherSuggestion);
            return;
        }
        const voucher = event.target.closest('.voucher-apply');
        if (voucher) {
            apVoucher(voucher);
            return;
        }
        const removeVoucher = event.target.closest('.voucher-remove');
        if (removeVoucher) {
            goVoucher(removeVoucher);
            return;
        }
        const pay = event.target.closest('.pay-chip');
        if (pay) {
            chonPhuongThucThanhToan(pay);
            return;
        }
        const checkout = event.target.closest('.checkout-btn');
        if (checkout) {
            xacNhanThanhToan(checkout);
            return;
        }
        const tab = event.target.closest('.tab');
        if (tab && !event.target.closest('.btn-close-tab')) {
            event.preventDefault();
            chonTab(tab.dataset.hoadon);
        }
    });

    document.addEventListener('focusout', event => {
        const qtyInput = event.target.closest?.('.qty-input');
        if (qtyInput) {
            capNhatSoLuongNhap(qtyInput).catch(error => hienThiLoi(error.message));
        }
    });

    document.getElementById('close-transfer-payment')?.addEventListener('click', dongModalXacNhanThanhToan);
    document.getElementById('cancel-transfer-payment')?.addEventListener('click', dongModalXacNhanThanhToan);
    document.getElementById('confirm-transfer-payment')?.addEventListener('click', event => {
        xacNhanThanhToanTuModal(event.currentTarget).catch(error => hienThiLoi(error.message));
    });
    document.getElementById('cash-paid-amount')?.addEventListener('input', event => {
        const value = parseCurrencyVi(event.currentTarget.value);
        event.currentTarget.value = value ? new Intl.NumberFormat('vi-VN').format(value) : '';
        capNhatTienThoiModal();
    });
    document.getElementById('cash-paid-amount')?.addEventListener('keydown', event => {
        if (event.key === 'Enter') {
            event.preventDefault();
            document.getElementById('confirm-transfer-payment')?.click();
        }
    });
    document.getElementById('transfer-payment-modal')?.addEventListener('click', event => {
        if (event.target.id === 'transfer-payment-modal') dongModalXacNhanThanhToan();
    });
    document.getElementById('close-customer-create')?.addEventListener('click', dongModalThemKhachHang);
    document.getElementById('cancel-customer-create')?.addEventListener('click', dongModalThemKhachHang);
    document.getElementById('customer-create-modal')?.addEventListener('click', event => {
        if (event.target.id === 'customer-create-modal') dongModalThemKhachHang();
    });
    document.getElementById('customer-create-form')?.addEventListener('submit', event => {
        event.preventDefault();
        const submitButton = event.currentTarget.querySelector('.customer-form-submit');
        themVaChonKhachHang(submitButton).catch(error => hienThiLoi(error.message));
    });
    document.getElementById('skip-print-invoice')?.addEventListener('click', quayLaiBanHangSauThanhToan);
    document.getElementById('print-paid-invoice')?.addEventListener('click', inHoaDonSauThanhToan);
    document.getElementById('close-cancel-invoice')?.addEventListener('click', dongModalHuyHoaDon);
    document.getElementById('exit-cancel-invoice')?.addEventListener('click', dongModalHuyHoaDon);
    document.getElementById('confirm-cancel-invoice')?.addEventListener('click', event => {
        xacNhanHuyHoaDon(event.currentTarget).catch(error => hienThiLoi(error.message));
    });
    document.getElementById('cancel-invoice-modal')?.addEventListener('click', event => {
        if (event.target.id === 'cancel-invoice-modal') dongModalHuyHoaDon();
    });

    tuDongApVoucherTotNhat();

    document.addEventListener('keydown', event => {
        const qtyInput = event.target.closest?.('.qty-input');
        if (qtyInput && event.key === 'Enter') {
            event.preventDefault();
            qtyInput.blur();
            return;
        }
        if (event.key === 'Escape') {
            const paymentSuccessModal = document.getElementById('payment-success-modal');
            if (paymentSuccessModal && !paymentSuccessModal.classList.contains('hidden')) {
                quayLaiBanHangSauThanhToan();
                return;
            }
            dongPanelKhachHang();
            dongModalThemKhachHang();
            dongModalXacNhanThanhToan();
            dongModalHuyHoaDon();
        }
    });

    const openQr = document.getElementById('open-product-qr');
    const closeQr = document.getElementById('close-product-qr');
    const cancelQr = document.getElementById('cancel-product-qr');
    const restartQr = document.getElementById('restart-product-qr');
    const submitQr = document.getElementById('submit-product-qr');
    const qrInput = document.getElementById('product-qr-code');

    openQr?.addEventListener('click', () => moQuetQr().catch(error => hienThiLoi(error.message)));
    closeQr?.addEventListener('click', () => dongQuetQr());
    cancelQr?.addEventListener('click', () => dongQuetQr());
    restartQr?.addEventListener('click', () => moQuetQr().catch(error => hienThiLoi(error.message)));
    submitQr?.addEventListener('click', () => {
        themSanPhamTuMaQr(qrInput?.value).catch(error => capNhatTrangThaiQr(error.message));
    });
    qrInput?.addEventListener('keydown', event => {
        if (event.key === 'Enter') {
            event.preventDefault();
            themSanPhamTuMaQr(qrInput.value).catch(error => capNhatTrangThaiQr(error.message));
        }
    });
});
