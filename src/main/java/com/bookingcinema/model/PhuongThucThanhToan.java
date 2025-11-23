package com.bookingcinema.model;

public class PhuongThucThanhToan {
    private int id;
    private String noiDung;

    public PhuongThucThanhToan(int id, String noiDung) {
        this.id = id;
        this.noiDung = noiDung;
    }

    // Override toString để hiển thị tên trong ComboBox
    @Override
    public String toString() {
        return noiDung;
    }

    public int getId() { return id; }
    public String getNoiDung() { return noiDung; }
}