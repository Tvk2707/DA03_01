package BanHangTaiQuay.Service;

import BanHangTaiQuay.Dao.BanHangDAO;
import BanHangTaiQuay.Dao.BanHangDAOImpl;

public class CaLamViecServiceImpl implements CaLamViecService {

    private final BanHangDAO banHangDAO = new BanHangDAOImpl();

    @Override
    public Integer layCaDangMo(int idNhanVien) {
        return banHangDAO.timCaDangMo(idNhanVien);
    }
}
