package BE.service.impl;

import BE.Entity.*;
import BE.dao.*;
import BE.dao.impl.*;
import BE.service.LookupService;
import java.util.List;

/**
 * LookupService implementation - quản lý các bảng tra cứu
 */
public class LookupServiceImpl implements LookupService {
    
    private final DanhMucDao danhMucDao = new DanhMucDaoImpl();
    private final ThuongHieuDao thuongHieuDao = new ThuongHieuDaoImpl();
    private final ChatLieuDao chatLieuDao = new ChatLieuDaoImpl();
    private final KieuDangDao kieuDangDao = new KieuDangDaoImpl();
    private final MauSacDao mauSacDao = new MauSacDaoImpl();
    private final KichCoDao kichCoDao = new KichCoDaoImpl();
    private final TrongKinhDao trongKinhDao = new TrongKinhDaoImpl();
    private final GongKinhDao gongKinhDao = new GongKinhDaoImpl();
    private final HinhDangGongDao hinhDangGongDao = new HinhDangGongDaoImpl();
    private final KieuQuaiKinhDao kieuQuaiKinhDao = new KieuQuaiKinhDaoImpl();
    
    // ===== DANH MUC =====
    
    @Override
    public DanhMuc themDanhMuc(DanhMuc danhMuc) {
        if (danhMuc == null || danhMuc.getTenDanhMuc() == null || 
            danhMuc.getTenDanhMuc().trim().isEmpty()) {
            throw new RuntimeException("Tên danh mục không được để trống");
        }
        
        DanhMuc existing = danhMucDao.findByTen(danhMuc.getTenDanhMuc().trim());
        if (existing != null) {
            throw new RuntimeException("Danh mục với tên này đã tồn tại");
        }
        
        return danhMucDao.save(danhMuc);
    }
    
    @Override
    public DanhMuc capNhatDanhMuc(DanhMuc danhMuc) {
        if (danhMuc == null || danhMuc.getId() == null) {
            throw new RuntimeException("ID danh mục không được để trống");
        }
        if (danhMuc.getTenDanhMuc() == null || danhMuc.getTenDanhMuc().trim().isEmpty()) {
            throw new RuntimeException("Tên danh mục không được để trống");
        }
        
        DanhMuc existing = danhMucDao.findByTen(danhMuc.getTenDanhMuc().trim());
        if (existing != null && !existing.getId().equals(danhMuc.getId())) {
            throw new RuntimeException("Danh mục với tên này đã tồn tại");
        }
        
        return danhMucDao.update(danhMuc);
    }
    
    @Override
    public void xoaDanhMuc(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID danh mục không được để trống");
        }
        danhMucDao.deleteById(id);
    }
    
    @Override
    public DanhMuc layDanhMucTheoId(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID danh mục không được để trống");
        }
        return danhMucDao.findById(id);
    }
    
    @Override
    public List<DanhMuc> layTatCaDanhMuc() {
        return danhMucDao.findAll();
    }
    
    @Override
    public List<DanhMuc> layDanhMucCoPhanTrang(int pageNumber, int pageSize) {
        return danhMucDao.findWithPaging(pageNumber, pageSize);
    }
    
    @Override
    public long demDanhMuc() {
        return danhMucDao.count();
    }
    
    @Override
    public List<DanhMuc> timKiemDanhMuc(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return layTatCaDanhMuc();
        }
        return danhMucDao.searchByKeyword(keyword.trim());
    }
    
    // ===== THUONG HIEU =====
    
    @Override
    public ThuongHieu themThuongHieu(ThuongHieu thuongHieu) {
        if (thuongHieu == null || thuongHieu.getTenThuongHieu() == null || 
            thuongHieu.getTenThuongHieu().trim().isEmpty()) {
            throw new RuntimeException("Tên thương hiệu không được để trống");
        }
        
        ThuongHieu existing = thuongHieuDao.findByTen(thuongHieu.getTenThuongHieu().trim());
        if (existing != null) {
            throw new RuntimeException("Thương hiệu với tên này đã tồn tại");
        }
        
        return thuongHieuDao.save(thuongHieu);
    }
    
    @Override
    public ThuongHieu capNhatThuongHieu(ThuongHieu thuongHieu) {
        if (thuongHieu == null || thuongHieu.getId() == null) {
            throw new RuntimeException("ID thương hiệu không được để trống");
        }
        if (thuongHieu.getTenThuongHieu() == null || thuongHieu.getTenThuongHieu().trim().isEmpty()) {
            throw new RuntimeException("Tên thương hiệu không được để trống");
        }
        
        ThuongHieu existing = thuongHieuDao.findByTen(thuongHieu.getTenThuongHieu().trim());
        if (existing != null && !existing.getId().equals(thuongHieu.getId())) {
            throw new RuntimeException("Thương hiệu với tên này đã tồn tại");
        }
        
        return thuongHieuDao.update(thuongHieu);
    }
    
    @Override
    public void xoaThuongHieu(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID thương hiệu không được để trống");
        }
        thuongHieuDao.deleteById(id);
    }
    
    @Override
    public ThuongHieu layThuongHieuTheoId(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID thương hiệu không được để trống");
        }
        return thuongHieuDao.findById(id);
    }
    
    @Override
    public List<ThuongHieu> layTatCaThuongHieu() {
        return thuongHieuDao.findAll();
    }
    
    @Override
    public List<ThuongHieu> layThuongHieuCoPhanTrang(int pageNumber, int pageSize) {
        return thuongHieuDao.findWithPaging(pageNumber, pageSize);
    }
    
    @Override
    public long demThuongHieu() {
        return thuongHieuDao.count();
    }
    @Override
    public List<ThuongHieu> timKiemThuongHieu(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return layTatCaThuongHieu();
        }
        return thuongHieuDao.searchByKeyword(keyword.trim());
    }
    
    // ===== CHAT LIEU =====
    
    @Override
    public ChatLieu themChatLieu(ChatLieu chatLieu) {
        if (chatLieu == null || chatLieu.getTenChatLieu() == null || 
            chatLieu.getTenChatLieu().trim().isEmpty()) {
            throw new RuntimeException("Tên chất liệu không được để trống");
        }
        
        ChatLieu existing = chatLieuDao.findByTen(chatLieu.getTenChatLieu().trim());
        if (existing != null) {
            throw new RuntimeException("Chất liệu với tên này đã tồn tại");
        }
        
        return chatLieuDao.save(chatLieu);
    }
    
    @Override
    public ChatLieu capNhatChatLieu(ChatLieu chatLieu) {
        if (chatLieu == null || chatLieu.getId() == null) {
            throw new RuntimeException("ID chất liệu không được để trống");
        }
        if (chatLieu.getTenChatLieu() == null || chatLieu.getTenChatLieu().trim().isEmpty()) {
            throw new RuntimeException("Tên chất liệu không được để trống");
        }
        
        ChatLieu existing = chatLieuDao.findByTen(chatLieu.getTenChatLieu().trim());
        if (existing != null && !existing.getId().equals(chatLieu.getId())) {
            throw new RuntimeException("Chất liệu với tên này đã tồn tại");
        }
        
        return chatLieuDao.update(chatLieu);
    }
    
    @Override
    public void xoaChatLieu(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID chất liệu không được để trống");
        }
        chatLieuDao.deleteById(id);
    }
    
    @Override
    public ChatLieu layChatLieuTheoId(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID chất liệu không được để trống");
        }
        return chatLieuDao.findById(id);
    }
    
    @Override
    public List<ChatLieu> layTatCaChatLieu() {
        return chatLieuDao.findAll();
    }
    
    @Override
    public List<ChatLieu> layChatLieuCoPhanTrang(int pageNumber, int pageSize) {
        return chatLieuDao.findWithPaging(pageNumber, pageSize);
    }
    
    @Override
    public long demChatLieu() {
        return chatLieuDao.count();
    }
    @Override
    public List<ChatLieu> timKiemChatLieu(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return layTatCaChatLieu();
        }
        return chatLieuDao.searchByKeyword(keyword.trim());
    }
    // ===== KIEU DANG =====
    
    @Override
    public KieuDang themKieuDang(KieuDang kieuDang) {
        if (kieuDang == null || kieuDang.getTenKieuDang() == null || 
            kieuDang.getTenKieuDang().trim().isEmpty()) {
            throw new RuntimeException("Tên kiểu dáng không được để trống");
        }
        
        KieuDang existing = kieuDangDao.findByTen(kieuDang.getTenKieuDang().trim());
        if (existing != null) {
            throw new RuntimeException("Kiểu dáng với tên này đã tồn tại");
        }
        
        return kieuDangDao.save(kieuDang);
    }
    
    @Override
    public KieuDang capNhatKieuDang(KieuDang kieuDang) {
        if (kieuDang == null || kieuDang.getId() == null) {
            throw new RuntimeException("ID kiểu dáng không được để trống");
        }
        if (kieuDang.getTenKieuDang() == null || kieuDang.getTenKieuDang().trim().isEmpty()) {
            throw new RuntimeException("Tên kiểu dáng không được để trống");
        }
        
        KieuDang existing = kieuDangDao.findByTen(kieuDang.getTenKieuDang().trim());
        if (existing != null && !existing.getId().equals(kieuDang.getId())) {
            throw new RuntimeException("Kiểu dáng với tên này đã tồn tại");
        }
        
        return kieuDangDao.update(kieuDang);
    }
    
    @Override
    public void xoaKieuDang(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID kiểu dáng không được để trống");
        }
        kieuDangDao.deleteById(id);
    }
    
    @Override
    public KieuDang layKieuDangTheoId(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID kiểu dáng không được để trống");
        }
        return kieuDangDao.findById(id);
    }
    
    @Override
    public List<KieuDang> layTatCaKieuDang() {
        return kieuDangDao.findAll();
    }
    
    @Override
    public List<KieuDang> layKieuDangCoPhanTrang(int pageNumber, int pageSize) {
        return kieuDangDao.findWithPaging(pageNumber, pageSize);
    }
    
    @Override
    public long demKieuDang() {
        return kieuDangDao.count();
    }
    @Override
    public List<KieuDang> timKiemKieuDang(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return layTatCaKieuDang();
        }
        return kieuDangDao.searchByKeyword(keyword.trim());
    }
    // ===== MAU SAC =====
    
    @Override
    public MauSac themMauSac(MauSac mauSac) {
        if (mauSac == null || mauSac.getTenMau() == null || 
            mauSac.getTenMau().trim().isEmpty()) {
            throw new RuntimeException("Tên màu sắc không được để trống");
        }
        
        MauSac existing = mauSacDao.findByTen(mauSac.getTenMau().trim());
        if (existing != null) {
            throw new RuntimeException("Màu sắc với tên này đã tồn tại");
        }
        
        return mauSacDao.save(mauSac);
    }
    
    @Override
    public MauSac capNhatMauSac(MauSac mauSac) {
        if (mauSac == null || mauSac.getId() == null) {
            throw new RuntimeException("ID màu sắc không được để trống");
        }
        if (mauSac.getTenMau() == null || mauSac.getTenMau().trim().isEmpty()) {
            throw new RuntimeException("Tên màu sắc không được để trống");
        }
        
        MauSac existing = mauSacDao.findByTen(mauSac.getTenMau().trim());
        if (existing != null && !existing.getId().equals(mauSac.getId())) {
            throw new RuntimeException("Màu sắc với tên này đã tồn tại");
        }
        
        return mauSacDao.update(mauSac);
    }
    
    @Override
    public void xoaMauSac(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID màu sắc không được để trống");
        }
        mauSacDao.deleteById(id);
    }
    
    @Override
    public MauSac layMauSacTheoId(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID màu sắc không được để trống");
        }
        return mauSacDao.findById(id);
    }
    
    @Override
    public List<MauSac> layTatCaMauSac() {
        return mauSacDao.findAll();
    }
    
    @Override
    public List<MauSac> layMauSacCoPhanTrang(int pageNumber, int pageSize) {
        return mauSacDao.findWithPaging(pageNumber, pageSize);
    }
    
    @Override
    public long demMauSac() {
        return mauSacDao.count();
    }
    
    @Override
    public List<MauSac> timKiemMauSac(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return layTatCaMauSac();
        }
        return mauSacDao.searchByKeyword(keyword.trim());
    }

    // ===== KICH CO =====
    
    @Override
    public KichCo themKichCo(KichCo kichCo) {
        if (kichCo == null || kichCo.getTenKichCo() == null || 
            kichCo.getTenKichCo().trim().isEmpty()) {
            throw new RuntimeException("Tên kích cỡ không được để trống");
        }
        
        KichCo existing = kichCoDao.findByTen(kichCo.getTenKichCo().trim());
        if (existing != null) {
            throw new RuntimeException("Kích cỡ với tên này đã tồn tại");
        }
        
        return kichCoDao.save(kichCo);
    }
    
    @Override
    public KichCo capNhatKichCo(KichCo kichCo) {
        if (kichCo == null || kichCo.getId() == null) {
            throw new RuntimeException("ID kích cỡ không được để trống");
        }
        if (kichCo.getTenKichCo() == null || kichCo.getTenKichCo().trim().isEmpty()) {
            throw new RuntimeException("Tên kích cỡ không được để trống");
        }
        
        KichCo existing = kichCoDao.findByTen(kichCo.getTenKichCo().trim());
        if (existing != null && !existing.getId().equals(kichCo.getId())) {
            throw new RuntimeException("Kích cỡ với tên này đã tồn tại");
        }
        
        return kichCoDao.update(kichCo);
    }
    
    @Override
    public void xoaKichCo(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID kích cỡ không được để trống");
        }
        kichCoDao.deleteById(id);
    }
    
    @Override
    public KichCo layKichCoTheoId(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID kích cỡ không được để trống");
        }
        return kichCoDao.findById(id);
    }
    
    @Override
    public List<KichCo> layTatCaKichCo() {
        return kichCoDao.findAll();
    }
    
    @Override
    public List<KichCo> layKichCoCoPhanTrang(int pageNumber, int pageSize) {
        return kichCoDao.findWithPaging(pageNumber, pageSize);
    }
    
    @Override
    public long demKichCo() {
        return kichCoDao.count();
    }
    @Override
    public List<KichCo> timKiemKichCo(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return layTatCaKichCo();
        }
        return kichCoDao.searchByKeyword(keyword.trim());
    }
    
    // ===== TRONG KINH =====
    
    @Override
    public TrongKinh themTrongKinh(TrongKinh trongKinh) {
        if (trongKinh == null || trongKinh.getLoaiTrong() == null || 
            trongKinh.getLoaiTrong().trim().isEmpty()) {
            throw new RuntimeException("Loại trong không được để trống");
        }
        
        TrongKinh existing = trongKinhDao.findByTen(trongKinh.getLoaiTrong().trim());
        if (existing != null) {
            throw new RuntimeException("Loại trong này đã tồn tại");
        }
        
        return trongKinhDao.save(trongKinh);
    }
    
    @Override
    public TrongKinh capNhatTrongKinh(TrongKinh trongKinh) {
        if (trongKinh == null || trongKinh.getId() == null) {
            throw new RuntimeException("ID trong kính không được để trống");
        }
        if (trongKinh.getLoaiTrong() == null || trongKinh.getLoaiTrong().trim().isEmpty()) {
            throw new RuntimeException("Loại trong không được để trống");
        }
        
        TrongKinh existing = trongKinhDao.findByTen(trongKinh.getLoaiTrong().trim());
        if (existing != null && !existing.getId().equals(trongKinh.getId())) {
            throw new RuntimeException("Loại trong này đã tồn tại");
        }
        
        return trongKinhDao.update(trongKinh);
    }
    
    @Override
    public void xoaTrongKinh(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID trong kính không được để trống");
        }
        trongKinhDao.deleteById(id);
    }
    
    @Override
    public TrongKinh layTrongKinhTheoId(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID trong kính không được để trống");
        }
        return trongKinhDao.findById(id);
    }
    
    @Override
    public List<TrongKinh> layTatCaTrongKinh() {
        return trongKinhDao.findAll();
    }
    
    @Override
    public List<TrongKinh> layTrongKinhCoPhanTrang(int pageNumber, int pageSize) {
        return trongKinhDao.findWithPaging(pageNumber, pageSize);
    }
    
    @Override
    public long demTrongKinh() {
        return trongKinhDao.count();
    }

    @Override
    public List<TrongKinh> timKiemTrongKinh(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return layTatCaTrongKinh();
        }
        return trongKinhDao.searchByKeyword(keyword.trim());
    }
    // ===== GONG KINH =====
    
    @Override
    public GongKinh themGongKinh(GongKinh gongKinh) {
        if (gongKinh == null) {
            throw new RuntimeException("Gọng kính không được để trống");
        }
        
        return gongKinhDao.save(gongKinh);
    }
    
    @Override
    public GongKinh capNhatGongKinh(GongKinh gongKinh) {
        if (gongKinh == null || gongKinh.getId() == null) {
            throw new RuntimeException("ID gọng kính không được để trống");
        }
        
        return gongKinhDao.update(gongKinh);
    }
    
    @Override
    public void xoaGongKinh(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID gọng kính không được để trống");
        }
        gongKinhDao.deleteById(id);
    }
    
    @Override
    public GongKinh layGongKinhTheoId(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID gọng kính không được để trống");
        }
        return gongKinhDao.findById(id);
    }
    
    @Override
    public List<GongKinh> layTatCaGongKinh() {
        return gongKinhDao.findAll();
    }
    
    @Override
    public List<GongKinh> layGongKinhCoPhanTrang(int pageNumber, int pageSize) {
        return gongKinhDao.findWithPaging(pageNumber, pageSize);
    }
    
    @Override
    public long demGongKinh() {
        return gongKinhDao.count();
    }

    @Override
    public List<GongKinh> timKiemGongKinh(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return layTatCaGongKinh();
        }
        return gongKinhDao.searchByKeyword(keyword.trim());
    }

    // ===== HINH DANG GONG =====
    
    @Override
    public HinhDangGong themHinhDangGong(HinhDangGong hinhDangGong) {
        if (hinhDangGong == null || hinhDangGong.getHinhDang() == null || 
            hinhDangGong.getHinhDang().trim().isEmpty()) {
            throw new RuntimeException("Hình dáng không được để trống");
        }
        
        HinhDangGong existing = hinhDangGongDao.findByTen(hinhDangGong.getHinhDang().trim());
        if (existing != null) {
            throw new RuntimeException("Hình dáng này đã tồn tại");
        }
        
        return hinhDangGongDao.save(hinhDangGong);
    }
    
    @Override
    public HinhDangGong capNhatHinhDangGong(HinhDangGong hinhDangGong) {
        if (hinhDangGong == null || hinhDangGong.getId() == null) {
            throw new RuntimeException("ID hình dáng gọng không được để trống");
        }
        if (hinhDangGong.getHinhDang() == null || hinhDangGong.getHinhDang().trim().isEmpty()) {
            throw new RuntimeException("Hình dáng không được để trống");
        }
        
        HinhDangGong existing = hinhDangGongDao.findByTen(hinhDangGong.getHinhDang().trim());
        if (existing != null && !existing.getId().equals(hinhDangGong.getId())) {
            throw new RuntimeException("Hình dáng này đã tồn tại");
        }
        
        return hinhDangGongDao.update(hinhDangGong);
    }
    
    @Override
    public void xoaHinhDangGong(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID hình dáng gọng không được để trống");
        }
        hinhDangGongDao.deleteById(id);
    }
    
    @Override
    public HinhDangGong layHinhDangGongTheoId(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID hình dáng gọng không được để trống");
        }
        return hinhDangGongDao.findById(id);
    }
    
    @Override
    public List<HinhDangGong> layTatCaHinhDangGong() {
        return hinhDangGongDao.findAll();
    }
    
    @Override
    public List<HinhDangGong> layHinhDangGongCoPhanTrang(int pageNumber, int pageSize) {
        return hinhDangGongDao.findWithPaging(pageNumber, pageSize);
    }
    
    @Override
    public long demHinhDangGong() {
        return hinhDangGongDao.count();
    }

    
    // ===== KIEU QUAI KINH =====
    
    @Override
    public KieuQuaiKinh themKieuQuaiKinh(KieuQuaiKinh kieuQuaiKinh) {
        if (kieuQuaiKinh == null) {
            throw new RuntimeException("Kiểu quai kính không được để trống");
        }
        
        return kieuQuaiKinhDao.save(kieuQuaiKinh);
    }
    
    @Override
    public KieuQuaiKinh capNhatKieuQuaiKinh(KieuQuaiKinh kieuQuaiKinh) {
        if (kieuQuaiKinh == null || kieuQuaiKinh.getId() == null) {
            throw new RuntimeException("ID kiểu quai kính không được để trống");
        }
        
        return kieuQuaiKinhDao.update(kieuQuaiKinh);
    }
    
    @Override
    public void xoaKieuQuaiKinh(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID kiểu quai kính không được để trống");
        }
        kieuQuaiKinhDao.deleteById(id);
    }
    
    @Override
    public KieuQuaiKinh layKieuQuaiKinhTheoId(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID kiểu quai kính không được để trống");
        }
        return kieuQuaiKinhDao.findById(id);
    }
    
    @Override
    public List<KieuQuaiKinh> layTatCaKieuQuaiKinh() {
        return kieuQuaiKinhDao.findAll();
    }
    
    @Override
    public List<KieuQuaiKinh> layKieuQuaiKinhCoPhanTrang(int pageNumber, int pageSize) {
        return kieuQuaiKinhDao.findWithPaging(pageNumber, pageSize);
    }
    
    @Override
    public long demKieuQuaiKinh() {
        return kieuQuaiKinhDao.count();
    }
}
