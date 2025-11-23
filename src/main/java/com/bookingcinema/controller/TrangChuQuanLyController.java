package com.bookingcinema.controller;

import com.bookingcinema.App;
import com.bookingcinema.utils.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class TrangChuQuanLyController {

    @FXML private Label lblWelcome;
    @FXML private BorderPane contentPane;

    @FXML
    public void initialize() {
        if (UserSession.getInstance().getCurrentUser() != null) {
            lblWelcome.setText("Xin chào, " + UserSession.getInstance().getCurrentUser().getHoTen());
        }

        try {
            loadPhimShowtimeView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadContent(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/bookingcinema/view/" + fxmlFile + ".fxml"));
        Parent root = loader.load();
        contentPane.setCenter(root);
    }

    private void loadPhimShowtimeView() throws IOException {
        loadContent("quan_ly_phim_suat_chieu");
    }

    private void loadEmployeeView() throws IOException {
        loadContent("quan_ly_nhan_vien");
    }

    private void loadShiftView() throws IOException {
        loadContent("quan_ly_ca_lam_viec");
    }

    // Đã đổi tên hàm load Report View để tải ReportDetailView.fxml
    private void loadbao_cao() throws IOException {
        loadContent("bao_cao");
    }

    @FXML
    private void switchView(ActionEvent event) throws IOException {
        Button source = (Button) event.getSource();

        if (source.getId().equals("btnQLPhim")) {
            loadPhimShowtimeView();
        } else if (source.getId().equals("btnQLNhanVien")) {
            loadEmployeeView();
        } else if (source.getId().equals("btnQLCaLamViec")) {
            loadShiftView();
        } else if (source.getId().equals("btnBaoCao")) {
            loadbao_cao();
        }
    }

    @FXML
    public void handleLogout() throws IOException {
        UserSession.getInstance().clearSession();
        App.setRoot("dang_nhap");
    }
}

