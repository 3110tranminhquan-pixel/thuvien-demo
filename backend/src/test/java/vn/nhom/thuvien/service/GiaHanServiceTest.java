package vn.nhom.thuvien.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.nhom.thuvien.entity.BanDoc;
import vn.nhom.thuvien.entity.BanSach;
import vn.nhom.thuvien.entity.ChiTietMuon;
import vn.nhom.thuvien.entity.DauSach;
import vn.nhom.thuvien.entity.LoaiBanDoc;
import vn.nhom.thuvien.entity.PhieuMuon;
import vn.nhom.thuvien.entity.enums.TrangThaiThe;
import vn.nhom.thuvien.exception.LoiNghiepVu;
import vn.nhom.thuvien.exception.LoiQuyen;
import vn.nhom.thuvien.repository.ChiTietMuonRepository;
import vn.nhom.thuvien.repository.DatTruocRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

// Một số dòng của bảng quyết định gia hạn (mục 5.2.3)
@ExtendWith(MockitoExtension.class)
class GiaHanServiceTest {

    @Mock ChiTietMuonRepository chiTietRepo;
    @Mock DatTruocRepository datTruocRepo;
    @InjectMocks GiaHanService giaHanService;

    ChiTietMuon ct;
    BanDoc bd;

    @BeforeEach
    void caiDat() {
        LoaiBanDoc loai = new LoaiBanDoc();
        loai.setSoLanGiaHan(1);
        loai.setSoNgayGiaHan(7);
        bd = new BanDoc();
        bd.setId(1L);
        bd.setLoai(loai);
        DauSach ds = new DauSach();
        ds.setId(5L);
        BanSach bs = new BanSach();
        bs.setDauSach(ds);
        PhieuMuon pm = new PhieuMuon(bd, null);
        ct = pm.themChiTiet(bs, LocalDate.now().plusDays(3));
        ct.setId(100L);
        when(chiTietRepo.findById(100L)).thenReturn(Optional.of(ct));
        lenient().when(datTruocRepo.coNguoiDangCho(5L)).thenReturn(false);
    }

    @Test
    void giaHanThanhCong() {
        LocalDate hanCu = ct.getHanTra();
        giaHanService.giaHan(100L, 1L);
        assertEquals(hanCu.plusDays(7), ct.getHanTra());
    }

    @Test
    void khongDuocGiaHanSachCuaNguoiKhac() {
        assertThrows(LoiQuyen.class, () -> giaHanService.giaHan(100L, 2L));
    }

    @Test
    void theBiKhoaKhongGiaHan() {
        bd.setTrangThai(TrangThaiThe.BI_KHOA);
        LoiNghiepVu loi = assertThrows(LoiNghiepVu.class, () -> giaHanService.giaHan(100L, 1L));
        assertEquals("THE_BI_KHOA", loi.getCode());
    }

    @Test
    void coNguoiDatTruocKhongGiaHan() {
        when(datTruocRepo.coNguoiDangCho(5L)).thenReturn(true);
        LoiNghiepVu loi = assertThrows(LoiNghiepVu.class, () -> giaHanService.giaHan(100L, 1L));
        assertEquals("CO_NGUOI_CHO", loi.getCode());
    }

    @Test
    void thuThuGiaHanTaiQuay() {
        giaHanService.giaHan(100L, null);
        assertEquals(1, ct.getSoLanGiaHan().intValue());
    }
}
