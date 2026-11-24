package vn.nhom.thuvien.dto;

import java.time.LocalDate;

public record SachQuetDto(String maVach, Long dauSachId, String nhanDe, String viTriKe, LocalDate hanTraDuKien) {
}
