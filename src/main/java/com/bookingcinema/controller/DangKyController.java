package com.bookingcinema.controller;

import com.bookingcinema.App;
import com.bookingcinema.model.NguoiDung;
import com.bookingcinema.dao.NguoiDungDAO;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.time.LocalDate;

public class DangKyController {

    @FXML private TextField txtHoTen;
    @FXML private DatePicker dpNgaySinh;
    @FXML private TextField txtSDT;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTaiKhoan;
    @FXML private PasswordField txtMatKhau;
    @FXML private PasswordField txtXacNhanMatKhau; // Thêm biến mới
    @FXML private Label lblMessage;

    private NguoiDungDAO nguoiDungDAO = new NguoiDungDAO();

    @FXML
    private void handleRegister() {
        // 1. Lấy dữ liệu
        String hoTen = txtHoTen.getText().trim();
        LocalDate ngaySinh = dpNgaySinh.getValue();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();
        String taiKhoan = txtTaiKhoan.getText().trim();
        String matKhau = txtMatKhau.getText().trim();
        String xacNhanMatKhau = txtXacNhanMatKhau.getText().trim(); // Lấy dữ liệu xác nhận

        // 2. Validation cơ bản (Không được để trống)
        if (hoTen.isEmpty() || ngaySinh == null || sdt.isEmpty() || email.isEmpty() ||
                taiKhoan.isEmpty() || matKhau.isEmpty() || xacNhanMatKhau.isEmpty()) {
            lblMessage.setText("Vui lòng điền đầy đủ thông tin!");
            return;
        }

        // KIỂM TRA MẬT KHẨU XÁC NHẬN
        if (!matKhau.equals(xacNhanMatKhau)) {
            lblMessage.setText("Mật khẩu xác nhận không khớp!");
            return;
        }

        // 3. Validation chi tiết theo yêu cầu

        // Ngày sinh < hiện tại
        if (!ngaySinh.isBefore(LocalDate.now())) {
            lblMessage.setText("Ngày sinh phải nhỏ hơn ngày hiện tại!");
            return;
        }

        // Tài khoản: Tối thiểu 8 ký tự, không dấu cách
        if (taiKhoan.length() < 8 || taiKhoan.contains(" ")) {
            lblMessage.setText("Tài khoản phải >= 8 ký tự và không chứa khoảng trắng!");
            return;
        }

        // Mật khẩu: Tối thiểu 8 ký tự, 1 hoa, 1 đặc biệt, không dấu cách
        if (matKhau.length() < 8 || matKhau.contains(" ") || !matKhau.matches(".*[A-Z].*") || !matKhau.matches(".*[^a-zA-Z0-9].*")) {
            lblMessage.setText("Mật khẩu phải >=8 ký tự, có chữ hoa, ký tự đặc biệt & không khoảng trắng!");
            return;
        }

        // 4. Kiểm tra trùng lặp DB
        String error = nguoiDungDAO.checkExist(taiKhoan, email, sdt);
        if (error != null) {
            lblMessage.setText(error);
            return;
        }

        // 5. Tạo đối tượng và lưu
        NguoiDung newUser = new NguoiDung();
        newUser.setHoTen(hoTen);
        newUser.setNgaySinh(ngaySinh);
        newUser.setSdt(sdt);
        newUser.setEmail(email);
        newUser.setTaiKhoan(taiKhoan);
        newUser.setMatKhau(matKhau);
        // VaiTro set mặc định trong DAO

        boolean success = nguoiDungDAO.registerUser(newUser);
        if (success) {
            lblMessage.setStyle("-fx-text-fill: green;");
            lblMessage.setText("Đăng ký thành công! Đang chuyển về đăng nhập...");
            // Delay nhẹ hoặc chuyển luôn
            try {
                switchToLogin();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            lblMessage.setStyle("-fx-text-fill: red;");
            lblMessage.setText("Đăng ký thất bại. Vui lòng thử lại!");
        }
    }

    @FXML
    private void switchToLogin() throws IOException {
        App.setRoot("dang_nhap");
    }
}

