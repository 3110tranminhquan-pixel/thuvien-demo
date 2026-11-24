package vn.nhom.thuvien.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import vn.nhom.thuvien.dto.BanSachDto;
import vn.nhom.thuvien.dto.CapNhatBanSachRequest;
import vn.nhom.thuvien.dto.DanhMucDto;
import vn.nhom.thuvien.dto.DanhMucRequest;
import vn.nhom.thuvien.dto.DauSachChiTiet;
import vn.nhom.thuvien.dto.DauSachRequest;
import vn.nhom.thuvien.dto.DauSachTomTat;
import vn.nhom.thuvien.dto.TrangKetQua;
import vn.nhom.thuvien.exception.LoiNghiepVu;
import vn.nhom.thuvien.dto.ThemBanSachRequest;
import vn.nhom.thuvien.service.SachService;

import java.util.List;

@RestController
public class SachController {

    private final SachService sachService;

    public SachController(SachService sachService) {
        this.sachService = sachService;
    }

    // ----- Công khai -----

    @GetMapping("/api/sach")
    public TrangKetQua<DauSachTomTat> timKiem(@RequestParam(required = false) String tuKhoa,
                                             @RequestParam(required = false) Integer theLoai,
                                             @RequestParam(required = false) Integer nam,
                                             @RequestParam(defaultValue = "false") boolean conSach,
                                             @RequestParam(defaultValue = "0") int trang) {
        return sachService.timKiem(tuKhoa, theLoai, nam, conSach, trang);
    }

    @GetMapping("/api/sach/{id}")
    public DauSachChiTiet chiTiet(@PathVariable Long id) {
        return sachService.chiTiet(id);
    }

    @GetMapping("/api/danh-muc/{loai}")
    public List<DanhMucDto> danhMuc(@PathVariable String loai) {
        return switch (loai) {
            case "the-loai" -> sachService.dsTheLoai();
            case "nha-xuat-ban" -> sachService.dsNhaXuatBan();
            case "tac-gia" -> sachService.dsTacGia();
            default -> throw LoiNghiepVu.khongTimThay("Không có loại danh mục " + loai);
        };
    }

    // ----- Thủ thư -----

    @GetMapping("/api/quan-ly/dau-sach")
    @PreAuthorize("hasRole('THU_THU')")
    public List<DauSachTomTat> danhSachQuanLy(@RequestParam(required = false) String tuKhoa) {
        return sachService.danhSachQuanLy(tuKhoa);
    }

    @PostMapping("/api/sach")
    @PreAuthorize("hasRole('THU_THU')")
    @ResponseStatus(HttpStatus.CREATED)
    public DauSachChiTiet themDauSach(@Valid @RequestBody DauSachRequest req) {
        return sachService.themDauSach(req);
    }

    @PutMapping("/api/sach/{id}")
    @PreAuthorize("hasRole('THU_THU')")
    public DauSachChiTiet suaDauSach(@PathVariable Long id, @Valid @RequestBody DauSachRequest req) {
        return sachService.suaDauSach(id, req);
    }

    @PutMapping("/api/sach/{id}/trang-thai")
    @PreAuthorize("hasRole('THU_THU')")
    public DauSachChiTiet doiTrangThai(@PathVariable Long id, @RequestParam boolean ngung) {
        return sachService.doiTrangThaiDauSach(id, ngung);
    }

    @PostMapping("/api/sach/{id}/ban-sach")
    @PreAuthorize("hasRole('THU_THU')")
    @ResponseStatus(HttpStatus.CREATED)
    public List<BanSachDto> themBanSach(@PathVariable Long id, @Valid @RequestBody ThemBanSachRequest req) {
        return sachService.themBanSach(id, req);
    }

    @PutMapping("/api/ban-sach/{id}")
    @PreAuthorize("hasRole('THU_THU')")
    public BanSachDto capNhatBanSach(@PathVariable Long id, @RequestBody CapNhatBanSachRequest req) {
        return sachService.capNhatBanSach(id, req);
    }

    @PostMapping("/api/danh-muc/{loai}")
    @PreAuthorize("hasRole('THU_THU')")
    @ResponseStatus(HttpStatus.CREATED)
    public DanhMucDto themDanhMuc(@PathVariable String loai, @Valid @RequestBody DanhMucRequest req) {
        return luuDanhMuc(loai, null, req);
    }

    @PutMapping("/api/danh-muc/{loai}/{id}")
    @PreAuthorize("hasRole('THU_THU')")
    public DanhMucDto suaDanhMuc(@PathVariable String loai, @PathVariable Integer id,
                                 @Valid @RequestBody DanhMucRequest req) {
        return luuDanhMuc(loai, id, req);
    }

    @DeleteMapping("/api/danh-muc/{loai}/{id}")
    @PreAuthorize("hasRole('THU_THU')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void xoaDanhMuc(@PathVariable String loai, @PathVariable Integer id) {
        sachService.xoaDanhMuc(loai, id);
    }

    private DanhMucDto luuDanhMuc(String loai, Integer id, DanhMucRequest req) {
        return switch (loai) {
            case "the-loai" -> sachService.luuTheLoai(id, req);
            case "nha-xuat-ban" -> sachService.luuNhaXuatBan(id, req);
            case "tac-gia" -> sachService.luuTacGia(id, req);
            default -> throw LoiNghiepVu.khongTimThay("Không có loại danh mục " + loai);
        };
    }
}
