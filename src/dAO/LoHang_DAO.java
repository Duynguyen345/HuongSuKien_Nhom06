package dAO;

import connectDB.ConnectDB;
import model.LoHang;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class LoHang_DAO {

    public ArrayList<LoHang> getAllLoHang() {
        ArrayList<LoHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM LoHang ORDER BY ngayNhap DESC";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LoHang lh = new LoHang(
                    rs.getString("maLo"),
                    rs.getString("maHH"),
                    rs.getDate("ngayNhap").toLocalDate(),
                    rs.getDate("hanSuDung").toLocalDate(),
                    rs.getInt("soLuongNhap"),
                    rs.getInt("soLuongTon")
                );
                ds.add(lh);
            }
        } catch (SQLException e) {
            System.err.println("Loi getAllLoHang: " + e.getMessage());
        }
        return ds;
    }

    public boolean themLoHang(LoHang lh) {
        String sql = "INSERT INTO LoHang(maLo,maHH,ngayNhap,hanSuDung,soLuongNhap,soLuongTon) VALUES(?,?,?,?,?,?)";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, lh.getMaLo());
            ps.setString(2, lh.getMaHH());
            ps.setDate(3, Date.valueOf(lh.getNgayNhap()));
            ps.setDate(4, Date.valueOf(lh.getHanSuDung()));
            ps.setInt(5, lh.getSoLuongNhap());
            ps.setInt(6, lh.getSoLuongTon());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Loi themLoHang: " + e.getMessage());
            return false;
        }
    }

    public boolean xoaLoHang(String maLo) {
        String sql = "DELETE FROM LoHang WHERE maLo=?";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maLo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Loi xoaLoHang: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<LoHang> getLoHangHetHan() {
        ArrayList<LoHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM LoHang WHERE hanSuDung < CAST(GETDATE() AS DATE)";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ds.add(new LoHang(
                    rs.getString("maLo"), rs.getString("maHH"),
                    rs.getDate("ngayNhap").toLocalDate(),
                    rs.getDate("hanSuDung").toLocalDate(),
                    rs.getInt("soLuongNhap"), rs.getInt("soLuongTon")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Loi getLoHangHetHan: " + e.getMessage());
        }
        return ds;
        
    }
    public boolean suaLoHang(LoHang lh) {
        String sql = "UPDATE LoHang SET maHH=?, ngayNhap=?, hanSuDung=?, soLuongNhap=?, soLuongTon=? WHERE maLo=?";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, lh.getMaHH());
            ps.setDate(2, Date.valueOf(lh.getNgayNhap()));
            ps.setDate(3, Date.valueOf(lh.getHanSuDung()));
            ps.setInt(4, lh.getSoLuongNhap());
            ps.setInt(5, lh.getSoLuongTon());
            ps.setString(6, lh.getMaLo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public ArrayList<LoHang> getLoHangSapHetHan(int soNgay) {
        ArrayList<LoHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM LoHang WHERE hanSuDung >= CAST(GETDATE() AS DATE) " +
                     "AND hanSuDung <= DATEADD(DAY, ?, CAST(GETDATE() AS DATE))";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, soNgay);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                ds.add(new LoHang(rs.getString("maLo"), rs.getString("maHH"),
                    rs.getDate("ngayNhap").toLocalDate(), rs.getDate("hanSuDung").toLocalDate(),
                    rs.getInt("soLuongNhap"), rs.getInt("soLuongTon")));
        } catch (SQLException e) { e.printStackTrace(); }
        return ds;
    }
}