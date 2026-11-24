-- Sinh dữ liệu lớn để kiểm thử hiệu năng (NFR-01): 20.000 đầu sách, 50.000 bản sách
-- Chạy trên CSDL kiểm thử riêng, KHÔNG chạy trên CSDL thật.
-- ISBN sinh tự động không đúng chữ số kiểm tra, chỉ dùng để đo hiệu năng.
INSERT INTO dau_sach (isbn, nhan_de, nam_xb, gia_bia, loai_tai_lieu, the_loai_id, nxb_id)
SELECT '979' || lpad(g::text, 10, '0'),
       (ARRAY['Cơ sở', 'Nhập môn', 'Giáo trình', 'Bài tập', 'Kỹ thuật'])[1 + g % 5]
         || ' ' || (ARRAY['dữ liệu', 'lập trình', 'mạng máy tính', 'cầu đường', 'kinh tế vận tải',
                          'cơ học', 'điện tử', 'quản trị'])[1 + g % 8]
         || ' tập ' || g,
       2000 + g % 25,
       30000 + (g % 20) * 5000,
       'GIAO_TRINH',
       1 + g % 3,
       1 + g % 3
FROM generate_series(1, 20000) AS g;

INSERT INTO ban_sach (ma_vach, dau_sach_id, vi_tri_ke)
SELECT 'TN' || lpad(g::text, 8, '0'),
       (SELECT MIN(id) FROM dau_sach WHERE isbn LIKE '979%') + (g - 1) % 20000,
       'K' || lpad((g % 60)::text, 2, '0')
FROM generate_series(1, 50000) AS g;

ANALYZE dau_sach;
ANALYZE ban_sach;
