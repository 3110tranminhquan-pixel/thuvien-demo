package vn.nhom.thuvien.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import vn.nhom.thuvien.dto.ChuyenDoi;
import vn.nhom.thuvien.dto.NhatKyDto;
import vn.nhom.thuvien.entity.NhatKy;
import vn.nhom.thuvien.repository.NguoiDungRepository;
import vn.nhom.thuvien.repository.NhatKyRepository;
import vn.nhom.thuvien.security.NguoiDungDangNhap;
import vn.nhom.thuvien.security.NguoiDungHienTai;

import java.util.List;

// FR-36: ghi nhật ký các thao tác quan trọng
@Service
public class NhatKyService {

    private final NhatKyRepository nhatKyRepo;
    private final NguoiDungRepository nguoiDungRepo;

    public NhatKyService(NhatKyRepository nhatKyRepo, NguoiDungRepository nguoiDungRepo) {
        this.nhatKyRepo = nhatKyRepo;
        this.nguoiDungRepo = nguoiDungRepo;
    }

    // Ghi cho người đang gọi API
    @Transactional
    public void ghi(String hanhDong, String doiTuong, String maDoiTuong) {
        NguoiDungDangNhap nd = NguoiDungHienTai.layHoacNull();
        ghiCho(nd != null ? nd.nguoiDungId() : null, hanhDong, doiTuong, maDoiTuong);
    }

    // Ghi cho một người dùng cụ thể, ví dụ lúc đăng nhập (chưa có token)
    @Transactional
    public void ghiCho(Long nguoiDungId, String hanhDong, String doiTuong, String maDoiTuong) {
        NhatKy nk = new NhatKy();
        if (nguoiDungId != null) {
            nk.setNguoiDung(nguoiDungRepo.getReferenceById(nguoiDungId));
        }
        nk.setHanhDong(hanhDong);
        nk.setDoiTuong(doiTuong);
        nk.setMaDoiTuong(maDoiTuong);
        nk.setDiaChiIp(layDiaChiIp());
        nhatKyRepo.save(nk);
    }

    @Transactional(readOnly = true)
    public List<NhatKyDto> ganDay() {
        return nhatKyRepo.findTop300ByOrderByIdDesc().stream().map(ChuyenDoi::nhatKy).toList();
    }

    // Khi chạy sau Nginx thì IP thật nằm trong header X-Forwarded-For
    private String layDiaChiIp() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs)) {
            return null;   // tác vụ định kỳ, không có request
        }
        HttpServletRequest req = attrs.getRequest();
        String xff = req.getHeader("X-Forwarded-For");
        String ip = (xff != null && !xff.isBlank()) ? xff.split(",")[0].trim() : req.getRemoteAddr();
        return ip != null && ip.length() > 45 ? ip.substring(0, 45) : ip;
    }
}
