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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import vn.nhom.thuvien.entity.enums.LoaiTaiLieu;
import vn.nhom.thuvien.entity.enums.TrangThaiBanSach;
import vn.nhom.thuvien.entity.enums.TrangThaiDauSach;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "dau_sach")
public class DauSach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "nhan_de")
    private String nhanDe;

    @Column(name = "nam_xb")
    private Integer namXb;

    @Column(name = "lan_xb")
    private Integer lanXb = 1;

    @Column(name = "so_trang")
    private Integer soTrang;

    @Column(name = "gia_bia")
    private BigDecimal giaBia;

    @Column(name = "ma_ddc")
    private String maDdc;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_tai_lieu")
    private LoaiTaiLieu loaiTaiLieu;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThaiDauSach trangThai = TrangThaiDauSach.DANG_DUNG;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "the_loai_id")
    private TheLoai theLoai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nxb_id")
    private NhaXuatBan nhaXuatBan;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "dau_sach_tac_gia",
            joinColumns = @JoinColumn(name = "dau_sach_id"),
            inverseJoinColumns = @JoinColumn(name = "tac_gia_id"))
    private Set<TacGia> dsTacGia = new LinkedHashSet<>();

    @OneToMany(mappedBy = "dauSach", fetch = FetchType.LAZY)
    @OrderBy("maVach")
    private List<BanSach> dsBanSach = new ArrayList<>();

    public long soBanSanSang() {
        return dsBanSach.stream().filter(b -> b.getTrangThai() == TrangThaiBanSach.SAN_SANG).count();
    }

    // Số bản đang thuộc kho (không tính bản đã mất, đã thanh lý)
    public long soBanHienCo() {
        return dsBanSach.stream()
                .filter(b -> b.getTrangThai() != TrangThaiBanSach.MAT && b.getTrangThai() != TrangThaiBanSach.THANH_LY)
                .count();
    }

    // BR-04: tài liệu tham khảo chỉ đọc tại chỗ
    public boolean choMuonVe() {
        return loaiTaiLieu != LoaiTaiLieu.THAM_KHAO;
    }

    public String tenCacTacGia() {
        List<String> ten = new ArrayList<>();
        for (TacGia tg : dsTacGia) {
            ten.add(tg.getHoTen());
        }
        return String.join(", ", ten);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getNhanDe() { return nhanDe; }
    public void setNhanDe(String nhanDe) { this.nhanDe = nhanDe; }
    public Integer getNamXb() { return namXb; }
    public void setNamXb(Integer namXb) { this.namXb = namXb; }
    public Integer getLanXb() { return lanXb; }
    public void setLanXb(Integer lanXb) { this.lanXb = lanXb; }
    public Integer getSoTrang() { return soTrang; }
    public void setSoTrang(Integer soTrang) { this.soTrang = soTrang; }
    public BigDecimal getGiaBia() { return giaBia; }
    public void setGiaBia(BigDecimal giaBia) { this.giaBia = giaBia; }
    public String getMaDdc() { return maDdc; }
    public void setMaDdc(String maDdc) { this.maDdc = maDdc; }
    public LoaiTaiLieu getLoaiTaiLieu() { return loaiTaiLieu; }
    public void setLoaiTaiLieu(LoaiTaiLieu loaiTaiLieu) { this.loaiTaiLieu = loaiTaiLieu; }
    public TrangThaiDauSach getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiDauSach trangThai) { this.trangThai = trangThai; }
    public TheLoai getTheLoai() { return theLoai; }
    public void setTheLoai(TheLoai theLoai) { this.theLoai = theLoai; }
    public NhaXuatBan getNhaXuatBan() { return nhaXuatBan; }
    public void setNhaXuatBan(NhaXuatBan nhaXuatBan) { this.nhaXuatBan = nhaXuatBan; }
    public Set<TacGia> getDsTacGia() { return dsTacGia; }
    public void setDsTacGia(Set<TacGia> dsTacGia) { this.dsTacGia = dsTacGia; }
    public List<BanSach> getDsBanSach() { return dsBanSach; }
    public void setDsBanSach(List<BanSach> dsBanSach) { this.dsBanSach = dsBanSach; }
}
