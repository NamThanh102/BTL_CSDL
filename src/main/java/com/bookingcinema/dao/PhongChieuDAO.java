package com.bookingcinema.dao;

import com.bookingcinema.model.PhongChieu;
import com.bookingcinema.utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PhongChieuDAO {

    public List<PhongChieu> getAllPhongChieu() {
        List<PhongChieu> list = new ArrayList<>();
        // Chỉ lấy các phòng đang hoạt động
        String query = "SELECT idPhongChieu, SucChua, TrangThai FROM PhongChieu WHERE TrangThai = 'DANGHOATDONG' ORDER BY idPhongChieu ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                PhongChieu pc = new PhongChieu();
                pc.setIdPhongChieu(rs.getInt("idPhongChieu"));
                pc.setSucChua(rs.getInt("SucChua"));
                pc.setTrangThai(rs.getString("TrangThai"));
                list.add(pc);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}