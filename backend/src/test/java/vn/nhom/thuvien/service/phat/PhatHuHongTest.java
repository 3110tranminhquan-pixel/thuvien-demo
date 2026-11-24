package vn.nhom.thuvien.service.phat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.nhom.thuvien.entity.BanSach;
import vn.nhom.thuvien.entity.ChiTietMuon;
import vn.nhom.thuvien.entity.DauSach;
import vn.nhom.thuvien.entity.enums.TinhTrangSach;
import vn.nhom.thuvien.service.ThamSoService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PhatHuHongTest {

    @Mock ThamSoService thamSo;
    @InjectMocks PhatHuHong phatHuHong;

    private ChiTietMuon traVoiTinhTrang(TinhTrangSach tt) {
        DauSach ds = new DauSach();
        ds.setGiaBia(new BigDecimal("40000"));
        BanSach bs = new BanSach();
        bs.setDauSach(ds);
        ChiTietMuon ct = new ChiTietMuon();
        ct.setBanSach(bs);
        ct.setTinhTrangKhiTra(tt);
        return ct;
    }

    @Test
    void hongNhePhat30PhanTram() {
        when(thamSo.laySo("TY_LE_HONG_NHE")).thenReturn(new BigDecimal("0.3"));
        BigDecimal tien = phatHuHong.tinh(traVoiTinhTrang(TinhTrangSach.HONG_NHE), LocalDate.now());
        assertEquals(0, new BigDecimal("12000").compareTo(tien));
    }

    @Test
    void hongNangPhat100PhanTram() {
        when(thamSo.laySo("TY_LE_HONG_NANG")).thenReturn(new BigDecimal("1.0"));
        BigDecimal tien = phatHuHong.tinh(traVoiTinhTrang(TinhTrangSach.HONG_NANG), LocalDate.now());
        assertEquals(0, new BigDecimal("40000").compareTo(tien));
    }

    @Test
    void binhThuongKhongPhat() {
        BigDecimal tien = phatHuHong.tinh(traVoiTinhTrang(TinhTrangSach.BINH_THUONG), LocalDate.now());
        assertEquals(0, BigDecimal.ZERO.compareTo(tien));
    }
}
