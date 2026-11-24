package vn.nhom.thuvien.dto;

import jakarta.validation.constraints.NotBlank;

public record DoiMatKhauRequest(
        @NotBlank(message = "Chưa nhập mật khẩu hiện tại") String matKhauCu,
        @NotBlank(message = "Chưa nhập mật khẩu mới") String matKhauMoi) {
}
