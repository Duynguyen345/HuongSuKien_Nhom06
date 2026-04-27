package dAO;

import java.sql.*;
import connectDB.ConnectDB;
import model.KhachHang;
import model.LoaiKhachHang;

public class KhachHang_DAO {
    public KhachHang timKhachHangTheoSDT(String sdt) {
        KhachHang kh = null;
        String sql = "SELECT k.*, l.tenLKH, l.giamGia FROM KHACHHANG k " +
                     "JOIN LOAIKHACHHANG l ON k.maLKH = l.maLKH WHERE soDienThoai = ?";
        
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, sdt);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LoaiKhachHang loai = new LoaiKhachHang(
                        rs.getString("maLKH"),
                        rs.getString("tenLKH"),
                        rs.getInt("giamGia")
                    );
                    kh = new KhachHang(
                        rs.getString("maKH"),
                        rs.getString("tenKH"),
                        rs.getString("soDienThoai"),
                        rs.getInt("diemTL"),
                        loai
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kh;
    }
}