import { createContext, useContext, useEffect, useState } from 'react';
import { datAccessToken, datXuLyHetPhien } from '../api/ApiClient';
import * as api from '../api/thuVienApi';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [nguoiDung, setNguoiDung] = useState(null);
  const [dangTai, setDangTai] = useState(true);

  // Mở trang (hoặc F5) thì thử dùng refresh token trong cookie để khôi phục phiên đăng nhập
  useEffect(() => {
    datXuLyHetPhien(() => setNguoiDung(null));
    api.lamMoiPhien()
      .then((res) => {
        datAccessToken(res.accessToken);
        setNguoiDung(res.nguoiDung);
      })
      .catch(() => setNguoiDung(null))
      .finally(() => setDangTai(false));
  }, []);

  const dangNhap = async (tenDangNhap, matKhau) => {
    const res = await api.dangNhap(tenDangNhap, matKhau);
    datAccessToken(res.accessToken);
    setNguoiDung(res.nguoiDung);
    return res.nguoiDung;
  };

  const dangXuat = async () => {
    try {
      await api.dangXuat();
    } finally {
      datAccessToken(null);
      setNguoiDung(null);
    }
  };

  return (
    <AuthContext.Provider value={{ nguoiDung, dangTai, dangNhap, dangXuat }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
