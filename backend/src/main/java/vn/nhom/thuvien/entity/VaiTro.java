package vn.nhom.thuvien.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vai_tro")
public class VaiTro {

    public static final int BAN_DOC = 1;
    public static final int THU_THU = 2;
    public static final int QUAN_TRI = 3;

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "ten")
    private String ten;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }
}
