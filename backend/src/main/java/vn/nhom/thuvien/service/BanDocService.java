package vn.nhom.thuvien.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom.thuvien.dto.BanDocDto;
import vn.nhom.thuvien.dto.BanDocRequest;
import vn.nhom.thuvien.dto.CapTheResponse;
import vn.nhom.thuvien.dto.ChuyenDoi;
import vn.nhom.thuvien.dto.HoSoBanDocDto;
import vn.nhom.thuvien.dto.LuotMuonDto;
import vn.nhom.thuvien.entity.BanDoc;
import vn.nhom.thuvien.entity.ChiTietMuon;
import vn.nhom.thuvien.entity.LoaiBanDoc;
import vn.nhom.thuvien.entity.NguoiDung;
import vn.nhom.thuvien.entity.VaiTro;
import vn.nhom.thuvien.entity.enums.TrangThaiThe;
import vn.nhom.thuvien.exception.LoiNghiepVu;
import vn.nhom.thuvien.repository.BanDocRepository;
import vn.nhom.thuvien.repository.ChiTietMuonRepository;
import vn.nhom.thuvien.repository.LoaiBanDocRepository;
import vn.nhom.thuvien.repository.NguoiDungRepository;
import vn.nhom.thuvien.repository.PhieuPhatRepository;
import vn.nhom.thuvien.repository.VaiTroRepository;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;

@Service
public class BanDocService {

    private static final String KY_TU_MAT_KHAU = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";

    private final BanDocRepository banDocRepo;
    private final LoaiBanDocRepository loaiRepo;
    private final NguoiDungRepository nguoiDungRepo;
    private final VaiTroRepository vaiTroRepo;
    private final ChiTietMuonRepository chiTietRepo;
    private final PhieuPhatRepository phieuPhatRepo;
    private final DatTruocService datTruocService;
    private final ThamSoService thamSo;
    private final NhatKyService nhatKy;
    private final ThongBaoService thongBao;
    private final PasswordEncoder encoder;
    private final SecureRandom ngauNhien = new SecureRandom();

    public BanDocService(BanDocRepository banDocRepo, LoaiBanDocRepository loaiRepo,
                         NguoiDungRepository nguoiDungRepo, VaiTroRepository vaiTroRepo,
                         ChiTietMuonRepository chiTietRepo, PhieuPhatRepository phieuPhatRepo,
                         DatTruocService datTruocService, ThamSoService thamSo, NhatKyService nhatKy,
                         ThongBaoService thongBao, PasswordEncoder encoder) {
        this.banDocRepo = banDocRepo;
        this.loaiRepo = loaiRepo;
        this.nguoiDungRepo = nguoiDungRepo;
        this.vaiTroRepo = vaiTroRepo;
        this.chiTietRepo = chiTietRepo;
        this.phieuPhatRepo = phieuPhatRepo;
        this.datTruocService = datTruocService;
        this.thamSo = thamSo;
        this.nhatKy = nhatKy;
        this.thongBao = thongBao;
        this.encoder = encoder;
    }

    public BanDoc layTheoId(Long id) {
        return banDocRepo.findById(id).orElseThrow(() -> LoiNghiepVu.khongTimThay("Không tìm thấy bạn đọc"));
    }

    @Transactional(readOnly = true)
    public BanDocDto timTheoMaThe(String maThe) {
        BanDoc bd = banDocRepo.findByMaThe(maThe.trim().toUpperCase())
                .orElseThrow(() -> LoiNghiepVu.khongTimThay("Không có bạn đọc mang mã thẻ " + maThe));
        return ChuyenDoi.banDoc(bd);
    }

    @Transactional(readOnly = true)
    public List<BanDocDto> danhSach(String tuKhoa) {
        return banDocRepo.timKiem(tuKhoa == null ? "" : tuKhoa.trim()).stream().map(ChuyenDoi::banDoc).toList();
    }

    // FR-15: hồ sơ gồm sách đang mượn, lịch sử mượn, tiền phạt và đặt trước
    @Transactional(readOnly = true)
    public HoSoBanDocDto hoSo(Long id) {
        BanDoc bd = layTheoId(id);
        List<ChiTietMuon> tatCa = chiTietRepo.findByPhieuMuonBanDocIdOrderByIdDesc(id);
        List<LuotMuonDto> dangMuon = tatCa.stream().filter(ChiTietMuon::dangMuon).map(ChuyenDoi::luotMuon).toList();
        List<LuotMuonDto> lichSu = tatCa.stream().filter(ct -> !ct.dangMuon()).map(ChuyenDoi::luotMuon).toList();
        return new HoSoBanDocDto(ChuyenDoi.banDoc(bd), dangMuon, lichSu,
                phieuPhatRepo.findByChiTietMuonPhieuMuonBanDocIdOrderByIdDesc(id).stream().map(ChuyenDoi::phieuPhat).toList(),
                datTruocService.dsCuaBanDoc(id));
    }

    // FR-11: cấp thẻ mới, tự tạo tài khoản đăng nhập (tên đăng nhập = mã thẻ)
    @Transactional
    public CapTheResponse capThe(BanDocRequest req) {
        if (banDocRepo.existsByEmail(req.email().trim()))
            throw new LoiNghiepVu("TRUNG_EMAIL", "Email này đã được dùng cho một bạn đọc khác");
        if (!req.ngayHetHan().isAfter(LocalDate.now()))
            throw new LoiNghiepVu("NGAY_SAI", "Ngày hết hạn thẻ phải sau ngày cấp");
        LoaiBanDoc loai = loaiRepo.findById(req.loaiId())
                .orElseThrow(() -> LoiNghiepVu.khongTimThay("Không có loại bạn đọc này"));

        String maThe = sinhMaThe();
        String matKhau = sinhMatKhau();

        NguoiDung nd = new NguoiDung();
        nd.setTenDangNhap(maThe);
        nd.setMatKhauHash(encoder.encode(matKhau));
        nd.setVaiTro(vaiTroRepo.getReferenceById(VaiTro.BAN_DOC));
        nguoiDungRepo.save(nd);

        BanDoc bd = new BanDoc();
        bd.setMaThe(maThe);
        ganThongTin(bd, req);
        bd.setLoai(loai);
        bd.setNguoiDung(nd);
        banDocRepo.save(bd);

        thongBao.guiMatKhauBanDau(bd, matKhau);
        nhatKy.ghi("CAP_THE", "ban_doc", maThe);
        return new CapTheResponse(ChuyenDoi.banDoc(bd), maThe, matKhau);
    }

    // FR-12: sửa thông tin, gia hạn thẻ (đổi ngày hết hạn)
    @Transactional
    public BanDocDto capNhat(Long id, BanDocRequest req) {
        BanDoc bd = layTheoId(id);
        if (banDocRepo.existsByEmailAndIdNot(req.email().trim(), id))
            throw new LoiNghiepVu("TRUNG_EMAIL", "Email này đã được dùng cho một bạn đọc khác");
        LoaiBanDoc loai = loaiRepo.findById(req.loaiId())
                .orElseThrow(() -> LoiNghiepVu.khongTimThay("Không có loại bạn đọc này"));
        ganThongTin(bd, req);
        bd.setLoai(loai);
        // Thẻ hết hạn được gia hạn thì về Hoạt động (sơ đồ trạng thái thẻ)
        if (bd.getTrangThai() == TrangThaiThe.HET_HAN && !bd.getNgayHetHan().isBefore(LocalDate.now())) {
            bd.setTrangThai(TrangThaiThe.HOAT_DONG);
            xetLaiTrangThaiThe(bd);
        }
        return ChuyenDoi.banDoc(bd);
    }

    // FR-13: khóa thủ công, bắt buộc có lý do
    @Transactional
    public BanDocDto khoaThuCong(Long id, String lyDo) {
        BanDoc bd = layTheoId(id);
        if (bd.getTrangThai() == TrangThaiThe.NGUNG)
            throw new LoiNghiepVu("THE_NGUNG", "Thẻ đã ngừng sử dụng");
        bd.setTrangThai(TrangThaiThe.BI_KHOA);
        bd.setKhoaThuCong(true);
        bd.setLyDoKhoa(lyDo.trim());
        datTruocService.huyTatCaCuaBanDoc(id);   // BR-14
        nhatKy.ghi("KHOA_THE", "ban_doc", bd.getMaThe() + ": " + lyDo.trim());
        return ChuyenDoi.banDoc(bd);
    }

    // BR-15: thẻ khóa thủ công chỉ được mở thủ công
    @Transactional
    public BanDocDto moKhoaThuCong(Long id) {
        BanDoc bd = layTheoId(id);
        if (bd.getTrangThai() != TrangThaiThe.BI_KHOA)
            throw new LoiNghiepVu("THE_KHONG_BI_KHOA", "Thẻ không ở trạng thái bị khóa");
        if (!Boolean.TRUE.equals(bd.getKhoaThuCong()))
            throw new LoiNghiepVu("KHOA_TU_DONG",
                    "Thẻ bị khóa tự động, sẽ tự mở khi bạn đọc trả hết sách quá hạn và thanh toán hết phiếu phạt");
        bd.setTrangThai(TrangThaiThe.HOAT_DONG);
        bd.setKhoaThuCong(false);
        bd.setLyDoKhoa(null);
        nhatKy.ghi("MO_THE", "ban_doc", bd.getMaThe());
        xetLaiTrangThaiThe(bd);   // còn vi phạm thì khóa tự động lại ngay
        return ChuyenDoi.banDoc(bd);
    }

    // FR-27, BR-14, BR-15: xét khóa / mở thẻ tự động. Gọi sau khi trả sách, thu tiền, hủy phiếu phạt
    // và chạy định kỳ lúc 0 giờ.
    @Transactional
    public void xetLaiTrangThaiThe(BanDoc bd) {
        if (Boolean.TRUE.equals(bd.getKhoaThuCong())) return;
        if (bd.getTrangThai() != TrangThaiThe.HOAT_DONG && bd.getTrangThai() != TrangThaiThe.BI_KHOA) return;

        LocalDate homNay = LocalDate.now();
        BigDecimal no = bd.tongNo();
        int tre = bd.soNgayTreLonNhat(homNay);
        BigDecimal nguongNo = thamSo.laySo("NGUONG_NO_KHOA");
        int nguongNgay = thamSo.laySo("NGUONG_NGAY_TRE_KHOA").intValue();

        if (bd.getTrangThai() == TrangThaiThe.HOAT_DONG) {
            String lyDo = null;
            if (no.compareTo(nguongNo) > 0) {
                lyDo = "Tự động: nợ phạt " + no.toPlainString() + " đồng, vượt mức " + nguongNo.toPlainString() + " đồng";
            } else if (tre > nguongNgay) {
                lyDo = "Tự động: có sách quá hạn " + tre + " ngày";
            }
            if (lyDo != null) {
                bd.setTrangThai(TrangThaiThe.BI_KHOA);
                bd.setLyDoKhoa(lyDo);
                datTruocService.huyTatCaCuaBanDoc(bd.getId());
            }
        } else if (tre == 0 && no.signum() == 0) {
            // BR-15: đã trả hết sách quá hạn và hết nợ thì mở lại
            bd.setTrangThai(TrangThaiThe.HOAT_DONG);
            bd.setLyDoKhoa(null);
        }
    }

    // Chạy lúc 0 giờ: thẻ quá ngày hết hạn chuyển Hết hạn, sau đó xét khóa / mở cho từng bạn đọc
    @Transactional
    public int xetKhoaTheTuDong() {
        LocalDate homNay = LocalDate.now();
        List<BanDoc> ds = banDocRepo.findByTrangThaiIn(List.of(TrangThaiThe.HOAT_DONG, TrangThaiThe.BI_KHOA));
        int soThayDoi = 0;
        for (BanDoc bd : ds) {
            TrangThaiThe truoc = bd.getTrangThai();
            if (truoc == TrangThaiThe.HOAT_DONG && bd.getNgayHetHan().isBefore(homNay)) {
                bd.setTrangThai(TrangThaiThe.HET_HAN);
            } else {
                xetLaiTrangThaiThe(bd);
            }
            if (bd.getTrangThai() != truoc) soThayDoi++;
        }
        return soThayDoi;
    }

    private void ganThongTin(BanDoc bd, BanDocRequest req) {
        bd.setHoTen(req.hoTen().trim());
        bd.setNgaySinh(req.ngaySinh());
        bd.setEmail(req.email().trim());
        bd.setSoDienThoai(req.soDienThoai());
        bd.setDonVi(req.donVi());
        bd.setNgayHetHan(req.ngayHetHan());
    }

    private String sinhMaThe() {
        String lonNhat = banDocRepo.maTheLonNhat();
        int so = lonNhat == null ? 1 : Integer.parseInt(lonNhat.substring(2)) + 1;
        return String.format("BD%06d", so);
    }

    // 10 ký tự, luôn có cả chữ và số
    private String sinhMatKhau() {
        StringBuilder sb = new StringBuilder();
        sb.append((char) ('a' + ngauNhien.nextInt(26)));
        sb.append((char) ('2' + ngauNhien.nextInt(8)));
        for (int i = 0; i < 8; i++) {
            sb.append(KY_TU_MAT_KHAU.charAt(ngauNhien.nextInt(KY_TU_MAT_KHAU.length())));
        }
        return sb.toString();
    }
}
