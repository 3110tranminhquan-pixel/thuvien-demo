import { useEffect, useState } from 'react';
import { layLoi } from '../../api/ApiClient';
import { dsLoaiBanDocQuanTri, dsThamSo, suaLoaiBanDoc, suaThamSo } from '../../api/thuVienApi';
import ThongBao from '../../components/ThongBao';

// FR-35: sửa tham số quy định và hạn mức theo loại bạn đọc. Mỗi lần sửa đều ghi nhật ký.
export default function ThamSo() {
  const [dsTs, setDsTs] = useState([]);
  const [dsLoai, setDsLoai] = useState([]);
  const [loi, setLoi] = useState('');
  const [ok, setOk] = useState('');

  const tai = () => {
    dsThamSo().then(setDsTs).catch((e) => setLoi(layLoi(e)));
    dsLoaiBanDocQuanTri().then(setDsLoai);
  };
  useEffect(() => { tai(); }, []);

  const luuTs = async (ts) => {
    setLoi(''); setOk('');
    try { await suaThamSo(ts.ma, ts.giaTri); setOk(`Đã lưu ${ts.ma}`); tai(); } catch (e) { setLoi(layLoi(e)); }
  };

  const luuLoai = async (l) => {
    setLoi(''); setOk('');
    try {
      await suaLoaiBanDoc(l.id, {
        ...l, soSachToiDa: Number(l.soSachToiDa), soNgayMuon: Number(l.soNgayMuon),
        soLanGiaHan: Number(l.soLanGiaHan), soNgayGiaHan: Number(l.soNgayGiaHan),
      });
      setOk(`Đã lưu hạn mức "${l.tenLoai}"`);
      tai();
    } catch (e) { setLoi(layLoi(e)); }
  };

  const doiTs = (ma, giaTri) => setDsTs((ds) => ds.map((t) => (t.ma === ma ? { ...t, giaTri } : t)));
  const doiLoai = (id, truong, giaTri) => setDsLoai((ds) => ds.map((l) => (l.id === id ? { ...l, [truong]: giaTri } : l)));

  return (
    <div>
      <ThongBao loi={loi} ok={ok} />
      <div className="the">
        <h3 style={{ marginTop: 0 }}>Hạn mức theo loại bạn đọc</h3>
        <table>
          <thead><tr><th>Loại</th><th>Số sách tối đa</th><th>Số ngày mượn</th><th>Số lần gia hạn</th><th>Số ngày mỗi lần gia hạn</th><th></th></tr></thead>
          <tbody>
            {dsLoai.map((l) => (
              <tr key={l.id}>
                <td>{l.tenLoai}</td>
                {['soSachToiDa', 'soNgayMuon', 'soLanGiaHan', 'soNgayGiaHan'].map((tr) => (
                  <td key={tr}><input type="number" style={{ width: 80 }} value={l[tr]} onChange={(e) => doiLoai(l.id, tr, e.target.value)} /></td>
                ))}
                <td><button className="nut nut-nho" onClick={() => luuLoai(l)}>Lưu</button></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <div className="the">
        <h3 style={{ marginTop: 0 }}>Tham số quy định</h3>
        <table>
          <thead><tr><th>Mã</th><th>Mô tả</th><th>Giá trị</th><th></th></tr></thead>
          <tbody>
            {dsTs.map((t) => (
              <tr key={t.ma}>
                <td><code>{t.ma}</code></td>
                <td>{t.moTa}</td>
                <td><input style={{ width: 110 }} value={t.giaTri} onChange={(e) => doiTs(t.ma, e.target.value)} /></td>
                <td><button className="nut nut-nho" onClick={() => luuTs(t)}>Lưu</button></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
