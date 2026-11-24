package vn.nhom.thuvien.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom.thuvien.dto.LoaiBanDocDto;
import vn.nhom.thuvien.dto.ThamSoDto;
import vn.nhom.thuvien.entity.LoaiBanDoc;
import vn.nhom.thuvien.entity.ThamSo;
import vn.nhom.thuvien.exception.LoiNghiepVu;
import vn.nhom.thuvien.repository.LoaiBanDocRepository;
import vn.nhom.thuvien.repository.ThamSoRepository;

import java.math.BigDecimal;
import java.util.List;

// Các con số quy định nằm trong bảng tham_so_he_thong, không viết cứng trong code (FR-35)
@Service
public class ThamSoService {

    private final ThamSoRepository thamSoRepo;
    private final LoaiBanDocRepository loaiRepo;
    private final NhatKyService nhatKy;

    public ThamSoService(ThamSoRepository thamSoRepo, LoaiBanDocRepository loaiRepo, NhatKyService nhatKy) {
        this.thamSoRepo = thamSoRepo;
        this.loaiRepo = loaiRepo;
        this.nhatKy = nhatKy;
    }

    public BigDecimal laySo(String ma) {
        ThamSo ts = thamSoRepo.findById(ma)
                .orElseThrow(() -> new IllegalStateException("Thiếu tham số " + ma + " trong bảng tham_so_he_thong"));
        return new BigDecimal(ts.getGiaTri().trim());
    }

    @Transactional(readOnly = true)
    public List<ThamSoDto> danhSach() {
        return thamSoRepo.findAllByOrderByMaAsc().stream()
                .map(t -> new ThamSoDto(t.getMa(), t.getGiaTri(), t.getMoTa())).toList();
    }

    @Transactional
    public ThamSoDto capNhat(String ma, String giaTri) {
        ThamSo ts = thamSoRepo.findById(ma)
                .orElseThrow(() -> LoiNghiepVu.khongTimThay("Không có tham số " + ma));
        BigDecimal so;
        try {
            so = new BigDecimal(giaTri.trim());
        } catch (NumberFormatException e) {
            throw new LoiNghiepVu("GIA_TRI_SAI", "Giá trị của tham số phải là số");
        }
        if (so.signum() < 0) {
            throw new LoiNghiepVu("GIA_TRI_SAI", "Giá trị của tham số không được âm");
        }
        String cu = ts.getGiaTri();
        ts.setGiaTri(so.toPlainString());
        nhatKy.ghi("SUA_THAM_SO", "tham_so_he_thong", ma + ": " + cu + " -> " + ts.getGiaTri());
        return new ThamSoDto(ts.getMa(), ts.getGiaTri(), ts.getMoTa());
    }

    @Transactional(readOnly = true)
    public List<LoaiBanDocDto> dsLoaiBanDoc() {
        return loaiRepo.findAllByOrderByIdAsc().stream().map(ThamSoService::sangDto).toList();
    }

    @Transactional
    public LoaiBanDocDto capNhatLoai(Integer id, LoaiBanDocDto req) {
        LoaiBanDoc l = loaiRepo.findById(id)
                .orElseThrow(() -> LoiNghiepVu.khongTimThay("Không có loại bạn đọc này"));
        l.setSoSachToiDa(req.soSachToiDa());
        l.setSoNgayMuon(req.soNgayMuon());
        l.setSoLanGiaHan(req.soLanGiaHan());
        l.setSoNgayGiaHan(req.soNgayGiaHan());
        nhatKy.ghi("SUA_THAM_SO", "loai_ban_doc", String.valueOf(id));
        return sangDto(l);
    }

    private static LoaiBanDocDto sangDto(LoaiBanDoc l) {
        return new LoaiBanDocDto(l.getId(), l.getTenLoai(), l.getSoSachToiDa(), l.getSoNgayMuon(),
                l.getSoLanGiaHan(), l.getSoNgayGiaHan());
    }
}
