USE QuanLyBanVeOnline;

-- BƯỚC 1: RESET TẤT CẢ CÁC BỘ ĐẾM AUTO_INCREMENT VỀ 1
-- Đảm bảo ID dữ liệu mới sẽ bắt đầu từ 1, tránh bị nhảy số.
ALTER TABLE CaLamViec AUTO_INCREMENT = 1;
ALTER TABLE PhongChieu AUTO_INCREMENT = 1;
ALTER TABLE Ghe AUTO_INCREMENT = 1;
ALTER TABLE PhuongThucThanhToan AUTO_INCREMENT = 1;
ALTER TABLE HoaDon AUTO_INCREMENT = 1;
ALTER TABLE LichCuThe AUTO_INCREMENT = 1;
ALTER TABLE Phim AUTO_INCREMENT = 1;
ALTER TABLE SuatChieu AUTO_INCREMENT = 1;
ALTER TABLE TheLoai AUTO_INCREMENT = 1;
ALTER TABLE VeXemPhim AUTO_INCREMENT = 1;


-- BƯỚC 2: CHÈN DỮ LIỆU MẪU

-- 2.1. Thêm Người dùng (Quản lý & Khách hàng mẫu)
INSERT INTO NguoiDung (idNguoiDung, TaiKhoan, MatKhau, HoTen, NgaySinh, SDT, Email, VaiTro, TrangThai) VALUES
                                                                                                           ('QL01', 'AdminCSDL', 'Pass@123', 'Quản Lý Rạp', '1990-01-01', '0909000001', 'admincsdl@gmail.com', 'QUANLY', 'DANGLAM'),
                                                                                                           ('KH01', 'KhachHangTest', 'User@123', 'Nguyễn Văn Khách', '2000-05-20', '0909000002', 'khachtest@gmail.com', 'KHACHHANG', NULL);

-- 2.2. Thêm Phương thức thanh toán (idPhuongThucThanhToan sẽ là 1, 2, 3, 4)
INSERT INTO PhuongThucThanhToan (NoiDung) VALUES
                                              ('Thẻ ATM Nội địa'),
                                              ('Thẻ Quốc tế (Visa/Master)'),
                                              ('Ví điện tử Momo'),
                                              ('Tiền mặt tại quầy');

-- 2.3. Thêm Thể loại phim (idTheLoai sẽ là 1, 2, 3, 4, 5, 6)
INSERT INTO TheLoai (NoiDung) VALUES
                                  ('Hành động'), ('Tình cảm'), ('Lịch sử'), ('Hoạt hình'), ('Hài hước'), ('Viễn tưởng');

-- 2.4. Thêm Phim (idPhim sẽ là 1, 2, 3, 4)
INSERT INTO Phim (Ten, NgayPhatHanh, ThoiLuong, NgonNguChinh, NoiDung, idNguoiDung) VALUES
                                                                                        ( 'Đào, Phở và Piano', '2024-02-10', 100, 'Tiếng Việt', 'Phim lịch sử Hà Nội 1946', 'QL01'),
                                                                                        ('Mai', '2024-02-10', 131, 'Tiếng Việt', 'Phim tâm lý tình cảm', 'QL01'),
                                                                                        ('Kung Fu Panda 4', '2024-03-08', 94, 'Tiếng Anh', 'Gấu trúc võ hiệp', 'QL01'),
                                                                                        ('Dune: Part Two', '2024-03-01', 166, 'Tiếng Anh', 'Khoa học viễn tưởng sử thi', 'QL01');

-- 2.5. Gán Thể loại cho Phim (Sử dụng Subquery để liên kết)
INSERT INTO TheLoaiPhim (idTheLoai, idPhim)
SELECT (SELECT idTheLoai FROM TheLoai WHERE NoiDung = 'Lịch sử'), 1
UNION ALL SELECT (SELECT idTheLoai FROM TheLoai WHERE NoiDung = 'Tình cảm'), 1
UNION ALL SELECT (SELECT idTheLoai FROM TheLoai WHERE NoiDung = 'Tình cảm'), 2
UNION ALL SELECT (SELECT idTheLoai FROM TheLoai WHERE NoiDung = 'Hài hước'), 2
UNION ALL SELECT (SELECT idTheLoai FROM TheLoai WHERE NoiDung = 'Hành động'), 3
UNION ALL SELECT (SELECT idTheLoai FROM TheLoai WHERE NoiDung = 'Hoạt hình'), 3
UNION ALL SELECT (SELECT idTheLoai FROM TheLoai WHERE NoiDung = 'Hành động'), 4
UNION ALL SELECT (SELECT idTheLoai FROM TheLoai WHERE NoiDung = 'Viễn tưởng'), 4;

-- 2.6. Thêm Phòng Chiếu (idPhongChieu sẽ là 1, 2)
INSERT INTO PhongChieu (SucChua, TrangThai) VALUES
                                                (40, 'DANGHOATDONG'),
                                                (40, 'DANGHOATDONG');

-- 2.7. Tạo Ghế cho Phòng 1 & 2
DELIMITER $$
CREATE PROCEDURE GenerateSeats(IN phongID INT, IN rs CHAR(5))
BEGIN
    DECLARE r INT DEFAULT 0;
    DECLARE i INT DEFAULT 1;
    DECLARE rowChar CHAR(1);
    WHILE r < LENGTH(rs) DO
        SET rowChar = SUBSTRING(rs, r + 1, 1);
        SET i = 1;
        WHILE i <= 8 DO
            INSERT IGNORE INTO Ghe (TrangThai, idPhongChieu, Hang, Cot)
            VALUES ('TOT', phongID, rowChar, i);
            SET i = i + 1;
END WHILE;
        SET r = r + 1;
END WHILE;
END$$
DELIMITER ;

CALL GenerateSeats(1, 'ABCDE');
CALL GenerateSeats(2, 'ABCDE');
DROP PROCEDURE GenerateSeats;

-- 2.8. Thêm Suất Chiếu (idSuatChieu sẽ là 1, 2, 3, 4, 5, 6)
INSERT INTO SuatChieu (GiaVe, ThoiGianBatDau, idPhongChieu, idPhim, idNguoiDung) VALUES
                                                                                     (50000, '2025-12-01 09:00:00', 1, 1, 'QL01'),
                                                                                     (60000, '2025-12-01 14:00:00', 2, 1, 'QL01'),
                                                                                     (70000, '2025-12-01 20:00:00', 1, 1, 'QL01'),
                                                                                     (80000, '2025-12-01 10:00:00', 2, 2, 'QL01'),
                                                                                     (90000, '2025-12-01 19:00:00', 2, 2, 'QL01'),
                                                                                     (120000, '2025-12-02 19:00:00', 1, 4, 'QL01');


