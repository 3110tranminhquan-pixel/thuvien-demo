package vn.nhom.thuvien.service.phat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.nhom.thuvien.entity.BanSach;
import vn.nhom.thuvien.entity.ChiTietMuon;
import vn.nhom.thuvien.entity.DauSach;
import vn.nhom.thuvien.service.ThamSoService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PhatTreHanTest {

    @Mock ThamSoService thamSo;
    @InjectMocks PhatTreHan phatTreHan;

    static final LocalDate HAN_TRA = LocalDate.of(2026, 11, 20);

    @BeforeEach
    void caiDat() {
        when(thamSo.laySo("PHAT_TRE_MOI_NGAY")).thenReturn(new BigDecimal("5000"));
        when(thamSo.laySo("TY_LE_DEN_MAT")).thenReturn(new BigDecimal("2.0"));
    }

    private ChiTietMuon taoChiTiet(String giaBia) {
        DauSach ds = new DauSach();
        ds.setGiaBia(new BigDecimal(giaBia));
        BanSach bs = new BanSach();
        bs.setDauSach(ds);
        ChiTietMuon ct = new ChiTietMuon();
        ct.setBanSach(bs);
        ct.setHanTra(HAN_TRA);
        return ct;
    }

    @ParameterizedTest(name = "Trễ {0} ngày, giá bìa 40.000 -> phạt {1}")
    @CsvSource({
        "0,  0",        // trả đúng hạn
        "1,  5000",     // biên dưới
        "10, 50000",
        "11, 55000",
        "16, 80000",    // vừa chạm trần 200% giá bìa
        "17, 80000"     // vượt trần -> bị chặn
    })
    void tinhPhatTheoSoNgayTre(int soNgayTre, String tienMongDoi) {
        ChiTietMuon ct = taoChiTiet("40000");
        BigDecimal ketQua = phatTreHan.tinh(ct, HAN_TRA.plusDays(soNgayTre));
        assertEquals(0, new BigDecimal(tienMongDoi).compareTo(ketQua),
                     "Sai tiền phạt khi trễ " + soNgayTre + " ngày");
    }

    @Test
    void traTruocHanKhongBiPhat() {
        ChiTietMuon ct = taoChiTiet("40000");
        assertEquals(0, BigDecimal.ZERO.compareTo(phatTreHan.tinh(ct, HAN_TRA.minusDays(3))));
    }
}
