package vn.nhom.thuvien.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.nhom.thuvien.entity.BanDoc;
import vn.nhom.thuvien.entity.enums.TrangThaiThe;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BanDocRepository extends JpaRepository<BanDoc, Long> {

    Optional<BanDoc> findByMaThe(String maThe);

    Optional<BanDoc> findByNguoiDungId(Long nguoiDungId);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    List<BanDoc> findByTrangThaiIn(Collection<TrangThaiThe> dsTrangThai);

    // tuKhoa rỗng thì lấy tất cả
    @Query("""
        SELECT b FROM BanDoc b
        WHERE :tuKhoa = ''
           OR LOWER(b.hoTen) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))
           OR LOWER(b.maThe) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))
           OR LOWER(b.email) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))
        ORDER BY b.maThe
        """)
    List<BanDoc> timKiem(@Param("tuKhoa") String tuKhoa);

    @Query(value = "SELECT MAX(ma_the) FROM ban_doc", nativeQuery = true)
    String maTheLonNhat();
}
