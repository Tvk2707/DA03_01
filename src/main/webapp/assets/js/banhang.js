/* POS client: calls the servlet routes used by BanHangController. */

let idHoaDonHienTai = window.idHoaDonHienTai || null;
let phuongThucThanhToanDangChon = 'PTTT001';

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
    const category = document.querySelector('.chip.active')?.dataset.danhmuc || '';
    return timSanPhamDebounced(keyword || '', category);
}

const timSanPhamDebounced = debounce(async (keyword, idDanhMuc) => {
    const query = new URLSearchParams({ keyword: keyword || '' });
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
    // Cho phép chọn nhiều sản phẩm trước khi đồng bộ lại giao diện.
    clearTimeout(timerTaiLaiGioHang);
    hangDoiThemSanPham.push({ idSpct, button });
    button?.classList.add('is-loading');
    button?.setAttribute('data-add-pending', 'true');

    clearTimeout(timerXuLyThemSanPham);
    timerXuLyThemSanPham = setTimeout(() => {
        xuLyHangDoiThemSanPham().catch(error => {
            console.error('POS add product queue error:', error);
        });
    }, 350);
}

async function doiSoLuong(idChiTiet, soLuongMoi, button) {
    if (!idHoaDonHienTai) return;
    await withLoading(button, async () => {
        await BanHangAPI.goi('capNhatSoLuong', { idChiTiet, soLuongMoi });
        window.location.reload();
    });
}

const traCuuKhachHangDebounced = debounce(async (soDienThoai) => {
    if (soDienThoai.length < 9) return;
    if (!idHoaDonHienTai) {
        hienThiLoi('Vui lòng tạo hoặc chọn hóa đơn trước khi chọn khách hàng.');
        return;
    }
    const data = await BanHangAPI.goi('traCuuKhachHang', { soDienThoai });
    const formMoi = document.getElementById('form-them-khach-nhanh');
    if (data.found) {
        formMoi?.classList.add('hidden');
        await BanHangAPI.goi('ganKhachHang', {
            idHoaDon: idHoaDonHienTai,
            idKhachHang: data.khachHang.id
        });
        window.location.reload();
    } else {
        formMoi?.classList.remove('hidden');
        const input = document.getElementById('input-sdt-moi');
        if (input) input.value = soDienThoai;
    }
}, 400);

async function themVaChonKhachHang(button) {
    if (!idHoaDonHienTai) {
        hienThiLoi('Vui lòng tạo hoặc chọn hóa đơn trước khi chọn khách hàng.');
        return;
    }
    const soDienThoai = document.getElementById('input-sdt-moi')?.value.trim();
    const hoTen = document.getElementById('input-ten-moi')?.value.trim();
    if (!soDienThoai || !hoTen) {
        hienThiLoi('Vui long nhap ho ten va so dien thoai.');
        return;
    }
    await withLoading(button, async () => {
        const data = await BanHangAPI.goi('traCuuHoacTaoKhachHang', { soDienThoai, hoTen });
        await BanHangAPI.goi('ganKhachHang', {
            idHoaDon: idHoaDonHienTai,
            idKhachHang: data.khachHang.id
        });
        window.location.reload();
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
        window.location.reload();
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

function chonPhuongThucThanhToan(element) {
    document.querySelectorAll('.pay-chip').forEach(chip => chip.classList.remove('active'));
    element.classList.add('active');
    phuongThucThanhToanDangChon = element.dataset.ma;
    capNhatTienMat();
}

function layTongTienHienThi() {
    const text = document.getElementById('checkout-total')?.textContent || '0';
    return text.replace(/[^\d]/g, '') || '0';
}

function parseSoTien(value) {
    const digits = String(value ?? '').replace(/[^\d]/g, '');
    return digits ? Number(digits) : 0;
}

function dinhDangTien(value) {
    return new Intl.NumberFormat('vi-VN').format(Math.max(0, value || 0)) + ' đ';
}

function capNhatTienMat() {
    const panel = document.getElementById('cash-payment-panel');
    const input = document.getElementById('cash-amount');
    const change = document.getElementById('cash-change');
    const error = document.getElementById('cash-payment-error');
    if (!panel || !input || !change || !error) return;

    const laTienMat = phuongThucThanhToanDangChon === 'PTTT001';
    panel.hidden = !laTienMat;
    if (!laTienMat) return;

    const tongTien = parseSoTien(layTongTienHienThi());
    const tienKhachDua = parseSoTien(input.value);
    const tienThoi = tienKhachDua - tongTien;
    change.textContent = dinhDangTien(tienThoi > 0 ? tienThoi : 0);

    if (!tienKhachDua) {
        error.hidden = true;
        return;
    }
    error.textContent = tienThoi < 0 ? 'Số tiền khách đưa chưa đủ.' : '';
    error.hidden = tienThoi >= 0;
}

function laThanhToanChuyenKhoan() {
    return ['PTTT002', 'PTTT004', 'CK', 'THE'].includes(phuongThucThanhToanDangChon);
}

function moModalThanhToanChuyenKhoan() {
    const modal = document.getElementById('transfer-payment-modal');
    const qrImage = document.getElementById('transfer-payment-qr');
    const qrWrap = document.getElementById('transfer-payment-qr-wrap');
    const title = document.getElementById('transfer-payment-title');
    const hint = modal?.querySelector('.transfer-modal__hint');
    const transactionInput = document.getElementById('transfer-transaction-code');
    if (!modal || !qrImage || !transactionInput) {
        throw new BanHangError('Không tải được màn hình thanh toán chuyển khoản.');
    }

    const laThe = phuongThucThanhToanDangChon === 'PTTT004';
    const amount = layTongTienHienThi();
    if (laThe) {
        title.textContent = 'Thanh toán thẻ ngân hàng';
        hint.textContent = 'Khách thanh toán trên máy POS, sau đó nhập mã giao dịch để xác nhận.';
        qrWrap.hidden = true;
        transactionInput.placeholder = 'Ví dụ: POS260722123456';
        document.getElementById('transfer-payment-note').value = 'Thanh toán bằng thẻ ngân hàng';
    } else {
        title.textContent = 'Thanh toán chuyển khoản / QR';
        hint.textContent = 'Khách quét mã QR, hoàn tất chuyển khoản rồi nhập mã giao dịch ngân hàng để xác nhận.';
        const qrContent = `THANH TOAN HOA DON ${idHoaDonHienTai} SO TIEN ${amount} VND`;
        qrImage.src = `https://api.qrserver.com/v1/create-qr-code/?size=260x260&data=${encodeURIComponent(qrContent)}`;
        qrWrap.hidden = false;
        transactionInput.placeholder = 'Ví dụ: FT260722123456';
        document.getElementById('transfer-payment-note').value = 'Thanh toán bằng QR/chuyển khoản';
    }
    transactionInput.value = '';
    modal.classList.remove('hidden');
    modal.setAttribute('aria-hidden', 'false');
    transactionInput.focus();
}

function dongModalThanhToanChuyenKhoan() {
    const modal = document.getElementById('transfer-payment-modal');
    if (!modal) return;
    modal.classList.add('hidden');
    modal.setAttribute('aria-hidden', 'true');
}

async function xacNhanThanhToanChuyenKhoan(button) {
    const maGiaoDich = document.getElementById('transfer-transaction-code')?.value.trim();
    const ghiChu = document.getElementById('transfer-payment-note')?.value.trim();
    if (!maGiaoDich) {
        hienThiLoi('Vui lòng nhập mã giao dịch ngân hàng sau khi kiểm tra đã nhận tiền.');
        return;
    }

    await withLoading(button, async () => {
        const data = await BanHangAPI.thanhToan({
            idHoaDon: idHoaDonHienTai,
            maPttt: phuongThucThanhToanDangChon,
            soTienKhachDua: layTongTienHienThi(),
            maGiaoDich,
            ghiChu
        });
        sauThanhToanThanhCong(data);
    });
}

function sauThanhToanThanhCong(data) {
    const detailUrl = data.detailUrl || duongDanChiTietHoaDon(data.idHoaDon || idHoaDonHienTai);
    alert('Thanh toán thành công. Đang mở chi tiết hóa đơn để xem và in.');
    window.location.assign(detailUrl);
}

async function xacNhanThanhToan(button) {
    if (!idHoaDonHienTai) {
        hienThiLoi('Chua co hoa don dang mo.');
        return;
    }

    if (laThanhToanChuyenKhoan()) {
        try {
            moModalThanhToanChuyenKhoan();
        } catch (error) {
            hienThiLoi(error instanceof BanHangError ? error.message : 'Không thể mở thanh toán QR.');
        }
        return;
    }

    const tongTien = parseSoTien(layTongTienHienThi());
    let soTienKhachDua = tongTien;
    if (phuongThucThanhToanDangChon === 'PTTT001') {
        soTienKhachDua = parseSoTien(document.getElementById('cash-amount')?.value);
        if (!soTienKhachDua) {
            hienThiLoi('Vui lòng nhập số tiền khách đưa.');
            document.getElementById('cash-amount')?.focus();
            return;
        }
        if (soTienKhachDua < tongTien) {
            hienThiLoi('Số tiền khách đưa chưa đủ.');
            return;
        }
    }
    await withLoading(button, async () => {
        const data = await BanHangAPI.thanhToan({
            idHoaDon: idHoaDonHienTai,
            maPttt: phuongThucThanhToanDangChon,
            soTienKhachDua: String(soTienKhachDua)
        });
        if (phuongThucThanhToanDangChon === 'PTTT001') {
            alert('Thanh toán tiền mặt thành công. Tiền thối lại: '
                + dinhDangTien(data.tienThoi || 0)
                + '. Đang mở chi tiết hóa đơn để xem và in.');
            window.location.assign(data.detailUrl || duongDanChiTietHoaDon(data.idHoaDon || idHoaDonHienTai));
            return;
        }
        sauThanhToanThanhCong(data);
    });
}

function chonTab(idHoaDon) {
    window.location.href = `${posContextPath()}/ban-hang?id=${encodeURIComponent(idHoaDon)}`;
}

async function taoHoaDonMoi(button) {
    await withLoading(button, async () => {
        await BanHangAPI.goi('taoHoaDon');
        window.location.reload();
    });
}

function xoaHoaDonCho(event, idHoaDon) {
    event.stopPropagation();
    if (!confirm('Ban co chac muon huy hoa don #' + idHoaDon + ' khong?')) return;
    const params = new URLSearchParams({
        idHoaDon,
        lyDo: 'Thu ngan huy don cho tren man hinh POS'
    });
    fetch(`${posContextPath()}/ban-hang/huy-hoa-don?${params}`, { method: 'POST' })
        .then(response => BanHangAPI.docJson(response))
        .then(() => window.location.reload())
        .catch(error => hienThiLoi(error.message));
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
        const category = document.querySelector('.chip.active')?.dataset.danhmuc || '';
        timSanPhamDebounced(search.value, category).catch(error => hienThiLoi(error.message));
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

    const cashInput = document.getElementById('cash-amount');
    cashInput?.addEventListener('input', () => {
        const value = parseSoTien(cashInput.value);
        cashInput.value = value ? new Intl.NumberFormat('vi-VN').format(value) : '';
        capNhatTienMat();
    });
    capNhatTienMat();

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
        const chip = event.target.closest('.chip');
        if (chip) {
            document.querySelectorAll('.chip').forEach(item => item.classList.remove('active'));
            chip.classList.add('active');
            timSanPhamDebounced(search?.value || '', chip.dataset.danhmuc || '')
                .catch(error => hienThiLoi(error.message));
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
        const addCustomer = event.target.closest('.btn-them-khach');
        if (addCustomer) {
            themVaChonKhachHang(addCustomer);
            return;
        }
        const voucher = event.target.closest('.voucher-apply');
        if (voucher) {
            apVoucher(voucher);
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

    document.getElementById('close-transfer-payment')?.addEventListener('click', dongModalThanhToanChuyenKhoan);
    document.getElementById('cancel-transfer-payment')?.addEventListener('click', dongModalThanhToanChuyenKhoan);
    document.getElementById('confirm-transfer-payment')?.addEventListener('click', event => {
        xacNhanThanhToanChuyenKhoan(event.currentTarget).catch(error => hienThiLoi(error.message));
    });
    document.getElementById('transfer-payment-modal')?.addEventListener('click', event => {
        if (event.target.id === 'transfer-payment-modal') dongModalThanhToanChuyenKhoan();
    });

    document.addEventListener('keydown', event => {
        if (event.key === 'Escape') {
            dongPanelKhachHang();
            dongModalThanhToanChuyenKhoan();
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
