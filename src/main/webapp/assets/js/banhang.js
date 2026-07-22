/**
 * banhang.js — Kết nối giao diện ban-hang.jsp (theo cấu trúc pos-mockup.html) với Servlet /ban-hang, /thanh-toan
 * Include file này ở cuối <body> của ban-hang.jsp, SAU khối script khai báo:
 *     const idHoaDonHienTai = ${idHoaDonDangTao};
 */

// ============================================================
// 0. STATE + API WRAPPER
// ============================================================

let idHoaDonHienTai = window.idHoaDonHienTai || null; // JSP phải set biến này trước khi include file
let phuongThucThanhToanDangChon = 'TIEN_MAT';

const BanHangAPI = {
    async goi(action, params = {}) {
        const body = new URLSearchParams({ action, ...params });
        try {
            const res = await fetch('ban-hang', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body
            });
            // Kiểm tra nếu backend chuyển hướng
            if (res.redirected) {
                window.location.href = res.url;
                return { success: true, redirected: true };
            }
            const data = await res.json();
            if (!data.success) throw new BanHangError(data.message || 'Có lỗi xảy ra');
            return data;
        } catch (error) {
            // Xử lý lỗi mạng hoặc JSON parse error
            console.error('Lỗi API:', error);
            throw new BanHangError('Không thể kết nối đến máy chủ hoặc có lỗi xử lý dữ liệu.');
        }
    },
    async thanhToan(params) {
        const res = await fetch('thanh-toan', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams(params)
        });
        const data = await res.json();
        if (!data.success) throw new BanHangError(data.message || 'Thanh toán thất bại');
        return data;
    }
};

class BanHangError extends Error {}

function formatTien(n) {
    return new Intl.NumberFormat('vi-VN').format(n) + 'đ';
}

function hienThiLoi(msg) {
    // TODO: thay bằng toast riêng nếu có — tạm dùng alert để không phụ thuộc thư viện ngoài
    alert(msg);
}

async function withLoading(el, fn) {
    if (!el || el.disabled) return;
    el.disabled = true;
    el.classList.add('is-loading');
    try {
        await fn();
    } catch (err) {
        if (err instanceof BanHangError) hienThiLoi(err.message);
        else { hienThiLoi('Không kết nối được máy chủ, thử lại.'); console.error(err); }
    } finally {
        if (el) {
            el.disabled = false;
            el.classList.remove('is-loading');
        }
    }
}

function debounce(fn, delay) {
    let t;
    return (...args) => { clearTimeout(t); t = setTimeout(() => fn(...args), delay); };
}

// ============================================================
// 1. RENDER — GIỎ HÀNG (.cart-list bên trong .right-col)
// ============================================================

function renderGioHang(gioHang) {
    const list = document.querySelector('.cart-list');
    if (!list) return;

    if (!gioHang.items || gioHang.items.length === 0) {
        list.innerHTML = `<div class="cart-empty">Chưa có sản phẩm nào trong giỏ</div>`;
    } else {
        list.innerHTML = gioHang.items.map(it => `
            <div class="cart-item" data-id="${it.idChiTiet}">
                <div class="ci-thumb">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6">
                        <circle cx="6.5" cy="12" r="3.2"/><circle cx="17.5" cy="12" r="3.2"/><path d="M9.7 12h4.6"/>
                    </svg>
                </div>
                <div class="ci-body">
                    <div class="ci-name">${escapeHtml(it.tenSp)}</div>
                    <div class="ci-variant">${escapeHtml(it.bienThe || '')}</div>
                    <div class="ci-row">
                        <div class="qty-stepper">
                            <button class="qty-minus" data-id="${it.idChiTiet}" data-qty="${it.soLuong}">–</button>
                            <span>${it.soLuong}</span>
                            <button class="qty-plus" data-id="${it.idChiTiet}" data-qty="${it.soLuong}" data-spct="${it.idSpct}">+</button>
                        </div>
                        <div class="ci-price">${formatTien(it.tongTienDong)}</div>
                    </div>
                </div>
            </div>
        `).join('');
    }

    document.getElementById('cart-count').textContent = (gioHang.items || []).length;

    const tamTinh = gioHang.tamTinh ?? gioHang.tongTien;
    const giamGia = gioHang.giamGia ?? 0;
    document.getElementById('sum-tamtinh').textContent = formatTien(tamTinh);
    document.getElementById('sum-giamgia').textContent = giamGia > 0 ? '– ' + formatTien(giamGia) : formatTien(0);
    document.getElementById('sum-tongcong').textContent = formatTien(gioHang.tongTien);
    document.getElementById('checkout-total').textContent = formatTien(gioHang.tongTien);
}

// ============================================================
// 2. RENDER — KHÁCH HÀNG (.cust-box)
// ============================================================

function renderKhachHang(kh) {
    const box = document.querySelector('.cust-box');
    if (!box) return;

    if (!kh) {
        box.querySelector('.cust-avatar').textContent = 'KL';
        box.querySelector('.cust-name').textContent = 'Khách lẻ';
        box.querySelector('.cust-sub').textContent = 'Chưa gắn số điện thoại';
        box.dataset.idKhachHang = '';
        return;
    }
    const initials = (kh.hoTen || 'KH').trim().split(/\s+/).slice(-2).map(w => w[0]).join('').toUpperCase();
    box.querySelector('.cust-avatar').textContent = initials;
    box.querySelector('.cust-name').textContent = kh.hoTen;
    box.querySelector('.cust-sub').textContent = kh.soDienThoai;
    box.dataset.idKhachHang = kh.id;
}

// ============================================================
// 3. RENDER — LƯỚI SẢN PHẨM (.product-grid)
// ============================================================

function renderLuoiSanPham(danhSach) {
    const grid = document.querySelector('.product-grid');
    if (!grid) return;

    if (danhSach.length === 0) {
        grid.innerHTML = `<div class="grid-empty">Không tìm thấy sản phẩm phù hợp</div>`;
        return;
    }

    grid.innerHTML = danhSach.map(sp => `
        <div class="p-card" data-spct="${sp.id}">
            <div class="p-thumb">
                <svg width="34" height="34" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6">
                    <circle cx="6.5" cy="12" r="3.2"/><circle cx="17.5" cy="12" r="3.2"/><path d="M9.7 12h4.6M3 12l-1.5-1M21 12l1.5-1"/>
                </svg>
            </div>
            <div class="p-name">${escapeHtml(sp.tenSp)}</div>
            <div class="p-meta ${sp.tonKho <= 3 ? 'stock-low' : ''}" data-tonkho>Còn ${sp.tonKho} · ${escapeHtml(sp.moTaBienThe || '')}</div>
            <div class="p-bottom">
                <div class="p-price">${formatTien(sp.gia)}</div>
                <div class="p-add" ${sp.tonKho <= 0 ? 'data-disabled="true"' : ''}>+</div>
            </div>
        </div>
    `).join('');
}

function capNhatTonKhoTrenCard(idSpct, tonMoi) {
    const card = document.querySelector(`.p-card[data-spct="${idSpct}"]`);
    if (!card) return;
    const meta = card.querySelector('[data-tonkho]');
    if (meta) {
        meta.textContent = meta.textContent.replace(/Còn \d+/, `Còn ${tonMoi}`);
        meta.classList.toggle('stock-low', tonMoi <= 3);
    }
    const addBtn = card.querySelector('.p-add');
    if (addBtn) {
        if (tonMoi <= 0) addBtn.setAttribute('data-disabled', 'true');
        else addBtn.removeAttribute('data-disabled');
    }
}

function escapeHtml(str) {
    const div = document.createElement('div');
    div.textContent = str ?? '';
    return div.innerHTML;
}

// ============================================================
// 4. LUỒNG — THÊM / SỬA / XÓA SẢN PHẨM TRONG GIỎ
// ============================================================

async function themSanPham(idSpct, btn) {
    await withLoading(btn, async () => {
        const data = await BanHangAPI.goi('themSanPham', { idHoaDon: idHoaDonHienTai, idSpct, soLuong: 1 });
        if (data.redirected) return;
        renderGioHang(data.gioHang);
        capNhatTonKhoTrenCard(idSpct, data.tonKhoMoi);
    });
}

async function doiSoLuong(idChiTiet, soLuongMoi, btn) {
    await withLoading(btn, async () => {
        const data = await BanHangAPI.goi('capNhatSoLuong', { idChiTiet, soLuongMoi });
        if (data.redirected) return;
        renderGioHang(data.gioHang);
        if (data.tonKhoMoi !== undefined && data.idSpct) {
            capNhatTonKhoTrenCard(data.idSpct, data.tonKhoMoi);
        }
    });
}

// ============================================================
// 5. LUỒNG — TÌM SẢN PHẨM + LỌC DANH MỤC
// ============================================================

const timSanPhamDebounced = debounce(async (keyword, idDanhMuc) => {
    const data = await BanHangAPI.goi('sanPhams', { keyword: keyword || '', idDanhMuc: idDanhMuc || '' });
    if (data.redirected) return;
    renderLuoiSanPham(data.sanPhams);
}, 300);

// ============================================================
// 6. LUỒNG — KHÁCH HÀNG
// ============================================================

const traCuuKhachHangDebounced = debounce(async (sdt) => {
    if (sdt.length < 9) return;
    const data = await BanHangAPI.goi('traCuuKhachHang', { soDienThoai: sdt });
    if (data.redirected) return;
    const formMoi = document.getElementById('form-them-khach-nhanh');
    if (data.found) {
        renderKhachHang(data.khachHang);
        formMoi?.classList.add('hidden');
    } else {
        formMoi?.classList.remove('hidden');
        document.getElementById('input-sdt-moi').value = sdt;
    }
}, 400);

async function themVaChonKhachHang(btn) {
    const sdt = document.getElementById('input-sdt-moi').value.trim();
    const hoTen = document.getElementById('input-ten-moi').value.trim();
    if (!sdt) return hienThiLoi('Vui lòng nhập số điện thoại');

    await withLoading(btn, async () => {
        const data = await BanHangAPI.goi('themKhachHang', { idHoaDon: idHoaDonHienTai, soDienThoai: sdt, hoTen });
        if (data.redirected) return;
        renderKhachHang(data.khachHang);
        document.getElementById('form-them-khach-nhanh')?.classList.add('hidden');
        dongPanelKhachHang();
    });
}

function moPanelKhachHang() { document.getElementById('panel-khach-hang')?.classList.remove('hidden'); }
function dongPanelKhachHang() { document.getElementById('panel-khach-hang')?.classList.add('hidden'); }

// ============================================================
// 7. LUỒNG — VOUCHER
// ============================================================

async function apVoucher(btn) {
    const input = document.getElementById('input-voucher');
    const ma = input.value.trim();
    if (!ma) return;
    await withLoading(btn, async () => {
        const data = await BanHangAPI.goi('apVoucher', { idHoaDon: idHoaDonHienTai, maVoucher: ma });
        if (data.redirected) return;
        renderGioHang(data.gioHang);
        input.value = '';
    });
}

// ============================================================
// 8. LUỒNG — PHƯƠNG THỨC THANH TOÁN + THANH TOÁN
// ============================================================

function chonPhuongThucThanhToan(chipEl) {
    document.querySelectorAll('.pay-chip').forEach(c => c.classList.remove('active'));
    chipEl.classList.add('active');
    phuongThucThanhToanDangChon = chipEl.dataset.ma;
}

async function xacNhanThanhToan(btn) {
    if (!idHoaDonHienTai) return hienThiLoi('Chưa có hóa đơn đang mở');

    let soTienDua = '0';
    if (phuongThucThanhToanDangChon === 'TIEN_MAT') {
        soTienDua = prompt('Số tiền khách đưa:');
        if (soTienDua === null) return; // hủy
    }

    await withLoading(btn, async () => {
        const data = await BanHangAPI.thanhToan({
            idHoaDon: idHoaDonHienTai,
            maPttt: phuongThucThanhToanDangChon,
            soTienKhachDua: soTienDua || '0'
        });
        window.location.href = data.redirectUrl;
    });
}

// ============================================================
// 9. LUỒNG — TAB HÓA ĐƠN CHỜ
// ============================================================

async function chonTab(idHoaDon, tabEl) {
    // Chuyển hướng đến trang với id hóa đơn được chọn
    window.location.href = `ban-hang?id=${idHoaDon}`;
}

async function taoHoaDonMoi(btn) {
    await withLoading(btn, async () => {
        // API sẽ xử lý việc tạo hóa đơn và backend sẽ tự chuyển hướng.
        // Fetch API sẽ tự động theo dõi chuyển hướng và cập nhật window.location.
        const data = await BanHangAPI.goi('taoHoaDon');
        // Nếu không có chuyển hướng tự động (ví dụ: API trả về JSON), xử lý ở đây.
        if (data && !data.redirected) {
             // Fallback nếu backend không redirect: giả sử nó trả về id hóa đơn mới
            if (data.newInvoiceId) {
                window.location.href = `ban-hang?id=${data.newInvoiceId}`;
            }
        }
    });
}


// ============================================================
// 10. GẮN SỰ KIỆN — EVENT DELEGATION (chạy 1 lần khi trang load)
// ============================================================

document.addEventListener('DOMContentLoaded', () => {

    // --- Ô tìm sản phẩm ---
    const oTim = document.getElementById('search-product');
    if (oTim) {
        oTim.addEventListener('input', () => {
            const idDanhMuc = document.querySelector('.chip.active')?.dataset.danhmuc || '';
            timSanPhamDebounced(oTim.value, idDanhMuc);
        });
    }

    // --- Ô tra cứu SĐT khách hàng ---
    const oSdt = document.getElementById('input-sdt');
    if (oSdt) {
        oSdt.addEventListener('input', () => traCuuKhachHangDebounced(oSdt.value.trim()));
    }

    // --- Nút tạo đơn hàng chính ---
    const btnTaoDonHang = document.querySelector('.pos-btn-solid');
    if (btnTaoDonHang) {
        btnTaoDonHang.addEventListener('click', (e) => {
            taoHoaDonMoi(e.currentTarget);
        });
    }


    // --- Click chung toàn trang (delegation) ---
    document.addEventListener('click', (e) => {

        // Thêm sản phẩm vào giỏ
        const addBtn = e.target.closest('.p-add');
        if (addBtn && !addBtn.dataset.disabled) {
            const idSpct = addBtn.closest('.p-card')?.dataset.spct;
            if (idSpct) themSanPham(idSpct, addBtn);
            return;
        }

        // Tăng số lượng
        const plusBtn = e.target.closest('.qty-plus');
        if (plusBtn) {
            const idChiTiet = plusBtn.dataset.id;
            const qtyMoi = parseInt(plusBtn.dataset.qty, 10) + 1;
            doiSoLuong(idChiTiet, qtyMoi, plusBtn);
            return;
        }

        // Giảm số lượng (về 0 -> backend tự xóa dòng, xem hướng dẫn tích hợp mục 3.5)
        const minusBtn = e.target.closest('.qty-minus');
        if (minusBtn) {
            const idChiTiet = minusBtn.dataset.id;
            const qtyMoi = parseInt(minusBtn.dataset.qty, 10) - 1;
            doiSoLuong(idChiTiet, qtyMoi, minusBtn);
            return;
        }

        // Lọc theo danh mục
        const chip = e.target.closest('.chip');
        if (chip) {
            document.querySelectorAll('.chip').forEach(c => c.classList.remove('active'));
            chip.classList.add('active');
            timSanPhamDebounced(oTim?.value || '', chip.dataset.danhmuc || '');
            return;
        }

        // Đổi khách hàng
        const linkDoiKhach = e.target.closest('.cust-box .link-btn');
        if (linkDoiKhach) { moPanelKhachHang(); return; }

        // Thêm nhanh khách hàng mới
        const btnThemKhach = e.target.closest('.btn-them-khach');
        if (btnThemKhach) { themVaChonKhachHang(btnThemKhach); return; }

        // Áp voucher
        const btnVoucher = e.target.closest('.voucher-apply');
        if (btnVoucher) { apVoucher(btnVoucher); return; }

        // Chọn phương thức thanh toán
        const payChip = e.target.closest('.pay-chip');
        if (payChip) { chonPhuongThucThanhToan(payChip); return; }

        // Thanh toán
        const checkoutBtn = e.target.closest('.checkout-btn');
        if (checkoutBtn) { xacNhanThanhToan(checkoutBtn); return; }

        // Chuyển tab hóa đơn
        const tab = e.target.closest('.tab');
        if (tab) {
            // Ngăn chặn hành vi mặc định nếu có và gọi hàm chọn tab
            e.preventDefault();
            chonTab(tab.dataset.hoadon, tab);
            return;
        }

        // Thêm tab hóa đơn mới
        const tabAdd = e.target.closest('.tab-add');
        if (tabAdd) {
            taoHoaDonMoi(tabAdd);
            return;
        }

        function loadSanPham(keyword = "") {
            fetch(contextPath + "/ban-hang/tim-san-pham?keyword=" + encodeURIComponent(keyword))
                .then(response => response.text())
                .then(html => {
                    document.getElementById("productContainer").innerHTML = html;
                });
        }

        let currentHoaDon = null;

        function themSanPham(card) {

            if (!currentHoaDon) {
                alert("Vui lòng chọn hóa đơn!");
                return;
            }

            fetch(contextPath + "/ban-hang/them-san-pham", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: new URLSearchParams({
                    idHoaDon: currentHoaDon,
                    idSanPhamChiTiet: card.dataset.id,
                    soLuong: 1
                })
            })
                .then(res => res.json())
                .then(data => {
                    if(data.success){
                        alert("Đã thêm sản phẩm");
                    }else{
                        alert(data.message);
                    }
                });

        }
        document.addEventListener("DOMContentLoaded", function () {

            loadSanPham();

        });
    });
});