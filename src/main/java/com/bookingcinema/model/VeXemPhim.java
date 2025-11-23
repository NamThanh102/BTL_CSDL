package com.bookingcinema.model;

public class VeXemPhim {
    private int idVe;
    private String trangThai;
    private int idSuatChieu;
    private int idHoaDon;
    private int idGhe;

    public VeXemPhim(int idVe, String trangThai, int idSuatChieu, int idHoaDon, int idGhe) {
        this.idVe = idVe;
        this.trangThai = trangThai;
        this.idSuatChieu = idSuatChieu;
        this.idHoaDon = idHoaDon;
        this.idGhe = idGhe;
    }

    public int getIdVe() {
        return idVe;
    }

    public void setIdVe(int idVe) {
        this.idVe = idVe;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public int getIdSuatChieu() {
        return idSuatChieu;
    }

    public void setIdSuatChieu(int idSuatChieu) {
        this.idSuatChieu = idSuatChieu;
    }

    public int getIdHoaDon() {
        return idHoaDon;
    }

    public void setIdHoaDon(int idHoaDon) {
        this.idHoaDon = idHoaDon;
    }

    public int getIdGhe() {
        return idGhe;
    }

    public void setIdGhe(int idGhe) {
        this.idGhe = idGhe;
    }
}