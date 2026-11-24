package vn.nhom.thuvien.dto;

// Dùng chung cho thể loại (phu = mã DDC), nhà xuất bản (phu = địa chỉ), tác giả (phu bỏ trống)
public record DanhMucDto(Integer id, String ten, String phu) {
}
