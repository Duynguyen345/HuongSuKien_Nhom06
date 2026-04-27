package dAO;

import java.sql.*;
import connectDB.ConnectDB;
import model.HangHoa;

public class HangHoa_DAO {
    public HangHoa timTheoMaVach(String maVach) {
        HangHoa sp = null;
        String sql = "SELECT * FROM HANGHOA WHERE maVach = ?";
        
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, maVach);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    sp = new HangHoa(
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
        return sp;
    }
}