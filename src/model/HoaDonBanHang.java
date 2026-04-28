package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDonBanHang {
	private String maHD;
	private LocalDateTime ngayLap;
	private NhanVien nhanVien;
	private KhachHang khachHang;
	private List<ChiTietHoaDon> dsChiTiet = new ArrayList<>();
	private double tongTien;
 	public HoaDonBanHang() {
		// TODO Auto-generated constructor stub
	}
 	public HoaDonBanHang(String maHD, LocalDateTime ngayLap, NhanVien nhanVien, KhachHang khachHang,
			List<ChiTietHoaDon> dsChiTiet, double tongTien) {
		super();
		this.maHD = maHD;
		this.ngayLap = LocalDateTime.now();
		this.nhanVien = nhanVien;
		this.khachHang = khachHang;
		this.dsChiTiet = dsChiTiet;
		this.tongTien = tongTien;
	}
	public String getMaHD() {
		return maHD;
	}
	public void setMaHD(String maHD) {
		this.maHD = maHD;
	}
	public LocalDateTime getNgayLap() {
		return ngayLap;
	}
	public void setNgayLap(LocalDateTime ngayLap) {
		this.ngayLap = ngayLap;
	}
	public NhanVien getNhanVien() {
		return nhanVien;
	}
	public void setNhanVien(NhanVien nhanVien) {
		this.nhanVien = nhanVien;
	}
	public KhachHang getKhachHang() {
		return khachHang;
	}
	public void setKhachHang(KhachHang khachHang) {
		this.khachHang = khachHang;
	}
	public List<ChiTietHoaDon> getDsChiTiet() {
		return dsChiTiet;
	}
	public void setDsChiTiet(List<ChiTietHoaDon> dsChiTiet) {
		this.dsChiTiet = dsChiTiet;
	}
	public double getTongTien() {
		return tongTien;
	}
	public void setTongTien(double tongTien) {
		this.tongTien = tongTien;
	}
 	
}
