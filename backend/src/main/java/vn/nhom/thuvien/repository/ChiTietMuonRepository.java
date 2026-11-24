package vn.nhom.thuvien.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.nhom.thuvien.entity.ChiTietMuon;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ChiTietMuonRepository extends JpaRepository<ChiTietMuon, Long> {

    // Lượt mượn chưa trả của một bản sách (chỉ mục uq_ctm_dang_muon bảo đảm tối đa một dòng)
    @Query("SELECT ct FROM ChiTietMuon ct WHERE ct.banSach.id = :banSachId AND ct.ngayTra IS NULL")
    Optional<ChiTietMuon> timDangMuon(@Param("banSachId") Long banSachId);

    List<ChiTietMuon> findByNgayTraIsNullAndHanTraBeforeOrderByHanTraAsc(LocalDate ngay);

    List<ChiTietMuon> findByNgayTraIsNullAndHanTra(LocalDate ngay);

    List<ChiTietMuon> findByPhieuMuonBanDocIdOrderByIdDesc(Long banDocId);
}
