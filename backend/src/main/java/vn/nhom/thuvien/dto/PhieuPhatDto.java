package vn.nhom.thuvien.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PhieuPhatDto(Long id, Long chiTietId, String maThe, String hoTen, String nhanDe, String maVach,
                           String loaiViPham, BigDecimal soTien, String trangThai, LocalDateTime ngayLap,
                           String nguoiLap, LocalDateTime ngayThu, String nguoiThu, String lyDoHuy) {
}
