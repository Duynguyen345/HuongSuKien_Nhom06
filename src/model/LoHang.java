package model;

import java.time.LocalDate;

public class LoHang {
    private String maLo;
    private String maHH;
    private LocalDate ngayNhap;
    private LocalDate hanSuDung;
    private int soLuongNhap;
    private int soLuongTon;

    public LoHang() {}

    public LoHang(String maLo, String maHH, LocalDate ngayNhap,
                  LocalDate hanSuDung, int soLuongNhap, int soLuongTon) {
        this.maLo = maLo; this.maHH = maHH;
        this.ngayNhap = ngayNhap; this.hanSuDung = hanSuDung;
        this.soLuongNhap = soLuongNhap; this.soLuongTon = soLuongTon;
    }

    // Kiểm tra đã hết hạn chưa (HSD trước ngày hiện tại)
    public boolean isHetHan() {
        if (hanSuDung == null) return false;
        return LocalDate.now().isAfter(hanSuDung);
    }

    // Kiểm tra sắp hết hạn trong N ngày tới
    public boolean isSapHetHan(int soNgay) {
        if (hanSuDung == null) return false;
        LocalDate ngayCanh = LocalDate.now().plusDays(soNgay);
        return !LocalDate.now().isAfter(hanSuDung) && hanSuDung.isBefore(ngayCanh);
    }
    
    public String getMaLo()            { return maLo; }
    public void   setMaLo(String v)    { maLo = v; }
    public String getMaHH()            { return maHH; }
    public void   setMaHH(String v)    { maHH = v; }
    public LocalDate getNgayNhap()     { return ngayNhap; }
    public void setNgayNhap(LocalDate v){ ngayNhap = v; }
    public LocalDate getHanSuDung()    { return hanSuDung; }
    public void setHanSuDung(LocalDate v){ hanSuDung = v; }
    public int getSoLuongNhap()        { return soLuongNhap; }
    public void setSoLuongNhap(int v)  { soLuongNhap = v; }
    public int getSoLuongTon()         { return soLuongTon; }
    public void setSoLuongTon(int v)   { soLuongTon = v; }
}