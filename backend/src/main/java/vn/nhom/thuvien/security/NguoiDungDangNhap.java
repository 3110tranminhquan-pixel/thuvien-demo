package vn.nhom.thuvien.security;

// Thông tin người gọi API, đọc ra từ access token
public record NguoiDungDangNhap(Long nguoiDungId, String tenDangNhap, String vaiTro, Long banDocId) {

    public boolean laBanDoc() {
        return "BAN_DOC".equals(vaiTro);
    }
}
