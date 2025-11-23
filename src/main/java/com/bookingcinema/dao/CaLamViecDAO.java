package com.bookingcinema.dao;

import com.bookingcinema.model.CaLamViec;
import com.bookingcinema.utils.DatabaseConnection;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CaLamViecDAO {

    public List<CaLamViec> getAllCaLamViec() {
        List<CaLamViec> list = new ArrayList<>();
        String query = "SELECT * FROM CaLamViec ORDER BY GioBatDau";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                CaLamViec ca = new CaLamViec();
                ca.setIdCaLamViec(rs.getInt("idCaLamViec"));
                ca.setNoiDung(rs.getString("NoiDung"));
                ca.setGioBatDau(rs.getTime("GioBatDau").toLocalTime());
                ca.setGioKetThuc(rs.getTime("GioKetThuc").toLocalTime());
                list.add(ca);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertCaLamViec(CaLamViec ca) {
        String query = "INSERT INTO CaLamViec (NoiDung, GioBatDau, GioKetThuc) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, ca.getNoiDung());
            pstmt.setTime(2, Time.valueOf(ca.getGioBatDau()));
            pstmt.setTime(3, Time.valueOf(ca.getGioKetThuc()));
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCaLamViec(CaLamViec ca) {
        String query = "UPDATE CaLamViec SET NoiDung = ?, GioBatDau = ?, GioKetThuc = ? WHERE idCaLamViec = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, ca.getNoiDung());
            pstmt.setTime(2, Time.valueOf(ca.getGioBatDau()));
            pstmt.setTime(3, Time.valueOf(ca.getGioKetThuc()));
            pstmt.setInt(4, ca.getIdCaLamViec());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCaLamViec(int idCaLamViec) {
        String query = "DELETE FROM CaLamViec WHERE idCaLamViec = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idCaLamViec);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public CaLamViec getCaLamViecById(int idCaLamViec) {
        String query = "SELECT * FROM CaLamViec WHERE idCaLamViec = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idCaLamViec);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                CaLamViec ca = new CaLamViec();
                ca.setIdCaLamViec(rs.getInt("idCaLamViec"));
                ca.setNoiDung(rs.getString("NoiDung"));
                ca.setGioBatDau(rs.getTime("GioBatDau").toLocalTime());
                ca.setGioKetThuc(rs.getTime("GioKetThuc").toLocalTime());
                return ca;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
