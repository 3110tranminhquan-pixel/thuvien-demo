package vn.nhom.thuvien.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom.thuvien.dto.ChuyenDoi;
import vn.nhom.thuvien.dto.DatTruocDto;
import vn.nhom.thuvien.entity.BanDoc;
import vn.nhom.thuvien.entity.BanSach;
import vn.nhom.thuvien.entity.DatTruoc;
import vn.nhom.thuvien.entity.DauSach;
import vn.nhom.thuvien.entity.enums.TrangThaiBanSach;
import vn.nhom.thuvien.entity.enums.TrangThaiDatTruoc;
import vn.nhom.thuvien.entity.enums.TrangThaiDauSach;
import vn.nhom.thuvien.entity.enums.TrangThaiThe;
import vn.nhom.thuvien.exception.LoiNghiepVu;
import vn.nhom.thuvien.exception.LoiQuyen;
import vn.nhom.thuvien.repository.BanDocRepository;
import vn.nhom.thuvien.repository.DatTruocRepository;
import vn.nhom.thuvien.repository.DauSachRepository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DatTruocService {

    private final DatTruocRepository datTruocRepo;
    private final DauSachRepository dauSachRepo;
    private final BanDocRepository banDocRepo;
    private final ThamSoService thamSo;
    private final ApplicationEventPublisher suKien;

    public DatTruocService(DatTruocRepository datTruocRepo, DauSachRepository dauSachRepo,
                           BanDocRepository banDocRepo, ThamSoService thamSo, ApplicationEventPublisher suKien) {
        this.datTruocRepo = datTruocRepo;
        this.dauSachRepo = dauSachRepo;
        this.banDocRepo = banDocRepo;
        this.thamSo = thamSo;
        this.suKien = suKien;
    }

    // BR-11
    @Transactional
    public DatTruocDto datTruoc(Long banDocId, Long dauSachId) {
        BanDoc bd = banDocRepo.findById(banDocId)
                .orElseThrow(() -> LoiNghiepVu.khongTimThay("Không tìm thấy bạn đọc"));
        DauSach ds = dauSachRepo.findById(dauSachId)
                .orElseThrow(() -> LoiNghiepVu.khongTimThay("Không tìm thấy đầu sách"));

        if (bd.getTrangThai() != TrangThaiThe.HOAT_DONG)
            throw new LoiNghiepVu("THE_KHONG_HOAT_DONG", "Thẻ không ở trạng thái hoạt động nên không đặt trước được");
        if (ds.getTrangThai() != TrangThaiDauSach.DANG_DUNG || !ds.choMuonVe())
            throw new LoiNghiepVu("KHONG_DAT_DUOC", "Đầu sách này không cho mượn về nên không đặt trước được");
        if (ds.soBanSanSang() > 0)
            throw new LoiNghiepVu("CON_SACH", "Đầu sách vẫn còn bản sẵn sàng, bạn có thể đến mượn trực tiếp");
        if (ds.soBanHienCo() == 0)
            throw new LoiNghiepVu("CHUA_CO_BAN", "Đầu sách chưa có bản nào trong kho");
        if (bd.dangMuonDauSach(dauSachId))
            throw new LoiNghiepVu("DANG_MUON", "Bạn đang mượn đầu sách này");
        if (datTruocRepo.existsByBanDocIdAndDauSachIdAndTrangThaiIn(banDocId, dauSachId, DatTruocRepository.CON_HIEU_LUC))
            throw new LoiNghiepVu("DA_DAT", "Bạn đã đặt trước đầu sách này");
        int toiDa = thamSo.laySo("SO_DAT_TRUOC_TOI_DA").intValue();
        if (datTruocRepo.countByBanDocIdAndTrangThaiIn(banDocId, DatTruocRepository.CON_HIEU_LUC) >= toiDa)
            throw new LoiNghiepVu("VUOT_SO_DAT_TRUOC", "Mỗi bạn đọc chỉ được đặt trước tối đa " + toiDa + " đầu sách");

        DatTruoc dt = new DatTruoc();
        dt.setBanDoc(bd);
        dt.setDauSach(ds);
        datTruocRepo.save(dt);
        return ChuyenDoi.datTruoc(dt, thuTu(dt));
    }

    // FR-22: bạn đọc hủy lượt đặt của mình
    @Transactional
    public void huy(Long datTruocId, Long banDocYeuCauId) {
        DatTruoc dt = datTruocRepo.findById(datTruocId)
                .orElseThrow(() -> LoiNghiepVu.khongTimThay("Không tìm thấy lượt đặt trước"));
        if (!dt.getBanDoc().getId().equals(banDocYeuCauId))
            throw new LoiQuyen("Bạn không thể hủy lượt đặt trước của người khác");
        if (!dt.conHieuLuc())
            throw new LoiNghiepVu("DA_KET_THUC", "Lượt đặt trước này đã kết thúc");
        huyVaChuyenSach(dt);
    }

    // BR-12: giữ 48 giờ; hạn giữ rơi vào Chủ nhật (Thư viện nghỉ) thì cộng thêm 24 giờ
    public LocalDateTime tinhHanGiu(LocalDateTime lucThongBao) {
        long soGio = thamSo.laySo("SO_GIO_GIU").longValue();
        LocalDateTime han = lucThongBao.plusHours(soGio);
        if (han.getDayOfWeek() == DayOfWeek.SUNDAY) {
            han = han.plusHours(24);
        }
        return han;
    }

    // FR-23: bản sách vừa về thì giữ cho người đứng đầu hàng chờ.
    // Trả về lượt được giữ, hoặc rỗng nếu không còn ai chờ (người gọi tự chuyển sách về Sẵn sàng).
    @Transactional
    public Optional<DatTruoc> giuChoNguoiKeTiep(BanSach bs) {
        Optional<DatTruoc> dauHang = datTruocRepo.nguoiDauHang(bs.getDauSach().getId());
        if (dauHang.isEmpty()) {
            return Optional.empty();
        }
        DatTruoc dt = dauHang.get();
        if (bs.getTrangThai() != TrangThaiBanSach.DANG_GIU) {
            bs.chuyenTrangThai(TrangThaiBanSach.DANG_GIU);
        }
        dt.giuSach(bs, tinhHanGiu(LocalDateTime.now()));
        BanDoc bd = dt.getBanDoc();
        suKien.publishEvent(new SuKienGiuSach(dt.getId(), bd.getEmail(), bd.getHoTen(),
                bs.getDauSach().getNhanDe(), bs.getMaVach(), dt.getHanGiu()));
        return Optional.of(dt);
    }

    // FR-24: chạy mỗi giờ, hủy các lượt giữ đã quá hạn và chuyển sách cho người kế tiếp
    @Transactional
    public int xuLyHetHanGiu() {
        List<DatTruoc> ds = datTruocRepo.findByTrangThaiAndHanGiuBefore(TrangThaiDatTruoc.DANG_GIU, LocalDateTime.now());
        for (DatTruoc dt : ds) {
            huyVaChuyenSach(dt);
        }
        return ds.size();
    }

    // BR-14: thẻ bị khóa thì hủy mọi lượt đặt trước còn hiệu lực
    @Transactional
    public void huyTatCaCuaBanDoc(Long banDocId) {
        for (DatTruoc dt : datTruocRepo.findByBanDocIdAndTrangThaiIn(banDocId, DatTruocRepository.CON_HIEU_LUC)) {
            huyVaChuyenSach(dt);
        }
    }

    @Transactional(readOnly = true)
    public List<DatTruocDto> dsCuaBanDoc(Long banDocId) {
        return datTruocRepo.findByBanDocIdOrderByThoiDiemDatDesc(banDocId).stream()
                .map(dt -> ChuyenDoi.datTruoc(dt, thuTu(dt))).toList();
    }

    // Thuộc tính dẫn xuất /thuTu: tính từ thời điểm đặt, không lưu cột riêng
    private Long thuTu(DatTruoc dt) {
        if (dt.getTrangThai() != TrangThaiDatTruoc.DANG_CHO) {
            return null;
        }
        return datTruocRepo.countByDauSachIdAndTrangThaiAndThoiDiemDatBefore(
                dt.getDauSach().getId(), TrangThaiDatTruoc.DANG_CHO, dt.getThoiDiemDat()) + 1;
    }

    private void huyVaChuyenSach(DatTruoc dt) {
        boolean dangGiu = dt.getTrangThai() == TrangThaiDatTruoc.DANG_GIU;
        dt.huy();
        if (dangGiu && dt.getBanSach() != null) {
            BanSach bs = dt.getBanSach();
            if (giuChoNguoiKeTiep(bs).isEmpty()) {
                bs.chuyenTrangThai(TrangThaiBanSach.SAN_SANG);
            }
        }
    }
}
