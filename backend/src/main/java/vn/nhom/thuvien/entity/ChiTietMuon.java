package vn.nhom.thuvien.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import vn.nhom.thuvien.entity.enums.TinhTrangSach;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chi_tiet_muon")
public class ChiTietMuon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phieu_muon_id")
    private PhieuMuon phieuMuon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ban_sach_id")
    private BanSach banSach;

    @Column(name = "han_tra")
    private LocalDate hanTra;

    @Column(name = "ngay_tra")
    private LocalDateTime ngayTra;

    @Column(name = "so_lan_gia_han")
    private Integer soLanGiaHan = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "tinh_trang_khi_tra")
    private TinhTrangSach tinhTrangKhiTra;

    @OneToMany(mappedBy = "chiTietMuon", fetch = FetchType.LAZY)
    private List<PhieuPhat> dsPhieuPhat = new ArrayList<>();

    public boolean dangMuon() {
        return ngayTra == null;
    }

    // BR-07: số ngày trễ tính theo ngày lịch, trả đúng ngày hạn thì không trễ
    public int soNgayTre(LocalDate ngayXet) {
        long tre = ChronoUnit.DAYS.between(hanTra, ngayXet);
        return tre > 0 ? (int) tre : 0;
    }

    // BR-10: hai điều kiện thuộc về chính lượt mượn
    public KetQuaKiemTra kiemTraGiaHan(LoaiBanDoc loai, LocalDate homNay) {
        if (soNgayTre(homNay) > 0)
            return KetQuaKiemTra.khongDat("Sách đã quá hạn, vui lòng mang sách đến trả");
        if (soLanGiaHan >= loai.getSoLanGiaHan())
            return KetQuaKiemTra.khongDat("Đã gia hạn tối đa " + loai.getSoLanGiaHan() + " lần");
        return KetQuaKiemTra.dat();
    }

    // BR-10: hạn mới tính từ hạn cũ, không phải từ ngày gia hạn
    public void giaHan(int soNgay) {
        this.hanTra = this.hanTra.plusDays(soNgay);
        this.soLanGiaHan++;
    }

    public void ghiNhanTra(TinhTrangSach tinhTrang, LocalDateTime luc) {
        this.tinhTrangKhiTra = tinhTrang;
        this.ngayTra = luc;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PhieuMuon getPhieuMuon() { return phieuMuon; }
    public void setPhieuMuon(PhieuMuon phieuMuon) { this.phieuMuon = phieuMuon; }
    public BanSach getBanSach() { return banSach; }
    public void setBanSach(BanSach banSach) { this.banSach = banSach; }
    public LocalDate getHanTra() { return hanTra; }
    public void setHanTra(LocalDate hanTra) { this.hanTra = hanTra; }
    public LocalDateTime getNgayTra() { return ngayTra; }
    public void setNgayTra(LocalDateTime ngayTra) { this.ngayTra = ngayTra; }
    public Integer getSoLanGiaHan() { return soLanGiaHan; }
    public void setSoLanGiaHan(Integer soLanGiaHan) { this.soLanGiaHan = soLanGiaHan; }
    public TinhTrangSach getTinhTrangKhiTra() { return tinhTrangKhiTra; }
    public void setTinhTrangKhiTra(TinhTrangSach tinhTrangKhiTra) { this.tinhTrangKhiTra = tinhTrangKhiTra; }
    public List<PhieuPhat> getDsPhieuPhat() { return dsPhieuPhat; }
    public void setDsPhieuPhat(List<PhieuPhat> dsPhieuPhat) { this.dsPhieuPhat = dsPhieuPhat; }
}
