import { useEffect, useState } from 'react';
import { layLoi } from '../../api/ApiClient';
import { layDanhMuc, luuDanhMuc, xoaDanhMuc } from '../../api/thuVienApi';
import ThongBao from '../../components/ThongBao';

const CAC_LOAI = [
  { ma: 'the-loai', ten: 'Thể loại', phu: 'Mã DDC' },
  { ma: 'nha-xuat-ban', ten: 'Nhà xuất bản', phu: 'Địa chỉ' },
  { ma: 'tac-gia', ten: 'Tác giả', phu: null },
];

// FR-07: quản lý danh mục tác giả, thể loại, nhà xuất bản
export default function DanhMuc() {
  return (
    <div className="ba-cot">
      {CAC_LOAI.map((l) => <CotDanhMuc key={l.ma} loai={l} />)}
    </div>
  );
}

function CotDanhMuc({ loai }) {
  const [ds, setDs] = useState([]);
  const [dangSua, setDangSua] = useState(null);   // { id, ten, phu }
  const [loi, setLoi] = useState('');

  const tai = () => layDanhMuc(loai.ma).then(setDs);
  useEffect(() => { tai(); }, []);

  const luu = async (e) => {
    e.preventDefault();
    setLoi('');
    try {
      await luuDanhMuc(loai.ma, dangSua.id, { ten: dangSua.ten, phu: dangSua.phu });
      setDangSua(null);
      tai();
    } catch (err) { setLoi(layLoi(err)); }
  };

  const xoa = async (id) => {
    setLoi('');
    try {
      await xoaDanhMuc(loai.ma, id);
      tai();
    } catch (err) { setLoi(layLoi(err)); }
  };

  return (
    <div className="the">
      <div className="hang giua-hai-ben">
        <h3 style={{ margin: 0 }}>{loai.ten}</h3>
        <button className="nut nut-nho" onClick={() => setDangSua({ id: null, ten: '', phu: '' })}>+ Thêm</button>
      </div>
      <ThongBao loi={loi} />
      {dangSua && (
        <form onSubmit={luu} className="form-doc">
          <input placeholder="Tên" value={dangSua.ten} onChange={(e) => setDangSua({ ...dangSua, ten: e.target.value })} autoFocus />
          {loai.phu && <input placeholder={loai.phu} value={dangSua.phu || ''} onChange={(e) => setDangSua({ ...dangSua, phu: e.target.value })} />}
          <div className="hang">
            <button className="nut nut-nho">Lưu</button>
            <button type="button" className="nut nut-phu nut-nho" onClick={() => setDangSua(null)}>Hủy</button>
          </div>
        </form>
      )}
      <table>
        <tbody>
          {ds.map((x) => (
            <tr key={x.id}>
              <td>{x.ten}{x.phu && <div className="mo">{x.phu}</div>}</td>
              <td className="nut-nhom" style={{ textAlign: 'right' }}>
                <button className="nut-link" onClick={() => setDangSua({ ...x })}>Sửa</button>
                <button className="nut-link chu-do" onClick={() => xoa(x.id)}>Xóa</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
