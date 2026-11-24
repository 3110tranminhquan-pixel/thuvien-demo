package vn.nhom.thuvien.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import vn.nhom.thuvien.dto.DatTruocDto;
import vn.nhom.thuvien.dto.DatTruocRequest;
import vn.nhom.thuvien.exception.LoiQuyen;
import vn.nhom.thuvien.security.NguoiDungDangNhap;
import vn.nhom.thuvien.service.DatTruocService;

@RestController
public class DatTruocController {

    private final DatTruocService datTruocService;

    public DatTruocController(DatTruocService datTruocService) {
        this.datTruocService = datTruocService;
    }

    // Mã bạn đọc lấy từ token, không nhận từ người gọi (mục 3.13.2)
    @PostMapping("/api/dat-truoc")
    @PreAuthorize("hasRole('BAN_DOC')")
    @ResponseStatus(HttpStatus.CREATED)
    public DatTruocDto datTruoc(@AuthenticationPrincipal NguoiDungDangNhap nd, @Valid @RequestBody DatTruocRequest req) {
        return datTruocService.datTruoc(banDocCua(nd), req.dauSachId());
    }

    @DeleteMapping("/api/dat-truoc/{id}")
    @PreAuthorize("hasRole('BAN_DOC')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void huy(@AuthenticationPrincipal NguoiDungDangNhap nd, @PathVariable Long id) {
        datTruocService.huy(id, banDocCua(nd));
    }

    static Long banDocCua(NguoiDungDangNhap nd) {
        if (nd.banDocId() == null) throw new LoiQuyen("Tài khoản không gắn với hồ sơ bạn đọc nào");
        return nd.banDocId();
    }
}
