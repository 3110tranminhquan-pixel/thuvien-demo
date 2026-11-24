package vn.nhom.thuvien.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.nhom.thuvien.service.BanDocService;
import vn.nhom.thuvien.service.DatTruocService;
import vn.nhom.thuvien.service.MuonTraService;
import vn.nhom.thuvien.service.ThongBaoService;

@Component
public class LichTacVu {

    private static final Logger log = LoggerFactory.getLogger(LichTacVu.class);

    private final MuonTraService muonTraService;
    private final DatTruocService datTruocService;
    private final BanDocService banDocService;
    private final ThongBaoService thongBaoService;

    public LichTacVu(MuonTraService muonTraService, DatTruocService datTruocService,
                     BanDocService banDocService, ThongBaoService thongBaoService) {
        this.muonTraService = muonTraService;
        this.datTruocService = datTruocService;
        this.banDocService = banDocService;
        this.thongBaoService = thongBaoService;
    }

    // 0 giờ mỗi ngày: chuyển sách quá hạn, xét khóa thẻ
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Ho_Chi_Minh")
    public void chayLuc0Gio() {
        int soQuaHan = muonTraService.danhDauQuaHan();        // Đang mượn -> Quá hạn
        int soThe = banDocService.xetKhoaTheTuDong();         // FR-27, BR-14, BR-15
        log.info("Tác vụ 0 giờ: {} bản sách chuyển Quá hạn, {} thẻ đổi trạng thái", soQuaHan, soThe);
    }

    // Phút thứ 5 của mỗi giờ: hủy các lượt giữ sách đã hết hạn
    @Scheduled(cron = "0 5 * * * *", zone = "Asia/Ho_Chi_Minh")
    public void chayMoiGio() {
        int so = datTruocService.xuLyHetHanGiu();             // FR-24, BR-12
        if (so > 0) log.info("Đã hủy {} lượt giữ sách quá hạn", so);
    }

    // 7 giờ sáng mỗi ngày: gửi email nhắc hạn
    @Scheduled(cron = "0 0 7 * * *", zone = "Asia/Ho_Chi_Minh")
    public void guiNhacHan() {
        int a = thongBaoService.nhacTruocHan();               // FR-28: trước hạn 2 ngày
        int b = thongBaoService.baoQuaHan();                  // FR-28: quá hạn, nhắc lại mỗi 7 ngày
        log.info("Đã gửi {} email nhắc trước hạn, {} email báo quá hạn", a, b);
    }

    // Máy demo thường không bật lúc 0 giờ, nên khởi động xong cũng chạy bù một lần
    @EventListener(ApplicationReadyEvent.class)
    public void chayKhiKhoiDong() {
        chayLuc0Gio();
        chayMoiGio();
    }
}
