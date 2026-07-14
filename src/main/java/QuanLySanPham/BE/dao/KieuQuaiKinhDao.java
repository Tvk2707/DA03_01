package BE.dao;

import BE.Entity.DanhMuc;
import BE.Entity.KieuQuaiKinh;

import java.util.List;

/**
 * Interface DAO cho entity KieuQuaiKinh
 */
public interface KieuQuaiKinhDao extends GenericDao<KieuQuaiKinh, Integer> {
    
    /**
     * Tìm kiểu quai kính theo tên
     */
    KieuQuaiKinh findByTen(String ten);

}
