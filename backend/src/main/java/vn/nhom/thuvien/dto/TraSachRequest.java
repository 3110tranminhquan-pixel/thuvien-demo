package vn.nhom.thuvien.dto;

import jakarta.validation.constraints.NotBlank;

public record TraSachRequest(
        @NotBlank(message = "Chưa quét mã vạch") String maVach,
        @NotBlank(message = "Phải chọn tình trạng sách khi nhận trả") String tinhTrang) {
}
