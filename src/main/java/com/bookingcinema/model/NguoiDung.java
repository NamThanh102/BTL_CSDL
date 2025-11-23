package com.bookingcinema.model;

import java.time.LocalDate;

public class NguoiDung {
    private String idNguoiDung;
    private String taiKhoan;
    private String matKhau;
    private String hoTen;
    private LocalDate ngaySinh;
    private String sdt;
    private String email;
    private String vaiTro; // KHACHHANG, NHANVIEN, QUANLY
    private LocalDate ngayBatDau;
    private String trangThai;
    private String idQuanLy;

    // Constructor đầy đủ
    public NguoiDung(String idNguoiDung, String taiKhoan, String matKhau, String hoTen,
                     LocalDate ngaySinh, String sdt, String email, String vaiTro,
                     LocalDate ngayBatDau, String trangThai, String idQuanLy) {
        this.idNguoiDung = idNguoiDung;
        this.taiKhoan = taiKhoan;
        this.matKhau = matKhau;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.sdt = sdt;
        this.email = email;
        this.vaiTro = vaiTro;
        this.ngayBatDau = ngayBatDau;
        this.trangThai = trangThai;
        this.idQuanLy = idQuanLy;
    }

    // Constructor rỗng
    public NguoiDung() {}

    // Getters and Setters
    public String getIdNguoiDung() { return idNguoiDung; }
    public void setIdNguoiDung(String idNguoiDung) { this.idNguoiDung = idNguoiDung; }

    public String getTaiKhoan() { return taiKhoan; }
    public void setTaiKhoan(String taiKhoan) { this.taiKhoan = taiKhoan; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getVaiTro() { return vaiTro; }
    public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }

    public LocalDate getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(LocalDate ngayBatDau) { this.ngayBatDau = ngayBatDau; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getIdQuanLy() { return idQuanLy; }
    public void setIdQuanLy(String idQuanLy) { this.idQuanLy = idQuanLy; }
}