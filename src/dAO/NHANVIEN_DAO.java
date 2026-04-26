package dAO;

import connectDB.ConnectDB;
import model.NhanVien;

import java.sql.*;
import java.time.LocalDate;

/**
 * NHANVIEN_DAO – Data Access Object cho bảng NHANVIEN.
 *
 * Cấu trúc bảng:
 *   maNV       NVARCHAR(20)  PK   – Mã nhân viên (dùng để đăng nhập)
 *   tenNV      NVARCHAR(100)      – Tên nhân viên
 *   diaChi     NVARCHAR(255)      – Địa chỉ
 *   ngayVaoLam DATE               – Ngày vào làm
 *   gioiTinh   NVARCHAR(10)       – Giới tính
 *   sdt        NVARCHAR(15)       – Số điện thoại
 *   matKhau    NVARCHAR(255)      – Mật khẩu
 *   quanLy     BIT                – 1 = Quản lý | 0 = Nhân viên thu ngân
 */
public class NHANVIEN_DAO {

    // ─── Tên bảng & cột ───────────────────────────────────────
    private static final String TABLE        = "NHANVIEN";
    private static final String COL_MA       = "maNV";
    private static final String COL_TEN      = "tenNV";
    private static final String COL_DIA_CHI  = "diaChi";
    private static final String COL_NGAY     = "ngayVaoLam";
    private static final String COL_GIOI     = "gioiTinh";
    private static final String COL_SDT      = "sdt";
    private static final String COL_PASS     = "matKhau";
    private static final String COL_QUAN_LY  = "quanLy";
    // ──────────────────────────────────────────────────────────

    // ══════════════════════════════════════════════════════════
    //  1. XÁC THỰC ĐĂNG NHẬP
    // ══════════════════════════════════════════════════════════

    /**
     * Xác thực đăng nhập – trả về NhanVien nếu đúng, null nếu sai.
     *
     * @param maNV    Mã nhân viên nhập từ form
     * @param matKhau Mật khẩu nhập từ form
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
    //  2. LẤY THÔNG TIN NHÂN VIÊN ĐẦY ĐỦ
    // ══════════════════════════════════════════════════════════

    /**
     * Lấy toàn bộ thông tin nhân viên theo mã.
     */
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
    //  3. THÊM NHÂN VIÊN MỚI
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
            ps.setString(5, nv.getGioiTinh());
            ps.setString(6, nv.getSdt());
            ps.setString(7, nv.getMatKhau());
            ps.setBoolean(8, nv.isQuanLy());
            return ps.executeUpdate() > 0;
        }
    }

    // ══════════════════════════════════════════════════════════
    //  4. CẬP NHẬT THÔNG TIN
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
            ps.setString(4, nv.getGioiTinh());
            ps.setString(5, nv.getSdt());
            ps.setString(6, nv.getMatKhau());
            ps.setBoolean(7, nv.isQuanLy());
            ps.setString(8, nv.getMaNV());
            return ps.executeUpdate() > 0;
        }
    }

    // ══════════════════════════════════════════════════════════
    //  5. XOÁ NHÂN VIÊN
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
    //  6. HELPER – ánh xạ ResultSet → NhanVien
    // ══════════════════════════════════════════════════════════

    private NhanVien mapRow(ResultSet rs) throws SQLException {
        Date sqlDate = rs.getDate(COL_NGAY);
        LocalDate ngay = (sqlDate != null) ? sqlDate.toLocalDate() : null;

        return new NhanVien(
            rs.getString(COL_MA),
            rs.getString(COL_TEN),
            rs.getString(COL_DIA_CHI),
            ngay,
            rs.getString(COL_GIOI),
            rs.getString(COL_SDT),
            rs.getString(COL_PASS),
            rs.getBoolean(COL_QUAN_LY)
        );
    }
}
