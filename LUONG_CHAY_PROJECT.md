# LUỒNG CHẠY HỆ THỐNG ĐẶT VÉ XEM PHIM ONLINE - NHÓM 13

## 📋 MỤC LỤC
1. [Tổng quan hệ thống](#1-tổng-quan-hệ-thống)
2. [Khởi động ứng dụng](#2-khởi-động-ứng-dụng)
3. [Luồng đăng nhập & phân quyền](#3-luồng-đăng-nhập--phân-quyền)
4. [Luồng chức năng theo vai trò](#4-luồng-chức-năng-theo-vai-trò)
5. [Luồng đặt vé (Khách hàng)](#5-luồng-đặt-vé-khách-hàng)
6. [Luồng quản lý (Quản lý)](#6-luồng-quản-lý-quản-lý)
7. [Luồng nghiệp vụ (Nhân viên)](#7-luồng-nghiệp-vụ-nhân-viên)
8. [Cơ chế Session & Database](#8-cơ-chế-session--database)

## 1. TỔNG QUAN HỆ THỐNG
### Kiến trúc 3 lớp (MVC Pattern)
```
┌─────────────────────────────────────────────────────────────┐
│                      PRESENTATION LAYER                      │
│  (FXML Views + Controllers - JavaFX)                        │
└─────────────────┬───────────────────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────────────────┐
│                      BUSINESS LOGIC LAYER                    │
│  (DAO - Data Access Objects + Models)                       │
└─────────────────┬───────────────────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────────────────┐
│                      DATA LAYER                              │
│  (MySQL Database - QuanLyBanVeOnline)                       │
└─────────────────────────────────────────────────────────────┘
```

### Cấu trúc thư mục chính
```
src/main/
├── java/com/bookingcinema/
│   ├── App.java                    # Entry point
│   ├── controller/                 # 20 Controllers
│   ├── dao/                        # 8 DAO classes
│   ├── model/                      # 11 Model classes
│   └── utils/                      # UserSession, DatabaseConnection
└── resources/com/bookingcinema/view/
    └── *.fxml                      # 20 FXML views
```
---

## 2. KHỞI ĐỘNG ỨNG DỤNG

### 2.1. Entry Point - `App.java`

```java
main() → launch() → start(Stage)
```
**Trình tự thực thi:**
1. **Khởi tạo JavaFX Application**
   ```
   App.main(args) được gọi
   └─> launch() (JavaFX Framework)
       └─> start(Stage primaryStage)
   ```

2. **Kiểm tra kết nối Database**
   ```java
   Connection conn = DatabaseConnection.getConnection();
   // jdbc:mysql://localhost:3306/QuanLyBanVeOnline
   // USER: root, PASSWORD: 123456
   ```

3. **Load màn hình đăng nhập**
   ```java
   FXMLLoader.load("/com/bookingcinema/view/dang_nhap.fxml")
   Scene(1000x700) → Stage
   ```

### 2.2. Cơ chế chuyển màn hình

**Hàm `App.setRoot(String fxml)`** - Chuyển màn hình cơ bản:
```java
App.setRoot("dang_nhap") 
→ Load FXML 
→ Giữ nguyên kích thước Scene (1000x700)
→ Không tạo Stage mới
```

**Hàm `App.setRoot(Parent root)`** - Chuyển màn hình nâng cao:
```java
// Dùng khi đã có Controller và cần truyền dữ liệu
FXMLLoader loader = new FXMLLoader(...);
Parent root = loader.load();
Controller ctrl = loader.getController();
ctrl.setData(...);
App.setRoot(root);
```

---

## 3. LUỒNG ĐĂNG NHẬP & PHÂN QUYỀN

### 3.1. Màn hình đăng nhập (`dang_nhap.fxml`)

```
┌──────────────────────────────────────────────┐
│     HỆ THỐNG BÁN VÉ XEM PHIM ONLINE         │
│                                              │
│  Tài khoản: [____________]                  │
│  Mật khẩu:  [____________]                  │
│                                              │
│        [ Đăng nhập ]  [ Đăng ký ]           │
└──────────────────────────────────────────────┘
```

### 3.2. Xử lý đăng nhập - `DangNhapController.java`

**Luồng xử lý:**

```
handleLogin() được gọi
│
├─> Validate input (rỗng?)
│
├─> NguoiDungDAO.checkLogin(taiKhoan, matKhau)
│   │
│   └─> SQL: SELECT * FROM NguoiDung 
│           WHERE TaiKhoan = ? AND MatKhau = ?
│
├─> Nếu tìm thấy user:
│   │
│   ├─> UserSession.getInstance().setCurrentUser(user)  ✓
│   │   // Lưu thông tin người dùng vào Session
│   │
│   └─> Phân quyền theo VaiTro:
│       │
│       ├─> "QUANLY"     → App.setRoot("trang_chu_quan_ly")
│       ├─> "NHANVIEN"   → App.setRoot("trang_chu_nhan_vien")
│       └─> "KHACHHANG"  → App.setRoot("trang_chu_khach_hang")
│
└─> Nếu không tìm thấy:
    └─> Hiển thị lỗi "Tài khoản hoặc mật khẩu không đúng!"
```

### 3.3. Đăng ký tài khoản - `DangKyController.java`

**Luồng đăng ký:**

```
handleRegister() được gọi
│
├─> Validate:
│   ├─> Các trường bắt buộc không rỗng
│   ├─> Email đúng định dạng (@)
│   ├─> Số điện thoại 10-11 số
│   └─> Mật khẩu khớp với xác nhận
│
├─> Kiểm tra tài khoản đã tồn tại
│   └─> NguoiDungDAO.checkAccountExists(taiKhoan)
│
├─> Tạo NguoiDung mới (VaiTro = "KHACHHANG")
│
├─> NguoiDungDAO.insert(newUser)
│   └─> SQL: INSERT INTO NguoiDung VALUES (...)
│
└─> Chuyển về màn hình đăng nhập
    └─> App.setRoot("dang_nhap")
```

---

## 4. LUỒNG CHỨC NĂNG THEO VAI TRÒ

### 4.1. Dashboard theo vai trò

#### A. KHÁCH HÀNG - `trang_chu_khach_hang.fxml`

```
┌────────────────────────────────────────────────────────────┐
│  Xin chào, [Tên khách hàng]          [Cập nhật TT] [Đăng xuất] │
├────────────────────────────────────────────────────────────┤
│  Tìm kiếm: [________] Thể loại: [v] Thời lượng: [v]       │
├────────────────────────────────────────────────────────────┤
│  ┌────────┐  ┌────────┐  ┌────────┐  ┌────────┐          │
│  │ Phim 1 │  │ Phim 2 │  │ Phim 3 │  │ Phim 4 │          │
│  │[Đặt vé]│  │[Đặt vé]│  │[Đặt vé]│  │[Đặt vé]│          │
│  └────────┘  └────────┘  └────────┘  └────────┘          │
└────────────────────────────────────────────────────────────┘
       [Lịch sử mua vé]
```

#### B. NHÂN VIÊN - `trang_chu_nhan_vien.fxml`

```
┌────────────────────────────────────────────────────────────┐
│  Xin chào, [Tên nhân viên]       [Cập nhật TT] [Đăng xuất]  │
├────────────────────────────────────────────────────────────┤
│  Tab 1: Tra cứu khách hàng                                 │
│  Tab 2: Xem hóa đơn                                        │
│  Tab 3: In vé                                              │
└────────────────────────────────────────────────────────────┘
```

#### C. QUẢN LÝ - `trang_chu_quan_ly.fxml`

```
┌────────────────────────────────────────────────────────────┐
│  Xin chào, [Tên quản lý]                      [Đăng xuất]   │
├────────────────────────────────────────────────────────────┤
│  [QL Phim & Suất chiếu] [QL Nhân viên] [QL Ca làm việc] [Báo cáo] │
├────────────────────────────────────────────────────────────┤
│                                                            │
│              CONTENT AREA (BorderPane)                    │
│                                                            │
└────────────────────────────────────────────────────────────┘
```

---

## 5. LUỒNG ĐẶT VÉ (KHÁCH HÀNG)

### 5.1. Sơ đồ tổng quan

```
Trang chủ KH → Chọn phim → Chọn suất chiếu → Chọn ghế → Thanh toán → Xác nhận
```

### 5.2. Chi tiết từng bước

#### **BƯỚC 1: Trang chủ & Tìm phim** - `TrangChuKhachHangController`

**Chức năng:**
- Hiển thị danh sách phim (FlowPane - Grid Layout)
- Tìm kiếm theo tên phim (Live Search)
- Lọc theo: Thể loại, Thời lượng, Ngôn ngữ

**SQL Query:**
```sql
-- PhimDAO.getAllPhim()
SELECT p.*, GROUP_CONCAT(DISTINCT t.NoiDung) AS TheLoaiList,
       COUNT(DISTINCT sc.idSuatChieu) AS ShowtimeCount
FROM Phim p
LEFT JOIN TheLoaiPhim tp ON p.idPhim = tp.idPhim
LEFT JOIN TheLoai t ON tp.idTheLoai = t.idTheLoai
LEFT JOIN SuatChieu sc ON p.idPhim = sc.idPhim
WHERE sc.ThoiGianBatDau >= NOW()
GROUP BY p.idPhim
HAVING ShowtimeCount > 0
ORDER BY p.NgayPhatHanh DESC;
```

**Render phim:**
```java
for (Phim phim : filteredList) {
    VBox card = createMovieCard(phim);
    flowPanePhim.getChildren().add(card);
}
```

**Khi nhấn "Đặt vé":**
```java
handleBooking(Phim phim) {
    FXMLLoader loader = new FXMLLoader("dat_ve_suat_chieu.fxml");
    Parent root = loader.load();
    DatVeSuatChieuController ctrl = loader.getController();
    ctrl.setPhim(phim);
    App.setRoot(root);
}
```

---

#### **BƯỚC 2: Chọn suất chiếu** - `DatVeSuatChieuController`

**Giao diện:**
```
┌──────────────────────────────────────────────┐
│  Phim: [Tên phim]                           │
│                                              │
│  Chọn ngày: [DatePicker]                    │
│                                              │
│  Suất chiếu khả dụng:                       │
│  ┌────────────┬──────────┬─────────────┐   │
│  │ Thời gian  │ Phòng    │ Giá vé      │   │
│  ├────────────┼──────────┼─────────────┤   │
│  │ 14:00      │ Phòng 2  │ 60,000 VNĐ  │[Chọn] │
│  │ 16:30      │ Phòng 1  │ 70,000 VNĐ  │[Chọn] │
│  └────────────┴──────────┴─────────────┘   │
│                                              │
│  [< Trở về]                                 │
└──────────────────────────────────────────────┘
```

**SQL Query lấy suất chiếu:**
```sql
-- SuatChieuDAO.getSuatChieuByPhimAndDate(idPhim, ngayChieu)
SELECT * FROM SuatChieu
WHERE idPhim = ?
  AND DATE(ThoiGianBatDau) = ?
  AND ThoiGianBatDau >= NOW()
ORDER BY ThoiGianBatDau;
```

**Khi chọn suất:**
```java
handleSelectShowtime(SuatChieu sc) {
    FXMLLoader loader = new FXMLLoader("dat_ve_ghe.fxml");
    DatVeGheController ctrl = loader.getController();
    ctrl.setSuatChieuData(sc, phim);
    App.setRoot(loader.load());
}
```

---

#### **BƯỚC 3: Chọn ghế ngồi** - `DatVeGheController`

**Giao diện sơ đồ ghế:**
```
┌──────────────────────────────────────────────┐
│  Phim: [Tên] | Suất: 14:00 | Phòng: 2      │
├──────────────────────────────────────────────┤
│              MÀN HÌNH CHIẾU                  │
│  ═════════════════════════════════════       │
│                                              │
│    A  ■ ■ □ □ □ □ ■ □                       │
│    B  □ □ ■ □ □ □ □ □                       │
│    C  □ □ □ □ □ □ □ □                       │
│    D  ■ □ □ ■ □ □ ■ □                       │
│    E  □ □ □ □ □ □ □ □                       │
│                                              │
│  ■ Đã đặt   □ Còn trống   ■ Đang chọn      │
│                                              │
│  Ghế đã chọn: A3, B5, C1                    │
│  Tổng tiền: 180,000 VNĐ                     │
│                                              │
│  [< Trở về]  [Tiếp tục thanh toán >]       │
└──────────────────────────────────────────────┘
```

**SQL Query lấy ghế:**
```sql
-- GheDAO.getGheByIdPhongChieu(idPhongChieu)
SELECT * FROM Ghe
WHERE idPhongChieu = ?
ORDER BY Hang, Cot;

-- GheDAO.getBookedSeatsBySuatChieu(idSuatChieu)
SELECT g.* FROM Ghe g
JOIN VeXemPhim v ON g.idGhe = v.idGhe
JOIN HoaDon h ON v.idHoaDon = h.idHoaDon
WHERE v.idSuatChieu = ?
  AND h.TrangThai = 'DATHANHTOAN';
```

**Logic chọn ghế:**
```java
// Khi click vào ghế
handleSeatClick(Ghe ghe) {
    if (ghe đã đặt) → return;
    
    if (ghe đang chọn) 
        → Bỏ chọn → Xóa khỏi selectedSeats
    else 
        → Chọn → Thêm vào selectedSeats
    
    updateUI();
}
```

**Khi nhấn "Tiếp tục thanh toán":**
```java
handleContinue() {
    if (selectedSeats.isEmpty()) {
        alert("Vui lòng chọn ít nhất 1 ghế!");
        return;
    }
    
    FXMLLoader loader = new FXMLLoader("thanh_toan.fxml");
    ThanhToanController ctrl = loader.getController();
    ctrl.setBookingData(suatChieu, phim, selectedSeats);
    App.setRoot(loader.load());
}
```

---

#### **BƯỚC 4: Thanh toán** - `ThanhToanController`

**Giao diện:**
```
┌──────────────────────────────────────────────┐
│         PHIẾU THANH TOÁN (HÓA ĐƠN)          │
├──────────────────────────────────────────────┤
│  Phim: Đào, Phở và Piano                    │
│  Suất chiếu: 14:00 01/12/2025               │
│  Phòng: 2                                    │
│  Ghế: A3, B5, C1                            │
│  Tổng tiền: 180,000 VNĐ                     │
│                                              │
│  Phương thức thanh toán: [v Chọn]           │
│   - Thẻ Quốc tế (Visa/Master)               │
│   - Chuyển khoản                            │
│   - Tiền mặt                                │
│                                              │
│  [< Quay lại]  [XÁC NHẬN THANH TOÁN]       │
└──────────────────────────────────────────────┘
```

**SQL Queries trong giao dịch thanh toán:**

```sql
-- 1. Lấy phương thức thanh toán
SELECT * FROM PhuongThucThanhToan;

-- 2. TẠO HÓA ĐƠN (TRANSACTION BEGIN)
START TRANSACTION;

-- 2a. Insert HoaDon
INSERT INTO HoaDon (NgayThanhToan, TrangThai, idPhuongThucThanhToan, idNguoiDung)
VALUES (NOW(), 'DATHANHTOAN', ?, ?);
-- Lấy idHoaDon vừa tạo

-- 2b. Insert VeXemPhim (cho từng ghế)
INSERT INTO VeXemPhim (TrangThai, idSuatChieu, idHoaDon, idGhe)
VALUES ('CHUASUDUNG', ?, ?, ?);
-- Lặp lại cho tất cả ghế đã chọn

COMMIT;
-- Nếu lỗi: ROLLBACK;
```

**Code xử lý:**
```java
handlePayment() {
    PhuongThucThanhToan method = cboPhuongThuc.getValue();
    String userId = UserSession.getInstance()
                               .getCurrentUser()
                               .getIdNguoiDung();
    
    boolean success = hoaDonDAO.createBooking(
        userId, 
        suatChieu.getIdSuatChieu(), 
        method.getId(), 
        selectedSeats
    );
    
    if (success) {
        showAlert("Thanh toán thành công!");
        App.setRoot("trang_chu_khach_hang");
    }
}
```

---

#### **BƯỚC 5: Xem lịch sử & Chi tiết hóa đơn**

**A. Lịch sử mua vé** - `LichSuController`

**SQL Query:**
```sql
-- HoaDonDAO.getHoaDonByUserId(idNguoiDung)
SELECT h.*, 
       pttt.NoiDung as TenPTTT, 
       SUM(s.GiaVe) as TongTien 
FROM HoaDon h
JOIN PhuongThucThanhToan pttt ON h.idPhuongThucThanhToan = pttt.idPhuongThucThanhToan
JOIN VeXemPhim v ON h.idHoaDon = v.idHoaDon
JOIN SuatChieu s ON v.idSuatChieu = s.idSuatChieu
WHERE h.idNguoiDung = ?
GROUP BY h.idHoaDon
ORDER BY h.NgayThanhToan DESC;
```

**Giao diện:**
```
┌──────────────────────────────────────────────┐
│  LỊCH SỬ MUA VÉ                             │
├──────────────────────────────────────────────┤
│  Mã HD │ Ngày TT    │ Tổng tiền │ PTTT     │[Chi tiết] │
│  2     │ 25/11/2025 │ 60,000    │ Visa     │  [Xem]    │
│  1     │ 20/11/2025 │ 120,000   │ Tiền mặt │  [Xem]    │
└──────────────────────────────────────────────┘
```

**B. Chi tiết hóa đơn** - `ChiTietHoaDonController`

**SQL Query:**
```sql
-- HoaDonDAO.getTicketDetailsByHoaDonId(idHoaDon)
SELECT v.TrangThai AS TrangThaiVe, 
       s.ThoiGianBatDau, 
       s.GiaVe, 
       s.idPhongChieu, 
       p.Ten, 
       g.Hang, 
       g.Cot
FROM VeXemPhim v
JOIN SuatChieu s ON v.idSuatChieu = s.idSuatChieu
JOIN Phim p ON s.idPhim = p.idPhim
JOIN Ghe g ON v.idGhe = g.idGhe
WHERE v.idHoaDon = ?;
```

**Hiển thị:**
- Thông tin khách hàng (từ UserSession)
- Chi tiết phim, suất chiếu, phòng
- Danh sách ghế đã đặt
- Tổng tiền, phương thức thanh toán

---

## 6. LUỒNG QUẢN LÝ (QUẢN LÝ)

### 6.1. Quản lý Phim & Suất chiếu - `QuanLyPhimSuatChieuController`

**Giao diện (TabPane):**

```
┌────────────────────────────────────────────────────────────┐
│  [Tab Phim]  [Tab Suất chiếu]                             │
├────────────────────────────────────────────────────────────┤
│  TAB PHIM:                                                 │
│  ┌──────────────────────────────────────────────────────┐ │
│  │ FORM NHẬP PHIM                                       │ │
│  │ Tên phim: [___________]  Ngày PH: [DatePicker]      │ │
│  │ Thời lượng: [___] phút   Ngôn ngữ: [___]            │ │
│  │ Nội dung: [___________________________]             │ │
│  │ Thể loại: [✓] Hành động [✓] Tình cảm [ ] Kinh dị   │ │
│  │                                                      │ │
│  │           [LƯU PHIM]  [SỬA]  [XÓA]                  │ │
│  └──────────────────────────────────────────────────────┘ │
│                                                            │
│  ┌──────────────────────────────────────────────────────┐ │
│  │ DANH SÁCH PHIM (TableView)                          │ │
│  │ ID│Tên phim    │Ngày PH   │Thời lượng│Thể loại     │ │
│  │ 1 │Đào, Phở... │01/12/2025│ 120      │Tình cảm    │ │
│  │ 2 │Mai         │10/12/2025│ 110      │Tình cảm    │ │
│  └──────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────┘
```

**Chức năng CRUD Phim:**

**1. THÊM/SỬA PHIM:**
```java
handleSavePhim() {
    Phim phim = (selectedPhim == null) ? new Phim() : selectedPhim;
    
    // Set dữ liệu từ form
    phim.setTen(txtTenPhim.getText());
    phim.setNgayPhatHanh(dpNgayPH.getValue());
    phim.setThoiLuong(Float.parseFloat(txtThoiLuong.getText()));
    phim.setNgonNguChinh(txtNgonNgu.getText());
    phim.setNoiDung(txtNoiDung.getText());
    
    // Lưu phim
    if (selectedPhim == null) {
        PhimDAO.insert(phim);
        
        // Lưu thể loại (many-to-many)
        for (CheckBox cb : selectedGenres) {
            TheLoaiDAO.insertTheLoaiPhim(newPhimId, cb.getUserData());
        }
    } else {
        PhimDAO.update(phim);
        TheLoaiDAO.deleteTheLoaiPhim(phim.getIdPhim());
        // Insert lại thể loại mới
    }
    
    refreshPhimTable();
}
```

**SQL Queries:**
```sql
-- Insert Phim
INSERT INTO Phim (Ten, NgayPhatHanh, ThoiLuong, NgonNguChinh, NoiDung, idNguoiDung)
VALUES (?, ?, ?, ?, ?, ?);

-- Insert Thể loại phim (Junction table)
INSERT INTO TheLoaiPhim (idPhim, idTheLoai) VALUES (?, ?);

-- Update Phim
UPDATE Phim SET Ten = ?, NgayPhatHanh = ?, ... WHERE idPhim = ?;

-- Delete Phim
DELETE FROM Phim WHERE idPhim = ?;
```

**2. XÓA PHIM:**
```java
handleDeletePhim() {
    if (selectedPhim == null) return;
    
    // Confirm dialog
    Alert confirm = new Alert(AlertType.CONFIRMATION);
    confirm.setContentText("Xóa phim sẽ xóa tất cả suất chiếu liên quan!");
    
    if (confirm.showAndWait().get() == ButtonType.OK) {
        PhimDAO.delete(selectedPhim.getIdPhim());
        refreshPhimTable();
    }
}
```

---

**Tab Suất chiếu** - `SuatChieuController`

**Chức năng:**
- Tạo suất chiếu mới cho phim
- Tìm kiếm suất chiếu theo phim/phòng
- Sửa/Xóa suất chiếu

**SQL Queries:**
```sql
-- Lấy suất chiếu
SELECT sc.*, p.Ten as TenPhim
FROM SuatChieu sc
JOIN Phim p ON sc.idPhim = p.idPhim
WHERE sc.idPhim = ? AND sc.idPhongChieu = ?
ORDER BY sc.ThoiGianBatDau DESC;

-- Insert suất chiếu
INSERT INTO SuatChieu (ThoiGianBatDau, GiaVe, idPhim, idPhongChieu)
VALUES (?, ?, ?, ?);

-- Update suất chiếu
UPDATE SuatChieu 
SET ThoiGianBatDau = ?, GiaVe = ?, idPhim = ?, idPhongChieu = ?
WHERE idSuatChieu = ?;

-- Delete suất chiếu
DELETE FROM SuatChieu WHERE idSuatChieu = ?;
```

---

### 6.2. Quản lý Nhân viên - `QuanLyNhanVienController`

**Giao diện:**
```
┌────────────────────────────────────────────────────────────┐
│  FORM THÊM/SỬA NHÂN VIÊN                                   │
│  Tài khoản: [___________]  Mật khẩu: [___________]        │
│  Họ tên: [___________]     SĐT: [___________]             │
│  Email: [___________]      CCCD: [___________]            │
│  Ngày sinh: [DatePicker]   Ca làm việc: [v Chọn]         │
│                                                            │
│           [LƯU]  [SỬA]  [XÓA]                             │
├────────────────────────────────────────────────────────────┤
│  DANH SÁCH NHÂN VIÊN                                       │
│  Tìm kiếm: [________]                                     │
│  ┌────────────────────────────────────────────────────┐   │
│  │ ID  │Tài khoản│Họ tên      │SĐT       │Ca LV     │   │
│  │ NV1 │nhanvien1│Nguyễn A    │0909...   │Ca sáng   │   │
│  │ NV2 │nhanvien2│Trần B      │0908...   │Ca chiều  │   │
│  └────────────────────────────────────────────────────┘   │
└────────────────────────────────────────────────────────────┘
```

**SQL Queries:**
```sql
-- Lấy danh sách nhân viên
SELECT nd.*, ca.NoiDung as TenCa
FROM NguoiDung nd
LEFT JOIN CaLamViec ca ON nd.idCaLamViec = ca.idCaLamViec
WHERE nd.VaiTro = 'NHANVIEN'
ORDER BY nd.HoTen;

-- Insert nhân viên
INSERT INTO NguoiDung (TaiKhoan, MatKhau, HoTen, SoDienThoai, Email, CCCD, NgaySinh, VaiTro, idCaLamViec)
VALUES (?, ?, ?, ?, ?, ?, ?, 'NHANVIEN', ?);

-- Update nhân viên
UPDATE NguoiDung 
SET HoTen = ?, SoDienThoai = ?, Email = ?, CCCD = ?, NgaySinh = ?, idCaLamViec = ?
WHERE idNguoiDung = ?;

-- Delete nhân viên
DELETE FROM NguoiDung WHERE idNguoiDung = ?;
```

---

### 6.3. Quản lý Ca làm việc - `QuanLyCaLamViecController`

**SQL Queries:**
```sql
-- Lấy ca làm việc
SELECT * FROM CaLamViec ORDER BY GioBatDau;

-- Insert ca làm việc
INSERT INTO CaLamViec (NoiDung, GioBatDau, GioKetThuc)
VALUES (?, ?, ?);

-- Update ca làm việc
UPDATE CaLamViec 
SET NoiDung = ?, GioBatDau = ?, GioKetThuc = ?
WHERE idCaLamViec = ?;

-- Delete ca làm việc
DELETE FROM CaLamViec WHERE idCaLamViec = ?;
```

---

### 6.4. Báo cáo doanh thu - `BaoCaoController`

**Giao diện:**
```
┌────────────────────────────────────────────────────────────┐
│  TẠO BÁO CÁO DOANH THU                                     │
│  Ngày bắt đầu: [10/26/2025]  Ngày kết thúc: [11/26/2025]  │
│                                                            │
│              [TẠO BÁO CÁO]                                 │
├────────────────────────────────────────────────────────────┤
│  CÔNG TY BÁN VÉ XEM PHIM ONLINE - NHÓM 13                 │
│  BÁO CÁO DOANH THU                                        │
│  Thực hiện từ ngày: 26/10/2025 đến ngày: 26/11/2025       │
│                                                            │
│  Người tạo: Quản Lý Rap (Quản lý)                         │
│  Email: admincsdt@gmail.com                                │
│  Thời gian tạo: 26/11/2025 02:51:45                       │
│                                                            │
│  1. CÁC CHỈ SỐ TỔNG HỢP                                   │
│  ┌──────────────┬─────────────┬────────────────────────┐  │
│  │ TỔNG DOANH THU│ SỐ VÉ BÁN RA│ SUẤT CHIẾU ĐÃ CÓ GIÁ │  │
│  │ 1,186,264 VNĐ │    4 vé     │       7 suất          │  │
│  └──────────────┴─────────────┴────────────────────────┘  │
│                                                            │
│  2. TOP 5 PHIM CÓ DOANH THU CAO NHẤT                      │
│  ┌──┬──────────────┬───────┬────┬───┬─────────┬──────┐   │
│  │ID│Tên Phim      │Thể loại│SC │VB │Doanh Thu│Tỉ trọng│   │
│  │4 │Dune: Part Two│Hành động│2 │2  │486,264  │40.99%│   │
│  │1 │Đào, Phở và..│Tình cảm │3 │2  │360,000  │30.35%│   │
│  │2 │Mai           │Tình cảm │2 │0  │340,000  │28.66%│   │
│  └──┴──────────────┴───────┴────┴───┴─────────┴──────┘   │
│                                                            │
│  3. TOP 5 PHIM CÓ DOANH THU THẤP NHẤT                     │
│  ... (tương tự)                                           │
│                                                            │
│               [ĐÓNG BÁO CÁO]                              │
└────────────────────────────────────────────────────────────┘
```

**SQL Query chính:**
```sql
SELECT p.idPhim, p.Ten, 
    GROUP_CONCAT(DISTINCT t.NoiDung ORDER BY t.NoiDung SEPARATOR ', ') AS TheLoaiList, 
    COUNT(DISTINCT sc.idSuatChieu) AS TongSuatChieu, 
    COUNT(DISTINCT CASE WHEN h.TrangThai = 'DATHANHTOAN' 
        AND h.NgayThanhToan >= ? AND h.NgayThanhToan < ? 
        THEN v.idVeXemPhim END) AS SoLuongVe, 
    COALESCE(SUM(CASE WHEN h.TrangThai = 'DATHANHTOAN' 
        AND h.NgayThanhToan >= ? AND h.NgayThanhToan < ? 
        THEN sc.GiaVe ELSE 0 END), 0) AS TongDoanhThu 
FROM Phim p 
LEFT JOIN TheLoaiPhim tp ON p.idPhim = tp.idPhim 
LEFT JOIN TheLoai t ON tp.idTheLoai = t.idTheLoai 
LEFT JOIN SuatChieu sc ON p.idPhim = sc.idPhim 
    AND sc.ThoiGianBatDau >= ? 
    AND sc.ThoiGianBatDau < ? 
LEFT JOIN VeXemPhim v ON sc.idSuatChieu = v.idSuatChieu 
LEFT JOIN HoaDon h ON v.idHoaDon = h.idHoaDon 
GROUP BY p.idPhim, p.Ten 
HAVING TongSuatChieu > 0 
ORDER BY TongDoanhThu DESC;
```

**Logic tính toán:**
```java
// 1. Tổng hợp
totalRevenue = SUM(TongDoanhThu);
totalTickets = SUM(SoLuongVe);
totalShowtimes = SUM(TongSuatChieu);

// 2. Top 5 cao nhất
top5 = reportList.sorted(DESC by TongDoanhThu).limit(5);

// 3. Top 5 thấp nhất
bottom5 = reportList.sorted(ASC by TongDoanhThu).limit(5);

// 4. Tỉ trọng
for (phim in allPhims) {
    phim.tiTrong = (phim.doanhThu / totalRevenue) * 100;
}
```

---

## 7. LUỒNG NGHIỆP VỤ (NHÂN VIÊN)

### 7.1. Tra cứu khách hàng - `TraCuuKhachHangController`

**SQL Query:**
```sql
-- Tìm khách hàng theo SĐT hoặc tên
SELECT * FROM NguoiDung
WHERE VaiTro = 'KHACHHANG'
  AND (SoDienThoai LIKE ? OR HoTen LIKE ?)
ORDER BY HoTen;
```

### 7.2. Xem hóa đơn - `XemHoaDonNhanVienController`

**SQL Query:**
```sql
-- Lấy hóa đơn theo mã hóa đơn
SELECT h.*, 
       nd.HoTen, 
       nd.SoDienThoai,
       pttt.NoiDung as TenPTTT,
       SUM(s.GiaVe) as TongTien
FROM HoaDon h
JOIN NguoiDung nd ON h.idNguoiDung = nd.idNguoiDung
JOIN PhuongThucThanhToan pttt ON h.idPhuongThucThanhToan = pttt.idPhuongThucThanhToan
JOIN VeXemPhim v ON h.idHoaDon = v.idHoaDon
JOIN SuatChieu s ON v.idSuatChieu = s.idSuatChieu
WHERE h.idHoaDon = ?
GROUP BY h.idHoaDon;
```

### 7.3. In vé - `InVeController`

**Chức năng:**
- Tìm kiếm vé theo mã hóa đơn
- Hiển thị thông tin vé chi tiết
- In vé (xuất PDF hoặc in trực tiếp)

**SQL Query lấy thông tin vé:**
```sql
SELECT v.*, 
       h.NgayThanhToan,
       sc.ThoiGianBatDau,
       sc.GiaVe,
       p.Ten as TenPhim,
       g.Hang,
       g.Cot,
       nd.HoTen
FROM VeXemPhim v
JOIN HoaDon h ON v.idHoaDon = h.idHoaDon
JOIN SuatChieu sc ON v.idSuatChieu = sc.idSuatChieu
JOIN Phim p ON sc.idPhim = p.idPhim
JOIN Ghe g ON v.idGhe = g.idGhe
JOIN NguoiDung nd ON h.idNguoiDung = nd.idNguoiDung
WHERE v.idHoaDon = ?;
```

---

## 8. CƠ CHẾ SESSION & DATABASE

### 8.1. UserSession - Singleton Pattern

**File:** `utils/UserSession.java`

```java
public class UserSession {
    private static UserSession instance;
    private NguoiDung currentUser;
    
    // Singleton
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }
    
    // Lưu user sau khi đăng nhập
    public void setCurrentUser(NguoiDung user);
    
    // Lấy thông tin user hiện tại
    public NguoiDung getCurrentUser();
    
    // Xóa session khi đăng xuất
    public void clearSession();
}
```

**Sử dụng:**
```java
// Sau khi đăng nhập thành công
UserSession.getInstance().setCurrentUser(user);

// Lấy thông tin user bất kỳ đâu
NguoiDung currentUser = UserSession.getInstance().getCurrentUser();
String userId = currentUser.getIdNguoiDung();
String role = currentUser.getVaiTro();

// Khi đăng xuất
UserSession.getInstance().clearSession();
App.setRoot("dang_nhap");
```

---

### 8.2. DatabaseConnection - Singleton Pattern

**File:** `utils/DatabaseConnection.java`

**Thông tin kết nối:**
```java
URL      = "jdbc:mysql://localhost:3306/QuanLyBanVeOnline"
USER     = "root"
PASSWORD = "123456"
```

**Cơ chế:**
```java
public static Connection getConnection() {
    if (connection == null || connection.isClosed()) {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
    }
    return connection;
}
```

**Sử dụng trong DAO:**
```java
public List<Phim> getAllPhim() {
    List<Phim> list = new ArrayList<>();
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(SQL);
         ResultSet rs = pstmt.executeQuery()) {
        
        while (rs.next()) {
            Phim p = new Phim();
            p.setIdPhim(rs.getInt("idPhim"));
            p.setTen(rs.getString("Ten"));
            // ...
            list.add(p);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    return list;
}
```

---

### 8.3. Pattern DAO (Data Access Object)

**Cấu trúc:**
```
Model (POJO)  ←→  DAO  ←→  Database
```

**Ví dụ: PhimDAO**

```java
public class PhimDAO {
    
    // CREATE
    public boolean insert(Phim phim) {
        String sql = "INSERT INTO Phim (...) VALUES (?, ?, ...)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phim.getTen());
            // ...
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // READ
    public List<Phim> getAllPhim() { ... }
    public Phim getPhimById(int id) { ... }
    
    // UPDATE
    public boolean update(Phim phim) { ... }
    
    // DELETE
    public boolean delete(int id) { ... }
}
```

---

## 9. SƠ ĐỒ TỔNG QUÁT

### 9.1. Sơ đồ luồng chính

```
                    ┌─────────────┐
                    │  App.main() │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │ Đăng nhập   │
                    └──────┬──────┘
                           │
         ┌─────────────────┼─────────────────┐
         │                 │                 │
    ┌────▼────┐      ┌─────▼─────┐    ┌─────▼─────┐
    │ QUẢN LÝ │      │ NHÂN VIÊN │    │KHÁCH HÀNG │
    └────┬────┘      └─────┬─────┘    └─────┬─────┘
         │                 │                 │
    ┌────┼────┐       ┌────┼────┐       ┌────┼────┐
    │QL Phim  │       │Tra cứu  │       │Tìm phim │
    │QL NV    │       │KH       │       │Chọn SC  │
    │QL Ca LV │       │Xem HĐ   │       │Chọn ghế │
    │Báo cáo  │       │In vé    │       │Thanh toán│
    └─────────┘       └─────────┘       │Lịch sử  │
                                        └─────────┘
```

### 9.2. Luồng dữ liệu

```
Controller → DAO → Database
    ↓         ↓
  View ← Model (POJO)
```

---

## 10. CÁC ĐIỂM QUAN TRỌNG

### 10.1. Quản lý State
- **UserSession**: Lưu thông tin người dùng đăng nhập
- **Scene Management**: `App.setRoot()` giữ nguyên kích thước window
- **Data Passing**: Truyền dữ liệu giữa controllers qua `setData()` methods

### 10.2. Transaction Management
- Sử dụng `Connection.setAutoCommit(false)`
- `COMMIT` khi thành công
- `ROLLBACK` khi có lỗi
- Đảm bảo tính toàn vẹn dữ liệu (ví dụ: tạo HoaDon + VeXemPhim)

### 10.3. Validation
- Frontend validation (JavaFX)
- Backend validation (DAO)
- Kiểm tra foreign key constraints
- Kiểm tra business rules (ví dụ: ghế đã đặt chưa)

### 10.4. Security
- Mật khẩu lưu dạng plain text (⚠️ Nên mã hóa MD5/BCrypt)
- Phân quyền dựa trên VaiTro
- SQL Injection prevention (PreparedStatement)

---

## 11. TECH STACK

- **Frontend**: JavaFX (FXML)
- **Backend**: Java (JDK 11+)
- **Database**: MySQL 8.0
- **Build Tool**: Maven
- **Design Pattern**: MVC, DAO, Singleton
- **JDBC Driver**: mysql-connector-java

---

## 12. CẤU TRÚC DATABASE

### Các bảng chính:
1. `NguoiDung` - Quản lý tài khoản (KHACHHANG, NHANVIEN, QUANLY)
2. `Phim` - Thông tin phim
3. `TheLoai` - Thể loại phim
4. `TheLoaiPhim` - Junction table (Many-to-Many)
5. `SuatChieu` - Lịch chiếu phim
6. `PhongChieu` - Phòng chiếu
7. `Ghe` - Ghế ngồi
8. `HoaDon` - Hóa đơn thanh toán
9. `VeXemPhim` - Vé đã đặt
10. `PhuongThucThanhToan` - Phương thức thanh toán
11. `CaLamViec` - Ca làm việc nhân viên

### Quan hệ:
- `NguoiDung` 1-N `HoaDon`
- `Phim` M-N `TheLoai` (qua `TheLoaiPhim`)
- `Phim` 1-N `SuatChieu`
- `SuatChieu` 1-N `VeXemPhim`
- `HoaDon` 1-N `VeXemPhim`
- `Ghe` 1-N `VeXemPhim`
- `PhongChieu` 1-N `Ghe`

---

## KẾT LUẬN

Hệ thống được thiết kế theo mô hình MVC chuẩn, với các luồng nghiệp vụ rõ ràng cho từng vai trò người dùng. Việc sử dụng DAO pattern và Singleton pattern giúp code dễ bảo trì và mở rộng.

**Điểm mạnh:**
- Phân quyền rõ ràng
- Giao dịch database an toàn (Transaction)
- UI/UX thân thiện
- Code modular, dễ maintain

**Có thể cải thiện:**
- Mã hóa mật khẩu
- Thêm logging system
- Exception handling tốt hơn
- Unit testing
- Caching cho data thường xuyên truy vấn
