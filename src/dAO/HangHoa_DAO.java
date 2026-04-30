package dAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import connectDB.ConnectDB;
import model.HangHoa;
import model.LoaiHangHoa;

public class HangHoa_DAO {


   

//frm bán hàng
    /** Tìm hàng hóa theo mã vạch để quét (Dùng trong FrmBanHang) */
	
    public HangHoa timTheoMaVach(String maVach) {
        String sql = "SELECT * FROM HangHoa hh JOIN LoaiHangHoa lhh ON hh.maLoaiHang = lhh.maLoaiHang WHERE hh.maVach = ? AND hh.conKinhDoanh = 1";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maVach);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LoaiHangHoa loaiHang = new LoaiHangHoa(rs.getString("maLoaiHang"), rs.getString("tenLoaiHang"), rs.getString("moTa"));
                    return new HangHoa(rs.getString("maHH"), rs.getString("maVach"), rs.getString("tenHH"),
                                     rs.getDouble("giaSP"), rs.getString("hinhAnh"), loaiHang, rs.getBoolean("conKinhDoanh"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Lấy toàn bộ hàng hóa để hiện danh sách bên trái (Dùng trong FrmBanHang) */
    public List<HangHoa> layTatCa() {
        return getAllHangHoaForSanPhamPanel(); // Gọi lại hàm bên dưới để tránh viết trùng code
    }

    /** Tìm theo mã HH (Dùng cho double-click trong FrmBanHang) */
    public HangHoa timTheoMaHH(String maHH) {
        String sql = "SELECT * FROM HangHoa hh JOIN LoaiHangHoa lhh ON hh.maLoaiHang = lhh.maLoaiHang WHERE hh.maHH = ? AND hh.conKinhDoanh = 1";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHH);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LoaiHangHoa loaiHang = new LoaiHangHoa(rs.getString("maLoaiHang"), rs.getString("tenLoaiHang"), rs.getString("moTa"));
                    return new HangHoa(rs.getString("maHH"), rs.getString("maVach"), rs.getString("tenHH"),
                                     rs.getDouble("giaSP"), rs.getString("hinhAnh"), loaiHang, rs.getBoolean("conKinhDoanh"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

 
    //  PHẦN DÀNH CHO QUẢN LÝ (HangHoaPanel)
 

  
    //Lấy toàn bộ hàng hóa 
    public List<HangHoa> getAllHangHoaForSanPhamPanel() {
        List<HangHoa> list = new ArrayList<>();
        String sql = "SELECT * FROM HangHoa hh JOIN LoaiHangHoa lhh ON hh.maLoaiHang = lhh.maLoaiHang WHERE hh.conKinhDoanh = 1 ORDER BY hh.tenHH";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LoaiHangHoa loaiHang = new LoaiHangHoa(rs.getString("maLoaiHang"), rs.getString("tenLoaiHang"), rs.getString("moTa"));
                list.add(new HangHoa(rs.getString("maHH"), rs.getString("maVach"), rs.getString("tenHH"),
                                   rs.getDouble("giaSP"), rs.getString("hinhAnh"), loaiHang, rs.getBoolean("conKinhDoanh")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }


    //Lấy tên file ảnh theo mã dùng trong panel
    public String getHinhAnhByMa(String maHH) {
        String sql = "SELECT hinhAnh FROM HangHoa WHERE maHH = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHH);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("hinhAnh");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

   //them mới
    public boolean themHangHoa(HangHoa hh) {
        String sql = "INSERT INTO HangHoa (maHH, maVach, tenHH, hinhAnh, giaSP, maLoaiHang, conKinhDoanh) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hh.getMaHH());
            ps.setString(2, hh.getMaVach());
            ps.setString(3, hh.getTenHH());
            ps.setString(4, hh.getHinhAnh());
            ps.setDouble(5, hh.getGiaSP());
            ps.setString(6, hh.getLoaiHangHoa().getMaLoaiHang());
            ps.setBoolean(7, hh.isConKinhDoanh());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

  //cập nhật hàng hóa
    public boolean capNhatHangHoa(HangHoa hh) {
        String sql = "UPDATE HangHoa SET maVach = ?, tenHH = ?, hinhAnh = ?, giaSP = ?, maLoaiHang = ? WHERE maHH = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hh.getMaVach());
            ps.setString(2, hh.getTenHH());
            ps.setString(3, hh.getHinhAnh());
            ps.setDouble(4, hh.getGiaSP());
            ps.setString(5, hh.getLoaiHangHoa().getMaLoaiHang());
            ps.setString(6, hh.getMaHH());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

   //Xóa
    public boolean xoaHangHoa(String maHH) {
        String sql = "UPDATE HangHoa SET conKinhDoanh = 0 WHERE maHH = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHH);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

//tìm theo tên
    public List<HangHoa> timKiemHangHoa(String tuKhoa) {
        List<HangHoa> list = new ArrayList<>();
        String sql = "SELECT * FROM HangHoa hh JOIN LoaiHangHoa lhh ON hh.maLoaiHang = lhh.maLoaiHang WHERE hh.tenHH COLLATE Vietnamese_CI_AI LIKE ? AND hh.conKinhDoanh = 1";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + tuKhoa + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LoaiHangHoa loaiHang = new LoaiHangHoa(rs.getString("maLoaiHang"), rs.getString("tenLoaiHang"), rs.getString("moTa"));
                    list.add(new HangHoa(rs.getString("maHH"), rs.getString("maVach"), rs.getString("tenHH"),
                                     rs.getDouble("giaSP"), rs.getString("hinhAnh"), loaiHang, rs.getBoolean("conKinhDoanh")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}