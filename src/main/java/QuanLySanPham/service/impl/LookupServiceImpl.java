package QuanLySanPham.service.impl;

import QuanLySanPham.Entity.*;
import QuanLySanPham.Utils.ValidationException;
import QuanLySanPham.Utils.ValidationUtils;
import QuanLySanPham.dao.*;
import QuanLySanPham.dao.impl.*;
import QuanLySanPham.service.LookupService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private void validateAndThrow(Map<String, String> errors) {
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed", errors);
        }
    }

    // ===== DANH MUC =====

    private void validateDanhMuc(DanhMuc danhMuc, Map<String, String> errors) {
        ValidationUtils.validateTen(errors, "tenDanhMuc", danhMuc.getTenDanhMuc());
        if (!errors.containsKey("tenDanhMuc")) {
            String normalizedTen = ValidationUtils.normalizeString(danhMuc.getTenDanhMuc());
            DanhMuc existing = danhMucDao.findByTen(normalizedTen);
            if (existing != null && (danhMuc.getId() == null || !existing.getId().equals(danhMuc.getId()))) {
                errors.put("tenDanhMuc", "Tên danh mục đã tồn tại.");
            }
            danhMuc.setTenDanhMuc(normalizedTen);
        }
    }

    @Override
    public DanhMuc themDanhMuc(DanhMuc danhMuc) {
        Map<String, String> errors = new HashMap<>();
        validateDanhMuc(danhMuc, errors);
        validateAndThrow(errors);
        return danhMucDao.save(danhMuc);
    }

    @Override
    public DanhMuc capNhatDanhMuc(DanhMuc danhMuc) {
        Map<String, String> errors = new HashMap<>();
        if (danhMuc == null || danhMuc.getId() == null) {
            errors.put("id", "ID danh mục không hợp lệ.");
            throw new ValidationException("Validation failed", errors);
        }
        validateDanhMuc(danhMuc, errors);
        validateAndThrow(errors);
        return danhMucDao.update(danhMuc);
    }

    // ===== THUONG HIEU =====

    private void validateThuongHieu(ThuongHieu thuongHieu, Map<String, String> errors) {
        ValidationUtils.validateTen(errors, "tenThuongHieu", thuongHieu.getTenThuongHieu());
        if (!errors.containsKey("tenThuongHieu")) {
            String normalizedTen = ValidationUtils.normalizeString(thuongHieu.getTenThuongHieu());
            ThuongHieu existing = thuongHieuDao.findByTen(normalizedTen);
            if (existing != null && (thuongHieu.getId() == null || !existing.getId().equals(thuongHieu.getId()))) {
                errors.put("tenThuongHieu", "Tên thương hiệu đã tồn tại.");
            }
            thuongHieu.setTenThuongHieu(normalizedTen);
        }
    }

    @Override
    public ThuongHieu themThuongHieu(ThuongHieu thuongHieu) {
        Map<String, String> errors = new HashMap<>();
        validateThuongHieu(thuongHieu, errors);
        validateAndThrow(errors);
        return thuongHieuDao.save(thuongHieu);
    }

    @Override
    public ThuongHieu capNhatThuongHieu(ThuongHieu thuongHieu) {
        Map<String, String> errors = new HashMap<>();
        if (thuongHieu == null || thuongHieu.getId() == null) {
            errors.put("id", "ID thương hiệu không hợp lệ.");
            throw new ValidationException("Validation failed", errors);
        }
        validateThuongHieu(thuongHieu, errors);
        validateAndThrow(errors);
        return thuongHieuDao.update(thuongHieu);
    }

    // ===== Các phương thức khác giữ nguyên...
    @Override
    public void xoaDanhMuc(Integer id) {
        danhMucDao.deleteById(id);
    }

    @Override
    public DanhMuc layDanhMucTheoId(Integer id) {
        return danhMucDao.findById(id);
    }

    @Override
    public List<DanhMuc> layTatCaDanhMuc() {
        return danhMucDao.findAll();
    }

    @Override
    public List<DanhMuc> timKiemDanhMuc(String keyword) {
        return danhMucDao.searchByKeyword(keyword);
    }
    @Override
    public void xoaThuongHieu(Integer id) {
        thuongHieuDao.deleteById(id);
    }

    @Override
    public ThuongHieu layThuongHieuTheoId(Integer id) {
        return thuongHieuDao.findById(id);
    }

    @Override
    public List<ThuongHieu> layTatCaThuongHieu() {
        return thuongHieuDao.findAll();
    }

    @Override
    public List<ThuongHieu> timKiemThuongHieu(String keyword) {
        return thuongHieuDao.searchByKeyword(keyword);
    }

    @Override
    public ChatLieu themChatLieu(ChatLieu chatLieu) {
        Map<String, String> errors = new HashMap<>();
        ValidationUtils.validateTen(errors, "tenChatLieu", chatLieu.getTenChatLieu());
        if (!errors.containsKey("tenChatLieu")) {
            String normalizedTen = ValidationUtils.normalizeString(chatLieu.getTenChatLieu());
            ChatLieu existing = chatLieuDao.findByTen(normalizedTen);
            if (existing != null && (chatLieu.getId() == null || !existing.getId().equals(chatLieu.getId()))) {
                errors.put("tenChatLieu", "Tên chất liệu đã tồn tại.");
            }
            chatLieu.setTenChatLieu(normalizedTen);
        }
        validateAndThrow(errors);
        return chatLieuDao.save(chatLieu);
    }

    @Override
    public ChatLieu capNhatChatLieu(ChatLieu chatLieu) {
        Map<String, String> errors = new HashMap<>();
        if (chatLieu == null || chatLieu.getId() == null) {
            errors.put("id", "ID chất liệu không hợp lệ.");
            throw new ValidationException("Validation failed", errors);
        }
        ValidationUtils.validateTen(errors, "tenChatLieu", chatLieu.getTenChatLieu());
        if (!errors.containsKey("tenChatLieu")) {
            String normalizedTen = ValidationUtils.normalizeString(chatLieu.getTenChatLieu());
            ChatLieu existing = chatLieuDao.findByTen(normalizedTen);
            if (existing != null && !existing.getId().equals(chatLieu.getId())) {
                errors.put("tenChatLieu", "Tên chất liệu đã tồn tại.");
            }
            chatLieu.setTenChatLieu(normalizedTen);
        }
        validateAndThrow(errors);
        return chatLieuDao.update(chatLieu);
    }
    @Override
    public void xoaChatLieu(Integer id) {
        chatLieuDao.deleteById(id);
    }

    @Override
    public ChatLieu layChatLieuTheoId(Integer id) {
        return chatLieuDao.findById(id);
    }

    @Override
    public List<ChatLieu> layTatCaChatLieu() {
        return chatLieuDao.findAll();
    }

    @Override
    public List<ChatLieu> timKiemChatLieu(String keyword) {
        return chatLieuDao.searchByKeyword(keyword);
    }

    @Override
    public KieuDang themKieuDang(KieuDang kieuDang) {
        Map<String, String> errors = new HashMap<>();
        ValidationUtils.validateTen(errors, "tenKieuDang", kieuDang.getTenKieuDang());
        if (!errors.containsKey("tenKieuDang")) {
            String normalizedTen = ValidationUtils.normalizeString(kieuDang.getTenKieuDang());
            KieuDang existing = kieuDangDao.findByTen(normalizedTen);
            if (existing != null && (kieuDang.getId() == null || !existing.getId().equals(kieuDang.getId()))) {
                errors.put("tenKieuDang", "Tên kiểu dáng đã tồn tại.");
            }
            kieuDang.setTenKieuDang(normalizedTen);
        }
        validateAndThrow(errors);
        return kieuDangDao.save(kieuDang);
    }

    @Override
    public KieuDang capNhatKieuDang(KieuDang kieuDang) {
        Map<String, String> errors = new HashMap<>();
        if (kieuDang == null || kieuDang.getId() == null) {
            errors.put("id", "ID kiểu dáng không hợp lệ.");
            throw new ValidationException("Validation failed", errors);
        }
        ValidationUtils.validateTen(errors, "tenKieuDang", kieuDang.getTenKieuDang());
        if (!errors.containsKey("tenKieuDang")) {
            String normalizedTen = ValidationUtils.normalizeString(kieuDang.getTenKieuDang());
            KieuDang existing = kieuDangDao.findByTen(normalizedTen);
            if (existing != null && !existing.getId().equals(kieuDang.getId())) {
                errors.put("tenKieuDang", "Tên kiểu dáng đã tồn tại.");
            }
            kieuDang.setTenKieuDang(normalizedTen);
        }
        validateAndThrow(errors);
        return kieuDangDao.update(kieuDang);
    }
    @Override
    public void xoaKieuDang(Integer id) {
        kieuDangDao.deleteById(id);
    }

    @Override
    public KieuDang layKieuDangTheoId(Integer id) {
        return kieuDangDao.findById(id);
    }

    @Override
    public List<KieuDang> layTatCaKieuDang() {
        return kieuDangDao.findAll();
    }

    @Override
    public List<KieuDang> timKiemKieuDang(String keyword) {
        return kieuDangDao.searchByKeyword(keyword);
    }

    @Override
    public MauSac themMauSac(MauSac mauSac) {
        Map<String, String> errors = new HashMap<>();
        ValidationUtils.validateTen(errors, "tenMau", mauSac.getTenMau());
        if (!errors.containsKey("tenMau")) {
            String normalizedTen = ValidationUtils.normalizeString(mauSac.getTenMau());
            MauSac existing = mauSacDao.findByTen(normalizedTen);
            if (existing != null && (mauSac.getId() == null || !existing.getId().equals(mauSac.getId()))) {
                errors.put("tenMau", "Tên màu sắc đã tồn tại.");
            }
            mauSac.setTenMau(normalizedTen);
        }
        validateAndThrow(errors);
        return mauSacDao.save(mauSac);
    }

    @Override
    public MauSac capNhatMauSac(MauSac mauSac) {
        Map<String, String> errors = new HashMap<>();
        if (mauSac == null || mauSac.getId() == null) {
            errors.put("id", "ID màu sắc không hợp lệ.");
            throw new ValidationException("Validation failed", errors);
        }
        ValidationUtils.validateTen(errors, "tenMau", mauSac.getTenMau());
        if (!errors.containsKey("tenMau")) {
            String normalizedTen = ValidationUtils.normalizeString(mauSac.getTenMau());
            MauSac existing = mauSacDao.findByTen(normalizedTen);
            if (existing != null && !existing.getId().equals(mauSac.getId())) {
                errors.put("tenMau", "Tên màu sắc đã tồn tại.");
            }
            mauSac.setTenMau(normalizedTen);
        }
        validateAndThrow(errors);
        return mauSacDao.update(mauSac);
    }
    @Override
    public void xoaMauSac(Integer id) {
        mauSacDao.deleteById(id);
    }

    @Override
    public MauSac layMauSacTheoId(Integer id) {
        return mauSacDao.findById(id);
    }

    @Override
    public List<MauSac> layTatCaMauSac() {
        return mauSacDao.findAll();
    }

    @Override
    public List<MauSac> timKiemMauSac(String keyword) {
        return mauSacDao.searchByKeyword(keyword);
    }

    @Override
    public KichCo themKichCo(KichCo kichCo) {
        Map<String, String> errors = new HashMap<>();
        ValidationUtils.validateTen(errors, "tenKichCo", kichCo.getTenKichCo());
        if (!errors.containsKey("tenKichCo")) {
            String normalizedTen = ValidationUtils.normalizeString(kichCo.getTenKichCo());
            KichCo existing = kichCoDao.findByTen(normalizedTen);
            if (existing != null && (kichCo.getId() == null || !existing.getId().equals(kichCo.getId()))) {
                errors.put("tenKichCo", "Tên kích cỡ đã tồn tại.");
            }
            kichCo.setTenKichCo(normalizedTen);
        }
        validateAndThrow(errors);
        return kichCoDao.save(kichCo);
    }

    @Override
    public KichCo capNhatKichCo(KichCo kichCo) {
        Map<String, String> errors = new HashMap<>();
        if (kichCo == null || kichCo.getId() == null) {
            errors.put("id", "ID kích cỡ không hợp lệ.");
            throw new ValidationException("Validation failed", errors);
        }
        ValidationUtils.validateTen(errors, "tenKichCo", kichCo.getTenKichCo());
        if (!errors.containsKey("tenKichCo")) {
            String normalizedTen = ValidationUtils.normalizeString(kichCo.getTenKichCo());
            KichCo existing = kichCoDao.findByTen(normalizedTen);
            if (existing != null && !existing.getId().equals(kichCo.getId())) {
                errors.put("tenKichCo", "Tên kích cỡ đã tồn tại.");
            }
            kichCo.setTenKichCo(normalizedTen);
        }
        validateAndThrow(errors);
        return kichCoDao.update(kichCo);
    }
    @Override
    public void xoaKichCo(Integer id) {
        kichCoDao.deleteById(id);
    }

    @Override
    public KichCo layKichCoTheoId(Integer id) {
        return kichCoDao.findById(id);
    }

    @Override
    public List<KichCo> layTatCaKichCo() {
        return kichCoDao.findAll();
    }

    @Override
    public List<KichCo> timKiemKichCo(String keyword) {
        return kichCoDao.searchByKeyword(keyword);
    }

    @Override
    public TrongKinh themTrongKinh(TrongKinh trongKinh) {
        Map<String, String> errors = new HashMap<>();
        ValidationUtils.validateTen(errors, "loaiTrong", trongKinh.getLoaiTrong());
        if (!errors.containsKey("loaiTrong")) {
            String normalizedTen = ValidationUtils.normalizeString(trongKinh.getLoaiTrong());
            TrongKinh existing = trongKinhDao.findByTen(normalizedTen);
            if (existing != null && (trongKinh.getId() == null || !existing.getId().equals(trongKinh.getId()))) {
                errors.put("loaiTrong", "Loại tròng kính đã tồn tại.");
            }
            trongKinh.setLoaiTrong(normalizedTen);
        }
        validateAndThrow(errors);
        return trongKinhDao.save(trongKinh);
    }

    @Override
    public TrongKinh capNhatTrongKinh(TrongKinh trongKinh) {
        Map<String, String> errors = new HashMap<>();
        if (trongKinh == null || trongKinh.getId() == null) {
            errors.put("id", "ID tròng kính không hợp lệ.");
            throw new ValidationException("Validation failed", errors);
        }
        ValidationUtils.validateTen(errors, "loaiTrong", trongKinh.getLoaiTrong());
        if (!errors.containsKey("loaiTrong")) {
            String normalizedTen = ValidationUtils.normalizeString(trongKinh.getLoaiTrong());
            TrongKinh existing = trongKinhDao.findByTen(normalizedTen);
            if (existing != null && !existing.getId().equals(trongKinh.getId())) {
                errors.put("loaiTrong", "Loại tròng kính đã tồn tại.");
            }
            trongKinh.setLoaiTrong(normalizedTen);
        }
        validateAndThrow(errors);
        return trongKinhDao.update(trongKinh);
    }
    @Override
    public void xoaTrongKinh(Integer id) {
        trongKinhDao.deleteById(id);
    }

    @Override
    public TrongKinh layTrongKinhTheoId(Integer id) {
        return trongKinhDao.findById(id);
    }

    @Override
    public List<TrongKinh> layTatCaTrongKinh() {
        return trongKinhDao.findAll();
    }

    @Override
    public List<TrongKinh> timKiemTrongKinh(String keyword) {
        return trongKinhDao.searchByKeyword(keyword);
    }
    @Override
    public GongKinh themGongKinh(GongKinh gongKinh) {
        Map<String, String> errors = new HashMap<>();
        ValidationUtils.checkNull(errors, "hinhDangGong", gongKinh.getHinhDangGong(), "Vui lòng chọn hình dáng gọng.");
        ValidationUtils.checkNull(errors, "kieuQuaiKinh", gongKinh.getKieuQuaiKinh(), "Vui lòng chọn kiểu quai kính.");
        validateAndThrow(errors);
        return gongKinhDao.save(gongKinh);
    }

    @Override
    public GongKinh capNhatGongKinh(GongKinh gongKinh) {
        Map<String, String> errors = new HashMap<>();
        if (gongKinh == null || gongKinh.getId() == null) {
            errors.put("id", "ID gọng kính không hợp lệ.");
            throw new ValidationException("Validation failed", errors);
        }
        ValidationUtils.checkNull(errors, "hinhDangGong", gongKinh.getHinhDangGong(), "Vui lòng chọn hình dáng gọng.");
        ValidationUtils.checkNull(errors, "kieuQuaiKinh", gongKinh.getKieuQuaiKinh(), "Vui lòng chọn kiểu quai kính.");
        validateAndThrow(errors);
        return gongKinhDao.update(gongKinh);
    }
    @Override
    public void xoaGongKinh(Integer id) {
        gongKinhDao.deleteById(id);
    }

    @Override
    public GongKinh layGongKinhTheoId(Integer id) {
        return gongKinhDao.findById(id);
    }

    @Override
    public List<GongKinh> layTatCaGongKinh() {
        return gongKinhDao.findAll();
    }

    @Override
    public List<GongKinh> timKiemGongKinh(String keyword) {
        return gongKinhDao.searchByKeyword(keyword);
    }

    @Override
    public HinhDangGong themHinhDangGong(HinhDangGong hinhDangGong) {
        Map<String, String> errors = new HashMap<>();
        ValidationUtils.validateTen(errors, "hinhDang", hinhDangGong.getHinhDang());
        if (!errors.containsKey("hinhDang")) {
            String normalizedTen = ValidationUtils.normalizeString(hinhDangGong.getHinhDang());
            HinhDangGong existing = hinhDangGongDao.findByTen(normalizedTen);
            if (existing != null && (hinhDangGong.getId() == null || !existing.getId().equals(hinhDangGong.getId()))) {
                errors.put("hinhDang", "Hình dáng gọng đã tồn tại.");
            }
            hinhDangGong.setHinhDang(normalizedTen);
        }
        validateAndThrow(errors);
        return hinhDangGongDao.save(hinhDangGong);
    }

    @Override
    public HinhDangGong capNhatHinhDangGong(HinhDangGong hinhDangGong) {
        Map<String, String> errors = new HashMap<>();
        if (hinhDangGong == null || hinhDangGong.getId() == null) {
            errors.put("id", "ID hình dáng gọng không hợp lệ.");
            throw new ValidationException("Validation failed", errors);
        }
        ValidationUtils.validateTen(errors, "hinhDang", hinhDangGong.getHinhDang());
        if (!errors.containsKey("hinhDang")) {
            String normalizedTen = ValidationUtils.normalizeString(hinhDangGong.getHinhDang());
            HinhDangGong existing = hinhDangGongDao.findByTen(normalizedTen);
            if (existing != null && !existing.getId().equals(hinhDangGong.getId())) {
                errors.put("hinhDang", "Hình dáng gọng đã tồn tại.");
            }
            hinhDangGong.setHinhDang(normalizedTen);
        }
        validateAndThrow(errors);
        return hinhDangGongDao.update(hinhDangGong);
    }
    @Override
    public void xoaHinhDangGong(Integer id) {
        hinhDangGongDao.deleteById(id);
    }

    @Override
    public HinhDangGong layHinhDangGongTheoId(Integer id) {
        return hinhDangGongDao.findById(id);
    }

    @Override
    public List<HinhDangGong> layTatCaHinhDangGong() {
        return hinhDangGongDao.findAll();
    }

    @Override
    public KieuQuaiKinh themKieuQuaiKinh(KieuQuaiKinh kieuQuaiKinh) {
        Map<String, String> errors = new HashMap<>();
        ValidationUtils.validateTen(errors, "kieuQuai", kieuQuaiKinh.getKieuQuai());
        validateAndThrow(errors);
        return kieuQuaiKinhDao.save(kieuQuaiKinh);
    }

    @Override
    public KieuQuaiKinh capNhatKieuQuaiKinh(KieuQuaiKinh kieuQuaiKinh) {
        Map<String, String> errors = new HashMap<>();
        if (kieuQuaiKinh == null || kieuQuaiKinh.getId() == null) {
            errors.put("id", "ID kiểu quai kính không hợp lệ.");
            throw new ValidationException("Validation failed", errors);
        }
        ValidationUtils.validateTen(errors, "kieuQuai", kieuQuaiKinh.getKieuQuai());
        validateAndThrow(errors);
        return kieuQuaiKinhDao.update(kieuQuaiKinh);
    }
    @Override
    public void xoaKieuQuaiKinh(Integer id) {
        kieuQuaiKinhDao.deleteById(id);
    }

    @Override
    public KieuQuaiKinh layKieuQuaiKinhTheoId(Integer id) {
        return kieuQuaiKinhDao.findById(id);
    }

    @Override
    public List<KieuQuaiKinh> layTatCaKieuQuaiKinh() {
        return kieuQuaiKinhDao.findAll();
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
    public List<ThuongHieu> layThuongHieuCoPhanTrang(int pageNumber, int pageSize) {
        return thuongHieuDao.findWithPaging(pageNumber, pageSize);
    }

    @Override
    public long demThuongHieu() {
        return thuongHieuDao.count();
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
    public List<KieuDang> layKieuDangCoPhanTrang(int pageNumber, int pageSize) {
        return kieuDangDao.findWithPaging(pageNumber, pageSize);
    }

    @Override
    public long demKieuDang() {
        return kieuDangDao.count();
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
    public List<KichCo> layKichCoCoPhanTrang(int pageNumber, int pageSize) {
        return kichCoDao.findWithPaging(pageNumber, pageSize);
    }

    @Override
    public long demKichCo() {
        return kichCoDao.count();
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
    public List<GongKinh> layGongKinhCoPhanTrang(int pageNumber, int pageSize) {
        return gongKinhDao.findWithPaging(pageNumber, pageSize);
    }

    @Override
    public long demGongKinh() {
        return gongKinhDao.count();
    }

    @Override
    public List<HinhDangGong> layHinhDangGongCoPhanTrang(int pageNumber, int pageSize) {
        return hinhDangGongDao.findWithPaging(pageNumber, pageSize);
    }

    @Override
    public long demHinhDangGong() {
        return hinhDangGongDao.count();
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
