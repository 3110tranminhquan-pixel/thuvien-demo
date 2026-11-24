import { useState } from 'react';
import { layLoi } from '../../api/ApiClient';
import { giaHan, hoSoBanDoc, timBanDocTheoThe } from '../../api/thuVienApi';
import { ngay } from '../../components/dinhDang';
import ThongBao from '../../components/ThongBao';

// Thủ thư gia hạn tại quầy cho bạn đọc (FR-20)
export default function GiaHanTaiQuay() {
  const [maThe, setMaThe] = useState('');
  const [hs, setHs] = useState(null);
  const [loi, setLoi] = useState('');
  const [ok, setOk] = useState('');

  const tai = async (ma) => {
    const bd = await timBanDocTheoThe(ma);
    setHs(await hoSoBanDoc(bd.id));
  };

  const tim = async (e) => {
    e.preventDefault();
    setLoi(''); setOk('');
    try { await tai(maThe.trim()); } catch (err) { setHs(null); setLoi(layLoi(err)); }
  };

  const giaHanMot = async (m) => {
    setLoi(''); setOk('');
    try {
      const kq = await giaHan(m.chiTietId);
      setOk(`Đã gia hạn "${m.nhanDe}", hạn trả mới ${ngay(kq.hanTra)}`);
      await tai(hs.banDoc.maThe);
    } catch (err) {
      setLoi(layLoi(err));
    }
  };

  return (
    <div>
      <form className="the hang" onSubmit={tim}>
        <input placeholder="Quét hoặc nhập mã thẻ bạn đọc" value={maThe} onChange={(e) => setMaThe(e.target.value)} autoFocus />
        <button className="nut">Tìm</button>
      </form>
      <ThongBao loi={loi} ok={ok} />
      {hs && (
        <div className="the">
          <h3 style={{ marginTop: 0 }}>{hs.banDoc.hoTen} ({hs.banDoc.maThe})</h3>
          <table>
            <thead><tr><th>Nhan đề</th><th>Mã vạch</th><th>Hạn trả</th><th>Đã gia hạn</th><th></th></tr></thead>
            <tbody>
              {hs.dangMuon.map((m) => (
                <tr key={m.chiTietId}>
                  <td>{m.nhanDe}</td>
                  <td>{m.maVach}</td>
                  <td>{ngay(m.hanTra)} {m.soNgayTre > 0 && <span className="chu-do">(trễ {m.soNgayTre} ngày)</span>}</td>
                  <td>{m.soLanGiaHan} lần</td>
                  <td><button className="nut nut-nho" onClick={() => giaHanMot(m)}>Gia hạn</button></td>
                </tr>
              ))}
              {hs.dangMuon.length === 0 && <tr><td colSpan={5} className="mo">Bạn đọc không mượn cuốn nào</td></tr>}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
