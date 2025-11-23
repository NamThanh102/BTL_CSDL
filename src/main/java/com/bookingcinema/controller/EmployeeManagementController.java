package com.bookingcinema.controller;

import com.bookingcinema.dao.NguoiDungDAO;
import com.bookingcinema.model.NguoiDung;
import com.bookingcinema.utils.UserSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EmployeeManagementController {

    @FXML private TableView<NguoiDung> tableEmployee;
    @FXML private TableColumn<NguoiDung, String> colId;
    @FXML private TableColumn<NguoiDung, String> colHoTen;
    @FXML private TableColumn<NguoiDung, String> colTaiKhoan;
    @FXML private TableColumn<NguoiDung, String> colSDT;
    @FXML private TableColumn<NguoiDung, String> colEmail;
    @FXML private TableColumn<NguoiDung, String> colVaiTro;
    @FXML private TableColumn<NguoiDung, String> colTrangThai;

    @FXML private TextField txtId;
    @FXML private TextField txtTaiKhoan;
    @FXML private TextField txtMatKhau;
    @FXML private TextField txtHoTen;
    @FXML private DatePicker dpNgaySinh;
    @FXML private TextField txtSDT;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<String> cbVaiTro;
    @FXML private ComboBox<String> cbTrangThai;
    @FXML private DatePicker dpNgayBatDau;

    private NguoiDungDAO nguoiDungDAO = new NguoiDungDAO();
    private ObservableList<NguoiDung> employeeList = FXCollections.observableArrayList();
    private NguoiDung selectedEmployee = null;

    @FXML
    public void initialize() {
        setupTable();
        setupComboBoxes();
        loadEmployeeData();

        tableEmployee.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        showEmployeeDetails(newSelection);
                    }
                });
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idNguoiDung"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colTaiKhoan.setCellValueFactory(new PropertyValueFactory<>("taiKhoan"));
        colSDT.setCellValueFactory(new PropertyValueFactory<>("sdt"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colVaiTro.setCellValueFactory(new PropertyValueFactory<>("vaiTro"));
        colTrangThai.setCellValueFactory(cellData -> {
            String trangThai = cellData.getValue().getTrangThai();
            return new SimpleStringProperty(trangThai == null ? "N/A" : trangThai);
        });
    }

    private void setupComboBoxes() {
        cbVaiTro.setItems(FXCollections.observableArrayList("NHANVIEN", "QUANLY"));
        cbVaiTro.setValue("NHANVIEN");

        cbTrangThai.setItems(FXCollections.observableArrayList("DANGLAM", "NGHIPHEP"));
        cbTrangThai.setValue("DANGLAM");
    }

    private void loadEmployeeData() {
        List<NguoiDung> allUsers = nguoiDungDAO.getAllNguoiDung();
        employeeList.clear();
        
        // Chỉ hiển thị nhân viên và quản lý
        for (NguoiDung user : allUsers) {
            if ("NHANVIEN".equals(user.getVaiTro()) || "QUANLY".equals(user.getVaiTro())) {
                employeeList.add(user);
            }
        }
        
        tableEmployee.setItems(employeeList);
    }

    private void showEmployeeDetails(NguoiDung employee) {
        selectedEmployee = employee;
        txtId.setText(employee.getIdNguoiDung());
        txtTaiKhoan.setText(employee.getTaiKhoan());
        txtMatKhau.setText(""); // Không hiển thị mật khẩu
        txtHoTen.setText(employee.getHoTen());
        dpNgaySinh.setValue(employee.getNgaySinh());
        txtSDT.setText(employee.getSdt());
        txtEmail.setText(employee.getEmail());
        cbVaiTro.setValue(employee.getVaiTro());
        cbTrangThai.setValue(employee.getTrangThai());
        dpNgayBatDau.setValue(employee.getNgayBatDau());
    }

    @FXML
    private void handleAdd() {
        if (!validateInput()) return;

        NguoiDung newEmployee = new NguoiDung();
        newEmployee.setIdNguoiDung(txtId.getText().trim());
        newEmployee.setTaiKhoan(txtTaiKhoan.getText().trim());
        newEmployee.setMatKhau(txtMatKhau.getText().trim());
        newEmployee.setHoTen(txtHoTen.getText().trim());
        newEmployee.setNgaySinh(dpNgaySinh.getValue());
        newEmployee.setSdt(txtSDT.getText().trim());
        newEmployee.setEmail(txtEmail.getText().trim());
        newEmployee.setVaiTro(cbVaiTro.getValue());
        newEmployee.setTrangThai(cbTrangThai.getValue());
        newEmployee.setNgayBatDau(dpNgayBatDau.getValue());
        newEmployee.setIdQuanLy(UserSession.getInstance().getCurrentUser().getIdNguoiDung());

        if (nguoiDungDAO.insertNguoiDung(newEmployee)) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Thêm nhân viên thành công!");
            loadEmployeeData();
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm nhân viên. Có thể ID/Email/SDT đã tồn tại.");
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedEmployee == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn nhân viên cần sửa!");
            return;
        }

        if (!validateInput()) return;

        selectedEmployee.setTaiKhoan(txtTaiKhoan.getText().trim());
        if (!txtMatKhau.getText().trim().isEmpty()) {
            selectedEmployee.setMatKhau(txtMatKhau.getText().trim());
        }
        selectedEmployee.setHoTen(txtHoTen.getText().trim());
        selectedEmployee.setNgaySinh(dpNgaySinh.getValue());
        selectedEmployee.setSdt(txtSDT.getText().trim());
        selectedEmployee.setEmail(txtEmail.getText().trim());
        selectedEmployee.setVaiTro(cbVaiTro.getValue());
        selectedEmployee.setTrangThai(cbTrangThai.getValue());
        selectedEmployee.setNgayBatDau(dpNgayBatDau.getValue());

        if (nguoiDungDAO.updateNguoiDung(selectedEmployee)) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật nhân viên thành công!");
            loadEmployeeData();
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật nhân viên!");
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedEmployee == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn nhân viên cần xóa!");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận");
        confirmAlert.setHeaderText("Xóa nhân viên");
        confirmAlert.setContentText("Bạn có chắc muốn xóa nhân viên: " + selectedEmployee.getHoTen() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (nguoiDungDAO.deleteNguoiDung(selectedEmployee.getIdNguoiDung())) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Xóa nhân viên thành công!");
                loadEmployeeData();
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa nhân viên!");
            }
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    private void clearForm() {
        selectedEmployee = null;
        txtId.clear();
        txtTaiKhoan.clear();
        txtMatKhau.clear();
        txtHoTen.clear();
        dpNgaySinh.setValue(null);
        txtSDT.clear();
        txtEmail.clear();
        cbVaiTro.setValue("NHANVIEN");
        cbTrangThai.setValue("DANGLAM");
        dpNgayBatDau.setValue(LocalDate.now());
        tableEmployee.getSelectionModel().clearSelection();
    }

    private boolean validateInput() {
        if (txtId.getText().trim().isEmpty() ||
                txtTaiKhoan.getText().trim().isEmpty() ||
                txtHoTen.getText().trim().isEmpty() ||
                dpNgaySinh.getValue() == null ||
                txtSDT.getText().trim().isEmpty() ||
                txtEmail.getText().trim().isEmpty() ||
                dpNgayBatDau.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng điền đầy đủ thông tin!");
            return false;
        }

        if (selectedEmployee == null && txtMatKhau.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập mật khẩu cho nhân viên mới!");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
