package vn.nhom.thuvien.service.phat;

import org.springframework.stereotype.Component;
import vn.nhom.thuvien.entity.ChiTietMuon;
import vn.nhom.thuvien.service.ThamSoService;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class PhatMatSach implements CachTinhPhat {

    private final ThamSoService thamSo;

    public PhatMatSach(ThamSoService thamSo) {
        this.thamSo = thamSo;
    }

    // BR-13: mất sách đền 200% giá bìa (phần phạt trễ nếu có được lập phiếu riêng)
    @Override
    public BigDecimal tinh(ChiTietMuon ct, LocalDate ngayXet) {
        return ct.getBanSach().getDauSach().getGiaBia().multiply(thamSo.laySo("TY_LE_DEN_MAT"));
    }
}
