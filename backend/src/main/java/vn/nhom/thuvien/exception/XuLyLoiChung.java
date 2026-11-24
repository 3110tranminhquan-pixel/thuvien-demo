package vn.nhom.thuvien.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

// Mọi lỗi đều trả về cùng một dạng {"code": ..., "message": ...} (NFR-11)
@RestControllerAdvice
public class XuLyLoiChung {

    private static final Logger log = LoggerFactory.getLogger(XuLyLoiChung.class);

    private ResponseEntity<Map<String, String>> traVe(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(Map.of("code", code, "message", message));
    }

    @ExceptionHandler(LoiNghiepVu.class)
    public ResponseEntity<Map<String, String>> loiNghiepVu(LoiNghiepVu e) {
        return traVe(e.getStatus(), e.getCode(), e.getMessage());
    }

    @ExceptionHandler(LoiQuyen.class)
    public ResponseEntity<Map<String, String>> loiQuyen(LoiQuyen e) {
        return traVe(HttpStatus.FORBIDDEN, "KHONG_CO_QUYEN", e.getMessage());
    }

    // Sai vai trò khi gọi API có @PreAuthorize
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> saiVaiTro(AccessDeniedException e) {
        return traVe(HttpStatus.FORBIDDEN, "KHONG_CO_QUYEN", "Bạn không có quyền thực hiện thao tác này");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> duLieuSai(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getDefaultMessage())
                .findFirst().orElse("Dữ liệu không hợp lệ");
        return traVe(HttpStatus.BAD_REQUEST, "DU_LIEU_KHONG_HOP_LE", msg);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, String>> saiDinhDang(Exception e) {
        return traVe(HttpStatus.BAD_REQUEST, "DU_LIEU_KHONG_HOP_LE", "Dữ liệu gửi lên không đúng định dạng");
    }

    // Ví dụ trùng ISBN, trùng email, hoặc xóa danh mục đang được dùng
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> viPhamRangBuoc(DataIntegrityViolationException e) {
        log.warn("Vi phạm ràng buộc dữ liệu: {}", e.getMostSpecificCause().getMessage());
        return traVe(HttpStatus.CONFLICT, "VI_PHAM_RANG_BUOC",
                "Dữ liệu bị trùng hoặc đang được sử dụng ở nơi khác");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> loiKhac(Exception e) {
        // Các lỗi chuẩn của Spring MVC (404 sai đường dẫn, 405 sai phương thức...) giữ nguyên mã của chúng
        if (e instanceof ErrorResponse er) {
            return ResponseEntity.status(er.getStatusCode())
                    .body(Map.of("code", "LOI_YEU_CAU", "message", "Yêu cầu không hợp lệ: " + er.getStatusCode()));
        }
        log.error("Lỗi không mong đợi", e);
        return traVe(HttpStatus.INTERNAL_SERVER_ERROR, "LOI_HE_THONG", "Có lỗi xảy ra, vui lòng thử lại");
    }
}
