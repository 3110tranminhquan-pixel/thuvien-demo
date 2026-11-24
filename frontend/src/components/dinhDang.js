// Các hàm định dạng hiển thị dùng chung

export const tien = (so) =>
  so == null ? '' : Number(so).toLocaleString('vi-VN') + ' đ';

export const ngay = (giaTri) => {
  if (!giaTri) return '';
  const [n, t, d] = giaTri.slice(0, 10).split('-');
  return `${d}/${t}/${n}`;
};

export const ngayGio = (giaTri) => (giaTri ? `${giaTri.slice(11, 16)} ${ngay(giaTri)}` : '');

export const TEN_TRANG_THAI_THE = {
  HOAT_DONG: 'Hoạt động', BI_KHOA: 'Bị khóa', HET_HAN: 'Hết hạn', NGUNG: 'Ngừng sử dụng',
};

export const TEN_TRANG_THAI_DAT = {
  DANG_CHO: 'Đang chờ', DANG_GIU: 'Đang giữ sách', DA_NHAN: 'Đã nhận', DA_HUY: 'Đã hủy',
};

export const TEN_TRANG_THAI_PHAT = {
  CHUA_THANH_TOAN: 'Chưa thanh toán', DA_THANH_TOAN: 'Đã thanh toán', DA_HUY: 'Đã hủy',
};

export const TEN_VI_PHAM = {
  TRE_HAN: 'Trả trễ hạn', HONG_NHE: 'Hỏng nhẹ', HONG_NANG: 'Hỏng nặng', MAT_SACH: 'Mất sách',
};

export const TEN_TINH_TRANG = {
  BINH_THUONG: 'Bình thường', HONG_NHE: 'Hỏng nhẹ', HONG_NANG: 'Hỏng nặng', MAT: 'Mất',
};

export const TEN_LOAI_TAI_LIEU = {
  GIAO_TRINH: 'Giáo trình', SACH_TK: 'Sách tham khảo (cho mượn)', THAM_KHAO: 'Tài liệu tham khảo (đọc tại chỗ)',
};

export const TEN_VAI_TRO = { BAN_DOC: 'Bạn đọc', THU_THU: 'Thủ thư', QUAN_TRI: 'Quản trị viên' };

// Màu nhãn theo trạng thái
export const mauTrangThai = (ma) => {
  if (['SAN_SANG', 'HOAT_DONG', 'DA_THANH_TOAN', 'DA_NHAN', 'DANG_DUNG'].includes(ma)) return 'xanh';
  if (['QUA_HAN', 'BI_KHOA', 'CHUA_THANH_TOAN', 'MAT', 'HET_HAN'].includes(ma)) return 'do';
  if (['DANG_GIU', 'DANG_CHO', 'DANG_SUA'].includes(ma)) return 'vang';
  return 'xam';
};
