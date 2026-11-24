import { useEffect, useState } from 'react';
import { layLoi } from '../../api/ApiClient';
import {
  capNhatBanSach, chiTietSach, doiTrangThaiDauSach, dsDauSachQuanLy, layDanhMuc, suaDauSach, themBanSach, themDauSach,
} from '../../api/thuVienApi';
import { TEN_LOAI_TAI_LIEU } from '../../components/dinhDang';
import HopThoai from '../../components/HopThoai';
import Nhan from '../../components/Nhan';
import ThongBao from '../../components/ThongBao';

const FORM_TRONG = {
  isbn: '', nhanDe: '', namXb: '', lanXb: 1, soTrang: '', giaBia: '', maDdc: '',
  loaiTaiLieu: 'GIAO_TRINH', theLoaiId: '', nxbId: '', dsTacGiaId: [],
};

// Quản lý đầu sách và bản sách (FR-06, FR-08, FR-09, FR-10)
export default function QuanLyDauSach() {
  const [ds, setDs] = useState([]);
  const [tuKhoa, setTuKhoa] = useState('');
  const [danhMuc, setDanhMuc] = useState({ theLoai: [], nxb: [], tacGia: [] });
  const [form, setForm] = useState(null);         // đang thêm / sửa đầu sách
  const [banSach, setBanSach] = useState(null);   // đang xem bản sách của một đầu sách
  const [loi, setLoi] = useState('');
  const [ok, setOk] = useState('');

  const tai = () => dsDauSachQuanLy(tuKhoa).then(setDs).catch((e) => setLoi(layLoi(e)));

  useEffect(() => {
    tai();
    Promise.all([layDanhMuc('the-loai'), layDanhMuc('nha-xuat-ban'), layDanhMuc('tac-gia')])
      .then(([theLoai, nxb, tacGia]) => setDanhMuc({ theLoai, nxb, tacGia }));
  }, []);

  const moSua = async (id) => {
    setLoi('');
    const s = await chiTietSach(id);
    setForm({
      id: s.id, isbn: s.isbn, nhanDe: s.nhanDe, namXb: s.namXb ?? '', lanXb: s.lanXb ?? 1, soTrang: s.soTrang ?? '',
      giaBia: s.giaBia, maDdc: s.maDdc ?? '', loaiTaiLieu: s.loaiTaiLieu, theLoaiId: s.theLoaiId, nxbId: s.nxbId,
      dsTacGiaId: s.dsTacGiaId,
    });
  };

  const luu = async (e) => {
    e.preventDefault();
    setLoi('');
    const duLieu = {
      ...form,
      namXb: form.namXb === '' ? null : Number(form.namXb),
      soTrang: form.soTrang === '' ? null : Number(form.soTrang),
      lanXb: Number(form.lanXb) || 1,
      giaBia: form.giaBia === '' ? null : Number(form.giaBia),
      theLoaiId: form.theLoaiId === '' ? null : Number(form.theLoaiId),
      nxbId: form.nxbId === '' ? null : Number(form.nxbId),
    };
    try {
      if (form.id) await suaDauSach(form.id, duLieu); else await themDauSach(duLieu);
      setOk(form.id ? 'Đã lưu thay đổi' : 'Đã thêm đầu sách, bấm "Bản sách" để nhập số cuốn');
      setForm(null);
      tai();
    } catch (err) {
      setLoi(layLoi(err));
    }
  };

  const doiTrangThai = async (s) => {
    setLoi(''); setOk('');
    try {
      await doiTrangThaiDauSach(s.id, s.trangThai === 'DANG_DUNG');
      tai();
    } catch (err) { setLoi(layLoi(err)); }
  };

  const chonTacGia = (id) => setForm((f) => ({
    ...f, dsTacGiaId: f.dsTacGiaId.includes(id) ? f.dsTacGiaId.filter((x) => x !== id) : [...f.dsTacGiaId, id],
  }));

  return (
    <div>
      <div className="hang giua-hai-ben">
        <form className="hang" onSubmit={(e) => { e.preventDefault(); tai(); }}>
          <input placeholder="Tìm theo nhan đề hoặc ISBN" value={tuKhoa} onChange={(e) => setTuKhoa(e.target.value)} />
          <button className="nut nut-phu">Tìm</button>
        </form>
        <button className="nut" onClick={() => { setLoi(''); setForm({ ...FORM_TRONG }); }}>+ Thêm đầu sách</button>
      </div>
      {!form && !banSach && <ThongBao loi={loi} ok={ok} />}

      <div className="the">
        <table>
          <thead><tr><th>ISBN</th><th>Nhan đề</th><th>Tác giả</th><th>Loại</th><th>Sẵn sàng</th><th>Trạng thái</th><th></th></tr></thead>
          <tbody>
            {ds.map((s) => (
              <tr key={s.id}>
                <td>{s.isbn}</td>
                <td>{s.nhanDe}</td>
                <td>{s.tacGia}</td>
                <td>{TEN_LOAI_TAI_LIEU[s.loaiTaiLieu]?.split(' (')[0]}</td>
                <td>{s.soSanSang}/{s.tongSo}</td>
                <td><Nhan ma={s.trangThai} ten={s.trangThai === 'DANG_DUNG' ? 'Đang dùng' : 'Ngừng'} /></td>
                <td className="nut-nhom">
                  <button className="nut nut-nho" onClick={() => moSua(s.id)}>Sửa</button>
                  <button className="nut nut-nho" onClick={() => setBanSach(s.id)}>Bản sách</button>
                  <button className="nut nut-phu nut-nho" onClick={() => doiTrangThai(s)}>{s.trangThai === 'DANG_DUNG' ? 'Ngừng' : 'Dùng lại'}</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {form && (
        <HopThoai tieuDe={form.id ? 'Sửa đầu sách' : 'Thêm đầu sách'} onDong={() => setForm(null)} rong>
          <form onSubmit={luu} className="form-luoi">
            <label>ISBN-13<input value={form.isbn} onChange={(e) => setForm({ ...form, isbn: e.target.value })} /></label>
            <label>Nhan đề<input value={form.nhanDe} onChange={(e) => setForm({ ...form, nhanDe: e.target.value })} /></label>
            <label>Năm xuất bản<input type="number" value={form.namXb} onChange={(e) => setForm({ ...form, namXb: e.target.value })} /></label>
            <label>Lần xuất bản<input type="number" value={form.lanXb} onChange={(e) => setForm({ ...form, lanXb: e.target.value })} /></label>
            <label>Số trang<input type="number" value={form.soTrang} onChange={(e) => setForm({ ...form, soTrang: e.target.value })} /></label>
            <label>Giá bìa (đồng)<input type="number" value={form.giaBia} onChange={(e) => setForm({ ...form, giaBia: e.target.value })} /></label>
            <label>Mã DDC<input value={form.maDdc} onChange={(e) => setForm({ ...form, maDdc: e.target.value })} /></label>
            <label>Loại tài liệu
              <select value={form.loaiTaiLieu} onChange={(e) => setForm({ ...form, loaiTaiLieu: e.target.value })}>
                {Object.entries(TEN_LOAI_TAI_LIEU).map(([ma, ten]) => <option key={ma} value={ma}>{ten}</option>)}
              </select>
            </label>
            <label>Thể loại
              <select value={form.theLoaiId} onChange={(e) => setForm({ ...form, theLoaiId: e.target.value })}>
                <option value="">-- Chọn --</option>
                {danhMuc.theLoai.map((t) => <option key={t.id} value={t.id}>{t.ten}</option>)}
              </select>
            </label>
            <label>Nhà xuất bản
              <select value={form.nxbId} onChange={(e) => setForm({ ...form, nxbId: e.target.value })}>
                <option value="">-- Chọn --</option>
                {danhMuc.nxb.map((n) => <option key={n.id} value={n.id}>{n.ten}</option>)}
              </select>
            </label>
            <div className="ca-hang">
              <div className="nhan-o">Tác giả</div>
              <div className="hop-chon">
                {danhMuc.tacGia.map((t) => (
                  <label key={t.id} className="chon">
                    <input type="checkbox" checked={form.dsTacGiaId.includes(t.id)} onChange={() => chonTacGia(t.id)} /> {t.ten}
                  </label>
                ))}
              </div>
            </div>
            <div className="ca-hang"><ThongBao loi={loi} /></div>
            <div className="ca-hang chan-cot">
              <button type="button" className="nut nut-phu" onClick={() => setForm(null)}>Hủy</button>
              <button className="nut">Lưu</button>
            </div>
          </form>
        </HopThoai>
      )}

      {banSach && <HopThoaiBanSach dauSachId={banSach} onDong={() => { setBanSach(null); tai(); }} />}
    </div>
  );
}

// Danh sách bản sách của một đầu sách: thêm bản mới, đổi vị trí, đổi trạng thái (Đang sửa / Sẵn sàng / Thanh lý)
function HopThoaiBanSach({ dauSachId, onDong }) {
  const [s, setS] = useState(null);
  const [soLuong, setSoLuong] = useState(1);
  const [viTri, setViTri] = useState('');
  const [loi, setLoi] = useState('');
  const [ok, setOk] = useState('');

  const tai = () => chiTietSach(dauSachId).then(setS).catch((e) => setLoi(layLoi(e)));
  useEffect(() => { tai(); }, [dauSachId]);

  const them = async (e) => {
    e.preventDefault();
    setLoi(''); setOk('');
    try {
      const moi = await themBanSach(dauSachId, Number(soLuong), viTri);
      setOk(`Đã thêm ${moi.length} bản: ${moi.map((b) => b.maVach).join(', ')}`);
      tai();
    } catch (err) { setLoi(layLoi(err)); }
  };

  const doi = async (b, trangThai) => {
    setLoi(''); setOk('');
    try {
      await capNhatBanSach(b.id, { trangThai });
      tai();
    } catch (err) { setLoi(layLoi(err)); }
  };

  return (
    <HopThoai tieuDe={s ? `Bản sách – ${s.nhanDe}` : 'Bản sách'} onDong={onDong} rong>
      <form className="hang" onSubmit={them}>
        <label>Số bản thêm <input type="number" min={1} max={50} value={soLuong} style={{ width: 80 }} onChange={(e) => setSoLuong(e.target.value)} /></label>
        <label>Vị trí kệ <input value={viTri} style={{ width: 120 }} placeholder="VD: K01-T2" onChange={(e) => setViTri(e.target.value)} /></label>
        <button className="nut">Thêm, tự sinh mã vạch</button>
      </form>
      <ThongBao loi={loi} ok={ok} />
      {s && (
        <table>
          <thead><tr><th>Mã vạch</th><th>Vị trí</th><th>Ngày nhập</th><th>Trạng thái</th><th>Đổi trạng thái</th></tr></thead>
          <tbody>
            {s.dsBanSach.map((b) => (
              <tr key={b.id}>
                <td>{b.maVach}</td>
                <td>{b.viTriKe}</td>
                <td>{b.ngayNhap}</td>
                <td><Nhan ma={b.trangThai} ten={b.tenTrangThai} /></td>
                <td className="nut-nhom">
                  {b.trangThai === 'SAN_SANG' && <button className="nut nut-phu nut-nho" onClick={() => doi(b, 'DANG_SUA')}>Đưa đi sửa</button>}
                  {b.trangThai === 'DANG_SUA' && <>
                    <button className="nut nut-nho" onClick={() => doi(b, 'SAN_SANG')}>Sửa xong</button>
                    <button className="nut nut-do nut-nho" onClick={() => doi(b, 'THANH_LY')}>Thanh lý</button>
                  </>}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </HopThoai>
  );
}
