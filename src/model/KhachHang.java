package model;

public class KhachHang {

    private String maKH; 
    private String tenKH;
    private String soDienThoai;
    private int diemTL = 0;
    private LoaiKhachHang loaiKhachHang;

    // Constructor mặc định
    public KhachHang() {
    }

    /**
     * Constructor dùng khi nhân viên tạo mới khách hàng (chưa có mã KH, DB tự sinh/Trigger)
     */
    public KhachHang(String tenKH, String soDienThoai, int diemTL, LoaiKhachHang loaiKhachHang) {
        setTenKH(tenKH);
        setSoDienThoai(soDienThoai);
        setDiemTL(diemTL);
        setLoaiKhachHang(loaiKhachHang);
    }
    
    /**
     * Constructor lấy đầy đủ dữ liệu từ SQL lên
     */
    public KhachHang(String maKH, String tenKH, String soDienThoai, int diemTL, LoaiKhachHang loaiKhachHang) {
        setMaKH(maKH);
        setTenKH(tenKH);
        setSoDienThoai(soDienThoai);
        setDiemTL(diemTL);
        setLoaiKhachHang(loaiKhachHang);
    }
    
    /**
     * Constructor dùng cho phương thức sửa khách hàng (không đổi loại KH)
     */
    public KhachHang(String maKH, String tenKH, String soDienThoai, int diemTL) {
        setMaKH(maKH);
        setTenKH(tenKH);
        setSoDienThoai(soDienThoai);
        setDiemTL(diemTL);
    }

    /**
     * Constructor tạo khách hàng mới nhanh tại quầy thu ngân (chỉ cần Tên & SĐT)
     */
    public KhachHang(String tenKH, String soDienThoai, int diemTL) {
        setTenKH(tenKH);
        setSoDienThoai(soDienThoai);
        setDiemTL(diemTL);
    }

    

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        if (maKH == null || !maKH.matches("^KH\\d+$") || maKH.length() > 10) {
            throw new IllegalArgumentException("Mã khách hàng sai định dạng (phải có dạng KHxxx và tối đa 10 ký tự).");
        }
        this.maKH = maKH;
    }
    
    public String getTenKH() {
        return tenKH;
    }

    public void setTenKH(String tenKH) {
        if (tenKH == null || tenKH.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khách hàng không được để trống.");
        }
        this.tenKH = tenKH.trim();
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || !soDienThoai.matches("^0\\d{9}$")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ (phải bắt đầu bằng số 0 và đủ 10 chữ số).");
        }
        this.soDienThoai = soDienThoai;
    }

    public int getDiemTL() {
        return diemTL;
    }

    public void setDiemTL(int diemTL) {
        if (diemTL < 0) {
            throw new IllegalArgumentException("Điểm tích lũy phải lớn hơn hoặc bằng 0.");
        }
        this.diemTL = diemTL; 
    }

    public LoaiKhachHang getLoaiKhachHang() {
        return loaiKhachHang;
    }

    public void setLoaiKhachHang(LoaiKhachHang loaiKhachHang) {
        this.loaiKhachHang = loaiKhachHang;
    }

    // --- CÁC HÀM NGHIỆP VỤ CHO CỬA HÀNG TIỆN LỢI ---

    public void tichDiem(int diemMoi) {
        if (diemMoi > 0) {
            this.diemTL += diemMoi;
        }
    }

    public void truDiem(int diemSuDung) {
        if (diemSuDung <= 0) {
            throw new IllegalArgumentException("Số điểm trừ phải lớn hơn 0.");
        }
        if (this.diemTL >= diemSuDung) {
            this.diemTL -= diemSuDung;
        } else {
            throw new IllegalArgumentException("Khách hàng không đủ điểm tích lũy để sử dụng.");
        }
    }

 // tự động phân thẻ thành viên
    public void tuDongPhanHang() {
        if (this.diemTL >= 5000) {
            this.loaiKhachHang = new LoaiKhachHang("VANG", "Vàng", 10, 5000);
        } else if (this.diemTL >= 1000) {
            this.loaiKhachHang = new LoaiKhachHang("BAC", "Bạc", 5, 1000);
        } else {
            this.loaiKhachHang = new LoaiKhachHang("DONG", "Đồng", 0, 0);
        }
    }
    // =========================================================

    @Override
    public String toString() {
        return "KhachHang [maKH=" + maKH + ", tenKH=" + tenKH + ", soDienThoai=" + soDienThoai
                + ", diemTL=" + diemTL + ", loaiKhachHang=" + (loaiKhachHang != null ? loaiKhachHang.getTenLKH() : "Null") + "]";
    }
}