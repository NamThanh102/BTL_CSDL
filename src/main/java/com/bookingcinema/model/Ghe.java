package com.bookingcinema.model;

public class Ghe {
    private int idGhe;
    private String trangThai; // TOT, BAOTRI
    private int idPhongChieu;
    private String hang;
    private int cot;

    public Ghe() {}

    public int getIdGhe() { return idGhe; }
    public void setIdGhe(int idGhe) { this.idGhe = idGhe; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public int getIdPhongChieu() { return idPhongChieu; }
    public void setIdPhongChieu(int idPhongChieu) { this.idPhongChieu = idPhongChieu; }

    public String getHang() { return hang; }
    public void setHang(String hang) { this.hang = hang; }

    public int getCot() { return cot; }
    public void setCot(int cot) { this.cot = cot; }

    // Helper để lấy tên ghế hiển thị (VD: A1, B5)
    public String getTenGhe() {
        return hang + cot;
    }
}
