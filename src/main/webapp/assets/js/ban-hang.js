/**
 * ban-hang.js
 * Client-side logic for the Point of Sale (POS) page.
 */

// Helper function to display toast messages
function showToast(message, isSuccess = true) {
    const container = document.getElementById('toast-container') || document.body;
    const toast = document.createElement('div');
    toast.className = `toast-custom ${isSuccess ? 'toast-success-style' : 'toast-error-style'}`;
    toast.innerHTML = `<i class="fas ${isSuccess ? 'fa-check-circle' : 'fa-circle-exclamation'}"></i><span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = "0";
        toast.style.transform = "translateY(-10px)";
        setTimeout(() => toast.remove(), 500);
    }, 3000);
}

// Placeholder function to re-render the entire cart from server data
function renderCartFromServer(idHoaDon) {
    console.log(`Requesting updated cart for invoice ID: ${idHoaDon}`);
    showToast('Giỏ hàng đã được cập nhật.', true);
}

/**
 * Action 1: Create a new pending invoice
 */
async function taoHoaDon() {
    try {
        const response = await fetch('/ban-hang/tao-hoa-don', { method: 'POST' });
        const data = await response.json();
        if (response.ok && data.success) {
            showToast('Đã tạo hóa đơn mới thành công!');
            console.log('New invoice created:', data.hoaDon);
            return data.hoaDon;
        } else {
            throw new Error(data.message || 'Không thể tạo hóa đơn mới.');
        }
    } catch (error) {
        showToast(error.message, false);
        console.error('Error creating invoice:', error);
    }
}

/**
 * Action 2: Search for products by keyword
 * @param {string} tuKhoa The search keyword.
 */
async function timSanPham(tuKhoa = '') {
    try {
        const response = await fetch(`/ban-hang/tim-san-pham?keyword=${encodeURIComponent(tuKhoa)}`);
        if (!response.ok) throw new Error('Lỗi khi tải danh sách sản phẩm.');
        const html = await response.text();
        const productGridContainer = document.getElementById('product-grid-container');
        if (productGridContainer) productGridContainer.innerHTML = html;
    } catch (error) {
        showToast(error.message, false);
        console.error('Error searching products:', error);
    }
}

/**
 * Action 3: Add a product to the cart
 * @param {number} idHoaDon
 * @param {number} idSpct
 * @param {number} soLuong
 */
async function themSanPham(idHoaDon, idSpct, soLuong) {
    try {
        const response = await fetch('/ban-hang/them-san-pham', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({ idHoaDon, idSanPhamChiTiet: idSpct, soLuong })
        });
        const data = await response.json();
        if (response.ok && data.success) {
            renderCartFromServer(idHoaDon);
        } else {
            throw new Error(data.message || 'Thêm sản phẩm thất bại.');
        }
    } catch (error) {
        showToast(error.message, false);
        console.error('Error adding product:', error);
    }
}

/**
 * Action 4: Remove a product from the cart
 * @param {number} idHoaDon
 * @param {number} idChiTiet
 */
async function xoaSanPham(idHoaDon, idChiTiet) {
    try {
        const response = await fetch('/ban-hang/xoa-san-pham', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({ idHoaDon, idChiTiet })
        });
        const data = await response.json();
        if (response.ok && data.success) {
            renderCartFromServer(idHoaDon);
        } else {
            throw new Error(data.message || 'Xóa sản phẩm thất bại.');
        }
    } catch (error) {
        showToast(error.message, false);
        console.error('Error removing product:', error);
    }
}

/**
 * Action 5: Update product quantity in the cart
 * @param {number} idChiTiet
 * @param {number} soLuongMoi
 */
async function capNhatSoLuong(idChiTiet, soLuongMoi) {
    try {
        const response = await fetch('/ban-hang/cap-nhat-so-luong', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({ idChiTiet, soLuongMoi })
        });
        const data = await response.json();
        if (response.ok && data.success) {
            renderCartFromServer(null); // Pass a real invoice ID here
        } else {
            throw new Error(data.message || 'Cập nhật số lượng thất bại.');
        }
    } catch (error) {
        showToast(error.message, false);
        console.error('Error updating quantity:', error);
    }
}

/**
 * Action 6: Find or create a customer
 * @param {string} sdt Phone number
 * @param {string|null} hoTen Full name (optional, for creation)
 */
async function traCuuKhachHang(sdt, hoTen = null) {
    try {
        const isCreating = hoTen !== null;
        const response = await fetch('/ban-hang/tra-cuu-khach-hang', {
            method: isCreating ? 'POST' : 'GET',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: isCreating ? new URLSearchParams({ soDienThoai: sdt, hoTen: hoTen }) : null
        });
        const data = await response.json();
        if (response.ok && data.success) {
            return data.khachHang;
        } else {
            throw new Error(data.message || 'Không tìm thấy khách hàng.');
        }
    } catch (error) {
        showToast(error.message, false);
        console.error('Error finding/creating customer:', error);
        return null;
    }
}

/**
 * Action 7: Assign a customer to an invoice
 * @param {number} idHoaDon
 * @param {number} idKhachHang
 */
async function ganKhachHang(idHoaDon, idKhachHang) {
    try {
        const response = await fetch('/ban-hang/gan-khach-hang', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({ idHoaDon, idKhachHang })
        });
        const data = await response.json();
        if (response.ok && data.success) {
            showToast('Đã gán khách hàng vào hóa đơn.');
            renderCartFromServer(idHoaDon);
        } else {
            throw new Error(data.message || 'Gán khách hàng thất bại.');
        }
    } catch (error) {
        showToast(error.message, false);
        console.error('Error assigning customer:', error);
    }
}

/**
 * Action 8: Apply a voucher to an invoice
 * @param {number} idHoaDon
 * @param {string} maVoucher
 */
async function apVoucher(idHoaDon, maVoucher) {
    try {
        const response = await fetch('/ban-hang/ap-voucher', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({ idHoaDon, maVoucher })
        });
        const data = await response.json();
        if (response.ok && data.success) {
            showToast('Áp dụng voucher thành công!');
            renderCartFromServer(idHoaDon);
        } else {
            throw new Error(data.message || 'Áp dụng voucher thất bại.');
        }
    } catch (error) {
        showToast(error.message, false);
        console.error('Error applying voucher:', error);
    }
}

/**
 * Action 9: Cancel an invoice
 * @param {number} idHoaDon
 */
async function huyHoaDon(idHoaDon) {
    if (confirm('Bạn có chắc chắn muốn hủy hóa đơn này không? Hành động này không thể hoàn tác.')) {
        try {
            const lyDo = prompt("Nhập lý do hủy (không bắt buộc):", "");
            const response = await fetch('/ban-hang/huy-hoa-don', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams({ idHoaDon, lyDo: lyDo || '' })
            });
            const data = await response.json();
            if (response.ok && data.success) {
                showToast('Đã hủy hóa đơn thành công.');
                // TODO: Remove the invoice tab from the UI
            } else {
                throw new Error(data.message || 'Hủy hóa đơn thất bại.');
            }
        } catch (error) {
            showToast(error.message, false);
            console.error('Error canceling invoice:', error);
        }
    }
}

/**
 * Action 10: Confirm payment for an invoice
 * @param {number} idHoaDon
 * @param {string} maPttt
 * @param {number} soTienKhachDua
 */
async function xacNhanThanhToan(idHoaDon, maPttt, soTienKhachDua) {
    try {
        const response = await fetch('/thanh-toan/thanh-toan', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({ idHoaDon, maPttt, soTienKhachDua })
        });
        const data = await response.json();
        if (response.ok && data.success) {
            showToast('Thanh toán thành công!');
            // TODO: Close payment modal, maybe show print invoice option
        } else {
            throw new Error(data.message || 'Thanh toán thất bại.');
        }
    } catch (error) {
        showToast(error.message, false);
        console.error('Error during payment:', error);
    }
}


document.addEventListener('DOMContentLoaded', () => {
    // Wiring examples
});
