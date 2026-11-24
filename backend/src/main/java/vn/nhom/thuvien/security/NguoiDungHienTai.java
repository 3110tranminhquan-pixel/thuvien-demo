package vn.nhom.thuvien.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

// Lấy người đang gọi API từ SecurityContext (đã được BoLocJwt đặt vào)
public final class NguoiDungHienTai {

    private NguoiDungHienTai() {
    }

    public static NguoiDungDangNhap layHoacNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof NguoiDungDangNhap nd) {
            return nd;
        }
        return null;
    }
}
