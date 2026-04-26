package model;

import java.time.LocalDate;

/**
 * Model – Nhân viên.
 * Ánh xạ trực tiếp với bảng NHANVIEN trong SQL Server.
 */
public class NhanVien {

    private String    maNV;
    private String    tenNV;
    private String    diaChi;
    private LocalDate ngayVaoLam;
    private String    gioiTinh;
    private String    sdt;
    private String    matKhau;
    private boolean   quanLy;   // true = Quản lý | false = Nhân viên thu ngân

    // ─── Constructor đầy đủ ───
    public NhanVien(String maNV, String tenNV, String diaChi,
                    LocalDate ngayVaoLam, String gioiTinh,
                    String sdt, String matKhau, boolean quanLy) {
        this.maNV       = maNV;
        this.tenNV      = tenNV;
        this.diaChi     = diaChi;
        this.ngayVaoLam = ngayVaoLam;
        this.gioiTinh   = gioiTinh;
        this.sdt        = sdt;
        this.matKhau    = matKhau;
        this.quanLy     = quanLy;
    }

    // ─── Constructor rút gọn (dùng sau đăng nhập) ───
    public NhanVien(String maNV, String tenNV, boolean quanLy) {
        this.maNV   = maNV;
        this.tenNV  = tenNV;
        this.quanLy = quanLy;
    }

    // ─── Getters ───
    public String    getMaNV()       { return maNV; }
    public String    getTenNV()      { return tenNV; }
    public String    getDiaChi()     { return diaChi; }
    public LocalDate getNgayVaoLam() { return ngayVaoLam; }
    public String    getGioiTinh()   { return gioiTinh; }
    public String    getSdt()        { return sdt; }
    public String    getMatKhau()    { return matKhau; }
    public boolean   isQuanLy()      { return quanLy; }

    // ─── Setters ───
    public void setMaNV(String maNV)             { this.maNV = maNV; }
    public void setTenNV(String tenNV)           { this.tenNV = tenNV; }
    public void setDiaChi(String diaChi)         { this.diaChi = diaChi; }
    public void setNgayVaoLam(LocalDate ngayVaoLam) { this.ngayVaoLam = ngayVaoLam; }
    public void setGioiTinh(String gioiTinh)     { this.gioiTinh = gioiTinh; }
    public void setSdt(String sdt)               { this.sdt = sdt; }
    public void setMatKhau(String matKhau)       { this.matKhau = matKhau; }
    public void setQuanLy(boolean quanLy)        { this.quanLy = quanLy; }

    /** Trả về vai trò hiển thị: "Quản lý" hoặc "Nhân viên thu ngân" */
    public String getVaiTro() {
        return quanLy ? "Quản lý" : "Nhân viên thu ngân";
    }

    @Override
    public String toString() {
        return String.format("[%s] %s – %s", maNV, tenNV, getVaiTro());
    }
}
