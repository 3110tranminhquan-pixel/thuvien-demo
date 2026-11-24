import { useEffect, useState } from 'react';
import { layLoi } from '../../api/ApiClient';
import { chayTacVu, dsNhatKy } from '../../api/thuVienApi';
import { ngayGio } from '../../components/dinhDang';
import ThongBao from '../../components/ThongBao';

// FR-36: xem nhật ký các thao tác quan trọng
export default function NhatKy() {
  const [ds, setDs] = useState([]);
  const [loi, setLoi] = useState('');
  const [ok, setOk] = useState('');

  const tai = () => dsNhatKy().then(setDs).catch((e) => setLoi(layLoi(e)));
  useEffect(() => { tai(); }, []);

  const chay = async () => {
    setLoi(''); setOk('');
    try { const r = await chayTacVu(); setOk(r.message); tai(); } catch (e) { setLoi(layLoi(e)); }
  };

  return (
    <div>
      <div className="hang giua-hai-ben">
        <span className="mo">300 thao tác gần nhất</span>
        <button className="nut nut-phu" onClick={chay} title="Đánh dấu quá hạn, xét khóa thẻ, hủy lượt giữ hết hạn">
          Chạy tác vụ định kỳ ngay
        </button>
      </div>
      <ThongBao loi={loi} ok={ok} />
      <div className="the">
        <table>
          <thead><tr><th>Thời gian</th><th>Người thực hiện</th><th>Hành động</th><th>Đối tượng</th><th>Chi tiết</th><th>IP</th></tr></thead>
          <tbody>
            {ds.map((n) => (
              <tr key={n.id}>
                <td>{ngayGio(n.thoiGian)}</td>
                <td>{n.nguoiDung}</td>
                <td><code>{n.hanhDong}</code></td>
                <td>{n.doiTuong}</td>
                <td>{n.maDoiTuong}</td>
                <td>{n.diaChiIp}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
