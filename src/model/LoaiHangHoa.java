package model;

public class LoaiHangHoa {
	private String maLoaiHang;
	private String tenLoaiHang;
	private String moTa;
	
	public LoaiHangHoa() {
		// Constructor rỗng
	}

	public LoaiHangHoa(String maLoaiHang, String tenLoaiHang, String moTa) {
        // Dùng hàm set để bắt lỗi ngay từ lúc tạo đối tượng
		setMaLoaiHang(maLoaiHang);
		setTenLoaiHang(tenLoaiHang);
		setMoTa(moTa);
	}

	public String getMaLoaiHang() {
		return maLoaiHang;
	}

	public void setMaLoaiHang(String maLoaiHang) {
        if (maLoaiHang == null || maLoaiHang.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã loại hàng không được để trống.");
        }
        if (maLoaiHang.length() > 10) {
            throw new IllegalArgumentException("Mã loại hàng không được vượt quá 10 ký tự.");
        }
		this.maLoaiHang = maLoaiHang;
	}

	public String getTenLoaiHang() {
		return tenLoaiHang;
	}

	public void setTenLoaiHang(String tenLoaiHang) {
        if (tenLoaiHang == null || tenLoaiHang.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên loại hàng không được để trống.");
        }
		this.tenLoaiHang = tenLoaiHang;
	}

	public String getMoTa() {
		return moTa;
	}

	public void setMoTa(String moTa) {
		this.moTa = moTa; // Mô tả có thể để trống (NULL) theo DB nên không cần bắt lỗi
	}
	
	@Override
	public String toString() {
		// Rất tốt! Dùng cho JComboBox hiển thị tên
		return tenLoaiHang;
	}
}