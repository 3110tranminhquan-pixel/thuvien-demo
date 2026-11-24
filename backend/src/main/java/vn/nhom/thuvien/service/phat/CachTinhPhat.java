package vn.nhom.thuvien.service.phat;

import vn.nhom.thuvien.entity.ChiTietMuon;

import java.math.BigDecimal;
import java.time.LocalDate;

// Mẫu Strategy (mục 3.9.4): mỗi loại vi phạm một cách tính
public interface CachTinhPhat {
    BigDecimal tinh(ChiTietMuon ct, LocalDate ngayXet);
}
