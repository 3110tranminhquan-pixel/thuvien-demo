package vn.nhom.thuvien.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.nhom.thuvien.dto.DangNhapRequest;
import vn.nhom.thuvien.dto.DangNhapResponse;
import vn.nhom.thuvien.dto.DoiMatKhauRequest;
import vn.nhom.thuvien.security.NguoiDungDangNhap;
import vn.nhom.thuvien.security.TienIchJwt;
import vn.nhom.thuvien.service.XacThucService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String TEN_COOKIE = "refresh_token";

    private final XacThucService xacThucService;
    private final TienIchJwt tienIchJwt;
    private final boolean cookieSecure;

    public AuthController(XacThucService xacThucService, TienIchJwt tienIchJwt,
                          @Value("${thuvien.cookie-secure}") boolean cookieSecure) {
        this.xacThucService = xacThucService;
        this.tienIchJwt = tienIchJwt;
        this.cookieSecure = cookieSecure;
    }

    @PostMapping("/login")
    public ResponseEntity<DangNhapResponse> dangNhap(@Valid @RequestBody DangNhapRequest req) {
        XacThucService.KetQuaDangNhap kq = xacThucService.dangNhap(req.tenDangNhap(), req.matKhau());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, taoCookie(kq.refreshToken(), tienIchJwt.soGiayRefresh()).toString())
                .body(kq.phanHoi());
    }

    @PostMapping("/refresh")
    public DangNhapResponse lamMoi(@CookieValue(name = TEN_COOKIE, required = false) String refreshToken) {
        return xacThucService.lamMoi(refreshToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> dangXuat(@CookieValue(name = TEN_COOKIE, required = false) String refreshToken) {
        xacThucService.dangXuat(refreshToken);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, taoCookie("", 0).toString())
                .build();
    }

    @PostMapping("/doi-mat-khau")
    public ResponseEntity<Void> doiMatKhau(@AuthenticationPrincipal NguoiDungDangNhap nd,
                                           @Valid @RequestBody DoiMatKhauRequest req) {
        xacThucService.doiMatKhau(nd.nguoiDungId(), req.matKhauCu(), req.matKhauMoi());
        return ResponseEntity.noContent().build();
    }

    // Refresh token để trong cookie HttpOnly, SameSite=Strict (mục 3.13.3)
    private ResponseCookie taoCookie(String giaTri, long soGiay) {
        return ResponseCookie.from(TEN_COOKIE, giaTri)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(soGiay)
                .build();
    }
}
