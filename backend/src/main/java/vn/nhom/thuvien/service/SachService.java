package vn.nhom.thuvien.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom.thuvien.dto.BanSachDto;
import vn.nhom.thuvien.dto.CapNhatBanSachRequest;
import vn.nhom.thuvien.dto.ChuyenDoi;
import vn.nhom.thuvien.dto.DanhMucDto;
import vn.nhom.thuvien.dto.DanhMucRequest;
import vn.nhom.thuvien.dto.DauSachChiTiet;
import vn.nhom.thuvien.dto.DauSachRequest;
import vn.nhom.thuvien.dto.DauSachTomTat;
import vn.nhom.thuvien.dto.ThemBanSachRequest;
import vn.nhom.thuvien.dto.TrangKetQua;
import vn.nhom.thuvien.entity.BanSach;
import vn.nhom.thuvien.entity.DauSach;
import vn.nhom.thuvien.entity.NhaXuatBan;
import vn.nhom.thuvien.entity.TacGia;
import vn.nhom.thuvien.entity.TheLoai;
import vn.nhom.thuvien.entity.enums.LoaiTaiLieu;
import vn.nhom.thuvien.entity.enums.TrangThaiBanSach;
import vn.nhom.thuvien.entity.enums.TrangThaiDatTruoc;
import vn.nhom.thuvien.entity.enums.TrangThaiDauSach;
import vn.nhom.thuvien.exception.LoiNghiepVu;
import vn.nhom.thuvien.repository.BanSachRepository;
import vn.nhom.thuvien.repository.DatTruocRepository;
import vn.nhom.thuvien.repository.DauSachRepository;
import vn.nhom.thuvien.repository.NhaXuatBanRepository;
import vn.nhom.thuvien.repository.TacGiaRepository;
import vn.nhom.thuvien.repository.TheLoaiRepository;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class SachService {

    public static final int SO_DONG_MOI_TRANG = 20;

    private final DauSachRepository dauSachRepo;
    private final BanSachRepository banSachRepo;
    private final TheLoaiRepository theLoaiRepo;
    private final NhaXuatBanRepository nxbRepo;
    private final TacGiaRepository tacGiaRepo;
    private final DatTruocRepository datTruocRepo;

    public SachService(DauSachRepository dauSachRepo, BanSachRepository banSachRepo, TheLoaiRepository theLoaiRepo,
                       NhaXuatBanRepository nxbRepo, TacGiaRepository tacGiaRepo, DatTruocRepository datTruocRepo) {
        this.dauSachRepo = dauSachRepo;
        this.banSachRepo = banSachRepo;
        this.theLoaiRepo = theLoaiRepo;
        this.nxbRepo = nxbRepo;
        this.tacGiaRepo = tacGiaRepo;
        this.datTruocRepo = datTruocRepo;
    }

    // FR-04
    @Transactional(readOnly = true)
    public TrangKetQua<DauSachTomTat> timKiem(String tuKhoa, Integer theLoai, Integer nam, boolean chiConSach, int trang) {
        String tk = tuKhoa == null ? "" : tuKhoa.trim();
        String mau = "%" + tk + "%";   // mau = "%" + tuKhoa.trim() + "%"
        Page<DauSach> kq = dauSachRepo.timKiem(tk, mau, theLoai, nam, chiConSach,
                PageRequest.of(Math.max(trang, 0), SO_DONG_MOI_TRANG));
        return new TrangKetQua<>(kq.getContent().stream().map(ChuyenDoi::tomTat).toList(),
                kq.getTotalElements(), kq.getNumber(), kq.getTotalPages());
    }

    // FR-05
    @Transactional(readOnly = true)
    public DauSachChiTiet chiTiet(Long id) {
        DauSach ds = layDauSach(id);
        return ChuyenDoi.chiTiet(ds, datTruocRepo.countByDauSachIdAndTrangThai(id, TrangThaiDatTruoc.DANG_CHO));
    }

    @Transactional(readOnly = true)
    public List<DauSachTomTat> danhSachQuanLy(String tuKhoa) {
        return dauSachRepo.danhSachQuanLy(tuKhoa == null ? "" : tuKhoa.trim()).stream()
                .map(ChuyenDoi::tomTat).toList();
    }

    // FR-06
    @Transactional
    public DauSachChiTiet themDauSach(DauSachRequest req) {
        String isbn = chuanHoaIsbn(req.isbn());
        if (dauSachRepo.existsByIsbn(isbn))
            throw new LoiNghiepVu("TRUNG_ISBN", "ISBN " + isbn + " đã có trong hệ thống");
        DauSach ds = new DauSach();
        ganThongTin(ds, req, isbn);
        dauSachRepo.save(ds);
        return ChuyenDoi.chiTiet(ds, 0);
    }

    @Transactional
    public DauSachChiTiet suaDauSach(Long id, DauSachRequest req) {
        DauSach ds = layDauSach(id);
        String isbn = chuanHoaIsbn(req.isbn());
        if (dauSachRepo.existsByIsbnAndIdNot(isbn, id))
            throw new LoiNghiepVu("TRUNG_ISBN", "ISBN " + isbn + " đã có trong hệ thống");
        ganThongTin(ds, req, isbn);
        return chiTiet(id);
    }

    // Ngừng sử dụng chứ không xóa, để giữ lịch sử mượn
    @Transactional
    public DauSachChiTiet doiTrangThaiDauSach(Long id, boolean ngung) {
        DauSach ds = layDauSach(id);
        ds.setTrangThai(ngung ? TrangThaiDauSach.NGUNG : TrangThaiDauSach.DANG_DUNG);
        return chiTiet(id);
    }

    // FR-08: thêm bản sách, tự sinh mã vạch dạng 2 chữ + 8 số theo loại tài liệu
    @Transactional
    public List<BanSachDto> themBanSach(Long dauSachId, ThemBanSachRequest req) {
        DauSach ds = layDauSach(dauSachId);
        String tienTo = tienToMaVach(ds.getLoaiTaiLieu());
        String lonNhat = banSachRepo.maVachLonNhat(tienTo + "%");
        long so = lonNhat == null ? 0 : Long.parseLong(lonNhat.substring(2));
        List<BanSachDto> kq = new ArrayList<>();
        for (int i = 0; i < req.soLuong(); i++) {
            so++;
            BanSach bs = new BanSach();
            bs.setMaVach(tienTo + String.format("%08d", so));
            bs.setDauSach(ds);
            bs.setViTriKe(req.viTriKe());
            banSachRepo.save(bs);
            ds.getDsBanSach().add(bs);
            kq.add(ChuyenDoi.banSach(bs));
        }
        return kq;
    }

    // FR-09: đánh dấu Đang sửa, sửa xong về Sẵn sàng, hoặc Thanh lý. Chuyển đổi phải đúng sơ đồ trạng thái.
    @Transactional
    public BanSachDto capNhatBanSach(Long banSachId, CapNhatBanSachRequest req) {
        BanSach bs = banSachRepo.findById(banSachId)
                .orElseThrow(() -> LoiNghiepVu.khongTimThay("Không tìm thấy bản sách"));
        if (req.viTriKe() != null) bs.setViTriKe(req.viTriKe().trim());
        if (req.ghiChu() != null) bs.setGhiChu(req.ghiChu().trim());
        if (req.trangThai() != null && !req.trangThai().isBlank()
                && !req.trangThai().equals(bs.getTrangThai().name())) {
            TrangThaiBanSach moi;
            try {
                moi = TrangThaiBanSach.valueOf(req.trangThai());
            } catch (IllegalArgumentException e) {
                throw new LoiNghiepVu("TRANG_THAI_SAI", "Trạng thái bản sách không hợp lệ");
            }
            if (moi != TrangThaiBanSach.DANG_SUA && moi != TrangThaiBanSach.THANH_LY && moi != TrangThaiBanSach.SAN_SANG)
                throw new LoiNghiepVu("KHONG_DUOC_DOI",
                        "Ở đây chỉ được đổi sang Đang sửa, Sẵn sàng hoặc Thanh lý; các trạng thái khác do mượn trả tự cập nhật");
            bs.chuyenTrangThai(moi);
        }
        return ChuyenDoi.banSach(bs);
    }

    // FR-10: kiểm tra số kiểm tra của ISBN-13
    public static boolean isbn13HopLe(String isbn) {
        if (isbn == null || !isbn.matches("\\d{13}")) return false;
        int tong = 0;
        for (int i = 0; i < 12; i++) {
            int so = isbn.charAt(i) - '0';
            tong += (i % 2 == 0) ? so : so * 3;
        }
        int kiemTra = (10 - tong % 10) % 10;
        return kiemTra == isbn.charAt(12) - '0';
    }

    // ----- Danh mục: thể loại, nhà xuất bản, tác giả (FR-07) -----

    @Transactional(readOnly = true)
    public List<DanhMucDto> dsTheLoai() {
        return theLoaiRepo.findAllByOrderByTenAsc().stream().map(t -> new DanhMucDto(t.getId(), t.getTen(), t.getMaDdc())).toList();
    }

    @Transactional(readOnly = true)
    public List<DanhMucDto> dsNhaXuatBan() {
        return nxbRepo.findAllByOrderByTenAsc().stream().map(n -> new DanhMucDto(n.getId(), n.getTen(), n.getDiaChi())).toList();
    }

    @Transactional(readOnly = true)
    public List<DanhMucDto> dsTacGia() {
        return tacGiaRepo.findAllByOrderByHoTenAsc().stream().map(t -> new DanhMucDto(t.getId(), t.getHoTen(), null)).toList();
    }

    @Transactional
    public DanhMucDto luuTheLoai(Integer id, DanhMucRequest req) {
        String ten = req.ten().trim();
        if (id == null ? theLoaiRepo.existsByTenIgnoreCase(ten) : theLoaiRepo.existsByTenIgnoreCaseAndIdNot(ten, id))
            throw new LoiNghiepVu("TRUNG_TEN", "Thể loại \"" + ten + "\" đã có");
        TheLoai t = id == null ? new TheLoai()
                : theLoaiRepo.findById(id).orElseThrow(() -> LoiNghiepVu.khongTimThay("Không tìm thấy thể loại"));
        t.setTen(ten);
        t.setMaDdc(req.phu());
        theLoaiRepo.saveAndFlush(t);
        return new DanhMucDto(t.getId(), t.getTen(), t.getMaDdc());
    }

    @Transactional
    public DanhMucDto luuNhaXuatBan(Integer id, DanhMucRequest req) {
        String ten = req.ten().trim();
        if (id == null ? nxbRepo.existsByTenIgnoreCase(ten) : nxbRepo.existsByTenIgnoreCaseAndIdNot(ten, id))
            throw new LoiNghiepVu("TRUNG_TEN", "Nhà xuất bản \"" + ten + "\" đã có");
        NhaXuatBan n = id == null ? new NhaXuatBan()
                : nxbRepo.findById(id).orElseThrow(() -> LoiNghiepVu.khongTimThay("Không tìm thấy nhà xuất bản"));
        n.setTen(ten);
        n.setDiaChi(req.phu());
        nxbRepo.saveAndFlush(n);
        return new DanhMucDto(n.getId(), n.getTen(), n.getDiaChi());
    }

    @Transactional
    public DanhMucDto luuTacGia(Integer id, DanhMucRequest req) {
        TacGia t = id == null ? new TacGia()
                : tacGiaRepo.findById(id).orElseThrow(() -> LoiNghiepVu.khongTimThay("Không tìm thấy tác giả"));
        t.setHoTen(req.ten().trim());
        tacGiaRepo.saveAndFlush(t);
        return new DanhMucDto(t.getId(), t.getHoTen(), null);
    }

    // Xóa danh mục đang được đầu sách dùng sẽ bị khóa ngoại chặn, bộ xử lý lỗi chung trả về mã 409
    @Transactional
    public void xoaDanhMuc(String loai, Integer id) {
        switch (loai) {
            case "the-loai" -> { theLoaiRepo.deleteById(id); theLoaiRepo.flush(); }
            case "nha-xuat-ban" -> { nxbRepo.deleteById(id); nxbRepo.flush(); }
            case "tac-gia" -> { tacGiaRepo.deleteById(id); tacGiaRepo.flush(); }
            default -> throw LoiNghiepVu.khongTimThay("Không có loại danh mục " + loai);
        }
    }

    private void ganThongTin(DauSach ds, DauSachRequest req, String isbn) {
        LoaiTaiLieu loai;
        try {
            loai = LoaiTaiLieu.valueOf(req.loaiTaiLieu());
        } catch (IllegalArgumentException e) {
            throw new LoiNghiepVu("LOAI_SAI", "Loại tài liệu không hợp lệ");
        }
        ds.setIsbn(isbn);
        ds.setNhanDe(req.nhanDe().trim());
        ds.setNamXb(req.namXb());
        ds.setLanXb(req.lanXb() == null ? 1 : req.lanXb());
        ds.setSoTrang(req.soTrang());
        ds.setGiaBia(req.giaBia());
        ds.setMaDdc(req.maDdc());
        ds.setLoaiTaiLieu(loai);
        ds.setTheLoai(theLoaiRepo.findById(req.theLoaiId())
                .orElseThrow(() -> LoiNghiepVu.khongTimThay("Không tìm thấy thể loại")));
        ds.setNhaXuatBan(nxbRepo.findById(req.nxbId())
                .orElseThrow(() -> LoiNghiepVu.khongTimThay("Không tìm thấy nhà xuất bản")));
        Set<TacGia> dsTacGia = new LinkedHashSet<>(tacGiaRepo.findAllById(req.dsTacGiaId()));
        if (dsTacGia.size() != new LinkedHashSet<>(req.dsTacGiaId()).size())
            throw LoiNghiepVu.khongTimThay("Có tác giả không tồn tại");
        ds.getDsTacGia().clear();
        ds.getDsTacGia().addAll(dsTacGia);
    }

    private String chuanHoaIsbn(String isbn) {
        String s = isbn.replace("-", "").replace(" ", "").trim();
        if (!isbn13HopLe(s))
            throw new LoiNghiepVu("ISBN_SAI", "ISBN-13 không hợp lệ (sai độ dài hoặc sai số kiểm tra)");
        return s;
    }

    private String tienToMaVach(LoaiTaiLieu loai) {
        return switch (loai) {
            case GIAO_TRINH -> "GT";
            case SACH_TK -> "ST";
            case THAM_KHAO -> "TK";
        };
    }

    private DauSach layDauSach(Long id) {
        return dauSachRepo.findById(id).orElseThrow(() -> LoiNghiepVu.khongTimThay("Không tìm thấy đầu sách"));
    }
}
