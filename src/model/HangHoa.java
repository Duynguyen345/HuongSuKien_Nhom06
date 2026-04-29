package model;

public class HangHoa {
    private String maHH;
    private String maVach;
    private String tenHH;
    private double giaSP;
    private String hinhAnh;
    private LoaiHangHoa loaiHangHoa;
    private boolean conKinhDoanh;
    
    public HangHoa() {
        // Constructor rỗng
    }

    public HangHoa(String maHH, String maVach, String tenHH, double giaSP, String hinhAnh, LoaiHangHoa loaiHangHoa, boolean conKinhDoanh) {
        // Thay vì dùng this.maHH = maHH, ta gọi hàm set để nó kiểm tra lỗi luôn
        setMaHH(maHH);
        setMaVach(maVach);
        setTenHH(tenHH);
        setGiaSP(giaSP);
        setHinhAnh(hinhAnh);
        setLoaiHangHoa(loaiHangHoa);
        setConKinhDoanh(conKinhDoanh);
    }

    public String getMaHH() {
        return maHH;
    }

    public void setMaHH(String maHH) {
        if (maHH == null || maHH.trim().isEmpty() || maHH.length() > 15) {
            throw new IllegalArgumentException("Mã hàng hóa không hợp lệ (tối đa 15 ký tự).");
        }
        this.maHH = maHH;
    }

    public String getMaVach() {
        return maVach;
    }

    public void setMaVach(String maVach) {
        if (maVach == null || maVach.trim().isEmpty() || maVach.length() > 30) {
            throw new IllegalArgumentException("Mã vạch không hợp lệ (tối đa 30 ký tự).");
        }
        this.maVach = maVach;
    }

    public String getTenHH() {
        return tenHH;
    }

    public void setTenHH(String tenHH) {
        if (tenHH == null || tenHH.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên hàng hóa không được để trống.");
        }
        this.tenHH = tenHH;
    }

    public double getGiaSP() {
        return giaSP;
    }

    public void setGiaSP(double giaSP) {
        if (giaSP <= 0) {
            throw new IllegalArgumentException("Giá sản phẩm phải lớn hơn 0.");
        }
        this.giaSP = giaSP;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public LoaiHangHoa getLoaiHangHoa() {
        return loaiHangHoa;
    }

    public void setLoaiHangHoa(LoaiHangHoa loaiHangHoa) {
        if (loaiHangHoa == null) {
            throw new IllegalArgumentException("Loại hàng hóa không được để trống.");
        }
        this.loaiHangHoa = loaiHangHoa;
    }

    public boolean isConKinhDoanh() {
        return conKinhDoanh;
    }

    public void setConKinhDoanh(boolean conKinhDoanh) {
        this.conKinhDoanh = conKinhDoanh;
    }
    
  
    //Hàm hỗ trợ thêm để dễ nhúng vào Giao Diện
    public String getTenLoaiHH() {
        return loaiHangHoa != null ? loaiHangHoa.getTenLoaiHang() : "N/A";
    }

    @Override
    public String toString() {
        return "HangHoa [maHH=" + maHH + ", maVach=" + maVach + ", tenHH=" + tenHH + ", giaSP=" + giaSP + "]";
    }
}