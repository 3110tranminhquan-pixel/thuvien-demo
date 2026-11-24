import ApiClient from './ApiClient';

const d = (p) => p.then((res) => res.data);

// Xác thực
export const dangNhap = (tenDangNhap, matKhau) => d(ApiClient.post('/auth/login', { tenDangNhap, matKhau }));
export const lamMoiPhien = () => d(ApiClient.post('/auth/refresh'));
export const dangXuat = () => d(ApiClient.post('/auth/logout'));
export const doiMatKhau = (matKhauCu, matKhauMoi) => d(ApiClient.post('/auth/doi-mat-khau', { matKhauCu, matKhauMoi }));

// Tra cứu (công khai)
export const timSach = (thamSo) => d(ApiClient.get('/sach', { params: thamSo }));
export const chiTietSach = (id) => d(ApiClient.get(`/sach/${id}`));
export const layDanhMuc = (loai) => d(ApiClient.get(`/danh-muc/${loai}`));

// Quản lý tài liệu
export const dsDauSachQuanLy = (tuKhoa) => d(ApiClient.get('/quan-ly/dau-sach', { params: { tuKhoa } }));
export const themDauSach = (data) => d(ApiClient.post('/sach', data));
export const suaDauSach = (id, data) => d(ApiClient.put(`/sach/${id}`, data));
export const doiTrangThaiDauSach = (id, ngung) => d(ApiClient.put(`/sach/${id}/trang-thai`, null, { params: { ngung } }));
export const themBanSach = (id, soLuong, viTriKe) => d(ApiClient.post(`/sach/${id}/ban-sach`, { soLuong, viTriKe }));
export const capNhatBanSach = (id, data) => d(ApiClient.put(`/ban-sach/${id}`, data));
export const luuDanhMuc = (loai, id, data) =>
  d(id ? ApiClient.put(`/danh-muc/${loai}/${id}`, data) : ApiClient.post(`/danh-muc/${loai}`, data));
export const xoaDanhMuc = (loai, id) => d(ApiClient.delete(`/danh-muc/${loai}/${id}`));

// Bạn đọc
export const dsBanDoc = (tuKhoa) => d(ApiClient.get('/ban-doc', { params: { tuKhoa } }));
export const timBanDocTheoThe = (maThe) => d(ApiClient.get(`/ban-doc/the/${encodeURIComponent(maThe)}`));
export const hoSoBanDoc = (id) => d(ApiClient.get(`/ban-doc/${id}/lich-su`));
export const capThe = (data) => d(ApiClient.post('/ban-doc', data));
export const suaBanDoc = (id, data) => d(ApiClient.put(`/ban-doc/${id}`, data));
export const khoaThe = (id, lyDo) => d(ApiClient.post(`/ban-doc/${id}/khoa`, { lyDo }));
export const moKhoaThe = (id) => d(ApiClient.post(`/ban-doc/${id}/mo-khoa`));
export const dsLoaiBanDoc = () => d(ApiClient.get('/loai-ban-doc'));

// Lưu thông
export const kiemTraSach = (banDocId, maVach, dsMaVachDaQuet) =>
  d(ApiClient.post('/muon/kiem-tra-sach', { banDocId, maVach, dsMaVachDaQuet }));
export const taoPhieuMuon = (banDocId, dsMaVach) => d(ApiClient.post('/muon', { banDocId, dsMaVach }));
export const xemTruocTra = (maVach) => d(ApiClient.get(`/tra/xem-truoc/${encodeURIComponent(maVach)}`));
export const traSach = (maVach, tinhTrang) => d(ApiClient.post('/tra', { maVach, tinhTrang }));
export const giaHan = (chiTietId) => d(ApiClient.post(`/gia-han/${chiTietId}`));

// Đặt trước, tài khoản của tôi
export const datTruoc = (dauSachId) => d(ApiClient.post('/dat-truoc', { dauSachId }));
export const huyDatTruoc = (id) => d(ApiClient.delete(`/dat-truoc/${id}`));
export const hoSoCuaToi = () => d(ApiClient.get('/toi/ho-so'));

// Vi phạm, báo cáo
export const dsPhieuPhat = (trangThai, tuKhoa) => d(ApiClient.get('/phat', { params: { trangThai, tuKhoa } }));
export const thanhToanPhat = (id) => d(ApiClient.post(`/phat/${id}/thanh-toan`));
export const huyPhieuPhat = (id, lyDo) => d(ApiClient.post(`/phat/${id}/huy`, { lyDo }));
export const baoMatSach = (maVach) => d(ApiClient.post('/phat/bao-mat', { maVach }));
export const dsQuaHan = () => d(ApiClient.get('/bao-cao/qua-han'));

// Quản trị
export const dsTaiKhoan = () => d(ApiClient.get('/quan-tri/tai-khoan'));
export const taoTaiKhoan = (data) => d(ApiClient.post('/quan-tri/tai-khoan', data));
export const capNhatTaiKhoan = (id, data) => d(ApiClient.put(`/quan-tri/tai-khoan/${id}`, data));
export const dsThamSo = () => d(ApiClient.get('/quan-tri/tham-so'));
export const suaThamSo = (ma, giaTri) => d(ApiClient.put(`/quan-tri/tham-so/${ma}`, { giaTri }));
export const dsLoaiBanDocQuanTri = () => d(ApiClient.get('/quan-tri/loai-ban-doc'));
export const suaLoaiBanDoc = (id, data) => d(ApiClient.put(`/quan-tri/loai-ban-doc/${id}`, data));
export const dsNhatKy = () => d(ApiClient.get('/quan-tri/nhat-ky'));
export const chayTacVu = () => d(ApiClient.post('/quan-tri/chay-tac-vu'));
