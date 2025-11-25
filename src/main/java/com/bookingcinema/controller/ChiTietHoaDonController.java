package com.bookingcinema.controller;

import com.bookingcinema.App;
import com.bookingcinema.model.HoaDon;
import com.bookingcinema.model.NguoiDung;
import com.bookingcinema.dao.HoaDonDAO;
import com.bookingcinema.model.TicketDetailDTO;
import com.bookingcinema.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ChiTietHoaDonController {

    @FXML private Label lblMaHD;
    @FXML private Label lblNgayThanhToan;
    @FXML private Label lblTenPhim;
    @FXML private Label lblSuatChieu;
    @FXML private Label lblPhong;
    @FXML private Label lblTongTien;
    @FXML private Label lblPTTT;
    @FXML private Label lblGheDaChon;
    @FXML private Label lblTrangThai;
    @FXML private Label lblGiaVe;
    @FXML private Label lblSoLuong;

    // THÊM: Label cho thông tin khách hàng
    @FXML private Label lblTenKhachHang;
    @FXML private Label lblSdtKhachHang;
    @FXML private Label lblEmailKhachHang;

    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    public void setHoaDon(HoaDon hoaDon) {
        if (hoaDon == null) return;

        // 1. Lấy thông tin khách hàng từ UserSession
        NguoiDung khachHang = UserSession.getInstance().getCurrentUser();

        // Hiển thị thông tin khách hàng
        if (khachHang != null) {
            lblTenKhachHang.setText(khachHang.getHoTen());
            lblSdtKhachHang.setText(khachHang.getSdt() != null ? khachHang.getSdt() : "Chưa cập nhật");
            lblEmailKhachHang.setText(khachHang.getEmail() != null ? khachHang.getEmail() : "Chưa cập nhật");
        } else {
            lblTenKhachHang.setText("Không xác định");
            lblSdtKhachHang.setText("N/A");
            lblEmailKhachHang.setText("N/A");
        }

        // 2. Tải chi tiết vé
        List<TicketDetailDTO> tickets = hoaDonDAO.getTicketDetailsByHoaDonId(hoaDon.getIdHoaDon());

        if (tickets.isEmpty()) {
            lblTenPhim.setText("Không tìm thấy chi tiết vé.");
            return;
        }

        // 3. Hiển thị thông tin chính
        TicketDetailDTO firstTicket = tickets.get(0);

        lblMaHD.setText(String.valueOf(hoaDon.getIdHoaDon()));
        lblNgayThanhToan.setText(hoaDon.getNgayThanhToan().format(dtf));
        lblTrangThai.setText(hoaDon.getTrangThai());

        lblTenPhim.setText(firstTicket.getTenPhim());
        lblSuatChieu.setText(firstTicket.getThoiGianBatDau().format(dtf));
        lblPhong.setText(String.valueOf(firstTicket.getIdPhongChieu()));

        // Gán giá vé và số lượng
        lblGiaVe.setText(String.format("%,d VNĐ", firstTicket.getGiaVe()));
        lblSoLuong.setText(String.valueOf(tickets.size()));

        // 4. Hiển thị danh sách ghế và tổng tiền
        String seatList = tickets.stream()
                .map(TicketDetailDTO::getTenGhe)
                .collect(Collectors.joining(", "));

        lblGheDaChon.setText(seatList);
        lblTongTien.setText(String.format("%,d VNĐ", hoaDon.getTongTien()));
        lblPTTT.setText(hoaDon.getTenPTTT());
    }

    @FXML
    private void goBack() throws IOException {
        App.setRoot("lich_su"); // Quay lại màn hình lịch sử
    }
}