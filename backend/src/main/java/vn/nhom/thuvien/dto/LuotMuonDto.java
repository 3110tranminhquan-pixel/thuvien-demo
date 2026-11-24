package vn.nhom.thuvien.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LuotMuonDto(Long chiTietId, Long phieuMuonId, String maVach, Long dauSachId, String nhanDe,
                          LocalDateTime ngayMuon, LocalDate hanTra, LocalDateTime ngayTra, int soLanGiaHan,
                          int soNgayTre, String tinhTrangKhiTra, String maThe, String hoTen) {
}
