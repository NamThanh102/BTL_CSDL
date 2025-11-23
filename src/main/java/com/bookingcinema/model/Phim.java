package com.bookingcinema.model;

import java.time.LocalDate;

public class Phim {
    private int idPhim;
    private String ten;
    private LocalDate ngayPhatHanh;
    private float thoiLuong;
    private String ngonNguChinh;
    private String noiDung;
    private String idNguoiDung;
    private String theLoai;
    private int showtimeCount;

    public Phim() {
    }

    // Constructor cập nhật thêm theLoai
    public Phim(int idPhim, String ten, LocalDate ngayPhatHanh, float thoiLuong, String ngonNguChinh, String noiDung, String idNguoiDung, String theLoai) {
        this.idPhim = idPhim;
        this.ten = ten;
        this.ngayPhatHanh = ngayPhatHanh;
        this.thoiLuong = thoiLuong;
        this.ngonNguChinh = ngonNguChinh;
        this.noiDung = noiDung;
        this.idNguoiDung = idNguoiDung;
        this.theLoai = theLoai;
    }

    // Getters and Setters cũ giữ nguyên...
    public int getIdPhim() { return idPhim; }
    public void setIdPhim(int idPhim) { this.idPhim = idPhim; }
    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }
    public LocalDate getNgayPhatHanh() { return ngayPhatHanh; }
    public void setNgayPhatHanh(LocalDate ngayPhatHanh) { this.ngayPhatHanh = ngayPhatHanh; }
    public float getThoiLuong() { return thoiLuong; }
    public void setThoiLuong(float thoiLuong) { this.thoiLuong = thoiLuong; }
    public String getNgonNguChinh() { return ngonNguChinh; }
    public void setNgonNguChinh(String ngonNguChinh) { this.ngonNguChinh = ngonNguChinh; }
    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    public String getIdNguoiDung() { return idNguoiDung; }
    public void setIdNguoiDung(String idNguoiDung) { this.idNguoiDung = idNguoiDung; }

    public String getTheLoai() { return theLoai; }
    public void setTheLoai(String theLoai) { this.theLoai = theLoai; }
    public int getShowtimeCount() { return showtimeCount; }
    public void setShowtimeCount(int showtimeCount) { this.showtimeCount = showtimeCount; }
}