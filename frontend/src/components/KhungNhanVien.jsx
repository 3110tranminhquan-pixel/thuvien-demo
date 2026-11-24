import { NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { TEN_VAI_TRO } from './dinhDang';

const MENU_THU_THU = [
  { nhom: 'Lưu thông', muc: [['/thu-thu/muon', 'Mượn sách'], ['/thu-thu/tra', 'Trả sách'], ['/thu-thu/gia-han', 'Gia hạn tại quầy']] },
  { nhom: 'Tài liệu', muc: [['/thu-thu/dau-sach', 'Đầu sách'], ['/thu-thu/danh-muc', 'Tác giả, thể loại, NXB']] },
  { nhom: 'Bạn đọc', muc: [['/thu-thu/ban-doc', 'Danh sách bạn đọc']] },
  { nhom: 'Vi phạm', muc: [['/thu-thu/phieu-phat', 'Phiếu phạt'], ['/thu-thu/qua-han', 'Danh sách quá hạn']] },
];

const MENU_QUAN_TRI = [
  { nhom: 'Hệ thống', muc: [['/quan-tri/tai-khoan', 'Tài khoản'], ['/quan-tri/tham-so', 'Tham số quy định'], ['/quan-tri/nhat-ky', 'Nhật ký']] },
  { nhom: 'Theo dõi', muc: [['/quan-tri/phieu-phat', 'Phiếu phạt'], ['/quan-tri/qua-han', 'Danh sách quá hạn']] },
];

// Bố cục khu thủ thư và quản trị: thanh menu bên trái, nội dung bên phải (giống bản mẫu Hình 3.28)
export default function KhungNhanVien() {
  const { nguoiDung, dangXuat } = useAuth();
  const dieuHuong = useNavigate();
  const { pathname } = useLocation();
  const menu = nguoiDung?.vaiTro === 'QUAN_TRI' ? MENU_QUAN_TRI : MENU_THU_THU;
  const tieuDe = menu.flatMap((n) => n.muc).find(([duongDan]) => pathname.startsWith(duongDan))?.[1] || '';

  const thoat = async () => {
    await dangXuat();
    dieuHuong('/dang-nhap');
  };

  return (
    <div className="khung-nv">
      <aside className="menu-trai">
        <NavLink to="/" className="logo">THƯ VIỆN</NavLink>
        {menu.map((n) => (
          <div key={n.nhom}>
            <div className="nhom-menu">{n.nhom}</div>
            {n.muc.map(([duongDan, ten]) => (
              <NavLink key={duongDan} to={duongDan} className="muc-menu">{ten}</NavLink>
            ))}
          </div>
        ))}
      </aside>
      <div className="phan-chinh">
        <div className="thanh-nv">
          <h1>{tieuDe}</h1>
          <div className="nguoi-nv">
            {TEN_VAI_TRO[nguoiDung?.vaiTro]}: {nguoiDung?.tenDangNhap} &nbsp;|&nbsp;
            <button className="nut-link" onClick={thoat}>Đăng xuất</button>
          </div>
        </div>
        <div className="noi-dung-nv">
          <Outlet />
        </div>
      </div>
    </div>
  );
}
