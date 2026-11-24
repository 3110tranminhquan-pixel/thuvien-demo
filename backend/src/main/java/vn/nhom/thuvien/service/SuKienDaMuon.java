package vn.nhom.thuvien.service;

import java.util.List;

// Phát ra sau khi tạo phiếu mượn, ThongBaoService nghe và gửi email xác nhận (mẫu Observer)
public record SuKienDaMuon(Long phieuMuonId, String email, String hoTen, List<String> dsSach) {
}
