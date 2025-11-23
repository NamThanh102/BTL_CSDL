package com.bookingcinema.controller;

import com.bookingcinema.App;
import com.bookingcinema.model.Ghe;
import com.bookingcinema.dao.GheDAO;
import com.bookingcinema.model.Phim;
import com.bookingcinema.model.SuatChieu;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookingSeatController {

    @FXML private Label lblThongTinSuat;
    @FXML private GridPane gridGhe;
    @FXML private Label lblGheChon;
    @FXML private Label lblTongTien;
    @FXML private Button btnThanhToan;

    private SuatChieu currentSuatChieu;
    private Phim currentPhim;
    private GheDAO gheDAO = new GheDAO();

    // Danh sách ghế khách hàng đang chọn (lưu object Ghe)
    private List<Ghe> selectedSeats = new ArrayList<>();

    // Nhận dữ liệu từ màn hình trước
    public void setSuatChieuData(SuatChieu suatChieu, Phim phim) {
        this.currentSuatChieu = suatChieu;
        this.currentPhim = phim;

        lblThongTinSuat.setText(String.format("%s - Phòng %d",
                phim.getTen(), suatChieu.getIdPhongChieu()));

        loadSeats();
    }

    private void loadSeats() {
        gridGhe.getChildren().clear();

        // 1. Lấy toàn bộ ghế của phòng
        List<Ghe> allSeats = gheDAO.getGheByPhong(currentSuatChieu.getIdPhongChieu());

        // 2. Lấy danh sách ID ghế đã bị mua
        List<Integer> bookedSeatIds = gheDAO.getGheDaDat(currentSuatChieu.getIdSuatChieu());

        // 3. Vẽ ghế lên Grid
        for (Ghe ghe : allSeats) {
            ToggleButton btnSeat = new ToggleButton(ghe.getTenGhe());
            btnSeat.setPrefSize(50, 40);

            // Chuyển đổi Hàng (A, B..) thành chỉ số dòng (0, 1..)
            int rowIndex = ghe.getHang().charAt(0) - 'A';
            int colIndex = ghe.getCot() - 1;

            // Style mặc định
            btnSeat.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-cursor: hand; -fx-border-color: #aaa; -fx-border-radius: 3;");

            // CHECK TRẠNG THÁI
            if ("BAOTRI".equals(ghe.getTrangThai())) {
                // Ghế bảo trì
                btnSeat.setDisable(true);
                btnSeat.setStyle("-fx-background-color: #555; -fx-text-fill: #888;");
            } else if (bookedSeatIds.contains(ghe.getIdGhe())) {
                // Ghế đã bán
                btnSeat.setDisable(true);
                btnSeat.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-opacity: 1.0;"); // Đỏ
            } else {
                // Ghế trống -> Cho phép chọn
                btnSeat.setOnAction(e -> toggleSeatSelection(btnSeat, ghe));
            }

            gridGhe.add(btnSeat, colIndex, rowIndex);
        }
    }

    private void toggleSeatSelection(ToggleButton btn, Ghe ghe) {
        if (btn.isSelected()) {
            // Chọn ghế
            selectedSeats.add(ghe);
            btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;"); // Xanh lá
        } else {
            // Bỏ chọn
            selectedSeats.remove(ghe);
            btn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #aaa;");
        }
        updateSummary();
    }

    private void updateSummary() {
        // Cập nhật text hiển thị ghế
        StringBuilder sb = new StringBuilder("Ghế chọn: ");
        for (Ghe g : selectedSeats) {
            sb.append(g.getTenGhe()).append(", ");
        }
        // Xóa dấu phẩy cuối
        if (!selectedSeats.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        lblGheChon.setText(sb.toString());

        // Tính tổng tiền
        long total = (long) selectedSeats.size() * currentSuatChieu.getGiaVe();
        lblTongTien.setText(String.format("Tổng tiền: %,d VNĐ", total));

        // Bật/tắt nút thanh toán
        btnThanhToan.setDisable(selectedSeats.isEmpty());
    }

    @FXML
    private void handleConfirm() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/bookingcinema/view/payment.fxml"));
            Parent root = loader.load();

            PaymentController controller = loader.getController();
            controller.setBookingData(currentSuatChieu, currentPhim, selectedSeats);

            // SỬA ĐỔI: Dùng App.setRoot
            App.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/bookingcinema/view/booking_time.fxml"));
        Parent root = loader.load();

        BookingTimeController controller = loader.getController();
        controller.setPhim(currentPhim);

        // SỬA ĐỔI: Dùng App.setRoot
        App.setRoot(root);
    }
}