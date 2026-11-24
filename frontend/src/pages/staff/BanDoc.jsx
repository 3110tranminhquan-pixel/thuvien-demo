import { useEffect, useState } from 'react';
import { layLoi } from '../../api/ApiClient';
import { capThe, dsBanDoc, dsLoaiBanDoc, hoSoBanDoc, khoaThe, moKhoaThe, suaBanDoc } from '../../api/thuVienApi';
import {
  ngay, tien, TEN_TINH_TRANG, TEN_TRANG_THAI_DAT, TEN_TRANG_THAI_PHAT, TEN_TRANG_THAI_THE, TEN_VI_PHAM,
} from '../../components/dinhDang';
import HopThoai from '../../components/HopThoai';
import Nhan from '../../components/Nhan';
import ThongBao from '../../components/ThongBao';

const FORM_TRONG = { hoTen: '', ngaySinh: '', email: '', soDienThoai: '', donVi: '', ngayHetHan: '', loaiId: '' };

// Quản lý bạn đọc (FR-11, FR-12, FR-13, FR-15)
export default function BanDoc() {
  const [ds, setDs] = useState([]);
  const [tuKhoa, setTuKhoa] = useState('');
  const [dsLoai, setDsLoai] = useState([]);
  const [form, setForm] = useState(null);
  const [hoSo, setHoSo] = useState(null);
  const [loi, setLoi] = useState('');
  const [ok, setOk] = useState('');

  const tai = () => dsBanDoc(tuKhoa).then(setDs).catch((e) => setLoi(layLoi(e)));
  useEffect(() => { tai(); dsLoaiBanDoc().then(setDsLoai); }, []);

  const moThem = () => {
    const sau4Nam = new Date();
    sau4Nam.setFullYear(sau4Nam.getFullYear() + 4);
    setLoi('');
    setForm({ ...FORM_TRONG, ngayHetHan: sau4Nam.toISOString().slice(0, 10), loaiId: dsLoai[0]?.id || '' });
  };

  const moSua = (bd) => {
    setLoi('');
    setForm({
      id: bd.id, hoTen: bd.hoTen, ngaySinh: bd.ngaySinh || '', email: bd.email, soDienThoai: bd.soDienThoai || '',
      donVi: bd.donVi || '', ngayHetHan: bd.ngayHetHan, loaiId: bd.loaiId,
    });
  };

  const luu = async (e) => {
    e.preventDefault();
    setLoi('');
    const duLieu = { ...form, ngaySinh: form.ngaySinh || null, loaiId: Number(form.loaiId) || null, ngayHetHan: form.ngayHetHan || null };
    try {
      if (form.id) {
        await suaBanDoc(form.id, duLieu);
        setOk('Đã lưu thông tin bạn đọc');
      } else {
        const kq = await capThe(duLieu);
        setOk(`Đã cấp thẻ ${kq.banDoc.maThe}. Tên đăng nhập: ${kq.tenDangNhap}, mật khẩu ban đầu: ${kq.matKhauBanDau} (đã gửi email cho bạn đọc)`);
      }
      setForm(null);
      tai();
    } catch (err) { setLoi(layLoi(err)); }
  };

  const khoa = async (bd) => {
    const lyDo = window.prompt(`Lý do khóa thẻ ${bd.maThe}:`);
    if (lyDo === null) return;
    setLoi(''); setOk('');
    try { await khoaThe(bd.id, lyDo); setOk(`Đã khóa thẻ ${bd.maThe}`); tai(); } catch (err) { setLoi(layLoi(err)); }
  };

  const mo = async (bd) => {
    setLoi(''); setOk('');
    try { await moKhoaThe(bd.id); setOk(`Đã mở thẻ ${bd.maThe}`); tai(); } catch (err) { setLoi(layLoi(err)); }
  };

  const xemHoSo = async (bd) => {
    setLoi('');
    try { setHoSo(await hoSoBanDoc(bd.id)); } catch (err) { setLoi(layLoi(err)); }
  };

  return (
    <div>
      <div className="hang giua-hai-ben">
        <form className="hang" onSubmit={(e) => { e.preventDefault(); tai(); }}>
          <input placeholder="Tìm theo tên, mã thẻ hoặc email" value={tuKhoa} onChange={(e) => setTuKhoa(e.target.value)} />
          <button className="nut nut-phu">Tìm</button>
        </form>
        <button className="nut" onClick={moThem}>+ Cấp thẻ mới</button>
      </div>
      {!form && <ThongBao loi={loi} ok={ok} />}

      <div className="the">
        <table>
          <thead><tr><th>Mã thẻ</th><th>Họ tên</th><th>Loại</th><th>Đơn vị</th><th>Hạn thẻ</th><th>Đang mượn</th><th>Nợ phạt</th><th>Trạng thái</th><th></th></tr></thead>
          <tbody>
            {ds.map((bd) => (
              <tr key={bd.id}>
                <td>{bd.maThe}</td>
                <td>{bd.hoTen}</td>
                <td>{bd.tenLoai}</td>
                <td>{bd.donVi}</td>
                <td>{ngay(bd.ngayHetHan)}</td>
                <td>{bd.soDangMuon}/{bd.soToiDa}</td>
                <td className={bd.tongNo > 0 ? 'chu-do' : ''}>{tien(bd.tongNo)}</td>
                <td><Nhan ma={bd.trangThai} ten={TEN_TRANG_THAI_THE[bd.trangThai] + (bd.khoaThuCong ? ' (thủ công)' : '')} /></td>
                <td className="nut-nhom">
                  <button className="nut nut-nho" onClick={() => xemHoSo(bd)}>Hồ sơ</button>
                  <button className="nut nut-nho" onClick={() => moSua(bd)}>Sửa</button>
                  {bd.trangThai === 'BI_KHOA'
                    ? <button className="nut nut-phu nut-nho" onClick={() => mo(bd)}>Mở khóa</button>
                    : <button className="nut nut-do nut-nho" onClick={() => khoa(bd)}>Khóa</button>}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {form && (
        <HopThoai tieuDe={form.id ? 'Sửa thông tin bạn đọc' : 'Cấp thẻ bạn đọc mới'} onDong={() => setForm(null)} rong>
          <form onSubmit={luu} className="form-luoi">
            <label>Họ tên<input value={form.hoTen} onChange={(e) => setForm({ ...form, hoTen: e.target.value })} /></label>
            <label>Ngày sinh<input type="date" value={form.ngaySinh} onChange={(e) => setForm({ ...form, ngaySinh: e.target.value })} /></label>
            <label>Email<input value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} /></label>
            <label>Số điện thoại<input value={form.soDienThoai} onChange={(e) => setForm({ ...form, soDienThoai: e.target.value })} /></label>
            <label>Đơn vị (khoa, phòng)<input value={form.donVi} onChange={(e) => setForm({ ...form, donVi: e.target.value })} /></label>
            <label>Ngày hết hạn thẻ<input type="date" value={form.ngayHetHan} onChange={(e) => setForm({ ...form, ngayHetHan: e.target.value })} /></label>
            <label>Loại bạn đọc
              <select value={form.loaiId} onChange={(e) => setForm({ ...form, loaiId: e.target.value })}>
                {dsLoai.map((l) => <option key={l.id} value={l.id}>{l.tenLoai}</option>)}
              </select>
            </label>
            <div className="ca-hang"><ThongBao loi={loi} /></div>
            <div className="ca-hang chan-cot">
              <button type="button" className="nut nut-phu" onClick={() => setForm(null)}>Hủy</button>
              <button className="nut">{form.id ? 'Lưu' : 'Cấp thẻ'}</button>
            </div>
          </form>
        </HopThoai>
      )}

      {hoSo && (
        <HopThoai tieuDe={`Hồ sơ bạn đọc – ${hoSo.banDoc.hoTen} (${hoSo.banDoc.maThe})`} onDong={() => setHoSo(null)} rong>
          {hoSo.banDoc.lyDoKhoa && <div className="hop-loi">Thẻ bị khóa: {hoSo.banDoc.lyDoKhoa}</div>}
          <h4>Đang mượn ({hoSo.dangMuon.length})</h4>
          <table>
            <thead><tr><th>Nhan đề</th><th>Mã vạch</th><th>Ngày mượn</th><th>Hạn trả</th></tr></thead>
            <tbody>
              {hoSo.dangMuon.map((m) => (
                <tr key={m.chiTietId}><td>{m.nhanDe}</td><td>{m.maVach}</td><td>{ngay(m.ngayMuon)}</td>
                  <td>{ngay(m.hanTra)} {m.soNgayTre > 0 && <span className="chu-do">(trễ {m.soNgayTre} ngày)</span>}</td></tr>
              ))}
            </tbody>
          </table>
          <h4>Lịch sử mượn ({hoSo.lichSu.length})</h4>
          <table>
            <thead><tr><th>Nhan đề</th><th>Ngày mượn</th><th>Ngày trả</th><th>Tình trạng</th></tr></thead>
            <tbody>
              {hoSo.lichSu.map((m) => (
                <tr key={m.chiTietId}><td>{m.nhanDe}</td><td>{ngay(m.ngayMuon)}</td><td>{ngay(m.ngayTra)}</td><td>{TEN_TINH_TRANG[m.tinhTrangKhiTra]}</td></tr>
              ))}
            </tbody>
          </table>
          <h4>Phiếu phạt ({hoSo.phieuPhat.length})</h4>
          <table>
            <thead><tr><th>Ngày lập</th><th>Vi phạm</th><th>Số tiền</th><th>Trạng thái</th></tr></thead>
            <tbody>
              {hoSo.phieuPhat.map((p) => (
                <tr key={p.id}><td>{ngay(p.ngayLap)}</td><td>{TEN_VI_PHAM[p.loaiViPham]}</td><td>{tien(p.soTien)}</td>
                  <td><Nhan ma={p.trangThai} ten={TEN_TRANG_THAI_PHAT[p.trangThai]} /></td></tr>
              ))}
            </tbody>
          </table>
          <h4>Đặt trước ({hoSo.datTruoc.length})</h4>
          <table>
            <tbody>
              {hoSo.datTruoc.map((d) => (
                <tr key={d.id}><td>{d.nhanDe}</td><td><Nhan ma={d.trangThai} ten={TEN_TRANG_THAI_DAT[d.trangThai]} /></td></tr>
              ))}
            </tbody>
          </table>
        </HopThoai>
      )}
    </div>
  );
}
