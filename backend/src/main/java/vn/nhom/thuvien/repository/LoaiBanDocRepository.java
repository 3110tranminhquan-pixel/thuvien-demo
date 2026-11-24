package vn.nhom.thuvien.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.nhom.thuvien.entity.LoaiBanDoc;

import java.util.List;

public interface LoaiBanDocRepository extends JpaRepository<LoaiBanDoc, Integer> {

    List<LoaiBanDoc> findAllByOrderByIdAsc();
}
