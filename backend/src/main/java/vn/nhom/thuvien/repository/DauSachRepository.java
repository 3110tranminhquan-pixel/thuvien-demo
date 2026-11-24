package vn.nhom.thuvien.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.nhom.thuvien.entity.DauSach;

import java.util.List;

public interface DauSachRepository extends JpaRepository<DauSach, Long> {

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);

    // FR-04: tìm theo nhan đề, tác giả hoặc ISBN, lọc thêm thể loại, năm, chỉ sách còn.
    // Ba nhánh tìm (nhan đề, ISBN, tác giả) tách bằng UNION để mỗi nhánh dùng được chỉ mục riêng,
    // nhan đề dùng chỉ mục trigram idx_dau_sach_nhan_de_trgm khi từ khóa đủ hiếm.
    // Các tham số đều CAST rõ kiểu để PostgreSQL không báo lỗi khi giá trị là null.
    @Query(value = """
        SELECT ds.* FROM dau_sach ds
        WHERE ds.trang_thai = 'DANG_DUNG'
          AND (CAST(:tuKhoa AS text) = ''
               OR ds.id IN (SELECT d.id FROM dau_sach d WHERE d.nhan_de ILIKE CAST(:mau AS text)
                            UNION SELECT d.id FROM dau_sach d WHERE d.isbn = CAST(:tuKhoa AS text)
                            UNION SELECT x.dau_sach_id FROM dau_sach_tac_gia x JOIN tac_gia tg ON tg.id = x.tac_gia_id
                                  WHERE tg.ho_ten ILIKE CAST(:mau AS text)))
          AND (CAST(:theLoai AS integer) IS NULL OR ds.the_loai_id = CAST(:theLoai AS integer))
          AND (CAST(:nam AS integer) IS NULL OR ds.nam_xb = CAST(:nam AS integer))
          AND (CAST(:chiConSach AS boolean) = false
               OR EXISTS (SELECT 1 FROM ban_sach b WHERE b.dau_sach_id = ds.id AND b.trang_thai = 'SAN_SANG'))
        ORDER BY ds.nhan_de
        """,
        countQuery = """
        SELECT COUNT(*) FROM dau_sach ds
        WHERE ds.trang_thai = 'DANG_DUNG'
          AND (CAST(:tuKhoa AS text) = ''
               OR ds.id IN (SELECT d.id FROM dau_sach d WHERE d.nhan_de ILIKE CAST(:mau AS text)
                            UNION SELECT d.id FROM dau_sach d WHERE d.isbn = CAST(:tuKhoa AS text)
                            UNION SELECT x.dau_sach_id FROM dau_sach_tac_gia x JOIN tac_gia tg ON tg.id = x.tac_gia_id
                                  WHERE tg.ho_ten ILIKE CAST(:mau AS text)))
          AND (CAST(:theLoai AS integer) IS NULL OR ds.the_loai_id = CAST(:theLoai AS integer))
          AND (CAST(:nam AS integer) IS NULL OR ds.nam_xb = CAST(:nam AS integer))
          AND (CAST(:chiConSach AS boolean) = false
               OR EXISTS (SELECT 1 FROM ban_sach b WHERE b.dau_sach_id = ds.id AND b.trang_thai = 'SAN_SANG'))
        """,
        nativeQuery = true)
    Page<DauSach> timKiem(@Param("tuKhoa") String tuKhoa, @Param("mau") String mau,
                          @Param("theLoai") Integer theLoai, @Param("nam") Integer nam,
                          @Param("chiConSach") boolean chiConSach, Pageable trang);

    // Danh sách cho thủ thư quản lý, gồm cả đầu sách đã ngừng
    @Query("""
        SELECT d FROM DauSach d
        WHERE :tuKhoa = ''
           OR LOWER(d.nhanDe) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))
           OR d.isbn LIKE CONCAT('%', :tuKhoa, '%')
        ORDER BY d.id DESC
        """)
    List<DauSach> danhSachQuanLy(@Param("tuKhoa") String tuKhoa);
}
