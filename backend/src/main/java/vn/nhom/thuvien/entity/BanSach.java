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
import vn.nhom.thuvien.entity.enums.TrangThaiBanSach;
import vn.nhom.thuvien.exception.LoiNghiepVu;

import java.time.LocalDate;

@Entity
@Table(name = "ban_sach")
public class BanSach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ma_vach")
    private String maVach;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dau_sach_id")
    private DauSach dauSach;

    @Column(name = "vi_tri_ke")
    private String viTriKe;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThaiBanSach trangThai = TrangThaiBanSach.SAN_SANG;

    @Column(name = "ngay_nhap")
    private LocalDate ngayNhap = LocalDate.now();

    @Column(name = "ghi_chu")
    private String ghiChu;

    // Chặn mọi chuyển đổi không có trên sơ đồ trạng thái (mục 3.8.2)
    public void chuyenTrangThai(TrangThaiBanSach moi) {
        if (!trangThai.coTheChuyenSang(moi)) {
            throw new LoiNghiepVu("CHUYEN_TRANG_THAI_SAI",
                    "Không thể chuyển bản sách " + maVach + " từ \"" + trangThai.getTen()
                            + "\" sang \"" + moi.getTen() + "\"");
        }
        this.trangThai = moi;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMaVach() { return maVach; }
    public void setMaVach(String maVach) { this.maVach = maVach; }
    public DauSach getDauSach() { return dauSach; }
    public void setDauSach(DauSach dauSach) { this.dauSach = dauSach; }
    public String getViTriKe() { return viTriKe; }
    public void setViTriKe(String viTriKe) { this.viTriKe = viTriKe; }
    public TrangThaiBanSach getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiBanSach trangThai) { this.trangThai = trangThai; }
    public LocalDate getNgayNhap() { return ngayNhap; }
    public void setNgayNhap(LocalDate ngayNhap) { this.ngayNhap = ngayNhap; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
