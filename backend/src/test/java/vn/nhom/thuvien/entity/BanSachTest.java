package vn.nhom.thuvien.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import vn.nhom.thuvien.entity.enums.TrangThaiBanSach;
import vn.nhom.thuvien.exception.LoiNghiepVu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Kiểm thử chuyển trạng thái theo sơ đồ ở mục 3.8.2 (TC-15)
class BanSachTest {

    private BanSach banSach(TrangThaiBanSach tt) {
        BanSach bs = new BanSach();
        bs.setMaVach("GT00000001");
        bs.setTrangThai(tt);
        return bs;
    }

    @ParameterizedTest(name = "{0} -> {1} phải bị chặn")
    @CsvSource({
        "MAT, DANG_MUON",
        "THANH_LY, SAN_SANG",
        "DANG_SUA, DANG_MUON",
        "SAN_SANG, DANG_GIU"
    })
    void chanChuyenDoiKhongCoTrenSoDo(TrangThaiBanSach tu, TrangThaiBanSach den) {
        BanSach bs = banSach(tu);
        assertThrows(LoiNghiepVu.class, () -> bs.chuyenTrangThai(den));
        assertEquals(tu, bs.getTrangThai());
    }

    @ParameterizedTest(name = "{0} -> {1} hợp lệ")
    @CsvSource({
        "SAN_SANG, DANG_MUON",
        "DANG_MUON, QUA_HAN",
        "DANG_MUON, DANG_GIU",
        "QUA_HAN, SAN_SANG",
        "DANG_GIU, DANG_MUON",
        "DANG_SUA, THANH_LY"
    })
    void choPhepChuyenDoiTrenSoDo(TrangThaiBanSach tu, TrangThaiBanSach den) {
        BanSach bs = banSach(tu);
        bs.chuyenTrangThai(den);
        assertEquals(den, bs.getTrangThai());
    }

    @Test
    void matLaTrangThaiKetThuc() {
        assertEquals(0, TrangThaiBanSach.MAT.trangThaiKeTiep().size());
    }
}
