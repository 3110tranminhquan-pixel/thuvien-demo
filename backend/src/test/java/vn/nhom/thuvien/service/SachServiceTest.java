package vn.nhom.thuvien.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

// FR-10: kiểm tra ISBN-13
class SachServiceTest {

    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
        "9786040000217, true",
        "9786040000218, false",     // sai số kiểm tra
        "978604000021, false",      // thiếu một chữ số
        "97860400002AB, false"
    })
    void kiemTraIsbn13(String isbn, boolean hopLe) {
        assertEquals(hopLe, SachService.isbn13HopLe(isbn));
    }
}
