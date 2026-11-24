package vn.nhom.thuvien.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

// BR-12: hạn giữ 48 giờ, rơi vào Chủ nhật thì cộng thêm 24 giờ
@ExtendWith(MockitoExtension.class)
class DatTruocServiceTest {

    @Mock ThamSoService thamSo;
    @InjectMocks DatTruocService datTruocService;

    @BeforeEach
    void caiDat() {
        when(thamSo.laySo("SO_GIO_GIU")).thenReturn(new BigDecimal("48"));
    }

    @Test
    void hanGiuBinhThuong() {
        LocalDateTime thu2 = LocalDateTime.of(2026, 11, 23, 9, 0);       // thứ Hai
        assertEquals(LocalDateTime.of(2026, 11, 25, 9, 0), datTruocService.tinhHanGiu(thu2));
    }

    @Test
    void hanGiuRoiVaoChuNhatDuocCongThem24Gio() {
        LocalDateTime thu6 = LocalDateTime.of(2026, 11, 20, 10, 0);      // thứ Sáu + 48h = Chủ nhật
        assertEquals(LocalDateTime.of(2026, 11, 23, 10, 0), datTruocService.tinhHanGiu(thu6));
    }
}
