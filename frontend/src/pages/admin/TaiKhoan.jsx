import { useEffect, useState } from 'react';
import { layLoi } from '../../api/ApiClient';
import { capNhatTaiKhoan, dsTaiKhoan, taoTaiKhoan } from '../../api/thuVienApi';
import { ngayGio, TEN_VAI_TRO } from '../../components/dinhDang';
import HopThoai from '../../components/HopThoai';
import Nhan from '../../components/Nhan';
import ThongBao from '../../components/ThongBao';

const VAI_TRO = [[2, 'THU_THU'], [3, 'QUAN_TRI']];

// FR-34: tạo, khóa tài khoản và gán vai trò
export default function TaiKhoan() {
  const [ds, setDs] = useState([]);
  const [form, setForm] = useState(null);
  const [loi, setLoi] = useState('');
  const [ok, setOk] = useState('');

  const tai = () => dsTaiKhoan().then(setDs).catch((e) => setLoi(layLoi(e)));
  useEffect(() => { tai(); }, []);

  const tao = async (e) => {
    e.preventDefault();
    setLoi('');
    try {
      await taoTaiKhoan({ ...form, vaiTroId: Number(form.vaiTroId) });
      setOk(`Đã tạo tài khoản ${form.tenDangNhap}`);
      setForm(null);
      tai();
    } catch (err) { setLoi(layLoi(err)); }
  };

  const capNhat = async (tk, duLieu, thongBao) => {
    setLoi(''); setOk('');
    try { await capNhatTaiKhoan(tk.id, duLieu); setOk(thongBao); tai(); } catch (err) { setLoi(layLoi(err)); }
  };

  return (
    <div>
      <div className="hang giua-hai-ben">
        <span className="mo">Tài khoản bạn đọc được tạo tự động khi thủ thư cấp thẻ.</span>
        <button className="nut" onClick={() => { setLoi(''); setForm({ tenDangNhap: '', matKhau: '', vaiTroId: 2 }); }}>+ Tạo tài khoản nhân viên</button>
      </div>
      {!form && <ThongBao loi={loi} ok={ok} />}
      <div className="the">
        <table>
          <thead><tr><th>Tên đăng nhập</th><th>Vai trò</th><th>Trạng thái</th><th>Tạm khóa đến</th><th></th></tr></thead>
          <tbody>
            {ds.map((tk) => (
              <tr key={tk.id}>
                <td>{tk.tenDangNhap}</td>
                <td>
                  {tk.vaiTro === 'BAN_DOC' ? TEN_VAI_TRO[tk.vaiTro] : (
                    <select value={tk.vaiTroId} onChange={(e) => capNhat(tk, { vaiTroId: Number(e.target.value) }, 'Đã đổi vai trò')}>
                      {VAI_TRO.map(([id, ma]) => <option key={id} value={id}>{TEN_VAI_TRO[ma]}</option>)}
                    </select>
                  )}
                </td>
                <td><Nhan ma={tk.trangThai} ten={tk.trangThai === 'HOAT_DONG' ? 'Hoạt động' : 'Bị khóa'} /></td>
                <td>{ngayGio(tk.khoaDen)}</td>
                <td>
                  {tk.trangThai === 'HOAT_DONG'
                    ? <button className="nut nut-do nut-nho" onClick={() => capNhat(tk, { trangThai: 'BI_KHOA' }, `Đã khóa ${tk.tenDangNhap}`)}>Khóa</button>
                    : <button className="nut nut-nho" onClick={() => capNhat(tk, { trangThai: 'HOAT_DONG' }, `Đã mở ${tk.tenDangNhap}`)}>Mở khóa</button>}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {form && (
        <HopThoai tieuDe="Tạo tài khoản nhân viên" onDong={() => setForm(null)}>
          <form onSubmit={tao} className="form-doc">
            <label>Tên đăng nhập</label>
            <input value={form.tenDangNhap} onChange={(e) => setForm({ ...form, tenDangNhap: e.target.value })} />
            <label>Mật khẩu (tối thiểu 8 ký tự, có chữ và số)</label>
            <input type="password" value={form.matKhau} onChange={(e) => setForm({ ...form, matKhau: e.target.value })} />
            <label>Vai trò</label>
            <select value={form.vaiTroId} onChange={(e) => setForm({ ...form, vaiTroId: e.target.value })}>
              {VAI_TRO.map(([id, ma]) => <option key={id} value={id}>{TEN_VAI_TRO[ma]}</option>)}
            </select>
            <ThongBao loi={loi} />
            <button className="nut">Tạo</button>
          </form>
        </HopThoai>
      )}
    </div>
  );
}
