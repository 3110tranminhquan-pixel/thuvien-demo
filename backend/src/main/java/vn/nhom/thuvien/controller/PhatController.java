package vn.nhom.thuvien.controller;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.nhom.thuvien.dto.LuotMuonDto;
import vn.nhom.thuvien.dto.LyDoRequest;
import vn.nhom.thuvien.dto.MaVachRequest;
import vn.nhom.thuvien.dto.PhieuPhatDto;
import vn.nhom.thuvien.repository.NguoiDungRepository;
import vn.nhom.thuvien.security.NguoiDungDangNhap;
import vn.nhom.thuvien.service.BaoCaoService;
import vn.nhom.thuvien.service.ViPhamService;

import java.util.List;

@RestController
public class PhatController {

    private final ViPhamService viPhamService;
    private final BaoCaoService baoCaoService;
    private final NguoiDungRepository nguoiDungRepo;

    public PhatController(ViPhamService viPhamService, BaoCaoService baoCaoService, NguoiDungRepository nguoiDungRepo) {
        this.viPhamService = viPhamService;
        this.baoCaoService = baoCaoService;
        this.nguoiDungRepo = nguoiDungRepo;
    }

    @GetMapping("/api/phat")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI')")
    public List<PhieuPhatDto> danhSach(@RequestParam(required = false) String trangThai,
                                       @RequestParam(required = false) String tuKhoa) {
        return viPhamService.danhSach(trangThai, tuKhoa);
    }

    @GetMapping("/api/phat/{id}")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI')")
    public PhieuPhatDto mot(@PathVariable Long id) {
        return viPhamService.mot(id);
    }

    @PostMapping("/api/phat/{id}/thanh-toan")
    @PreAuthorize("hasRole('THU_THU')")
    public PhieuPhatDto thanhToan(@AuthenticationPrincipal NguoiDungDangNhap nd, @PathVariable Long id) {
        return viPhamService.thanhToan(id, nguoiDungRepo.getReferenceById(nd.nguoiDungId()));
    }

    @PostMapping("/api/phat/{id}/huy")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public PhieuPhatDto huy(@PathVariable Long id, @Valid @RequestBody LyDoRequest req) {
        return viPhamService.huy(id, req.lyDo());
    }

    @PostMapping("/api/phat/bao-mat")
    @PreAuthorize("hasRole('THU_THU')")
    public List<PhieuPhatDto> baoMat(@AuthenticationPrincipal NguoiDungDangNhap nd, @Valid @RequestBody MaVachRequest req) {
        return viPhamService.baoMat(req.maVach(), nguoiDungRepo.getReferenceById(nd.nguoiDungId()));
    }

    // FR-32
    @GetMapping("/api/bao-cao/qua-han")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI')")
    public List<LuotMuonDto> quaHan() {
        return baoCaoService.dsQuaHan();
    }
}
