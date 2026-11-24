package vn.nhom.thuvien.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.nhom.thuvien.entity.PhieuPhat;
import vn.nhom.thuvien.entity.enums.TrangThaiPhieuPhat;

import java.util.List;

public interface PhieuPhatRepository extends JpaRepository<PhieuPhat, Long> {

    List<PhieuPhat> findAllByOrderByIdDesc();

    List<PhieuPhat> findByTrangThaiOrderByIdDesc(TrangThaiPhieuPhat trangThai);

    List<PhieuPhat> findByChiTietMuonPhieuMuonBanDocIdOrderByIdDesc(Long banDocId);
}
