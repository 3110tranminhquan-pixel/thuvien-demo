package vn.nhom.thuvien.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tham_so_he_thong")
public class ThamSo {

    @Id
    @Column(name = "ma")
    private String ma;

    @Column(name = "gia_tri")
    private String giaTri;

    @Column(name = "mo_ta")
    private String moTa;

    public String getMa() { return ma; }
    public void setMa(String ma) { this.ma = ma; }
    public String getGiaTri() { return giaTri; }
    public void setGiaTri(String giaTri) { this.giaTri = giaTri; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}
