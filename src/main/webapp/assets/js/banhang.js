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
        apVoucher: ['/ban-hang/ap-voucher', 'POST'],
        goVoucher: ['/ban-hang/go-voucher', 'POST'],
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
    const selectors = ['.cart-list', '#cart-count', '#sum-tamtinh', '#sum-giamgia', '#sum-tongcong', '#checkout-total'];

    selectors.forEach(selector => {
        const current = document.querySelector(selector);
        const next = doc.querySelector(selector);
        if (!current || !next) return;
        if (selector === '.cart-list') current.innerHTML = next.innerHTML;
        else {
            current.textContent = next.textContent;
            if (next.dataset.amount != null) {
                current.dataset.amount = next.dataset.amount;
            }
        }
    });

    if (!daCoTrongGio && itemSelector) {
        const dongMoi = document.querySelector(`.cart-list ${itemSelector}`);
        if (dongMoi) {
            dongMoi.classList.add('cart-item--new');
            dongMoi.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
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
    await withLoading(button, async () => {
        await BanHangAPI.goi('capNhatSoLuong', { idChiTiet, soLuongMoi });
        window.location.reload();
    });
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
        meta.textContent = [khachHang.soDienThoai, khachHang.maKhachHang].filter(Boolean).join(' · ') || 'Chưa có thông tin liên hệ';

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
    const gioiTinh = document.getElementById('input-khach-gioi-tinh')?.value;
    if (!hoTen || !soDienThoai || gioiTinh === '') {
        hienThiLoi('Vui long nhap ho ten, so dien thoai va gioi tinh.');
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
    });
}

let productQrScanner = null;
let productQrLastCode = '';

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
        hienThiLoi('Mã QR sản phẩm không được để trống.');
        return;
    }
    if (!idHoaDonHienTai) {
        hienThiLoi('Vui lòng tạo hoặc chọn hóa đơn trước khi quét sản phẩm.');
        return;
    }

    capNhatTrangThaiQr('Đang tìm sản phẩm...');
    const data = await BanHangAPI.goi('quetQr', { ma: maQr });
    await BanHangAPI.goi('themSanPham', {
        idHoaDon: idHoaDonHienTai,
        idSpct: data.idSanPhamChiTiet,
        soLuong: 1
    });
    await dungQuetQr();
    window.location.reload();
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
    capNhatTrangThaiQr('Đang chờ camera hoặc mã QR.');

    if (typeof Html5Qrcode === 'undefined') {
        capNhatTrangThaiQr('Camera QR chưa tải được. Bạn có thể nhập mã thủ công.');
        return;
    }

    await dungQuetQr();
    productQrScanner = new Html5Qrcode('product-qr-reader');
    try {
        await productQrScanner.start(
            { facingMode: 'environment' },
            { fps: 10, qrbox: { width: 220, height: 220 } },
            async decodedText => {
                if (productQrLastCode) return;
                productQrLastCode = decodedText;
                await dungQuetQr();
                try {
                    await themSanPhamTuMaQr(decodedText);
                } catch (error) {
                    productQrLastCode = '';
                    capNhatTrangThaiQr(error.message || 'Không thể thêm sản phẩm từ mã QR.');
                }
            },
            () => {}
        );
    } catch (error) {
        await dungQuetQr();
        capNhatTrangThaiQr('Không mở được camera. Bạn có thể nhập mã thủ công.');
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
    });
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
    } else {
        title.textContent = 'Thanh toán chuyển khoản / QR';
        hint.textContent = `Khách quét QR và chuyển ${amountText}. Sau khi nhận tiền, bấm Xác nhận thanh toán. Chọn Hủy để giữ hóa đơn chờ.`;
        const qrContent = `THANH TOAN HOA DON ${idHoaDonHienTai} SO TIEN ${amount} VND`;
        qrImage.src = `https://api.qrserver.com/v1/create-qr-code/?size=260x260&data=${encodeURIComponent(qrContent)}`;
        qrWrap.hidden = false;
    }
    modal.classList.remove('hidden');
    modal.setAttribute('aria-hidden', 'false');
    document.getElementById('confirm-transfer-payment')?.focus();
}

function dongModalXacNhanThanhToan() {
    const modal = document.getElementById('transfer-payment-modal');
    if (!modal) return;
    modal.classList.add('hidden');
    modal.setAttribute('aria-hidden', 'true');
}

async function xacNhanThanhToanTuModal(button) {
    await withLoading(button, async () => {
        const data = await BanHangAPI.thanhToan({
            idHoaDon: idHoaDonHienTai,
            maPttt: phuongThucThanhToanDangChon
        });
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

    const tongTien = Number(layTongTienHienThi());
    if (!tongTien) {
        hienThiLoi('Hóa đơn chưa có số tiền cần thanh toán.');
        return;
    }
    moModalXacNhanThanhToan();
}

function chonTab(idHoaDon) {
    window.location.href = `${posContextPath()}/ban-hang?id=${encodeURIComponent(idHoaDon)}`;
}

async function taoHoaDonMoi(button) {
    await withLoading(button, async () => {
        const data = await BanHangAPI.goi('taoHoaDon');
        const idHoaDonMoi = data.idHoaDon || idHoaDonHienTai;
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

    document.getElementById('close-transfer-payment')?.addEventListener('click', dongModalXacNhanThanhToan);
    document.getElementById('cancel-transfer-payment')?.addEventListener('click', dongModalXacNhanThanhToan);
    document.getElementById('confirm-transfer-payment')?.addEventListener('click', event => {
        xacNhanThanhToanTuModal(event.currentTarget).catch(error => hienThiLoi(error.message));
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

    document.addEventListener('keydown', event => {
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
