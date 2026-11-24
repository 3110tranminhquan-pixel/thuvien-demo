package vn.nhom.thuvien.dto;

import vn.nhom.thuvien.entity.BanDoc;
import vn.nhom.thuvien.entity.BanSach;
import vn.nhom.thuvien.entity.ChiTietMuon;
import vn.nhom.thuvien.entity.DatTruoc;
import vn.nhom.thuvien.entity.DauSach;
import vn.nhom.thuvien.entity.KetQuaKiemTra;
import vn.nhom.thuvien.entity.NguoiDung;
import vn.nhom.thuvien.entity.NhatKy;
import vn.nhom.thuvien.entity.PhieuMuon;
import vn.nhom.thuvien.entity.PhieuPhat;
import vn.nhom.thuvien.entity.TacGia;

import java.time.LocalDate;
import java.util.List;

// Chuyển entity sang DTO để trả qua API, không trả thẳng entity ra ngoài
public final class ChuyenDoi {

    private ChuyenDoi() {
    }

    public static DauSachTomTat tomTat(DauSach ds) {
        return new DauSachTomTat(ds.getId(), ds.getIsbn(), ds.getNhanDe(), ds.tenCacTacGia(), ds.getNamXb(),
                ds.getTheLoai() != null ? ds.getTheLoai().getTen() : null,
                ds.getLoaiTaiLieu().name(), ds.getTrangThai().name(),
                ds.soBanSanSang(), ds.soBanHienCo());
    }

    public static BanSachDto banSach(BanSach bs) {
        return new BanSachDto(bs.getId(), bs.getMaVach(), bs.getViTriKe(), bs.getTrangThai().name(),
                bs.getTrangThai().getTen(), bs.getNgayNhap(), bs.getGhiChu());
    }

    public static DauSachChiTiet chiTiet(DauSach ds, long soNguoiCho) {
        List<Integer> dsTacGiaId = ds.getDsTacGia().stream().map(TacGia::getId).toList();
        List<BanSachDto> dsBan = ds.getDsBanSach().stream().map(ChuyenDoi::banSach).toList();
        return new DauSachChiTiet(ds.getId(), ds.getIsbn(), ds.getNhanDe(), ds.tenCacTacGia(), dsTacGiaId,
                ds.getNamXb(), ds.getLanXb(), ds.getSoTrang(), ds.getGiaBia(), ds.getMaDdc(),
                ds.getLoaiTaiLieu().name(), ds.getTrangThai().name(),
                ds.getTheLoai().getId(), ds.getTheLoai().getTen(),
                ds.getNhaXuatBan().getId(), ds.getNhaXuatBan().getTen(),
                ds.soBanSanSang(), ds.soBanHienCo(), soNguoiCho, ds.choMuonVe(), dsBan);
    }

    public static BanDocDto banDoc(BanDoc bd) {
        LocalDate homNay = LocalDate.now();
        KetQuaKiemTra kq = bd.coTheMuon(homNay);
        return new BanDocDto(bd.getId(), bd.getMaThe(), bd.getHoTen(), bd.getNgaySinh(), bd.getEmail(),
                bd.getSoDienThoai(), bd.getDonVi(), bd.getNgayCap(), bd.getNgayHetHan(),
                bd.getTrangThai().name(), bd.getLyDoKhoa(), Boolean.TRUE.equals(bd.getKhoaThuCong()),
                bd.getLoai().getId(), bd.getLoai().getTenLoai(),
                bd.soSachDangMuon(), bd.getLoai().getSoSachToiDa(), bd.tongNo(),
                kq.isDat(), kq.getLyDo());
    }

    public static LuotMuonDto luotMuon(ChiTietMuon ct) {
        PhieuMuon pm = ct.getPhieuMuon();
        BanSach bs = ct.getBanSach();
        int tre = ct.getNgayTra() == null
                ? ct.soNgayTre(LocalDate.now())
                : ct.soNgayTre(ct.getNgayTra().toLocalDate());
        return new LuotMuonDto(ct.getId(), pm.getId(), bs.getMaVach(), bs.getDauSach().getId(),
                bs.getDauSach().getNhanDe(), pm.getNgayMuon(), ct.getHanTra(), ct.getNgayTra(),
                ct.getSoLanGiaHan(), tre,
                ct.getTinhTrangKhiTra() != null ? ct.getTinhTrangKhiTra().name() : null,
                pm.getBanDoc().getMaThe(), pm.getBanDoc().getHoTen());
    }

    public static DatTruocDto datTruoc(DatTruoc dt, Long thuTu) {
        return new DatTruocDto(dt.getId(), dt.getDauSach().getId(), dt.getDauSach().getNhanDe(),
                dt.getThoiDiemDat(), dt.getTrangThai().name(), dt.getHanGiu(),
                dt.getBanSach() != null ? dt.getBanSach().getMaVach() : null, thuTu);
    }

    public static PhieuPhatDto phieuPhat(PhieuPhat pp) {
        ChiTietMuon ct = pp.getChiTietMuon();
        BanDoc bd = ct.getPhieuMuon().getBanDoc();
        return new PhieuPhatDto(pp.getId(), ct.getId(), bd.getMaThe(), bd.getHoTen(),
                ct.getBanSach().getDauSach().getNhanDe(), ct.getBanSach().getMaVach(),
                pp.getLoaiViPham().name(), pp.getSoTien(), pp.getTrangThai().name(), pp.getNgayLap(),
                ten(pp.getNguoiLap()), pp.getNgayThu(), ten(pp.getNguoiThu()), pp.getLyDoHuy());
    }

    public static PhieuMuonDto phieuMuon(PhieuMuon pm) {
        return new PhieuMuonDto(pm.getId(), pm.getBanDoc().getMaThe(), pm.getBanDoc().getHoTen(),
                pm.getNgayMuon(), pm.getDsChiTiet().stream().map(ChuyenDoi::luotMuon).toList());
    }

    public static TaiKhoanDto taiKhoan(NguoiDung nd) {
        return new TaiKhoanDto(nd.getId(), nd.getTenDangNhap(), nd.getVaiTro().getId(), nd.getVaiTro().getTen(),
                nd.getTrangThai().name(), nd.getKhoaDen());
    }

    public static NhatKyDto nhatKy(NhatKy nk) {
        return new NhatKyDto(nk.getId(), nk.getThoiGian(), ten(nk.getNguoiDung()), nk.getHanhDong(),
                nk.getDoiTuong(), nk.getMaDoiTuong(), nk.getDiaChiIp());
    }

    private static String ten(NguoiDung nd) {
        return nd == null ? null : nd.getTenDangNhap();
    }
}
