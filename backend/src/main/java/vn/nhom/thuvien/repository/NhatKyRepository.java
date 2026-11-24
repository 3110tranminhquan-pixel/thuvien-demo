package vn.nhom.thuvien.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.nhom.thuvien.entity.NhatKy;

import java.util.List;

public interface NhatKyRepository extends JpaRepository<NhatKy, Long> {

    List<NhatKy> findTop300ByOrderByIdDesc();
}
