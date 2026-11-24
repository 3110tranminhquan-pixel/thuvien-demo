package vn.nhom.thuvien.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.nhom.thuvien.entity.BanSach;

import java.time.LocalDate;
import java.util.Optional;

public interface BanSachRepository extends JpaRepository<BanSach, Long> {

    // Khóa dòng (SELECT ... FOR UPDATE) để hai quầy không cùng cho mượn một bản sách
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM BanSach b WHERE b.maVach = :maVach")
    Optional<BanSach> timDeMuon(@Param("maVach") String maVach);

    Optional<BanSach> findByMaVach(String maVach);

    @Query(value = "SELECT MAX(ma_vach) FROM ban_sach WHERE ma_vach LIKE CAST(:tienTo AS text)", nativeQuery = true)
    String maVachLonNhat(@Param("tienTo") String tienTo);

    // Chạy lúc 0 giờ: các bản sách đang mượn đã qua hạn trả thì chuyển sang Quá hạn
    @Modifying
    @Query(value = """
        UPDATE ban_sach SET trang_thai = 'QUA_HAN'
        WHERE trang_thai = 'DANG_MUON'
          AND id IN (SELECT ban_sach_id FROM chi_tiet_muon
                     WHERE ngay_tra IS NULL AND han_tra < CAST(:homNay AS date))
        """, nativeQuery = true)
    int danhDauQuaHan(@Param("homNay") LocalDate homNay);
}
