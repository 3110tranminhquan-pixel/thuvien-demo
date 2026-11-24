package vn.nhom.thuvien.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record XemTruocTraDto(Long chiTietId, String maVach, String nhanDe, BigDecimal giaBia, String maThe,
                             String hoTen, LocalDateTime ngayMuon, LocalDate hanTra, int soNgayTre,
                             BigDecimal phatTreTamTinh, BigDecimal phatHongNhe, BigDecimal phatHongNang) {
}
