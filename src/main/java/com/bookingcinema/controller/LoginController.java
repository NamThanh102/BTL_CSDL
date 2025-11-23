package com.bookingcinema.controller;

import com.bookingcinema.App;
import com.bookingcinema.model.NguoiDung;
import com.bookingcinema.dao.NguoiDungDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtTaiKhoan;

    @FXML
    private PasswordField txtMatKhau;

    @FXML
    private Label lblMessage;

    private NguoiDungDAO nguoiDungDAO = new NguoiDungDAO();

    @FXML
    private void handleLogin() throws IOException {
        String taiKhoan = txtTaiKhoan.getText();
        String matKhau = txtMatKhau.getText();

        if (taiKhoan.isEmpty() || matKhau.isEmpty()) {
            lblMessage.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        NguoiDung user = nguoiDungDAO.checkLogin(taiKhoan, matKhau);

        if (user != null) {
            // 1. LƯU SESSION (Quan trọng)
            com.bookingcinema.utils.UserSession.getInstance().setCurrentUser(user);

            String vaiTro = user.getVaiTro();
            System.out.println("User: " + user.getHoTen() + " - Role: " + vaiTro);

            // 2. ĐIỀU HƯỚNG
            if ("QUANLY".equalsIgnoreCase(vaiTro)) {
                App.setRoot("manager_dashboard");
            } else if ("NHANVIEN".equalsIgnoreCase(vaiTro)) {
                lblMessage.setText("Xin chào Nhân Viên (Chưa có giao diện)");
            } else {
                // Chuyển sang Dashboard Khách hàng
                App.setRoot("customer_dashboard");
            }

        } else {
            lblMessage.setText("Tài khoản hoặc mật khẩu không đúng!");
        }
    }

    @FXML
    private void switchToRegister() throws IOException {
        App.setRoot("register"); // Chuyển sang màn hình đăng ký
    }
}