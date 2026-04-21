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
