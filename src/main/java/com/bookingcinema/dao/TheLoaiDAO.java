package com.bookingcinema.dao;

import com.bookingcinema.model.NguoiDung;
import com.bookingcinema.model.TheLoai;
import com.bookingcinema.utils.DatabaseConnection;
import com.bookingcinema.utils.UserSession;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TheLoaiDAO {
    public List<TheLoai> getAllTheLoai() {
        List<TheLoai> list = new ArrayList<>();
        String query = "SELECT tl.idTheLoai, tl.NoiDung, COUNT(tlp.idPhim) as SoPhim " +
                "FROM TheLoai tl " +
                "LEFT JOIN TheLoaiPhim tlp ON tl.idTheLoai = tlp.idTheLoai " +
                "GROUP BY tl.idTheLoai, tl.NoiDung " +
                "ORDER BY tl.NoiDung ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                TheLoai tl = new TheLoai();
                tl.setIdTheLoai(rs.getInt("idTheLoai"));
                tl.setNoiDung(rs.getString("NoiDung"));
                tl.setSoPhim(rs.getInt("SoPhim"));
                list.add(tl);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertTheLoai(TheLoai theLoai) {
        // 1. Lấy idNguoiDung hiện tại từ UserSession
        NguoiDung currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser == null) {
            System.err.println("Lỗi: Không tìm thấy thông tin người dùng trong session.");
            return false;
        }
        String idNguoiDung = currentUser.getIdNguoiDung();

        // 2. Cập nhật truy vấn để bao gồm idNguoiDung
        String query = "INSERT INTO TheLoai (NoiDung, idNguoiDung) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, theLoai.getNoiDung());
            pstmt.setString(2, idNguoiDung); // Thêm idNguoiDung vào truy vấn
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTheLoai(TheLoai theLoai) {
        String query = "UPDATE TheLoai SET NoiDung = ? WHERE idTheLoai = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, theLoai.getNoiDung());
            pstmt.setInt(2, theLoai.getIdTheLoai());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTheLoai(int idTheLoai) {
        // Xóa các liên kết trong TheLoaiPhim trước
        String deleteLinks = "DELETE FROM TheLoaiPhim WHERE idTheLoai = ?";
        String deleteTheLoai = "DELETE FROM TheLoai WHERE idTheLoai = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt1 = conn.prepareStatement(deleteLinks);
                 PreparedStatement pstmt2 = conn.prepareStatement(deleteTheLoai)) {

                pstmt1.setInt(1, idTheLoai);
                pstmt1.executeUpdate();

                pstmt2.setInt(1, idTheLoai);
                int result = pstmt2.executeUpdate();

                conn.commit();
                return result > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public TheLoai getTheLoaiById(int idTheLoai) {
        String query = "SELECT * FROM TheLoai WHERE idTheLoai = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idTheLoai);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                TheLoai tl = new TheLoai();
                tl.setIdTheLoai(rs.getInt("idTheLoai"));
                tl.setNoiDung(rs.getString("NoiDung"));
                // Giả sử bạn cần lấy idNguoiDung khi lấy thể loại
                tl.setIdNguoiDung(rs.getString("idNguoiDung"));
                return tl;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean checkDuplicateNoiDung(String noiDung, int excludeId) {
        String query = "SELECT COUNT(*) FROM TheLoai WHERE NoiDung = ? AND idTheLoai != ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, noiDung);
            pstmt.setInt(2, excludeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}