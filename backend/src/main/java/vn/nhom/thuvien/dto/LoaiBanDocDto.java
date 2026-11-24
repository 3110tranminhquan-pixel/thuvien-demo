package vn.nhom.thuvien.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LoaiBanDocDto(
        Integer id,
        String tenLoai,
        @NotNull(message = "Thiếu số sách tối đa") @Min(value = 1, message = "Số sách tối đa phải lớn hơn 0") Integer soSachToiDa,
        @NotNull(message = "Thiếu số ngày mượn") @Min(value = 1, message = "Số ngày mượn phải lớn hơn 0") Integer soNgayMuon,
        @NotNull(message = "Thiếu số lần gia hạn") @Min(value = 0, message = "Số lần gia hạn không được âm") Integer soLanGiaHan,
        @NotNull(message = "Thiếu số ngày gia hạn") @Min(value = 1, message = "Số ngày gia hạn phải lớn hơn 0") Integer soNgayGiaHan) {
}
