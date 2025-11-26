package com.bookingcinema.dao;

import com.bookingcinema.model.NguoiDung;
import com.bookingcinema.utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NguoiDungDAO {

    // 1. Hàm kiểm tra đăng nhập
    // Do DB đã set collation là _bin, nên câu lệnh này tự động phân biệt hoa thường
    public NguoiDung checkLogin(String taiKhoan, String matKhau) {
        String query = "SELECT * FROM NguoiDung WHERE BINARY TaiKhoan = ? AND BINARY MatKhau = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, taiKhoan);
            pstmt.setString(2, matKhau);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                NguoiDung user = new NguoiDung();
                user.setIdNguoiDung(rs.getString("idNguoiDung"));
                user.setTaiKhoan(rs.getString("TaiKhoan"));
                user.setMatKhau(rs.getString("MatKhau")); // <-- FIX: Tải Mật khẩu vào Session
                user.setHoTen(rs.getString("HoTen"));

                // Tải các trường còn lại
                if (rs.getDate("NgaySinh") != null) {
                    user.setNgaySinh(rs.getDate("NgaySinh").toLocalDate());
                }
                user.setSdt(rs.getString("SDT"));
                user.setEmail(rs.getString("Email"));
                user.setVaiTro(rs.getString("VaiTro"));

                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 2. Kiểm tra sự tồn tại (Dùng cho Đăng ký)
    // Hệ thống sẽ cho phép 'abc@gmail.com' đăng ký dù 'ABC@gmail.com' đã tồn tại (vì là 2 email khác nhau)
    public String checkExist(String taiKhoan, String email, String sdt) {
        String query = "SELECT TaiKhoan, Email, SDT FROM NguoiDung WHERE TaiKhoan = ? OR Email = ? OR SDT = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, taiKhoan);
            pstmt.setString(2, email);
            pstmt.setString(3, sdt);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // So sánh chính xác (equals) thay vì equalsIgnoreCase vì DB đã phân biệt rồi
                if (rs.getString("TaiKhoan").equals(taiKhoan)) return "Tài khoản đã tồn tại!";
                if (rs.getString("Email").equals(email)) return "Email đã tồn tại!";
                if (rs.getString("SDT").equals(sdt)) return "Số điện thoại đã tồn tại!";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Lỗi kết nối CSDL!";
        }
        return null;
    }

    // 3. Đăng ký người dùng mới (Giữ nguyên)
    public boolean registerUser(NguoiDung user) {
        String generatedID = "KH" + System.currentTimeMillis();
        // Giới hạn độ dài ID nếu cần thiết để khớp với CHAR(20)
        if (generatedID.length() > 20) {
            generatedID = generatedID.substring(0, 20);
        }

        String query = "INSERT INTO NguoiDung (idNguoiDung, TaiKhoan, MatKhau, HoTen, NgaySinh, SDT, Email, VaiTro) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, generatedID);
            pstmt.setString(2, user.getTaiKhoan());
            pstmt.setString(3, user.getMatKhau());
            pstmt.setString(4, user.getHoTen());
            pstmt.setDate(5, java.sql.Date.valueOf(user.getNgaySinh()));
            pstmt.setString(6, user.getSdt());
            pstmt.setString(7, user.getEmail());
            pstmt.setString(8, "KHACHHANG");

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String checkExist(String taiKhoan, String email, String sdt, String excludeId) {
        // Kiểm tra 3 trường, loại trừ ID đang xét
        String query = "SELECT TaiKhoan, Email, SDT FROM NguoiDung WHERE (TaiKhoan = ? OR Email = ? OR SDT = ?) AND idNguoiDung != ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, taiKhoan);
            pstmt.setString(2, email);
            pstmt.setString(3, sdt);
            pstmt.setString(4, excludeId); // Loại trừ ID hiện tại

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Do DB là case sensitive, ta dùng .equals()
                if (rs.getString("TaiKhoan").equals(taiKhoan)) return "Tài khoản đã tồn tại!";
                if (rs.getString("Email").equals(email)) return "Email đã tồn tại!";
                if (rs.getString("SDT").equals(sdt)) return "Số điện thoại đã tồn tại!";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Lỗi kết nối CSDL!";
        }
        return null; // Không trùng
    }

    // Thêm hàm updateUserInfo
    public boolean updateUserInfo(NguoiDung user, String newPassword) {
        // Cập nhật tất cả các trường có thể thay đổi (Bắt buộc phải set MatKhau)
        String query = "UPDATE NguoiDung SET MatKhau = ?, HoTen = ?, NgaySinh = ?, SDT = ?, Email = ? WHERE idNguoiDung = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Tham số 1: Mật khẩu mới (hoặc mật khẩu cũ nếu không đổi)
            pstmt.setString(1, newPassword);

            // Các tham số khác
            pstmt.setString(2, user.getHoTen());
            pstmt.setDate(3, java.sql.Date.valueOf(user.getNgaySinh()));
            pstmt.setString(4, user.getSdt());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, user.getIdNguoiDung());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy tất cả người dùng
    public java.util.List<NguoiDung> getAllNguoiDung() {
        java.util.List<NguoiDung> list = new java.util.ArrayList<>();
        String query = "SELECT * FROM NguoiDung ORDER BY idNguoiDung";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                NguoiDung user = new NguoiDung();
                user.setIdNguoiDung(rs.getString("idNguoiDung"));
                user.setTaiKhoan(rs.getString("TaiKhoan"));
                user.setMatKhau(rs.getString("MatKhau"));
                user.setHoTen(rs.getString("HoTen"));
                if (rs.getDate("NgaySinh") != null) {
                    user.setNgaySinh(rs.getDate("NgaySinh").toLocalDate());
                }
                user.setSdt(rs.getString("SDT"));
                user.setEmail(rs.getString("Email"));
                user.setVaiTro(rs.getString("VaiTro"));
                if (rs.getDate("NgayBatDau") != null) {
                    user.setNgayBatDau(rs.getDate("NgayBatDau").toLocalDate());
                }
                user.setTrangThai(rs.getString("TrangThai"));
                user.setIdQuanLy(rs.getString("idQuanLy"));
                list.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm người dùng mới
    public boolean insertNguoiDung(NguoiDung user) {
        String query = "INSERT INTO NguoiDung (idNguoiDung, TaiKhoan, MatKhau, HoTen, NgaySinh, SDT, Email, VaiTro, NgayBatDau, TrangThai, idQuanLy) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, user.getIdNguoiDung());
            pstmt.setString(2, user.getTaiKhoan());
            pstmt.setString(3, user.getMatKhau());
            pstmt.setString(4, user.getHoTen());
            pstmt.setDate(5, java.sql.Date.valueOf(user.getNgaySinh()));
            pstmt.setString(6, user.getSdt());
            pstmt.setString(7, user.getEmail());
            pstmt.setString(8, user.getVaiTro());
            pstmt.setDate(9, user.getNgayBatDau() != null ? java.sql.Date.valueOf(user.getNgayBatDau()) : null);
            pstmt.setString(10, user.getTrangThai());
            pstmt.setString(11, user.getIdQuanLy());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật người dùng
    public boolean updateNguoiDung(NguoiDung user) {
        String query = "UPDATE NguoiDung SET TaiKhoan = ?, MatKhau = ?, HoTen = ?, NgaySinh = ?, SDT = ?, Email = ?, " +
                "VaiTro = ?, NgayBatDau = ?, TrangThai = ?, idQuanLy = ? WHERE idNguoiDung = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, user.getTaiKhoan());
            pstmt.setString(2, user.getMatKhau());
            pstmt.setString(3, user.getHoTen());
            pstmt.setDate(4, java.sql.Date.valueOf(user.getNgaySinh()));
            pstmt.setString(5, user.getSdt());
            pstmt.setString(6, user.getEmail());
            pstmt.setString(7, user.getVaiTro());
            pstmt.setDate(8, user.getNgayBatDau() != null ? java.sql.Date.valueOf(user.getNgayBatDau()) : null);
            pstmt.setString(9, user.getTrangThai());
            pstmt.setString(10, user.getIdQuanLy());
            pstmt.setString(11, user.getIdNguoiDung());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa người dùng
    public boolean deleteNguoiDung(String idNguoiDung) {
        String query = "DELETE FROM NguoiDung WHERE idNguoiDung = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, idNguoiDung);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Tìm người dùng theo tài khoản
    public NguoiDung getUserByTaiKhoan(String taiKhoan) {
        String query = "SELECT * FROM NguoiDung WHERE TaiKhoan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, taiKhoan);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToNguoiDung(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Tìm người dùng theo Email
    public NguoiDung getUserByEmail(String email) {
        String query = "SELECT * FROM NguoiDung WHERE Email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToNguoiDung(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Tìm người dùng theo số điện thoại
    public NguoiDung getUserBySDT(String sdt) {
        String query = "SELECT * FROM NguoiDung WHERE SDT = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, sdt);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToNguoiDung(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Tìm kiếm partial match - Tài khoản (chỉ khách hàng)
    public NguoiDung searchCustomerByTaiKhoan(String taiKhoan) {
        String query = "SELECT * FROM NguoiDung WHERE TaiKhoan LIKE ? AND VaiTro = 'KHACHHANG'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + taiKhoan + "%");
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToNguoiDung(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Tìm kiếm partial match - Email (chỉ khách hàng)
    public NguoiDung searchCustomerByEmail(String email) {
        String query = "SELECT * FROM NguoiDung WHERE Email LIKE ? AND VaiTro = 'KHACHHANG'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + email + "%");
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToNguoiDung(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Tìm kiếm partial match - Số điện thoại (chỉ khách hàng)
    public NguoiDung searchCustomerBySDT(String sdt) {
        String query = "SELECT * FROM NguoiDung WHERE SDT LIKE ? AND VaiTro = 'KHACHHANG'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + sdt + "%");
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToNguoiDung(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Tìm kiếm partial match - Tài khoản (chỉ nhân viên)
    public NguoiDung searchEmployeeByTaiKhoan(String taiKhoan) {
        String query = "SELECT * FROM NguoiDung WHERE TaiKhoan LIKE ? AND VaiTro = 'NHANVIEN'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + taiKhoan + "%");
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToNguoiDung(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Tìm kiếm partial match - Email (chỉ nhân viên)
    public NguoiDung searchEmployeeByEmail(String email) {
        String query = "SELECT * FROM NguoiDung WHERE Email LIKE ? AND VaiTro = 'NHANVIEN'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + email + "%");
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToNguoiDung(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Tìm kiếm partial match - Số điện thoại (chỉ nhân viên)
    public NguoiDung searchEmployeeBySDT(String sdt) {
        String query = "SELECT * FROM NguoiDung WHERE SDT LIKE ? AND VaiTro = 'NHANVIEN'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + sdt + "%");
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToNguoiDung(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Tìm kiếm partial match - Tài khoản
    public NguoiDung searchUserByTaiKhoan(String taiKhoan) {
        String query = "SELECT * FROM NguoiDung WHERE TaiKhoan LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + taiKhoan + "%");
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToNguoiDung(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Tìm kiếm partial match - Email
    public NguoiDung searchUserByEmail(String email) {
        String query = "SELECT * FROM NguoiDung WHERE Email LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + email + "%");
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToNguoiDung(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Tìm kiếm partial match - Số điện thoại
    public NguoiDung searchUserBySDT(String sdt) {
        String query = "SELECT * FROM NguoiDung WHERE SDT LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + sdt + "%");
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToNguoiDung(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Helper method để map ResultSet thành NguoiDung object
    private NguoiDung mapResultSetToNguoiDung(ResultSet rs) throws SQLException {
        NguoiDung user = new NguoiDung();
        user.setIdNguoiDung(rs.getString("idNguoiDung"));
        user.setTaiKhoan(rs.getString("TaiKhoan"));
        user.setMatKhau(rs.getString("MatKhau"));
        user.setHoTen(rs.getString("HoTen"));
        if (rs.getDate("NgaySinh") != null) {
            user.setNgaySinh(rs.getDate("NgaySinh").toLocalDate());
        }
        user.setSdt(rs.getString("SDT"));
        user.setEmail(rs.getString("Email"));
        user.setVaiTro(rs.getString("VaiTro"));
        return user;
    }
}