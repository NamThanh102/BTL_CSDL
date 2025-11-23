package com.bookingcinema.controller;

import com.bookingcinema.App;
import com.bookingcinema.model.Phim;
import com.bookingcinema.model.SuatChieu;
import com.bookingcinema.dao.SuatChieuDAO;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DatVeSuatChieuController {

    @FXML private Label lblTenPhim;
    @FXML private Label lblThongTinPhim;
    @FXML private FlowPane flowPaneSuatChieu;

    private Phim selectedPhim;
    private SuatChieuDAO suatChieuDAO = new SuatChieuDAO();
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void setPhim(Phim phim) {
        this.selectedPhim = phim;

        lblTenPhim.setText(phim.getTen());

        // Hiển thị đầy đủ thông tin: Thể loại, Thời lượng, Ngôn ngữ
        String thongTin = String.format("Thể loại: %s | Thời lượng: %.1f phút | Ngôn ngữ: %s",
                phim.getTheLoai(),
                phim.getThoiLuong(),
                phim.getNgonNguChinh());
        lblThongTinPhim.setText(thongTin);

        loadShowtimes();
    }

    private void loadShowtimes() {
        flowPaneSuatChieu.getChildren().clear();
        List<SuatChieu> listSuatChieu = suatChieuDAO.getSuatChieuByPhim(selectedPhim.getIdPhim());

        if (listSuatChieu.isEmpty()) {
            flowPaneSuatChieu.getChildren().add(new Label("Chưa có suất chiếu nào cho phim này."));
            return;
        }

        for (SuatChieu sc : listSuatChieu) {
            Button btn = createShowtimeButton(sc);
            flowPaneSuatChieu.getChildren().add(btn);
        }
    }

    private Button createShowtimeButton(SuatChieu sc) {
        String text = String.format("%s\n%s\nPhòng %d\n%,d VNĐ",
                sc.getThoiGianBatDau().format(timeFormatter),
                sc.getThoiGianBatDau().format(dateFormatter),
                sc.getIdPhongChieu(),
                sc.getGiaVe());

        Button btn = new Button(text);
        btn.setPrefWidth(160);
        btn.setPrefHeight(100);
        btn.setStyle("-fx-background-color: white; -fx-border-color: #4CAF50; -fx-border-radius: 5; -fx-text-alignment: center; -fx-cursor: hand; -fx-font-size: 13px;");

        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #e8f5e9; -fx-border-color: #4CAF50; -fx-border-radius: 5; -fx-text-alignment: center; -fx-cursor: hand; -fx-font-size: 13px;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: white; -fx-border-color: #4CAF50; -fx-border-radius: 5; -fx-text-alignment: center; -fx-cursor: hand; -fx-font-size: 13px;"));

        btn.setOnAction(e -> {
            System.out.println("Chọn suất chiếu ID: " + sc.getIdSuatChieu());
            // TODO: Chuyển sang màn hình chọn Ghế
        });

        btn.setOnAction(e -> {
            try {
                goToBookingSeat(sc);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        return btn;
    }

    private void goToBookingSeat(SuatChieu sc) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/bookingcinema/view/dat_ve_ghe.fxml"));
        Parent root = loader.load();

        DatVeGheController controller = loader.getController();
        controller.setSuatChieuData(sc, selectedPhim);

        // SỬA ĐỔI: Dùng App.setRoot
        App.setRoot(root);
    }

    @FXML
    public void goBack() throws IOException {
        // SỬA ĐỔI: Quay về Dashboard đơn giản hơn
        App.setRoot("trang_chu_khach_hang");
    }
}

