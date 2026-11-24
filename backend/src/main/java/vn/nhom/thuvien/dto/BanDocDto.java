package vn.nhom.thuvien.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BanDocDto(Long id, String maThe, String hoTen, LocalDate ngaySinh, String email, String soDienThoai,
                        String donVi, LocalDate ngayCap, LocalDate ngayHetHan, String trangThai, String lyDoKhoa,
                        boolean khoaThuCong, Integer loaiId, String tenLoai,
                        int soDangMuon, int soToiDa, BigDecimal tongNo,
                        boolean duocMuon, String lyDoKhongDuocMuon) {
}
