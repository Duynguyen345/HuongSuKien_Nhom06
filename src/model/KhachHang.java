package model;

public class KhachHang {
	private String maKH;
	private String tenKH;
	private String soDienThoai;
	private int diemTL;
	private LoaiKhachHang loaiKH;
	public KhachHang() {
		// TODO Auto-generated constructor stub
	}
	public KhachHang(String maKH, String tenKH, String soDienThoai, int diemTL, LoaiKhachHang loaiKH) {
		super();
		this.maKH = maKH;
		this.tenKH = tenKH;
		this.soDienThoai = soDienThoai;
		this.diemTL = diemTL;
		this.loaiKH = loaiKH;
	}
	public String getMaKH() {
		return maKH;
	}
	public void setMaKH(String maKH) {
		this.maKH = maKH;
	}
	public String getTenKH() {
		return tenKH;
	}
	public void setTenKH(String tenKH) {
		this.tenKH = tenKH;
	}
	public String getSoDienThoai() {
		return soDienThoai;
	}
	public void setSoDienThoai(String soDienThoai) {
		this.soDienThoai = soDienThoai;
	}
	public int getDiemTL() {
		return diemTL;
	}
	public void setDiemTL(int diemTL) {
		this.diemTL = diemTL;
	}
	public LoaiKhachHang getLoaiKH() {
		return loaiKH;
	}
	public void setLoaiKH(LoaiKhachHang loaiKH) {
		this.loaiKH = loaiKH;
	}
	
}
