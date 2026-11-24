// Ô thông báo lỗi / thành công dùng chung
export default function ThongBao({ loi, ok }) {
  if (loi) return <div className="hop-loi">{loi}</div>;
  if (ok) return <div className="hop-ok">{ok}</div>;
  return null;
}
