package com.bookingcinema.controller;

import com.bookingcinema.App;
import com.bookingcinema.dao.HoaDonDAO;
import com.bookingcinema.model.HoaDon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class XemHoaDonNhanVienController {

    @FXML private TextField txtSearchIdUser;
    @FXML private Label lblMessage;
    @FXML private TableView<HoaDon> tableHoaDon;
    @FXML private TableColumn<HoaDon, Integer> colIdHoaDon;
    @FXML private TableColumn<HoaDon, String> colNgayThanhToan;
    @FXML private TableColumn<HoaDon, String> colTrangThai;
    @FXML private TableColumn<HoaDon, String> colPhuongThuc;
    @FXML private TableColumn<HoaDon, Long> colTongTien;

    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        // Setup các cột của TableView
        colIdHoaDon.setCellValueFactory(new PropertyValueFactory<>("idHoaDon"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colPhuongThuc.setCellValueFactory(new PropertyValueFactory<>("tenPTTT"));
        colTongTien.setCellValueFactory(new PropertyValueFactory<>("tongTien"));

        // Định dạng cột NgayThanhToan
        colNgayThanhToan.setCellValueFactory(cellData -> {
            String formattedDate = cellData.getValue().getNgayThanhToan().format(formatter);
            return new javafx.beans.property.SimpleStringProperty(formattedDate);
        });
    }

    @FXML
    private void handleSearch() {
        String idNguoiDung = txtSearchIdUser.getText().trim();
        if (idNguoiDung.isEmpty()) {
            lblMessage.setText("Vui lòng nhập ID người dùng!");
            lblMessage.setStyle("-fx-text-fill: red;");
            tableHoaDon.setItems(FXCollections.observableArrayList());
            return;
        }

        List<HoaDon> hoaDonList = hoaDonDAO.getHoaDonByUserId(idNguoiDung);
        
        if (hoaDonList != null && !hoaDonList.isEmpty()) {
            ObservableList<HoaDon> data = FXCollections.observableArrayList(hoaDonList);
            tableHoaDon.setItems(data);
            lblMessage.setText("Tìm thấy " + hoaDonList.size() + " hóa đơn");
            lblMessage.setStyle("-fx-text-fill: green;");
        } else {
            tableHoaDon.setItems(FXCollections.observableArrayList());
            lblMessage.setText("Không tìm thấy hóa đơn cho người dùng: " + idNguoiDung);
            lblMessage.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void goBack() throws IOException {
        App.setRoot("trang_chu_nhan_vien");
    }
}
