package vn.nhom.thuvien.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import vn.nhom.thuvien.entity.BanDoc;
import vn.nhom.thuvien.entity.ChiTietMuon;
import vn.nhom.thuvien.repository.ChiTietMuonRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

// Gửi email. Khi phát triển, thư đi vào MailHog (http://localhost:8025) chứ không gửi thật.
// Không có máy chủ email thì chỉ ghi log, các chức năng mượn trả vẫn chạy bình thường.
@Service
public class ThongBaoService {

    private static final Logger log = LoggerFactory.getLogger(ThongBaoService.class);
    private static final DateTimeFormatter NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter GIO = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    private final JavaMailSender mailSender;
    private final ChiTietMuonRepository chiTietRepo;
    private final ThamSoService thamSo;
    private final String nguoiGui;

    public ThongBaoService(JavaMailSender mailSender, ChiTietMuonRepository chiTietRepo, ThamSoService thamSo,
                           @Value("${thuvien.email-gui}") String nguoiGui) {
        this.mailSender = mailSender;
        this.chiTietRepo = chiTietRepo;
        this.thamSo = thamSo;
        this.nguoiGui = nguoiGui;
    }

    // Chỉ chạy khi giao tác tạo phiếu đã lưu thành công
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void khiDaMuon(SuKienDaMuon sk) {
        StringBuilder nd = new StringBuilder("Chào " + sk.hoTen() + ",\n\nThư viện xác nhận bạn vừa mượn:\n");
        for (String dong : sk.dsSach()) {
            nd.append("  - ").append(dong).append('\n');
        }
        nd.append("\nVui lòng trả sách đúng hạn. Bạn có thể gia hạn trên trang Tài khoản của tôi.\n");
        guiThu(sk.email(), "[Thư viện] Xác nhận mượn sách", nd.toString());
    }

    // FR-29: báo sách đặt trước đã về
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void khiGiuSach(SuKienGiuSach sk) {
        String nd = "Chào " + sk.hoTen() + ",\n\nSách bạn đặt trước đã có: \"" + sk.nhanDe() + "\" (mã "
                + sk.maVach() + ").\nThư viện giữ sách cho bạn đến " + sk.hanGiu().format(GIO)
                + ". Quá thời hạn này lượt đặt sẽ tự hủy.\n";
        guiThu(sk.email(), "[Thư viện] Sách đặt trước đã sẵn sàng", nd);
    }

    public void guiMatKhauBanDau(BanDoc bd, String matKhau) {
        String nd = "Chào " + bd.getHoTen() + ",\n\nThẻ thư viện của bạn đã được cấp.\nTên đăng nhập: "
                + bd.getMaThe() + "\nMật khẩu ban đầu: " + matKhau + "\n\nVui lòng đổi mật khẩu sau lần đăng nhập đầu tiên.\n";
        guiThu(bd.getEmail(), "[Thư viện] Thông tin tài khoản", nd);
    }

    // FR-28: nhắc trước hạn trả
    @Transactional(readOnly = true)
    public int nhacTruocHan() {
        int soNgay = thamSo.laySo("SO_NGAY_NHAC_TRUOC").intValue();
        LocalDate ngayHan = LocalDate.now().plusDays(soNgay);
        List<ChiTietMuon> ds = chiTietRepo.findByNgayTraIsNullAndHanTra(ngayHan);
        for (ChiTietMuon ct : ds) {
            BanDoc bd = ct.getPhieuMuon().getBanDoc();
            guiThu(bd.getEmail(), "[Thư viện] Sắp đến hạn trả sách",
                    "Chào " + bd.getHoTen() + ",\n\nSách \"" + ct.getBanSach().getDauSach().getNhanDe()
                            + "\" sẽ đến hạn trả vào ngày " + ct.getHanTra().format(NGAY) + ".\n");
        }
        return ds.size();
    }

    // FR-28: báo quá hạn ngày đầu tiên, sau đó nhắc lại mỗi 7 ngày
    @Transactional(readOnly = true)
    public int baoQuaHan() {
        int chuKy = thamSo.laySo("CHU_KY_NHAC_QUA_HAN").intValue();
        LocalDate homNay = LocalDate.now();
        int dem = 0;
        for (ChiTietMuon ct : chiTietRepo.findByNgayTraIsNullAndHanTraBeforeOrderByHanTraAsc(homNay)) {
            int tre = ct.soNgayTre(homNay);
            if (tre == 1 || (chuKy > 0 && (tre - 1) % chuKy == 0)) {
                BanDoc bd = ct.getPhieuMuon().getBanDoc();
                guiThu(bd.getEmail(), "[Thư viện] Sách đã quá hạn",
                        "Chào " + bd.getHoTen() + ",\n\nSách \"" + ct.getBanSach().getDauSach().getNhanDe()
                                + "\" đã quá hạn " + tre + " ngày (hạn trả " + ct.getHanTra().format(NGAY)
                                + "). Vui lòng mang sách đến trả sớm để tránh bị khóa thẻ.\n");
                dem++;
            }
        }
        return dem;
    }

    private void guiThu(String den, String tieuDe, String noiDung) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(nguoiGui);
            msg.setTo(den);
            msg.setSubject(tieuDe);
            msg.setText(noiDung);
            mailSender.send(msg);
        } catch (Exception e) {
            log.warn("Không gửi được email tới {}: {}", den, e.getMessage());
        }
    }
}
