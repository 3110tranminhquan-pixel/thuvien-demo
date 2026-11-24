package vn.nhom.thuvien.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import vn.nhom.thuvien.dto.PhieuMuonDto;
import vn.nhom.thuvien.entity.BanDoc;
import vn.nhom.thuvien.entity.BanSach;
import vn.nhom.thuvien.entity.DauSach;
import vn.nhom.thuvien.entity.LoaiBanDoc;
import vn.nhom.thuvien.entity.PhieuMuon;
import vn.nhom.thuvien.entity.enums.LoaiTaiLieu;
import vn.nhom.thuvien.entity.enums.TrangThaiBanSach;
import vn.nhom.thuvien.entity.enums.TrangThaiThe;
import vn.nhom.thuvien.exception.LoiNghiepVu;
import vn.nhom.thuvien.repository.BanDocRepository;
import vn.nhom.thuvien.repository.BanSachRepository;
import vn.nhom.thuvien.repository.ChiTietMuonRepository;
import vn.nhom.thuvien.repository.DatTruocRepository;
import vn.nhom.thuvien.repository.PhieuMuonRepository;
import vn.nhom.thuvien.repository.PhieuPhatRepository;
import vn.nhom.thuvien.service.phat.PhatHuHong;
import vn.nhom.thuvien.service.phat.PhatTreHan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Các nhánh chính của taoPhieuMuon (BR-01 → BR-06)
@ExtendWith(MockitoExtension.class)
class MuonTraServiceTest {

    @Mock BanDocRepository banDocRepo;
    @Mock BanSachRepository banSachRepo;
    @Mock PhieuMuonRepository phieuMuonRepo;
    @Mock ChiTietMuonRepository chiTietRepo;
    @Mock DatTruocRepository datTruocRepo;
    @Mock PhieuPhatRepository phieuPhatRepo;
    @Mock DatTruocService datTruocService;
    @Mock BanDocService banDocService;
    @Mock PhatTreHan phatTreHan;
    @Mock PhatHuHong phatHuHong;
    @Mock ApplicationEventPublisher suKien;
    @InjectMocks MuonTraService muonTraService;

    BanDoc bd;

    @BeforeEach
    void caiDat() {
        LoaiBanDoc sv = new LoaiBanDoc();
        sv.setSoSachToiDa(5);
        sv.setSoNgayMuon(14);
        bd = new BanDoc();
        bd.setId(1L);
        bd.setMaThe("BD000001");
        bd.setLoai(sv);
        bd.setNgayHetHan(LocalDate.now().plusYears(1));
        when(banDocRepo.findById(1L)).thenReturn(Optional.of(bd));
    }

    private BanSach banSach(String maVach, long dauSachId, LoaiTaiLieu loai) {
        DauSach ds = new DauSach();
        ds.setId(dauSachId);
        ds.setNhanDe("Sách " + dauSachId);
        ds.setGiaBia(new BigDecimal("50000"));
        ds.setLoaiTaiLieu(loai);
        BanSach bs = new BanSach();
        bs.setMaVach(maVach);
        bs.setDauSach(ds);
        return bs;
    }

    @Test
    void theBiKhoaKhongDuocMuon() {
        bd.setTrangThai(TrangThaiThe.BI_KHOA);
        LoiNghiepVu loi = assertThrows(LoiNghiepVu.class,
                () -> muonTraService.taoPhieuMuon(1L, List.of("GT00000001"), null));
        assertEquals("KHONG_DU_DIEU_KIEN", loi.getCode());
    }

    @Test
    void vuotHanMucBiTuChoi() {
        bd.getLoai().setSoSachToiDa(1);
        LoiNghiepVu loi = assertThrows(LoiNghiepVu.class,
                () -> muonTraService.taoPhieuMuon(1L, List.of("GT00000001", "GT00000002"), null));
        assertEquals("VUOT_HAN_MUC", loi.getCode());
    }

    @Test
    void sachThamKhaoChiDocTaiCho() {
        when(banSachRepo.timDeMuon("TK00000001")).thenReturn(Optional.of(banSach("TK00000001", 4L, LoaiTaiLieu.THAM_KHAO)));
        LoiNghiepVu loi = assertThrows(LoiNghiepVu.class,
                () -> muonTraService.taoPhieuMuon(1L, List.of("TK00000001"), null));
        assertEquals("CHI_DOC_TAI_CHO", loi.getCode());
    }

    @Test
    void muonThanhCongHanTra14Ngay() {
        BanSach bs = banSach("GT00000001", 1L, LoaiTaiLieu.GIAO_TRINH);
        when(banSachRepo.timDeMuon("GT00000001")).thenReturn(Optional.of(bs));
        when(phieuMuonRepo.save(any(PhieuMuon.class))).thenAnswer(inv -> inv.getArgument(0));

        PhieuMuonDto pm = muonTraService.taoPhieuMuon(1L, List.of("GT00000001"), null);

        assertEquals(TrangThaiBanSach.DANG_MUON, bs.getTrangThai());
        assertEquals(LocalDate.now().plusDays(14), pm.dsChiTiet().get(0).hanTra());
        verify(suKien).publishEvent(any(SuKienDaMuon.class));
    }
}
