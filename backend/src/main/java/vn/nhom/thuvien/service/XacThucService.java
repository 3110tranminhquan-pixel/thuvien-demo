package vn.nhom.thuvien.service;

import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom.thuvien.dto.DangNhapResponse;
import vn.nhom.thuvien.dto.ThongTinNguoiDung;
import vn.nhom.thuvien.entity.BanDoc;
import vn.nhom.thuvien.entity.NguoiDung;
import vn.nhom.thuvien.entity.enums.TrangThaiTaiKhoan;
import vn.nhom.thuvien.exception.LoiNghiepVu;
import vn.nhom.thuvien.repository.BanDocRepository;
import vn.nhom.thuvien.repository.NguoiDungRepository;
import vn.nhom.thuvien.security.NguoiDungDangNhap;
import vn.nhom.thuvien.security.TienIchJwt;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class XacThucService {

    private static final String SAI_THONG_TIN = "Tên đăng nhập hoặc mật khẩu không đúng";

    private final NguoiDungRepository nguoiDungRepo;
    private final BanDocRepository banDocRepo;
    private final PasswordEncoder encoder;
    private final TienIchJwt tienIchJwt;
    private final NhatKyService nhatKy;

    // Các refresh token đã thu hồi khi đăng xuất: jti -> thời điểm hết hạn (giây).
    // Bản demo lưu trong bộ nhớ; khởi động lại máy chủ thì danh sách này mất.
    private final Map<String, Long> daThuHoi = new ConcurrentHashMap<>();

    public XacThucService(NguoiDungRepository nguoiDungRepo, BanDocRepository banDocRepo, PasswordEncoder encoder,
                          TienIchJwt tienIchJwt, NhatKyService nhatKy) {
        this.nguoiDungRepo = nguoiDungRepo;
        this.banDocRepo = banDocRepo;
        this.encoder = encoder;
        this.tienIchJwt = tienIchJwt;
        this.nhatKy = nhatKy;
    }

    public record KetQuaDangNhap(DangNhapResponse phanHoi, String refreshToken) {
    }

    // Không đặt @Transactional ở đây: lần đăng nhập sai vẫn phải lưu được số lần sai rồi mới báo lỗi
    public KetQuaDangNhap dangNhap(String tenDangNhap, String matKhau) {
        NguoiDung nd = nguoiDungRepo.findByTenDangNhap(tenDangNhap.trim())
                .orElseThrow(() -> new LoiNghiepVu("DANG_NHAP_SAI", SAI_THONG_TIN, HttpStatus.UNAUTHORIZED));
        LocalDateTime now = LocalDateTime.now();

        // NFR-07: còn trong thời gian khóa thì từ chối luôn, không kiểm tra mật khẩu
        if (nd.dangBiKhoaTam(now)) {
            throw new LoiNghiepVu("TAM_KHOA", "Tài khoản tạm khóa do nhập sai nhiều lần, thử lại sau "
                    + nd.getKhoaDen().format(DateTimeFormatter.ofPattern("HH:mm")), HttpStatus.UNAUTHORIZED);
        }
        if (!encoder.matches(matKhau, nd.getMatKhauHash())) {
            boolean biKhoa = nd.ghiNhanSai(now);
            nguoiDungRepo.save(nd);
            if (biKhoa) {
                nhatKy.ghiCho(nd.getId(), "KHOA_TAM_TAI_KHOAN", "nguoi_dung", nd.getTenDangNhap());
            }
            throw new LoiNghiepVu("DANG_NHAP_SAI", SAI_THONG_TIN, HttpStatus.UNAUTHORIZED);
        }
        if (nd.getTrangThai() == TrangThaiTaiKhoan.BI_KHOA) {
            throw new LoiNghiepVu("TAI_KHOAN_BI_KHOA", "Tài khoản đã bị khóa, vui lòng liên hệ quản trị viên",
                    HttpStatus.UNAUTHORIZED);
        }
        nd.datLaiSoLanSai();
        nguoiDungRepo.save(nd);
        nhatKy.ghiCho(nd.getId(), "DANG_NHAP", "nguoi_dung", nd.getTenDangNhap());

        NguoiDungDangNhap dn = taoThongTin(nd);
        return new KetQuaDangNhap(
                new DangNhapResponse(tienIchJwt.taoAccessToken(dn), thongTin(nd, dn)),
                tienIchJwt.taoRefreshToken(nd.getId()));
    }

    // Dùng refresh token (trong cookie) xin access token mới
    @Transactional(readOnly = true)
    public DangNhapResponse lamMoi(String refreshToken) {
        Claims c = refreshToken == null ? null : tienIchJwt.docRefreshToken(refreshToken);
        if (c == null || daThuHoi.containsKey(c.getId())) {
            throw new LoiNghiepVu("PHIEN_HET_HAN", "Phiên đăng nhập đã hết hạn", HttpStatus.UNAUTHORIZED);
        }
        NguoiDung nd = nguoiDungRepo.findById(Long.valueOf(c.getSubject()))
                .filter(x -> x.getTrangThai() == TrangThaiTaiKhoan.HOAT_DONG)
                .orElseThrow(() -> new LoiNghiepVu("PHIEN_HET_HAN", "Phiên đăng nhập đã hết hạn", HttpStatus.UNAUTHORIZED));
        NguoiDungDangNhap dn = taoThongTin(nd);
        return new DangNhapResponse(tienIchJwt.taoAccessToken(dn), thongTin(nd, dn));
    }

    public void dangXuat(String refreshToken) {
        Claims c = refreshToken == null ? null : tienIchJwt.docRefreshToken(refreshToken);
        if (c != null) {
            daThuHoi.put(c.getId(), c.getExpiration().getTime() / 1000);
        }
        long bayGio = System.currentTimeMillis() / 1000;
        daThuHoi.values().removeIf(hetHan -> hetHan < bayGio);   // dọn các token đã tự hết hạn
    }

    // FR-03
    @Transactional
    public void doiMatKhau(Long nguoiDungId, String matKhauCu, String matKhauMoi) {
        NguoiDung nd = nguoiDungRepo.findById(nguoiDungId)
                .orElseThrow(() -> LoiNghiepVu.khongTimThay("Không tìm thấy tài khoản"));
        if (!encoder.matches(matKhauCu, nd.getMatKhauHash()))
            throw new LoiNghiepVu("SAI_MAT_KHAU", "Mật khẩu hiện tại không đúng");
        kiemTraChinhSach(matKhauMoi);
        nd.setMatKhauHash(encoder.encode(matKhauMoi));
        nhatKy.ghi("DOI_MAT_KHAU", "nguoi_dung", nd.getTenDangNhap());
    }

    // Chính sách mật khẩu (mục 3.13.1): tối thiểu 8 ký tự, có cả chữ và số
    public static void kiemTraChinhSach(String matKhau) {
        if (matKhau == null || matKhau.length() < 8 || !matKhau.matches(".*[A-Za-z].*") || !matKhau.matches(".*\\d.*"))
            throw new LoiNghiepVu("MAT_KHAU_YEU", "Mật khẩu phải có tối thiểu 8 ký tự, gồm cả chữ và số");
    }

    private NguoiDungDangNhap taoThongTin(NguoiDung nd) {
        Long banDocId = banDocRepo.findByNguoiDungId(nd.getId()).map(BanDoc::getId).orElse(null);
        return new NguoiDungDangNhap(nd.getId(), nd.getTenDangNhap(), nd.tenVaiTro(), banDocId);
    }

    private ThongTinNguoiDung thongTin(NguoiDung nd, NguoiDungDangNhap dn) {
        Optional<BanDoc> bd = dn.banDocId() == null ? Optional.empty() : banDocRepo.findById(dn.banDocId());
        return new ThongTinNguoiDung(nd.getId(), nd.getTenDangNhap(), dn.vaiTro(), dn.banDocId(),
                bd.map(BanDoc::getHoTen).orElse(nd.getTenDangNhap()));
    }
}
