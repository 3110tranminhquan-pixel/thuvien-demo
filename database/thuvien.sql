CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE vai_tro (
    id   SMALLINT PRIMARY KEY,
    ten  VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE nguoi_dung (
    id             BIGSERIAL PRIMARY KEY,
    ten_dang_nhap  VARCHAR(50)  NOT NULL UNIQUE,
    mat_khau_hash  VARCHAR(100) NOT NULL,
    vai_tro_id     SMALLINT     NOT NULL REFERENCES vai_tro(id) ON DELETE RESTRICT,
    trang_thai     VARCHAR(10)  NOT NULL DEFAULT 'HOAT_DONG'
                   CHECK (trang_thai IN ('HOAT_DONG', 'BI_KHOA')),
    so_lan_sai     SMALLINT     NOT NULL DEFAULT 0 CHECK (so_lan_sai >= 0),
    khoa_den       TIMESTAMP
);

CREATE TABLE loai_ban_doc (
    id               SMALLSERIAL PRIMARY KEY,
    ten_loai         VARCHAR(50) NOT NULL UNIQUE,
    so_sach_toi_da   SMALLINT NOT NULL CHECK (so_sach_toi_da > 0),
    so_ngay_muon     SMALLINT NOT NULL CHECK (so_ngay_muon > 0),
    so_lan_gia_han   SMALLINT NOT NULL CHECK (so_lan_gia_han >= 0),
    so_ngay_gia_han  SMALLINT NOT NULL CHECK (so_ngay_gia_han > 0)
);

CREATE TABLE ban_doc (
    id             BIGSERIAL PRIMARY KEY,
    ma_the         CHAR(8)      NOT NULL UNIQUE CHECK (ma_the ~ '^BD[0-9]{6}$'),
    ho_ten         VARCHAR(100) NOT NULL,
    ngay_sinh      DATE,
    email          VARCHAR(100) NOT NULL UNIQUE,
    so_dien_thoai  VARCHAR(15),
    don_vi         VARCHAR(100),
    ngay_cap       DATE NOT NULL DEFAULT CURRENT_DATE,
    ngay_het_han   DATE NOT NULL,
    trang_thai     VARCHAR(10) NOT NULL DEFAULT 'HOAT_DONG'
                   CHECK (trang_thai IN ('HOAT_DONG', 'BI_KHOA', 'HET_HAN', 'NGUNG')),
    ly_do_khoa     VARCHAR(255),
    khoa_thu_cong  BOOLEAN NOT NULL DEFAULT FALSE,
    loai_id        SMALLINT NOT NULL REFERENCES loai_ban_doc(id) ON DELETE RESTRICT,
    nguoi_dung_id  BIGINT UNIQUE REFERENCES nguoi_dung(id) ON DELETE RESTRICT,
    CHECK (ngay_het_han > ngay_cap)
);

CREATE TABLE the_loai (
    id      SERIAL PRIMARY KEY,
    ten     VARCHAR(100) NOT NULL UNIQUE,
    ma_ddc  VARCHAR(10)
);

CREATE TABLE nha_xuat_ban (
    id       SERIAL PRIMARY KEY,
    ten      VARCHAR(150) NOT NULL UNIQUE,
    dia_chi  VARCHAR(255)
);

CREATE TABLE tac_gia (
    id      SERIAL PRIMARY KEY,
    ho_ten  VARCHAR(100) NOT NULL
);

CREATE TABLE dau_sach (
    id             BIGSERIAL PRIMARY KEY,
    isbn           CHAR(13)     NOT NULL UNIQUE,
    nhan_de        VARCHAR(255) NOT NULL,
    nam_xb         SMALLINT CHECK (nam_xb BETWEEN 1900 AND 2100),
    lan_xb         SMALLINT DEFAULT 1,
    so_trang       SMALLINT,
    gia_bia        NUMERIC(12, 0) NOT NULL CHECK (gia_bia > 0),
    ma_ddc         VARCHAR(20),
    loai_tai_lieu  VARCHAR(10) NOT NULL
                   CHECK (loai_tai_lieu IN ('GIAO_TRINH', 'SACH_TK', 'THAM_KHAO')),
    trang_thai     VARCHAR(10) NOT NULL DEFAULT 'DANG_DUNG'
                   CHECK (trang_thai IN ('DANG_DUNG', 'NGUNG')),
    the_loai_id    INT NOT NULL REFERENCES the_loai(id) ON DELETE RESTRICT,
    nxb_id         INT NOT NULL REFERENCES nha_xuat_ban(id) ON DELETE RESTRICT
);

CREATE TABLE dau_sach_tac_gia (
    dau_sach_id      BIGINT NOT NULL REFERENCES dau_sach(id) ON DELETE RESTRICT,
    tac_gia_id       INT    NOT NULL REFERENCES tac_gia(id) ON DELETE RESTRICT,
    vai_tro_tac_gia  VARCHAR(20) DEFAULT 'TAC_GIA',
    PRIMARY KEY (dau_sach_id, tac_gia_id)
);

CREATE TABLE ban_sach (
    id           BIGSERIAL PRIMARY KEY,
    ma_vach      CHAR(10) NOT NULL UNIQUE CHECK (ma_vach ~ '^[A-Z]{2}[0-9]{8}$'),
    dau_sach_id  BIGINT NOT NULL REFERENCES dau_sach(id) ON DELETE RESTRICT,
    vi_tri_ke    VARCHAR(20),
    trang_thai   VARCHAR(10) NOT NULL DEFAULT 'SAN_SANG'
                 CHECK (trang_thai IN ('SAN_SANG', 'DANG_MUON', 'QUA_HAN', 'DANG_GIU',
                                       'DANG_SUA', 'MAT', 'THANH_LY')),
    ngay_nhap    DATE NOT NULL DEFAULT CURRENT_DATE,
    ghi_chu      VARCHAR(255)
);

CREATE TABLE phieu_muon (
    id           BIGSERIAL PRIMARY KEY,
    ban_doc_id   BIGINT NOT NULL REFERENCES ban_doc(id) ON DELETE RESTRICT,
    thu_thu_id   BIGINT NOT NULL REFERENCES nguoi_dung(id) ON DELETE RESTRICT,
    ngay_muon    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE chi_tiet_muon (
    id                  BIGSERIAL PRIMARY KEY,
    phieu_muon_id       BIGINT NOT NULL REFERENCES phieu_muon(id) ON DELETE RESTRICT,
    ban_sach_id         BIGINT NOT NULL REFERENCES ban_sach(id) ON DELETE RESTRICT,
    han_tra             DATE NOT NULL,
    ngay_tra            TIMESTAMP,
    so_lan_gia_han      SMALLINT NOT NULL DEFAULT 0 CHECK (so_lan_gia_han >= 0),
    tinh_trang_khi_tra  VARCHAR(12)
                        CHECK (tinh_trang_khi_tra IN ('BINH_THUONG', 'HONG_NHE', 'HONG_NANG', 'MAT')),
    UNIQUE (phieu_muon_id, ban_sach_id)
);

-- Một bản sách chỉ có tối đa một lượt mượn chưa trả
CREATE UNIQUE INDEX uq_ctm_dang_muon ON chi_tiet_muon (ban_sach_id) WHERE ngay_tra IS NULL;

CREATE TABLE dat_truoc (
    id             BIGSERIAL PRIMARY KEY,
    ban_doc_id     BIGINT NOT NULL REFERENCES ban_doc(id) ON DELETE RESTRICT,
    dau_sach_id    BIGINT NOT NULL REFERENCES dau_sach(id) ON DELETE RESTRICT,
    ban_sach_id    BIGINT REFERENCES ban_sach(id) ON DELETE RESTRICT,
    thoi_diem_dat  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    han_giu        TIMESTAMP,
    trang_thai     VARCHAR(10) NOT NULL DEFAULT 'DANG_CHO'
                   CHECK (trang_thai IN ('DANG_CHO', 'DANG_GIU', 'DA_NHAN', 'DA_HUY')),
    CHECK (trang_thai <> 'DANG_GIU' OR (ban_sach_id IS NOT NULL AND han_giu IS NOT NULL))
);

-- Một bạn đọc không đặt trùng một đầu sách khi lượt cũ còn hiệu lực
CREATE UNIQUE INDEX uq_dt_con_hieu_luc ON dat_truoc (ban_doc_id, dau_sach_id)
    WHERE trang_thai IN ('DANG_CHO', 'DANG_GIU');

CREATE TABLE phieu_phat (
    id                BIGSERIAL PRIMARY KEY,
    chi_tiet_muon_id  BIGINT NOT NULL REFERENCES chi_tiet_muon(id) ON DELETE RESTRICT,
    loai_vi_pham      VARCHAR(10) NOT NULL
                      CHECK (loai_vi_pham IN ('TRE_HAN', 'HONG_NHE', 'HONG_NANG', 'MAT_SACH')),
    so_tien           NUMERIC(12, 0) NOT NULL CHECK (so_tien >= 0),
    trang_thai        VARCHAR(15) NOT NULL DEFAULT 'CHUA_THANH_TOAN'
                      CHECK (trang_thai IN ('CHUA_THANH_TOAN', 'DA_THANH_TOAN', 'DA_HUY')),
    ngay_lap          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    nguoi_lap_id      BIGINT NOT NULL REFERENCES nguoi_dung(id) ON DELETE RESTRICT,
    ngay_thu          TIMESTAMP,
    nguoi_thu_id      BIGINT REFERENCES nguoi_dung(id) ON DELETE RESTRICT,
    ly_do_huy         VARCHAR(255),
    CHECK (trang_thai <> 'DA_HUY' OR ly_do_huy IS NOT NULL)
);

CREATE TABLE tham_so_he_thong (
    ma       VARCHAR(30) PRIMARY KEY,
    gia_tri  VARCHAR(50) NOT NULL,
    mo_ta    VARCHAR(255)
);

CREATE TABLE nhat_ky (
    id             BIGSERIAL PRIMARY KEY,
    nguoi_dung_id  BIGINT REFERENCES nguoi_dung(id) ON DELETE RESTRICT,
    hanh_dong      VARCHAR(50) NOT NULL,
    doi_tuong      VARCHAR(50),
    ma_doi_tuong   VARCHAR(50),
    thoi_gian      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    dia_chi_ip     VARCHAR(45)
);

-- Chỉ mục phục vụ tra cứu và tác vụ định kỳ
CREATE INDEX idx_dau_sach_nhan_de_trgm ON dau_sach USING GIN (nhan_de gin_trgm_ops);
CREATE INDEX idx_ban_sach_ds_tt ON ban_sach (dau_sach_id, trang_thai);
CREATE INDEX idx_ctm_chua_tra ON chi_tiet_muon (han_tra) WHERE ngay_tra IS NULL;

-- Dữ liệu cấu hình ban đầu
INSERT INTO vai_tro (id, ten) VALUES (1, 'BAN_DOC'), (2, 'THU_THU'), (3, 'QUAN_TRI');

INSERT INTO loai_ban_doc (ten_loai, so_sach_toi_da, so_ngay_muon, so_lan_gia_han, so_ngay_gia_han)
VALUES ('Sinh viên', 5, 14, 1, 7),
       ('Giảng viên - Cán bộ', 10, 30, 1, 14);

INSERT INTO tham_so_he_thong (ma, gia_tri, mo_ta) VALUES
('PHAT_TRE_MOI_NGAY',   '5000',  'Tiền phạt trễ hạn mỗi ngày mỗi cuốn (đồng) - BR-07'),
('TY_LE_DEN_MAT',       '2.0',   'Tỷ lệ đền khi mất sách so với giá bìa - BR-13, trần BR-07'),
('TY_LE_HONG_NHE',      '0.3',   'Tỷ lệ phạt hỏng nhẹ so với giá bìa - BR-08'),
('TY_LE_HONG_NANG',     '1.0',   'Tỷ lệ phạt hỏng nặng so với giá bìa - BR-08'),
('SO_DAT_TRUOC_TOI_DA', '3',     'Số đầu sách đặt trước tối đa - BR-11'),
('SO_GIO_GIU',          '48',    'Số giờ giữ sách đặt trước - BR-12'),
('NGUONG_NO_KHOA',      '50000', 'Tổng nợ chưa thanh toán vượt mức này thì khóa thẻ (đồng) - BR-14'),
('NGUONG_NGAY_TRE_KHOA','30',    'Số ngày quá hạn vượt mức này thì khóa thẻ - BR-14'),
('SO_NGAY_NHAC_TRUOC',  '2',     'Gửi email nhắc trước hạn trả bao nhiêu ngày - FR-28'),
('CHU_KY_NHAC_QUA_HAN', '7',     'Nhắc lại email quá hạn sau mỗi bao nhiêu ngày - FR-28');

-- Dữ liệu mẫu nhỏ để demo và chạy ca kiểm thử
-- Mật khẩu của mọi tài khoản mẫu là: Thuvien@123 (chuỗi bên dưới là BCrypt, hệ số 10)
INSERT INTO nguoi_dung (ten_dang_nhap, mat_khau_hash, vai_tro_id) VALUES
('admin',    '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 3),
('thuthu01', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 2),
('thuthu02', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 2),
('BD000001', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 1),
('BD000002', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 1),
('BD000003', '$2a$10$oIQHwqTiRjSjh8gnIU4WzOmwBZgLBgJrjU0WrKtSthg2aHsxxPBPe', 1);

INSERT INTO ban_doc (ma_the, ho_ten, email, don_vi, ngay_cap, ngay_het_han, loai_id, nguoi_dung_id) VALUES
('BD000001', 'Nguyễn Văn An',  'an.nv@truong.edu.vn',   'Khoa CNTT',  '2025-09-01', '2029-09-01', 1, 4),
('BD000002', 'Trần Thị Bình',  'binh.tt@truong.edu.vn', 'Khoa Kinh tế', '2025-09-01', '2029-09-01', 1, 5),
('BD000003', 'Lê Minh Châu',   'chau.lm@truong.edu.vn', 'Khoa CNTT',  '2024-09-01', '2030-09-01', 2, 6);

INSERT INTO the_loai (ten, ma_ddc) VALUES ('Tin học', '005'), ('Kỹ thuật giao thông', '625'), ('Kinh tế', '330');
INSERT INTO nha_xuat_ban (ten) VALUES ('NXB Giáo dục Việt Nam'), ('NXB Giao thông vận tải'), ('NXB Đại học Quốc gia TP.HCM');
INSERT INTO tac_gia (ho_ten) VALUES ('Nguyễn Văn A'), ('Trần Văn B'), ('Phạm Thị C');

INSERT INTO dau_sach (isbn, nhan_de, nam_xb, gia_bia, loai_tai_lieu, the_loai_id, nxb_id) VALUES
('9786040000217', 'Cơ sở dữ liệu',                      2022, 80000, 'GIAO_TRINH', 1, 1),
('9786040000224', 'Phân tích thiết kế hệ thống',        2023, 40000, 'GIAO_TRINH', 1, 3),
('9786040000231', 'Cầu đường bộ',                       2021, 120000, 'GIAO_TRINH', 2, 2),
('9786040000248', 'Từ điển Anh - Việt kỹ thuật',         2020, 250000, 'THAM_KHAO', 2, 2),
('9786040000255', 'Kinh tế vận tải',                    2022, 60000, 'SACH_TK', 3, 3);

INSERT INTO dau_sach_tac_gia (dau_sach_id, tac_gia_id) VALUES (1, 1), (2, 1), (2, 2), (3, 3), (5, 2);

INSERT INTO ban_sach (ma_vach, dau_sach_id, vi_tri_ke) VALUES
('GT00000001', 1, 'K01-T2'), ('GT00000002', 1, 'K01-T2'), ('GT00000003', 1, 'K01-T2'),
('GT00000004', 2, 'K01-T3'), ('GT00000005', 2, 'K01-T3'),
('GT00000006', 3, 'K05-T1'),
('TK00000001', 4, 'K10-T1'),
('ST00000001', 5, 'K07-T4'), ('ST00000002', 5, 'K07-T4');
