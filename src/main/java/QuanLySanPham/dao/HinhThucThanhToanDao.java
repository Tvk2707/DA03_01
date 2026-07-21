package QuanLySanPham.dao;

import QuanLySanPham.Entity.HinhThucThanhToan;

public interface HinhThucThanhToanDao extends GenericDao<HinhThucThanhToan, Integer> {
    HinhThucThanhToan findByMa(String ma);
}
