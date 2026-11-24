import { useState } from 'react';
import { layLoi } from '../../api/ApiClient';
import { baoMatSach, traSach, xemTruocTra } from '../../api/thuVienApi';
import { ngay, tien } from '../../components/dinhDang';
import ThongBao from '../../components/ThongBao';
import { useBarcodeInput } from '../../hooks/useBarcodeInput';

// Màn hình Trả sách (UC07, FR-18, FR-19, FR-23) và báo mất sách (FR-25, BR-13)
export default function TraSach() {
  const [xemTruoc, setXemTruoc] = useState(null);
  const [tinhTrang, setTinhTrang] = useState('');
  const [hoiBaoMat, setHoiBaoMat] = useState(false);
  const [daTra, setDaTra] = useState([]);
  const [loi, setLoi] = useState('');

  const xuLyQuet = async (ma) => {
    setLoi(''); setTinhTrang(''); setHoiBaoMat(false);
    try {
      setXemTruoc(await xemTruocTra(ma));
    } catch (e) {
      setXemTruoc(null);
      setLoi(layLoi(e));
    }
  };

  const { inputRef, handleKeyDown, handleBlur } = useBarcodeInput(xuLyQuet);

  const xacNhanTra = async () => {
    setLoi('');
    if (!tinhTrang) { setLoi('Phải chọn tình trạng sách trước khi xác nhận'); return; }
    try {
      const kq = await traSach(xemTruoc.maVach, tinhTrang);
      setDaTra((ds) => [{ ...kq, loai: 'tra' }, ...ds]);
      setXemTruoc(null);
    } catch (e) {
      setLoi(layLoi(e));
    }
  };

  const xacNhanMat = async () => {
    setLoi('');
    try {
      const dsPhieu = await baoMatSach(xemTruoc.maVach);
      const tong = dsPhieu.reduce((t, p) => t + Number(p.soTien), 0);
      setDaTra((ds) => [{
        loai: 'mat', maVach: xemTruoc.maVach, nhanDe: xemTruoc.nhanDe, maThe: xemTruoc.maThe,
        hoTen: xemTruoc.hoTen, tongPhat: tong,
      }, ...ds]);
      setXemTruoc(null);
      setHoiBaoMat(false);
    } catch (e) {
      setLoi(layLoi(e));
    }
  };

  const phatHong = tinhTrang === 'HONG_NHE' ? xemTruoc?.phatHongNhe : tinhTrang === 'HONG_NANG' ? xemTruoc?.phatHongNang : 0;
  const tongTam = xemTruoc ? Number(xemTruoc.phatTreTamTinh) + Number(phatHong || 0) : 0;

  return (
    <div className="hai-cot">
      <div className="cot-trai">
        <div className="the">
          <div className="nhan-o">Quét mã vạch sách trả</div>
          <input ref={inputRef} className="o-quet" placeholder="Đưa máy quét vào mã..."
                 onKeyDown={handleKeyDown} onBlur={handleBlur} />
        </div>
        <ThongBao loi={loi} />

        {xemTruoc && (
          <div className="the">
            <div className="ten-lon">{xemTruoc.nhanDe}</div>
            <div className="dong"><span>Mã vạch</span><span>{xemTruoc.maVach}</span></div>
            <div className="dong"><span>Người mượn</span><span>{xemTruoc.hoTen} ({xemTruoc.maThe})</span></div>
            <div className="dong"><span>Ngày mượn</span><span>{ngay(xemTruoc.ngayMuon)}</span></div>
            <div className="dong"><span>Hạn trả</span><span>{ngay(xemTruoc.hanTra)}</span></div>
            <div className="dong">
              <span>Số ngày trễ</span>
              <span className={xemTruoc.soNgayTre > 0 ? 'chu-do' : ''}>{xemTruoc.soNgayTre}</span>
            </div>
            <div className="dong"><span>Phạt trễ hạn tạm tính</span><span>{tien(xemTruoc.phatTreTamTinh)}</span></div>

            <div className="nhan-o" style={{ marginTop: 14 }}>Tình trạng sách (bắt buộc)</div>
            <label className="chon"><input type="radio" name="tt" checked={tinhTrang === 'BINH_THUONG'} onChange={() => setTinhTrang('BINH_THUONG')} /> Bình thường</label>
            <label className="chon"><input type="radio" name="tt" checked={tinhTrang === 'HONG_NHE'} onChange={() => setTinhTrang('HONG_NHE')} /> Hỏng nhẹ (phạt {tien(xemTruoc.phatHongNhe)})</label>
            <label className="chon"><input type="radio" name="tt" checked={tinhTrang === 'HONG_NANG'} onChange={() => setTinhTrang('HONG_NANG')} /> Hỏng nặng (phạt {tien(xemTruoc.phatHongNang)})</label>

            <div className="dong tong"><span>Tổng tiền phạt</span><span>{tien(tongTam)}</span></div>
            <div className="chan-cot">
              {!hoiBaoMat ? (
                <button className="nut nut-phu" onClick={() => setHoiBaoMat(true)}>Báo mất sách</button>
              ) : (
                <button className="nut nut-do" onClick={xacNhanMat}>Chắc chắn báo mất?</button>
              )}
              <button className="nut nut-lon" onClick={xacNhanTra}>Xác nhận trả</button>
            </div>
          </div>
        )}
      </div>

      <div className="the cot-phai">
        <h3 style={{ marginTop: 0 }}>Đã xử lý trong phiên ({daTra.length})</h3>
        <table>
          <thead><tr><th>Sách</th><th>Bạn đọc</th><th>Tiền phạt</th><th>Kết quả</th></tr></thead>
          <tbody>
            {daTra.map((k, i) => (
              <tr key={i}>
                <td>{k.nhanDe}<div className="mo">{k.maVach}</div></td>
                <td>{k.hoTen}<div className="mo">{k.maThe}</div></td>
                <td>{k.loai === 'mat' ? tien(k.tongPhat) : tien(Number(k.tienPhatTre) + Number(k.tienPhatHong))}</td>
                <td>
                  {k.loai === 'mat' ? <span className="chu-do">Đã báo mất</span> : (
                    <>
                      Sách chuyển: <b>{k.trangThaiMoi}</b>
                      {k.giuChoMaThe && <div className="chu-vang">Giữ cho {k.giuChoHoTen} ({k.giuChoMaThe}), đặt lên kệ giữ sách</div>}
                      {k.trangThaiThe === 'BI_KHOA' && <div className="chu-do">Thẻ bạn đọc đang bị khóa</div>}
                    </>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
