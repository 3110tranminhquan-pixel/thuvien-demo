package vn.nhom.thuvien.exception;

// Lỗi sai quyền sở hữu dữ liệu, trả mã 403
public class LoiQuyen extends RuntimeException {

    public LoiQuyen(String message) {
        super(message);
    }
}
