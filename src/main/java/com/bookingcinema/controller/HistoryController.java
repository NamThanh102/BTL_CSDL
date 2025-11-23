package com.bookingcinema.controller;

import com.bookingcinema.App;
import com.bookingcinema.model.HoaDon;
import com.bookingcinema.dao.HoaDonDAO;
import com.bookingcinema.model.TicketDetailDTO;
import com.bookingcinema.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class HistoryController {

    @FXML private Label lblWelcome;
    @FXML private TableView<HoaDon> tblHoaDon;
    @FXML private TableColumn<HoaDon, Integer> colID;
    @FXML private TableColumn<HoaDon, LocalDateTime> colNgayTao;
    @FXML private TableColumn<HoaDon, String> colPTTT;
    @FXML private TableColumn<HoaDon, Long> colTongTien;
    @FXML private TableColumn<HoaDon, String> colTrangThai;
    @FXML private TableColumn<HoaDon, Void> colChiTiet; // Cột nút bấm

    @FXML private TableView<TicketDetailDTO> tblVe;
    @FXML private TableColumn<TicketDetailDTO, String> colTenPhim;
    @FXML private TableColumn<TicketDetailDTO, LocalDateTime> colGioChieu;
    @FXML private TableColumn<TicketDetailDTO, Integer> colPhong;
    @FXML private TableColumn<TicketDetailDTO, String> colGhe;
    @FXML private TableColumn<TicketDetailDTO, Integer> colGiaVe;
    @FXML private TableColumn<TicketDetailDTO, String> colTrangThaiVe;

    private HoaDonDAO hoaDonDAO = new HoaDonDAO();

    private void openInvoiceDetail(HoaDon hoaDon) throws IOException {
        FXMLLoader loader = new FXMLLoader();

        // CẬP NHẬT: Thiết lập Location rõ ràng
        loader.setLocation(App.class.getResource("/com/bookingcinema/view/invoice_detail.fxml"));

        Parent root = loader.load(); // Lệnh load sẽ chạy sau khi Location đã được set

        InvoiceDetailController controller = loader.getController();
        controller.setHoaDon(hoaDon);

        App.setRoot(root);
    }

    @FXML
    public void initialize() throws IOException {
        if (UserSession.getInstance().getCurrentUser() == null) {
            goBack();
            return;
        }

        String userId = UserSession.getInstance().getCurrentUser().getIdNguoiDung();
        lblWelcome.setText("Tài khoản: " + UserSession.getInstance().getCurrentUser().getTaiKhoan());

        // 1. CẤU HÌNH CỘT HÓA ĐƠN
        colID.setCellValueFactory(new PropertyValueFactory<>("idHoaDon"));
        colNgayTao.setCellValueFactory(new PropertyValueFactory<>("ngayThanhToan"));
        colPTTT.setCellValueFactory(new PropertyValueFactory<>("tenPTTT"));
        colTongTien.setCellValueFactory(new PropertyValueFactory<>("tongTien"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));

        // 2. CẤU HÌNH CỘT CHI TIẾT VÉ (Đã đổi tên colGioChieu -> Thời điểm chiếu)
        colTenPhim.setCellValueFactory(new PropertyValueFactory<>("tenPhim"));
        colGioChieu.setCellValueFactory(new PropertyValueFactory<>("thoiGianBatDau"));
        colPhong.setCellValueFactory(new PropertyValueFactory<>("idPhongChieu"));
        colGhe.setCellValueFactory(new PropertyValueFactory<>("tenGhe"));
        colGiaVe.setCellValueFactory(new PropertyValueFactory<>("giaVe"));
        colTrangThaiVe.setCellValueFactory(new PropertyValueFactory<>("trangThaiVe"));

        // 3. THIẾT LẬP CỘT NÚT BẤM (CellFactory)
        Callback<TableColumn<HoaDon, Void>, TableCell<HoaDon, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<HoaDon, Void> call(final TableColumn<HoaDon, Void> param) {
                final TableCell<HoaDon, Void> cell = new TableCell<>() {

                    private final Button btn = new Button("Xem hóa đơn");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            HoaDon hoaDon = getTableView().getItems().get(getIndex());
                            try {
                                openInvoiceDetail(hoaDon);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        btn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand;");
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        colChiTiet.setCellFactory(cellFactory);

        // 4. LOAD DỮ LIỆU
        loadHoaDon(userId);

        // 5. LISTENER và SẮP XẾP MẶC ĐỊNH
        tblHoaDon.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadTicketDetails(newSelection.getIdHoaDon());
            } else {
                tblVe.getItems().clear();
            }
        });

        // SẮP XẾP MẶC ĐỊNH (Theo ngày gần nhất)
        colNgayTao.setSortType(TableColumn.SortType.DESCENDING);
        tblHoaDon.getSortOrder().add(colNgayTao);
        if (!tblHoaDon.getItems().isEmpty()) {
            tblHoaDon.sort();
        }
    }

    private void loadHoaDon(String userId) {
        List<HoaDon> list = hoaDonDAO.getHoaDonByUserId(userId);
        tblHoaDon.setItems(FXCollections.observableArrayList(list));
    }

    private void loadTicketDetails(int idHoaDon) {
        List<TicketDetailDTO> list = hoaDonDAO.getTicketDetailsByHoaDonId(idHoaDon);
        tblVe.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    private void goBack() throws IOException {
        App.setRoot("customer_dashboard");
    }
}