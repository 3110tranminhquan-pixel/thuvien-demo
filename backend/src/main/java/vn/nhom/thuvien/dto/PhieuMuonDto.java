package vn.nhom.thuvien.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PhieuMuonDto(Long id, String maThe, String hoTen, LocalDateTime ngayMuon, List<LuotMuonDto> dsChiTiet) {
}
