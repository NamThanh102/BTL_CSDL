-- Kích hoạt cơ sở dữ liệu
USE QuanLyBanVeOnline;


-- Tắt kiểm tra khóa ngoại để thực hiện TRUNCATE dễ dàng
SET FOREIGN_KEY_CHECKS = 0;


-- 1. Xóa dữ liệu cũ và Đặt lại AUTO_INCREMENT = 1 cho tất cả các bảng
-- Chú ý: Cần TRUNCATE các bảng có khóa ngoại trước.
TRUNCATE TABLE VeXemPhim;
TRUNCATE TABLE HoaDon;
TRUNCATE TABLE SuatChieu;
TRUNCATE TABLE TheLoaiPhim;
TRUNCATE TABLE Phim;
TRUNCATE TABLE TheLoai;
TRUNCATE TABLE LichCuThe;
TRUNCATE TABLE CaLamViec;
TRUNCATE TABLE Ghe;
TRUNCATE TABLE PhongChieu;
TRUNCATE TABLE NguoiDung;
TRUNCATE TABLE PhuongThucThanhToan;


-- Đặt lại AUTO_INCREMENT cho các bảng nếu TRUNCATE không thực hiện việc đó
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


-- Bật lại kiểm tra khóa ngoại
SET FOREIGN_KEY_CHECKS = 1;


-- ====================================================================
-- BƯỚC 1: CHÈN DỮ LIỆU CƠ BẢN (USERS, PAYMENTS, SHIFTS)
-- ====================================================================


-- 1. NguoiDung (3 tài khoản)
-- LƯU Ý: MatKhau được đặt là '123' cho mục đích thử nghiệm. Trong môi trường thực tế, nên sử dụng hàm băm (ví dụ: SHA2('123', 256)).
INSERT INTO NguoiDung (idNguoiDung, TaiKhoan, MatKhau, HoTen, NgaySinh, SDT, Email, VaiTro, NgayBatDau, TrangThai, idQuanLy) VALUES
('QL001', 'admin', '123', 'Nguyễn Văn A (Quản lý)', '1980-01-01', '0901000000', 'admin@cinema.vn', 'QUANLY', '2018-01-01', 'DANGLAM', NULL),
('NV001', 'nv1', '123', 'Lê Thị B (Nhân viên)', '1995-05-15', '0902000001', 'nv1@cinema.vn', 'NHANVIEN', '2022-10-01', 'DANGLAM', 'QL001'),
('KH001', 'kh1', '123', 'Trần Văn C (Khách hàng)', '2000-12-31', '0903000002', 'kh1@user.com', 'KHACHHANG', NULL, NULL, NULL);


-- 2. PhuongThucThanhToan (Không có "Thanh toán tại quầy")
INSERT INTO PhuongThucThanhToan (NoiDung) VALUES
('Momo'),
('Visa/MasterCard'),
('ZaloPay'),
('Chuyển khoản ngân hàng');


-- 3. CaLamViec (Ca làm việc mẫu)
INSERT INTO CaLamViec (NoiDung, GioBatDau, GioKetThuc) VALUES
('CASANG', '08:00:00', '12:00:00'),
('CACHIEU', '13:00:00', '17:00:00'),
('CATOI', '18:00:00', '22:00:00');


-- 4. LichCuThe (Phân công lịch làm việc mẫu cho NV001)
INSERT INTO LichCuThe (NgayLamViec, idCaLamViec, idNguoiDung) VALUES
(CURDATE(), 1, 'NV001'),
(CURDATE(), 2, 'NV001'),
(DATE_ADD(CURDATE(), INTERVAL 1 DAY), 3, 'NV001');


-- ====================================================================
-- BƯỚC 2: CHÈN DỮ LIỆU PHÒNG CHIẾU VÀ GHẾ
-- ====================================================================


-- 1. PhongChieu (4 phòng chiếu)
INSERT INTO PhongChieu (SucChua, TrangThai) VALUES
(40, 'DANGHOATDONG'), -- idPhongChieu=1
(40, 'DANGHOATDONG'), -- idPhongChieu=2
(40, 'BAOTRI'),        -- idPhongChieu=3
(40, 'DANGHOATDONG'); -- idPhongChieu=4


-- 2. Ghe (40 ghế/phòng, 5 hàng A-E, 8 cột 1-8)
-- Dùng cú pháp lặp để chèn 160 ghế
INSERT INTO Ghe (TrangThai, idPhongChieu, Hang, Cot)
SELECT
    'TOT',
    pc.idPhongChieu,
    h.Hang,
    c.Cot
FROM
    (SELECT 1 AS Cot UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8) AS c
CROSS JOIN
    (SELECT 'A' AS Hang UNION ALL SELECT 'B' UNION ALL SELECT 'C' UNION ALL SELECT 'D' UNION ALL SELECT 'E') AS h
CROSS JOIN
    PhongChieu AS pc
WHERE pc.SucChua = 40; -- Chỉ chèn cho các phòng có sức chứa 40


-- ====================================================================
-- BƯỚC 3: CHÈN DỮ LIỆU PHIM VÀ THỂ LOẠI
-- ====================================================================


-- 1. TheLoai (8 Thể loại - do QL001 tạo)
INSERT INTO TheLoai (NoiDung, idNguoiDung) VALUES
('Hành động', 'QL001'),     -- idTheLoai=1
('Kinh dị', 'QL001'),       -- idTheLoai=2
('Lãng mạn', 'QL001'),      -- idTheLoai=3
('Hài hước', 'QL001'),      -- idTheLoai=4
('Khoa học viễn tưởng', 'QL001'), -- idTheLoai=5
('Phiêu lưu', 'QL001'),    -- idTheLoai=6
('Hoạt hình', 'QL001'),     -- idTheLoai=7
('Tài liệu', 'QL001');      -- idTheLoai=8


-- 2. Phim (20 Bộ phim - do NV001 quản lý)
INSERT INTO Phim (Ten, NgayPhatHanh, ThoiLuong, NgonNguChinh, NoiDung, idNguoiDung) VALUES
('Phim Hành động 1', '2023-10-01', 120.5, 'Tiếng Anh', 'Mô tả phim hành động kịch tính.', 'NV001'), -- idPhim=1
('Phim Kinh dị 1', '2023-10-05', 90.0, 'Tiếng Việt', 'Mô tả phim ma rùng rợn.', 'NV001'), -- idPhim=2
('Phim Tình cảm 1', '2023-11-01', 105.3, 'Tiếng Hàn', 'Mô tả phim lãng mạn nhẹ nhàng.', 'NV001'), -- idPhim=3
('Phim Hài 1', '2023-11-15', 95.8, 'Tiếng Anh', 'Mô tả phim hài hước vui nhộn.', 'NV001'), -- idPhim=4
('Phim Sci-Fi 1', '2024-01-01', 140.0, 'Tiếng Anh', 'Mô tả phim khoa học viễn tưởng với robot.', 'NV001'), -- idPhim=5
('Phim Phiêu lưu 1', '2024-01-10', 110.5, 'Tiếng Việt', 'Mô tả hành trình khám phá bí ẩn.', 'NV001'), -- idPhim=6
('Phim Hoạt hình 1', '2024-02-01', 85.0, 'Tiếng Nhật', 'Mô tả phim hoạt hình đáng yêu.', 'NV001'), -- idPhim=7
('Phim Tài liệu 1', '2024-03-01', 60.0, 'Tiếng Anh', 'Mô tả về lịch sử điện ảnh.', 'NV001'), -- idPhim=8
('Phim Hành động 2', '2024-03-15', 135.5, 'Tiếng Anh', 'Phần tiếp theo của bộ phim hành động.', 'NV001'), -- idPhim=9
('Phim Kinh dị 2', '2024-04-01', 100.0, 'Tiếng Việt', 'Kinh dị về ngôi nhà bị bỏ hoang.', 'NV001'), -- idPhim=10
('Phim Tình cảm 2', '2024-04-10', 115.0, 'Tiếng Anh', 'Mối tình vượt thời gian.', 'NV001'), -- idPhim=11
('Phim Hài 2', '2024-05-01', 98.0, 'Tiếng Việt', 'Chuyện tình công sở hài hước.', 'NV001'), -- idPhim=12
('Phim Sci-Fi 2', '2024-05-15', 150.0, 'Tiếng Anh', 'Du hành vũ trụ.', 'NV001'), -- idPhim=13
('Phim Phiêu lưu 2', '2024-06-01', 125.0, 'Tiếng Nhật', 'Khám phá rừng rậm Amazon.', 'NV001'), -- idPhim=14
('Phim Hoạt hình 2', '2024-06-10', 92.0, 'Tiếng Anh', 'Cuộc phiêu lưu của các loài vật.', 'NV001'), -- idPhim=15
('Phim Hành động 3', '2024-07-01', 118.0, 'Tiếng Anh', 'Mật vụ đối đầu khủng bố.', 'NV001'), -- idPhim=16
('Phim Kinh dị 3', '2024-07-15', 103.0, 'Tiếng Hàn', 'Bí ẩn tại bệnh viện tâm thần.', 'NV001'), -- idPhim=17
('Phim Tình cảm 3', '2024-08-01', 112.5, 'Tiếng Việt', 'Hồi ức về mối tình đầu.', 'NV001'), -- idPhim=18
('Phim Hài 3', '2024-08-10', 88.0, 'Tiếng Anh', 'Tình huống dở khóc dở cười.', 'NV001'), -- idPhim=19
('Phim Sci-Fi 3', '2024-09-01', 130.0, 'Tiếng Anh', 'Cuộc chiến giữa các vì sao.', 'NV001'); -- idPhim=20


-- 3. TheLoaiPhim (Gán thể loại ngẫu nhiên)
INSERT INTO TheLoaiPhim (idTheLoai, idPhim) VALUES
(1, 1), (6, 1),
(2, 2),
(3, 3), (4, 3),
(4, 4),
(5, 5), (1, 5),
(6, 6), (1, 6),
(7, 7),
(8, 8),
(1, 9), (6, 9),
(2, 10), (6, 10),
(3, 11),
(4, 12), (3, 12),
(5, 13),
(6, 14), (7, 14),
(7, 15),
(1, 16),
(2, 17),
(3, 18),
(4, 19),
(5, 20), (1, 20);


-- ====================================================================
-- BƯỚC 4: DỮ LIỆU VẬN HÀNH (SHOWTIMES, INVOICES, TICKETS)
-- ====================================================================


-- 1. SuatChieu (3 suất chiếu mẫu - do NV001 quản lý)
-- Giả sử hôm nay là ngày 25/11/2025
INSERT INTO SuatChieu (GiaVe, ThoiGianBatDau, idPhongChieu, idPhim, idNguoiDung) VALUES
(80000, CONCAT(CURDATE(), ' 18:00:00'), 1, 1, 'NV001'), -- SC1: Phim Hành động 1, Phòng 1, Tối nay
(75000, CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 14:30:00'), 2, 5, 'NV001'), -- SC2: Phim Sci-Fi 1, Phòng 2, Chiều mai
(90000, CONCAT(CURDATE(), ' 20:30:00'), 4, 3, 'NV001'); -- SC3: Phim Tình cảm 1, Phòng 4, Tối nay


-- 2. HoaDon (2 hóa đơn mẫu cho KH001)
INSERT INTO HoaDon (NgayThanhToan, TrangThai, idPhuongThucThanhToan, idNguoiDung) VALUES
(NOW(), 'DATHANHTOAN', 1, 'KH001'), -- HD1: Thanh toán Momo
(NOW(), 'DATHANHTOAN', 2, 'KH001'); -- HD2: Thanh toán Visa


-- 3. VeXemPhim (Vé được mua trong các hóa đơn)
-- Vé cho HD1 (Suất chiếu 1)
INSERT INTO VeXemPhim (TrangThai, idSuatChieu, idHoaDon, idGhe)
SELECT 'CHUASUDUNG', 1, 1, idGhe
FROM Ghe
WHERE idPhongChieu = 1 AND (Hang = 'A' AND Cot = 1) OR (Hang = 'A' AND Cot = 2) LIMIT 2; -- Ghế A1, A2 Phòng 1


-- Vé cho HD2 (Suất chiếu 3)
INSERT INTO VeXemPhim (TrangThai, idSuatChieu, idHoaDon, idGhe)
SELECT 'CHUASUDUNG', 3, 2, idGhe
FROM Ghe
WHERE idPhongChieu = 4 AND (Hang = 'B' AND Cot = 5) OR (Hang = 'B' AND Cot = 6) OR (Hang = 'B' AND Cot = 7) LIMIT 3; -- Ghế B5, B6, B7 Phòng 4


