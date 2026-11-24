package vn.nhom.thuvien.service;

import java.time.LocalDateTime;

// Phát ra khi một bản sách được giữ cho người đặt trước (FR-29)
public record SuKienGiuSach(Long datTruocId, String email, String hoTen, String nhanDe, String maVach,
                            LocalDateTime hanGiu) {
}
