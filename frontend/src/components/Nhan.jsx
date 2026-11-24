import { mauTrangThai } from './dinhDang';

// Nhãn màu nhỏ hiển thị trạng thái
export default function Nhan({ ma, ten }) {
  return <span className={`nhan nhan-${mauTrangThai(ma)}`}>{ten || ma}</span>;
}
