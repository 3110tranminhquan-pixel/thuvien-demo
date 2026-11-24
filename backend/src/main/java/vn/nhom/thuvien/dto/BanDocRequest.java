package vn.nhom.thuvien.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BanDocRequest(
        @NotBlank(message = "Chưa nhập họ tên") String hoTen,
        LocalDate ngaySinh,
        @NotBlank(message = "Chưa nhập email") @Email(message = "Email không hợp lệ") String email,
        String soDienThoai,
        String donVi,
        @NotNull(message = "Chưa nhập ngày hết hạn thẻ") LocalDate ngayHetHan,
        @NotNull(message = "Chưa chọn loại bạn đọc") Integer loaiId) {
}
