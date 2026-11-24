package vn.nhom.thuvien.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TaoPhieuMuonRequest(
        @NotNull(message = "Chưa quét thẻ bạn đọc") Long banDocId,
        @NotEmpty(message = "Chưa quét cuốn sách nào") List<String> dsMaVach) {
}
