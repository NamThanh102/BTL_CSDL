package com.bookingcinema.model;

public class TheLoai {
    private int idTheLoai;
    private String noiDung;
    private int soPhim; // Số lượng phim thuộc thể loại này

    public TheLoai() {}

    public TheLoai(int idTheLoai, String noiDung) {
        this.idTheLoai = idTheLoai;
        this.noiDung = noiDung;
    }

    public int getIdTheLoai() { return idTheLoai; }
    public void setIdTheLoai(int idTheLoai) { this.idTheLoai = idTheLoai; }

    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    
    public int getSoPhim() { return soPhim; }
    public void setSoPhim(int soPhim) { this.soPhim = soPhim; }
}