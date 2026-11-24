package vn.nhom.thuvien.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom.thuvien.dto.ChuyenDoi;
import vn.nhom.thuvien.dto.PhieuPhatDto;
import vn.nhom.thuvien.entity.BanDoc;
import vn.nhom.thuvien.entity.BanSach;
import vn.nhom.thuvien.entity.ChiTietMuon;
import vn.nhom.thuvien.entity.NguoiDung;
import vn.nhom.thuvien.entity.PhieuPhat;
import vn.nhom.thuvien.entity.enums.LoaiViPham;
import vn.nhom.thuvien.entity.enums.TinhTrangSach;
import vn.nhom.thuvien.entity.enums.TrangThaiBanSach;
import vn.nhom.thuvien.entity.enums.TrangThaiPhieuPhat;
import vn.nhom.thuvien.exception.LoiNghiepVu;
import vn.nhom.thuvien.repository.BanSachRepository;
import vn.nhom.thuvien.repository.ChiTietMuonRepository;
import vn.nhom.thuvien.repository.PhieuPhatRepository;
import vn.nhom.thuvien.service.phat.PhatMatSach;
import vn.nhom.thuvien.service.phat.PhatTreHan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ViPhamService {

    private final PhieuPhatRepository phieuPhatRepo;
    private final BanSachRepository banSachRepo;
    private final ChiTietMuonRepository chiTietRepo;
    private final PhatMatSach phatMatSach;
    private final PhatTreHan phatTreHan;
    private final BanDocService banDocService;
    private final NhatKyService nhatKy;

    public ViPhamService(PhieuPhatRepository phieuPhatRepo, BanSachRepository banSachRepo,
                         ChiTietMuonRepository chiTietRepo, PhatMatSach phatMatSach, PhatTreHan phatTreHan,
                         BanDocService banDocService, NhatKyService nhatKy) {
        this.phieuPhatRepo = phieuPhatRepo;
        this.banSachRepo = banSachRepo;
        this.chiTietRepo = chiTietRepo;
        this.phatMatSach = phatMatSach;
        this.phatTreHan = phatTreHan;
        this.banDocService = banDocService;
        this.nhatKy = nhatKy;
    }

    @Transactional(readOnly = true)
    public List<PhieuPhatDto> danhSach(String trangThai, String tuKhoa) {
        List<PhieuPhat> ds;
        if (trangThai == null || trangThai.isBlank()) {
            ds = phieuPhatRepo.findAllByOrderByIdDesc();
        } else {
            TrangThaiPhieuPhat tt;
            try {
                tt = TrangThaiPhieuPhat.valueOf(trangThai);
            } catch (IllegalArgumentException e) {
                throw new LoiNghiepVu("TRANG_THAI_SAI", "Trạng thái phiếu phạt không hợp lệ");
            }
            ds = phieuPhatRepo.findByTrangThaiOrderByIdDesc(tt);
        }
        String tk = tuKhoa == null ? "" : tuKhoa.trim().toLowerCase();
        return ds.stream().map(ChuyenDoi::phieuPhat)
                .filter(p -> tk.isEmpty() || p.maThe().toLowerCase().contains(tk) || p.hoTen().toLowerCase().contains(tk))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PhieuPhatDto> cuaBanDoc(Long banDocId) {
        return phieuPhatRepo.findByChiTietMuonPhieuMuonBanDocIdOrderByIdDesc(banDocId).stream()
                .map(ChuyenDoi::phieuPhat).toList();
    }

    @Transactional(readOnly = true)
    public PhieuPhatDto mot(Long id) {
        return ChuyenDoi.phieuPhat(layPhieu(id));
    }

    // FR-26: ghi nhận đã thu tiền
    @Transactional
    public PhieuPhatDto thanhToan(Long id, NguoiDung thuThu) {
        PhieuPhat pp = layPhieu(id);
        pp.thanhToan(thuThu);
        banDocService.xetLaiTrangThaiThe(banDocCua(pp));   // BR-15
        return ChuyenDoi.phieuPhat(pp);
    }

    // FR-25: chỉ quản trị viên hủy được phiếu lập nhầm, bắt buộc lý do, có ghi nhật ký
    @Transactional
    public PhieuPhatDto huy(Long id, String lyDo) {
        PhieuPhat pp = layPhieu(id);
        pp.huy(lyDo);
        nhatKy.ghi("HUY_PHIEU_PHAT", "phieu_phat", id + ": " + lyDo.trim());
        banDocService.xetLaiTrangThaiThe(banDocCua(pp));
        return ChuyenDoi.phieuPhat(pp);
    }

    // BR-13: báo mất sách. Đền 200% giá bìa, nếu đã quá hạn thì lập thêm phiếu phạt trễ tính đến ngày báo mất.
    @Transactional
    public List<PhieuPhatDto> baoMat(String maVach, NguoiDung thuThu) {
        String ma = maVach.trim().toUpperCase();
        BanSach bs = banSachRepo.timDeMuon(ma)
                .orElseThrow(() -> new LoiNghiepVu("MA_VACH_SAI", "Mã vạch " + ma + " không tồn tại"));
        ChiTietMuon ct = chiTietRepo.timDangMuon(bs.getId())
                .orElseThrow(() -> new LoiNghiepVu("KHONG_DANG_MUON", "Bản sách " + ma + " không có lượt mượn nào chưa trả"));
        LocalDate homNay = LocalDate.now();
        List<PhieuPhat> daLap = new ArrayList<>();

        BigDecimal tienTre = phatTreHan.tinh(ct, homNay);
        if (tienTre.signum() > 0) {
            daLap.add(phieuPhatRepo.save(PhieuPhat.lap(ct, LoaiViPham.TRE_HAN, tienTre, thuThu)));
        }
        daLap.add(phieuPhatRepo.save(PhieuPhat.lap(ct, LoaiViPham.MAT_SACH, phatMatSach.tinh(ct, homNay), thuThu)));

        ct.ghiNhanTra(TinhTrangSach.MAT, LocalDateTime.now());   // đóng lượt mượn với tình trạng "Mất"
        bs.chuyenTrangThai(TrangThaiBanSach.MAT);
        banDocService.xetLaiTrangThaiThe(ct.getPhieuMuon().getBanDoc());
        return daLap.stream().map(ChuyenDoi::phieuPhat).toList();
    }

    private PhieuPhat layPhieu(Long id) {
        return phieuPhatRepo.findById(id).orElseThrow(() -> LoiNghiepVu.khongTimThay("Không tìm thấy phiếu phạt"));
    }

    private BanDoc banDocCua(PhieuPhat pp) {
        return pp.getChiTietMuon().getPhieuMuon().getBanDoc();
    }
}
