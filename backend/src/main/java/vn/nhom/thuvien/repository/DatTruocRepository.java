package vn.nhom.thuvien.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.nhom.thuvien.entity.DatTruoc;
import vn.nhom.thuvien.entity.enums.TrangThaiDatTruoc;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DatTruocRepository extends JpaRepository<DatTruoc, Long> {

    List<TrangThaiDatTruoc> CON_HIEU_LUC = List.of(TrangThaiDatTruoc.DANG_CHO, TrangThaiDatTruoc.DANG_GIU);

    Optional<DatTruoc> findByBanSachIdAndTrangThai(Long banSachId, TrangThaiDatTruoc trangThai);

    long countByDauSachIdAndTrangThai(Long dauSachId, TrangThaiDatTruoc trangThai);

    long countByDauSachIdAndTrangThaiAndThoiDiemDatBefore(Long dauSachId, TrangThaiDatTruoc trangThai, LocalDateTime moc);

    // Người đứng đầu hàng chờ: ai đặt trước được phục vụ trước (BR-12)
    Optional<DatTruoc> findFirstByDauSachIdAndTrangThaiOrderByThoiDiemDatAscIdAsc(Long dauSachId, TrangThaiDatTruoc trangThai);

    long countByBanDocIdAndTrangThaiIn(Long banDocId, Collection<TrangThaiDatTruoc> dsTrangThai);

    boolean existsByBanDocIdAndDauSachIdAndTrangThaiIn(Long banDocId, Long dauSachId, Collection<TrangThaiDatTruoc> dsTrangThai);

    List<DatTruoc> findByBanDocIdAndTrangThaiIn(Long banDocId, Collection<TrangThaiDatTruoc> dsTrangThai);

    List<DatTruoc> findByBanDocIdOrderByThoiDiemDatDesc(Long banDocId);

    List<DatTruoc> findByTrangThaiAndHanGiuBefore(TrangThaiDatTruoc trangThai, LocalDateTime luc);

    default Optional<DatTruoc> timDangGiu(Long banSachId) {
        return findByBanSachIdAndTrangThai(banSachId, TrangThaiDatTruoc.DANG_GIU);
    }

    default boolean coNguoiDangCho(Long dauSachId) {
        return countByDauSachIdAndTrangThai(dauSachId, TrangThaiDatTruoc.DANG_CHO) > 0;
    }

    default Optional<DatTruoc> nguoiDauHang(Long dauSachId) {
        return findFirstByDauSachIdAndTrangThaiOrderByThoiDiemDatAscIdAsc(dauSachId, TrangThaiDatTruoc.DANG_CHO);
    }
}
