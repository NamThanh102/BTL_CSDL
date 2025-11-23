package com.bookingcinema.controller;

import com.bookingcinema.App;
import com.bookingcinema.model.Phim;
import com.bookingcinema.dao.PhimDAO;
import com.bookingcinema.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TrangChuKhachHangController {

    @FXML private Label lblWelcome;
    @FXML private FlowPane flowPanePhim;

    // Các ComboBox lọc
    @FXML private ComboBox<String> cboTheLoai;
    @FXML private ComboBox<String> cboThoiLuong;
    @FXML private ComboBox<String> cboNgonNgu; // Mới thêm

    private PhimDAO phimDAO = new PhimDAO();
    private List<Phim> masterMovieList = new ArrayList<>();

    @FXML
    public void initialize() {
        if (UserSession.getInstance().getCurrentUser() != null) {
            lblWelcome.setText("Xin chào, " + UserSession.getInstance().getCurrentUser().getHoTen());
        }

        masterMovieList = phimDAO.getAllPhim();
        initFilters();
        renderMovies(masterMovieList);
    }

    private void initFilters() {
        // 1. Cấu hình Thể loại
        Set<String> genres = new HashSet<>();
        genres.add("Tất cả");
        for (Phim p : masterMovieList) {
            if (p.getTheLoai() != null && !p.getTheLoai().isEmpty()) {
                String[] parts = p.getTheLoai().split(", ");
                for (String part : parts) genres.add(part.trim());
            }
        }
        cboTheLoai.setItems(FXCollections.observableArrayList(genres));
        cboTheLoai.setValue("Tất cả");
        cboTheLoai.setOnAction(e -> applyFilter());

        // 2. Cấu hình Thời lượng
        cboThoiLuong.setItems(FXCollections.observableArrayList(
                "Tất cả", "Dưới 90 phút", "90 - 120 phút", "Trên 120 phút"
        ));
        cboThoiLuong.setValue("Tất cả");
        cboThoiLuong.setOnAction(e -> applyFilter());

        // 3. MỚI: Cấu hình Ngôn ngữ
        Set<String> languages = new HashSet<>();
        languages.add("Tất cả");
        for (Phim p : masterMovieList) {
            // Lấy ngôn ngữ từ danh sách phim
            if (p.getNgonNguChinh() != null && !p.getNgonNguChinh().isEmpty()) {
                languages.add(p.getNgonNguChinh());
            }
        }
        cboNgonNgu.setItems(FXCollections.observableArrayList(languages));
        cboNgonNgu.setValue("Tất cả");
        cboNgonNgu.setOnAction(e -> applyFilter());
    }

    // Logic lọc tổng hợp
    private void applyFilter() {
        String selectedGenre = cboTheLoai.getValue();
        String selectedDuration = cboThoiLuong.getValue();
        String selectedLanguage = cboNgonNgu.getValue(); // Lấy giá trị ngôn ngữ

        List<Phim> filteredList = masterMovieList.stream().filter(p -> {
            // Check Thể loại
            boolean matchGenre = "Tất cả".equals(selectedGenre) ||
                    (p.getTheLoai() != null && p.getTheLoai().contains(selectedGenre));

            // Check Thời lượng
            boolean matchDuration = true;
            if (!"Tất cả".equals(selectedDuration)) {
                float duration = p.getThoiLuong();
                if ("Dưới 90 phút".equals(selectedDuration)) matchDuration = duration < 90;
                else if ("90 - 120 phút".equals(selectedDuration)) matchDuration = duration >= 90 && duration <= 120;
                else if ("Trên 120 phút".equals(selectedDuration)) matchDuration = duration > 120;
            }

            // MỚI: Check Ngôn ngữ
            boolean matchLanguage = "Tất cả".equals(selectedLanguage) ||
                    (p.getNgonNguChinh() != null && p.getNgonNguChinh().equals(selectedLanguage));

            return matchGenre && matchDuration && matchLanguage;
        }).collect(Collectors.toList());

        renderMovies(filteredList);
    }

    @FXML
    private void handleResetFilter() {
        cboTheLoai.setValue("Tất cả");
        cboThoiLuong.setValue("Tất cả");
        cboNgonNgu.setValue("Tất cả"); // Reset ngôn ngữ
        renderMovies(masterMovieList);
    }

    private void renderMovies(List<Phim> listToRender) {
        flowPanePhim.getChildren().clear();
        if (listToRender.isEmpty()) {
            Label lblTrong = new Label("Không tìm thấy phim phù hợp.");
            lblTrong.setFont(Font.font(16));
            lblTrong.setPadding(new Insets(20));
            flowPanePhim.getChildren().add(lblTrong);
            return;
        }
        for (Phim p : listToRender) {
            flowPanePhim.getChildren().add(createMovieCard(p));
        }
    }

    private VBox createMovieCard(Phim p) {
        VBox card = new VBox();
        card.setPrefWidth(220);
        card.setPrefHeight(300);
        card.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setSpacing(8);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.TOP_CENTER);

        Label lblTen = new Label(p.getTen());
        lblTen.setWrapText(true);
        lblTen.setFont(Font.font("System", FontWeight.BOLD, 16));
        lblTen.setAlignment(Pos.CENTER);
        lblTen.setStyle("-fx-text-alignment: center;");

        Label lblThoiLuong = new Label("⏱ " + p.getThoiLuong() + " phút");
        lblThoiLuong.setStyle("-fx-text-fill: #555; -fx-font-size: 12px;");

        Label lblTheLoai = new Label("Phim: " + p.getTheLoai());
        lblTheLoai.setWrapText(true);
        lblTheLoai.setAlignment(Pos.CENTER);
        lblTheLoai.setStyle("-fx-text-fill: #007bff; -fx-font-size: 12px; -fx-text-alignment: center;");

        Label lblNgonNgu = new Label("Ngôn ngữ: " + p.getNgonNguChinh());
        lblNgonNgu.setStyle("-fx-text-fill: #555; -fx-font-style: italic; -fx-font-size: 12px;");

        Button btnBook = new Button("Đặt Vé Ngay");
        btnBook.setPrefWidth(180);
        btnBook.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        btnBook.setOnAction(e -> {
            try {
                openBookingTime(p);
            } catch (IOException ex) { ex.printStackTrace(); }
        });

        card.getChildren().addAll(lblTen, lblThoiLuong, lblTheLoai, lblNgonNgu, btnBook);
        return card;
    }

    private void openBookingTime(Phim p) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/bookingcinema/view/dat_ve_suat_chieu.fxml"));
        Parent root = loader.load();

        DatVeSuatChieuController controller = loader.getController();
        controller.setPhim(p);

        // SỬA ĐỔI: Dùng App.setRoot để giữ nguyên khung hình
        App.setRoot(root);
    }

    @FXML
    private void handleLogout() throws IOException {
        UserSession.getInstance().clearSession();
        // SỬA ĐỔI
        App.setRoot("dang_nhap");
    }

    @FXML
    private void goTolich_su() throws IOException {
        App.setRoot("lich_su");
    }

    @FXML
    private void goToUpdateInfo() throws IOException {
        // Chuyển sang màn hình cập nhật thông tin
        App.setRoot("cap_nhat_thong_tin");
    }
}

