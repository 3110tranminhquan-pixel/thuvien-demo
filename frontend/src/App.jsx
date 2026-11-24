import { Navigate, Outlet, Route, Routes } from 'react-router-dom';
import KhungNhanVien from './components/KhungNhanVien';
import ThanhTren from './components/ThanhTren';
import NhatKy from './pages/admin/NhatKy';
import TaiKhoan from './pages/admin/TaiKhoan';
import ThamSo from './pages/admin/ThamSo';
import ChiTietSach from './pages/public/ChiTietSach';
import DangNhap from './pages/public/DangNhap';
import TrangChu from './pages/public/TrangChu';
import TaiKhoanCuaToi from './pages/reader/TaiKhoanCuaToi';
import BanDoc from './pages/staff/BanDoc';
import DanhMuc from './pages/staff/DanhMuc';
import DanhSachQuaHan from './pages/staff/DanhSachQuaHan';
import GiaHanTaiQuay from './pages/staff/GiaHanTaiQuay';
import MuonSach from './pages/staff/MuonSach';
import PhieuPhat from './pages/staff/PhieuPhat';
import QuanLyDauSach from './pages/staff/QuanLyDauSach';
import TraSach from './pages/staff/TraSach';
import ChanTheoVaiTro from './routes/ChanTheoVaiTro';

// Khu công khai và khu bạn đọc dùng chung thanh trên cùng
function KhungCongKhai() {
  return (
    <>
      <ThanhTren />
      <Outlet />
    </>
  );
}

export default function App() {
  return (
    <Routes>
      <Route element={<KhungCongKhai />}>
        <Route path="/" element={<TrangChu />} />
        <Route path="/sach/:id" element={<ChiTietSach />} />
        <Route path="/dang-nhap" element={<DangNhap />} />
        <Route path="/tai-khoan" element={<ChanTheoVaiTro vaiTro={['BAN_DOC']}><TaiKhoanCuaToi /></ChanTheoVaiTro>} />
      </Route>

      <Route path="/thu-thu" element={<ChanTheoVaiTro vaiTro={['THU_THU']}><KhungNhanVien /></ChanTheoVaiTro>}>
        <Route index element={<Navigate to="muon" replace />} />
        <Route path="muon" element={<MuonSach />} />
        <Route path="tra" element={<TraSach />} />
        <Route path="gia-han" element={<GiaHanTaiQuay />} />
        <Route path="dau-sach" element={<QuanLyDauSach />} />
        <Route path="danh-muc" element={<DanhMuc />} />
        <Route path="ban-doc" element={<BanDoc />} />
        <Route path="phieu-phat" element={<PhieuPhat />} />
        <Route path="qua-han" element={<DanhSachQuaHan />} />
      </Route>

      <Route path="/quan-tri" element={<ChanTheoVaiTro vaiTro={['QUAN_TRI']}><KhungNhanVien /></ChanTheoVaiTro>}>
        <Route index element={<Navigate to="tai-khoan" replace />} />
        <Route path="tai-khoan" element={<TaiKhoan />} />
        <Route path="tham-so" element={<ThamSo />} />
        <Route path="nhat-ky" element={<NhatKy />} />
        <Route path="phieu-phat" element={<PhieuPhat />} />
        <Route path="qua-han" element={<DanhSachQuaHan />} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
