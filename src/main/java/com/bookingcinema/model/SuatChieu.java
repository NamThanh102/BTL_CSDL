package com.bookingcinema.model;

import java.time.LocalDateTime;

public class SuatChieu {
    private int idSuatChieu;
    private int giaVe;
    private LocalDateTime thoiGianBatDau;
    private int idPhongChieu;
    private int idPhim;
    private String idNguoiDung;
    private String tenPhim;

    public SuatChieu() {}

    public String getTenPhim() { return tenPhim; }
    public void setTenPhim(String tenPhim) { this.tenPhim = tenPhim; }

    public int getIdSuatChieu() { return idSuatChieu; }
    public void setIdSuatChieu(int idSuatChieu) { this.idSuatChieu = idSuatChieu; }

    public int getGiaVe() { return giaVe; }
    public void setGiaVe(int giaVe) { this.giaVe = giaVe; }

    public LocalDateTime getThoiGianBatDau() { return thoiGianBatDau; }
    public void setThoiGianBatDau(LocalDateTime thoiGianBatDau) { this.thoiGianBatDau = thoiGianBatDau; }

    public int getIdPhongChieu() { return idPhongChieu; }
    public void setIdPhongChieu(int idPhongChieu) { this.idPhongChieu = idPhongChieu; }

    public int getIdPhim() { return idPhim; }
    public void setIdPhim(int idPhim) { this.idPhim = idPhim; }

    public String getIdNguoiDung() { return idNguoiDung; }
    public void setIdNguoiDung(String idNguoiDung) { this.idNguoiDung = idNguoiDung; }
}