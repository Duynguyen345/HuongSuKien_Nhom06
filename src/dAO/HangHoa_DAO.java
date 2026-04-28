package dAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import connectDB.ConnectDB;
import model.HangHoa;

public class HangHoa_DAO {

    /** Tìm hàng hóa theo mã vạch để quét bán hàng */
    public HangHoa timTheoMaVach(String maVach) {
        String sql = "SELECT maHH, maVach, tenHH, giaSP FROM HangHoa WHERE maVach = ? AND conKinhDoanh = 1";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maVach);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new HangHoa(
                        rs.getString("maHH"),
                        rs.getString("maVach"),
                        rs.getString("tenHH"),
                        rs.getDouble("giaSP")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Lấy toàn bộ hàng hóa đang kinh doanh */
    public List<HangHoa> layTatCa() {
        List<HangHoa> list = new ArrayList<>();
        String sql = "SELECT maHH, maVach, tenHH, giaSP FROM HangHoa WHERE conKinhDoanh = 1 ORDER BY tenHH";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new HangHoa(
                    rs.getString("maHH"),
                    rs.getString("maVach"),
                    rs.getString("tenHH"),
                    rs.getDouble("giaSP")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Tìm hàng hóa theo maHH (dùng cho double-click từ bảng danh sách) */
    public HangHoa timTheoMaHH(String maHH) {
        String sql = "SELECT maHH, maVach, tenHH, giaSP FROM HangHoa WHERE maHH = ? AND conKinhDoanh = 1";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maHH);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new HangHoa(
                    rs.getString("maHH"), rs.getString("maVach"),
                    rs.getString("tenHH"), rs.getDouble("giaSP"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}