package com.bookingcinema.controller;

import com.bookingcinema.App;
import com.bookingcinema.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Label;

import java.io.IOException;

public class TrangChuNhanVienController {

    @FXML private Label lblWelcome;
    @FXML private BorderPane contentPane1; // Tra cứu khách hàng
    @FXML private BorderPane contentPane2; // Xem hóa đơn
    @FXML private BorderPane contentPane3; // In vé

    @FXML
    public void initialize() {
        if (UserSession.getInstance().getCurrentUser() != null) {
            lblWelcome.setText("Xin chào, " + UserSession.getInstance().getCurrentUser().getHoTen());
        }
        
        // Load các tab content
        loadTabContent();
    }

    private void loadTabContent() {
        try {
            // Load tab 1: Tra cứu khách hàng
            FXMLLoader loader1 = new FXMLLoader(App.class.getResource("/com/bookingcinema/view/tra_cuu_khach_hang.fxml"));
            contentPane1.setCenter(loader1.load());

            // Load tab 2: Xem hóa đơn
            FXMLLoader loader2 = new FXMLLoader(App.class.getResource("/com/bookingcinema/view/xem_hoa_don_nhan_vien.fxml"));
            contentPane2.setCenter(loader2.load());

            // Load tab 3: In vé
            FXMLLoader loader3 = new FXMLLoader(App.class.getResource("/com/bookingcinema/view/in_ve.fxml"));
            contentPane3.setCenter(loader3.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToUpdateInfo() throws IOException {
        App.setRoot("cap_nhat_thong_tin_nhan_vien");
    }

    @FXML
    public void handleLogout() throws IOException {
        UserSession.getInstance().clearSession();
        App.setRoot("dang_nhap");
    }
}
