package vn.nhom.thuvien.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom.thuvien.dto.ChuyenDoi;
import vn.nhom.thuvien.dto.LuotMuonDto;
import vn.nhom.thuvien.entity.BanDoc;
import vn.nhom.thuvien.entity.ChiTietMuon;
import vn.nhom.thuvien.entity.KetQuaKiemTra;
import vn.nhom.thuvien.entity.enums.TrangThaiThe;
import vn.nhom.thuvien.exception.LoiNghiepVu;
import vn.nhom.thuvien.exception.LoiQuyen;
import vn.nhom.thuvien.repository.ChiTietMuonRepository;
import vn.nhom.thuvien.repository.DatTruocRepository;

import java.time.LocalDate;

@Service
public class GiaHanService {

    private final ChiTietMuonRepository chiTietRepo;
    private final DatTruocRepository datTruocRepo;

    public GiaHanService(ChiTietMuonRepository chiTietRepo, DatTruocRepository datTruocRepo) {
        this.chiTietRepo = chiTietRepo;
        this.datTruocRepo = datTruocRepo;
    }

    // banDocYeuCauId: có giá trị khi bạn đọc tự gia hạn trên web (lấy từ token), null khi thủ thư gia hạn tại quầy.
    // Thứ tự kiểm tra: quyền sở hữu, thẻ, quá hạn, số lần, người đặt trước.
    @Transactional
    public LuotMuonDto giaHan(Long chiTietId, Long banDocYeuCauId) {
        ChiTietMuon ct = chiTietRepo.findById(chiTietId)
            .orElseThrow(() -> new LoiNghiepVu("KHONG_TIM_THAY", "Không tìm thấy lượt mượn"));
        BanDoc bd = ct.getPhieuMuon().getBanDoc();

        if (banDocYeuCauId != null && !bd.getId().equals(banDocYeuCauId))   // NFR-08
            throw new LoiQuyen("Bạn không thể gia hạn sách của người khác");
        if (!ct.dangMuon())
            throw new LoiNghiepVu("DA_TRA", "Sách này đã được trả");
        if (bd.getTrangThai() == TrangThaiThe.BI_KHOA)                        // BR-10
            throw new LoiNghiepVu("THE_BI_KHOA", "Thẻ đang bị khóa: " + bd.getLyDoKhoa());

        KetQuaKiemTra kq = ct.kiemTraGiaHan(bd.getLoai(), LocalDate.now());   // BR-10
        if (!kq.isDat()) throw new LoiNghiepVu("KHONG_GIA_HAN_DUOC", kq.getLyDo());

        Long dauSachId = ct.getBanSach().getDauSach().getId();
        if (datTruocRepo.coNguoiDangCho(dauSachId))                           // BR-10
            throw new LoiNghiepVu("CO_NGUOI_CHO", "Sách đang có người đặt trước, không thể gia hạn");

        ct.giaHan(bd.getLoai().getSoNgayGiaHan());
        return ChuyenDoi.luotMuon(ct);
    }
}
