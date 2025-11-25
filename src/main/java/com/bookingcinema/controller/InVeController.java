package com.bookingcinema.controller;

import com.bookingcinema.dao.HoaDonDAO;
import com.bookingcinema.model.TicketDetailDTO;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class InVeController {

    @FXML private TextField txtSearchIdHoaDon;
    @FXML private Label lblMessage;
    @FXML private VBox vboxTicketInfo;
    @FXML private Label lblTitleThongTin;
    @FXML private VBox vboxVeDetails;
    @FXML private Button btnPrint;

    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        vboxTicketInfo.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 15;");
        vboxTicketInfo.setVisible(false);
        btnPrint.setDisable(true);
    }

    @FXML
    private void handleSearch() {
        String idHoaDonStr = txtSearchIdHoaDon.getText().trim();
        if (idHoaDonStr.isEmpty()) {
            lblMessage.setText("Vui lòng nhập ID hóa đơn!");
            lblMessage.setStyle("-fx-text-fill: red;");
            vboxTicketInfo.setVisible(false);
            btnPrint.setDisable(true);
            return;
        }

        try {
            int idHoaDon = Integer.parseInt(idHoaDonStr);
            
            List<TicketDetailDTO> tickets = hoaDonDAO.getTicketDetailsByHoaDonId(idHoaDon);
            
            if (tickets != null && !tickets.isEmpty()) {
                displayTickets(idHoaDon, tickets);
                lblMessage.setText("Tìm thấy " + tickets.size() + " vé");
                lblMessage.setStyle("-fx-text-fill: green;");
                btnPrint.setDisable(false);
            } else {
                lblMessage.setText("Không tìm thấy vé cho hóa đơn ID: " + idHoaDon);
                lblMessage.setStyle("-fx-text-fill: red;");
                vboxTicketInfo.setVisible(false);
                btnPrint.setDisable(true);
            }
        } catch (NumberFormatException e) {
            lblMessage.setText("ID hóa đơn phải là số!");
            lblMessage.setStyle("-fx-text-fill: red;");
            vboxTicketInfo.setVisible(false);
            btnPrint.setDisable(true);
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
    private void handlePrint() {
        if (vboxTicketInfo.isVisible()) {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null) {
                boolean success = job.printPage(vboxTicketInfo);
                if (success) {
                    job.endJob();
                    lblMessage.setText("In vé thành công!");
                    lblMessage.setStyle("-fx-text-fill: green;");
                } else {
                    lblMessage.setText("Lỗi in vé!");
                    lblMessage.setStyle("-fx-text-fill: red;");
                }
            }
        }
    }
}
