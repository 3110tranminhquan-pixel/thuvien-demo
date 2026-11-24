package vn.nhom.thuvien.dto;

import jakarta.validation.constraints.NotNull;

public record DatTruocRequest(@NotNull(message = "Chưa chọn đầu sách") Long dauSachId) {
}
