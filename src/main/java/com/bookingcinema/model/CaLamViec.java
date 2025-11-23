package com.bookingcinema.model;

import java.time.LocalTime;

public class CaLamViec {
    private int idCaLamViec;
    private String noiDung; // CASANG, CACHIEU, CATOI
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;

    public CaLamViec() {}

    public CaLamViec(int idCaLamViec, String noiDung, LocalTime gioBatDau, LocalTime gioKetThuc) {
        this.idCaLamViec = idCaLamViec;
        this.noiDung = noiDung;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
    }

    // Getters and Setters
    public int getIdCaLamViec() { return idCaLamViec; }
    public void setIdCaLamViec(int idCaLamViec) { this.idCaLamViec = idCaLamViec; }

    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }

    public LocalTime getGioBatDau() { return gioBatDau; }
    public void setGioBatDau(LocalTime gioBatDau) { this.gioBatDau = gioBatDau; }

    public LocalTime getGioKetThuc() { return gioKetThuc; }
    public void setGioKetThuc(LocalTime gioKetThuc) { this.gioKetThuc = gioKetThuc; }

    @Override
    public String toString() {
        return noiDung + " (" + gioBatDau + " - " + gioKetThuc + ")";
    }
}
