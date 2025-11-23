package com.bookingcinema.controller;

import com.bookingcinema.App;
import com.bookingcinema.model.NguoiDung;
import com.bookingcinema.dao.NguoiDungDAO;
import com.bookingcinema.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.time.LocalDate;

public class UpdateInfoController {

    @FXML private Label lblTaiKhoan;
    @FXML private TextField txtHoTen;
    @FXML private DatePicker dpNgaySinh;
    @FXML private TextField txtSDT;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtMatKhau;
    @FXML private PasswordField txtXacNhanMatKhau;
    @FXML private Label lblMessage;

    private NguoiDungDAO nguoiDungDAO = new NguoiDungDAO();

    @FXML
    public void initialize() {
        // Load dữ liệu cũ của user hiện tại
        NguoiDung currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            lblTaiKhoan.setText("Tài khoản: " + currentUser.getTaiKhoan());
            txtHoTen.setText(currentUser.getHoTen());

            // CẬP NHẬT: Điền sẵn Ngày sinh
            dpNgaySinh.setValue(currentUser.getNgaySinh());

            txtSDT.setText(currentUser.getSdt());
            txtEmail.setText(currentUser.getEmail());
        } else {
            lblMessage.setText("Lỗi: Không tìm thấy thông tin phiên người dùng.");
        }
    }

    @FXML
    private void handleUpdate() {
        NguoiDung currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String newHoTen = txtHoTen.getText().trim();
        LocalDate newNgaySinh = dpNgaySinh.getValue();
        String newSDT = txtSDT.getText().trim();
        String newEmail = txtEmail.getText().trim();
        String newMatKhau = txtMatKhau.getText().trim();
        String newXacNhanMatKhau = txtXacNhanMatKhau.getText().trim();

        // 1. Validation (Tương tự Đăng ký)
        if (newHoTen.isEmpty() || newNgaySinh == null || newSDT.isEmpty() || newEmail.isEmpty()) {
            lblMessage.setText("Vui lòng điền đầy đủ Họ tên, Ngày sinh, SĐT, Email.");
            return;
        }

        // 2. Kiểm tra Mật khẩu (Nếu người dùng muốn đổi)
        String finalPassword = currentUser.getMatKhau(); // Mật khẩu cũ
        if (!newMatKhau.isEmpty()) {
            if (!newMatKhau.equals(newXacNhanMatKhau)) {
                lblMessage.setText("Mật khẩu mới không khớp!");
                return;
            }
            // Validation mật khẩu mới
            if (newMatKhau.length() < 8 || newMatKhau.contains(" ") || !newMatKhau.matches(".*[A-Z].*") || !newMatKhau.matches(".*[^a-zA-Z0-9].*")) {
                lblMessage.setText("Mật khẩu mới phải >=8 ký tự, có chữ hoa, ký tự đặc biệt & không khoảng trắng!");
                return;
            }
            finalPassword = newMatKhau;
        }

        // 3. Kiểm tra trùng lặp SĐT/Email (Loại trừ ID của chính mình)
        // Check nếu SĐT hoặc Email có thay đổi thì mới check duplicate
        if (!newSDT.equals(currentUser.getSdt()) || !newEmail.equals(currentUser.getEmail())) {
            String error = nguoiDungDAO.checkExist(currentUser.getTaiKhoan(), newEmail, newSDT, currentUser.getIdNguoiDung());
            if (error != null) {
                lblMessage.setText(error);
                return;
            }
        }

        // 4. Tạo đối tượng update
        NguoiDung updatedUser = new NguoiDung();
        updatedUser.setIdNguoiDung(currentUser.getIdNguoiDung());
        updatedUser.setHoTen(newHoTen);
        updatedUser.setNgaySinh(newNgaySinh);
        updatedUser.setSdt(newSDT);
        updatedUser.setEmail(newEmail);

        // 5. Gọi DAO và Update Session
        boolean success = nguoiDungDAO.updateUserInfo(updatedUser, finalPassword);

        if (success) {
            // Cập nhật lại Session để Dashboard thấy thay đổi ngay lập tức
            currentUser.setHoTen(newHoTen);
            currentUser.setNgaySinh(newNgaySinh);
            currentUser.setSdt(newSDT);
            currentUser.setEmail(newEmail);
            currentUser.setMatKhau(finalPassword); // Cần thiết nếu muốn dùng mật khẩu này cho các lần login sau (nếu lưu cache)
            UserSession.getInstance().setCurrentUser(currentUser);

            lblMessage.setStyle("-fx-text-fill: green;");
            lblMessage.setText("Cập nhật thành công! Quay về trang chủ.");

            try {
                goBack();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            lblMessage.setStyle("-fx-text-fill: red;");
            lblMessage.setText("Cập nhật thất bại. Vui lòng kiểm tra lại dữ liệu.");
        }
    }

    @FXML
    private void goBack() throws IOException {
        App.setRoot("customer_dashboard");
    }
}