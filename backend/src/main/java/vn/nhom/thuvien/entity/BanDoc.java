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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import vn.nhom.thuvien.entity.enums.TrangThaiPhieuPhat;
import vn.nhom.thuvien.entity.enums.TrangThaiThe;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ban_doc")
public class BanDoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ma_the")
    private String maThe;

    @Column(name = "ho_ten")
    private String hoTen;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "email")
    private String email;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Column(name = "don_vi")
    private String donVi;

    @Column(name = "ngay_cap")
    private LocalDate ngayCap = LocalDate.now();

    @Column(name = "ngay_het_han")
    private LocalDate ngayHetHan;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThaiThe trangThai = TrangThaiThe.HOAT_DONG;

    @Column(name = "ly_do_khoa")
    private String lyDoKhoa;

    @Column(name = "khoa_thu_cong")
    private Boolean khoaThuCong = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loai_id")
    private LoaiBanDoc loai;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id")
    private NguoiDung nguoiDung;

    @OneToMany(mappedBy = "banDoc", fetch = FetchType.LAZY)
    private List<PhieuMuon> dsPhieuMuon = new ArrayList<>();

    // BR-01 và BR-03: điều kiện chung của bạn đọc trước khi mượn thêm
    public KetQuaKiemTra coTheMuon(LocalDate ngay) {
        if (trangThai == TrangThaiThe.BI_KHOA)
            return KetQuaKiemTra.khongDat("Thẻ đang bị khóa" + (lyDoKhoa != null ? ": " + lyDoKhoa : ""));
        if (trangThai == TrangThaiThe.NGUNG)
            return KetQuaKiemTra.khongDat("Thẻ đã ngừng sử dụng");
        if (trangThai == TrangThaiThe.HET_HAN || ngayHetHan.isBefore(ngay))
            return KetQuaKiemTra.khongDat("Thẻ đã hết hạn từ ngày " + ngayHetHan);
        long soQuaHan = dsDangMuon().stream().filter(ct -> ct.soNgayTre(ngay) > 0).count();
        if (soQuaHan > 0)
            return KetQuaKiemTra.khongDat("Bạn đọc đang có " + soQuaHan + " cuốn quá hạn chưa trả");
        if (tongNo().signum() > 0)
            return KetQuaKiemTra.khongDat("Bạn đọc còn phiếu phạt chưa thanh toán");
        return KetQuaKiemTra.dat();
    }

    public List<ChiTietMuon> dsDangMuon() {
        List<ChiTietMuon> ds = new ArrayList<>();
        for (PhieuMuon pm : dsPhieuMuon) {
            for (ChiTietMuon ct : pm.getDsChiTiet()) {
                if (ct.dangMuon()) ds.add(ct);
            }
        }
        return ds;
    }

    public int soSachDangMuon() {
        return dsDangMuon().size();
    }

    // BR-05: không mượn cùng lúc hai bản của một đầu sách
    public boolean dangMuonDauSach(Long dauSachId) {
        return dsDangMuon().stream()
                .anyMatch(ct -> ct.getBanSach().getDauSach().getId().equals(dauSachId));
    }

    // Tổng tiền các phiếu phạt chưa thanh toán
    public BigDecimal tongNo() {
        BigDecimal tong = BigDecimal.ZERO;
        for (PhieuMuon pm : dsPhieuMuon) {
            for (ChiTietMuon ct : pm.getDsChiTiet()) {
                for (PhieuPhat pp : ct.getDsPhieuPhat()) {
                    if (pp.getTrangThai() == TrangThaiPhieuPhat.CHUA_THANH_TOAN)
                        tong = tong.add(pp.getSoTien());
                }
            }
        }
        return tong;
    }

    // Số ngày trễ lớn nhất trong các cuốn đang mượn, dùng cho BR-14
    public int soNgayTreLonNhat(LocalDate ngay) {
        int max = 0;
        for (ChiTietMuon ct : dsDangMuon()) {
            max = Math.max(max, ct.soNgayTre(ngay));
        }
        return max;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMaThe() { return maThe; }
    public void setMaThe(String maThe) { this.maThe = maThe; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public String getDonVi() { return donVi; }
    public void setDonVi(String donVi) { this.donVi = donVi; }
    public LocalDate getNgayCap() { return ngayCap; }
    public void setNgayCap(LocalDate ngayCap) { this.ngayCap = ngayCap; }
    public LocalDate getNgayHetHan() { return ngayHetHan; }
    public void setNgayHetHan(LocalDate ngayHetHan) { this.ngayHetHan = ngayHetHan; }
    public TrangThaiThe getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiThe trangThai) { this.trangThai = trangThai; }
    public String getLyDoKhoa() { return lyDoKhoa; }
    public void setLyDoKhoa(String lyDoKhoa) { this.lyDoKhoa = lyDoKhoa; }
    public Boolean getKhoaThuCong() { return khoaThuCong; }
    public void setKhoaThuCong(Boolean khoaThuCong) { this.khoaThuCong = khoaThuCong; }
    public LoaiBanDoc getLoai() { return loai; }
    public void setLoai(LoaiBanDoc loai) { this.loai = loai; }
    public NguoiDung getNguoiDung() { return nguoiDung; }
    public void setNguoiDung(NguoiDung nguoiDung) { this.nguoiDung = nguoiDung; }
    public List<PhieuMuon> getDsPhieuMuon() { return dsPhieuMuon; }
    public void setDsPhieuMuon(List<PhieuMuon> dsPhieuMuon) { this.dsPhieuMuon = dsPhieuMuon; }
}
