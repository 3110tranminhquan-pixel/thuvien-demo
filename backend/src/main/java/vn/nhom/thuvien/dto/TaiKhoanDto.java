package vn.nhom.thuvien.dto;

import java.time.LocalDateTime;

public record TaiKhoanDto(Long id, String tenDangNhap, Integer vaiTroId, String vaiTro, String trangThai,
                          LocalDateTime khoaDen) {
}
