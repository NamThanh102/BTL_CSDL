package com.bookingcinema.dao;

import com.bookingcinema.model.Ghe;
import com.bookingcinema.model.PhuongThucThanhToan;
import com.bookingcinema.model.TicketDetailDTO;
import com.bookingcinema.utils.DatabaseConnection;
import com.bookingcinema.model.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    // Lấy danh sách phương thức thanh toán
    public List<PhuongThucThanhToan> getPaymentMethods() {
        List<PhuongThucThanhToan> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM PhuongThucThanhToan")) {
            while (rs.next()) {
                list.add(new PhuongThucThanhToan(rs.getInt("idPhuongThucThanhToan"), rs.getString("NoiDung")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // XỬ LÝ GIAO DỊCH: Tạo Hóa Đơn + Tạo Vé
    public boolean createBooking(String idNguoiDung, int idSuatChieu, int idPhuongThuc, List<Ghe> gheList) {
        Connection conn = null;
        PreparedStatement stmtHoaDon = null;
        PreparedStatement stmtVe = null;

        // SỬA ĐỔI SQL: Chỉ còn NgayThanhToan
        String insertHoaDon = "INSERT INTO HoaDon (NgayThanhToan, TrangThai, idPhuongThucThanhToan, idNguoiDung) VALUES (?, ?, ?, ?)";
        String insertVe = "INSERT INTO VeXemPhim (TrangThai, idSuatChieu, idHoaDon, idGhe) VALUES (?, ?, ?, ?)";

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Tạo Hóa Đơn
            stmtHoaDon = conn.prepareStatement(insertHoaDon, Statement.RETURN_GENERATED_KEYS);
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());

            // SỬA ĐỔI: Chỉ có 4 tham số
            stmtHoaDon.setTimestamp(1, now); // 1. NgayThanhToan
            stmtHoaDon.setString(2, "DATHANHTOAN"); // 2. TrangThai
            stmtHoaDon.setInt(3, idPhuongThuc); // 3. idPhuongThucThanhToan
            stmtHoaDon.setString(4, idNguoiDung); // 4. idNguoiDung

            int affectedRows = stmtHoaDon.executeUpdate();
            if (affectedRows == 0) throw new SQLException("Tạo hóa đơn thất bại.");

            // Lấy ID Hóa đơn vừa tạo (giữ nguyên logic)
            int idHoaDon = 0;
            try (ResultSet generatedKeys = stmtHoaDon.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idHoaDon = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Không lấy được ID hóa đơn.");
                }
            }

            // 3. Tạo các Vé tương ứng (giữ nguyên logic)
            stmtVe = conn.prepareStatement(insertVe);
            for (Ghe ghe : gheList) {
                stmtVe.setString(1, "CHUASUDUNG");
                stmtVe.setInt(2, idSuatChieu);
                stmtVe.setInt(3, idHoaDon);
                stmtVe.setInt(4, ghe.getIdGhe());
                stmtVe.addBatch();
            }
            stmtVe.executeBatch();

            // 4. Commit
            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.out.println("Giao dịch thất bại. Đang Rollback...");
                    conn.rollback();
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public List<HoaDon> getHoaDonByUserId(String idNguoiDung) {
        List<HoaDon> list = new ArrayList<>();

        // Cập nhật Query:
        // - Lấy pttt.NoiDung (as TenPTTT)
        // - Tính SUM(s.GiaVe) (as TongTien)
        // - GROUP BY h.idHoaDon
        String query = "SELECT h.*, pttt.NoiDung as TenPTTT, SUM(s.GiaVe) as TongTien FROM HoaDon h " +
                "JOIN PhuongThucThanhToan pttt ON h.idPhuongThucThanhToan = pttt.idPhuongThucThanhToan " +
                "JOIN VeXemPhim v ON h.idHoaDon = v.idHoaDon " +
                "JOIN SuatChieu s ON v.idSuatChieu = s.idSuatChieu " +
                "WHERE h.idNguoiDung = ? GROUP BY h.idHoaDon ORDER BY h.NgayThanhToan DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, idNguoiDung);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setIdHoaDon(rs.getInt("idHoaDon"));
                hd.setNgayThanhToan(rs.getTimestamp("NgayThanhToan").toLocalDateTime());
                hd.setTrangThai(rs.getString("TrangThai"));

                // Ánh xạ 2 trường tính toán
                hd.setTenPTTT(rs.getString("TenPTTT")); // <--- Lấy tên PTTT
                hd.setTongTien(rs.getLong("TongTien")); // <--- Lấy Tổng tiền

                list.add(hd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Có thể bị lỗi nếu bạn chưa chạy lại script dữ liệu mẫu (đặc biệt là bảng PTTT)
        }
        return list;
    }

    // Lấy chi tiết vé (DTO) theo Mã Hóa đơn
    public List<TicketDetailDTO> getTicketDetailsByHoaDonId(int idHoaDon) {
        List<TicketDetailDTO> list = new ArrayList<>();
        // Query cần JOIN 4 bảng: VeXemPhim, SuatChieu, Phim, Ghe
        String query = "SELECT v.TrangThai AS TrangThaiVe, s.ThoiGianBatDau, s.GiaVe, s.idPhongChieu, p.Ten, g.Hang, g.Cot " +
                "FROM VeXemPhim v " +
                "JOIN SuatChieu s ON v.idSuatChieu = s.idSuatChieu " +
                "JOIN Phim p ON s.idPhim = p.idPhim " +
                "JOIN Ghe g ON v.idGhe = g.idGhe " +
                "WHERE v.idHoaDon = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idHoaDon);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                TicketDetailDTO dto = new TicketDetailDTO();
                dto.setTenPhim(rs.getString("Ten"));
                dto.setThoiGianBatDau(rs.getTimestamp("ThoiGianBatDau").toLocalDateTime());
                dto.setIdPhongChieu(rs.getInt("idPhongChieu"));
                dto.setGiaVe(rs.getInt("GiaVe"));
                dto.setTrangThaiVe(rs.getString("TrangThaiVe"));
                dto.setTenGhe(rs.getString("Hang") + rs.getInt("Cot"));
                list.add(dto);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}