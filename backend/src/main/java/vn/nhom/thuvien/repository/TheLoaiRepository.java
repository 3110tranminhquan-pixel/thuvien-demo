package vn.nhom.thuvien.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.nhom.thuvien.entity.TheLoai;

import java.util.List;

public interface TheLoaiRepository extends JpaRepository<TheLoai, Integer> {

    List<TheLoai> findAllByOrderByTenAsc();

    boolean existsByTenIgnoreCase(String ten);

    boolean existsByTenIgnoreCaseAndIdNot(String ten, Integer id);
}
