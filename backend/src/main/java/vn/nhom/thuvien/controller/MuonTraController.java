package vn.nhom.thuvien.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import vn.nhom.thuvien.dto.KetQuaTraDto;
import vn.nhom.thuvien.dto.KiemTraSachRequest;
import vn.nhom.thuvien.dto.LuotMuonDto;
import vn.nhom.thuvien.dto.PhieuMuonDto;
import vn.nhom.thuvien.dto.SachQuetDto;
import vn.nhom.thuvien.dto.TaoPhieuMuonRequest;
import vn.nhom.thuvien.dto.TraSachRequest;
import vn.nhom.thuvien.dto.XemTruocTraDto;
import vn.nhom.thuvien.repository.NguoiDungRepository;
import vn.nhom.thuvien.security.NguoiDungDangNhap;
import vn.nhom.thuvien.service.GiaHanService;
import vn.nhom.thuvien.service.MuonTraService;

@RestController
public class MuonTraController {

    private final MuonTraService muonTraService;
    private final GiaHanService giaHanService;
    private final NguoiDungRepository nguoiDungRepo;

    public MuonTraController(MuonTraService muonTraService, GiaHanService giaHanService,
                             NguoiDungRepository nguoiDungRepo) {
        this.muonTraService = muonTraService;
        this.giaHanService = giaHanService;
        this.nguoiDungRepo = nguoiDungRepo;
    }

    @PostMapping("/api/muon/kiem-tra-sach")
    @PreAuthorize("hasRole('THU_THU')")
    public SachQuetDto kiemTraSach(@Valid @RequestBody KiemTraSachRequest req) {
        return muonTraService.kiemTraSach(req.banDocId(), req.maVach(), req.dsMaVachDaQuet());
    }

    @PostMapping("/api/muon")
    @PreAuthorize("hasRole('THU_THU')")
    @ResponseStatus(HttpStatus.CREATED)
    public PhieuMuonDto taoPhieuMuon(@AuthenticationPrincipal NguoiDungDangNhap nd,
                                     @Valid @RequestBody TaoPhieuMuonRequest req) {
        return muonTraService.taoPhieuMuon(req.banDocId(), req.dsMaVach(),
                nguoiDungRepo.getReferenceById(nd.nguoiDungId()));
    }

    @GetMapping("/api/tra/xem-truoc/{maVach}")
    @PreAuthorize("hasRole('THU_THU')")
    public XemTruocTraDto xemTruocTra(@PathVariable String maVach) {
        return muonTraService.xemTruocTra(maVach);
    }

    @PostMapping("/api/tra")
    @PreAuthorize("hasRole('THU_THU')")
    public KetQuaTraDto traSach(@AuthenticationPrincipal NguoiDungDangNhap nd,
                                @Valid @RequestBody TraSachRequest req) {
        return muonTraService.traSach(req.maVach(), req.tinhTrang(),
                nguoiDungRepo.getReferenceById(nd.nguoiDungId()));
    }

    // Bạn đọc tự gia hạn (chỉ sách của mình) hoặc thủ thư gia hạn tại quầy
    @PostMapping("/api/gia-han/{chiTietId}")
    @PreAuthorize("hasAnyRole('BAN_DOC', 'THU_THU')")
    public LuotMuonDto giaHan(@AuthenticationPrincipal NguoiDungDangNhap nd, @PathVariable Long chiTietId) {
        // tài khoản bạn đọc mà không gắn hồ sơ nào thì dùng -1 để chắc chắn bị chặn ở bước kiểm tra quyền sở hữu
        Long banDocYeuCauId = nd.laBanDoc() ? (nd.banDocId() != null ? nd.banDocId() : -1L) : null;
        return giaHanService.giaHan(chiTietId, banDocYeuCauId);
    }
}
