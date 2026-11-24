package vn.nhom.thuvien.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChiTietMuonTest {

    static final LocalDate HAN_TRA = LocalDate.of(2026, 11, 20);

    private LoaiBanDoc sinhVien() {
        LoaiBanDoc l = new LoaiBanDoc();
        l.setSoLanGiaHan(1);
        l.setSoNgayGiaHan(7);
        return l;
    }

    private ChiTietMuon taoChiTiet() {
        ChiTietMuon ct = new ChiTietMuon();
        ct.setHanTra(HAN_TRA);
        return ct;
    }

    @Test
    void traDungNgayHanKhongTre() {
        assertEquals(0, taoChiTiet().soNgayTre(HAN_TRA));
    }

    @Test
    void traSauHanMotNgayLaTreMotNgay() {
        assertEquals(1, taoChiTiet().soNgayTre(HAN_TRA.plusDays(1)));
    }

    @Test
    void giaHanDuocKhiChuaQuaHanVaConLuot() {
        assertTrue(taoChiTiet().kiemTraGiaHan(sinhVien(), HAN_TRA).isDat());
    }

    @Test
    void khongGiaHanKhiDaQuaHan() {
        KetQuaKiemTra kq = taoChiTiet().kiemTraGiaHan(sinhVien(), HAN_TRA.plusDays(1));
        assertFalse(kq.isDat());
        assertEquals("Sách đã quá hạn, vui lòng mang sách đến trả", kq.getLyDo());
    }

    @Test
    void khongGiaHanKhiHetLuot() {
        ChiTietMuon ct = taoChiTiet();
        ct.giaHan(7);
        assertFalse(ct.kiemTraGiaHan(sinhVien(), HAN_TRA).isDat());
    }

    // BR-10: hạn mới tính từ hạn cũ, không phải từ ngày gia hạn
    @Test
    void hanMoiTinhTuHanCu() {
        ChiTietMuon ct = taoChiTiet();
        ct.giaHan(7);
        assertEquals(HAN_TRA.plusDays(7), ct.getHanTra());
        assertEquals(1, ct.getSoLanGiaHan().intValue());
    }
}
