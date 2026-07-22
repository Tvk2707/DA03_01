/* POS client: calls the servlet routes used by BanHangController. */

let idHoaDonHienTai = window.idHoaDonHienTai || null;
let phuongThucThanhToanDangChon = 'PTTT001';

function posContextPath() {
    const marker = '/ban-hang';
    const index = window.location.pathname.indexOf(marker);
    return index >= 0 ? window.location.pathname.substring(0, index) : '';
}

class BanHangError extends Error {}

const BanHangAPI = {
    routes: {
        taoHoaDon: ['/ban-hang/tao-hoa-don', 'POST'],
        themSanPham: ['/ban-hang/them-san-pham', 'POST'],
        xoaSanPham: ['/ban-hang/xoa-san-pham', 'POST'],
        capNhatSoLuong: ['/ban-hang/cap-nhat-so-luong', 'POST'],
        traCuuKhachHang: ['/ban-hang/tra-cuu-khach-hang', 'GET'],
        traCuuHoacTaoKhachHang: ['/ban-hang/tra-cuu-khach-hang', 'POST'],
        ganKhachHang: ['/ban-hang/gan-khach-hang', 'POST'],
        apVoucher: ['/ban-hang/ap-voucher', 'POST']
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
        const response = await fetch(posContextPath() + '/thanh-toan/thanh-toan', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
            body: new URLSearchParams(params)
        });
        return this.docJson(response);
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
        timer = setTimeout(() => callback(...args), delay);
    };
}

function timSanPham(keyword) {
    return timSanPhamDebounced(keyword || '', '');
}

const timSanPhamDebounced = debounce(async (keyword, idDanhMuc) => {
    const query = new URLSearchParams({ keyword: keyword || '' });
    if (idDanhMuc) query.set('idDanhMuc', idDanhMuc);
    const response = await fetch(`${posContextPath()}/ban-hang/tim-san-pham?${query}`);
    if (!response.ok) throw new BanHangError(`Loi HTTP ${response.status}.`);
    const grid = document.querySelector('.product-grid');
    if (grid) grid.innerHTML = await response.text();
}, 300);

async function themSanPham(idSpct, button) {
    await withLoading(button, async () => {
        await BanHangAPI.goi('themSanPham', { idHoaDon: idHoaDonHienTai, idSpct, soLuong: 1 });
        window.location.reload();
    });
}

async function doiSoLuong(idChiTiet, soLuongMoi, button) {
    await withLoading(button, async () => {
        await BanHangAPI.goi('capNhatSoLuong', { idChiTiet, soLuongMoi });
        window.location.reload();
    });
}

const traCuuKhachHangDebounced = debounce(async (soDienThoai) => {
    if (soDienThoai.length < 9) return;
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

function moPanelKhachHang() {
    document.getElementById('panel-khach-hang')?.classList.remove('hidden');
}

function dongPanelKhachHang() {
    document.getElementById('panel-khach-hang')?.classList.add('hidden');
}

async function apVoucher(button) {
    const input = document.getElementById('input-voucher');
    const maVoucher = input?.value.trim();
    if (!maVoucher) return;
    await withLoading(button, async () => {
        await BanHangAPI.goi('apVoucher', { idHoaDon: idHoaDonHienTai, maVoucher });
        window.location.reload();
    });
}

function chonPhuongThucThanhToan(element) {
    document.querySelectorAll('.pay-chip').forEach(chip => chip.classList.remove('active'));
    element.classList.add('active');
    phuongThucThanhToanDangChon = element.dataset.ma;
}

function layTongTienHienThi() {
    const text = document.getElementById('checkout-total')?.textContent || '0';
    return text.replace(/[^\d]/g, '') || '0';
}

async function xacNhanThanhToan(button) {
    if (!idHoaDonHienTai) {
        hienThiLoi('Chua co hoa don dang mo.');
        return;
    }
    let soTienKhachDua = layTongTienHienThi();
    if (phuongThucThanhToanDangChon === 'PTTT001') {
        soTienKhachDua = prompt('So tien khach dua:');
        if (soTienKhachDua === null) return;
    }
    await withLoading(button, async () => {
        await BanHangAPI.thanhToan({
            idHoaDon: idHoaDonHienTai,
            maPttt: phuongThucThanhToanDangChon,
            soTienKhachDua: String(soTienKhachDua).replace(/\D/g, '')
        });
        window.location.reload();
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
    search?.addEventListener('input', () => {
        const category = document.querySelector('.chip.active')?.dataset.danhmuc || '';
        timSanPhamDebounced(search.value, category).catch(error => hienThiLoi(error.message));
    });

    const phone = document.getElementById('input-sdt');
    phone?.addEventListener('input', () => {
        traCuuKhachHangDebounced(phone.value.trim()).catch(error => hienThiLoi(error.message));
    });

    document.addEventListener('click', event => {
        const add = event.target.closest('.p-add');
        if (add && add.dataset.disabled !== 'true') {
            const idSpct = add.closest('.p-card')?.dataset.spct;
            if (idSpct) themSanPham(idSpct, add);
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
        if (event.target.closest('.cust-box .link-btn')) {
            moPanelKhachHang();
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
        if (tab && !event.target.closest('.tab-close')) {
            event.preventDefault();
            chonTab(tab.dataset.hoadon);
        }
    });
});
