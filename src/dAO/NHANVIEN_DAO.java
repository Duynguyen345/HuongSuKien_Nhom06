package dAO;

import connectDB.ConnectDB;
import model.NhanVien;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * NHANVIEN_DAO – Data Access Object cho bảng NhanVien.
 *
 * Cấu trúc bảng (quanlycuahangtienloi):
 *   maNV       VARCHAR(10)    PK
 *   tenNV      NVARCHAR(100)
 *   diaChi     NVARCHAR(255)
 *   ngayVaoLam DATE
 *   gioiTinh   BIT            1 = Nam, 0 = Nữ
 *   sdt        VARCHAR(15)
 *   matKhau    VARCHAR(255)
 *   quanLy     BIT            1 = Quản lý, 0 = Thu ngân
 */
public class NHANVIEN_DAO {

    // ─── Tên bảng & cột ───────────────────────────────────────────
    private static final String TABLE        = "NhanVien";
    private static final String COL_MA       = "maNV";
    private static final String COL_TEN      = "tenNV";
    private static final String COL_DIA_CHI  = "diaChi";
    private static final String COL_NGAY     = "ngayVaoLam";
    private static final String COL_GIOI     = "gioiTinh";   // BIT: 1=Nam, 0=Nữ
    private static final String COL_SDT      = "sdt";
    private static final String COL_PASS     = "matKhau";
    private static final String COL_QUAN_LY  = "quanLy";
    // ────────────────────────────────────────────────────────────────

    // ══════════════════════════════════════════════════════════
    //  1. XÁC THỰC ĐĂNG NHẬP
    // ══════════════════════════════════════════════════════════

    /**
     * Xác thực đăng nhập – trả về NhanVien nếu đúng, null nếu sai.
     */
    public NhanVien dangNhap(String maNV, String matKhau) throws SQLException {
        String sql = "SELECT " + COL_MA + ", " + COL_TEN + ", " + COL_QUAN_LY
                   + " FROM " + TABLE
                   + " WHERE " + COL_MA   + " = ?"
                   + "   AND " + COL_PASS + " = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maNV.trim());
            ps.setString(2, matKhau);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new NhanVien(
                        rs.getString(COL_MA),
                        rs.getString(COL_TEN),
                        rs.getBoolean(COL_QUAN_LY)
                    );
                }
            }
        }
        return null;
    }

    // ══════════════════════════════════════════════════════════
    //  2. LẤY TẤT CẢ NHÂN VIÊN
    // ══════════════════════════════════════════════════════════

    /**
     * Trả về danh sách toàn bộ nhân viên.
     */
    public List<NhanVien> layTatCa() throws SQLException {
        String sql = "SELECT * FROM " + TABLE + " ORDER BY " + COL_MA;
        List<NhanVien> list = new ArrayList<>();

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ══════════════════════════════════════════════════════════
    //  3. TÌM THEO MÃ HOẶC TÊN (tìm kiếm mờ)
    // ══════════════════════════════════════════════════════════

    /**
     * Tìm nhân viên theo mã hoặc tên (LIKE). Truyền chuỗi rỗng → lấy tất cả.
     */
    public List<NhanVien> timKiem(String keyword) throws SQLException {
        String kw = "%" + keyword.trim() + "%";
        String sql = "SELECT * FROM " + TABLE
                   + " WHERE " + COL_MA  + " LIKE ?"
                   + "    OR " + COL_TEN + " LIKE ?"
                   + " ORDER BY " + COL_MA;
        List<NhanVien> list = new ArrayList<>();

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, kw);
            ps.setString(2, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ══════════════════════════════════════════════════════════
    //  4. LẤY THÔNG TIN NHÂN VIÊN THEO MÃ
    // ══════════════════════════════════════════════════════════

    public NhanVien timTheoMa(String maNV) throws SQLException {
        String sql = "SELECT * FROM " + TABLE + " WHERE " + COL_MA + " = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maNV.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ══════════════════════════════════════════════════════════
    //  5. KIỂM TRA TRÙNG MÃ
    // ══════════════════════════════════════════════════════════

    public boolean tonTai(String maNV) throws SQLException {
        String sql = "SELECT 1 FROM " + TABLE + " WHERE " + COL_MA + " = ?";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maNV.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ══════════════════════════════════════════════════════════
    //  6. THÊM NHÂN VIÊN MỚI
    // ══════════════════════════════════════════════════════════

    /**
     * @return true nếu thêm thành công
     */
    public boolean them(NhanVien nv) throws SQLException {
        String sql = "INSERT INTO " + TABLE
                   + " (" + COL_MA + "," + COL_TEN + "," + COL_DIA_CHI + ","
                   +        COL_NGAY + "," + COL_GIOI + "," + COL_SDT + ","
                   +        COL_PASS + "," + COL_QUAN_LY + ")"
                   + " VALUES (?,?,?,?,?,?,?,?)";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, nv.getMaNV());
            ps.setString(2, nv.getTenNV());
            ps.setString(3, nv.getDiaChi());
            ps.setObject(4, nv.getNgayVaoLam()); // LocalDate → DATE
            // gioiTinh: BIT → true (1=Nam), false (0=Nữ)
            ps.setBoolean(5, "Nam".equals(nv.getGioiTinh()));
            ps.setString(6, nv.getSdt());
            ps.setString(7, nv.getMatKhau());
            ps.setBoolean(8, nv.isQuanLy());
            return ps.executeUpdate() > 0;
        }
    }

    // ══════════════════════════════════════════════════════════
    //  7. CẬP NHẬT THÔNG TIN
    // ══════════════════════════════════════════════════════════

    /**
     * @return true nếu cập nhật thành công
     */
    public boolean capNhat(NhanVien nv) throws SQLException {
        String sql = "UPDATE " + TABLE + " SET "
                   + COL_TEN     + " = ?, "
                   + COL_DIA_CHI + " = ?, "
                   + COL_NGAY    + " = ?, "
                   + COL_GIOI    + " = ?, "
                   + COL_SDT     + " = ?, "
                   + COL_PASS    + " = ?, "
                   + COL_QUAN_LY + " = ? "
                   + "WHERE " + COL_MA + " = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, nv.getTenNV());
            ps.setString(2, nv.getDiaChi());
            ps.setObject(3, nv.getNgayVaoLam());
            ps.setBoolean(4, "Nam".equals(nv.getGioiTinh()));
            ps.setString(5, nv.getSdt());
            ps.setString(6, nv.getMatKhau());
            ps.setBoolean(7, nv.isQuanLy());
            ps.setString(8, nv.getMaNV());
            return ps.executeUpdate() > 0;
        }
    }

    // ══════════════════════════════════════════════════════════
    //  8. XOÁ NHÂN VIÊN
    // ══════════════════════════════════════════════════════════

    /**
     * @return true nếu xoá thành công
     */
    public boolean xoa(String maNV) throws SQLException {
        String sql = "DELETE FROM " + TABLE + " WHERE " + COL_MA + " = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maNV.trim());
            return ps.executeUpdate() > 0;
        }
    }

    // ══════════════════════════════════════════════════════════
    //  9. HELPER – ánh xạ ResultSet → NhanVien
    // ══════════════════════════════════════════════════════════

    private NhanVien mapRow(ResultSet rs) throws SQLException {
        Date sqlDate = rs.getDate(COL_NGAY);
        LocalDate ngay = (sqlDate != null) ? sqlDate.toLocalDate() : null;

        // gioiTinh là BIT: true=Nam, false=Nữ
        boolean gioiBit = rs.getBoolean(COL_GIOI);
        String gioiStr  = gioiBit ? "Nam" : "Nữ";

        return new NhanVien(
            rs.getString(COL_MA),
            rs.getString(COL_TEN),
            rs.getString(COL_DIA_CHI),
            ngay,
            gioiStr,
            rs.getString(COL_SDT),
            rs.getString(COL_PASS),
            rs.getBoolean(COL_QUAN_LY)
        );
    }
}
