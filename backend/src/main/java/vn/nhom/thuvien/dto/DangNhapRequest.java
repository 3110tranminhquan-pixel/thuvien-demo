package vn.nhom.thuvien.dto;

import jakarta.validation.constraints.NotBlank;

public record DangNhapRequest(
        @NotBlank(message = "Chưa nhập tên đăng nhập") String tenDangNhap,
        @NotBlank(message = "Chưa nhập mật khẩu") String matKhau) {
}
