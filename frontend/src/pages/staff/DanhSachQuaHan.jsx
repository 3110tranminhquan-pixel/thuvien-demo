import { useEffect, useState } from 'react';
import { layLoi } from '../../api/ApiClient';
import { dsQuaHan } from '../../api/thuVienApi';
import { ngay } from '../../components/dinhDang';
import ThongBao from '../../components/ThongBao';

// FR-32: danh sách bạn đọc đang có sách quá hạn
export default function DanhSachQuaHan() {
  const [ds, setDs] = useState([]);
  const [loi, setLoi] = useState('');

  useEffect(() => { dsQuaHan().then(setDs).catch((e) => setLoi(layLoi(e))); }, []);

  return (
    <div className="the">
      <ThongBao loi={loi} />
      <div className="mo">{ds.length} cuốn đang quá hạn</div>
      <table>
        <thead><tr><th>Mã thẻ</th><th>Bạn đọc</th><th>Nhan đề</th><th>Mã vạch</th><th>Ngày mượn</th><th>Hạn trả</th><th>Số ngày trễ</th></tr></thead>
        <tbody>
          {ds.map((m) => (
            <tr key={m.chiTietId}>
              <td>{m.maThe}</td>
              <td>{m.hoTen}</td>
              <td>{m.nhanDe}</td>
              <td>{m.maVach}</td>
              <td>{ngay(m.ngayMuon)}</td>
              <td>{ngay(m.hanTra)}</td>
              <td className={m.soNgayTre > 30 ? 'chu-do' : ''}><b>{m.soNgayTre}</b></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
