package vn.nhom.thuvien.dto;

import java.math.BigDecimal;
import java.util.List;

public record DauSachChiTiet(Long id, String isbn, String nhanDe, String tacGia, List<Integer> dsTacGiaId,
                             Integer namXb, Integer lanXb, Integer soTrang, BigDecimal giaBia, String maDdc,
                             String loaiTaiLieu, String trangThai,
                             Integer theLoaiId, String theLoai, Integer nxbId, String nhaXuatBan,
                             long soSanSang, long tongSo, long soNguoiCho, boolean choMuonVe,
                             List<BanSachDto> dsBanSach) {
}
