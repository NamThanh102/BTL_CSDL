module com.bookingcinema {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;             // Cần thiết cho JDBC/MySQL
    requires mysql.connector.j;    // Cần thiết cho Driver MySQL
    requires kernel;               // iText PDF kernel
    requires layout;               // iText PDF layout
    requires io;                   // iText PDF io

    // Mở package cho JavaFX truy cập để load giao diện
    opens com.bookingcinema to javafx.fxml;
    opens com.bookingcinema.controller to javafx.fxml;

    // Export package để các module khác dùng được
    exports com.bookingcinema;
    exports com.bookingcinema.controller;
    exports com.bookingcinema.model;
}