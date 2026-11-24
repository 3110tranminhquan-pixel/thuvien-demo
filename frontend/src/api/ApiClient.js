import axios from 'axios';

// Access token chỉ giữ trong bộ nhớ (không lưu localStorage để tránh bị đọc trộm qua XSS).
// Refresh token nằm trong cookie HttpOnly do backend đặt, JavaScript không đọc được.
let accessToken = null;
let khiHetPhien = () => {};

export function datAccessToken(token) {
  accessToken = token;
}

export function datXuLyHetPhien(ham) {
  khiHetPhien = ham;
}

const ApiClient = axios.create({ baseURL: '/api', withCredentials: true });

ApiClient.interceptors.request.use((config) => {
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`;
  }
  return config;
});

// Gặp 401 thì thử xin access token mới bằng refresh token, được thì gửi lại yêu cầu cũ một lần
let dangLamMoi = null;

ApiClient.interceptors.response.use(
  (res) => res,
  async (error) => {
    const yeuCau = error.config;
    const laApiXacThuc = yeuCau?.url?.startsWith('/auth/');
    if (error.response?.status === 401 && !yeuCau._daThuLai && !laApiXacThuc) {
      yeuCau._daThuLai = true;
      try {
        if (!dangLamMoi) {
          dangLamMoi = axios.post('/api/auth/refresh', null, { withCredentials: true });
        }
        const res = await dangLamMoi;
        datAccessToken(res.data.accessToken);
        return ApiClient(yeuCau);
      } catch (e) {
        datAccessToken(null);
        khiHetPhien();
      } finally {
        dangLamMoi = null;
      }
    }
    return Promise.reject(error);
  }
);

// Lấy câu thông báo tiếng Việt từ phản hồi lỗi của backend
export function layLoi(error) {
  return error?.response?.data?.message || 'Không kết nối được máy chủ, vui lòng thử lại';
}

export default ApiClient;
