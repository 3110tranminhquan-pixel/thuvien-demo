package vn.nhom.thuvien.dto;

import java.math.BigDecimal;

public record KetQuaTraDto(String maVach, String nhanDe, String maThe, String hoTen, int soNgayTre,
                           BigDecimal tienPhatTre, BigDecimal tienPhatHong, String trangThaiMoi,
                           String giuChoMaThe, String giuChoHoTen, String trangThaiThe) {
}
