package vn.nhom.thuvien.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom.thuvien.dto.ChuyenDoi;
import vn.nhom.thuvien.dto.TaiKhoanDto;
import vn.nhom.thuvien.entity.NguoiDung;
import vn.nhom.thuvien.entity.VaiTro;
import vn.nhom.thuvien.entity.enums.TrangThaiTaiKhoan;
import vn.nhom.thuvien.exception.LoiNghiepVu;
import vn.nhom.thuvien.repository.BanDocRepository;
import vn.nhom.thuvien.repository.NguoiDungRepository;
import vn.nhom.thuvien.repository.VaiTroRepository;

import java.util.List;

// FR-34: quản lý tài khoản nhân viên. Tài khoản bạn đọc được tạo khi cấp thẻ (BanDocService).
@Service
public class QuanTriService {

    private final NguoiDungRepository nguoiDungRepo;
    private final VaiTroRepository vaiTroRepo;
    private final BanDocRepository banDocRepo;
    private final PasswordEncoder encoder;
    private final NhatKyService nhatKy;

    public QuanTriService(NguoiDungRepository nguoiDungRepo, VaiTroRepository vaiTroRepo,
                          BanDocRepository banDocRepo, PasswordEncoder encoder, NhatKyService nhatKy) {
        this.nguoiDungRepo = nguoiDungRepo;
        this.vaiTroRepo = vaiTroRepo;
        this.banDocRepo = banDocRepo;
        this.encoder = encoder;
        this.nhatKy = nhatKy;
    }

    @Transactional(readOnly = true)
    public List<TaiKhoanDto> dsTaiKhoan() {
        return nguoiDungRepo.findAllByOrderByIdAsc().stream().map(ChuyenDoi::taiKhoan).toList();
    }

    @Transactional
    public TaiKhoanDto taoTaiKhoan(String tenDangNhap, String matKhau, Integer vaiTroId) {
        String ten = tenDangNhap.trim();
        if (nguoiDungRepo.existsByTenDangNhap(ten))
            throw new LoiNghiepVu("TRUNG_TEN", "Tên đăng nhập đã tồn tại");
        if (vaiTroId == VaiTro.BAN_DOC)
            throw new LoiNghiepVu("SAI_VAI_TRO", "Tài khoản bạn đọc được tạo tự động khi cấp thẻ");
        XacThucService.kiemTraChinhSach(matKhau);
        NguoiDung nd = new NguoiDung();
        nd.setTenDangNhap(ten);
        nd.setMatKhauHash(encoder.encode(matKhau));
        nd.setVaiTro(layVaiTro(vaiTroId));
        nguoiDungRepo.save(nd);
        nhatKy.ghi("TAO_TAI_KHOAN", "nguoi_dung", ten);
        return ChuyenDoi.taiKhoan(nd);
    }

    @Transactional
    public TaiKhoanDto capNhat(Long id, Integer vaiTroId, String trangThai, Long nguoiThucHienId) {
        NguoiDung nd = nguoiDungRepo.findById(id).orElseThrow(() -> LoiNghiepVu.khongTimThay("Không tìm thấy tài khoản"));
        boolean laBanDoc = banDocRepo.findByNguoiDungId(id).isPresent();

        if (vaiTroId != null && !vaiTroId.equals(nd.getVaiTro().getId())) {
            if (laBanDoc || vaiTroId == VaiTro.BAN_DOC)
                throw new LoiNghiepVu("SAI_VAI_TRO", "Không đổi vai trò giữa bạn đọc và nhân viên");
            if (id.equals(nguoiThucHienId))
                throw new LoiNghiepVu("TU_DOI_VAI_TRO", "Không thể tự đổi vai trò của chính mình");
            nd.setVaiTro(layVaiTro(vaiTroId));
            nhatKy.ghi("DOI_VAI_TRO", "nguoi_dung", nd.getTenDangNhap() + " -> " + nd.getVaiTro().getTen());
        }
        if (trangThai != null && !trangThai.isBlank()) {
            TrangThaiTaiKhoan tt;
            try {
                tt = TrangThaiTaiKhoan.valueOf(trangThai);
            } catch (IllegalArgumentException e) {
                throw new LoiNghiepVu("TRANG_THAI_SAI", "Trạng thái tài khoản không hợp lệ");
            }
            if (tt == TrangThaiTaiKhoan.BI_KHOA && id.equals(nguoiThucHienId))
                throw new LoiNghiepVu("TU_KHOA", "Không thể tự khóa tài khoản của chính mình");
            if (tt != nd.getTrangThai()) {
                nd.setTrangThai(tt);
                if (tt == TrangThaiTaiKhoan.HOAT_DONG) nd.datLaiSoLanSai();
                nhatKy.ghi(tt == TrangThaiTaiKhoan.BI_KHOA ? "KHOA_TAI_KHOAN" : "MO_TAI_KHOAN",
                        "nguoi_dung", nd.getTenDangNhap());
            }
        }
        return ChuyenDoi.taiKhoan(nd);
    }

    private VaiTro layVaiTro(Integer id) {
        return vaiTroRepo.findById(id).orElseThrow(() -> LoiNghiepVu.khongTimThay("Không có vai trò này"));
    }
}
