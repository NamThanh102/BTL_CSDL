package com.bookingcinema.model;

import java.time.LocalDateTime;

public class HoaDon {
    private int idHoaDon;
    private LocalDateTime ngayThanhToan;
    private String trangThai;
    private int idPhuongThuc;
    private String idNguoiDung;

    // THÊM: Các trường DTO/Derived cần cho việc hiển thị Lịch sử
    private String tenPTTT;
    private long tongTien;

    public HoaDon(){}

    // Constructor, Getters, Setters cho các trường gốc (Giữ nguyên)
    public int getIdHoaDon() { return idHoaDon; }
    public void setIdHoaDon(int idHoaDon) { this.idHoaDon = idHoaDon; }

    public LocalDateTime getNgayThanhToan() { return ngayThanhToan; }
    public void setNgayThanhToan(LocalDateTime ngayThanhToan) { this.ngayThanhToan = ngayThanhToan; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public int getIdPhuongThuc() { return idPhuongThuc; }
    public void setIdPhuongThuc(int idPhuongThuc) { this.idPhuongThuc = idPhuongThuc; }

    public String getIdNguoiDung() { return idNguoiDung; }
    public void setIdNguoiDung(String idNguoiDung) { this.idNguoiDung = idNguoiDung; }

    // THÊM: Getters và Setters cho các trường tính toán (DTO)

    // Cần cho cột P.Thuc TToan
    public String getTenPTTT() { return tenPTTT; }
    public void setTenPTTT(String tenPTTT) { this.tenPTTT = tenPTTT; }

    // Cần cho cột Tổng tiền
    public long getTongTien() { return tongTien; }
    public void setTongTien(long tongTien) { this.tongTien = tongTien; }
}