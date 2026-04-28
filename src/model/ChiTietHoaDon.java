package model;

public class ChiTietHoaDon {
	private HangHoa hangHoa;
	private int soLuong;
	private double donGia;
	
	public ChiTietHoaDon() {
		// TODO Auto-generated constructor stub
	}

	public ChiTietHoaDon(HangHoa hangHoa, int soLuong, double donGia) {
		super();
		this.hangHoa = hangHoa;
		this.soLuong = soLuong;
		this.donGia = donGia;
	}

	public HangHoa getHangHoa() {
		return hangHoa;
	}

	public void setHangHoa(HangHoa hangHoa) {
		this.hangHoa = hangHoa;
	}

	public int getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}

	public double getDonGia() {
		return donGia;
	}

	public void setDonGia(double donGia) {
		this.donGia = donGia;
	}
	public double getThanhTien() {
		return soLuong * donGia;
	}
}
