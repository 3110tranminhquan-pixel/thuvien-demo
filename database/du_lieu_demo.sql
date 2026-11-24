-- Dữ liệu demo và kiểm thử: chạy SAU thuvien.sql.
-- Ngày tháng tính theo ngày chạy script (CURRENT_DATE) nên chạy lúc nào cũng có đủ tình huống.
-- Sau khi chạy script, đăng nhập admin -> Nhật ký -> "Chạy tác vụ định kỳ ngay" (hoặc khởi động lại backend)
-- để hệ thống tự đánh dấu quá hạn, khóa thẻ theo BR-14 và xử lý lượt giữ sách hết hạn.
-- Mật khẩu mọi tài khoản: Thuvien@123
--
-- Các bạn đọc chuẩn bị sẵn cho ca kiểm thử (xem thêm file kich_ban_kiem_thu):
--   BD000001 An      : SV đang mượn 3/5, mượn thêm được          BD000002 Bình  : quá hạn 2 ngày
--   BD000003 Châu    : GV quá hạn 35 ngày (sẽ bị khóa)            BD000006 Phúc  : SV chưa mượn cuốn nào
--   BD000007 Giang   : SV đang mượn 4                             BD000008 Hiếu  : SV đang mượn 5
--   BD000009 Khoa    : GV đang mượn 9                             BD000010 Lan   : GV đang mượn 10
--   BD000011 Minh    : thẻ khóa thủ công                          BD000014 Quyên : nợ phạt 10.000 đ
--   BD000015 Sang    : thẻ hết hạn                                BD000016..22   : quá hạn 1, 8, 10, 11, 16, 17, 30, 31 ngày
--   BD000023 Diễm    : nợ đúng 50.000 đ                           BD000024 Đạt   : nợ 55.000 đ (sẽ bị khóa)

-- 1. Tác giả, đầu sách, bản sách
INSERT INTO tac_gia (ho_ten) VALUES
('Bùi Quang H'),
('Hoàng Thị E'),
('Lê Văn D'),
('Ngô Thị K'),
('Trịnh Văn L'),
('Võ Thanh F'),
('Đặng Minh G');

INSERT INTO dau_sach (isbn, nhan_de, nam_xb, gia_bia, loai_tai_lieu, the_loai_id, nxb_id) VALUES
('9786040000262', 'Lập trình Java cơ bản', 2023, 95000, 'GIAO_TRINH', 1, 3),
('9786040000279', 'Mạng máy tính', 2022, 85000, 'GIAO_TRINH', 1, 1),
('9786040000286', 'Cấu trúc dữ liệu và giải thuật', 2021, 70000, 'GIAO_TRINH', 1, 3),
('9786040000293', 'Hệ điều hành', 2022, 75000, 'SACH_TK', 1, 1),
('9786040000309', 'Vẽ kỹ thuật', 2020, 65000, 'GIAO_TRINH', 2, 2),
('9786040000316', 'Kinh tế vi mô', 2023, 55000, 'GIAO_TRINH', 3, 3),
('9786040000323', 'Kỹ thuật lập trình C++', 2021, 70000, 'GIAO_TRINH', 1, 1),
('9786040000330', 'Toán rời rạc', 2020, 60000, 'GIAO_TRINH', 1, 3),
('9786040000347', 'Xác suất thống kê', 2022, 55000, 'GIAO_TRINH', 1, 3),
('9786040000354', 'Kiến trúc máy tính', 2021, 90000, 'GIAO_TRINH', 1, 1),
('9786040000361', 'Trí tuệ nhân tạo', 2024, 120000, 'SACH_TK', 1, 3),
('9786040000378', 'Lập trình Python', 2023, 85000, 'GIAO_TRINH', 1, 3),
('9786040000385', 'Phát triển ứng dụng web', 2024, 95000, 'GIAO_TRINH', 1, 3),
('9786040000392', 'An toàn thông tin', 2023, 90000, 'GIAO_TRINH', 1, 1),
('9786040000408', 'Kinh tế vĩ mô', 2022, 55000, 'GIAO_TRINH', 3, 3),
('9786040000415', 'Quản trị học', 2021, 65000, 'GIAO_TRINH', 3, 1),
('9786040000422', 'Marketing căn bản', 2022, 60000, 'SACH_TK', 3, 3),
('9786040000439', 'Logistics và chuỗi cung ứng', 2023, 110000, 'SACH_TK', 3, 2),
('9786040000446', 'Cơ học kết cấu', 2019, 100000, 'GIAO_TRINH', 2, 2),
('9786040000453', 'Vật liệu xây dựng', 2020, 75000, 'GIAO_TRINH', 2, 2),
('9786040000460', 'Thủy lực cơ sở', 2018, 70000, 'GIAO_TRINH', 2, 2),
('9786040000477', 'Thiết kế đường ô tô', 2021, 130000, 'GIAO_TRINH', 2, 2),
('9786040000484', 'Tiếng Anh chuyên ngành CNTT', 2022, 50000, 'SACH_TK', 1, 1),
('9786040000491', 'Sổ tay kỹ sư cầu đường', 2019, 300000, 'THAM_KHAO', 2, 2),
('9786040000507', 'Từ điển Kinh tế Anh – Việt', 2020, 200000, 'THAM_KHAO', 3, 3);

INSERT INTO dau_sach_tac_gia (dau_sach_id, tac_gia_id)
SELECT ds.id, tg.id FROM (VALUES
    ('9786040000262', 'Lê Văn D'),
    ('9786040000279', 'Trần Văn B'),
    ('9786040000286', 'Lê Văn D'),
    ('9786040000286', 'Nguyễn Văn A'),
    ('9786040000293', 'Hoàng Thị E'),
    ('9786040000309', 'Phạm Thị C'),
    ('9786040000316', 'Hoàng Thị E'),
    ('9786040000323', 'Võ Thanh F'),
    ('9786040000330', 'Đặng Minh G'),
    ('9786040000347', 'Đặng Minh G'),
    ('9786040000354', 'Võ Thanh F'),
    ('9786040000361', 'Bùi Quang H'),
    ('9786040000378', 'Bùi Quang H'),
    ('9786040000385', 'Lê Văn D'),
    ('9786040000392', 'Trần Văn B'),
    ('9786040000408', 'Ngô Thị K'),
    ('9786040000415', 'Ngô Thị K'),
    ('9786040000422', 'Ngô Thị K'),
    ('9786040000439', 'Trịnh Văn L'),
    ('9786040000446', 'Phạm Thị C'),
    ('9786040000453', 'Trịnh Văn L'),
    ('9786040000460', 'Phạm Thị C'),
    ('9786040000477', 'Trịnh Văn L'),
    ('9786040000484', 'Hoàng Thị E'),
    ('9786040000491', 'Phạm Thị C'),
    ('9786040000507', 'Ngô Thị K')
) AS v(isbn, tac_gia) JOIN dau_sach ds ON ds.isbn = v.isbn JOIN tac_gia tg ON tg.ho_ten = v.tac_gia;

INSERT INTO ban_sach (ma_vach, dau_sach_id, vi_tri_ke)
SELECT v.ma, ds.id, v.ke FROM (VALUES
    ('GT00000059', '9786040000217', 'K01-T2'),
    ('GT00000057', '9786040000224', 'K01-T3'),
    ('GT00000058', '9786040000224', 'K01-T3'),
    ('GT00000007', '9786040000262', 'K02-T1'),
    ('GT00000008', '9786040000262', 'K02-T1'),
    ('GT00000009', '9786040000279', 'K03-T2'),
    ('GT00000010', '9786040000279', 'K03-T2'),
    ('GT00000011', '9786040000279', 'K03-T2'),
    ('GT00000012', '9786040000286', 'K04-T3'),
    ('ST00000003', '9786040000293', 'K05-T4'),
    ('GT00000013', '9786040000309', 'K06-T1'),
    ('GT00000014', '9786040000316', 'K07-T2'),
    ('GT00000015', '9786040000316', 'K07-T2'),
    ('GT00000016', '9786040000323', 'K08-T3'),
    ('GT00000017', '9786040000323', 'K08-T3'),
    ('GT00000018', '9786040000323', 'K08-T3'),
    ('GT00000019', '9786040000330', 'K09-T4'),
    ('GT00000020', '9786040000330', 'K09-T4'),
    ('GT00000021', '9786040000330', 'K09-T4'),
    ('GT00000022', '9786040000347', 'K10-T1'),
    ('GT00000023', '9786040000347', 'K10-T1'),
    ('GT00000024', '9786040000347', 'K10-T1'),
    ('GT00000025', '9786040000354', 'K02-T2'),
    ('GT00000026', '9786040000354', 'K02-T2'),
    ('GT00000027', '9786040000354', 'K02-T2'),
    ('ST00000004', '9786040000361', 'K03-T3'),
    ('ST00000005', '9786040000361', 'K03-T3'),
    ('ST00000006', '9786040000361', 'K03-T3'),
    ('GT00000028', '9786040000378', 'K04-T4'),
    ('GT00000029', '9786040000378', 'K04-T4'),
    ('GT00000030', '9786040000378', 'K04-T4'),
    ('GT00000031', '9786040000378', 'K04-T4'),
    ('GT00000032', '9786040000385', 'K05-T1'),
    ('GT00000033', '9786040000385', 'K05-T1'),
    ('GT00000034', '9786040000385', 'K05-T1'),
    ('GT00000035', '9786040000385', 'K05-T1'),
    ('GT00000036', '9786040000392', 'K06-T2'),
    ('GT00000037', '9786040000392', 'K06-T2'),
    ('GT00000038', '9786040000392', 'K06-T2'),
    ('GT00000039', '9786040000408', 'K07-T3'),
    ('GT00000040', '9786040000408', 'K07-T3'),
    ('GT00000041', '9786040000408', 'K07-T3'),
    ('GT00000042', '9786040000415', 'K08-T4'),
    ('GT00000043', '9786040000415', 'K08-T4'),
    ('GT00000044', '9786040000415', 'K08-T4'),
    ('ST00000007', '9786040000422', 'K09-T1'),
    ('ST00000008', '9786040000422', 'K09-T1'),
    ('ST00000009', '9786040000422', 'K09-T1'),
    ('ST00000010', '9786040000439', 'K10-T2'),
    ('ST00000011', '9786040000439', 'K10-T2'),
    ('GT00000045', '9786040000446', 'K02-T3'),
    ('GT00000046', '9786040000446', 'K02-T3'),
    ('GT00000047', '9786040000446', 'K02-T3'),
    ('GT00000048', '9786040000446', 'K02-T3'),
    ('GT00000049', '9786040000453', 'K03-T4'),
    ('GT00000050', '9786040000453', 'K03-T4'),
    ('GT00000051', '9786040000453', 'K03-T4'),
    ('GT00000052', '9786040000460', 'K04-T1'),
    ('GT00000053', '9786040000460', 'K04-T1'),
    ('GT00000054', '9786040000460', 'K04-T1'),
    ('GT00000055', '9786040000477', 'K05-T2'),
    ('GT00000056', '9786040000477', 'K05-T2'),
    ('ST00000012', '9786040000484', 'K06-T3'),
    ('ST00000013', '9786040000484', 'K06-T3'),
    ('TK00000002', '9786040000491', 'K07-T4'),
    ('TK00000003', '9786040000507', 'K08-T1')
) AS v(ma, isbn, ke) JOIN dau_sach ds ON ds.isbn = v.isbn;

-- 2. Bạn đọc mới, mỗi người một tài khoản đăng nhập (tên đăng nhập = mã thẻ)
INSERT INTO nguoi_dung (ten_dang_nhap, mat_khau_hash, vai_tro_id) VALUES
('BD000004', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 1),
('BD000005', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 1),
('BD000006', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 1),
('BD000007', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 1),
('BD000008', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 1),
('BD000009', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 1),
('BD000010', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 1),
('BD000011', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 1),
('BD000012', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 1),
('BD000013', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 1),
('BD000014', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 1),
('BD000015', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 1),
('BD000016', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 1),
('BD000017', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 1),
('BD000018', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 1),
('BD000019', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 1),
('BD000020', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 1),
('BD000021', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 1),
('BD000022', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 1),
('BD000023', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 1),
('BD000024', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 1),
('BD000025', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 1);

INSERT INTO ban_doc (ma_the, ho_ten, email, don_vi, ngay_cap, ngay_het_han, trang_thai, ly_do_khoa, khoa_thu_cong, loai_id, nguoi_dung_id)
SELECT v.ma, v.ten, v.email, v.dv, v.cap, v.het, v.tt, v.ly, v.tc, v.loai, nd.id FROM (VALUES
    ('BD000004', 'Phạm Quốc Dũng', 'dung.pq@truong.edu.vn', 'Khoa Công trình', DATE '2025-09-01', DATE '2029-09-01', 'HOAT_DONG', NULL, FALSE, 1),
    ('BD000005', 'Đỗ Thị Em', 'em.dt@truong.edu.vn', 'Khoa CNTT', DATE '2025-09-01', DATE '2029-09-01', 'HOAT_DONG', NULL, FALSE, 1),
    ('BD000006', 'Huỳnh Gia Phúc', 'phuc.hg@truong.edu.vn', 'Khoa CNTT', DATE '2025-09-01', DATE '2029-09-01', 'HOAT_DONG', NULL, FALSE, 1),
    ('BD000007', 'Vũ Thị Giang', 'giang.vt@truong.edu.vn', 'Khoa Kinh tế', DATE '2025-09-01', DATE '2029-09-01', 'HOAT_DONG', NULL, FALSE, 1),
    ('BD000008', 'Lý Minh Hiếu', 'hieu.lm@truong.edu.vn', 'Khoa CNTT', DATE '2025-09-01', DATE '2029-09-01', 'HOAT_DONG', NULL, FALSE, 1),
    ('BD000009', 'Trương Văn Khoa', 'khoa.tv@truong.edu.vn', 'Khoa CNTT', DATE '2024-09-01', DATE '2030-09-01', 'HOAT_DONG', NULL, FALSE, 2),
    ('BD000010', 'Mai Thị Lan', 'lan.mt@truong.edu.vn', 'Khoa Kinh tế', DATE '2024-09-01', DATE '2030-09-01', 'HOAT_DONG', NULL, FALSE, 2),
    ('BD000011', 'Đinh Công Minh', 'minh.dc@truong.edu.vn', 'Khoa Cơ khí', DATE '2025-09-01', DATE '2029-09-01', 'BI_KHOA', 'Báo mất thẻ, chờ cấp lại', TRUE, 1),
    ('BD000012', 'Châu Ngọc Nhi', 'nhi.cn@truong.edu.vn', 'Khoa CNTT', DATE '2025-09-01', DATE '2029-09-01', 'HOAT_DONG', NULL, FALSE, 1),
    ('BD000013', 'Phan Thanh Phong', 'phong.pt@truong.edu.vn', 'Khoa Công trình', DATE '2025-09-01', DATE '2029-09-01', 'HOAT_DONG', NULL, FALSE, 1),
    ('BD000014', 'La Thị Quyên', 'quyen.lt@truong.edu.vn', 'Khoa Kinh tế', DATE '2025-09-01', DATE '2029-09-01', 'HOAT_DONG', NULL, FALSE, 1),
    ('BD000015', 'Tạ Văn Sang', 'sang.tv@truong.edu.vn', 'Khoa Điện', DATE '2022-09-01', CURRENT_DATE - 10, 'HET_HAN', NULL, FALSE, 1),
    ('BD000016', 'Kiều Thị Thảo', 'thao.kt@truong.edu.vn', 'Khoa CNTT', DATE '2025-09-01', DATE '2029-09-01', 'HOAT_DONG', NULL, FALSE, 1),
    ('BD000017', 'Dương Minh Tuấn', 'tuan.dm@truong.edu.vn', 'Khoa Công trình', DATE '2025-09-01', DATE '2029-09-01', 'HOAT_DONG', NULL, FALSE, 1),
    ('BD000018', 'Lâm Thị Uyên', 'uyen.lt@truong.edu.vn', 'Khoa Kinh tế', DATE '2025-09-01', DATE '2029-09-01', 'HOAT_DONG', NULL, FALSE, 1),
    ('BD000019', 'Hà Quang Vinh', 'vinh.hq@truong.edu.vn', 'Khoa CNTT', DATE '2025-09-01', DATE '2029-09-01', 'HOAT_DONG', NULL, FALSE, 1),
    ('BD000020', 'Cao Thị Xuân', 'xuan.ct@truong.edu.vn', 'Khoa CNTT', DATE '2025-09-01', DATE '2029-09-01', 'HOAT_DONG', NULL, FALSE, 1),
    ('BD000021', 'Tôn Thất Yên', 'yen.tt@truong.edu.vn', 'Khoa Cơ khí', DATE '2025-09-01', DATE '2029-09-01', 'HOAT_DONG', NULL, FALSE, 1),
    ('BD000022', 'Nghiêm Văn Bảo', 'bao.nv@truong.edu.vn', 'Khoa Công trình', DATE '2024-09-01', DATE '2030-09-01', 'HOAT_DONG', NULL, FALSE, 2),
    ('BD000023', 'Quách Thị Diễm', 'diem.qt@truong.edu.vn', 'Khoa Kinh tế', DATE '2025-09-01', DATE '2029-09-01', 'HOAT_DONG', NULL, FALSE, 1),
    ('BD000024', 'Mạc Văn Đạt', 'dat.mv@truong.edu.vn', 'Khoa Điện', DATE '2025-09-01', DATE '2029-09-01', 'HOAT_DONG', NULL, FALSE, 1),
    ('BD000025', 'Ông Thị Hằng', 'hang.ot@truong.edu.vn', 'Khoa CNTT', DATE '2025-09-01', DATE '2029-09-01', 'HOAT_DONG', NULL, FALSE, 1)
) AS v(ma, ten, email, dv, cap, het, tt, ly, tc, loai) JOIN nguoi_dung nd ON nd.ten_dang_nhap = v.ma;

-- 3. Hàm tạm: tạo một phiếu mượn một cuốn (chỉ tồn tại trong phiên chạy script)
CREATE OR REPLACE FUNCTION pg_temp.muon(p_ma_the TEXT, p_ma_vach TEXT, p_ngay_muon TIMESTAMP, p_han_tra DATE,
                                        p_so_lan_gh INT DEFAULT 0, p_ngay_tra TIMESTAMP DEFAULT NULL,
                                        p_tinh_trang TEXT DEFAULT NULL)
RETURNS BIGINT AS $$
DECLARE v_pm BIGINT; v_ct BIGINT;
BEGIN
    INSERT INTO phieu_muon (ban_doc_id, thu_thu_id, ngay_muon)
    SELECT bd.id, nd.id, p_ngay_muon FROM ban_doc bd, nguoi_dung nd
    WHERE bd.ma_the = p_ma_the AND nd.ten_dang_nhap = 'thuthu01'
    RETURNING id INTO v_pm;
    INSERT INTO chi_tiet_muon (phieu_muon_id, ban_sach_id, han_tra, ngay_tra, so_lan_gia_han, tinh_trang_khi_tra)
    SELECT v_pm, id, p_han_tra, p_ngay_tra, p_so_lan_gh, p_tinh_trang FROM ban_sach WHERE ma_vach = p_ma_vach
    RETURNING id INTO v_ct;
    RETURN v_ct;
END $$ LANGUAGE plpgsql;

-- 4. Lượt mượn chưa trả
SELECT pg_temp.muon('BD000001', 'GT00000007', (CURRENT_DATE - 5) + TIME '09:07', CURRENT_DATE + 9, 0);
SELECT pg_temp.muon('BD000001', 'ST00000001', (CURRENT_DATE - 5) + TIME '09:07', CURRENT_DATE + 9, 0);
SELECT pg_temp.muon('BD000001', 'GT00000009', (CURRENT_DATE - 12) + TIME '09:07', CURRENT_DATE + 2, 0);
SELECT pg_temp.muon('BD000002', 'GT00000006', (CURRENT_DATE - 16) + TIME '10:14', CURRENT_DATE - 2, 0);
SELECT pg_temp.muon('BD000003', 'ST00000002', (CURRENT_DATE - 65) + TIME '11:21', CURRENT_DATE - 35, 0);
SELECT pg_temp.muon('BD000007', 'GT00000019', (CURRENT_DATE - 16) + TIME '15:49', CURRENT_DATE + 5, 1);
SELECT pg_temp.muon('BD000007', 'GT00000022', (CURRENT_DATE - 9) + TIME '15:49', CURRENT_DATE + 5, 0);
SELECT pg_temp.muon('BD000007', 'GT00000042', (CURRENT_DATE - 4) + TIME '15:49', CURRENT_DATE + 10, 0);
SELECT pg_temp.muon('BD000007', 'ST00000007', (CURRENT_DATE - 4) + TIME '15:49', CURRENT_DATE + 10, 0);
SELECT pg_temp.muon('BD000008', 'ST00000003', (CURRENT_DATE - 17) + TIME '16:56', CURRENT_DATE + 4, 1);
SELECT pg_temp.muon('BD000008', 'GT00000025', (CURRENT_DATE - 8) + TIME '16:56', CURRENT_DATE + 6, 0);
SELECT pg_temp.muon('BD000008', 'GT00000039', (CURRENT_DATE - 8) + TIME '16:56', CURRENT_DATE + 6, 0);
SELECT pg_temp.muon('BD000008', 'ST00000012', (CURRENT_DATE - 8) + TIME '16:56', CURRENT_DATE + 6, 0);
SELECT pg_temp.muon('BD000008', 'GT00000049', (CURRENT_DATE - 8) + TIME '16:56', CURRENT_DATE + 6, 0);
SELECT pg_temp.muon('BD000009', 'GT00000014', (CURRENT_DATE - 10) + TIME '08:03', CURRENT_DATE + 20, 0);
SELECT pg_temp.muon('BD000009', 'GT00000016', (CURRENT_DATE - 28) + TIME '08:03', CURRENT_DATE + 2, 0);
SELECT pg_temp.muon('BD000009', 'ST00000004', (CURRENT_DATE - 10) + TIME '08:03', CURRENT_DATE + 20, 0);
SELECT pg_temp.muon('BD000009', 'GT00000028', (CURRENT_DATE - 10) + TIME '08:03', CURRENT_DATE + 20, 0);
SELECT pg_temp.muon('BD000009', 'GT00000032', (CURRENT_DATE - 10) + TIME '08:03', CURRENT_DATE + 20, 0);
SELECT pg_temp.muon('BD000009', 'GT00000036', (CURRENT_DATE - 10) + TIME '08:03', CURRENT_DATE + 20, 0);
SELECT pg_temp.muon('BD000009', 'ST00000010', (CURRENT_DATE - 10) + TIME '08:03', CURRENT_DATE + 20, 0);
SELECT pg_temp.muon('BD000009', 'GT00000045', (CURRENT_DATE - 10) + TIME '08:03', CURRENT_DATE + 20, 0);
SELECT pg_temp.muon('BD000009', 'GT00000052', (CURRENT_DATE - 10) + TIME '08:03', CURRENT_DATE + 20, 0);
SELECT pg_temp.muon('BD000010', 'GT00000015', (CURRENT_DATE - 15) + TIME '09:10', CURRENT_DATE + 15, 0);
SELECT pg_temp.muon('BD000010', 'GT00000013', (CURRENT_DATE - 15) + TIME '09:10', CURRENT_DATE + 15, 0);
SELECT pg_temp.muon('BD000010', 'GT00000010', (CURRENT_DATE - 15) + TIME '09:10', CURRENT_DATE + 15, 0);
SELECT pg_temp.muon('BD000010', 'GT00000017', (CURRENT_DATE - 15) + TIME '09:10', CURRENT_DATE + 15, 0);
SELECT pg_temp.muon('BD000010', 'ST00000005', (CURRENT_DATE - 15) + TIME '09:10', CURRENT_DATE + 15, 0);
SELECT pg_temp.muon('BD000010', 'GT00000029', (CURRENT_DATE - 15) + TIME '09:10', CURRENT_DATE + 15, 0);
SELECT pg_temp.muon('BD000010', 'GT00000033', (CURRENT_DATE - 15) + TIME '09:10', CURRENT_DATE + 15, 0);
SELECT pg_temp.muon('BD000010', 'GT00000037', (CURRENT_DATE - 15) + TIME '09:10', CURRENT_DATE + 15, 0);
SELECT pg_temp.muon('BD000010', 'GT00000055', (CURRENT_DATE - 15) + TIME '09:10', CURRENT_DATE + 15, 0);
SELECT pg_temp.muon('BD000010', 'GT00000046', (CURRENT_DATE - 15) + TIME '09:10', CURRENT_DATE + 15, 0);
SELECT pg_temp.muon('BD000011', 'GT00000030', (CURRENT_DATE - 8) + TIME '10:17', CURRENT_DATE + 6, 0);
SELECT pg_temp.muon('BD000012', 'GT00000002', (CURRENT_DATE - 9) + TIME '11:24', CURRENT_DATE + 5, 0);
SELECT pg_temp.muon('BD000025', 'GT00000003', (CURRENT_DATE - 9) + TIME '15:55', CURRENT_DATE + 5, 0);
SELECT pg_temp.muon('BD000025', 'ST00000008', (CURRENT_DATE - 14) + TIME '15:55', CURRENT_DATE + 0, 0);
SELECT pg_temp.muon('BD000016', 'GT00000043', (CURRENT_DATE - 15) + TIME '15:52', CURRENT_DATE - 1, 0);
SELECT pg_temp.muon('BD000016', 'GT00000040', (CURRENT_DATE - 22) + TIME '15:52', CURRENT_DATE - 8, 0);
SELECT pg_temp.muon('BD000016', 'GT00000059', (CURRENT_DATE - 17) + TIME '15:52', CURRENT_DATE - 3, 0);
SELECT pg_temp.muon('BD000017', 'GT00000053', (CURRENT_DATE - 24) + TIME '16:59', CURRENT_DATE - 10, 0);
SELECT pg_temp.muon('BD000018', 'GT00000050', (CURRENT_DATE - 25) + TIME '08:06', CURRENT_DATE - 11, 0);
SELECT pg_temp.muon('BD000019', 'GT00000005', (CURRENT_DATE - 30) + TIME '09:13', CURRENT_DATE - 16, 0);
SELECT pg_temp.muon('BD000020', 'GT00000057', (CURRENT_DATE - 31) + TIME '10:20', CURRENT_DATE - 17, 0);
SELECT pg_temp.muon('BD000021', 'GT00000023', (CURRENT_DATE - 44) + TIME '11:27', CURRENT_DATE - 30, 0);
SELECT pg_temp.muon('BD000022', 'GT00000026', (CURRENT_DATE - 61) + TIME '12:34', CURRENT_DATE - 31, 0);

UPDATE ban_sach bs SET trang_thai = CASE WHEN ct.han_tra < CURRENT_DATE THEN 'QUA_HAN' ELSE 'DANG_MUON' END
FROM chi_tiet_muon ct WHERE ct.ban_sach_id = bs.id AND ct.ngay_tra IS NULL;

-- 5. Lượt mượn đã trả và phiếu phạt
SELECT pg_temp.muon('BD000001', 'GT00000012', (CURRENT_DATE - 40) + TIME '09:30', CURRENT_DATE - 26, 0, (CURRENT_DATE - 24) + TIME '15:20', 'BINH_THUONG');
INSERT INTO phieu_phat (chi_tiet_muon_id, loai_vi_pham, so_tien, trang_thai, ngay_lap, nguoi_lap_id, ngay_thu, nguoi_thu_id, ly_do_huy)
SELECT ct.id, 'TRE_HAN', 10000, 'DA_THANH_TOAN', (CURRENT_DATE - 24) + TIME '15:20', nd.id, (CURRENT_DATE - 24) + TIME '15:25', nd.id, NULL
FROM chi_tiet_muon ct JOIN phieu_muon pm ON pm.id = ct.phieu_muon_id JOIN ban_doc bd ON bd.id = pm.ban_doc_id
JOIN ban_sach bs ON bs.id = ct.ban_sach_id, nguoi_dung nd
WHERE bd.ma_the = 'BD000001' AND bs.ma_vach = 'GT00000012' AND ct.ngay_tra IS NOT NULL AND nd.ten_dang_nhap = 'thuthu01';
SELECT pg_temp.muon('BD000002', 'GT00000013', (CURRENT_DATE - 60) + TIME '09:30', CURRENT_DATE - 46, 0, (CURRENT_DATE - 50) + TIME '15:20', 'BINH_THUONG');
SELECT pg_temp.muon('BD000014', 'ST00000009', (CURRENT_DATE - 30) + TIME '09:30', CURRENT_DATE - 16, 0, (CURRENT_DATE - 14) + TIME '15:20', 'BINH_THUONG');
INSERT INTO phieu_phat (chi_tiet_muon_id, loai_vi_pham, so_tien, trang_thai, ngay_lap, nguoi_lap_id, ngay_thu, nguoi_thu_id, ly_do_huy)
SELECT ct.id, 'TRE_HAN', 10000, 'CHUA_THANH_TOAN', (CURRENT_DATE - 14) + TIME '15:20', nd.id, NULL, NULL, NULL
FROM chi_tiet_muon ct JOIN phieu_muon pm ON pm.id = ct.phieu_muon_id JOIN ban_doc bd ON bd.id = pm.ban_doc_id
JOIN ban_sach bs ON bs.id = ct.ban_sach_id, nguoi_dung nd
WHERE bd.ma_the = 'BD000014' AND bs.ma_vach = 'ST00000009' AND ct.ngay_tra IS NOT NULL AND nd.ten_dang_nhap = 'thuthu01';
SELECT pg_temp.muon('BD000023', 'GT00000018', (CURRENT_DATE - 40) + TIME '09:30', CURRENT_DATE - 26, 0, (CURRENT_DATE - 16) + TIME '15:20', 'BINH_THUONG');
INSERT INTO phieu_phat (chi_tiet_muon_id, loai_vi_pham, so_tien, trang_thai, ngay_lap, nguoi_lap_id, ngay_thu, nguoi_thu_id, ly_do_huy)
SELECT ct.id, 'TRE_HAN', 50000, 'CHUA_THANH_TOAN', (CURRENT_DATE - 16) + TIME '15:20', nd.id, NULL, NULL, NULL
FROM chi_tiet_muon ct JOIN phieu_muon pm ON pm.id = ct.phieu_muon_id JOIN ban_doc bd ON bd.id = pm.ban_doc_id
JOIN ban_sach bs ON bs.id = ct.ban_sach_id, nguoi_dung nd
WHERE bd.ma_the = 'BD000023' AND bs.ma_vach = 'GT00000018' AND ct.ngay_tra IS NOT NULL AND nd.ten_dang_nhap = 'thuthu01';
SELECT pg_temp.muon('BD000024', 'GT00000038', (CURRENT_DATE - 45) + TIME '09:30', CURRENT_DATE - 30, 0, (CURRENT_DATE - 19) + TIME '15:20', 'BINH_THUONG');
INSERT INTO phieu_phat (chi_tiet_muon_id, loai_vi_pham, so_tien, trang_thai, ngay_lap, nguoi_lap_id, ngay_thu, nguoi_thu_id, ly_do_huy)
SELECT ct.id, 'TRE_HAN', 55000, 'CHUA_THANH_TOAN', (CURRENT_DATE - 19) + TIME '15:20', nd.id, NULL, NULL, NULL
FROM chi_tiet_muon ct JOIN phieu_muon pm ON pm.id = ct.phieu_muon_id JOIN ban_doc bd ON bd.id = pm.ban_doc_id
JOIN ban_sach bs ON bs.id = ct.ban_sach_id, nguoi_dung nd
WHERE bd.ma_the = 'BD000024' AND bs.ma_vach = 'GT00000038' AND ct.ngay_tra IS NOT NULL AND nd.ten_dang_nhap = 'thuthu01';
SELECT pg_temp.muon('BD000007', 'GT00000031', (CURRENT_DATE - 50) + TIME '09:30', CURRENT_DATE - 36, 0, (CURRENT_DATE - 33) + TIME '15:20', 'BINH_THUONG');
INSERT INTO phieu_phat (chi_tiet_muon_id, loai_vi_pham, so_tien, trang_thai, ngay_lap, nguoi_lap_id, ngay_thu, nguoi_thu_id, ly_do_huy)
SELECT ct.id, 'TRE_HAN', 15000, 'DA_THANH_TOAN', (CURRENT_DATE - 33) + TIME '15:20', nd.id, (CURRENT_DATE - 33) + TIME '15:25', nd.id, NULL
FROM chi_tiet_muon ct JOIN phieu_muon pm ON pm.id = ct.phieu_muon_id JOIN ban_doc bd ON bd.id = pm.ban_doc_id
JOIN ban_sach bs ON bs.id = ct.ban_sach_id, nguoi_dung nd
WHERE bd.ma_the = 'BD000007' AND bs.ma_vach = 'GT00000031' AND ct.ngay_tra IS NOT NULL AND nd.ten_dang_nhap = 'thuthu01';
SELECT pg_temp.muon('BD000009', 'GT00000024', (CURRENT_DATE - 70) + TIME '09:30', CURRENT_DATE - 40, 0, (CURRENT_DATE - 42) + TIME '15:20', 'HONG_NHE');
INSERT INTO phieu_phat (chi_tiet_muon_id, loai_vi_pham, so_tien, trang_thai, ngay_lap, nguoi_lap_id, ngay_thu, nguoi_thu_id, ly_do_huy)
SELECT ct.id, 'HONG_NHE', 16500, 'DA_THANH_TOAN', (CURRENT_DATE - 42) + TIME '15:20', nd.id, (CURRENT_DATE - 42) + TIME '15:25', nd.id, NULL
FROM chi_tiet_muon ct JOIN phieu_muon pm ON pm.id = ct.phieu_muon_id JOIN ban_doc bd ON bd.id = pm.ban_doc_id
JOIN ban_sach bs ON bs.id = ct.ban_sach_id, nguoi_dung nd
WHERE bd.ma_the = 'BD000009' AND bs.ma_vach = 'GT00000024' AND ct.ngay_tra IS NOT NULL AND nd.ten_dang_nhap = 'thuthu01';
SELECT pg_temp.muon('BD000008', 'GT00000044', (CURRENT_DATE - 35) + TIME '09:30', CURRENT_DATE - 21, 0, (CURRENT_DATE - 21) + TIME '15:20', 'BINH_THUONG');
INSERT INTO phieu_phat (chi_tiet_muon_id, loai_vi_pham, so_tien, trang_thai, ngay_lap, nguoi_lap_id, ngay_thu, nguoi_thu_id, ly_do_huy)
SELECT ct.id, 'TRE_HAN', 5000, 'DA_HUY', (CURRENT_DATE - 21) + TIME '15:20', nd.id, NULL, NULL, 'Lập nhầm, bạn đọc trả đúng hạn'
FROM chi_tiet_muon ct JOIN phieu_muon pm ON pm.id = ct.phieu_muon_id JOIN ban_doc bd ON bd.id = pm.ban_doc_id
JOIN ban_sach bs ON bs.id = ct.ban_sach_id, nguoi_dung nd
WHERE bd.ma_the = 'BD000008' AND bs.ma_vach = 'GT00000044' AND ct.ngay_tra IS NOT NULL AND nd.ten_dang_nhap = 'thuthu01';
SELECT pg_temp.muon('BD000006', 'GT00000035', (CURRENT_DATE - 90) + TIME '09:30', CURRENT_DATE - 76, 0, (CURRENT_DATE - 78) + TIME '15:20', 'BINH_THUONG');

-- 6. Đặt trước
INSERT INTO dat_truoc (ban_doc_id, dau_sach_id, ban_sach_id, thoi_diem_dat, han_giu, trang_thai)
SELECT bd.id, ds.id, NULL, LOCALTIMESTAMP(0) - INTERVAL '30 hours', NULL, 'DANG_CHO'
FROM ban_doc bd, dau_sach ds WHERE bd.ma_the = 'BD000004' AND ds.isbn = '9786040000255';
INSERT INTO dat_truoc (ban_doc_id, dau_sach_id, ban_sach_id, thoi_diem_dat, han_giu, trang_thai)
SELECT bd.id, ds.id, NULL, LOCALTIMESTAMP(0) - INTERVAL '20 hours', NULL, 'DANG_CHO'
FROM ban_doc bd, dau_sach ds WHERE bd.ma_the = 'BD000005' AND ds.isbn = '9786040000255';
INSERT INTO dat_truoc (ban_doc_id, dau_sach_id, ban_sach_id, thoi_diem_dat, han_giu, trang_thai)
SELECT bd.id, ds.id, NULL, LOCALTIMESTAMP(0) - INTERVAL '10 hours', NULL, 'DANG_CHO'
FROM ban_doc bd, dau_sach ds WHERE bd.ma_the = 'BD000012' AND ds.isbn = '9786040000255';
INSERT INTO dat_truoc (ban_doc_id, dau_sach_id, ban_sach_id, thoi_diem_dat, han_giu, trang_thai)
SELECT bd.id, ds.id, NULL, LOCALTIMESTAMP(0) - INTERVAL '40 hours', NULL, 'DANG_CHO'
FROM ban_doc bd, dau_sach ds WHERE bd.ma_the = 'BD000013' AND ds.isbn = '9786040000293';
INSERT INTO dat_truoc (ban_doc_id, dau_sach_id, ban_sach_id, thoi_diem_dat, han_giu, trang_thai)
SELECT bd.id, ds.id, NULL, LOCALTIMESTAMP(0) - INTERVAL '38 hours', NULL, 'DANG_CHO'
FROM ban_doc bd, dau_sach ds WHERE bd.ma_the = 'BD000013' AND ds.isbn = '9786040000309';
INSERT INTO dat_truoc (ban_doc_id, dau_sach_id, ban_sach_id, thoi_diem_dat, han_giu, trang_thai)
SELECT bd.id, ds.id, NULL, LOCALTIMESTAMP(0) - INTERVAL '12 hours', NULL, 'DANG_CHO'
FROM ban_doc bd, dau_sach ds WHERE bd.ma_the = 'BD000012' AND ds.isbn = '9786040000309';
INSERT INTO dat_truoc (ban_doc_id, dau_sach_id, ban_sach_id, thoi_diem_dat, han_giu, trang_thai)
SELECT bd.id, ds.id, NULL, LOCALTIMESTAMP(0) - INTERVAL '36 hours', NULL, 'DANG_CHO'
FROM ban_doc bd, dau_sach ds WHERE bd.ma_the = 'BD000013' AND ds.isbn = '9786040000316';
INSERT INTO dat_truoc (ban_doc_id, dau_sach_id, ban_sach_id, thoi_diem_dat, han_giu, trang_thai)
SELECT bd.id, ds.id, (SELECT id FROM ban_sach WHERE ma_vach = 'GT00000012'), LOCALTIMESTAMP(0) - INTERVAL '60 hours', LOCALTIMESTAMP(0) + INTERVAL '2 hours', 'DANG_GIU'
FROM ban_doc bd, dau_sach ds WHERE bd.ma_the = 'BD000005' AND ds.isbn = '9786040000286';
INSERT INTO dat_truoc (ban_doc_id, dau_sach_id, ban_sach_id, thoi_diem_dat, han_giu, trang_thai)
SELECT bd.id, ds.id, (SELECT id FROM ban_sach WHERE ma_vach = 'GT00000008'), LOCALTIMESTAMP(0) - INTERVAL '80 hours', LOCALTIMESTAMP(0) - INTERVAL '1 hours', 'DANG_GIU'
FROM ban_doc bd, dau_sach ds WHERE bd.ma_the = 'BD000023' AND ds.isbn = '9786040000262';
INSERT INTO dat_truoc (ban_doc_id, dau_sach_id, ban_sach_id, thoi_diem_dat, han_giu, trang_thai)
SELECT bd.id, ds.id, NULL, LOCALTIMESTAMP(0) - INTERVAL '50 hours', NULL, 'DANG_CHO'
FROM ban_doc bd, dau_sach ds WHERE bd.ma_the = 'BD000017' AND ds.isbn = '9786040000262';
INSERT INTO dat_truoc (ban_doc_id, dau_sach_id, ban_sach_id, thoi_diem_dat, han_giu, trang_thai)
SELECT bd.id, ds.id, (SELECT id FROM ban_sach WHERE ma_vach = 'GT00000056'), LOCALTIMESTAMP(0) - INTERVAL '70 hours', LOCALTIMESTAMP(0) - INTERVAL '2 hours', 'DANG_GIU'
FROM ban_doc bd, dau_sach ds WHERE bd.ma_the = 'BD000025' AND ds.isbn = '9786040000477';
INSERT INTO dat_truoc (ban_doc_id, dau_sach_id, ban_sach_id, thoi_diem_dat, han_giu, trang_thai)
SELECT bd.id, ds.id, NULL, LOCALTIMESTAMP(0) - INTERVAL '300 hours', NULL, 'DA_NHAN'
FROM ban_doc bd, dau_sach ds WHERE bd.ma_the = 'BD000001' AND ds.isbn = '9786040000293';
UPDATE ban_sach SET trang_thai = 'DANG_GIU' WHERE ma_vach IN ('GT00000012', 'GT00000008', 'GT00000056');

-- 7. Bản sách đang sửa, đã thanh lý
UPDATE ban_sach SET trang_thai = 'DANG_SUA', ghi_chu = 'Bong gáy, đang dán lại' WHERE ma_vach IN ('GT00000034', 'GT00000047');
UPDATE ban_sach SET trang_thai = 'THANH_LY', ghi_chu = 'Rách nhiều trang' WHERE ma_vach IN ('GT00000020');
