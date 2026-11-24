import { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { layLoi } from '../../api/ApiClient';
import { layDanhMuc, timSach } from '../../api/thuVienApi';
import ThongBao from '../../components/ThongBao';

// Trang chủ kiêm trang kết quả tìm kiếm (FR-04). Điều kiện tìm nằm trên địa chỉ trang
// nên bấm quay lại vẫn giữ nguyên kết quả.
export default function TrangChu() {
  const [thamSo, setThamSo] = useSearchParams();
  const [tuKhoa, setTuKhoa] = useState(thamSo.get('tuKhoa') || '');
  const [dsTheLoai, setDsTheLoai] = useState([]);
  const [kq, setKq] = useState(null);
  const [loi, setLoi] = useState('');

  const theLoai = thamSo.get('theLoai') || '';
  const nam = thamSo.get('nam') || '';
  const conSach = thamSo.get('conSach') === 'true';
  const trang = Number(thamSo.get('trang') || 0);

  useEffect(() => { layDanhMuc('the-loai').then(setDsTheLoai).catch(() => {}); }, []);

  useEffect(() => {
    setLoi('');
    timSach({
      tuKhoa: thamSo.get('tuKhoa') || '',
      theLoai: theLoai || undefined,
      nam: nam || undefined,
      conSach,
      trang,
    }).then(setKq).catch((e) => setLoi(layLoi(e)));
  }, [thamSo]);

  const doi = (moi) => {
    const p = Object.fromEntries(thamSo.entries());
    Object.assign(p, moi);
    if (!('trang' in moi)) p.trang = '0';
    Object.keys(p).forEach((k) => { if (p[k] === '' || p[k] === undefined) delete p[k]; });
    setThamSo(p);
  };

  return (
    <div className="trang">
      <div className="o-tim-lon">
        <h2>Tra cứu tài liệu</h2>
        <form onSubmit={(e) => { e.preventDefault(); doi({ tuKhoa }); }} className="hang">
          <input className="o-tim" placeholder="Nhập nhan đề, tác giả hoặc ISBN..." value={tuKhoa}
                 onChange={(e) => setTuKhoa(e.target.value)} />
          <button className="nut">Tìm</button>
        </form>
        <div className="hang bo-loc">
          <select value={theLoai} onChange={(e) => doi({ theLoai: e.target.value })}>
            <option value="">Tất cả thể loại</option>
            {dsTheLoai.map((t) => <option key={t.id} value={t.id}>{t.ten}</option>)}
          </select>
          <input type="number" placeholder="Năm xuất bản" value={nam} style={{ width: 140 }}
                 onChange={(e) => doi({ nam: e.target.value })} />
          <label className="chon">
            <input type="checkbox" checked={conSach} onChange={(e) => doi({ conSach: e.target.checked ? 'true' : '' })} />
            Chỉ hiện sách còn
          </label>
        </div>
      </div>

      <ThongBao loi={loi} />
      {kq && (
        <div className="the">
          <div className="mo">Tìm thấy {kq.tongSo} đầu sách</div>
          <table>
            <thead>
              <tr><th>Nhan đề</th><th>Tác giả</th><th>Năm</th><th>Thể loại</th><th>Sẵn sàng</th></tr>
            </thead>
            <tbody>
              {kq.dsKetQua.map((s) => (
                <tr key={s.id}>
                  <td><Link to={`/sach/${s.id}`}>{s.nhanDe}</Link></td>
                  <td>{s.tacGia}</td>
                  <td>{s.namXb}</td>
                  <td>{s.theLoai}</td>
                  <td>
                    <span className={s.soSanSang > 0 ? 'chu-xanh' : 'chu-do'}>{s.soSanSang}/{s.tongSo}</span>
                  </td>
                </tr>
              ))}
              {kq.dsKetQua.length === 0 && (
                <tr><td colSpan={5} className="mo">
                  Không tìm thấy sách phù hợp. Thử bỏ bớt điều kiện lọc hoặc dùng từ khóa ngắn hơn.
                </td></tr>
              )}
            </tbody>
          </table>
          {kq.soTrang > 1 && (
            <div className="phan-trang">
              <button className="nut nut-phu" disabled={trang === 0} onClick={() => doi({ trang: String(trang - 1) })}>‹ Trước</button>
              <span>Trang {trang + 1}/{kq.soTrang}</span>
              <button className="nut nut-phu" disabled={trang + 1 >= kq.soTrang} onClick={() => doi({ trang: String(trang + 1) })}>Sau ›</button>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
