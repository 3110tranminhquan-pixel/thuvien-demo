package vn.nhom.thuvien.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "loai_ban_doc")
public class LoaiBanDoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ten_loai")
    private String tenLoai;

    @Column(name = "so_sach_toi_da")
    private Integer soSachToiDa;

    @Column(name = "so_ngay_muon")
    private Integer soNgayMuon;

    @Column(name = "so_lan_gia_han")
    private Integer soLanGiaHan;

    @Column(name = "so_ngay_gia_han")
    private Integer soNgayGiaHan;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTenLoai() { return tenLoai; }
    public void setTenLoai(String tenLoai) { this.tenLoai = tenLoai; }
    public Integer getSoSachToiDa() { return soSachToiDa; }
    public void setSoSachToiDa(Integer soSachToiDa) { this.soSachToiDa = soSachToiDa; }
    public Integer getSoNgayMuon() { return soNgayMuon; }
    public void setSoNgayMuon(Integer soNgayMuon) { this.soNgayMuon = soNgayMuon; }
    public Integer getSoLanGiaHan() { return soLanGiaHan; }
    public void setSoLanGiaHan(Integer soLanGiaHan) { this.soLanGiaHan = soLanGiaHan; }
    public Integer getSoNgayGiaHan() { return soNgayGiaHan; }
    public void setSoNgayGiaHan(Integer soNgayGiaHan) { this.soNgayGiaHan = soNgayGiaHan; }
}
