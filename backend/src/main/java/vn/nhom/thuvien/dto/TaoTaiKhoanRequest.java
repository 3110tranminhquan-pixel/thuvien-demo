package vn.nhom.thuvien.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaoTaiKhoanRequest(
        @NotBlank(message = "Chưa nhập tên đăng nhập") String tenDangNhap,
        @NotBlank(message = "Chưa nhập mật khẩu") String matKhau,
        @NotNull(message = "Chưa chọn vai trò") Integer vaiTroId) {
}
