package vn.nhom.thuvien.exception;

import org.springframework.http.HttpStatus;

// Lỗi nghiệp vụ: vượt hạn mức, thẻ bị khóa, ... Mặc định trả mã 422
public class LoiNghiepVu extends RuntimeException {

    private final String code;
    private final HttpStatus status;

    public LoiNghiepVu(String code, String message) {
        this(code, message, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    public LoiNghiepVu(String code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public static LoiNghiepVu khongTimThay(String message) {
        return new LoiNghiepVu("KHONG_TIM_THAY", message, HttpStatus.NOT_FOUND);
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
