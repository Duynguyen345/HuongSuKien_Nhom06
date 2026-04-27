package model;

public class LoaiKhachHang {
	private String maLKH;
	private String tenLKH;
	private int giamGia;
	
	public LoaiKhachHang() {
		// TODO Auto-generated constructor stub
	}

	public LoaiKhachHang(String maLKH, String tenLKH, int giamGia) {
		super();
		this.maLKH = maLKH;
		this.tenLKH = tenLKH;
		this.giamGia = giamGia;
	}

	public String getMaLKH() {
		return maLKH;
	}

	public void setMaLKH(String maLKH) {
		this.maLKH = maLKH;
	}

	public String getTenLKH() {
		return tenLKH;
	}

	public void setTenLKH(String tenLKH) {
		this.tenLKH = tenLKH;
	}

	public int getGiamGia() {
		return giamGia;
	}

	public void setGiamGia(int giamGia) {
		this.giamGia = giamGia;
	}
	
}
