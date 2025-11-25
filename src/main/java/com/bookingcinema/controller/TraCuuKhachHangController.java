package com.bookingcinema.controller;

import com.bookingcinema.App;
import com.bookingcinema.dao.NguoiDungDAO;
import com.bookingcinema.model.NguoiDung;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class TraCuuKhachHangController {

    @FXML private TextField txtSearchInput;
    @FXML private ComboBox<String> cboSearchType;
    @FXML private VBox vboxKhachHangInfo;
    @FXML private Label lblMessage;
    @FXML private Label lblTaiKhoan;
    @FXML private Label lblHoTen;
    @FXML private Label lblEmail;
    @FXML private Label lblSDT;
    @FXML private Label lblNgaySinh;
    @FXML private Label lblVaiTro;

    private NguoiDungDAO nguoiDungDAO = new NguoiDungDAO();

    @FXML
    public void initialize() {
        cboSearchType.getItems().addAll("Tài khoản", "Email", "Số điện thoại");
        cboSearchType.setValue("Tài khoản");
        vboxKhachHangInfo.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 15; -fx-spacing: 10;");
        vboxKhachHangInfo.setVisible(false);
    }

    @FXML
    private void handleSearch() {
        String searchInput = txtSearchInput.getText().trim();
        if (searchInput.isEmpty()) {
            lblMessage.setText("Vui lòng nhập thông tin tìm kiếm!");
            lblMessage.setStyle("-fx-text-fill: red;");
            vboxKhachHangInfo.setVisible(false);
            return;
        }

        String searchType = cboSearchType.getValue();
        NguoiDung user = null;

        // Tìm kiếm theo loại
        if ("Tài khoản".equals(searchType)) {
            user = nguoiDungDAO.getUserByTaiKhoan(searchInput);
        } else if ("Email".equals(searchType)) {
            user = nguoiDungDAO.getUserByEmail(searchInput);
        } else if ("Số điện thoại".equals(searchType)) {
            user = nguoiDungDAO.getUserBySDT(searchInput);
        }

        if (user != null) {
            displayUserInfo(user);
            lblMessage.setText("Tìm kiếm thành công!");
            lblMessage.setStyle("-fx-text-fill: green;");
        } else {
            lblMessage.setText("Không tìm thấy khách hàng với thông tin: " + searchInput);
            lblMessage.setStyle("-fx-text-fill: red;");
            vboxKhachHangInfo.setVisible(false);
        }
    }

    private void displayUserInfo(NguoiDung user) {
        lblTaiKhoan.setText("Tài khoản: " + user.getTaiKhoan());
        lblHoTen.setText("Họ tên: " + user.getHoTen());
        lblEmail.setText("Email: " + user.getEmail());
        lblSDT.setText("Số điện thoại: " + user.getSdt());
        lblNgaySinh.setText("Ngày sinh: " + (user.getNgaySinh() != null ? user.getNgaySinh() : "N/A"));
        lblVaiTro.setText("Vai trò: " + user.getVaiTro());
        vboxKhachHangInfo.setVisible(true);
    }

    @FXML
    public void goBack() throws IOException {
        App.setRoot("trang_chu_nhan_vien");
    }
}
