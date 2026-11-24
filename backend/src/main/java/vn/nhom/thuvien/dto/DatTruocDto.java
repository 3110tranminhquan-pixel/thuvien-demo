package vn.nhom.thuvien.dto;

import java.time.LocalDateTime;

public record DatTruocDto(Long id, Long dauSachId, String nhanDe, LocalDateTime thoiDiemDat, String trangThai,
                          LocalDateTime hanGiu, String maVachGiu, Long thuTu) {
}
