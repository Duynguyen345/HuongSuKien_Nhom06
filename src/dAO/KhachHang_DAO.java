package dAO;

import java.sql.*;
import java.util.ArrayList;
import connectDB.ConnectDB;
import model.KhachHang;
import model.LoaiKhachHang;

public class KhachHang_DAO {

    // 1. Tìm 1 khách hàng theo số điện thoại (Chính xác)
    public KhachHang timKhachHangTheoSDT(String sdt) {
        KhachHang kh = null;
        String sql = "SELECT k.*, l.tenLKH, l.giamGia, l.mucDiem FROM KhachHang k " +
                     "JOIN LoaiKhachHang l ON k.maLKH = l.maLKH WHERE k.soDienThoai = ?";
        
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, sdt);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LoaiKhachHang loai = new LoaiKhachHang(
                        rs.getString("maLKH"),
                        rs.getString("tenLKH"),
                        rs.getInt("giamGia"),
                        rs.getInt("mucDiem")
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

    // 2. Lấy danh sách tất cả khách hàng
    public ArrayList<KhachHang> getAllKhachHang() {
        ArrayList<KhachHang> dsKhachHang = new ArrayList<>();
        String sql = "SELECT k.*, l.tenLKH, l.giamGia, l.mucDiem FROM KhachHang k " +
                     "JOIN LoaiKhachHang l ON k.maLKH = l.maLKH ORDER BY k.maKH ASC";

        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                LoaiKhachHang loai = new LoaiKhachHang(
                    rs.getString("maLKH"),
                    rs.getString("tenLKH"),
                    rs.getInt("giamGia"),
                    rs.getInt("mucDiem")
                );
                KhachHang kh = new KhachHang(
                    rs.getString("maKH"),
                    rs.getString("tenKH"),
                    rs.getString("soDienThoai"),
                    rs.getInt("diemTL"),
                    loai
                );
                dsKhachHang.add(kh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsKhachHang;
    }

    // 3. Tìm khách hàng theo SĐT (Tìm kiếm gần đúng)
    public ArrayList<KhachHang> timDanhSachKhachHangTheoSDT(String sdt) {
        ArrayList<KhachHang> dsKhachHang = new ArrayList<>();
        String sql = "SELECT k.*, l.tenLKH, l.giamGia, l.mucDiem FROM KhachHang k " +
                     "JOIN LoaiKhachHang l ON k.maLKH = l.maLKH WHERE k.soDienThoai LIKE ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + sdt + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LoaiKhachHang loai = new LoaiKhachHang(
                        rs.getString("maLKH"),
                        rs.getString("tenLKH"),
                        rs.getInt("giamGia"),
                        rs.getInt("mucDiem")
                    );
                    KhachHang kh = new KhachHang(
                        rs.getString("maKH"),
                        rs.getString("tenKH"),
                        rs.getString("soDienThoai"),
                        rs.getInt("diemTL"),
                        loai
                    );
                    dsKhachHang.add(kh);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsKhachHang;
    }

 // 4. Thêm khách hàng mới
    public boolean themKhachHang(KhachHang kh) {
        // Chú ý: Đã bổ sung maKH vào cột và dấu ? vào VALUES
        String sql = "INSERT INTO KhachHang (maKH, tenKH, soDienThoai, diemTL, maLKH) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, kh.getMaKH());      // Gắn cái mã vừa sinh ở Panel vào đây
            stmt.setString(2, kh.getTenKH());
            stmt.setString(3, kh.getSoDienThoai());
            stmt.setInt(4, kh.getDiemTL());
            // Mặc định khách mới vào sẽ có hạng 'DONG'
            stmt.setString(5, kh.getLoaiKhachHang() != null ? kh.getLoaiKhachHang().getMaLKH() : "DONG");

            int n = stmt.executeUpdate();
            return n > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 5. Cập nhật thông tin khách hàng
    public boolean suaKhachHang(KhachHang kh) {
        String sql = "UPDATE KhachHang SET tenKH = ?, soDienThoai = ?, diemTL = ?, maLKH = ? WHERE maKH = ?";
        
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, kh.getTenKH());
            stmt.setString(2, kh.getSoDienThoai());
            stmt.setInt(3, kh.getDiemTL());
            stmt.setString(4, kh.getLoaiKhachHang() != null ? kh.getLoaiKhachHang().getMaLKH() : "DONG");
            stmt.setString(5, kh.getMaKH());

            int n = stmt.executeUpdate();
            return n > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 6. Xóa khách hàng
    public boolean xoaKhachHang(String maKH) {
        String sql = "DELETE FROM KhachHang WHERE maKH = ?";
        
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, maKH);
            int n = stmt.executeUpdate();
            return n > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}