import { Navigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

// Chặn trang theo vai trò. Đây chỉ để giao diện gọn, việc kiểm tra quyền thật sự nằm ở backend.
export default function ChanTheoVaiTro({ vaiTro, children }) {
  const { nguoiDung, dangTai } = useAuth();
  if (dangTai) return <div className="dang-tai">Đang tải...</div>;
  if (!nguoiDung) return <Navigate to="/dang-nhap" replace />;
  if (!vaiTro.includes(nguoiDung.vaiTro)) return <Navigate to="/" replace />;
  return children;
}
