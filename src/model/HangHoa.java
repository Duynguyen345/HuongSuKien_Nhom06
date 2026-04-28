package model;

public class HangHoa {
	private String maHH;
	private String maVach;
	private String tenHH;
	private double giaSP;
	public HangHoa() {
	}
	public HangHoa(String maHH, String maVach, String tenHH, double giaSP) {
		super();
		this.maHH = maHH;
		this.maVach = maVach;
		this.tenHH = tenHH;
		this.giaSP = giaSP;
	}
	public String getMaHH() {
		return maHH;
	}
	public void setMaHH(String maHH) {
		this.maHH = maHH;
	}
	public String getMaVach() {
		return maVach;
	}
	public void setMaVach(String maVach) {
		this.maVach = maVach;
	}
	public String getTenHH() {
		return tenHH;
	}
	public void setTenHH(String tenHH) {
		this.tenHH = tenHH;
	}
	public double getGiaSP() {
		return giaSP;
	}
	public void setGiaSP(double giaSP) {
		this.giaSP = giaSP;
	}
	
	
}
