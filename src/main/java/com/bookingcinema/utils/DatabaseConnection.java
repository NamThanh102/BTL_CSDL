package com.bookingcinema.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Thông tin cấu hình Database dựa trên file SQL bạn cung cấp
    private static final String URL = "jdbc:mysql://localhost:3306/QuanLyBanVeOnline";
    private static final String USER = "root"; // Mặc định của XAMPP/MySQL, hãy đổi nếu bạn đặt khác
    private static final String PASSWORD = "123456"; // Mặc định thường trống hoặc "123456", hãy điền pass của bạn

    private static Connection connection = null;

    // Private constructor để ngăn chặn việc khởi tạo trực tiếp từ bên ngoài
    private DatabaseConnection() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load driver (thường tự động ở các version mới, nhưng khai báo cho chắc chắn)
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Kết nối CSDL QuanLyBanVeOnline thành công!");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Kết nối CSDL thất bại: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection(Connection conn) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Đã đóng kết nối CSDL.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}