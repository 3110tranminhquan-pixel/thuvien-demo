package vn.nhom.thuvien.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import vn.nhom.thuvien.dto.CapNhatTaiKhoanRequest;
import vn.nhom.thuvien.dto.GiaTriRequest;
import vn.nhom.thuvien.dto.LoaiBanDocDto;
import vn.nhom.thuvien.dto.NhatKyDto;
import vn.nhom.thuvien.dto.TaiKhoanDto;
import vn.nhom.thuvien.dto.TaoTaiKhoanRequest;
import vn.nhom.thuvien.dto.ThamSoDto;
import vn.nhom.thuvien.scheduler.LichTacVu;
import vn.nhom.thuvien.security.NguoiDungDangNhap;
import vn.nhom.thuvien.service.NhatKyService;
import vn.nhom.thuvien.service.QuanTriService;
import vn.nhom.thuvien.service.ThamSoService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quan-tri")
@PreAuthorize("hasRole('QUAN_TRI')")
public class QuanTriController {

    private final QuanTriService quanTriService;
    private final ThamSoService thamSoService;
    private final NhatKyService nhatKyService;
    private final LichTacVu lichTacVu;

    public QuanTriController(QuanTriService quanTriService, ThamSoService thamSoService,
                             NhatKyService nhatKyService, LichTacVu lichTacVu) {
        this.quanTriService = quanTriService;
        this.thamSoService = thamSoService;
        this.nhatKyService = nhatKyService;
        this.lichTacVu = lichTacVu;
    }

    @GetMapping("/tai-khoan")
    public List<TaiKhoanDto> dsTaiKhoan() {
        return quanTriService.dsTaiKhoan();
    }

    @PostMapping("/tai-khoan")
    @ResponseStatus(HttpStatus.CREATED)
    public TaiKhoanDto taoTaiKhoan(@Valid @RequestBody TaoTaiKhoanRequest req) {
        return quanTriService.taoTaiKhoan(req.tenDangNhap(), req.matKhau(), req.vaiTroId());
    }

    @PutMapping("/tai-khoan/{id}")
    public TaiKhoanDto capNhatTaiKhoan(@AuthenticationPrincipal NguoiDungDangNhap nd, @PathVariable Long id,
                                       @RequestBody CapNhatTaiKhoanRequest req) {
        return quanTriService.capNhat(id, req.vaiTroId(), req.trangThai(), nd.nguoiDungId());
    }

    @GetMapping("/tham-so")
    public List<ThamSoDto> dsThamSo() {
        return thamSoService.danhSach();
    }

    @PutMapping("/tham-so/{ma}")
    public ThamSoDto suaThamSo(@PathVariable String ma, @Valid @RequestBody GiaTriRequest req) {
        return thamSoService.capNhat(ma, req.giaTri());
    }

    @GetMapping("/loai-ban-doc")
    public List<LoaiBanDocDto> dsLoai() {
        return thamSoService.dsLoaiBanDoc();
    }

    @PutMapping("/loai-ban-doc/{id}")
    public LoaiBanDocDto suaLoai(@PathVariable Integer id, @Valid @RequestBody LoaiBanDocDto req) {
        return thamSoService.capNhatLoai(id, req);
    }

    @GetMapping("/nhat-ky")
    public List<NhatKyDto> nhatKy() {
        return nhatKyService.ganDay();
    }

    // Chạy ngay các tác vụ định kỳ, dùng khi demo hoặc kiểm thử mà không phải chờ đến 0 giờ
    @PostMapping("/chay-tac-vu")
    public Map<String, String> chayTacVu() {
        lichTacVu.chayLuc0Gio();
        lichTacVu.chayMoiGio();
        lichTacVu.guiNhacHan();
        return Map.of("message", "Đã chạy các tác vụ định kỳ: đánh dấu quá hạn, xét khóa thẻ, xử lý lượt giữ hết hạn, gửi email nhắc");
    }
}
