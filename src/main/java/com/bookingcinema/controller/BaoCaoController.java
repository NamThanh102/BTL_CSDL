package com.bookingcinema.controller;

import com.bookingcinema.model.NguoiDung;
import com.bookingcinema.model.ReportRevenue;
import com.bookingcinema.utils.DatabaseConnection;
import com.bookingcinema.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.stream.Collectors;

class ReportSummary {
    long tongSoVe = 0;
    double tongDoanhThu = 0.0;
    long tongSuatChieu = 0;
}

public class BaoCaoController {

    // Components ĐIỀU KHIỂN
    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;

    // Components HIỂN THỊ CHUNG
    @FXML private VBox vboxReportContent;
    @FXML private Label lblReportPeriod;
    @FXML private Label lblCreatedBy;
    @FXML private Label lblCreatedEmail;
    @FXML private Label lblCreatedDate;
    @FXML private Label lblSummaryTongDoanhThu;
    @FXML private Label lblSummaryTongSuatChieu;
    @FXML private Label lblSummaryTongSoVe;

    @FXML private TableView<ReportRevenue> tblTop5;
    @FXML private TableView<ReportRevenue> tblBottom5;

    // Các cột cho Top/Bottom 5
    @FXML private TableColumn<ReportRevenue, Integer> colTopIdPhim;
    @FXML private TableColumn<ReportRevenue, String> colTopTenPhim;
    @FXML private TableColumn<ReportRevenue, String> colTopTheLoai;
    @FXML private TableColumn<ReportRevenue, Integer> colTopTongSuatChieu;
    @FXML private TableColumn<ReportRevenue, Integer> colTopSoVe;
    @FXML private TableColumn<ReportRevenue, Float> colTopDoanhThu;
    @FXML private TableColumn<ReportRevenue, Float> colTopTiTrong;
    // Bottom 5
    @FXML private TableColumn<ReportRevenue, Integer> colBottomIdPhim;
    @FXML private TableColumn<ReportRevenue, String> colBottomTenPhim;
    @FXML private TableColumn<ReportRevenue, String> colBottomTheLoai;
    @FXML private TableColumn<ReportRevenue, Integer> colBottomTongSuatChieu;
    @FXML private TableColumn<ReportRevenue, Integer> colBottomSoVe;
    @FXML private TableColumn<ReportRevenue, Float> colBottomDoanhThu;
    @FXML private TableColumn<ReportRevenue, Float> colBottomTiTrong;

    @FXML
    public void initialize() {
        setupTableView(tblTop5);
        setupTableView(tblBottom5);

        dpStartDate.setValue(LocalDate.now().minusMonths(1));
        dpEndDate.setValue(LocalDate.now());

        // ẨN NỘI DUNG BÁO CÁO NGAY KHI KHỞI TẠO
        if (vboxReportContent != null) {
            vboxReportContent.setVisible(false);
        }
    }

    private void setupTableView(TableView<ReportRevenue> tableView) {
        if (tableView == null) return;

        TableColumn<ReportRevenue, Integer> colIdPhim;
        TableColumn<ReportRevenue, String> colTenPhim;
        TableColumn<ReportRevenue, String> colTheLoai;
        TableColumn<ReportRevenue, Integer> colTongSuatChieu;
        TableColumn<ReportRevenue, Integer> colSoVe;
        TableColumn<ReportRevenue, Float> colDoanhThu;
        TableColumn<ReportRevenue, Float> colTiTrong;

        // Gán các cột theo fx:id
        if (tableView.getId().equals("tblTop5")) {
            colIdPhim = colTopIdPhim;
            colTenPhim = colTopTenPhim;
            colTheLoai = colTopTheLoai;
            colTongSuatChieu = colTopTongSuatChieu;
            colSoVe = colTopSoVe;
            colDoanhThu = colTopDoanhThu;
            colTiTrong = colTopTiTrong;
        } else { // tblBottom5
            colIdPhim = colBottomIdPhim;
            colTenPhim = colBottomTenPhim;
            colTheLoai = colBottomTheLoai;
            colTongSuatChieu = colBottomTongSuatChieu;
            colSoVe = colBottomSoVe;
            colDoanhThu = colBottomDoanhThu;
            colTiTrong = colBottomTiTrong;
        }

        // Thực hiện ánh xạ PropertyValueFactory
        if (colIdPhim != null) colIdPhim.setCellValueFactory(new PropertyValueFactory<>("idPhim"));
        if (colTenPhim != null) colTenPhim.setCellValueFactory(new PropertyValueFactory<>("tenPhim"));
        if (colTheLoai != null) colTheLoai.setCellValueFactory(new PropertyValueFactory<>("theLoai"));
        if (colTongSuatChieu != null) colTongSuatChieu.setCellValueFactory(new PropertyValueFactory<>("tongSuatChieu"));
        if (colSoVe != null) colSoVe.setCellValueFactory(new PropertyValueFactory<>("soLuongVe"));
        if (colDoanhThu != null) {
            colDoanhThu.setCellValueFactory(new PropertyValueFactory<>("tongDoanhThu"));
            formatCurrencyColumn(colDoanhThu);
        }
        if (colTiTrong != null) {
            colTiTrong.setCellValueFactory(new PropertyValueFactory<>("tiTrong"));
            formatPercentageColumn(colTiTrong);
        }
    }

    private void formatCurrencyColumn(TableColumn<ReportRevenue, Float> column) {
        column.setCellFactory(tc -> new TableCell<ReportRevenue, Float>() {
            @Override
            protected void updateItem(Float revenue, boolean empty) {
                super.updateItem(revenue, empty);
                setText((empty || revenue == null) ? null : String.format("%,.0f", revenue));
            }
        });
    }

    private void formatPercentageColumn(TableColumn<ReportRevenue, Float> column) {
        column.setCellFactory(tc -> new TableCell<ReportRevenue, Float>() {
            @Override
            protected void updateItem(Float percentage, boolean empty) {
                super.updateItem(percentage, empty);
                setText((empty || percentage == null) ? null : String.format("%,.2f %%", percentage));
            }
        });
    }

    @FXML
    public void handleGenerateReport() {
        generateReport();
        // HIỂN THỊ BÁO CÁO SAU KHI TẠO
        if (vboxReportContent != null) {
            vboxReportContent.setVisible(true);
        }
    }

    @FXML
    public void handleCloseReport() {
        // ẨN BÁO CÁO KHI NHẤN NÚT ĐÓNG
        if (vboxReportContent != null) {
            vboxReportContent.setVisible(false);
        }
    }

    private void generateReport() {
        LocalDate startDate = dpStartDate.getValue();
        LocalDate endDate = dpEndDate.getValue();

        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            new Alert(Alert.AlertType.ERROR, "Vui lòng chọn ngày bắt đầu hợp lệ và nhỏ hơn hoặc bằng ngày kết thúc.").showAndWait();
            if (vboxReportContent != null) vboxReportContent.setVisible(false);
            return;
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        // Hiển thị khoảng thời gian báo cáo
        lblReportPeriod.setText(String.format("Thực hiện từ ngày: %s đến ngày: %s",
                startDate.format(dateFormatter),
                endDate.format(dateFormatter)));

        // Lấy thông tin người dùng từ UserSession
        NguoiDung currentUser = UserSession.getInstance().getCurrentUser();

        // Hiển thị thông tin người tạo báo cáo
        if (currentUser != null) {
            String vaiTro = currentUser.getVaiTro();
            String vaiTroDisplay = "";

            // Hiển thị vai trò dễ đọc hơn
            switch (vaiTro) {
                case "QUANLY":
                    vaiTroDisplay = "Quản lý";
                    break;
                case "NHANVIEN":
                    vaiTroDisplay = "Nhân viên";
                    break;
                case "KHACHHANG":
                    vaiTroDisplay = "Khách hàng";
                    break;
                default:
                    vaiTroDisplay = vaiTro;
                    break;
            }

            System.out.println(currentUser.getHoTen());
            System.out.println(vaiTroDisplay);
            lblCreatedBy.setText("Người tạo: " + currentUser.getHoTen() + " (" + vaiTroDisplay + ")");
            lblCreatedEmail.setText("Email: " + currentUser.getEmail());
        } else {
            lblCreatedBy.setText("Người tạo: Không xác định");
        }

        // Hiển thị thời gian tạo báo cáo
        lblCreatedDate.setText("Thời gian tạo: " + LocalDateTime.now().format(dateTimeFormatter));

        ReportSummary summary = new ReportSummary();
        ObservableList<ReportRevenue> fullReport = fetchFullReport(startDate, endDate, summary);

        updateSummaryLabels(summary);
        analyzeTopBottomMovies(fullReport);
    }

    private void updateSummaryLabels(ReportSummary summary) {
        lblSummaryTongDoanhThu.setText(String.format("%,.0f VNĐ", summary.tongDoanhThu));
        lblSummaryTongSuatChieu.setText(String.format("%,d suất", summary.tongSuatChieu));
        lblSummaryTongSoVe.setText(String.format("%,d vé", summary.tongSoVe));
    }

    private void analyzeTopBottomMovies(ObservableList<ReportRevenue> fullReport) {
        if (fullReport.isEmpty()) {
            tblTop5.getItems().clear();
            tblBottom5.getItems().clear();
            return;
        }

        double totalRevenue = fullReport.stream().mapToDouble(ReportRevenue::getTongDoanhThu).sum();
        fullReport.forEach(item -> {
            float tiTrong = (totalRevenue > 0) ? (float) ((item.getTongDoanhThu() / totalRevenue) * 100) : 0.0f;
            item.tiTrongProperty().set(tiTrong);
        });

        ObservableList<ReportRevenue> top5List = fullReport.stream()
                .sorted(Comparator.comparingDouble(ReportRevenue::getTongDoanhThu).reversed())
                .limit(5)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        ObservableList<ReportRevenue> bottom5List = fullReport.stream()
                .sorted(Comparator.comparingDouble(ReportRevenue::getTongDoanhThu))
                .limit(5)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        tblTop5.setItems(top5List);
        tblBottom5.setItems(bottom5List);
    }

    private ObservableList<ReportRevenue> fetchFullReport(LocalDate startDate, LocalDate endDate, ReportSummary summary) {
        ObservableList<ReportRevenue> reportList = FXCollections.observableArrayList();
        Connection conn = null;

        String sql = "SELECT p.idPhim, p.Ten, " +
                "COUNT(DISTINCT sc.idSuatChieu) AS TongSuatChieu, " +
                "COUNT(v.idVeXemPhim) AS SoLuongVe, " +
                "COALESCE(SUM(sc.GiaVe), 0) AS TongDoanhThu, " +
                "GROUP_CONCAT(DISTINCT t.NoiDung SEPARATOR ', ') AS TheLoaiList " +
                "FROM Phim p " +
                "LEFT JOIN TheLoaiPhim tp ON p.idPhim = tp.idPhim " +
                "LEFT JOIN TheLoai t ON tp.idTheLoai = t.idTheLoai " +
                "LEFT JOIN SuatChieu sc ON p.idPhim = sc.idPhim " +
                "LEFT JOIN VeXemPhim v ON sc.idSuatChieu = v.idSuatChieu " +
                "LEFT JOIN HoaDon h ON v.idHoaDon = h.idHoaDon " +
                "    AND h.TrangThai = 'DATHANHTOAN' " +
                "    AND h.NgayThanhToan >= ? " +
                "    AND h.NgayThanhToan < ? " +
                "GROUP BY p.idPhim, p.Ten " +
                "ORDER BY TongDoanhThu DESC";

        try {
            conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setObject(1, java.sql.Timestamp.valueOf(startDate.atStartOfDay()));
            stmt.setObject(2, java.sql.Timestamp.valueOf(endDate.plusDays(1).atStartOfDay()));

            ResultSet rs = stmt.executeQuery();

            long totalVe = 0;
            double totalRevenue = 0.0;
            long totalSuatChieu = 0;

            while (rs.next()) {
                int idPhim = rs.getInt("idPhim");
                String tenPhim = rs.getString("Ten");
                String theLoai = rs.getString("TheLoaiList");
                int tongSuatChieu = rs.getInt("TongSuatChieu");
                int soLuongVe = rs.getInt("SoLuongVe");
                float tongDoanhThu = rs.getFloat("TongDoanhThu");

                reportList.add(new ReportRevenue(idPhim, tenPhim, theLoai, tongSuatChieu, soLuongVe, tongDoanhThu, 0.0f));

                totalVe += soLuongVe;
                totalRevenue += tongDoanhThu;
                totalSuatChieu += tongSuatChieu;
            }

            summary.tongSoVe = totalVe;
            summary.tongDoanhThu = totalRevenue;
            summary.tongSuatChieu = totalSuatChieu;

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Lỗi truy vấn dữ liệu báo cáo chi tiết: " + e.getMessage()).showAndWait();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return reportList;
    }
}