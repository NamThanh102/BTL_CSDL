package com.bookingcinema.dao;

import com.bookingcinema.model.Ghe;
import com.bookingcinema.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GheDAO {

    // Lấy danh sách tất cả ghế trong phòng, sắp xếp theo Hàng và Cột
    public List<Ghe> getGheByPhong(int idPhongChieu) {
        List<Ghe> list = new ArrayList<>();
        String query = "SELECT * FROM Ghe WHERE idPhongChieu = ? ORDER BY Hang, Cot";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idPhongChieu);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Ghe g = new Ghe();
                g.setIdGhe(rs.getInt("idGhe"));
                g.setTrangThai(rs.getString("TrangThai"));
                g.setIdPhongChieu(rs.getInt("idPhongChieu"));
                g.setHang(rs.getString("Hang"));
                g.setCot(rs.getInt("Cot"));
                list.add(g);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy danh sách ID các ghế đã được đặt cho suất chiếu này
    // (Dựa vào bảng VeXemPhim)
    public List<Integer> getGheDaDat(int idSuatChieu) {
        List<Integer> listDaDat = new ArrayList<>();
        // Lấy vé có trạng thái hợp lệ (không phải HETHAN hoặc DAHUY - tuỳ logic, ở đây check vé tồn tại)
        String query = "SELECT idGhe FROM VeXemPhim WHERE idSuatChieu = ? AND TrangThai != 'HETHAN'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idSuatChieu);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                listDaDat.add(rs.getInt("idGhe"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listDaDat;
    }
}