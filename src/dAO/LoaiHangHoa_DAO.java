package dAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import model.LoaiHangHoa;

public class LoaiHangHoa_DAO {

	//Lấy toàn bộ danh sách để đưa vào combo
    public List<LoaiHangHoa> getAllLoaiHangHoa() {
        List<LoaiHangHoa> ds = new ArrayList<>();
        String sql = "SELECT * FROM LoaiHangHoa";
        
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                LoaiHangHoa lhh = new LoaiHangHoa(
                    rs.getString("maLoaiHang"),
                    rs.getString("tenLoaiHang"),
                    rs.getString("moTa")
                );
                ds.add(lhh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    // 2. Tìm mã loại hàng khi biết tên 
    public String getMaLoaiHangByTen(String tenLoai) {
        String sql = "SELECT maLoaiHang FROM LoaiHangHoa WHERE tenLoaiHang = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, tenLoai);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("maLoaiHang");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 3Tìm đối tượng LoaiHangHoa khi biết mã 
    public LoaiHangHoa getLoaiHangHoaByMa(String ma) {
        String sql = "SELECT * FROM LoaiHangHoa WHERE maLoaiHang = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, ma);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new LoaiHangHoa(
                        rs.getString("maLoaiHang"),
                        rs.getString("tenLoaiHang"),
                        rs.getString("moTa")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}