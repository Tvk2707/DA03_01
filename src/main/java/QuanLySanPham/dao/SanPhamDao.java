package main.QuanLySanPham.BE.dao;

import main.QuanLySanPham.BE.Entity.SanPham;
import java.util.List;

/**
 * Interface DAO cho entity SanPham
 */
public interface SanPhamDao extends GenericDao<SanPham, Integer> {

    /**
     * Tìm sản phẩm theo tên (Chỉ lấy sản phẩm chưa bị xóa)
     */
    SanPham findByTenSanPham(String ten);

    /**
     * Lấy danh sách sản phẩm theo danh mục (Chỉ lấy sản phẩm chưa bị xóa)
     */
    List<SanPham> findByDanhMuc(Integer danhMucId);

    /**
     * Lấy danh sách sản phẩm theo thương hiệu (Chỉ lấy sản phẩm chưa bị xóa)
     */
    List<SanPham> findByThuongHieu(Integer thuongHieuId);

    /**
     * Tìm kiếm sản phẩm theo tên, danh mục, thương hiệu, và khoảng giá
     * Nếu giá trị nào là null, bỏ qua điều kiện đó
     * (Chỉ trả về các sản phẩm chưa bị xóa)
     */
    List<SanPham> search(String tenSanPham, Integer danhMucId, Integer thuongHieuId,
                         Double giaMin, Double giaMax);

    // --- CÁC HÀM CƠ BẢN ĐƯỢC OVERRIDE ĐỂ LỌC XÓA MỀM ---
    // Nếu GenericDao đã có sẵn thì không cần khai báo lại cũng được,
    // nhưng khai báo rõ giúp code minh bạch hơn.

    /**
     * Lấy danh sách tất cả sản phẩm chưa bị xóa
     */
    List<SanPham> findAll();

    /**
     * Tìm sản phẩm theo ID (Chỉ trả về nếu chưa bị xóa)
     */
    SanPham findById(Integer id);

    // Hàm delete(Integer id) đã có sẵn trong GenericDao,
    // ta chỉ override lại logic ở lớp Impl chứ không cần khai báo lại ở đây.
}