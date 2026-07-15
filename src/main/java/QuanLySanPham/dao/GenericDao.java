package QuanLySanPham.dao;

import java.util.List;

/**
 * Interface DAO chung cho tất cả entity
 * @param <T> Kiểu entity
 * @param <ID> Kiểu primary key
 */
public interface GenericDao<T, ID> {
    
    /**
     * Lưu entity mới vào database
     */
    T save(T entity);
    
    /**
     * Cập nhật entity đã tồn tại
     */
    T update(T entity);
    
    /**
     * Xóa entity khỏi database
     */
    void delete(T entity);
    
    /**
     * Xóa entity theo ID
     */
    void deleteById(ID id);
    
    /**
     * Tìm entity theo ID
     */
    T findById(ID id);
    
    /**
     * Lấy tất cả entity
     */
    List<T> findAll();
    
    /**
     * Lấy entity có phân trang
     * @param pageNumber Trang (bắt đầu từ 1)
     * @param pageSize Số lượng bản ghi mỗi trang
     */
    List<T> findWithPaging(int pageNumber, int pageSize);
    
    /**
     * Đếm tổng số entity
     */
    long count();
}
