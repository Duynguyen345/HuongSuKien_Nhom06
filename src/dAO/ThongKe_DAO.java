package dAO;

import connectDB.ConnectDB;
import java.sql.*;
import java.util.*;

public class ThongKe_DAO {

    // Doanh thu theo tháng — dùng đúng cột tongTienThanhToan
    public Map<String, Double> getDoanhThuTheoThang(int nam) {
        Map<String, Double> map = new LinkedHashMap<>();
        for (int i = 1; i <= 12; i++) map.put("T" + i, 0.0);
        String sql = "SELECT MONTH(ngayLapHDBH) AS thang, SUM(tongTienThanhToan) AS dt " +
                     "FROM HoaDonBanHang WHERE YEAR(ngayLapHDBH)=? GROUP BY MONTH(ngayLapHDBH)";
        try {
            PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
            ps.setInt(1, nam);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                map.put("T" + rs.getInt("thang"), rs.getDouble("dt"));
        } catch (SQLException e) { e.printStackTrace(); }
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
        try {
            PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
            ps.setInt(1, topN);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(new Object[]{rs.getString("tenHH"), rs.getInt("tongBan")});
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Doanh thu hôm nay
    public double getDoanhThuHomNay() {
        String sql = "SELECT ISNULL(SUM(tongTienThanhToan),0) FROM HoaDonBanHang " +
                     "WHERE CAST(ngayLapHDBH AS DATE)=CAST(GETDATE() AS DATE)";
        try {
            ResultSet rs = ConnectDB.getConnection().prepareStatement(sql).executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // Số hóa đơn hôm nay
    public int getSoHoaDonHomNay() {
        String sql = "SELECT COUNT(*) FROM HoaDonBanHang " +
                     "WHERE CAST(ngayLapHDBH AS DATE)=CAST(GETDATE() AS DATE)";
        try {
            ResultSet rs = ConnectDB.getConnection().prepareStatement(sql).executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // Doanh thu tháng này
    public double getDoanhThuThangNay() {
        String sql = "SELECT ISNULL(SUM(tongTienThanhToan),0) FROM HoaDonBanHang " +
                     "WHERE MONTH(ngayLapHDBH)=MONTH(GETDATE()) AND YEAR(ngayLapHDBH)=YEAR(GETDATE())";
        try {
            ResultSet rs = ConnectDB.getConnection().prepareStatement(sql).executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // Số hóa đơn tháng này
    public int getSoHoaDonThangNay() {
        String sql = "SELECT COUNT(*) FROM HoaDonBanHang " +
                     "WHERE MONTH(ngayLapHDBH)=MONTH(GETDATE()) AND YEAR(ngayLapHDBH)=YEAR(GETDATE())";
        try {
            ResultSet rs = ConnectDB.getConnection().prepareStatement(sql).executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}