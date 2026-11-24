import { useEffect, useState } from 'react';
import { layLoi } from '../../api/ApiClient';
import { dsPhieuPhat, huyPhieuPhat, thanhToanPhat } from '../../api/thuVienApi';
import { ngayGio, tien, TEN_TRANG_THAI_PHAT, TEN_VI_PHAM } from '../../components/dinhDang';
import HopThoai from '../../components/HopThoai';
import Nhan from '../../components/Nhan';
import ThongBao from '../../components/ThongBao';
import { useAuth } from '../../hooks/useAuth';

// Danh sách phiếu phạt. Thủ thư ghi nhận thu tiền và in biên lai (FR-26),
// quản trị viên hủy phiếu lập nhầm kèm lý do (FR-25).
export default function PhieuPhat() {
  const { nguoiDung } = useAuth();
  const laQuanTri = nguoiDung?.vaiTro === 'QUAN_TRI';
  const [ds, setDs] = useState([]);
  const [trangThai, setTrangThai] = useState('CHUA_THANH_TOAN');
  const [tuKhoa, setTuKhoa] = useState('');
  const [bienLai, setBienLai] = useState(null);
  const [loi, setLoi] = useState('');
  const [ok, setOk] = useState('');

  const tai = () => dsPhieuPhat(trangThai, tuKhoa).then(setDs).catch((e) => setLoi(layLoi(e)));
  useEffect(() => { tai(); }, [trangThai]);

  const thu = async (p) => {
    setLoi(''); setOk('');
    try {
      const daThu = await thanhToanPhat(p.id);
      setBienLai(daThu);
      tai();
    } catch (e) { setLoi(layLoi(e)); }
  };

  const huy = async (p) => {
    const lyDo = window.prompt(`Lý do hủy phiếu phạt số ${p.id}:`);
    if (lyDo === null) return;
    setLoi(''); setOk('');
    try {
      await huyPhieuPhat(p.id, lyDo);
      setOk(`Đã hủy phiếu số ${p.id}`);
      tai();
    } catch (e) { setLoi(layLoi(e)); }
  };

  const tong = ds.reduce((t, p) => t + Number(p.soTien), 0);

  return (
    <div>
      <div className="hang">
        <select value={trangThai} onChange={(e) => setTrangThai(e.target.value)}>
          <option value="">Tất cả</option>
          {Object.entries(TEN_TRANG_THAI_PHAT).map(([ma, ten]) => <option key={ma} value={ma}>{ten}</option>)}
        </select>
        <form className="hang" onSubmit={(e) => { e.preventDefault(); tai(); }}>
          <input placeholder="Mã thẻ hoặc tên bạn đọc" value={tuKhoa} onChange={(e) => setTuKhoa(e.target.value)} />
          <button className="nut nut-phu">Lọc</button>
        </form>
      </div>
      <ThongBao loi={loi} ok={ok} />
      <div className="the">
        <div className="mo">{ds.length} phiếu, tổng {tien(tong)}</div>
        <table>
          <thead><tr><th>Số</th><th>Ngày lập</th><th>Bạn đọc</th><th>Sách</th><th>Vi phạm</th><th>Số tiền</th><th>Trạng thái</th><th></th></tr></thead>
          <tbody>
            {ds.map((p) => (
              <tr key={p.id}>
                <td>{p.id}</td>
                <td>{ngayGio(p.ngayLap)}</td>
                <td>{p.hoTen}<div className="mo">{p.maThe}</div></td>
                <td>{p.nhanDe}<div className="mo">{p.maVach}</div></td>
                <td>{TEN_VI_PHAM[p.loaiViPham]}</td>
                <td>{tien(p.soTien)}</td>
                <td>
                  <Nhan ma={p.trangThai} ten={TEN_TRANG_THAI_PHAT[p.trangThai]} />
                  {p.lyDoHuy && <div className="mo">Lý do: {p.lyDoHuy}</div>}
                </td>
                <td className="nut-nhom">
                  {p.trangThai === 'CHUA_THANH_TOAN' && !laQuanTri && <button className="nut nut-nho" onClick={() => thu(p)}>Thu tiền</button>}
                  {p.trangThai === 'CHUA_THANH_TOAN' && laQuanTri && <button className="nut nut-do nut-nho" onClick={() => huy(p)}>Hủy phiếu</button>}
                  {p.trangThai === 'DA_THANH_TOAN' && <button className="nut nut-phu nut-nho" onClick={() => setBienLai(p)}>Biên lai</button>}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {bienLai && (
        <HopThoai tieuDe="Biên lai thu tiền phạt" onDong={() => setBienLai(null)}>
          <div className="bien-lai">
            <div className="giua"><b>THƯ VIỆN</b><br />BIÊN LAI THU TIỀN PHẠT<br />Số: {bienLai.id}</div>
            <div className="dong"><span>Bạn đọc</span><span>{bienLai.hoTen} ({bienLai.maThe})</span></div>
            <div className="dong"><span>Sách</span><span>{bienLai.nhanDe}</span></div>
            <div className="dong"><span>Mã vạch</span><span>{bienLai.maVach}</span></div>
            <div className="dong"><span>Lý do</span><span>{TEN_VI_PHAM[bienLai.loaiViPham]}</span></div>
            <div className="dong tong"><span>Số tiền</span><span>{tien(bienLai.soTien)}</span></div>
            <div className="dong"><span>Thời điểm thu</span><span>{ngayGio(bienLai.ngayThu)}</span></div>
            <div className="dong"><span>Người thu</span><span>{bienLai.nguoiThu}</span></div>
          </div>
          <div className="chan-cot khong-in">
            <button className="nut" onClick={() => window.print()}>In biên lai</button>
          </div>
        </HopThoai>
      )}
    </div>
  );
}
