-- ============================================================
--  QUẢN LÝ CỬA HÀNG TIỆN LỢI (SQL SERVER VERSION)
--  Tương thích: SSMS | JDBC (Eclipse)
--  Nhóm 6 – DHKTPM20B – IUH
-- ============================================================

USE master;
GO


/*
IF EXISTS (SELECT * FROM sys.databases WHERE name = 'quanlycuahangtienloi')
BEGIN
    DROP DATABASE quanlycuahangtienloi;
END
GO
*/

IF EXISTS (SELECT * FROM sys.databases WHERE name = 'quanlycuahangtienloi')
BEGIN
    ALTER DATABASE quanlycuahangtienloi SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE quanlycuahangtienloi;
END
GO






CREATE DATABASE quanlycuahangtienloi;
GO

USE quanlycuahangtienloi;
GO

-- ============================================================
-- 1. LOAI_KHACH_HANG
-- ============================================================
CREATE TABLE LoaiKhachHang (
    maLKH       VARCHAR(10)     NOT NULL,
    tenLKH      NVARCHAR(50)    NOT NULL,
    giamGia     DECIMAL(5,2)    NOT NULL DEFAULT 0, -- Phần trăm giảm giá (0-100)
    mucDiem     INT             NOT NULL DEFAULT 0, -- Điểm tích lũy tối thiểu

    CONSTRAINT PK_LoaiKhachHang  PRIMARY KEY (maLKH),
    CONSTRAINT CHK_LKH_GiamGia   CHECK (giamGia BETWEEN 0 AND 100),
    CONSTRAINT CHK_LKH_MucDiem   CHECK (mucDiem >= 0)
);

-- ============================================================
-- 2. KHACH_HANG
-- ============================================================
CREATE TABLE KhachHang (
    maKH            VARCHAR(10)     NOT NULL,
    tenKH           NVARCHAR(100)   NOT NULL,
    soDienThoai     VARCHAR(15)     NOT NULL,
    diemTL          INT             NOT NULL DEFAULT 0,
    maLKH           VARCHAR(10)     NOT NULL DEFAULT 'DONG',

    CONSTRAINT PK_KhachHang          PRIMARY KEY (maKH),
    CONSTRAINT UQ_KH_SoDienThoai     UNIQUE (soDienThoai),
    CONSTRAINT FK_KH_LoaiKH          FOREIGN KEY (maLKH) 
                                     REFERENCES LoaiKhachHang(maLKH)
                                     ON UPDATE CASCADE,
    CONSTRAINT CHK_KH_DiemTL         CHECK (diemTL >= 0),
    CONSTRAINT CHK_KH_SDT            CHECK (soDienThoai NOT LIKE '%[^0-9]%') -- Thay REGEXP bằng LIKE đơn giản
);

-- ============================================================
-- 3. NHAN_VIEN
-- ============================================================
CREATE TABLE NhanVien (
    maNV            VARCHAR(10)     NOT NULL,
    tenNV           NVARCHAR(100)   NOT NULL,
    diaChi          NVARCHAR(255)   NULL,
    ngayVaoLam      DATE            NOT NULL,
    gioiTinh        BIT             NOT NULL DEFAULT 1, -- 1 = Nam, 0 = Nữ
    sdt             VARCHAR(15)     NOT NULL,
    matKhau         VARCHAR(255)    NOT NULL,
    quanLy          BIT             NOT NULL DEFAULT 0, -- 1 = Quản lý, 0 = Thu ngân

    CONSTRAINT PK_NhanVien      PRIMARY KEY (maNV),
    CONSTRAINT UQ_NV_SDT        UNIQUE (sdt),
    CONSTRAINT CHK_NV_SDT       CHECK (sdt NOT LIKE '%[^0-9]%'),
    CONSTRAINT CHK_NV_NgayVaoLam CHECK (ngayVaoLam <= GETDATE())
);

-- ============================================================
-- 4. LOAI_HANG_HOA
-- ============================================================
CREATE TABLE LoaiHangHoa (
    maLoaiHang      VARCHAR(10)     NOT NULL,
    tenLoaiHang     NVARCHAR(100)   NOT NULL,
    moTa            NVARCHAR(255)   NULL,

    CONSTRAINT PK_LoaiHangHoa PRIMARY KEY (maLoaiHang)
);

-- ============================================================
-- 5. HANG_HOA
-- ============================================================
CREATE TABLE HangHoa (
    maHH            VARCHAR(15)     NOT NULL,
    maVach          VARCHAR(30)     NOT NULL,
    tenHH           NVARCHAR(150)   NOT NULL,
    hinhAnh         VARCHAR(500)    NULL,
    giaSP           DECIMAL(15,2)   NOT NULL,
    maLoaiHang      VARCHAR(10)     NOT NULL,
    conKinhDoanh    BIT             NOT NULL DEFAULT 1,

    CONSTRAINT PK_HangHoa            PRIMARY KEY (maHH),
    CONSTRAINT UQ_HH_MaVach          UNIQUE (maVach),
    CONSTRAINT FK_HH_LoaiHangHoa     FOREIGN KEY (maLoaiHang) 
                                     REFERENCES LoaiHangHoa(maLoaiHang)
                                     ON UPDATE CASCADE,
    CONSTRAINT CHK_HH_GiaSP          CHECK (giaSP > 0)
);

-- ============================================================
-- 6. LO_HANG
-- ============================================================
CREATE TABLE LoHang (
    maLo            VARCHAR(15)     NOT NULL,
    maHH            VARCHAR(15)     NOT NULL,
    ngayNhap        DATE            NOT NULL,
    hanSuDung       DATE            NOT NULL,
    soLuongNhap     INT             NOT NULL,
    soLuongTon      INT             NOT NULL,

    CONSTRAINT PK_LoHang            PRIMARY KEY (maLo),
    CONSTRAINT FK_LH_HangHoa        FOREIGN KEY (maHH) 
                                     REFERENCES HangHoa(maHH)
                                     ON UPDATE CASCADE,
    CONSTRAINT CHK_LH_SoLuongNhap  CHECK (soLuongNhap > 0),
    CONSTRAINT CHK_LH_SoLuongTon   CHECK (soLuongTon >= 0),
    CONSTRAINT CHK_LH_TonLeNhap    CHECK (soLuongTon <= soLuongNhap),
    CONSTRAINT CHK_LH_HanSuDung    CHECK (hanSuDung > ngayNhap)
);

-- ============================================================
-- 7. MA_GIAM_GIA (Dùng CHECK thay cho ENUM)
-- ============================================================
CREATE TABLE MaGiamGia (
    maGiam          VARCHAR(20)     NOT NULL,
    loaiGiam        VARCHAR(20)     NOT NULL DEFAULT 'PHAN_TRAM',
    giaTriGiam      DECIMAL(15,2)   NOT NULL,
    ngayBatDau      DATE            NOT NULL,
    ngayKetThuc     DATE            NOT NULL,
    soLanDungToiDa  INT             NOT NULL DEFAULT 1,
    soLanDaDung      INT             NOT NULL DEFAULT 0,
    conHieuLuc      BIT             NOT NULL DEFAULT 1,

    CONSTRAINT PK_MaGiamGia         PRIMARY KEY (maGiam),
    CONSTRAINT CHK_MGG_LoaiGiam     CHECK (loaiGiam IN ('PHAN_TRAM', 'SO_TIEN')),
    CONSTRAINT CHK_MGG_GiaTri       CHECK (giaTriGiam > 0),
    CONSTRAINT CHK_MGG_NgayHL       CHECK (ngayKetThuc >= ngayBatDau),
    CONSTRAINT CHK_MGG_PhanTram     CHECK (loaiGiam = 'SO_TIEN' OR (loaiGiam = 'PHAN_TRAM' AND giaTriGiam <= 100))
);

-- ============================================================
-- 8. HOA_DON_BAN_HANG
-- ============================================================
CREATE TABLE HoaDonBanHang (
    maHDBH              VARCHAR(15)     NOT NULL,
    ngayLapHDBH         DATETIME        NOT NULL DEFAULT GETDATE(),
    maNV                VARCHAR(10)     NOT NULL,
    maKH                VARCHAR(10)     NULL,
    maGiam              VARCHAR(20)     NULL,
    hinhThucThanhToan   VARCHAR(20)     NOT NULL DEFAULT 'TIEN_MAT',
    tongTienGoc         DECIMAL(15,2)   NOT NULL DEFAULT 0,
    tienGiamThanhVien   DECIMAL(15,2)   NOT NULL DEFAULT 0,
    tienGiamVoucher     DECIMAL(15,2)   NOT NULL DEFAULT 0,
    tongTienThanhToan   DECIMAL(15,2)   NOT NULL DEFAULT 0,
    diemTichLuy         INT             NOT NULL DEFAULT 0,

    CONSTRAINT PK_HoaDonBanHang         PRIMARY KEY (maHDBH),
    CONSTRAINT CHK_HDBH_HTTT            CHECK (hinhThucThanhToan IN ('TIEN_MAT', 'CHUYEN_KHOAN', 'VI_DIEN_TU')),
    CONSTRAINT FK_HDBH_NhanVien         FOREIGN KEY (maNV) REFERENCES NhanVien(maNV) ON UPDATE CASCADE,
    CONSTRAINT FK_HDBH_KhachHang        FOREIGN KEY (maKH) REFERENCES KhachHang(maKH) ON UPDATE CASCADE,
    CONSTRAINT FK_HDBH_MaGiamGia        FOREIGN KEY (maGiam) REFERENCES MaGiamGia(maGiam)
);

-- ============================================================
-- 9. CHI_TIET_HOA_DON
-- ============================================================
CREATE TABLE ChiTietHoaDon (
    maHDBH      VARCHAR(15)     NOT NULL,
    maLo        VARCHAR(15)     NOT NULL,
    soLuong     INT             NOT NULL,
    donGia      DECIMAL(15,2)   NOT NULL,
    thanhTien   DECIMAL(15,2)   NOT NULL,

    CONSTRAINT PK_ChiTietHoaDon     PRIMARY KEY (maHDBH, maLo),
    CONSTRAINT FK_CTHD_HoaDon       FOREIGN KEY (maHDBH) REFERENCES HoaDonBanHang(maHDBH) ON DELETE CASCADE,
    CONSTRAINT FK_CTHD_LoHang       FOREIGN KEY (maLo) REFERENCES LoHang(maLo)
);
GO

-- ============================================================
-- DỮ LIỆU MẪU (Bản đầy đủ 20 sản phẩm cho Nhóm 11)
-- ============================================================

-- 1. Loại khách hàng
INSERT INTO LoaiKhachHang VALUES ('DONG', N'Đồng', 0, 0), ('BAC', N'Bạc', 5, 1000), ('VANG', N'Vàng', 10, 5000);

-- 2. Loại hàng hóa
INSERT INTO LoaiHangHoa VALUES ('TP', N'Thực phẩm', N'Đồ ăn đóng gói'), ('DU', N'Đồ uống', N'Nước giải khát');

-- 3. Nhân viên
INSERT INTO NhanVien VALUES
    ('NV001', N'Nguyễn Tuấn Duy',   N'123 Lý Thường Kiệt, Q.10, TP.HCM', '2024-01-10', 1, '0901234501', '123456', 1),
    ('NV002', N'Trần Thị Bích Ngọc', N'45 Nguyễn Trãi, Q.1, TP.HCM',      '2024-03-15', 0, '0912345602', '123456', 0),
    ('NV003', N'Lê Văn Hùng',        N'78 Cách Mạng Tháng 8, Q.3, TP.HCM', '2025-06-01', 1, '0923456703', '123456', 0);

-- 4. Khách hàng
INSERT INTO KhachHang VALUES ('KH001', N'Trần Văn An', '0912345678', 1200, 'BAC');

-- 5. Danh sách 20 Hàng hóa (Khớp với file ảnh trong Resource.HangHoa)
INSERT INTO HangHoa (maHH, maVach, tenHH, hinhAnh, giaSP, maLoaiHang, conKinhDoanh) VALUES 
('HH001', '8934588012345', N'Mì Hảo Hảo', 'haohao.png', 5500, 'TP', 1),
('HH002', '89300002', N'Nước ngọt 7Up', '7up.png', 15000, 'DU', 1),
('HH003', '89300003', N'Nước khoáng Aquafina', 'aquafina.png', 10000, 'DU', 1),
('HH004', '89300004', N'Bánh su kem', 'Banhsukem.png', 25000, 'TP', 1),
('HH005', '89300005', N'Bánh bông lan nhỏ', 'Bonglannho.png', 12000, 'TP', 1),
('HH006', '89300006', N'Bông lan phô mai', 'Bonglanphomai.png', 45000, 'TP', 1),
('HH007', '89300007', N'Nước ngọt Coca', 'coca.png', 15000, 'DU', 1),
('HH008', '89300008', N'Sữa đậu nành Fami', 'fami.png', 7000, 'DU', 1),
('HH009', '89300009', N'Bánh Hamburger', 'Hamburger.png', 35000, 'TP', 1),
('HH010', '89300010', N'Hạt điều rang muối', 'hatdieu.png', 55000, 'TP', 1),
('HH011', '89300011', N'Khoai tây Lays', 'khoaitaylays.png', 18000, 'TP', 1),
('HH012', '89300012', N'Khô gà lá chanh', 'khogalachanh.png', 32000, 'TP', 1),
('HH013', '89300013', N'Mirinda đá dưa', 'mirindadua.png', 12000, 'DU', 1),
('HH014', '89300014', N'Mít sấy giòn', 'mitsay.png', 28000, 'TP', 1),
('HH015', '89300015', N'Nước ngọt Pepsi', 'pepsi.png', 15000, 'DU', 1),
('HH016', '89300016', N'Bánh Sandwich', 'Sandwich.png', 22000, 'TP', 1),
('HH017', '89300017', N'Snack rong biển', 'snackrongbien.png', 14000, 'TP', 1),
('HH018', '89300018', N'Nước ngọt Sprite', 'sprite.png', 15000, 'DU', 1),
('HH019', '89300019', N'Nước tăng lực Sting', 'sting.png', 15000, 'DU', 1),
('HH020', '89300020', N'Zero Soda', 'zerosoda.png', 18000, 'DU', 1);

-- 6. Lô hàng (Cần có ít nhất 1 lô cho mỗi sản phẩm để hiện tồn kho)
INSERT INTO LoHang VALUES ('LO001', 'HH001', '2025-03-01', '2026-01-01', 200, 87);
INSERT INTO LoHang VALUES ('LO002', 'HH002', '2025-03-01', '2026-01-01', 100, 50);
-- (Có thể thêm tiếp các LO003... nếu bạn muốn hiện tồn kho cho toàn bộ món)

GO
-- ============================================================
-- VIEW (Bắt đầu từ đây...)
-- ============================================================
-- VIEW
-- ============================================================
CREATE VIEW v_TonKho AS
SELECT
    h.maHH, h.maVach, h.tenHH, h.giaSP, lhh.tenLoaiHang,
    COALESCE(SUM(l.soLuongTon), 0) AS tongTonKho,
    MIN(l.hanSuDung) AS hanSuDungGanNhat
FROM HangHoa h
JOIN LoaiHangHoa lhh ON h.maLoaiHang = lhh.maLoaiHang
LEFT JOIN LoHang l ON h.maHH = l.maHH AND l.hanSuDung >= CAST(GETDATE() AS DATE)
WHERE h.conKinhDoanh = 1
GROUP BY h.maHH, h.maVach, h.tenHH, h.giaSP, lhh.tenLoaiHang;
GO

-- ============================================================
-- STORED PROCEDURES
-- ============================================================

-- Tìm khách hàng theo SĐT
CREATE PROCEDURE sp_TimKhachHangSDT @p_sdt VARCHAR(15)
AS
BEGIN
    SELECT kh.maKH, kh.tenKH, kh.soDienThoai, kh.diemTL, lkh.tenLKH, lkh.giamGia
    FROM KhachHang kh
    JOIN LoaiKhachHang lkh ON kh.maLKH = lkh.maLKH
    WHERE kh.soDienThoai = @p_sdt;
END;
GO

-- Tìm kiếm nhân viên theo mã hoặc tên (dùng cho Java LIKE query)
CREATE PROCEDURE sp_TimNhanVien @p_keyword NVARCHAR(100)
AS
BEGIN
    DECLARE @kw NVARCHAR(102) = N'%' + @p_keyword + N'%';
    SELECT maNV, tenNV,
           CASE gioiTinh WHEN 1 THEN N'Nam' ELSE N'Nữ' END AS gioiTinh,
           sdt, ngayVaoLam,
           CASE quanLy WHEN 1 THEN N'Quản lý' ELSE N'Nhân viên thu ngân' END AS vaiTro,
           diaChi
    FROM NhanVien
    WHERE maNV LIKE @kw OR tenNV LIKE @kw
    ORDER BY maNV;
END;
GO
-- ============================================================
-- Cập nhật mã vạch chuẩn Code-128 cho 20 sản phẩm 
-- ============================================================
USE quanlycuahangtienloi;
GO

-- Cập nhật mã vạch chuẩn EAN-13 cho 20 sản phẩm
UPDATE HangHoa SET maVach = '8930000000010' WHERE maHH = 'HH001';
UPDATE HangHoa SET maVach = '8930000000027' WHERE maHH = 'HH002';
UPDATE HangHoa SET maVach = '8930000000034' WHERE maHH = 'HH003';
UPDATE HangHoa SET maVach = '8930000000041' WHERE maHH = 'HH004';
UPDATE HangHoa SET maVach = '8930000000058' WHERE maHH = 'HH005';
UPDATE HangHoa SET maVach = '8930000000065' WHERE maHH = 'HH006';
UPDATE HangHoa SET maVach = '8930000000072' WHERE maHH = 'HH007';
UPDATE HangHoa SET maVach = '8930000000089' WHERE maHH = 'HH008';
UPDATE HangHoa SET maVach = '8930000000096' WHERE maHH = 'HH009';
UPDATE HangHoa SET maVach = '8930000000102' WHERE maHH = 'HH010';
UPDATE HangHoa SET maVach = '8930000000119' WHERE maHH = 'HH011';
UPDATE HangHoa SET maVach = '8930000000126' WHERE maHH = 'HH012';
UPDATE HangHoa SET maVach = '8930000000133' WHERE maHH = 'HH013';
UPDATE HangHoa SET maVach = '8930000000140' WHERE maHH = 'HH014';
UPDATE HangHoa SET maVach = '8930000000157' WHERE maHH = 'HH015';
UPDATE HangHoa SET maVach = '8930000000164' WHERE maHH = 'HH016';
UPDATE HangHoa SET maVach = '8930000000171' WHERE maHH = 'HH017';
UPDATE HangHoa SET maVach = '8930000000188' WHERE maHH = 'HH018';
UPDATE HangHoa SET maVach = '8930000000195' WHERE maHH = 'HH019';
UPDATE HangHoa SET maVach = '8930000000201' WHERE maHH = 'HH020';
GO
-- ============================================================
-- Thêm đồng loạt 20 lô	hàng cho 20 sản phẩm 
-- ============================================================
USE quanlycuahangtienloi;
GO

-- 1. Xóa dữ liệu các bảng phụ thuộc trước (xóa hóa đơn nháp để gỡ ràng buộc)
DELETE FROM ChiTietHoaDon;
DELETE FROM HoaDonBanHang;

-- 2. Lúc này bảng LoHang đã được "tự do", có thể xóa sạch
DELETE FROM LoHang;
GO

-- 3. Thêm đồng loạt 20 lô hàng cho 20 sản phẩm
INSERT INTO LoHang (maLo, maHH, ngayNhap, hanSuDung, soLuongNhap, soLuongTon) VALUES 
('LO001', 'HH001', '2025-04-01', '2026-04-01', 100, 100),
('LO002', 'HH002', '2025-04-01', '2026-04-01', 100, 100),
('LO003', 'HH003', '2025-04-01', '2026-04-01', 100, 100),
('LO004', 'HH004', '2025-04-01', '2025-05-01', 100, 100),
('LO005', 'HH005', '2025-04-01', '2025-05-01', 100, 100),
('LO006', 'HH006', '2025-04-01', '2025-05-01', 100, 100),
('LO007', 'HH007', '2025-04-01', '2026-04-01', 100, 100),
('LO008', 'HH008', '2025-04-01', '2025-10-01', 100, 100),
('LO009', 'HH009', '2025-04-01', '2025-04-15', 100, 100),
('LO010', 'HH010', '2025-04-01', '2026-04-01', 100, 100),
('LO011', 'HH011', '2025-04-01', '2026-04-01', 100, 100),
('LO012', 'HH012', '2025-04-01', '2025-10-01', 100, 100),
('LO013', 'HH013', '2025-04-01', '2026-04-01', 100, 100),
('LO014', 'HH014', '2025-04-01', '2025-12-01', 100, 100),
('LO015', 'HH015', '2025-04-01', '2026-04-01', 100, 100),
('LO016', 'HH016', '2025-04-01', '2025-04-10', 100, 100),
('LO017', 'HH017', '2025-04-01', '2026-04-01', 100, 100),
('LO018', 'HH018', '2025-04-01', '2026-04-01', 100, 100),
('LO019', 'HH019', '2025-04-01', '2026-04-01', 100, 100),
('LO020', 'HH020', '2025-04-01', '2026-04-01', 100, 100);
GO
