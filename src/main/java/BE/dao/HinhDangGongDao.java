package BE.dao;

import BE.Entity.DanhMuc;
import BE.Entity.HinhDangGong;
import BE.Entity.KichCo;

import java.util.List;

/**
 * Interface DAO cho entity HinhDangGong
 */
public interface HinhDangGongDao extends GenericDao<HinhDangGong, Integer> {
    
    /**
     * Tìm hình dáng gọng theo tên
     */
    HinhDangGong findByTen(String ten);

}
