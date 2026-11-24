package vn.nhom.thuvien.service.phat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.nhom.thuvien.entity.BanSach;
import vn.nhom.thuvien.entity.ChiTietMuon;
import vn.nhom.thuvien.entity.DauSach;
import vn.nhom.thuvien.service.ThamSoService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

// BR-13: mất sách đền 200% giá bìa
@ExtendWith(MockitoExtension.class)
class PhatMatSachTest {

    @Mock ThamSoService thamSo;
    @InjectMocks PhatMatSach phatMatSach;

    @Test
    void matSachDen200PhanTramGiaBia() {
        when(thamSo.laySo("TY_LE_DEN_MAT")).thenReturn(new BigDecimal("2.0"));
        DauSach ds = new DauSach();
        ds.setGiaBia(new BigDecimal("80000"));
        BanSach bs = new BanSach();
        bs.setDauSach(ds);
        ChiTietMuon ct = new ChiTietMuon();
        ct.setBanSach(bs);
        assertEquals(0, new BigDecimal("160000").compareTo(phatMatSach.tinh(ct, LocalDate.now())));
    }
}
