import { useState } from 'react';
import { layLoi } from '../../api/ApiClient';
import { kiemTraSach, taoPhieuMuon, timBanDocTheoThe } from '../../api/thuVienApi';
import { ngay, tien } from '../../components/dinhDang';
import ThongBao from '../../components/ThongBao';
import { useBarcodeInput } from '../../hooks/useBarcodeInput';

const LA_MA_THE = /^BD\d{6}$/i;

// Màn hình Mượn sách (UC06, FR-16, FR-17). Bố cục hai cột như bản mẫu Hình 3.28.
// Thủ thư chỉ việc quét liên tục: quét thẻ trước, rồi quét từng cuốn, cuối cùng nhấn Enter ở ô trống để xác nhận.
export default function MuonSach() {
  const [banDoc, setBanDoc] = useState(null);
  const [dsSach, setDsSach] = useState([]);
  const [loi, setLoi] = useState('');
  const [ok, setOk] = useState('');
  const [dangGui, setDangGui] = useState(false);

  const lamMoi = () => {
    setBanDoc(null);
    setDsSach([]);
    setLoi('');
  };

  const xuLyQuet = async (ma) => {
    setLoi(''); setOk('');
    try {
      if (LA_MA_THE.test(ma)) {
        const bd = await timBanDocTheoThe(ma.toUpperCase());
        setBanDoc(bd);
        setDsSach([]);
        return;
      }
      if (!banDoc) { setLoi('Hãy quét thẻ bạn đọc trước'); return; }
      if (!banDoc.duocMuon) return;   // lý do đã hiện ở ô đỏ bên dưới
      const sach = await kiemTraSach(banDoc.id, ma, dsSach.map((s) => s.maVach));
      setDsSach((ds) => [...ds, sach]);
    } catch (e) {
      setLoi(layLoi(e));
    }
  };

  const { inputRef, handleKeyDown, handleBlur } = useBarcodeInput(xuLyQuet);

  const xacNhan = async () => {
    if (!banDoc || dsSach.length === 0 || dangGui) return;
    setDangGui(true); setLoi('');
    try {
      const pm = await taoPhieuMuon(banDoc.id, dsSach.map((s) => s.maVach));
      setOk(`Đã lập phiếu mượn số ${pm.id} cho ${pm.hoTen}: ${pm.dsChiTiet.length} cuốn, hạn trả ${ngay(pm.dsChiTiet[0].hanTra)}`);
      lamMoi();
    } catch (e) {
      setLoi(layLoi(e));
    } finally {
      setDangGui(false);
      inputRef.current?.focus();
    }
  };

  // Ô nhập đang trống mà nhấn Enter thì coi như xác nhận mượn
  const khiNhanPhim = (e) => {
    if (e.key === 'Enter' && e.target.value.trim() === '') xacNhan();
    else handleKeyDown(e);
  };

  const soSauKhiMuon = banDoc ? banDoc.soDangMuon + dsSach.length : 0;

  return (
    <div className="hai-cot">
      <div className="cot-trai">
        <div className="the">
          <div className="nhan-o">Quét thẻ bạn đọc hoặc mã vạch sách</div>
          <input ref={inputRef} className="o-quet" placeholder="Đưa máy quét vào mã..."
                 onKeyDown={khiNhanPhim} onBlur={handleBlur} />
          <div className="ghi-chu">Nhấn Enter ở ô trống để xác nhận mượn</div>
        </div>

        {banDoc && (
          <div className="the">
            <div className="nhan-o">Bạn đọc</div>
            <div className="ten-lon">{banDoc.hoTen}</div>
            <div className="dong"><span>Mã thẻ</span><span>{banDoc.maThe}</span></div>
            <div className="dong"><span>Loại bạn đọc</span><span>{banDoc.tenLoai}</span></div>
            <div className="dong"><span>Đơn vị</span><span>{banDoc.donVi}</span></div>
            <div className="dong"><span>Hạn thẻ</span><span>{ngay(banDoc.ngayHetHan)}</span></div>
            {banDoc.tongNo > 0 && <div className="dong"><span>Tiền phạt chưa nộp</span><span className="chu-do">{tien(banDoc.tongNo)}</span></div>}
            <div className="nhan-o" style={{ marginTop: 16 }}>Đang mượn</div>
            <div className="thanh"><div style={{ width: `${Math.min(100, (soSauKhiMuon / banDoc.soToiDa) * 100)}%` }} /></div>
            <b>{soSauKhiMuon}/{banDoc.soToiDa} cuốn</b>
          </div>
        )}

        {banDoc && (banDoc.duocMuon ? (
          <div className="hop-ok">
            <b>✓ Được mượn</b>
            <div>Còn được mượn thêm {Math.max(0, banDoc.soToiDa - soSauKhiMuon)} cuốn.</div>
          </div>
        ) : (
          <div className="hop-loi"><b>✕ Không được mượn</b><div>{banDoc.lyDoKhongDuocMuon}</div></div>
        ))}
        <ThongBao loi={loi} ok={ok} />
      </div>

      <div className="the cot-phai">
        <div className="hang giua-hai-ben">
          <h3 style={{ margin: 0 }}>Sách vừa quét ({dsSach.length})</h3>
          {banDoc && <span className="mo">Hạn trả tính theo loại bạn đọc</span>}
        </div>
        <table>
          <thead><tr><th>#</th><th>Nhan đề</th><th>Mã vạch</th><th>Hạn trả dự kiến</th><th></th></tr></thead>
          <tbody>
            {dsSach.map((s, i) => (
              <tr key={s.maVach}>
                <td>{i + 1}</td>
                <td>{s.nhanDe}</td>
                <td>{s.maVach}</td>
                <td>{ngay(s.hanTraDuKien)}</td>
                <td><button className="nut-bo" title="Bỏ cuốn này"
                            onClick={() => setDsSach((ds) => ds.filter((x) => x.maVach !== s.maVach))}>✕</button></td>
              </tr>
            ))}
          </tbody>
        </table>
        <div className="chan-cot">
          <button className="nut nut-phu" onClick={lamMoi}>Hủy phiên</button>
          <button className="nut nut-lon" disabled={!banDoc || dsSach.length === 0 || dangGui} onClick={xacNhan}>
            Xác nhận mượn <small>(Enter)</small>
          </button>
        </div>
      </div>
    </div>
  );
}
