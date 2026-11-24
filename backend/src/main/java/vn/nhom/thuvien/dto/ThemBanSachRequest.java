package vn.nhom.thuvien.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ThemBanSachRequest(
        @Min(value = 1, message = "Số lượng tối thiểu là 1") @Max(value = 50, message = "Mỗi lần thêm tối đa 50 bản") int soLuong,
        String viTriKe) {
}
