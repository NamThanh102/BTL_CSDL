package com.bookingcinema.controller;

import com.bookingcinema.dao.HoaDonDAO;
import com.bookingcinema.model.TicketDetailDTO;
import com.bookingcinema.model.NguoiDung;
import com.bookingcinema.utils.UserSession;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InVeController {

    @FXML private TextField txtSearchIdHoaDon;
    @FXML private Label lblMessage;
    @FXML private VBox vboxTicketInfo;
    @FXML private Label lblTitleThongTin;
    @FXML private VBox vboxVeDetails;
    @FXML private Button btnExportPDF;

    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private int currentIdHoaDon = 0;
    private List<TicketDetailDTO> currentTickets = null;

    @FXML
    public void initialize() {
        vboxTicketInfo.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 15;");
        vboxTicketInfo.setVisible(false);
        btnExportPDF.setDisable(true);
    }

    @FXML
    private void handleSearch() {
        String idHoaDonStr = txtSearchIdHoaDon.getText().trim();
        if (idHoaDonStr.isEmpty()) {
            lblMessage.setText("Vui lòng nhập ID hóa đơn!");
            lblMessage.setStyle("-fx-text-fill: red;");
            vboxTicketInfo.setVisible(false);
            btnExportPDF.setDisable(true);
            return;
        }

        try {
            int idHoaDon = Integer.parseInt(idHoaDonStr);
            
            List<TicketDetailDTO> tickets = hoaDonDAO.getTicketDetailsByHoaDonId(idHoaDon);
            
            if (tickets != null && !tickets.isEmpty()) {
                currentIdHoaDon = idHoaDon;
                currentTickets = tickets;
                displayTickets(idHoaDon, tickets);
                lblMessage.setText("Tìm thấy " + tickets.size() + " vé");
                lblMessage.setStyle("-fx-text-fill: green;");
                btnExportPDF.setDisable(false);
            } else {
                lblMessage.setText("Không tìm thấy vé cho hóa đơn ID: " + idHoaDon);
                lblMessage.setStyle("-fx-text-fill: red;");
                vboxTicketInfo.setVisible(false);
                btnExportPDF.setDisable(true);
            }
        } catch (NumberFormatException e) {
            lblMessage.setText("ID hóa đơn phải là số!");
            lblMessage.setStyle("-fx-text-fill: red;");
            vboxTicketInfo.setVisible(false);
            btnExportPDF.setDisable(true);
        }
    }

    private void displayTickets(int idHoaDon, List<TicketDetailDTO> tickets) {
        vboxVeDetails.getChildren().clear();

        Label titleLabel = new Label("THÔNG TIN HÓA ĐƠN VÉ XEM PHIM");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setStyle("-fx-text-fill: #d32f2f;");
        vboxVeDetails.getChildren().add(titleLabel);

        Label hoaDonLabel = new Label("Mã hóa đơn: " + idHoaDon);
        hoaDonLabel.setFont(Font.font(14));
        vboxVeDetails.getChildren().add(hoaDonLabel);

        VBox.setMargin(titleLabel, new Insets(0, 0, 15, 0));
        VBox.setMargin(hoaDonLabel, new Insets(0, 0, 15, 0));

        int ticketNumber = 1;
        for (TicketDetailDTO ticket : tickets) {
            VBox ticketBox = createTicketBox(ticketNumber++, ticket);
            vboxVeDetails.getChildren().add(ticketBox);
        }

        vboxTicketInfo.setVisible(true);
    }

    private VBox createTicketBox(int ticketNum, TicketDetailDTO ticket) {
        VBox box = new VBox();
        box.setStyle("-fx-border-color: #bbb; -fx-border-width: 1; -fx-padding: 10; -fx-spacing: 5;");
        box.setPadding(new Insets(10));

        Label ticketTitleLabel = new Label("VÉ " + ticketNum);
        ticketTitleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        ticketTitleLabel.setStyle("-fx-text-fill: #2196F3;");

        Label tenPhimLabel = new Label("Tên phim: " + ticket.getTenPhim());
        Label thoiGianLabel = new Label("Thời gian chiếu: " + ticket.getThoiGianBatDau().format(formatter));
        Label phongLabel = new Label("Phòng chiếu: P" + String.format("%03d", ticket.getIdPhongChieu()));
        Label gheLabel = new Label("Ghế: " + ticket.getTenGhe());
        Label giaLabel = new Label("Giá vé: " + String.format("%,d VNĐ", ticket.getGiaVe()));
        Label trangThaiLabel = new Label("Trạng thái: " + ticket.getTrangThaiVe());
        trangThaiLabel.setStyle("-fx-text-fill: #4caf50; -fx-font-weight: bold;");

        box.getChildren().addAll(
            ticketTitleLabel,
            tenPhimLabel,
            thoiGianLabel,
            phongLabel,
            gheLabel,
            giaLabel,
            trangThaiLabel
        );

        VBox.setMargin(box, new Insets(0, 0, 15, 0));
        return box;
    }

    @FXML
    private void handleExportPDF() {
        if (currentTickets == null || currentTickets.isEmpty()) {
            lblMessage.setText("Không có dữ liệu để xuất PDF!");
            lblMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu phiếu thanh toán");
        fileChooser.setInitialFileName("PhieuThanhToan_HD" + currentIdHoaDon + ".pdf");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        File file = fileChooser.showSaveDialog(btnExportPDF.getScene().getWindow());
        if (file != null) {
            try {
                generatePDF(file.getAbsolutePath());
                lblMessage.setText("Xuất PDF thành công: " + file.getName());
                lblMessage.setStyle("-fx-text-fill: green;");
            } catch (IOException e) {
                lblMessage.setText("Lỗi xuất PDF: " + e.getMessage());
                lblMessage.setStyle("-fx-text-fill: red;");
                e.printStackTrace();
            }
        }
    }

    private void generatePDF(String filePath) throws IOException {
        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        try {
            // Load font
            InputStream fontStream = getClass().getResourceAsStream("/fonts/DejaVuSans.ttf");
            InputStream boldFontStream = getClass().getResourceAsStream("/fonts/DejaVuSans-Bold.ttf");
            
            PdfFont font = PdfFontFactory.createFont(fontStream.readAllBytes(), PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
            PdfFont boldFont = PdfFontFactory.createFont(boldFontStream.readAllBytes(), PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
            
            // Tiêu đề
            Paragraph title = new Paragraph("PHIẾU THANH TOÁN (HÓA ĐƠN)")
                .setFont(boldFont)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(18);
            document.add(title);

            Paragraph subtitle = new Paragraph("Hệ thống Bán vé xem phim Online - Nhóm 13")
                .setFont(font)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(12);
            document.add(subtitle);
            document.add(new Paragraph("\n"));

            // Thông tin khách hàng và hóa đơn
            TicketDetailDTO firstTicket = currentTickets.get(0);
            NguoiDung currentUser = UserSession.getInstance().getCurrentUser();
            
            document.add(new Paragraph("THÔNG TIN KHÁCH HÀNG").setFont(boldFont).setFontSize(12));
            document.add(new Paragraph("Họ tên: " + (currentUser != null ? currentUser.getHoTen() : "N/A")).setFont(font).setFontSize(10));
            document.add(new Paragraph("Số điện thoại: " + (currentUser != null ? currentUser.getSdt() : "N/A")).setFont(font).setFontSize(10));
            document.add(new Paragraph("Email: " + (currentUser != null ? currentUser.getEmail() : "N/A")).setFont(font).setFontSize(10));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Mã HD: " + currentIdHoaDon).setFont(font).setFontSize(10));
            document.add(new Paragraph("Ngày TT: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"))).setFont(font).setFontSize(10));
            document.add(new Paragraph("\n"));

            // Chi tiết đặt vé
            document.add(new Paragraph("CHI TIẾT ĐẶT VÉ").setFont(boldFont).setFontSize(12));
            
            document.add(new Paragraph("Phòng: " + firstTicket.getIdPhongChieu()).setFont(font).setFontSize(10));
            document.add(new Paragraph("Thời điểm: " + firstTicket.getThoiGianBatDau().format(formatter)).setFont(font).setFontSize(10));
            document.add(new Paragraph("Số lượng: " + currentTickets.size()).setFont(font).setFontSize(10));
            document.add(new Paragraph("Phim: " + firstTicket.getTenPhim()).setFont(font).setFontSize(10));
            document.add(new Paragraph("\n"));

            // Ghế đã chọn
            document.add(new Paragraph("GHẾ ĐÃ CHỌN:").setFont(boldFont).setFontSize(10));
            StringBuilder gheList = new StringBuilder();
            for (TicketDetailDTO ticket : currentTickets) {
                if (gheList.length() > 0) gheList.append(", ");
                gheList.append(ticket.getTenGhe());
            }
            document.add(new Paragraph(gheList.toString()).setFont(font).setFontSize(10));
            document.add(new Paragraph("\n"));

            // Tổng kết
            long tongTien = currentTickets.stream().mapToLong(TicketDetailDTO::getGiaVe).sum();
            
            document.add(new Paragraph("TỔNG KẾT").setFont(boldFont).setFontSize(12));
            
            Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
            summaryTable.setWidth(UnitValue.createPercentValue(100));
            
            summaryTable.addCell(new Cell().add(new Paragraph("Phương thức:").setFont(font).setFontSize(10)));
            summaryTable.addCell(new Cell().add(new Paragraph("DATHANHTOAN").setFont(font).setFontSize(10)));
            
            summaryTable.addCell(new Cell().add(new Paragraph("TỔNG CỘNG:").setFont(boldFont).setFontSize(12)));
            summaryTable.addCell(new Cell().add(new Paragraph(String.format("%,d VNĐ", tongTien))
                .setFont(boldFont)
                .setFontSize(12)
                .setFontColor(com.itextpdf.kernel.colors.ColorConstants.RED)));
            
            document.add(summaryTable);

        } finally {
            document.close();
        }
    }
}
