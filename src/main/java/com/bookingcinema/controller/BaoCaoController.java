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
import javafx.stage.FileChooser;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.PdfEncodings;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    @FXML
    public void handleExportPDF() {
        if (vboxReportContent == null || !vboxReportContent.isVisible()) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng tạo báo cáo trước khi xuất PDF!").showAndWait();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu Báo Cáo PDF");
        fileChooser.setInitialFileName("BaoCaoDoanhThu_" + LocalDate.now() + ".pdf");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        File file = fileChooser.showSaveDialog(vboxReportContent.getScene().getWindow());
        if (file != null) {
            try {
                generatePDF(file.getAbsolutePath());
                new Alert(Alert.AlertType.INFORMATION, "Xuất PDF thành công!\nĐường dẫn: " + file.getAbsolutePath()).showAndWait();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Lỗi khi xuất PDF: " + e.getMessage()).showAndWait();
                e.printStackTrace();
            }
        }
    }

    private void generatePDF(String filePath) throws IOException {
        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        try {
            // Load custom font from resources
            InputStream fontStream = getClass().getResourceAsStream("/fonts/DejaVuSans.ttf");
            InputStream boldFontStream = getClass().getResourceAsStream("/fonts/DejaVuSans-Bold.ttf");
            
            PdfFont font = PdfFontFactory.createFont(fontStream.readAllBytes(), PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
            PdfFont boldFont = PdfFontFactory.createFont(boldFontStream.readAllBytes(), PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
            
            // Tiêu đề
            Paragraph title = new Paragraph("CÔNG TY BÁN VÉ XEM PHIM ONLINE - NHÓM 13")
                .setFont(boldFont)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(16);
            document.add(title);

            Paragraph subtitle = new Paragraph("BÁO CÁO DOANH THU")
                .setFont(boldFont)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(14);
            document.add(subtitle);

            // Thông tin báo cáo
            document.add(new Paragraph(lblReportPeriod.getText()).setFont(font).setFontSize(10));
            document.add(new Paragraph(lblCreatedBy.getText()).setFont(font).setFontSize(10));
            document.add(new Paragraph(lblCreatedEmail.getText()).setFont(font).setFontSize(10));
            document.add(new Paragraph(lblCreatedDate.getText()).setFont(font).setFontSize(10));
            document.add(new Paragraph("\n"));

            // Phần 1: Tổng hợp
            document.add(new Paragraph("1. CÁC CHỈ SỐ TỔNG HỢP").setFont(boldFont).setFontSize(12));
            
            Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}));
            summaryTable.setWidth(UnitValue.createPercentValue(100));
            summaryTable.setFontSize(9);
            
            summaryTable.addHeaderCell(new Cell().add(new Paragraph("TỔNG DOANH THU").setFont(boldFont)));
            summaryTable.addHeaderCell(new Cell().add(new Paragraph("SỐ VÉ BÁN RA").setFont(boldFont)));
            summaryTable.addHeaderCell(new Cell().add(new Paragraph("SUẤT CHIẾU ĐÃ CÓ GIAO DỊCH").setFont(boldFont)));
            
            summaryTable.addCell(new Cell().add(new Paragraph(lblSummaryTongDoanhThu.getText()).setFont(font)));
            summaryTable.addCell(new Cell().add(new Paragraph(lblSummaryTongSoVe.getText()).setFont(font)));
            summaryTable.addCell(new Cell().add(new Paragraph(lblSummaryTongSuatChieu.getText()).setFont(font)));
            
            document.add(summaryTable);
            document.add(new Paragraph("\n"));

            // Phần 2: Top 5 phim có doanh thu cao nhất
            document.add(new Paragraph("2. TOP 5 PHIM CÓ DOANH THU CAO NHẤT").setFont(boldFont).setFontSize(12));
            
            Table top5Table = new Table(UnitValue.createPercentArray(new float[]{0.8f, 2.5f, 2.5f, 1.2f, 1.2f, 1.5f, 1.3f}));
            top5Table.setWidth(UnitValue.createPercentValue(100));
            top5Table.setFontSize(8);
            
            top5Table.addHeaderCell(new Cell().add(new Paragraph("ID").setFont(boldFont)));
            top5Table.addHeaderCell(new Cell().add(new Paragraph("Tên Phim").setFont(boldFont)));
            top5Table.addHeaderCell(new Cell().add(new Paragraph("Thể Loại").setFont(boldFont)));
            top5Table.addHeaderCell(new Cell().add(new Paragraph("Suất Chiếu").setFont(boldFont)));
            top5Table.addHeaderCell(new Cell().add(new Paragraph("Vé Bán").setFont(boldFont)));
            top5Table.addHeaderCell(new Cell().add(new Paragraph("Doanh Thu").setFont(boldFont)));
            top5Table.addHeaderCell(new Cell().add(new Paragraph("Tỉ Trọng").setFont(boldFont)));

            for (ReportRevenue item : tblTop5.getItems()) {
                top5Table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getIdPhim())).setFont(font)));
                top5Table.addCell(new Cell().add(new Paragraph(item.getTenPhim()).setFont(font)));
                top5Table.addCell(new Cell().add(new Paragraph(item.getTheLoai()).setFont(font)));
                top5Table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getTongSuatChieu())).setFont(font)));
                top5Table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getSoLuongVe())).setFont(font)));
                top5Table.addCell(new Cell().add(new Paragraph(String.format("%,.0f", item.getTongDoanhThu())).setFont(font)));
                top5Table.addCell(new Cell().add(new Paragraph(String.format("%.2f %%", item.getTiTrong())).setFont(font)));
            }
            
            document.add(top5Table);
            document.add(new Paragraph("\n"));

            // Phần 3: Top 5 phim có doanh thu thấp nhất
            document.add(new Paragraph("3. TOP 5 PHIM CÓ DOANH THU THẤP NHẤT").setFont(boldFont).setFontSize(12));
            
            Table bottom5Table = new Table(UnitValue.createPercentArray(new float[]{0.8f, 2.5f, 2.5f, 1.2f, 1.2f, 1.5f, 1.3f}));
            bottom5Table.setWidth(UnitValue.createPercentValue(100));
            bottom5Table.setFontSize(8);
            
            bottom5Table.addHeaderCell(new Cell().add(new Paragraph("ID").setFont(boldFont)));
            bottom5Table.addHeaderCell(new Cell().add(new Paragraph("Tên Phim").setFont(boldFont)));
            bottom5Table.addHeaderCell(new Cell().add(new Paragraph("Thể Loại").setFont(boldFont)));
            bottom5Table.addHeaderCell(new Cell().add(new Paragraph("Suất Chiếu").setFont(boldFont)));
            bottom5Table.addHeaderCell(new Cell().add(new Paragraph("Vé Bán").setFont(boldFont)));
            bottom5Table.addHeaderCell(new Cell().add(new Paragraph("Doanh Thu").setFont(boldFont)));
            bottom5Table.addHeaderCell(new Cell().add(new Paragraph("Tỉ Trọng").setFont(boldFont)));

            for (ReportRevenue item : tblBottom5.getItems()) {
                bottom5Table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getIdPhim())).setFont(font)));
                bottom5Table.addCell(new Cell().add(new Paragraph(item.getTenPhim()).setFont(font)));
                bottom5Table.addCell(new Cell().add(new Paragraph(item.getTheLoai()).setFont(font)));
                bottom5Table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getTongSuatChieu())).setFont(font)));
                bottom5Table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getSoLuongVe())).setFont(font)));
                bottom5Table.addCell(new Cell().add(new Paragraph(String.format("%,.0f", item.getTongDoanhThu())).setFont(font)));
                bottom5Table.addCell(new Cell().add(new Paragraph(String.format("%.2f %%", item.getTiTrong())).setFont(font)));
            }
            
            document.add(bottom5Table);
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n"));

            // Phần ký tên
            Table signatureTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
            signatureTable.setWidth(UnitValue.createPercentValue(100));
            
            // Người viết
            Cell leftTitleCell = new Cell().add(new Paragraph("Người viết:").setFont(boldFont).setFontSize(11));
            leftTitleCell.setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
            
            // Người nhận
            Cell rightTitleCell = new Cell().add(new Paragraph("Người nhận:").setFont(boldFont).setFontSize(11));
            rightTitleCell.setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
            rightTitleCell.setTextAlignment(TextAlignment.CENTER);

            signatureTable.addCell(leftTitleCell);
            signatureTable.addCell(rightTitleCell);
            
            // Dòng ghi chú in nghiêng
            Cell leftNoteCell = new Cell().add(new Paragraph("(Ký, ghi rõ họ tên)").setFont(font).setFontSize(9).setItalic());
            leftNoteCell.setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
            
            Cell rightNoteCell = new Cell().add(new Paragraph("(Ký, ghi rõ họ tên)").setFont(font).setFontSize(9).setItalic());
            rightNoteCell.setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
            rightNoteCell.setTextAlignment(TextAlignment.CENTER);
            
            signatureTable.addCell(leftNoteCell);
            signatureTable.addCell(rightNoteCell);
            
            document.add(signatureTable);

        } finally {
            document.close();
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

        String sql =
                "SELECT " +
                        "   p.idPhim, " +
                        "   p.Ten, " +
                        "   (SELECT GROUP_CONCAT(tl.NoiDung SEPARATOR ', ') " +
                        "    FROM TheLoaiPhim tlp " +
                        "    JOIN TheLoai tl ON tlp.idTheLoai = tl.idTheLoai " +
                        "    WHERE tlp.idPhim = p.idPhim) AS TheLoaiList, " +
                        "   COUNT(DISTINCT CASE WHEN h.idHoaDon IS NOT NULL THEN sc.idSuatChieu END) AS TongSuatChieu, " +
                        "   COUNT(DISTINCT CASE WHEN h.idHoaDon IS NOT NULL THEN v.idVeXemPhim END) AS SoLuongVe, " +
                        "   COALESCE(SUM(CASE WHEN h.idHoaDon IS NOT NULL THEN sc.GiaVe ELSE 0 END), 0) AS TongDoanhThu " +
                        "FROM Phim p " +
                        "LEFT JOIN SuatChieu sc ON p.idPhim = sc.idPhim " +
                        "LEFT JOIN VeXemPhim v ON sc.idSuatChieu = v.idSuatChieu " +
                        "LEFT JOIN HoaDon h ON v.idHoaDon = h.idHoaDon " +
                        "   AND h.TrangThai = 'DATHANHTOAN' " +
                        "   AND h.NgayThanhToan BETWEEN ? AND ? " +
                        "GROUP BY p.idPhim, p.Ten " +
                        "HAVING TongSuatChieu > 0 " +
                        "ORDER BY TongDoanhThu DESC";
        long totalVe = 0;
        long totalSuatChieu = 0;
        double totalRevenue = 0.0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, java.sql.Timestamp.valueOf(startDate.atStartOfDay()));
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(endDate.atTime(23, 59, 59)));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int idPhim = rs.getInt("idPhim");
                String tenPhim = rs.getString("Ten");

                String theLoai = rs.getString("TheLoaiList");
                if (theLoai == null || theLoai.isEmpty()) {
                    theLoai = "N/A";
                }

                int tongSuat = rs.getInt("TongSuatChieu");
                int soVe = rs.getInt("SoLuongVe");
                float doanhThu = rs.getFloat("TongDoanhThu");

                reportList.add(new ReportRevenue(
                        idPhim, tenPhim, theLoai,
                        tongSuat, soVe, doanhThu,
                        0.0f
                ));

                totalVe += soVe;
                totalSuatChieu += tongSuat;
                totalRevenue += doanhThu;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Lỗi lấy dữ liệu: " + e.getMessage()).showAndWait();
        }

        summary.tongDoanhThu = totalRevenue;
        summary.tongSoVe = totalVe;
        summary.tongSuatChieu = totalSuatChieu;

        return reportList;
    }
}