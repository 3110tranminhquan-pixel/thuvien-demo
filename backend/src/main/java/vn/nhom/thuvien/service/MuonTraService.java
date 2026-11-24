package vn.nhom.thuvien.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom.thuvien.dto.ChuyenDoi;
import vn.nhom.thuvien.dto.KetQuaTraDto;
import vn.nhom.thuvien.dto.PhieuMuonDto;
import vn.nhom.thuvien.dto.SachQuetDto;
import vn.nhom.thuvien.dto.XemTruocTraDto;
import vn.nhom.thuvien.entity.BanDoc;
import vn.nhom.thuvien.entity.BanSach;
import vn.nhom.thuvien.entity.ChiTietMuon;
import vn.nhom.thuvien.entity.DatTruoc;
import vn.nhom.thuvien.entity.DauSach;
import vn.nhom.thuvien.entity.KetQuaKiemTra;
import vn.nhom.thuvien.entity.LoaiBanDoc;
import vn.nhom.thuvien.entity.NguoiDung;
import vn.nhom.thuvien.entity.PhieuMuon;
import vn.nhom.thuvien.entity.PhieuPhat;
import vn.nhom.thuvien.entity.enums.LoaiViPham;
import vn.nhom.thuvien.entity.enums.TinhTrangSach;
import vn.nhom.thuvien.entity.enums.TrangThaiBanSach;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class MuonTraService {

    private final BanDocRepository banDocRepo;
    private final BanSachRepository banSachRepo;
    private final PhieuMuonRepository phieuMuonRepo;
    private final ChiTietMuonRepository chiTietRepo;
    private final DatTruocRepository datTruocRepo;
    private final PhieuPhatRepository phieuPhatRepo;
    private final DatTruocService datTruocService;
    private final BanDocService banDocService;
    private final PhatTreHan phatTreHan;
    private final PhatHuHong phatHuHong;
    private final ApplicationEventPublisher suKien;

    public MuonTraService(BanDocRepository banDocRepo, BanSachRepository banSachRepo,
                          PhieuMuonRepository phieuMuonRepo, ChiTietMuonRepository chiTietRepo,
                          DatTruocRepository datTruocRepo, PhieuPhatRepository phieuPhatRepo,
                          DatTruocService datTruocService, BanDocService banDocService,
                          PhatTreHan phatTreHan, PhatHuHong phatHuHong, ApplicationEventPublisher suKien) {
        this.banDocRepo = banDocRepo;
        this.banSachRepo = banSachRepo;
        this.phieuMuonRepo = phieuMuonRepo;
        this.chiTietRepo = chiTietRepo;
        this.datTruocRepo = datTruocRepo;
        this.phieuPhatRepo = phieuPhatRepo;
        this.datTruocService = datTruocService;
        this.banDocService = banDocService;
        this.phatTreHan = phatTreHan;
        this.phatHuHong = phatHuHong;
        this.suKien = suKien;
    }

    // Kiểm tra từng cuốn ngay lúc quét để thủ thư biết sớm, chưa ghi gì vào CSDL.
    // Khi bấm "Xác nhận mượn", taoPhieuMuon kiểm tra lại toàn bộ một lần nữa.
    @Transactional(readOnly = true)
    public SachQuetDto kiemTraSach(Long banDocId, String maVach, List<String> dsDaQuet) {
        BanDoc bd = banDocRepo.findById(banDocId)
                .orElseThrow(() -> new LoiNghiepVu("KHONG_TIM_THAY", "Không tìm thấy bạn đọc"));
        KetQuaKiemTra kq = bd.coTheMuon(LocalDate.now());
        if (!kq.isDat()) throw new LoiNghiepVu("KHONG_DU_DIEU_KIEN", kq.getLyDo());

        List<String> daQuet = dsDaQuet == null ? List.of() : dsDaQuet;
        String ma = maVach.trim().toUpperCase();
        if (daQuet.contains(ma))
            throw new LoiNghiepVu("DA_QUET", "Cuốn này đã có trong danh sách");

        LoaiBanDoc loai = bd.getLoai();
        if (bd.soSachDangMuon() + daQuet.size() + 1 > loai.getSoSachToiDa())
            throw new LoiNghiepVu("VUOT_HAN_MUC", "Bạn đọc đã mượn tối đa " + loai.getSoSachToiDa() + " cuốn");

        BanSach bs = banSachRepo.findByMaVach(ma)
                .orElseThrow(() -> new LoiNghiepVu("MA_VACH_SAI", "Mã vạch " + ma + " không tồn tại"));
        DauSach ds = bs.getDauSach();
        kiemTraBanSach(bd, bs, ds);

        Set<Long> dauSachDaQuet = new HashSet<>();
        for (String m : daQuet) {
            banSachRepo.findByMaVach(m).ifPresent(b -> dauSachDaQuet.add(b.getDauSach().getId()));
        }
        if (dauSachDaQuet.contains(ds.getId()))
            throw new LoiNghiepVu("TRUNG_DAU_SACH", "Không được mượn 2 bản của cùng một đầu sách");

        return new SachQuetDto(bs.getMaVach(), ds.getId(), ds.getNhanDe(), bs.getViTriKe(),
                LocalDate.now().plusDays(loai.getSoNgayMuon()));
    }

    @Transactional
    public PhieuMuonDto taoPhieuMuon(Long banDocId, List<String> dsMaVach, NguoiDung thuThu) {
        BanDoc bd = banDocRepo.findById(banDocId)
            .orElseThrow(() -> new LoiNghiepVu("KHONG_TIM_THAY", "Không tìm thấy bạn đọc"));

        KetQuaKiemTra kq = bd.coTheMuon(LocalDate.now());              // BR-01, BR-03
        if (!kq.isDat()) throw new LoiNghiepVu("KHONG_DU_DIEU_KIEN", kq.getLyDo());

        LoaiBanDoc loai = bd.getLoai();                                // BR-02
        if (bd.soSachDangMuon() + dsMaVach.size() > loai.getSoSachToiDa())
            throw new LoiNghiepVu("VUOT_HAN_MUC",
                "Bạn đọc đã mượn tối đa " + loai.getSoSachToiDa() + " cuốn");

        LocalDate hanTra = LocalDate.now().plusDays(loai.getSoNgayMuon()); // BR-06
        PhieuMuon phieu = new PhieuMuon(bd, thuThu);
        Set<Long> dauSachDaChon = new HashSet<>();
        List<String> dongEmail = new ArrayList<>();

        for (String maVach : dsMaVach) {
            String ma = maVach.trim().toUpperCase();
            BanSach bs = banSachRepo.timDeMuon(ma)                     // khóa dòng
                .orElseThrow(() -> new LoiNghiepVu("MA_VACH_SAI", "Mã vạch " + ma + " không tồn tại"));
            DauSach ds = bs.getDauSach();

            if (!dauSachDaChon.add(ds.getId()))                         // BR-05 trong cùng phiếu
                throw new LoiNghiepVu("TRUNG_DAU_SACH", "Không được mượn 2 bản của cùng một đầu sách");
            DatTruoc dangGiu = kiemTraBanSach(bd, bs, ds);              // BR-04, BR-05
            if (dangGiu != null) {
                dangGiu.daNhan();                                       // người đặt trước đến nhận
            }

            phieu.themChiTiet(bs, hanTra);
            bs.chuyenTrangThai(TrangThaiBanSach.DANG_MUON);
            dongEmail.add(ds.getNhanDe() + " (mã " + bs.getMaVach() + "), hạn trả " + hanTra);
        }

        PhieuMuon daLuu = phieuMuonRepo.save(phieu);
        // email gửi sau khi lưu xong (ThongBaoService nghe sự kiện ở pha AFTER_COMMIT)
        suKien.publishEvent(new SuKienDaMuon(daLuu.getId(), bd.getEmail(), bd.getHoTen(), dongEmail));
        return ChuyenDoi.phieuMuon(daLuu);
    }

    // Các điều kiện riêng của một bản sách. Trả về lượt đặt trước đang giữ bản này cho chính bạn đọc (nếu có).
    private DatTruoc kiemTraBanSach(BanDoc bd, BanSach bs, DauSach ds) {
        if (!ds.choMuonVe())                                            // BR-04
            throw new LoiNghiepVu("CHI_DOC_TAI_CHO", "Sách \"" + ds.getNhanDe() + "\" chỉ đọc tại chỗ");
        if (bd.dangMuonDauSach(ds.getId()))                             // BR-05
            throw new LoiNghiepVu("TRUNG_DAU_SACH", "Bạn đọc đang mượn một bản khác của đầu sách này");

        if (bs.getTrangThai() == TrangThaiBanSach.DANG_GIU) {           // BR-05: sách đang giữ
            DatTruoc dt = datTruocRepo.timDangGiu(bs.getId())
                .orElseThrow(() -> new LoiNghiepVu("KHONG_SAN_SANG", "Bản sách đang giữ nhưng không tìm thấy lượt đặt trước"));
            if (!dt.getBanDoc().getId().equals(bd.getId()))
                throw new LoiNghiepVu("DANG_GIU_NGUOI_KHAC", "Sách đang được giữ cho bạn đọc khác");
            return dt;
        }
        if (bs.getTrangThai() != TrangThaiBanSach.SAN_SANG)
            throw new LoiNghiepVu("KHONG_SAN_SANG",
                "Bản sách đang ở trạng thái \"" + bs.getTrangThai().getTen() + "\", không thể cho mượn");
        return null;
    }

    // Hiện thông tin lượt mượn và tiền phạt tạm tính trước khi thủ thư xác nhận trả
    @Transactional(readOnly = true)
    public XemTruocTraDto xemTruocTra(String maVach) {
        ChiTietMuon ct = timLuotDangMuon(maVach, false);
        BanSach bs = ct.getBanSach();
        BanDoc bd = ct.getPhieuMuon().getBanDoc();
        LocalDate homNay = LocalDate.now();
        return new XemTruocTraDto(ct.getId(), bs.getMaVach(), bs.getDauSach().getNhanDe(),
                bs.getDauSach().getGiaBia(), bd.getMaThe(), bd.getHoTen(), ct.getPhieuMuon().getNgayMuon(),
                ct.getHanTra(), ct.soNgayTre(homNay), phatTreHan.tinh(ct, homNay),
                phatHuHong.tinhTheoTinhTrang(ct, TinhTrangSach.HONG_NHE),
                phatHuHong.tinhTheoTinhTrang(ct, TinhTrangSach.HONG_NANG));
    }

    // Trả sách: mọi bước nằm trong một giao tác (mục 4.5.2)
    @Transactional
    public KetQuaTraDto traSach(String maVach, String tinhTrangChon, NguoiDung thuThu) {
        TinhTrangSach tinhTrang = docTinhTrang(tinhTrangChon);           // BR-08: bắt buộc chọn
        ChiTietMuon ct = timLuotDangMuon(maVach, true);
        BanSach bs = ct.getBanSach();
        BanDoc bd = ct.getPhieuMuon().getBanDoc();
        LocalDate homNay = LocalDate.now();

        int soNgayTre = ct.soNgayTre(homNay);
        ct.ghiNhanTra(tinhTrang, LocalDateTime.now());

        BigDecimal tienTre = phatTreHan.tinh(ct, homNay);                // BR-07
        if (tienTre.signum() > 0) {
            phieuPhatRepo.save(PhieuPhat.lap(ct, LoaiViPham.TRE_HAN, tienTre, thuThu));
        }
        BigDecimal tienHong = phatHuHong.tinh(ct, homNay);               // BR-08
        if (tienHong.signum() > 0) {
            LoaiViPham loai = tinhTrang == TinhTrangSach.HONG_NANG ? LoaiViPham.HONG_NANG : LoaiViPham.HONG_NHE;
            phieuPhatRepo.save(PhieuPhat.lap(ct, loai, tienHong, thuThu));
        }

        // BR-09: hỏng nặng -> Đang sửa; có người chờ -> Đang giữ; còn lại -> Sẵn sàng
        String giuChoMaThe = null;
        String giuChoHoTen = null;
        if (tinhTrang == TinhTrangSach.HONG_NANG) {
            bs.chuyenTrangThai(TrangThaiBanSach.DANG_SUA);
        } else {
            Optional<DatTruoc> giu = datTruocService.giuChoNguoiKeTiep(bs);   // FR-23
            if (giu.isPresent()) {
                giuChoMaThe = giu.get().getBanDoc().getMaThe();
                giuChoHoTen = giu.get().getBanDoc().getHoTen();
            } else {
                bs.chuyenTrangThai(TrangThaiBanSach.SAN_SANG);
            }
        }

        banDocService.xetLaiTrangThaiThe(bd);                              // BR-14, BR-15
        return new KetQuaTraDto(bs.getMaVach(), bs.getDauSach().getNhanDe(), bd.getMaThe(), bd.getHoTen(),
                soNgayTre, tienTre.setScale(0, java.math.RoundingMode.HALF_UP),
                tienHong.setScale(0, java.math.RoundingMode.HALF_UP),
                bs.getTrangThai().getTen(), giuChoMaThe, giuChoHoTen, bd.getTrangThai().name());
    }

    // Chạy lúc 0 giờ: Đang mượn -> Quá hạn
    @Transactional
    public int danhDauQuaHan() {
        return banSachRepo.danhDauQuaHan(LocalDate.now());
    }

    private ChiTietMuon timLuotDangMuon(String maVach, boolean khoaDong) {
        String ma = maVach.trim().toUpperCase();
        Optional<BanSach> bs = khoaDong ? banSachRepo.timDeMuon(ma) : banSachRepo.findByMaVach(ma);
        BanSach banSach = bs.orElseThrow(() -> new LoiNghiepVu("MA_VACH_SAI", "Mã vạch " + ma + " không tồn tại"));
        return chiTietRepo.timDangMuon(banSach.getId())
                .orElseThrow(() -> new LoiNghiepVu("KHONG_DANG_MUON", "Bản sách " + ma + " không có lượt mượn nào chưa trả"));
    }

    private TinhTrangSach docTinhTrang(String giaTri) {
        TinhTrangSach tt;
        try {
            tt = TinhTrangSach.valueOf(giaTri);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new LoiNghiepVu("CHUA_CHON_TINH_TRANG", "Phải chọn tình trạng sách khi nhận trả");
        }
        if (tt == TinhTrangSach.MAT)
            throw new LoiNghiepVu("DUNG_BAO_MAT", "Trường hợp mất sách dùng chức năng Báo mất sách");
        return tt;
    }
}
