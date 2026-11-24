package vn.nhom.thuvien.dto;

import jakarta.validation.constraints.NotBlank;

public record LyDoRequest(@NotBlank(message = "Phải nhập lý do") String lyDo) {
}
