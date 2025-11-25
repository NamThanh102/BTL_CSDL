# Giao Diện Nhân Viên - Hệ Thống Bán Vé Xem Phim

## Tóm Tắt Các Thay Đổi

Đã thêm đầy đủ giao diện cho vai trò **Nhân Viên** với các chức năng:

### 1. **Trang Chủ Nhân Viên**
- **File**: `TrangChuNhanVienController.java` + `trang_chu_nhan_vien.fxml`
- Hiển thị menu chính với 3 chức năng chính
- Nút đăng xuất và xem thông tin cá nhân

### 2. **Tra Cứu Khách Hàng**
- **Controller**: `TraCuuKhachHangController.java`
- **FXML**: `tra_cuu_khach_hang.fxml`
- **Chức năng**:
  - Tìm kiếm khách hàng theo:
    - Tài khoản
    - Email
    - Số điện thoại
  - Hiển thị đầy đủ thông tin khách hàng (ID, họ tên, email, SĐT, ngày sinh, vai trò)
- **Thêm vào DAO**: 
  - `getUserByTaiKhoan()` 
  - `getUserByEmail()`
  - `getUserBySDT()`
  - `mapResultSetToNguoiDung()` (helper method)

### 3. **Xem Hóa Đơn**
- **Controller**: `XemHoaDonNhanVienController.java`
- **FXML**: `xem_hoa_don_nhan_vien.fxml`
- **Chức năng**:
  - Tìm kiếm hóa đơn theo ID người dùng
  - Hiển thị danh sách hóa đơn trong bảng (ID, ngày thanh toán, trạng thái, phương thức TT, tổng tiền)
  - Định dạng ngày tháng dễ đọc

### 4. **In Vé**
- **Controller**: `InVeController.java`
- **FXML**: `in_ve.fxml`
- **Chức năng**:
  - Tìm kiếm vé theo ID hóa đơn
  - Hiển thị chi tiết từng vé:
    - Tên phim
    - Thời gian chiếu
    - Phòng chiếu
    - Ghế
    - Giá vé
    - Trạng thái vé
  - Nút in vé (sử dụng PrinterJob của JavaFX)
  - Định dạng hóa đơn vé đẹp, chuyên nghiệp

### 5. **Cập Nhật Thông Tin Cá Nhân (Nhân Viên)**
- **Controller**: `CapNhatThongTinNhanVienController.java`
- **FXML**: `cap_nhat_thong_tin_nhan_vien.fxml`
- **Chức năng**:
  - Cập nhật: Họ tên, Ngày sinh, SĐT, Email
  - **Đổi mật khẩu**:
    - Validation: ≥8 ký tự, có chữ hoa, ký tự đặc biệt, không khoảng trắng
    - Kiểm tra xác nhận mật khẩu
  - Kiểm tra trùng lặp SĐT/Email (loại trừ ID hiện tại)
  - Cập nhật UserSession ngay lập tức
- **Thêm vào DAO**: Overload `checkExist()` với idNguoiDung để loại trừ chính mình

### 6. **Cập Nhật Luồng Đăng Nhập**
- **File**: `DangNhapController.java`
- Thêm điều kiện điều hướng:
  ```java
  if ("QUANLY".equalsIgnoreCase(vaiTro)) {
      App.setRoot("trang_chu_quan_ly");
  } else if ("NHANVIEN".equalsIgnoreCase(vaiTro)) {
      App.setRoot("trang_chu_nhan_vien");  // ← Mới thêm
  } else {
      App.setRoot("trang_chu_khach_hang");
  }
  ```

## Cấu Trúc File Mới Tạo

```
src/main/java/com/bookingcinema/controller/
├── TrangChuNhanVienController.java       ✓
├── TraCuuKhachHangController.java        ✓
├── XemHoaDonNhanVienController.java      ✓
├── InVeController.java                    ✓
└── CapNhatThongTinNhanVienController.java ✓

src/main/resources/com/bookingcinema/view/
├── trang_chu_nhan_vien.fxml               ✓
├── tra_cuu_khach_hang.fxml                ✓
├── xem_hoa_don_nhan_vien.fxml             ✓
├── in_ve.fxml                             ✓
└── cap_nhat_thong_tin_nhan_vien.fxml      ✓
```

## Cách Sử Dụng

### Đăng Nhập Với Tài Khoản Nhân Viên
1. Chạy ứng dụng
2. Đăng nhập với tài khoản có vai trò `NHANVIEN`
3. Bạn sẽ được chuyển đến `trang_chu_nhan_vien.fxml`

### Các Tính Năng Có Sẵn
- **Thông tin cá nhân**: Cập nhật + Đổi mật khẩu
- **Tra cứu khách hàng**: Tìm theo tài khoản, email, hoặc SĐT
- **Xem hóa đơn**: Nhập ID khách hàng để xem lịch sử mua vé
- **In vé**: Nhập ID hóa đơn để xem và in chi tiết vé

## Lưu Ý Kỹ Thuật

- **DateTimeFormatter**: Sử dụng `dd/MM/yyyy HH:mm` để hiển thị ngày giờ rõ ràng
- **TableView**: Sử dụng PropertyValueFactory để binding dữ liệu
- **Validation**: Mật khẩu mới phải ≥8 ký tự, có chữ hoa, ký tự đặc biệt, không khoảng trắng
- **Print**: Sử dụng `PrinterJob.createPrinterJob()` để in vé
- **Session**: UserSession được cập nhật ngay sau khi thay đổi thông tin
- **Database**: Kiểm tra trùng lặp SĐT/Email loại trừ ID hiện tại (không bắt buộc cập nhật SĐT/Email nếu không thay đổi)

## Build & Chạy

```bash
# Biên dịch
.\mvnw.cmd clean compile

# Chạy ứng dụng
.\mvnw.cmd javafx:run
```

Tất cả các file đã được tạo và compile thành công! ✓
