package com.bookingcinema.controller;

import com.bookingcinema.dao.CaLamViecDAO;
import com.bookingcinema.model.CaLamViec;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class QuanLyCaLamViecController {

    @FXML private TableView<CaLamViec> tableShift;
    @FXML private TableColumn<CaLamViec, Integer> colId;
    @FXML private TableColumn<CaLamViec, String> colNoiDung;
    @FXML private TableColumn<CaLamViec, LocalTime> colGioBatDau;
    @FXML private TableColumn<CaLamViec, LocalTime> colGioKetThuc;

    @FXML private ComboBox<String> cbNoiDung;
    @FXML private TextField txtGioBatDau;
    @FXML private TextField txtGioKetThuc;

    private CaLamViecDAO caLamViecDAO = new CaLamViecDAO();
    private ObservableList<CaLamViec> shiftList = FXCollections.observableArrayList();
    private CaLamViec selectedShift = null;

    @FXML
    public void initialize() {
        setupTable();
        setupComboBox();
        loadShiftData();

        tableShift.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        showShiftDetails(newSelection);
                    }
                });
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idCaLamViec"));
        colNoiDung.setCellValueFactory(new PropertyValueFactory<>("noiDung"));
        colGioBatDau.setCellValueFactory(new PropertyValueFactory<>("gioBatDau"));
        colGioKetThuc.setCellValueFactory(new PropertyValueFactory<>("gioKetThuc"));
    }

    private void setupComboBox() {
        cbNoiDung.setItems(FXCollections.observableArrayList("CASANG", "CACHIEU", "CATOI"));
    }

    private void loadShiftData() {
        List<CaLamViec> list = caLamViecDAO.getAllCaLamViec();
        shiftList.clear();
        shiftList.addAll(list);
        tableShift.setItems(shiftList);
    }

    private void showShiftDetails(CaLamViec shift) {
        selectedShift = shift;
        cbNoiDung.setValue(shift.getNoiDung());
        txtGioBatDau.setText(shift.getGioBatDau().toString());
        txtGioKetThuc.setText(shift.getGioKetThuc().toString());
    }

    @FXML
    private void handleAdd() {
        if (!validateInput()) return;

        CaLamViec newShift = new CaLamViec();
        newShift.setNoiDung(cbNoiDung.getValue());
        
        try {
            newShift.setGioBatDau(LocalTime.parse(txtGioBatDau.getText().trim()));
            newShift.setGioKetThuc(LocalTime.parse(txtGioKetThuc.getText().trim()));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Định dạng giờ không hợp lệ! Vui lòng nhập theo format HH:mm (vd: 08:00)");
            return;
        }

        if (caLamViecDAO.insertCaLamViec(newShift)) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Thêm ca làm việc thành công!");
            loadShiftData();
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm ca làm việc!");
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedShift == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn ca làm việc cần sửa!");
            return;
        }

        if (!validateInput()) return;

        selectedShift.setNoiDung(cbNoiDung.getValue());
        
        try {
            selectedShift.setGioBatDau(LocalTime.parse(txtGioBatDau.getText().trim()));
            selectedShift.setGioKetThuc(LocalTime.parse(txtGioKetThuc.getText().trim()));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Định dạng giờ không hợp lệ! Vui lòng nhập theo format HH:mm (vd: 08:00)");
            return;
        }

        if (caLamViecDAO.updateCaLamViec(selectedShift)) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật ca làm việc thành công!");
            loadShiftData();
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật ca làm việc!");
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedShift == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn ca làm việc cần xóa!");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận");
        confirmAlert.setHeaderText("Xóa ca làm việc");
        confirmAlert.setContentText("Bạn có chắc muốn xóa ca làm việc: " + selectedShift.getNoiDung() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (caLamViecDAO.deleteCaLamViec(selectedShift.getIdCaLamViec())) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Xóa ca làm việc thành công!");
                loadShiftData();
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa ca làm việc! Có thể ca này đang được sử dụng.");
            }
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    private void clearForm() {
        selectedShift = null;
        cbNoiDung.setValue(null);
        txtGioBatDau.clear();
        txtGioKetThuc.clear();
        tableShift.getSelectionModel().clearSelection();
    }

    private boolean validateInput() {
        if (cbNoiDung.getValue() == null ||
                txtGioBatDau.getText().trim().isEmpty() ||
                txtGioKetThuc.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng điền đầy đủ thông tin!");
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

