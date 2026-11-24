package vn.nhom.thuvien.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.nhom.thuvien.entity.ThamSo;

import java.util.List;

public interface ThamSoRepository extends JpaRepository<ThamSo, String> {

    List<ThamSo> findAllByOrderByMaAsc();
}
