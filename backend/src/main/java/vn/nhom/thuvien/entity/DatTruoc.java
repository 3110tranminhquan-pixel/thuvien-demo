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
import jakarta.persistence.Table;
import vn.nhom.thuvien.entity.enums.TrangThaiDatTruoc;

import java.time.LocalDateTime;

@Entity
@Table(name = "dat_truoc")
public class DatTruoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ban_doc_id")
    private BanDoc banDoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dau_sach_id")
    private DauSach dauSach;

    // Chỉ có giá trị khi đã đến lượt và được giữ một bản sách
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ban_sach_id")
    private BanSach banSach;

    @Column(name = "thoi_diem_dat")
    private LocalDateTime thoiDiemDat = LocalDateTime.now();

    @Column(name = "han_giu")
    private LocalDateTime hanGiu;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThaiDatTruoc trangThai = TrangThaiDatTruoc.DANG_CHO;

    public boolean conHieuLuc() {
        return trangThai == TrangThaiDatTruoc.DANG_CHO || trangThai == TrangThaiDatTruoc.DANG_GIU;
    }

    public void giuSach(BanSach bs, LocalDateTime hanGiu) {
        this.banSach = bs;
        this.hanGiu = hanGiu;
        this.trangThai = TrangThaiDatTruoc.DANG_GIU;
    }

    public void huy() {
        this.trangThai = TrangThaiDatTruoc.DA_HUY;
    }

    public void daNhan() {
        this.trangThai = TrangThaiDatTruoc.DA_NHAN;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BanDoc getBanDoc() { return banDoc; }
    public void setBanDoc(BanDoc banDoc) { this.banDoc = banDoc; }
    public DauSach getDauSach() { return dauSach; }
    public void setDauSach(DauSach dauSach) { this.dauSach = dauSach; }
    public BanSach getBanSach() { return banSach; }
    public void setBanSach(BanSach banSach) { this.banSach = banSach; }
    public LocalDateTime getThoiDiemDat() { return thoiDiemDat; }
    public void setThoiDiemDat(LocalDateTime thoiDiemDat) { this.thoiDiemDat = thoiDiemDat; }
    public LocalDateTime getHanGiu() { return hanGiu; }
    public void setHanGiu(LocalDateTime hanGiu) { this.hanGiu = hanGiu; }
    public TrangThaiDatTruoc getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiDatTruoc trangThai) { this.trangThai = trangThai; }
}
