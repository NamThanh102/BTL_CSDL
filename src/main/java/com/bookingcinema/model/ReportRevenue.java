package com.bookingcinema.model;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model đại diện cho một hàng trong Báo cáo Doanh thu (Doanh thu theo Phim).
 */
public class ReportRevenue {
    private final IntegerProperty idPhim;
    private final StringProperty tenPhim;
    private final StringProperty theLoai;
    private final IntegerProperty tongSuatChieu;
    private final IntegerProperty soLuongVe;
    private final FloatProperty tongDoanhThu;
    private final FloatProperty tiTrong;

    public ReportRevenue(int idPhim, String tenPhim, String theLoai, int tongSuatChieu, int soLuongVe, float tongDoanhThu, float tiTrong) {
        this.idPhim = new SimpleIntegerProperty(idPhim);
        this.tenPhim = new SimpleStringProperty(tenPhim);
        this.theLoai = new SimpleStringProperty(theLoai);
        this.tongSuatChieu = new SimpleIntegerProperty(tongSuatChieu);
        this.soLuongVe = new SimpleIntegerProperty(soLuongVe);
        this.tongDoanhThu = new SimpleFloatProperty(tongDoanhThu);
        this.tiTrong = new SimpleFloatProperty(tiTrong);
    }

    // --- Property Getters (cho TableView) ---
    public IntegerProperty idPhimProperty() {
        return idPhim;
    }
    public StringProperty tenPhimProperty() {
        return tenPhim;
    }
    public StringProperty theLoaiProperty() {
        return theLoai;
    }
    public IntegerProperty tongSuatChieuProperty() {
        return tongSuatChieu;
    }
    public IntegerProperty soLuongVeProperty() {
        return soLuongVe;
    }
    public FloatProperty tongDoanhThuProperty() {
        return tongDoanhThu;
    }
    public FloatProperty tiTrongProperty() {
        return tiTrong;
    }

    // --- Getters ---
    public int getIdPhim() {
        return idPhim.get();
    }
    public String getTenPhim() {
        return tenPhim.get();
    }
    public String getTheLoai() {
        return theLoai.get();
    }
    public int getTongSuatChieu() {
        return tongSuatChieu.get();
    }
    public int getSoLuongVe() {
        return soLuongVe.get();
    }
    public float getTongDoanhThu() {
        return tongDoanhThu.get();
    }
    public float getTiTrong() {
        return tiTrong.get();
    }
}