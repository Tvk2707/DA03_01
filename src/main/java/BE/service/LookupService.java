package BE.service;

import BE.Entity.*;
import java.util.List;

/**
 * Interface LookupService - quản lý các bảng tra cứu
 * Gộp các thao tác CRUD cho: DanhMuc, ThuongHieu, ChatLieu, KieuDang, MauSac,
 * KichCo, TrongKinh, GongKinh, HinhDangGong, KieuQuaiKinh
 */
public interface LookupService {
    
    // ===== DANH MUC =====
    DanhMuc themDanhMuc(DanhMuc danhMuc);
    DanhMuc capNhatDanhMuc(DanhMuc danhMuc);
    void xoaDanhMuc(Integer id);
    DanhMuc layDanhMucTheoId(Integer id);
    List<DanhMuc> layTatCaDanhMuc();
    List<DanhMuc> layDanhMucCoPhanTrang(int pageNumber, int pageSize);
    long demDanhMuc();
    
    // ===== THUONG HIEU =====
    ThuongHieu themThuongHieu(ThuongHieu thuongHieu);
    ThuongHieu capNhatThuongHieu(ThuongHieu thuongHieu);
    void xoaThuongHieu(Integer id);
    ThuongHieu layThuongHieuTheoId(Integer id);
    List<ThuongHieu> layTatCaThuongHieu();
    List<ThuongHieu> layThuongHieuCoPhanTrang(int pageNumber, int pageSize);
    long demThuongHieu();
    
    // ===== CHAT LIEU =====
    ChatLieu themChatLieu(ChatLieu chatLieu);
    ChatLieu capNhatChatLieu(ChatLieu chatLieu);
    void xoaChatLieu(Integer id);
    ChatLieu layChatLieuTheoId(Integer id);
    List<ChatLieu> layTatCaChatLieu();
    List<ChatLieu> layChatLieuCoPhanTrang(int pageNumber, int pageSize);
    long demChatLieu();
    
    // ===== KIEU DANG =====
    KieuDang themKieuDang(KieuDang kieuDang);
    KieuDang capNhatKieuDang(KieuDang kieuDang);
    void xoaKieuDang(Integer id);
    KieuDang layKieuDangTheoId(Integer id);
    List<KieuDang> layTatCaKieuDang();
    List<KieuDang> layKieuDangCoPhanTrang(int pageNumber, int pageSize);
    long demKieuDang();
    
    // ===== MAU SAC =====
    MauSac themMauSac(MauSac mauSac);
    MauSac capNhatMauSac(MauSac mauSac);
    void xoaMauSac(Integer id);
    MauSac layMauSacTheoId(Integer id);
    List<MauSac> layTatCaMauSac();
    List<MauSac> layMauSacCoPhanTrang(int pageNumber, int pageSize);
    long demMauSac();
    
    // ===== KICH CO =====
    KichCo themKichCo(KichCo kichCo);
    KichCo capNhatKichCo(KichCo kichCo);
    void xoaKichCo(Integer id);
    KichCo layKichCoTheoId(Integer id);
    List<KichCo> layTatCaKichCo();
    List<KichCo> layKichCoCoPhanTrang(int pageNumber, int pageSize);
    long demKichCo();
    
    // ===== TRONG KINH =====
    TrongKinh themTrongKinh(TrongKinh trongKinh);
    TrongKinh capNhatTrongKinh(TrongKinh trongKinh);
    void xoaTrongKinh(Integer id);
    TrongKinh layTrongKinhTheoId(Integer id);
    List<TrongKinh> layTatCaTrongKinh();
    List<TrongKinh> layTrongKinhCoPhanTrang(int pageNumber, int pageSize);
    long demTrongKinh();
    
    // ===== GONG KINH =====
    GongKinh themGongKinh(GongKinh gongKinh);
    GongKinh capNhatGongKinh(GongKinh gongKinh);
    void xoaGongKinh(Integer id);
    GongKinh layGongKinhTheoId(Integer id);
    List<GongKinh> layTatCaGongKinh();
    List<GongKinh> layGongKinhCoPhanTrang(int pageNumber, int pageSize);
    long demGongKinh();
    
    // ===== HINH DANG GONG =====
    HinhDangGong themHinhDangGong(HinhDangGong hinhDangGong);
    HinhDangGong capNhatHinhDangGong(HinhDangGong hinhDangGong);
    void xoaHinhDangGong(Integer id);
    HinhDangGong layHinhDangGongTheoId(Integer id);
    List<HinhDangGong> layTatCaHinhDangGong();
    List<HinhDangGong> layHinhDangGongCoPhanTrang(int pageNumber, int pageSize);
    long demHinhDangGong();
    
    // ===== KIEU QUAI KINH =====
    KieuQuaiKinh themKieuQuaiKinh(KieuQuaiKinh kieuQuaiKinh);
    KieuQuaiKinh capNhatKieuQuaiKinh(KieuQuaiKinh kieuQuaiKinh);
    void xoaKieuQuaiKinh(Integer id);
    KieuQuaiKinh layKieuQuaiKinhTheoId(Integer id);
    List<KieuQuaiKinh> layTatCaKieuQuaiKinh();
    List<KieuQuaiKinh> layKieuQuaiKinhCoPhanTrang(int pageNumber, int pageSize);
    long demKieuQuaiKinh();
}
