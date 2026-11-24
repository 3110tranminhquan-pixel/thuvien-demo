package vn.nhom.thuvien.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record DauSachRequest(
        @NotBlank(message = "Chưa nhập ISBN") String isbn,
        @NotBlank(message = "Chưa nhập nhan đề") String nhanDe,
        @Min(value = 1900, message = "Năm xuất bản không hợp lệ") @Max(value = 2100, message = "Năm xuất bản không hợp lệ") Integer namXb,
        Integer lanXb,
        Integer soTrang,
        @NotNull(message = "Chưa nhập giá bìa") @Positive(message = "Giá bìa phải lớn hơn 0") BigDecimal giaBia,
        String maDdc,
        @NotBlank(message = "Chưa chọn loại tài liệu") String loaiTaiLieu,
        @NotNull(message = "Chưa chọn thể loại") Integer theLoaiId,
        @NotNull(message = "Chưa chọn nhà xuất bản") Integer nxbId,
        @NotEmpty(message = "Phải có ít nhất một tác giả") List<Integer> dsTacGiaId) {
}
