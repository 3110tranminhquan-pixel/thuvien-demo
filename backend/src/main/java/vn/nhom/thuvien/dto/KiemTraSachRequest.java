package vn.nhom.thuvien.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record KiemTraSachRequest(
        @NotNull(message = "Chưa quét thẻ bạn đọc") Long banDocId,
        @NotBlank(message = "Chưa quét mã vạch") String maVach,
        List<String> dsMaVachDaQuet) {
}
