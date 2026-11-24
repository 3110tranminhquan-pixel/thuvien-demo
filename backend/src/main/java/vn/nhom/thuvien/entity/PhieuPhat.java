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
import vn.nhom.thuvien.entity.enums.LoaiViPham;
import vn.nhom.thuvien.entity.enums.TrangThaiPhieuPhat;
import vn.nhom.thuvien.exception.LoiNghiepVu;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "phieu_phat")
public class PhieuPhat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chi_tiet_muon_id")
    private ChiTietMuon chiTietMuon;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_vi_pham")
    private LoaiViPham loaiViPham;

    @Column(name = "so_tien")
    private BigDecimal soTien;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThaiPhieuPhat trangThai = TrangThaiPhieuPhat.CHUA_THANH_TOAN;

    @Column(name = "ngay_lap")
    private LocalDateTime ngayLap = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_lap_id")
    private NguoiDung nguoiLap;

    @Column(name = "ngay_thu")
    private LocalDateTime ngayThu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_thu_id")
    private NguoiDung nguoiThu;

    @Column(name = "ly_do_huy")
    private String lyDoHuy;

    protected PhieuPhat() {
    }

    // Tạo phiếu và gắn luôn vào chi tiết mượn để các phép tính tongNo() thấy ngay
    public static PhieuPhat lap(ChiTietMuon ct, LoaiViPham loai, BigDecimal soTien, NguoiDung nguoiLap) {
        PhieuPhat pp = new PhieuPhat();
        pp.chiTietMuon = ct;
        pp.loaiViPham = loai;
        pp.soTien = soTien.setScale(0, RoundingMode.HALF_UP);   // cột so_tien là NUMERIC(12,0)
        pp.nguoiLap = nguoiLap;
        ct.getDsPhieuPhat().size();   // nạp danh sách cũ từ CSDL trước, rồi mới thêm phiếu mới
        ct.getDsPhieuPhat().add(pp);
        return pp;
    }

    public void thanhToan(NguoiDung thuThu) {
        if (trangThai != TrangThaiPhieuPhat.CHUA_THANH_TOAN)
            throw new LoiNghiepVu("PHIEU_DA_XU_LY", "Phiếu phạt này đã được xử lý trước đó");
        this.trangThai = TrangThaiPhieuPhat.DA_THANH_TOAN;
        this.ngayThu = LocalDateTime.now();
        this.nguoiThu = thuThu;
    }

    public void huy(String lyDo) {
        if (trangThai != TrangThaiPhieuPhat.CHUA_THANH_TOAN)
            throw new LoiNghiepVu("PHIEU_DA_XU_LY", "Chỉ hủy được phiếu chưa thanh toán");
        if (lyDo == null || lyDo.isBlank())
            throw new LoiNghiepVu("THIEU_LY_DO", "Phải nhập lý do hủy phiếu phạt");
        this.trangThai = TrangThaiPhieuPhat.DA_HUY;
        this.lyDoHuy = lyDo.trim();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ChiTietMuon getChiTietMuon() { return chiTietMuon; }
    public void setChiTietMuon(ChiTietMuon chiTietMuon) { this.chiTietMuon = chiTietMuon; }
    public LoaiViPham getLoaiViPham() { return loaiViPham; }
    public void setLoaiViPham(LoaiViPham loaiViPham) { this.loaiViPham = loaiViPham; }
    public BigDecimal getSoTien() { return soTien; }
    public void setSoTien(BigDecimal soTien) { this.soTien = soTien; }
    public TrangThaiPhieuPhat getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiPhieuPhat trangThai) { this.trangThai = trangThai; }
    public LocalDateTime getNgayLap() { return ngayLap; }
    public void setNgayLap(LocalDateTime ngayLap) { this.ngayLap = ngayLap; }
    public NguoiDung getNguoiLap() { return nguoiLap; }
    public void setNguoiLap(NguoiDung nguoiLap) { this.nguoiLap = nguoiLap; }
    public LocalDateTime getNgayThu() { return ngayThu; }
    public void setNgayThu(LocalDateTime ngayThu) { this.ngayThu = ngayThu; }
    public NguoiDung getNguoiThu() { return nguoiThu; }
    public void setNguoiThu(NguoiDung nguoiThu) { this.nguoiThu = nguoiThu; }
    public String getLyDoHuy() { return lyDoHuy; }
    public void setLyDoHuy(String lyDoHuy) { this.lyDoHuy = lyDoHuy; }
}
