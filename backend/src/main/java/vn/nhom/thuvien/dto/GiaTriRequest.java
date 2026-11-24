package vn.nhom.thuvien.dto;

import jakarta.validation.constraints.NotBlank;

public record GiaTriRequest(@NotBlank(message = "Chưa nhập giá trị") String giaTri) {
}
