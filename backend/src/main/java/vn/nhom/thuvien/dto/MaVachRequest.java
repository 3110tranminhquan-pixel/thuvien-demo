package vn.nhom.thuvien.dto;

import jakarta.validation.constraints.NotBlank;

public record MaVachRequest(@NotBlank(message = "Chưa nhập mã vạch") String maVach) {
}
