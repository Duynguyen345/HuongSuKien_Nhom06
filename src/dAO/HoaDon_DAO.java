package dAO;

import connectDB.ConnectDB;
import model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * HoaDon_DAO – Thao tác với HoaDonBanHang và ChiTietHoaDon trong quanlycuahangtienloi.
 *
 * Schema HoaDonBanHang:
 *   maHDBH, ngayLapHDBH, maNV, maKH, maGiam,
 *   hinhThucThanhToan, tongTienGoc, tienGiamThanhVien,
 *   tienGiamVoucher, tongTienThanhToan, diemTichLuy
 *
 * Schema ChiTietHoaDon:
 *   maHDBH, maLo, soLuong, donGia, thanhTien
 */
public class HoaDon_DAO {

    // ══════════════════════════════════════════════════════════
    //  1. SINH MÃ HÓA ĐƠN TỰ ĐỘNG  (HD + timestamp)
    // ══════════════════════════════════════════════════════════
    public String sinhMaHD() {
        return "HD" + System.currentTimeMillis() % 100000000L;
    }

    // ══════════════════════════════════════════════════════════
    //  2. LƯU HÓA ĐƠN + CHI TIẾT (transaction)
    // ══════════════════════════════════════════════════════════
    /**
     * @param maHD            mã hóa đơn
     * @param maNV            mã nhân viên
     * @param maKH            mã khách hàng (null nếu vãng lai)
     * @param hinhThuc        "Tiền mặt" | "Chuyển khoản"
     * @param tongTienGoc     tổng trước giảm
     * @param tienGiam        tiền giảm thành viên
     * @param tongThanhToan   thực trả
     * @param chiTiet         danh sách [{maHH, soLuong, donGia, thanhTien}]
     */
    public boolean luuHoaDon(String maHD, String maNV, String maKH,
                             String hinhThuc,
                             double tongTienGoc, double tienGiam, double tongThanhToan,
                             List<Object[]> chiTiet) throws SQLException {

        Connection con = ConnectDB.getConnection();
        con.setAutoCommit(false);
        try {
            // --- Header ---
            String sqlHD = "INSERT INTO HoaDonBanHang "
                    + "(maHDBH, ngayLapHDBH, maNV, maKH, hinhThucThanhToan, "
                    + " tongTienGoc, tienGiamThanhVien, tienGiamVoucher, tongTienThanhToan, diemTichLuy) "
                    + "VALUES (?,GETDATE(),?,?,?,?,?,0,?,0)";
            try (PreparedStatement ps = con.prepareStatement(sqlHD)) {
                ps.setString(1, maHD);
                ps.setString(2, maNV);
                if (maKH == null) ps.setNull(3, Types.VARCHAR); else ps.setString(3, maKH);
                ps.setString(4, hinhThuc);
                ps.setDouble(5, tongTienGoc);
                ps.setDouble(6, tienGiam);
                ps.setDouble(7, tongThanhToan);
                ps.executeUpdate();
            }

            // --- Chi tiết: lấy maLo hợp lệ từ LoHang theo maHH ---
            String sqlCT = "INSERT INTO ChiTietHoaDon (maHDBH, maLo, soLuong, donGia, thanhTien) "
                         + "VALUES (?,?,?,?,?)";
            try (PreparedStatement ps = con.prepareStatement(sqlCT)) {
                for (Object[] row : chiTiet) {
                    String maHH  = row[0].toString();
                    String maLo  = layMaLoTheoMaHH(con, maHH);
                    if (maLo == null) {
                        throw new SQLException("Sản phẩm '" + maHH + "' chưa có lô hàng trong hệ thống.");
                    }
                    ps.setString(1, maHD);
                    ps.setString(2, maLo);
                    ps.setInt   (3, ((Number) row[1]).intValue());
                    ps.setDouble(4, ((Number) row[2]).doubleValue());
                    ps.setDouble(5, ((Number) row[3]).doubleValue());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    // ══════════════════════════════════════════════════════════
    //  3. LẤY DANH SÁCH HÓA ĐƠN (có lọc)
    //     Truyền null / "" để bỏ qua điều kiện đó
    // ══════════════════════════════════════════════════════════
    public List<Object[]> layDanhSach(String tuKhoa, String hinhThuc,
                                      String tuNgay, String denNgay,
                                      double minTien, double maxTien) throws SQLException {
        List<Object[]> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT h.maHDBH, h.ngayLapHDBH, h.maNV, k.soDienThoai, "
          + "       h.hinhThucThanhToan, h.tongTienGoc, h.tienGiamThanhVien, h.tongTienThanhToan "
          + "FROM HoaDonBanHang h "
          + "LEFT JOIN KhachHang k ON h.maKH = k.maKH WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();

        if (tuKhoa != null && !tuKhoa.isBlank()) {
            sql.append(" AND (h.maHDBH LIKE ? OR k.soDienThoai LIKE ?)");
            String kw = "%" + tuKhoa + "%";
            params.add(kw); params.add(kw);
        }
        if (hinhThuc != null && !hinhThuc.isBlank() && !hinhThuc.equals("Tất cả")) {
            sql.append(" AND h.hinhThucThanhToan = ?");
            params.add(hinhThuc);
        }
        if (tuNgay != null && !tuNgay.isBlank()) {
            sql.append(" AND CAST(h.ngayLapHDBH AS DATE) >= ?");
            params.add(tuNgay);
        }
        if (denNgay != null && !denNgay.isBlank()) {
            sql.append(" AND CAST(h.ngayLapHDBH AS DATE) <= ?");
            params.add(denNgay);
        }
        if (minTien > 0) {
            sql.append(" AND h.tongTienThanhToan >= ?");
            params.add(minTien);
        }
        if (maxTien > 0) {
            sql.append(" AND h.tongTienThanhToan <= ?");
            params.add(maxTien);
        }
        sql.append(" ORDER BY h.ngayLapHDBH DESC");

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++)
                ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getString("maHDBH"),
                        rs.getTimestamp("ngayLapHDBH") != null
                            ? rs.getTimestamp("ngayLapHDBH").toLocalDateTime() : null,
                        rs.getString("maNV"),
                        rs.getString("soDienThoai") != null ? rs.getString("soDienThoai") : "Vãng lai",
                        rs.getString("hinhThucThanhToan"),
                        rs.getDouble("tongTienGoc"),
                        rs.getDouble("tienGiamThanhVien"),
                        rs.getDouble("tongTienThanhToan")
                    });
                }
            }
        }
        return list;
    }

    // ══════════════════════════════════════════════════════════
    //  4. LẤY CHI TIẾT HÓA ĐƠN
    // ══════════════════════════════════════════════════════════
    public List<Object[]> layChiTiet(String maHD) throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT c.maLo, h.tenHH, c.soLuong, c.donGia, c.thanhTien "
                   + "FROM ChiTietHoaDon c "
                   + "LEFT JOIN HangHoa h ON c.maLo = h.maHH "
                   + "WHERE c.maHDBH = ?";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getString("maLo"),
                        rs.getString("tenHH") != null ? rs.getString("tenHH") : rs.getString("maLo"),
                        rs.getInt("soLuong"),
                        rs.getDouble("donGia"),
                        rs.getDouble("thanhTien")
                    });
                }
            }
        }
        return list;
    }

    // ══════════════════════════════════════════════════════════
    //  5. LẤY THÔNG TIN HEADER HÓA ĐƠN (để in lại)
    // ══════════════════════════════════════════════════════════
    public Object[] layHeaderHD(String maHD) throws SQLException {
        String sql = "SELECT h.*, k.tenKH, k.soDienThoai "
                   + "FROM HoaDonBanHang h "
                   + "LEFT JOIN KhachHang k ON h.maKH = k.maKH "
                   + "WHERE h.maHDBH = ?";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                        rs.getString("maHDBH"),
                        rs.getTimestamp("ngayLapHDBH"),
                        rs.getString("maNV"),
                        rs.getString("tenKH"),
                        rs.getString("soDienThoai"),
                        rs.getString("hinhThucThanhToan"),
                        rs.getDouble("tongTienGoc"),
                        rs.getDouble("tienGiamThanhVien"),
                        rs.getDouble("tongTienThanhToan")
                    };
                }
            }
        }
        return null;
    }

    // ══════════════════════════════════════════════════════════
    //  6. HELPER – lấy maLo từ LoHang theo maHH (dùng lô có tồn kho > 0)
    // ══════════════════════════════════════════════════════════
    private String layMaLoTheoMaHH(Connection con, String maHH) throws SQLException {
        String sql = "SELECT TOP 1 maLo FROM LoHang WHERE maHH = ? AND soLuongTon > 0 ORDER BY hanSuDung ASC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHH);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("maLo");
            }
        }
        return null;
    }
}

