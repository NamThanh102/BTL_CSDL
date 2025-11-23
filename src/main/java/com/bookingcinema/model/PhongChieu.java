package com.bookingcinema.model;

public class PhongChieu {
    private int idPhongChieu;
    private int sucChua;
    private String trangThai;

    public PhongChieu() {}

    public PhongChieu(int idPhongChieu, int sucChua, String trangThai) {
        this.idPhongChieu = idPhongChieu;
        this.sucChua = sucChua;
        this.trangThai = trangThai;
    }

    public int getIdPhongChieu() { return idPhongChieu; }
    public void setIdPhongChieu(int idPhongChieu) { this.idPhongChieu = idPhongChieu; }

    public int getSucChua() { return sucChua; }
    public void setSucChua(int sucChua) { this.sucChua = sucChua; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}