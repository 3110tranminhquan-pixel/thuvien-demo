package vn.nhom.thuvien.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.nhom.thuvien.dto.HoSoBanDocDto;
import vn.nhom.thuvien.dto.LuotMuonDto;
import vn.nhom.thuvien.security.NguoiDungDangNhap;
import vn.nhom.thuvien.service.BanDocService;

import java.util.List;

// Các API "của tôi": không nhận mã bạn đọc từ người gọi mà tự lấy từ token
@RestController
@PreAuthorize("hasRole('BAN_DOC')")
public class ToiController {

    private final BanDocService banDocService;

    public ToiController(BanDocService banDocService) {
        this.banDocService = banDocService;
    }

    @GetMapping("/api/toi/ho-so")
    public HoSoBanDocDto hoSo(@AuthenticationPrincipal NguoiDungDangNhap nd) {
        return banDocService.hoSo(DatTruocController.banDocCua(nd));
    }

    @GetMapping("/api/toi/dang-muon")
    public List<LuotMuonDto> dangMuon(@AuthenticationPrincipal NguoiDungDangNhap nd) {
        return banDocService.hoSo(DatTruocController.banDocCua(nd)).dangMuon();
    }

    @GetMapping("/api/toi/lich-su")
    public List<LuotMuonDto> lichSu(@AuthenticationPrincipal NguoiDungDangNhap nd) {
        return banDocService.hoSo(DatTruocController.banDocCua(nd)).lichSu();
    }
}
