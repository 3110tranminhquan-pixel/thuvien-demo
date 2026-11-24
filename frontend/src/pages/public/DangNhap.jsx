import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { layLoi } from '../../api/ApiClient';
import ThongBao from '../../components/ThongBao';
import { useAuth } from '../../hooks/useAuth';

export default function DangNhap() {
  const { dangNhap } = useAuth();
  const dieuHuong = useNavigate();
  const [ten, setTen] = useState('');
  const [matKhau, setMatKhau] = useState('');
  const [loi, setLoi] = useState('');
  const [dangGui, setDangGui] = useState(false);

  const gui = async (e) => {
    e.preventDefault();
    setLoi('');
    setDangGui(true);
    try {
      const nd = await dangNhap(ten, matKhau);
      if (nd.vaiTro === 'THU_THU') dieuHuong('/thu-thu/muon');
      else if (nd.vaiTro === 'QUAN_TRI') dieuHuong('/quan-tri/tai-khoan');
      else dieuHuong('/tai-khoan');
    } catch (err) {
      setLoi(layLoi(err));
    } finally {
      setDangGui(false);
    }
  };

  return (
    <div className="trang-giua">
      <form className="the hop-dang-nhap" onSubmit={gui}>
        <h2>Đăng nhập</h2>
        <p className="mo">Bạn đọc dùng mã thẻ làm tên đăng nhập.</p>
        <label>Tên đăng nhập</label>
        <input value={ten} onChange={(e) => setTen(e.target.value)} autoFocus />
        <label>Mật khẩu</label>
        <input type="password" value={matKhau} onChange={(e) => setMatKhau(e.target.value)} />
        <ThongBao loi={loi} />
        <button className="nut nut-rong" disabled={dangGui}>{dangGui ? 'Đang đăng nhập...' : 'Đăng nhập'}</button>
      </form>
    </div>
  );
}
