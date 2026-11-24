package vn.nhom.thuvien.dto;

import java.time.LocalDateTime;

public record NhatKyDto(Long id, LocalDateTime thoiGian, String nguoiDung, String hanhDong, String doiTuong,
                        String maDoiTuong, String diaChiIp) {
}
