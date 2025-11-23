package com.bookingcinema.dao;

import com.bookingcinema.model.Phim;
import com.bookingcinema.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhimDAO {

    // Hàm này phải được sửa để JOIN thêm SuatChieu và tính COUNT
    public List<Phim> getAllPhim() {
        List<Phim> list = new ArrayList<>();

        // CẬP NHẬT QUERY: Thêm LEFT JOIN SuatChieu và COUNT(sc.idSuatChieu)
        String query = "SELECT p.*, GROUP_CONCAT(tl.NoiDung SEPARATOR ', ') as DanhSachTheLoai, " +
                "COUNT(sc.idSuatChieu) as ShowtimeCount " +
                "FROM Phim p " +
                "LEFT JOIN TheLoaiPhim tlp ON p.idPhim = tlp.idPhim " +
                "LEFT JOIN TheLoai tl ON tlp.idTheLoai = tl.idTheLoai " +
                "LEFT JOIN SuatChieu sc ON p.idPhim = sc.idPhim " + // NEW JOIN
                "GROUP BY p.idPhim ORDER BY p.idPhim DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Phim p = new Phim();
                p.setIdPhim(rs.getInt("idPhim"));
                p.setTen(rs.getString("Ten"));
                p.setNgayPhatHanh(rs.getDate("NgayPhatHanh").toLocalDate());
                p.setThoiLuong(rs.getFloat("ThoiLuong"));
                p.setNgonNguChinh(rs.getString("NgonNguChinh"));
                p.setNoiDung(rs.getString("NoiDung"));
                p.setIdNguoiDung(rs.getString("idNguoiDung"));
                String genres = rs.getString("DanhSachTheLoai");
                p.setTheLoai(genres == null ? "Chưa cập nhật" : genres);
                p.setShowtimeCount(rs.getInt("ShowtimeCount")); // NEW MAPPING
                list.add(p);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Lấy ID Thể loại đã gán cho một Phim
    public List<Integer> getGenreIdsByPhimId(int idPhim) {
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT idTheLoai FROM TheLoaiPhim WHERE idPhim = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idPhim);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("idTheLoai"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ids;
    }

    // Kiểm tra trùng tên Phim (dành cho cảnh báo)
    public boolean checkDuplicateTen(String tenPhim, int excludeId) {
        // Phim có thể trùng tên, nhưng ta cần kiểm tra xem có phim nào khác ngoài phim đang sửa không
        String query = "SELECT COUNT(*) FROM Phim WHERE Ten = ? AND idPhim != ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, tenPhim);
            pstmt.setInt(2, excludeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // Hàm phụ trợ: Xóa tất cả các liên kết thể loại cũ
    private void deleteExistingGenreLinks(Connection conn, int idPhim) throws SQLException {
        String query = "DELETE FROM TheLoaiPhim WHERE idPhim = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idPhim);
            pstmt.executeUpdate();
        }
    }

    // Hàm phụ trợ: Thêm mới các liên kết thể loại
    private void insertGenreLinks(Connection conn, int idPhim, List<Integer> genreIds) throws SQLException {
        if (genreIds.isEmpty()) return;
        String query = "INSERT INTO TheLoaiPhim (idTheLoai, idPhim) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            for (int idTheLoai : genreIds) {
                pstmt.setInt(1, idTheLoai);
                pstmt.setInt(2, idPhim);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    // THÊM/SỬA PHIM VÀ THỂ LOẠI (Hàm chính dùng Transaction)
    public boolean savePhim(Phim phim, List<Integer> genreIds, String idQuanLy) {
        Connection conn = null;
        PreparedStatement pstmtPhim = null;
        boolean isNew = (phim.getIdPhim() == 0);
        int generatedId = phim.getIdPhim();

        // 1. CHUẨN BỊ SQL
        String insertPhim = "INSERT INTO Phim (Ten, NgayPhatHanh, ThoiLuong, NgonNguChinh, NoiDung, idNguoiDung) VALUES (?, ?, ?, ?, ?, ?)";
        String updatePhim = "UPDATE Phim SET Ten = ?, NgayPhatHanh = ?, ThoiLuong = ?, NgonNguChinh = ?, NoiDung = ? WHERE idPhim = ?";

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            if (isNew) {
                // INSERT
                pstmtPhim = conn.prepareStatement(insertPhim, Statement.RETURN_GENERATED_KEYS);
                pstmtPhim.setString(1, phim.getTen());
                pstmtPhim.setDate(2, Date.valueOf(phim.getNgayPhatHanh()));
                pstmtPhim.setFloat(3, phim.getThoiLuong());
                pstmtPhim.setString(4, phim.getNgonNguChinh());
                pstmtPhim.setString(5, phim.getNoiDung());
                pstmtPhim.setString(6, idQuanLy);
                pstmtPhim.executeUpdate();

                // Lấy ID Phim vừa tạo
                try (ResultSet generatedKeys = pstmtPhim.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedId = generatedKeys.getInt(1);
                        phim.setIdPhim(generatedId); // Cập nhật lại ID cho đối tượng Phim
                    } else {
                        throw new SQLException("Tạo Phim thất bại, không lấy được ID.");
                    }
                }
            } else {
                // UPDATE
                pstmtPhim = conn.prepareStatement(updatePhim);
                pstmtPhim.setString(1, phim.getTen());
                pstmtPhim.setDate(2, Date.valueOf(phim.getNgayPhatHanh()));
                pstmtPhim.setFloat(3, phim.getThoiLuong());
                pstmtPhim.setString(4, phim.getNgonNguChinh());
                pstmtPhim.setString(5, phim.getNoiDung());
                pstmtPhim.setInt(6, phim.getIdPhim());
                pstmtPhim.executeUpdate();

                // 2. XÓA liên kết cũ trước khi thêm mới (chỉ chạy khi sửa)
                deleteExistingGenreLinks(conn, generatedId);
            }

            // 3. THÊM liên kết thể loại mới
            insertGenreLinks(conn, generatedId, genreIds);

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException rollbackEx) { rollbackEx.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); } catch (SQLException ignored) {} }
        }
    }

    // Xóa Phim (3.3 - Ngừng chiếu/DELETE)
    public boolean deletePhim(int idPhim) {
        String deleteTheLoaiPhim = "DELETE FROM TheLoaiPhim WHERE idPhim = ?";
        String deletePhim = "DELETE FROM Phim WHERE idPhim = ?";
        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Xóa liên kết thể loại trước
            try (PreparedStatement pstmt = conn.prepareStatement(deleteTheLoaiPhim)) {
                pstmt.setInt(1, idPhim);
                pstmt.executeUpdate();
            }

            // 2. Xóa Phim
            try (PreparedStatement pstmt = conn.prepareStatement(deletePhim)) {
                pstmt.setInt(1, idPhim);
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            // Lỗi FK sẽ bị bắt ở đây nếu Phim đang có Suất chiếu/Vé liên quan
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException rollbackEx) { rollbackEx.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); } catch (SQLException ignored) {} }
        }
    }
}