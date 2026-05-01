package dAO;

import connectDB.ConnectDB;
import java.sql.*;
import java.util.*;

public class ThongKe_DAO {

    // Doanh thu theo tháng — dùng đúng cột tongTienThanhToan
    public Map<String, Double> getDoanhThuTheoThang(int nam) {
        Map<String, Double> map = new LinkedHashMap<>();
        for (int i = 1; i <= 12; i++) {
            map.put("T" + i, 0.0);
        }
        
        String sql = "SELECT MONTH(ngayLapHDBH) AS thang, SUM(tongTienThanhToan) AS dt " +
                     "FROM HoaDonBanHang WHERE YEAR(ngayLapHDBH)=? GROUP BY MONTH(ngayLapHDBH)";
                     
        // Sử dụng try-with-resources để tự động đóng PreparedStatement và ResultSet
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setInt(1, nam);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put("T" + rs.getInt("thang"), rs.getDouble("dt"));
                }
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return map;
    }

    // Top N sản phẩm bán chạy — join qua maLo → HangHoa
    public List<Object[]> getTopSanPham(int topN) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT TOP(?) h.tenHH, SUM(c.soLuong) AS tongBan " +
                     "FROM ChiTietHoaDon c " +
                     "JOIN LoHang l ON c.maLo = l.maLo " +
                     "JOIN HangHoa h ON l.maHH = h.maHH " +
                     "GROUP BY h.tenHH ORDER BY tongBan DESC";
                     
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setInt(1, topN);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{rs.getString("tenHH"), rs.getInt("tongBan")});
                }
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return list;
    }

    // Doanh thu hôm nay
    public double getDoanhThuHomNay() {
        String sql = "SELECT ISNULL(SUM(tongTienThanhToan),0) FROM HoaDonBanHang " +
                     "WHERE CAST(ngayLapHDBH AS DATE)=CAST(GETDATE() AS DATE)";
                     
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            if (rs.next()) return rs.getDouble(1);
            
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return 0;
    }

    // Số hóa đơn hôm nay
    public int getSoHoaDonHomNay() {
        String sql = "SELECT COUNT(*) FROM HoaDonBanHang " +
                     "WHERE CAST(ngayLapHDBH AS DATE)=CAST(GETDATE() AS DATE)";
                     
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            if (rs.next()) return rs.getInt(1);
            
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return 0;
    }

    // Doanh thu tháng này
    public double getDoanhThuThangNay() {
        String sql = "SELECT ISNULL(SUM(tongTienThanhToan),0) FROM HoaDonBanHang " +
                     "WHERE MONTH(ngayLapHDBH)=MONTH(GETDATE()) AND YEAR(ngayLapHDBH)=YEAR(GETDATE())";
                     
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            if (rs.next()) return rs.getDouble(1);
            
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return 0;
    }

    // Số hóa đơn tháng này (giữ nguyên cho tương thích)
    public int getSoHoaDonThangNay() {
        String sql = "SELECT COUNT(*) FROM HoaDonBanHang " +
                     "WHERE MONTH(ngayLapHDBH)=MONTH(GETDATE()) AND YEAR(ngayLapHDBH)=YEAR(GETDATE())";
                     
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            if (rs.next()) return rs.getInt(1);
            
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return 0;
    }

    // --- CÁC HÀM MỚI THEO THÁNG / NĂM ĐƯỢC CHỌN ---
    public double getDoanhThuTheoThangNam(int thang, int nam) {
        String sql = "SELECT ISNULL(SUM(tongTienThanhToan),0) FROM HoaDonBanHang " +
                     "WHERE MONTH(ngayLapHDBH)=? AND YEAR(ngayLapHDBH)=?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, thang);
            ps.setInt(2, nam);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getSoHoaDonTheoThangNam(int thang, int nam) {
        String sql = "SELECT COUNT(*) FROM HoaDonBanHang " +
                     "WHERE MONTH(ngayLapHDBH)=? AND YEAR(ngayLapHDBH)=?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, thang);
            ps.setInt(2, nam);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public double getDoanhThuTheoNam(int nam) {
        String sql = "SELECT ISNULL(SUM(tongTienThanhToan),0) FROM HoaDonBanHang WHERE YEAR(ngayLapHDBH)=?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nam);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getSoHoaDonTheoNam(int nam) {
        String sql = "SELECT COUNT(*) FROM HoaDonBanHang WHERE YEAR(ngayLapHDBH)=?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nam);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // Doanh thu theo ngày trong tháng
    public Map<String, Double> getDoanhThuTheoNgayTrongThang(int thang, int nam) {
        Map<String, Double> map = new LinkedHashMap<>();
        // Lấy số ngày trong tháng
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, nam);
        cal.set(Calendar.MONTH, thang - 1);
        int maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= maxDays; i++) {
            map.put(String.valueOf(i), 0.0);
        }
        
        String sql = "SELECT DAY(ngayLapHDBH) AS ngay, SUM(tongTienThanhToan) AS dt " +
                     "FROM HoaDonBanHang WHERE MONTH(ngayLapHDBH)=? AND YEAR(ngayLapHDBH)=? GROUP BY DAY(ngayLapHDBH)";
                     
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setInt(1, thang);
            ps.setInt(2, nam);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(String.valueOf(rs.getInt("ngay")), rs.getDouble("dt"));
                }
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return map;
    }

    // Top N sản phẩm theo tháng
    public List<Object[]> getTopSanPhamTheoThang(int thang, int nam, int topN) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT TOP(?) h.tenHH, SUM(c.soLuong) AS tongBan " +
                     "FROM ChiTietHoaDon c " +
                     "JOIN LoHang l ON c.maLo = l.maLo " +
                     "JOIN HangHoa h ON l.maHH = h.maHH " +
                     "JOIN HoaDonBanHang hd ON hd.maHDBH = c.maHDBH " +
                     "WHERE MONTH(hd.ngayLapHDBH)=? AND YEAR(hd.ngayLapHDBH)=? " +
                     "GROUP BY h.tenHH ORDER BY tongBan DESC";
                     
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setInt(1, topN);
            ps.setInt(2, thang);
            ps.setInt(3, nam);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{rs.getString("tenHH"), rs.getInt("tongBan")});
                }
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return list;
    }
}