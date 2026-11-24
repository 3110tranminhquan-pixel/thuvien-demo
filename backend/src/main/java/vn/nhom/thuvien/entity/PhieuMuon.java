package vn.nhom.thuvien.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "phieu_muon")
public class PhieuMuon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ban_doc_id")
    private BanDoc banDoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thu_thu_id")
    private NguoiDung thuThu;

    @Column(name = "ngay_muon")
    private LocalDateTime ngayMuon = LocalDateTime.now();

    @OneToMany(mappedBy = "phieuMuon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietMuon> dsChiTiet = new ArrayList<>();

    protected PhieuMuon() {
    }

    public PhieuMuon(BanDoc banDoc, NguoiDung thuThu) {
        this.banDoc = banDoc;
        this.thuThu = thuThu;
    }

    public ChiTietMuon themChiTiet(BanSach banSach, LocalDate hanTra) {
        ChiTietMuon ct = new ChiTietMuon();
        ct.setPhieuMuon(this);
        ct.setBanSach(banSach);
        ct.setHanTra(hanTra);
        dsChiTiet.add(ct);
        return ct;
    }

    // Trạng thái phiếu suy ra từ các chi tiết, không lưu cột riêng (mục 3.8.2)
    public boolean daTraDu() {
        return dsChiTiet.stream().allMatch(ct -> ct.getNgayTra() != null);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BanDoc getBanDoc() { return banDoc; }
    public void setBanDoc(BanDoc banDoc) { this.banDoc = banDoc; }
    public NguoiDung getThuThu() { return thuThu; }
    public void setThuThu(NguoiDung thuThu) { this.thuThu = thuThu; }
    public LocalDateTime getNgayMuon() { return ngayMuon; }
    public void setNgayMuon(LocalDateTime ngayMuon) { this.ngayMuon = ngayMuon; }
    public List<ChiTietMuon> getDsChiTiet() { return dsChiTiet; }
    public void setDsChiTiet(List<ChiTietMuon> dsChiTiet) { this.dsChiTiet = dsChiTiet; }
}
