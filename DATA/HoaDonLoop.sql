-- ============================================================
-- SCRIPT TẠO DỮ LIỆU GIẢ (MOCK DATA) CHO BẢNG HÓA ĐƠN
-- ============================================================
USE quanlycuahangtienloi;
GO

-- 1. Clean up: Xóa dữ liệu cũ
DELETE FROM ChiTietHoaDon;
DELETE FROM HoaDonBanHang;
GO

-- (Tùy chọn) Thêm một số mã giảm giá mẫu nếu chưa có để script có data test
IF NOT EXISTS (SELECT 1 FROM MaGiamGia)
BEGIN
    INSERT INTO MaGiamGia (maGiam, loaiGiam, giaTriGiam, ngayBatDau, ngayKetThuc, soLanDungToiDa, soLanDaDung, conHieuLuc)
    VALUES 
    ('GIAM10K', 'SO_TIEN', 10000, '2020-01-01', '2030-12-31', 10000, 0, 1),
    ('GIAM5PT', 'PHAN_TRAM', 5, '2020-01-01', '2030-12-31', 10000, 0, 1);
END
GO

-- 2. Main Loop: Khởi tạo biến cho vòng lặp
DECLARE @i INT = 1;
DECLARE @TotalInvoices INT = 500;

WHILE @i <= @TotalInvoices
BEGIN
    -- Tạo mã hóa đơn tuần tự (HD000001, HD000002...)
    DECLARE @maHDBH VARCHAR(15) = 'HD' + RIGHT('000000' + CAST(@i AS VARCHAR), 6);
    
    -- Date: Random ngày từ 2020 đến nay
    DECLARE @DaysBetween INT = DATEDIFF(DAY, '2020-01-01', GETDATE());
    DECLARE @RandomDays INT = ABS(CHECKSUM(NEWID())) % @DaysBetween;
    DECLARE @ngayLapHDBH DATETIME = DATEADD(DAY, @RandomDays, '2020-01-01');

    -- Entities: Chọn ngẫu nhiên maNV
    DECLARE @maNV VARCHAR(10);
    SELECT TOP 1 @maNV = maNV FROM NhanVien ORDER BY NEWID();

    -- Entities: Chọn ngẫu nhiên maKH (cho phép 20% là khách lẻ/vãng lai không có maKH)
    DECLARE @maKH VARCHAR(10) = NULL;
    IF (ABS(CHECKSUM(NEWID())) % 100) > 20
    BEGIN
        SELECT TOP 1 @maKH = maKH FROM KhachHang ORDER BY NEWID();
    END

    -- Entities: Chọn ngẫu nhiên maGiam (tỷ lệ 30% có mã giảm)
    DECLARE @maGiam VARCHAR(20) = NULL;
    IF (ABS(CHECKSUM(NEWID())) % 100) < 30
    BEGIN
        SELECT TOP 1 @maGiam = maGiam FROM MaGiamGia WHERE conHieuLuc = 1 ORDER BY NEWID();
    END

    -- Lấy % giảm giá theo hạng thành viên của khách hàng
    DECLARE @giamGiaTV DECIMAL(5,2) = 0;
    IF @maKH IS NOT NULL
    BEGIN
        SELECT @giamGiaTV = l.giamGia 
        FROM KhachHang k 
        JOIN LoaiKhachHang l ON k.maLKH = l.maLKH 
        WHERE k.maKH = @maKH;
    END

    -- Lấy thông tin mã giảm giá (Voucher)
    DECLARE @loaiGiam VARCHAR(20) = NULL;
    DECLARE @giaTriGiam DECIMAL(15,2) = 0;
    IF @maGiam IS NOT NULL
    BEGIN
        SELECT @loaiGiam = loaiGiam, @giaTriGiam = giaTriGiam 
        FROM MaGiamGia WHERE maGiam = @maGiam;
    END

    -- Hình thức thanh toán ngẫu nhiên
    DECLARE @httt VARCHAR(20);
    DECLARE @randHTTT INT = ABS(CHECKSUM(NEWID())) % 3;
    SET @httt = CASE @randHTTT 
                    WHEN 0 THEN 'TIEN_MAT' 
                    WHEN 1 THEN 'CHUYEN_KHOAN' 
                    ELSE 'VI_DIEN_TU' 
                END;

    -- BƯỚC A: Insert vào HoaDonBanHang trước (Tổng tiền = 0) để thỏa mãn Foreign Key
    INSERT INTO HoaDonBanHang (maHDBH, ngayLapHDBH, maNV, maKH, maGiam, hinhThucThanhToan, tongTienGoc, tienGiamThanhVien, tienGiamVoucher, tongTienThanhToan, diemTichLuy)
    VALUES (@maHDBH, @ngayLapHDBH, @maNV, @maKH, @maGiam, @httt, 0, 0, 0, 0, 0);

    -- BƯỚC B: Nested Loop chèn ChiTietHoaDon (Từ 1 đến 5 mặt hàng ngẫu nhiên)
    DECLARE @NumItems INT = (ABS(CHECKSUM(NEWID())) % 5) + 1; 
    DECLARE @j INT = 1;
    DECLARE @tongTienGoc DECIMAL(15,2) = 0;

    WHILE @j <= @NumItems
    BEGIN
        DECLARE @maLo VARCHAR(15);
        DECLARE @donGia DECIMAL(15,2);
        
        -- RESET biến để tránh T-SQL giữ lại giá trị của vòng lặp trước nếu câu lệnh SELECT không trả về kết quả
        SET @maLo = NULL;
        SET @donGia = 0;

        -- Chọn ngẫu nhiên 1 lô hàng, không trùng lặp lô trong cùng 1 hóa đơn
        SELECT TOP 1 @maLo = l.maLo, @donGia = h.giaSP 
        FROM LoHang l 
        JOIN HangHoa h ON l.maHH = h.maHH 
        WHERE l.maLo NOT IN (SELECT maLo FROM ChiTietHoaDon WHERE maHDBH = @maHDBH)
        ORDER BY NEWID();

        IF @maLo IS NOT NULL
        BEGIN
            -- Số lượng ngẫu nhiên 1-5
            DECLARE @soLuong INT = (ABS(CHECKSUM(NEWID())) % 5) + 1; 
            
            -- Tính thanhTien cho mỗi dòng = Số lượng * Giá sản phẩm
            DECLARE @thanhTien DECIMAL(15,2) = @soLuong * @donGia;

            INSERT INTO ChiTietHoaDon (maHDBH, maLo, soLuong, donGia, thanhTien)
            VALUES (@maHDBH, @maLo, @soLuong, @donGia, @thanhTien);

            -- Cộng dồn tổng tiền gốc
            SET @tongTienGoc = @tongTienGoc + @thanhTien;
        END
        ELSE
        BEGIN
            -- Nếu không tìm thấy lô hàng nào khác (ví dụ kho chỉ có 2 lô mà random ra NumItems = 5) thì thoát vòng lặp con
            BREAK;
        END

        SET @j = @j + 1;
    END

    -- BƯỚC C: Calculations (Tính toán tổng cho hóa đơn)
    
    -- Tính tiền giảm theo thành viên
    DECLARE @tienGiamThanhVien DECIMAL(15,2) = @tongTienGoc * (@giamGiaTV / 100.0);
    DECLARE @tienGiamVoucher DECIMAL(15,2) = 0;

    -- Tính tiền giảm theo Voucher
    IF @maGiam IS NOT NULL
    BEGIN
        IF @loaiGiam = 'PHAN_TRAM'
            SET @tienGiamVoucher = @tongTienGoc * (@giaTriGiam / 100.0);
        ELSE IF @loaiGiam = 'SO_TIEN'
            SET @tienGiamVoucher = @giaTriGiam;
    END

    -- Cập nhật tổng tiền thanh toán sau khi trừ giảm giá
    DECLARE @tongTienThanhToan DECIMAL(15,2) = @tongTienGoc - @tienGiamThanhVien - @tienGiamVoucher;
    IF @tongTienThanhToan < 0 SET @tongTienThanhToan = 0;

    -- Tính điểm tích lũy = 0.01% của tổng tiền (10,000 VND = 1 điểm)
    DECLARE @diemTichLuy INT = CAST((@tongTienThanhToan * 0.0001) AS INT);

    -- Cập nhật (UPDATE) tổng kết vào bảng HoaDonBanHang
    UPDATE HoaDonBanHang
    SET tongTienGoc = @tongTienGoc,
        tienGiamThanhVien = @tienGiamThanhVien,
        tienGiamVoucher = @tienGiamVoucher,
        tongTienThanhToan = @tongTienThanhToan,
        diemTichLuy = @diemTichLuy
    WHERE maHDBH = @maHDBH;

    -- Tăng biến lặp
    SET @i = @i + 1;
END
GO
