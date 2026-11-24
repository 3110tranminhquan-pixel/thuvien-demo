import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { layLoi } from '../../api/ApiClient';
import { chiTietSach, datTruoc } from '../../api/thuVienApi';
import { tien, TEN_LOAI_TAI_LIEU } from '../../components/dinhDang';
import Nhan from '../../components/Nhan';
import ThongBao from '../../components/ThongBao';
import { useAuth } from '../../hooks/useAuth';

// FR-05: chi tiết đầu sách, số bản sẵn sàng, vị trí kệ và trạng thái từng bản
export default function ChiTietSach() {
  const { id } = useParams();
  const { nguoiDung } = useAuth();
  const [s, setS] = useState(null);
  const [loi, setLoi] = useState('');
  const [ok, setOk] = useState('');

  const tai = () => chiTietSach(id).then(setS).catch((e) => setLoi(layLoi(e)));
  useEffect(() => { tai(); }, [id]);

  const dat = async () => {
    setLoi(''); setOk('');
    try {
      const dt = await datTruoc(s.id);
      setOk(`Đặt trước thành công. Bạn đang ở vị trí thứ ${dt.thuTu} trong hàng chờ, sách về sẽ có email báo.`);
      tai();
    } catch (e) {
      setLoi(layLoi(e));
    }
  };

  if (!s) return <div className="trang"><ThongBao loi={loi} /></div>;

  return (
    <div className="trang">
      <button className="nut-link" onClick={() => window.history.back()}>‹ Quay lại</button>
      <div className="the">
        <h2>{s.nhanDe}</h2>
        <div className="luoi-2">
          <div>
            <p><b>Tác giả:</b> {s.tacGia}</p>
            <p><b>Nhà xuất bản:</b> {s.nhaXuatBan}{s.namXb ? `, ${s.namXb}` : ''}</p>
            <p><b>Thể loại:</b> {s.theLoai}</p>
            <p><b>ISBN:</b> {s.isbn}</p>
          </div>
          <div>
            <p><b>Loại tài liệu:</b> {TEN_LOAI_TAI_LIEU[s.loaiTaiLieu]}</p>
            <p><b>Giá bìa:</b> {tien(s.giaBia)}</p>
            <p><b>Sẵn sàng:</b> <span className={s.soSanSang > 0 ? 'chu-xanh' : 'chu-do'}>{s.soSanSang}/{s.tongSo} bản</span></p>
            {s.soNguoiCho > 0 && <p><b>Đang có {s.soNguoiCho} người chờ</b></p>}
          </div>
        </div>

        {!s.choMuonVe && <div className="hop-vang">Tài liệu này chỉ đọc tại chỗ, không cho mượn về.</div>}
        {nguoiDung?.vaiTro === 'BAN_DOC' && s.choMuonVe && s.soSanSang === 0 && s.tongSo > 0 && (
          <button className="nut" onClick={dat}>Đặt trước</button>
        )}
        {!nguoiDung && s.choMuonVe && s.soSanSang === 0 && (
          <p className="mo">Hết sách? <Link to="/dang-nhap">Đăng nhập</Link> để đặt trước.</p>
        )}
        <ThongBao loi={loi} ok={ok} />
      </div>

      <div className="the">
        <h3>Các bản sách</h3>
        <table>
          <thead><tr><th>Mã vạch</th><th>Vị trí kệ</th><th>Trạng thái</th></tr></thead>
          <tbody>
            {s.dsBanSach.map((b) => (
              <tr key={b.id}>
                <td>{b.maVach}</td>
                <td>{b.viTriKe}</td>
                <td><Nhan ma={b.trangThai} ten={b.tenTrangThai} /></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
