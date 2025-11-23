package com.bookingcinema.dao;

import com.bookingcinema.model.Phim;
import com.bookingcinema.model.PhongChieu;
import com.bookingcinema.model.SuatChieu;
import com.bookingcinema.utils.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SuatChieuDAO {

    public List<SuatChieu> getSuatChieuByPhim(int idPhim) {
        List<SuatChieu> list = new ArrayList<>();
        // Chỉ lấy các suất chiếu chưa diễn ra
        String query = "SELECT * FROM SuatChieu WHERE idPhim = ? AND ThoiGianBatDau > NOW() ORDER BY ThoiGianBatDau ASC";

        try (Connection conn = com.bookingcinema.utils.DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idPhim);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                SuatChieu sc = new SuatChieu();
                sc.setIdSuatChieu(rs.getInt("idSuatChieu"));
                sc.setGiaVe(rs.getInt("GiaVe"));
                sc.setThoiGianBatDau(rs.getTimestamp("ThoiGianBatDau").toLocalDateTime());
                sc.setIdPhongChieu(rs.getInt("idPhongChieu"));
                sc.setIdPhim(rs.getInt("idPhim"));
                sc.setIdNguoiDung(rs.getString("idNguoiDung"));

                // Các trường còn lại
                list.add(sc);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Lấy thông tin chi tiết Suất chiếu (Phim, Phòng, Giá) cho Manager Table
    public List<SuatChieu> getAllSuatChieuDetails() {
        List<SuatChieu> list = new ArrayList<>();
        // Query cần JOIN Phim để lấy Tên Phim (DTO)
        String query = "SELECT sc.*, p.Ten as TenPhim FROM SuatChieu sc " +
                "JOIN Phim p ON sc.idPhim = p.idPhim " +
                "ORDER BY sc.ThoiGianBatDau DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                SuatChieu sc = new SuatChieu();
                sc.setIdSuatChieu(rs.getInt("idSuatChieu"));
                sc.setGiaVe(rs.getInt("GiaVe"));
                sc.setThoiGianBatDau(rs.getTimestamp("ThoiGianBatDau").toLocalDateTime());
                sc.setIdPhongChieu(rs.getInt("idPhongChieu"));
                sc.setIdPhim(rs.getInt("idPhim"));
                sc.setIdNguoiDung(rs.getString("idNguoiDung"));
                sc.setTenPhim(rs.getString("TenPhim"));
                list.add(sc);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Helper: Lấy đối tượng Phim theo ID (cho fillForm)
    public Phim getPhimById(int idPhim) {
        PhimDAO phimDAO = new PhimDAO();
        return phimDAO.getAllPhim().stream().filter(p -> p.getIdPhim() == idPhim).findFirst().orElse(null);
    }

    // Helper: Lấy đối tượng Phòng theo ID (cho fillForm)
    public PhongChieu getPhongById(int idPhong) {
        PhongChieuDAO phongDAO = new PhongChieuDAO();
        return phongDAO.getAllPhongChieu().stream().filter(p -> p.getIdPhongChieu() == idPhong).findFirst().orElse(null);
    }

    // -----------------------------------------------------
    // LOGIC VALIDATION & CRUD CHÍNH (3.4)
    // -----------------------------------------------------

    // Kiểm tra chồng chéo suất chiếu (Validation)
    public boolean isOverlap(LocalDateTime thoiGianBatDau, float thoiLuongPhim, int idPhongChieu, int excludeIdSuatChieu) {

        // 1. Tính Giờ kết thúc của suất chiếu mới
        LocalDateTime thoiGianKetThuc = thoiGianBatDau.plusMinutes((long) thoiLuongPhim);

        // 2. SQL: Lấy tất cả các suất chiếu khác trong phòng này, kèm theo Thời lượng của Phim liên quan.
        String query = "SELECT sc.ThoiGianBatDau, p.ThoiLuong FROM SuatChieu sc " +
                "JOIN Phim p ON sc.idPhim = p.idPhim " + // <-- FIX QUAN TRỌNG: JOIN để lấy Phim.ThoiLuong
                "WHERE sc.idPhongChieu = ? AND sc.idSuatChieu != ?";

        try (Connection conn = com.bookingcinema.utils.DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idPhongChieu);
            pstmt.setInt(2, excludeIdSuatChieu);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime existingStart = rs.getTimestamp("ThoiGianBatDau").toLocalDateTime();
                    float existingDuration = rs.getFloat("ThoiLuong");
                    LocalDateTime existingEnd = existingStart.plusMinutes((long) Math.ceil(existingDuration));

                    // 3. Overlap check: (Existing Start < New End) AND (New Start < Existing End)
                    if (existingStart.isBefore(thoiGianKetThuc) && thoiGianBatDau.isBefore(existingEnd)) {
                        return true; // Conflict found
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Nếu có lỗi SQL (ví dụ: khóa ngoại), ta giả định trùng để ngăn chặn save
            return true;
        }
        return false;
    }

    // Thêm/Sửa Suất Chiếu (3.4)
    public boolean saveSuatChieu(SuatChieu sc) {
        boolean isNew = (sc.getIdSuatChieu() == 0);

        if (isNew) {
            String query = "INSERT INTO SuatChieu (GiaVe, ThoiGianBatDau, idPhongChieu, idPhim, idNguoiDung) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setInt(1, sc.getGiaVe());
                pstmt.setTimestamp(2, Timestamp.valueOf(sc.getThoiGianBatDau()));
                pstmt.setInt(3, sc.getIdPhongChieu());
                pstmt.setInt(4, sc.getIdPhim());
                pstmt.setString(5, sc.getIdNguoiDung());

                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) { e.printStackTrace(); }
        } else {
            // UPDATE
            String query = "UPDATE SuatChieu SET GiaVe = ?, ThoiGianBatDau = ?, idPhongChieu = ?, idPhim = ? WHERE idSuatChieu = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setInt(1, sc.getGiaVe());
                pstmt.setTimestamp(2, Timestamp.valueOf(sc.getThoiGianBatDau()));
                pstmt.setInt(3, sc.getIdPhongChieu());
                pstmt.setInt(4, sc.getIdPhim());
                pstmt.setInt(5, sc.getIdSuatChieu());

                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return false;
    }

    // Xóa Suất Chiếu (3.4)
    public boolean deleteSuatChieu(int idSuatChieu) {
        String query = "DELETE FROM SuatChieu WHERE idSuatChieu = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idSuatChieu);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}