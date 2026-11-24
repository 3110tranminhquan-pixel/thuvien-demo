import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { layLoi } from '../../api/ApiClient';
import { doiMatKhau, giaHan, hoSoCuaToi, huyDatTruoc } from '../../api/thuVienApi';
import {
  ngay, ngayGio, tien, TEN_TINH_TRANG, TEN_TRANG_THAI_DAT, TEN_TRANG_THAI_PHAT, TEN_TRANG_THAI_THE, TEN_VI_PHAM,
} from '../../components/dinhDang';
import Nhan from '../../components/Nhan';
import ThongBao from '../../components/ThongBao';

const CAC_TAB = [['dang-muon', 'Đang mượn'], ['dat-truoc', 'Đặt trước'], ['lich-su', 'Lịch sử mượn'],
  ['phat', 'Tiền phạt'], ['mat-khau', 'Đổi mật khẩu']];

// Trang "Tài khoản của tôi" (FR-15 phía bạn đọc, FR-20, FR-22, FR-03)
export default function TaiKhoanCuaToi() {
  const [hs, setHs] = useState(null);
  const [tab, setTab] = useState('dang-muon');
  const [loi, setLoi] = useState('');
  const [ok, setOk] = useState('');
  const [mk, setMk] = useState({ cu: '', moi: '', lai: '' });

  const tai = () => hoSoCuaToi().then(setHs).catch((e) => setLoi(layLoi(e)));
  useEffect(() => { tai(); }, []);

  const lam = async (ham, thongBao) => {
    setLoi(''); setOk('');
    try {
      await ham();
      setOk(thongBao);
      tai();
      return true;
    } catch (e) {
      setLoi(layLoi(e));
      return false;
    }
  };

  const doiMk = (e) => {
    e.preventDefault();
    if (mk.moi !== mk.lai) { setLoi('Mật khẩu nhập lại không khớp'); setOk(''); return; }
    lam(() => doiMatKhau(mk.cu, mk.moi), 'Đã đổi mật khẩu').then((thanhCong) => thanhCong && setMk({ cu: '', moi: '', lai: '' }));
  };

  if (!hs) return <div className="trang"><ThongBao loi={loi} /></div>;
  const bd = hs.banDoc;

  return (
    <div className="trang">
      <div className="the">
        <div className="hang giua-hai-ben">
          <div>
            <h2 style={{ margin: 0 }}>{bd.hoTen}</h2>
            <div className="mo">{bd.maThe} · {bd.tenLoai} · {bd.donVi}</div>
          </div>
          <div style={{ textAlign: 'right' }}>
            <Nhan ma={bd.trangThai} ten={TEN_TRANG_THAI_THE[bd.trangThai]} />
            <div className="mo">Hạn thẻ: {ngay(bd.ngayHetHan)}</div>
          </div>
        </div>
        <div className="hang" style={{ gap: 32, marginTop: 12 }}>
          <div>Đang mượn: <b>{bd.soDangMuon}/{bd.soToiDa}</b> cuốn</div>
          <div>Tiền phạt chưa nộp: <b className={bd.tongNo > 0 ? 'chu-do' : ''}>{tien(bd.tongNo)}</b></div>
        </div>
        {bd.lyDoKhoa && <div className="hop-loi">Thẻ bị khóa: {bd.lyDoKhoa}</div>}
      </div>

      <div className="cac-tab">
        {CAC_TAB.map(([ma, ten]) => (
          <button key={ma} className={tab === ma ? 'tab chon' : 'tab'} onClick={() => { setTab(ma); setLoi(''); setOk(''); }}>{ten}</button>
        ))}
      </div>
      <ThongBao loi={loi} ok={ok} />

      <div className="the">
        {tab === 'dang-muon' && (
          <table>
            <thead><tr><th>Nhan đề</th><th>Mã vạch</th><th>Ngày mượn</th><th>Hạn trả</th><th>Đã gia hạn</th><th></th></tr></thead>
            <tbody>
              {hs.dangMuon.map((m) => (
                <tr key={m.chiTietId}>
                  <td>{m.nhanDe}</td>
                  <td>{m.maVach}</td>
                  <td>{ngay(m.ngayMuon)}</td>
                  <td>{ngay(m.hanTra)} {m.soNgayTre > 0 && <span className="chu-do">(trễ {m.soNgayTre} ngày)</span>}</td>
                  <td>{m.soLanGiaHan} lần</td>
                  <td><button className="nut nut-nho" onClick={() => lam(() => giaHan(m.chiTietId), `Đã gia hạn "${m.nhanDe}"`)}>Gia hạn</button></td>
                </tr>
              ))}
              {hs.dangMuon.length === 0 && <tr><td colSpan={6} className="mo">Bạn không mượn cuốn nào</td></tr>}
            </tbody>
          </table>
        )}

        {tab === 'dat-truoc' && (
          <table>
            <thead><tr><th>Nhan đề</th><th>Thời điểm đặt</th><th>Trạng thái</th><th>Ghi chú</th><th></th></tr></thead>
            <tbody>
              {hs.datTruoc.map((d) => (
                <tr key={d.id}>
                  <td><Link to={`/sach/${d.dauSachId}`}>{d.nhanDe}</Link></td>
                  <td>{ngayGio(d.thoiDiemDat)}</td>
                  <td><Nhan ma={d.trangThai} ten={TEN_TRANG_THAI_DAT[d.trangThai]} /></td>
                  <td>
                    {d.trangThai === 'DANG_CHO' && `Thứ tự trong hàng chờ: ${d.thuTu}`}
                    {d.trangThai === 'DANG_GIU' && `Sách ${d.maVachGiu} giữ đến ${ngayGio(d.hanGiu)}`}
                  </td>
                  <td>
                    {(d.trangThai === 'DANG_CHO' || d.trangThai === 'DANG_GIU') && (
                      <button className="nut nut-phu nut-nho" onClick={() => lam(() => huyDatTruoc(d.id), 'Đã hủy đặt trước')}>Hủy</button>
                    )}
                  </td>
                </tr>
              ))}
              {hs.datTruoc.length === 0 && <tr><td colSpan={5} className="mo">Chưa đặt trước cuốn nào</td></tr>}
            </tbody>
          </table>
        )}

        {tab === 'lich-su' && (
          <table>
            <thead><tr><th>Nhan đề</th><th>Ngày mượn</th><th>Hạn trả</th><th>Ngày trả</th><th>Tình trạng khi trả</th></tr></thead>
            <tbody>
              {hs.lichSu.map((m) => (
                <tr key={m.chiTietId}>
                  <td>{m.nhanDe}</td>
                  <td>{ngay(m.ngayMuon)}</td>
                  <td>{ngay(m.hanTra)}</td>
                  <td>{ngay(m.ngayTra)} {m.soNgayTre > 0 && <span className="chu-do">(trễ {m.soNgayTre} ngày)</span>}</td>
                  <td>{TEN_TINH_TRANG[m.tinhTrangKhiTra]}</td>
                </tr>
              ))}
              {hs.lichSu.length === 0 && <tr><td colSpan={5} className="mo">Chưa có lịch sử</td></tr>}
            </tbody>
          </table>
        )}

        {tab === 'phat' && (
          <table>
            <thead><tr><th>Ngày lập</th><th>Sách</th><th>Vi phạm</th><th>Số tiền</th><th>Trạng thái</th></tr></thead>
            <tbody>
              {hs.phieuPhat.map((p) => (
                <tr key={p.id}>
                  <td>{ngay(p.ngayLap)}</td>
                  <td>{p.nhanDe}</td>
                  <td>{TEN_VI_PHAM[p.loaiViPham]}</td>
                  <td>{tien(p.soTien)}</td>
                  <td><Nhan ma={p.trangThai} ten={TEN_TRANG_THAI_PHAT[p.trangThai]} /></td>
                </tr>
              ))}
              {hs.phieuPhat.length === 0 && <tr><td colSpan={5} className="mo">Không có phiếu phạt nào</td></tr>}
            </tbody>
          </table>
        )}

        {tab === 'mat-khau' && (
          <form onSubmit={doiMk} className="form-doc" style={{ maxWidth: 360 }}>
            <label>Mật khẩu hiện tại</label>
            <input type="password" value={mk.cu} onChange={(e) => setMk({ ...mk, cu: e.target.value })} />
            <label>Mật khẩu mới (tối thiểu 8 ký tự, có chữ và số)</label>
            <input type="password" value={mk.moi} onChange={(e) => setMk({ ...mk, moi: e.target.value })} />
            <label>Nhập lại mật khẩu mới</label>
            <input type="password" value={mk.lai} onChange={(e) => setMk({ ...mk, lai: e.target.value })} />
            <button className="nut">Đổi mật khẩu</button>
          </form>
        )}
      </div>
    </div>
  );
}
