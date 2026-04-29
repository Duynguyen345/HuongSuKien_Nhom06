package model;

public class LoaiKhachHang {
    private String maLKH;          // Mã loại khách hàng (VD: DONG, BAC, VANG)
    private String tenLKH;         // Tên loại (VD: Đồng, Bạc, Vàng)
    private int giamGia;           // Phần trăm giảm giá (0 - 100)
    private int mucDiem;           // Mức điểm tối thiểu để đạt loại này

    public LoaiKhachHang() {
    }

    /**
     * Constructor dùng khi tạo mới loại khách hàng từ giao diện.
     * (Không có mã LKH vì có thể dùng Trigger SQL để sinh tự động)
     */
    public LoaiKhachHang(String tenLKH, int giamGia, int mucDiem) {
        setTenLKH(tenLKH);
        setGiamGia(giamGia);
        setMucDiem(mucDiem);
    }
    
    /**
     * Constructor dùng cho DAO lấy dữ liệu từ DB, hoặc dùng khi không cần mức điểm.
     */
    public LoaiKhachHang(String maLKH, String tenLKH, int giamGia) {
        setMaLKH(maLKH);
        setTenLKH(tenLKH);
        setGiamGia(giamGia);
    }
    
    /**
     * Constructor đầy đủ dùng khi load dữ liệu từ SQL lên.
     */
    public LoaiKhachHang(String maLKH, String tenLKH, int giamGia, int mucDiem) {
        setMaLKH(maLKH);
        setTenLKH(tenLKH);
        setGiamGia(giamGia);
        setMucDiem(mucDiem);
    }
    
    // --- GETTER & SETTER ---

    public String getMaLKH() {
        return maLKH;
    }

    public void setMaLKH(String maLKH) {
        // Đã sửa: Cho phép nhập chuỗi bất kỳ (DONG, BAC, VANG...) nhưng không quá 10 ký tự (theo VARCHAR(10))
        if(maLKH == null || maLKH.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã loại khách hàng không được để trống.");
        }
        if(maLKH.length() > 10) {
            throw new IllegalArgumentException("Mã loại khách hàng không được vượt quá 10 ký tự.");
        }
        this.maLKH = maLKH.trim();
    }

    public String getTenLKH() {
        return tenLKH;
    }

    public void setTenLKH(String tenLKH) {
        if (tenLKH == null || tenLKH.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên loại khách hàng không được để trống.");
        }
        this.tenLKH = tenLKH.trim();
    }

    public int getGiamGia() {
        return giamGia;
    }

    public void setGiamGia(int giamGia) {
        // Giảm giá phải từ 0% đến 100%
        if (giamGia < 0 || giamGia > 100) {
            throw new IllegalArgumentException("Phần trăm giảm giá phải nằm trong khoảng từ 0 đến 100.");
        }
        this.giamGia = giamGia;
    }

    public int getMucDiem() {
        return mucDiem;
    }

    public void setMucDiem(int mucDiem) {
        // Mức điểm tối thiểu để đạt hạng thẻ không được âm
        if (mucDiem < 0) {
            throw new IllegalArgumentException("Mức điểm phân hạng không được nhỏ hơn 0.");
        }
        this.mucDiem = mucDiem;
    }

    @Override
    public String toString() {
        return "LoaiKhachHang [maLKH=" + maLKH + ", tenLKH=" + tenLKH + ", giamGia=" + giamGia + "%, mucDiem=" + mucDiem + "]";
    }
}