import { useEffect, useRef } from 'react';

// Máy quét mã vạch hoạt động như bàn phím gõ rất nhanh rồi tự nhấn Enter.
// Hook này giữ con trỏ luôn ở ô nhập, nhận Enter thì gọi hàm xử lý rồi xóa ô để chờ mã tiếp theo.
export function useBarcodeInput(onScan) {
  const inputRef = useRef(null);

  useEffect(() => { inputRef.current?.focus(); }, []);

  const handleKeyDown = (e) => {
    const ma = e.target.value.trim();
    if (e.key === 'Enter' && ma !== '') {
      onScan(ma);
      e.target.value = '';
    }
  };

  // Thủ thư lỡ bấm ra ngoài thì ô nhập tự lấy lại con trỏ
  const handleBlur = () => setTimeout(() => inputRef.current?.focus(), 100);

  return { inputRef, handleKeyDown, handleBlur };
}
