package dAO;

import java.sql.*;
import java.util.ArrayList;
import connectDB.ConnectDB;
import model.LoaiKhachHang;

public class LoaiKhachHang_DAO {

    // 1. Lấy tất cả loại khách hàng (Dùng để đổ dữ liệu vào JComboBox)
    public ArrayList<LoaiKhachHang> getAllLoaiKhachHang() {
        ArrayList<LoaiKhachHang> dsLoaiKhachHang = new ArrayList<>();
        String sql = "SELECT * FROM LoaiKhachHang";
        
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
             
            while (rs.next()) {
                String maLKH = rs.getString("maLKH");
                String tenLKH = rs.getString("tenLKH");
              
                int giamGia = (int) rs.getDouble("giamGia"); 
                int mucDiem = rs.getInt("mucDiem");
                
                LoaiKhachHang lkh = new LoaiKhachHang(maLKH, tenLKH, giamGia, mucDiem);
                dsLoaiKhachHang.add(lkh);
            }   
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsLoaiKhachHang;
    }

    // 2. Tìm loại khách hàng theo tên (Dùng khi xử lý sự kiện trên JComboBox)
    public LoaiKhachHang getLoaiKhachHangTheoTen(String tenLKH) {
        LoaiKhachHang loaiKH = null;
        String sql = "SELECT * FROM LoaiKhachHang WHERE tenLKH = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, tenLKH);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    loaiKH = new LoaiKhachHang(
                        rs.getString("maLKH"),
                        rs.getString("tenLKH"),
                        (int) rs.getDouble("giamGia"),
                        rs.getInt("mucDiem")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loaiKH;
    }

    // 3. Tìm loại khách hàng theo mã (Dùng khi load Khách hàng từ DB)
    public LoaiKhachHang getLoaiKhachHangTheoMa(String maLKH) {
        LoaiKhachHang loaiKH = null;
        String sql = "SELECT * FROM LoaiKhachHang WHERE maLKH = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maLKH);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    loaiKH = new LoaiKhachHang(
                        rs.getString("maLKH"),
                        rs.getString("tenLKH"),
                        (int) rs.getDouble("giamGia"),
                        rs.getInt("mucDiem")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loaiKH;
    }
}