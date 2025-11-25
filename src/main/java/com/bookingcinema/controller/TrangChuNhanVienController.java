package com.bookingcinema.controller;

import com.bookingcinema.App;
import com.bookingcinema.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class TrangChuNhanVienController {

    @FXML private Label lblWelcome;

    @FXML
    public void initialize() {
        if (UserSession.getInstance().getCurrentUser() != null) {
            lblWelcome.setText("Xin chào, " + UserSession.getInstance().getCurrentUser().getHoTen());
        }
    }

    @FXML
    public void goToUpdateInfo() throws IOException {
        App.setRoot("cap_nhat_thong_tin_nhan_vien");
    }

    @FXML
    public void goToTraCuuKhachHang() throws IOException {
        App.setRoot("tra_cuu_khach_hang");
    }

    @FXML
    public void goToXemHoaDon() throws IOException {
        App.setRoot("xem_hoa_don_nhan_vien");
    }

    @FXML
    public void goToInVe() throws IOException {
        App.setRoot("in_ve");
    }

    @FXML
    public void handleLogout() throws IOException {
        UserSession.getInstance().clearSession();
        App.setRoot("dang_nhap");
    }
}
