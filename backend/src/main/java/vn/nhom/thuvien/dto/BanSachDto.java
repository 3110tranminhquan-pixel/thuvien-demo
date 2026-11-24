package vn.nhom.thuvien.dto;

import java.time.LocalDate;

public record BanSachDto(Long id, String maVach, String viTriKe, String trangThai, String tenTrangThai,
                         LocalDate ngayNhap, String ghiChu) {
}
