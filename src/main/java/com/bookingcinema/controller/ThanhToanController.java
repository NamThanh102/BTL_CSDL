package com.bookingcinema.controller;

import com.bookingcinema.App;
import com.bookingcinema.model.*;
import com.bookingcinema.dao.*;
import com.bookingcinema.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ThanhToanController {

    @FXML private Label lblTenPhim;
    @FXML private Label lblSuatChieu;
    @FXML private Label lblPhong;
    @FXML private Label lblGhe;
    @FXML private Label lblTongTien;
    @FXML private ComboBox<PhuongThucThanhToan> cboPhuongThuc;
    @FXML private Label lblMessage;
    @FXML private Button btnPay;

    private SuatChieu currentSuatChieu;
    private Phim currentPhim;
    private List<Ghe> selectedSeats;
    private HoaDonDAO hoaDonDAO = new HoaDonDAO();

    public void setBookingData(SuatChieu sc, Phim p, List<Ghe> gheList) {
        this.currentSuatChieu = sc;
        this.currentPhim = p;
        this.selectedSeats = gheList;

        // Hiển thị thông tin
        lblTenPhim.setText("Phim: " + p.getTen());
        lblSuatChieu.setText("Suất chiếu: " + sc.getThoiGianBatDau().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")));
        lblPhong.setText("Phòng: " + sc.getIdPhongChieu());

        StringBuilder sb = new StringBuilder("Ghế: ");
        for (Ghe g : gheList) sb.append(g.getTenGhe()).append(", ");
        if (!gheList.isEmpty()) sb.setLength(sb.length() - 2);
        lblGhe.setText(sb.toString());

        long total = (long) gheList.size() * sc.getGiaVe();
        lblTongTien.setText(String.format("Tổng tiền: %,d VNĐ", total));

        loadthanh_toanMethods();
    }

    private void loadthanh_toanMethods() {
        List<PhuongThucThanhToan> list = hoaDonDAO.getPaymentMethods();
        cboPhuongThuc.setItems(FXCollections.observableArrayList(list));
        if(!list.isEmpty()) cboPhuongThuc.getSelectionModel().select(0);
    }

    @FXML
    private void handlePayment() {
        PhuongThucThanhToan method = cboPhuongThuc.getValue();
        if (method == null) {
            lblMessage.setText("Vui lòng chọn phương thức thanh toán!");
            return;
        }

        String userId = UserSession.getInstance().getCurrentUser().getIdNguoiDung();
        boolean success = hoaDonDAO.createBooking(userId, currentSuatChieu.getIdSuatChieu(), method.getId(), selectedSeats);

        if (success) {
            showAlertAndReturn("Thành công", "Thanh toán thành công! Vé đã được gửi vào tài khoản.");
        } else {
            lblMessage.setText("Thanh toán thất bại. Vui lòng thử lại!");
        }
    }

    private void showAlertAndReturn(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();

        try {
            // SỬA ĐỔI: Quay về Dashboard
            App.setRoot("trang_chu_khach_hang");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/bookingcinema/view/dat_ve_ghe.fxml"));
        Parent root = loader.load();
        DatVeGheController controller = loader.getController();
        controller.setSuatChieuData(currentSuatChieu, currentPhim);

        // SỬA ĐỔI: Dùng App.setRoot
        App.setRoot(root);
    }
}

