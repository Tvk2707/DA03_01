package QuanLySanPham.dao;

import QuanLySanPham.Entity.PhieuGiamGia;

import java.time.LocalDate;
import java.util.List;

public interface PhieuGiamGiaDao extends GenericDao<PhieuGiamGia, Integer> {

    List<PhieuGiamGia> findAllPublicCoupons();

    PhieuGiamGia findPublicCouponById(Integer id);

    PhieuGiamGia savePublicCoupon(PhieuGiamGia coupon);

    PhieuGiamGia updatePublicCoupon(PhieuGiamGia coupon);

    void updatePublicCouponStatus(Integer couponId, Integer status);

    List<PhieuGiamGia> findAllPersonalCoupons();

    PhieuGiamGia findPersonalCouponById(Integer id);

    PhieuGiamGia findByMaVoucher(String maVoucher);

    String generateNextVoucherCode();

    PhieuGiamGia savePersonalCoupon(PhieuGiamGia coupon, Integer customerId);

    PhieuGiamGia updatePersonalCoupon(PhieuGiamGia coupon, Integer customerId);

    void updatePersonalCouponStatus(Integer couponId, Integer status);

    List<PhieuGiamGia> searchAndFilter(String keyword, String maVoucher, String tenVoucher,
                                        String loaiPhieu, String loaiGiamGia, String trangThai,
                                        LocalDate denNgay, int page, int pageSize);

    List<PhieuGiamGia> searchAndFilterForExport(String keyword, String maVoucher, String tenVoucher,
                                                 String loaiPhieu, String loaiGiamGia, String trangThai,
                                                 LocalDate denNgay);

    int countFiltered(String keyword, String maVoucher, String tenVoucher,
                      String loaiPhieu, String loaiGiamGia, String trangThai,
                      LocalDate denNgay);

    PhieuGiamGia getById(Integer id);

    void insert(PhieuGiamGia coupon);

    void updateCoupon(PhieuGiamGia coupon);

    void updateStatus(Integer couponId, Integer status);

    boolean checkDuplicateCode(String maVoucher, Integer currentId);

}
