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
import vn.nhom.thuvien.entity.enums.TrangThaiTaiKhoan;

import java.time.LocalDateTime;

@Entity
@Table(name = "nguoi_dung")
public class NguoiDung {

    // NFR-07: sai 5 lần liên tiếp thì khóa 15 phút
    public static final int SO_LAN_SAI_TOI_DA = 5;
    public static final int SO_PHUT_KHOA = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ten_dang_nhap")
    private String tenDangNhap;

    @Column(name = "mat_khau_hash")
    private String matKhauHash;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vai_tro_id")
    private VaiTro vaiTro;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThaiTaiKhoan trangThai = TrangThaiTaiKhoan.HOAT_DONG;

    @Column(name = "so_lan_sai")
    private Integer soLanSai = 0;

    @Column(name = "khoa_den")
    private LocalDateTime khoaDen;

    public boolean dangBiKhoaTam(LocalDateTime luc) {
        return khoaDen != null && khoaDen.isAfter(luc);
    }

    // Trả về true nếu lần sai này làm tài khoản bị khóa tạm
    public boolean ghiNhanSai(LocalDateTime luc) {
        soLanSai = soLanSai + 1;
        if (soLanSai >= SO_LAN_SAI_TOI_DA) {
            khoaDen = luc.plusMinutes(SO_PHUT_KHOA);
            soLanSai = 0;
            return true;
        }
        return false;
    }

    public void datLaiSoLanSai() {
        soLanSai = 0;
        khoaDen = null;
    }

    public String tenVaiTro() {
        return vaiTro.getTen();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }
    public String getMatKhauHash() { return matKhauHash; }
    public void setMatKhauHash(String matKhauHash) { this.matKhauHash = matKhauHash; }
    public VaiTro getVaiTro() { return vaiTro; }
    public void setVaiTro(VaiTro vaiTro) { this.vaiTro = vaiTro; }
    public TrangThaiTaiKhoan getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiTaiKhoan trangThai) { this.trangThai = trangThai; }
    public Integer getSoLanSai() { return soLanSai; }
    public void setSoLanSai(Integer soLanSai) { this.soLanSai = soLanSai; }
    public LocalDateTime getKhoaDen() { return khoaDen; }
    public void setKhoaDen(LocalDateTime khoaDen) { this.khoaDen = khoaDen; }
}
