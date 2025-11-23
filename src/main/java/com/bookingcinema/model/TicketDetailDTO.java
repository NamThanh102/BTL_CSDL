package com.bookingcinema.model;

import java.time.LocalDateTime;

// DTO chứa thông tin vé chi tiết (dùng cho TableView)
public class TicketDetailDTO {
    private String tenPhim;
    private LocalDateTime thoiGianBatDau;
    private int idPhongChieu;
    private String tenGhe;
    private int giaVe;
    private String trangThaiVe;

    public TicketDetailDTO() {}

    // (Constructor, Getters, Setters - bạn có thể tự thêm)

    public String getTenPhim() { return tenPhim; }
    public void setTenPhim(String tenPhim) { this.tenPhim = tenPhim; }

    public LocalDateTime getThoiGianBatDau() { return thoiGianBatDau; }
    public void setThoiGianBatDau(LocalDateTime thoiGianBatDau) { this.thoiGianBatDau = thoiGianBatDau; }

    public int getIdPhongChieu() { return idPhongChieu; }
    public void setIdPhongChieu(int idPhongChieu) { this.idPhongChieu = idPhongChieu; }

    public String getTenGhe() { return tenGhe; }
    public void setTenGhe(String tenGhe) { this.tenGhe = tenGhe; }

    public int getGiaVe() { return giaVe; }
    public void setGiaVe(int giaVe) { this.giaVe = giaVe; }

    public String getTrangThaiVe() { return trangThaiVe; }
    public void setTrangThaiVe(String trangThaiVe) { this.trangThaiVe = trangThaiVe; }
}