package vn.nhom.thuvien.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import vn.nhom.thuvien.dto.BanDocDto;
import vn.nhom.thuvien.dto.BanDocRequest;
import vn.nhom.thuvien.dto.CapTheResponse;
import vn.nhom.thuvien.dto.HoSoBanDocDto;
import vn.nhom.thuvien.dto.LoaiBanDocDto;
import vn.nhom.thuvien.dto.LyDoRequest;
import vn.nhom.thuvien.service.BanDocService;
import vn.nhom.thuvien.service.ThamSoService;

import java.util.List;

@RestController
public class BanDocController {

    private final BanDocService banDocService;
    private final ThamSoService thamSoService;

    public BanDocController(BanDocService banDocService, ThamSoService thamSoService) {
        this.banDocService = banDocService;
        this.thamSoService = thamSoService;
    }

    @GetMapping("/api/ban-doc")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI')")
    public List<BanDocDto> danhSach(@RequestParam(required = false) String tuKhoa) {
        return banDocService.danhSach(tuKhoa);
    }

    @GetMapping("/api/ban-doc/the/{maThe}")
    @PreAuthorize("hasRole('THU_THU')")
    public BanDocDto timTheoMaThe(@PathVariable String maThe) {
        return banDocService.timTheoMaThe(maThe);
    }

    @GetMapping("/api/ban-doc/{id}/lich-su")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI')")
    public HoSoBanDocDto hoSo(@PathVariable Long id) {
        return banDocService.hoSo(id);
    }

    @PostMapping("/api/ban-doc")
    @PreAuthorize("hasRole('THU_THU')")
    @ResponseStatus(HttpStatus.CREATED)
    public CapTheResponse capThe(@Valid @RequestBody BanDocRequest req) {
        return banDocService.capThe(req);
    }

    @PutMapping("/api/ban-doc/{id}")
    @PreAuthorize("hasRole('THU_THU')")
    public BanDocDto capNhat(@PathVariable Long id, @Valid @RequestBody BanDocRequest req) {
        return banDocService.capNhat(id, req);
    }

    @PostMapping("/api/ban-doc/{id}/khoa")
    @PreAuthorize("hasRole('THU_THU')")
    public BanDocDto khoa(@PathVariable Long id, @Valid @RequestBody LyDoRequest req) {
        return banDocService.khoaThuCong(id, req.lyDo());
    }

    @PostMapping("/api/ban-doc/{id}/mo-khoa")
    @PreAuthorize("hasRole('THU_THU')")
    public BanDocDto moKhoa(@PathVariable Long id) {
        return banDocService.moKhoaThuCong(id);
    }

    @GetMapping("/api/loai-ban-doc")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI')")
    public List<LoaiBanDocDto> dsLoai() {
        return thamSoService.dsLoaiBanDoc();
    }
}
