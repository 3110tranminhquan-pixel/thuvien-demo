package vn.nhom.thuvien.service.phat;

import org.springframework.stereotype.Component;
import vn.nhom.thuvien.entity.ChiTietMuon;
import vn.nhom.thuvien.entity.enums.TinhTrangSach;
import vn.nhom.thuvien.service.ThamSoService;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class PhatHuHong implements CachTinhPhat {

    private final ThamSoService thamSo;

    public PhatHuHong(ThamSoService thamSo) {
        this.thamSo = thamSo;
    }

    // BR-08: hỏng nhẹ 30%, hỏng nặng 100% giá bìa, dựa trên tình trạng thủ thư chọn khi nhận trả
    @Override
    public BigDecimal tinh(ChiTietMuon ct, LocalDate ngayXet) {
        return tinhTheoTinhTrang(ct, ct.getTinhTrangKhiTra());
    }

    // Dùng khi cần tính trước lúc lưu, ví dụ để hiện tiền tạm tính trên màn hình trả sách
    public BigDecimal tinhTheoTinhTrang(ChiTietMuon ct, TinhTrangSach tinhTrang) {
        String ma;
        if (tinhTrang == TinhTrangSach.HONG_NHE) {
            ma = "TY_LE_HONG_NHE";
        } else if (tinhTrang == TinhTrangSach.HONG_NANG) {
            ma = "TY_LE_HONG_NANG";
        } else {
            return BigDecimal.ZERO;
        }
        return ct.getBanSach().getDauSach().getGiaBia().multiply(thamSo.laySo(ma));
    }
}
