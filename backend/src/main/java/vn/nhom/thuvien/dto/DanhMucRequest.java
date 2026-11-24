package vn.nhom.thuvien.dto;

import jakarta.validation.constraints.NotBlank;

public record DanhMucRequest(@NotBlank(message = "Chưa nhập tên") String ten, String phu) {
}
