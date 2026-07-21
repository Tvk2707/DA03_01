document.addEventListener('DOMContentLoaded', function () {
  document.querySelectorAll('.toggle-trang-thai').forEach(function (switchEl) {
    switchEl.addEventListener('change', function () {
      const id = this.dataset.id;
      const trangThaiMoi = this.checked ? 1 : 0;
      const checkboxEl = this;

      const isChiTiet = window.location.pathname.includes('SanPhamChiTiet');
      const url = isChiTiet ? 'SanPhamChiTiet/doiTrangThai' : 'SanPham/doiTrangThai';

      fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `id=${id}&trangThai=${trangThaiMoi}`
      })
        .then(res => res.json())
        .then(data => {
          if (!data.success) {
            checkboxEl.checked = !checkboxEl.checked; // rollback UI
            alert('Cập nhật trạng thái thất bại: ' + (data.message || ''));
          }
          // (tuỳ chọn) hiện toast "Đã cập nhật trạng thái"
        })
        .catch(() => {
          checkboxEl.checked = !checkboxEl.checked; // rollback UI
          alert('Lỗi kết nối, vui lòng thử lại.');
        });
    });
  });
});