package com.bookingcinema;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.bookingcinema.utils.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;

public class App extends Application {

    private static Stage primaryStage; // Lưu trữ Stage chính

    @Override
    public void start(Stage stage) throws IOException {
        App.primaryStage = stage; // Lưu stage lại

        // Kiểm tra kết nối CSDL
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.out.println("Không thể kết nối CSDL.");
        }

        // Load màn hình đăng nhập ban đầu
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/bookingcinema/view/dang_nhap.fxml"));

        // Thiết lập kích thước chuẩn ban đầu: 1000x700
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);

        stage.setScene(scene);
        stage.setTitle("Hệ Thống Bán Vé Xem Phim Online - Nhóm 13");
        stage.setResizable(false); // Cố định không cho người dùng kéo giãn (nếu muốn)
        stage.show();
    }

    // Hàm chuyển cảnh cơ bản (Dùng cho Login, Register...)
    public static void setRoot(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/bookingcinema/view/" + fxml + ".fxml"));
        Parent root = fxmlLoader.load();

        // Gọi hàm setRoot(Parent) bên dưới
        setRoot(root);
    }

    // Hàm chuyển cảnh nâng cao (Dùng khi đã load root và controller xong)
    // Đây là hàm quan trọng nhất để giữ cố định khung hình!
    public static void setRoot(Parent root) {
        Scene currentScene = primaryStage.getScene();

        // Tạo scene mới với kích thước của scene cũ
        Scene newScene = new Scene(root, currentScene.getWidth(), currentScene.getHeight());

        // Set scene mới vào stage cũ (KHÔNG gọi stage.show() lại)
        primaryStage.setScene(newScene);
    }

    public static void main(String[] args) {
        launch();
    }
}