package vn.nhom.thuvien.entity.enums;

import java.util.EnumSet;
import java.util.Set;

public enum TrangThaiBanSach {
    SAN_SANG("Sẵn sàng"),
    DANG_MUON("Đang mượn"),
    QUA_HAN("Quá hạn"),
    DANG_GIU("Đang giữ"),
    DANG_SUA("Đang sửa"),
    MAT("Mất"),
    THANH_LY("Thanh lý");

    private final String ten;

    TrangThaiBanSach(String ten) {
        this.ten = ten;
    }

    public String getTen() {
        return ten;
    }

    // Các chuyển đổi được phép, đúng theo sơ đồ trạng thái bản sách (mục 3.8.2).
    // Không có SAN_SANG -> DANG_GIU vì chỉ đặt trước được khi không còn bản nào sẵn sàng (BR-11).
    public Set<TrangThaiBanSach> trangThaiKeTiep() {
        switch (this) {
            case SAN_SANG:
                return EnumSet.of(DANG_MUON, DANG_SUA);
            case DANG_MUON:
                return EnumSet.of(QUA_HAN, SAN_SANG, DANG_GIU, DANG_SUA, MAT);
            case QUA_HAN:
                return EnumSet.of(SAN_SANG, DANG_GIU, DANG_SUA, MAT);
            case DANG_GIU:
                return EnumSet.of(DANG_MUON, SAN_SANG);
            case DANG_SUA:
                return EnumSet.of(SAN_SANG, THANH_LY);
            default:
                // MAT và THANH_LY là trạng thái kết thúc
                return EnumSet.noneOf(TrangThaiBanSach.class);
        }
    }

    public boolean coTheChuyenSang(TrangThaiBanSach moi) {
        return trangThaiKeTiep().contains(moi);
    }
}
