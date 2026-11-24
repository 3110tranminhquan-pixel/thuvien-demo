// Hộp thoại nổi đơn giản (dùng cho form thêm, sửa)
export default function HopThoai({ tieuDe, onDong, children, rong }) {
  return (
    <div className="nen-mo" onMouseDown={onDong}>
      <div className={`hop-thoai ${rong ? 'rong' : ''}`} onMouseDown={(e) => e.stopPropagation()}>
        <div className="hop-thoai-dau">
          <h3>{tieuDe}</h3>
          <button className="nut-x" onClick={onDong}>✕</button>
        </div>
        {children}
      </div>
    </div>
  );
}
