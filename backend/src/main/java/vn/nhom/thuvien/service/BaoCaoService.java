package vn.nhom.thuvien.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom.thuvien.dto.ChuyenDoi;
import vn.nhom.thuvien.dto.LuotMuonDto;
import vn.nhom.thuvien.repository.ChiTietMuonRepository;

import java.time.LocalDate;
import java.util.List;

// Đợt 1 mới có báo cáo quá hạn (FR-32, mức Must). Các báo cáo mức Should làm ở đợt 2.
@Service
public class BaoCaoService {

    private final ChiTietMuonRepository chiTietRepo;

    public BaoCaoService(ChiTietMuonRepository chiTietRepo) {
        this.chiTietRepo = chiTietRepo;
    }

    @Transactional(readOnly = true)
    public List<LuotMuonDto> dsQuaHan() {
        return chiTietRepo.findByNgayTraIsNullAndHanTraBeforeOrderByHanTraAsc(LocalDate.now()).stream()
                .map(ChuyenDoi::luotMuon).toList();
    }
}
