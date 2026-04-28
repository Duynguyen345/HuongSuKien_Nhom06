package dAO;

import connectDB.ConnectDB;
import model.NhanVien;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * NHANVIEN_DAO – Data Access Object cho bảng NhanVien trong quanlycuahangtienloi.
 *
 * Schema bảng NhanVien:
 *   maNV        VARCHAR(10)   PK
 *   tenNV       NVARCHAR(100)
 *   diaChi      NVARCHAR(255) NULL
 *   ngayVaoLam  DATE
 *   gioiTinh    BIT           -- 1=Nam, 0=Nữ
 *   sdt         VARCHAR(15)
 *   matKhau     VARCHAR(255)
 *   quanLy      BIT           -- 1=Quản lý, 0=Thu ngân
 */
public class NHANVIEN_DAO {

    private static final String TABLE = "NhanVien";

    // ══════════════════════════════════════════════════════════
    //  1. XÁC THỰC ĐĂNG NHẬP
    // ══════════════════════════════════════════════════════════

    public NhanVien dangNhap(String maNV, String matKhau) throws SQLException {
        String sql = "SELECT maNV, tenNV, quanLy FROM " + TABLE
                   + " WHERE maNV = ? AND matKhau = ?";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maNV.trim());
            ps.setString(2, matKhau);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new NhanVien(
                        rs.getString("maNV"),
                        rs.getString("tenNV"),
                        rs.getBoolean("quanLy")
                    );
                }
            }
        }
        return null;
    }

    // ══════════════════════════════════════════════════════════
    //  2. LẤY TẤT CẢ NHÂN VIÊN
    // ══════════════════════════════════════════════════════════

    public List<NhanVien> layTatCa() throws SQLException {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE + " ORDER BY maNV";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ══════════════════════════════════════════════════════════
    //  3. TÌM KIẾM THEO MÃ HOẶC TÊN
    // ══════════════════════════════════════════════════════════

    public List<NhanVien> timKiem(String keyword) throws SQLException {
        String kw  = "%" + keyword.trim() + "%";
        String sql = "SELECT * FROM " + TABLE
                   + " WHERE maNV LIKE ? OR tenNV LIKE ? ORDER BY maNV";
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
    //  4. TÌM THEO MÃ
    // ══════════════════════════════════════════════════════════

    public NhanVien timTheoMa(String maNV) throws SQLException {
        String sql = "SELECT * FROM " + TABLE + " WHERE maNV = ?";
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
        String sql = "SELECT 1 FROM " + TABLE + " WHERE maNV = ?";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maNV.trim());
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    // ══════════════════════════════════════════════════════════
    //  6. THÊM
    // ══════════════════════════════════════════════════════════

    public boolean them(NhanVien nv) throws SQLException {
        String sql = "INSERT INTO " + TABLE
                   + " (maNV, tenNV, diaChi, ngayVaoLam, gioiTinh, sdt, matKhau, quanLy)"
                   + " VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, nv.getMaNV());
            ps.setString(2, nv.getTenNV());
            ps.setString(3, nv.getDiaChi());
            ps.setObject(4, nv.getNgayVaoLam());
            // gioiTinh BIT: "Nam" → 1, "Nữ" → 0
            ps.setBoolean(5, "Nam".equalsIgnoreCase(nv.getGioiTinh()));
            ps.setString(6, nv.getSdt());
            ps.setString(7, nv.getMatKhau());
            ps.setBoolean(8, nv.isQuanLy());
            return ps.executeUpdate() > 0;
        }
    }

    // ══════════════════════════════════════════════════════════
    //  7. CẬP NHẬT
    // ══════════════════════════════════════════════════════════

    public boolean capNhat(NhanVien nv) throws SQLException {
        String sql = "UPDATE " + TABLE + " SET "
                   + "tenNV=?, diaChi=?, ngayVaoLam=?, gioiTinh=?, sdt=?, matKhau=?, quanLy=? "
                   + "WHERE maNV=?";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, nv.getTenNV());
            ps.setString(2, nv.getDiaChi());
            ps.setObject(3, nv.getNgayVaoLam());
            ps.setBoolean(4, "Nam".equalsIgnoreCase(nv.getGioiTinh()));
            ps.setString(5, nv.getSdt());
            ps.setString(6, nv.getMatKhau());
            ps.setBoolean(7, nv.isQuanLy());
            ps.setString(8, nv.getMaNV());
            return ps.executeUpdate() > 0;
        }
    }

    // ══════════════════════════════════════════════════════════
    //  8. XÓA
    // ══════════════════════════════════════════════════════════

    public boolean xoa(String maNV) throws SQLException {
        String sql = "DELETE FROM " + TABLE + " WHERE maNV = ?";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maNV.trim());
            return ps.executeUpdate() > 0;
        }
    }

    // ══════════════════════════════════════════════════════════
    //  HELPER – ResultSet → NhanVien
    //  gioiTinh BIT: 1 → "Nam", 0 → "Nữ"
    // ══════════════════════════════════════════════════════════

    private NhanVien mapRow(ResultSet rs) throws SQLException {
        Date sqlDate = rs.getDate("ngayVaoLam");
        LocalDate ngay = (sqlDate != null) ? sqlDate.toLocalDate() : null;
        String gioiTinh = rs.getBoolean("gioiTinh") ? "Nam" : "Nữ";

        return new NhanVien(
            rs.getString("maNV"),
            rs.getString("tenNV"),
            rs.getString("diaChi"),
            ngay,
            gioiTinh,
            rs.getString("sdt"),
            rs.getString("matKhau"),
            rs.getBoolean("quanLy")
        );
    }
}
