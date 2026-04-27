-- ============================================================
--  Script tạo Database và bảng NHANVIEN cho dự án HSK
--  Chạy trong SQL Server Management Studio (SSMS)
--  hoặc qua sqlcmd: sqlcmd -S <server> -i setup_database.sql
-- ============================================================

-- 1. Tạo Database (nếu chưa tồn tại)
IF NOT EXISTS (
    SELECT name FROM sys.databases WHERE name = N'HSK_DB'
)
BEGIN
    CREATE DATABASE HSK_DB;
    PRINT N'✅ Đã tạo database HSK_DB';
END
ELSE
    PRINT N'ℹ️  Database HSK_DB đã tồn tại.';
GO

USE HSK_DB;
GO

-- 2. Tạo bảng NHANVIEN
IF NOT EXISTS (
    SELECT * FROM sys.objects
    WHERE object_id = OBJECT_ID(N'NHANVIEN') AND type = N'U'
)
BEGIN
    CREATE TABLE NHANVIEN (
        maNV        NVARCHAR(20)   NOT NULL PRIMARY KEY,   -- Mã nhân viên (dùng để đăng nhập)
        tenNV       NVARCHAR(100)  NOT NULL,               -- Tên nhân viên
        diaChi      NVARCHAR(255)  NULL,                   -- Địa chỉ
        ngayVaoLam  DATE           NULL DEFAULT GETDATE(), -- Ngày vào làm
        gioiTinh    NVARCHAR(10)   NULL,                   -- Giới tính: Nam / Nữ / Khác
        sdt         NVARCHAR(15)   NULL,                   -- Số điện thoại
        matKhau     NVARCHAR(255)  NOT NULL,               -- Mật khẩu
        quanLy      BIT            NOT NULL DEFAULT 0      -- 1 = Quản lý | 0 = Nhân viên thu ngân
    );
    PRINT N'✅ Đã tạo bảng NHANVIEN';
END
ELSE
BEGIN
    PRINT N'ℹ️  Bảng NHANVIEN đã tồn tại.';
END
GO

-- 3. Thêm dữ liệu mẫu
IF NOT EXISTS (SELECT 1 FROM NHANVIEN WHERE maNV = 'NV001')
BEGIN
    INSERT INTO NHANVIEN (maNV, tenNV, diaChi, ngayVaoLam, gioiTinh, sdt, matKhau, quanLy)
    VALUES
        ('NV001', N'Nguyễn Văn An',
         N'123 Lê Lợi, Q.1, TP.HCM',
         '2023-01-15', N'Nam',  '0901234567', '123456', 1),  -- Quản lý

        ('NV002', N'Trần Thị Bình',
         N'45 Nguyễn Huệ, Q.1, TP.HCM',
         '2023-03-20', N'Nữ',  '0912345678', '123456', 0),  -- Thu ngân

        ('NV003', N'Lê Hoàng Cường',
         N'78 Hai Bà Trưng, Q.3, TP.HCM',
         '2024-06-01', N'Nam', '0923456789', '123456', 0),  -- Thu ngân

        ('ADMIN', N'Quản Trị Viên',
         N'Văn phòng chính',
         '2022-01-01', N'Nam', '0900000000', 'admin',  1);  -- Quản lý

    PRINT N'✅ Đã thêm 4 tài khoản mẫu';
END
ELSE
    PRINT N'ℹ️  Dữ liệu mẫu đã tồn tại.';
GO

-- 4. Kiểm tra kết quả
SELECT
    maNV       AS [Mã NV],
    tenNV      AS [Tên nhân viên],
    gioiTinh   AS [Giới tính],
    sdt        AS [SĐT],
    ngayVaoLam AS [Ngày vào làm],
    matKhau    AS [Mật khẩu],
    CASE quanLy WHEN 1 THEN N'Quản lý' ELSE N'Nhân viên thu ngân' END AS [Vai trò]
FROM NHANVIEN;
GO

PRINT N'';
PRINT N'============================================';
PRINT N' Tài khoản đăng nhập thử:';
PRINT N'   NV001 / 123456  → Quản lý';
PRINT N'   NV002 / 123456  → Thu ngân';
PRINT N'   ADMIN / admin   → Quản lý';
PRINT N'============================================';

-- ============================================================
--  Script bổ sung các bảng phục vụ Module BÁN HÀNG	
-- ============================================================
USE HSK_DB;
GO

-- 1. Tạo bảng LOAIKHACHHANG: Phân hạng thành viên
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'LOAIKHACHHANG') AND type = N'U')
BEGIN
    CREATE TABLE LOAIKHACHHANG (
        maLKH    VARCHAR(10)   NOT NULL PRIMARY KEY,
        tenLKH   NVARCHAR(50)  NOT NULL,
        giamGia  INT           DEFAULT 0, -- % giảm giá trực tiếp
        mucDiem  INT           DEFAULT 0  -- Mức điểm tối thiểu để đạt hạng
    );
END

-- 2. Tạo bảng KHACHHANG: Quản lý khách thân thiết 
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'KHACHHANG') AND type = N'U')
BEGIN
    CREATE TABLE KHACHHANG (
        maKH         VARCHAR(10)   NOT NULL PRIMARY KEY,
        tenKH        NVARCHAR(100) NOT NULL,
        soDienThoai  VARCHAR(15)   UNIQUE,
        diemTL       INT           DEFAULT 0,
        maLKH        VARCHAR(10)   FOREIGN KEY REFERENCES LOAIKHACHHANG(maLKH)
    );
END

-- 3. Tạo bảng LOAIHANGHOA: Phân loại để hiển thị 
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'LOAIHANGHOA') AND type = N'U')
BEGIN
    CREATE TABLE LOAIHANGHOA (
        maLoaiHang  VARCHAR(10)   NOT NULL PRIMARY KEY,
        tenLoaiHang NVARCHAR(100) NOT NULL,
        mota        NVARCHAR(MAX) NULL
    );
END

-- 4. Tạo bảng HANGHOA: Trái tim của POS (Có mã vạch) 
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'HANGHOA') AND type = N'U')
BEGIN
    CREATE TABLE HANGHOA (
        maHH        VARCHAR(10)   NOT NULL PRIMARY KEY,
        maVach      VARCHAR(50)   NOT NULL UNIQUE, -- Cột này dùng để quét mã
        tenHH       NVARCHAR(200) NOT NULL,
        hinhAnh     NVARCHAR(MAX) NULL,
        giaSP       DECIMAL(18,2) NOT NULL CHECK (giaSP > 0),
        maLoaiHang  VARCHAR(10)   FOREIGN KEY REFERENCES LOAIHANGHOA(maLoaiHang)
    );
END

-- 5. Tạo bảng MAGIAMGIA: Các Voucher khuyến mãi 
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'MAGIAMGIA') AND type = N'U')
BEGIN
    CREATE TABLE MAGIAMGIA (
        maGiam      VARCHAR(50) NOT NULL PRIMARY KEY,
        giamGia     INT         NOT NULL -- Số % giảm thêm
    );
END

-- 6. Tạo bảng HOADONBANHANG & CHITIETHOADON: Kết quả của POS 
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'HOADONBANHANG') AND type = N'U')
BEGIN
    CREATE TABLE HOADONBANHANG (
        maHDBH            VARCHAR(10)    NOT NULL PRIMARY KEY,
        ngayLapHDBH       DATETIME       DEFAULT GETDATE(),
        maNV              NVARCHAR(20)   FOREIGN KEY REFERENCES NHANVIEN(maNV),
        maKH              VARCHAR(10)    FOREIGN KEY REFERENCES KHACHHANG(maKH),
        hinhThucThanhToan NVARCHAR(50),
        maGiam            VARCHAR(50)    FOREIGN KEY REFERENCES MAGIAMGIA(maGiam),
        tongTien          DECIMAL(18,2)  NOT NULL
    );
END

IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'CHITIETHOADON') AND type = N'U')
BEGIN
    CREATE TABLE CHITIETHOADON (
        maHDBH    VARCHAR(10)    FOREIGN KEY REFERENCES HOADONBANHANG(maHDBH),
        maHH      VARCHAR(10)    FOREIGN KEY REFERENCES HANGHOA(maHH),
        soLuong   INT            NOT NULL CHECK (soLuong > 0),
        donGia    DECIMAL(18,2)  NOT NULL,
        thanhTien AS (soLuong * donGia),
        PRIMARY KEY (maHDBH, maHH)
    );
END
GO

-- ============================================================
--  DỮ LIỆU MẪU 
-- ============================================================

-- Chèn Hạng khách hàng
INSERT INTO LOAIKHACHHANG (maLKH, tenLKH, giamGia, mucDiem) VALUES
    ('LKH01', N'Đồng', 0, 0),
    ('LKH02', N'Bạc', 2, 100),
    ('LKH03', N'Vàng', 5, 500);

-- Chèn Khách hàng mẫu
INSERT INTO KHACHHANG (maKH, tenKH, soDienThoai, diemTL, maLKH) VALUES
    ('KH001', N'Nguyễn Văn A', '0987654321', 150, 'LKH02'),
    ('KH002', N'Nguyễn Tuấn D', '0912345678', 50, 'LKH01');

-- Chèn Loại hàng và Hàng hóa
INSERT INTO LOAIHANGHOA (maLoaiHang, tenLoaiHang) VALUES ('L01', N'Thực phẩm');
INSERT INTO HANGHOA (maHH, maVach, tenHH, giaSP, maLoaiHang) VALUES
    ('SP001', '8934567890123', N'Mì Hảo Hảo Tôm Chua Cay', 5000, 'L01'),
    ('SP002', '8934567890456', N'Nước ngọt Coca-Cola 330ml', 10000, 'L01'),
    ('SP003', '8934567890789', N'Bánh Snack Oishi', 6000, 'L01');
	-- Thêm sản phẩm mẫu
INSERT INTO HANGHOA (maHH, maVach, tenHH, giaSP, maLoaiHang) VALUES
    ('SP004', '8934567890991', N'Sữa tươi Vinamilk 180ml', 7000, 'L01'),
    ('SP005', '8934567890992', N'Bánh mì ngọt Kinh Đô', 8000, 'L01'),
    ('SP006', '8934567890993', N'Nước suối Aquafina 500ml', 5000, 'L01'),
    ('SP007', '8934567890994', N'Trà xanh Không Độ 455ml', 10000, 'L01'),
    ('SP008', '8934567890995', N'Xúc xích Đức Việt', 12000, 'L01'),
    ('SP009', '8934567890996', N'Bánh Oreo Chocolate', 15000, 'L01');

PRINT N'Đã thêm sản phẩm mẫu!';
GO

-- Chèn Mã giảm giá mẫu
INSERT INTO MAGIAMGIA (maGiam, giamGia) VALUES
    ('GIAM10', 10),
    ('KHAI_TRUONG', 20);

PRINT N'Đã chèn dữ liệu mẫu cho Module Bán hàng!';
GO