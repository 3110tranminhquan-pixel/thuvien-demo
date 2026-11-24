package vn.nhom.thuvien.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.nhom.thuvien.entity.NhaXuatBan;

import java.util.List;

public interface NhaXuatBanRepository extends JpaRepository<NhaXuatBan, Integer> {

    List<NhaXuatBan> findAllByOrderByTenAsc();

    boolean existsByTenIgnoreCase(String ten);

    boolean existsByTenIgnoreCaseAndIdNot(String ten, Integer id);
}
