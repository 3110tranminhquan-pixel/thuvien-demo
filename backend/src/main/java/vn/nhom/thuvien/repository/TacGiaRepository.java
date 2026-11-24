package vn.nhom.thuvien.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.nhom.thuvien.entity.TacGia;

import java.util.List;

public interface TacGiaRepository extends JpaRepository<TacGia, Integer> {

    List<TacGia> findAllByOrderByHoTenAsc();
}
