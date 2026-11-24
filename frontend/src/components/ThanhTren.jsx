import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

// Thanh trên cùng của khu công khai và khu bạn đọc
export default function ThanhTren() {
  const { nguoiDung, dangXuat } = useAuth();
  const dieuHuong = useNavigate();

  const thoat = async () => {
    await dangXuat();
    dieuHuong('/');
  };

  return (
    <header className="thanh-tren">
      <Link to="/" className="logo-chu">THƯ VIỆN</Link>
      <nav>
        <Link to="/">Tra cứu</Link>
        {nguoiDung?.vaiTro === 'BAN_DOC' && <Link to="/tai-khoan">Tài khoản của tôi</Link>}
        {nguoiDung?.vaiTro === 'THU_THU' && <Link to="/thu-thu/muon">Khu thủ thư</Link>}
        {nguoiDung?.vaiTro === 'QUAN_TRI' && <Link to="/quan-tri/tai-khoan">Khu quản trị</Link>}
        {nguoiDung ? (
          <>
            <span className="ten-nguoi">{nguoiDung.hoTen}</span>
            <button className="nut-link" onClick={thoat}>Đăng xuất</button>
          </>
        ) : (
          <Link to="/dang-nhap" className="nut nut-nho">Đăng nhập</Link>
        )}
      </nav>
    </header>
  );
}
