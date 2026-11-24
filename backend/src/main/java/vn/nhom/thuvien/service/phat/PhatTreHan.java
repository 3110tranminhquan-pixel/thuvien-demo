package vn.nhom.thuvien.service.phat;

import org.springframework.stereotype.Component;
import vn.nhom.thuvien.entity.ChiTietMuon;
import vn.nhom.thuvien.service.ThamSoService;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class PhatTreHan implements CachTinhPhat {

    private final ThamSoService thamSo;

    public PhatTreHan(ThamSoService thamSo) {
        this.thamSo = thamSo;
    }

    @Override
    public BigDecimal tinh(ChiTietMuon ct, LocalDate ngayXet) {
        BigDecimal mucNgay = thamSo.laySo("PHAT_TRE_MOI_NGAY");                  // BR-07
        BigDecimal tien = mucNgay.multiply(BigDecimal.valueOf(ct.soNgayTre(ngayXet)));
        BigDecimal tran = ct.getBanSach().getDauSach().getGiaBia()
                            .multiply(thamSo.laySo("TY_LE_DEN_MAT"));            // trần = mức đền mất
        return tien.min(tran);
    }
}
