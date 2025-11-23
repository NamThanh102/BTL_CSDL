package com.bookingcinema.controller;

import com.bookingcinema.dao.PhimDAO;
import com.bookingcinema.dao.PhongChieuDAO;
import com.bookingcinema.dao.SuatChieuDAO;
import com.bookingcinema.model.*;
import com.bookingcinema.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SuatChieuController {

    // --- FXML Form Controls ---
    @FXML private ComboBox<Phim> cboPhim;
    @FXML private ComboBox<PhongChieu> cboPhongChieu;
    @FXML private DatePicker dpNgayChieu;
    @FXML private Spinner<Integer> spHour;
    @FXML private Spinner<Integer> spMinute;
    @FXML private TextField txtGiaVe;
    @FXML private Label lblShowtimeMessage;
    @FXML private Button btnSaveShowtime;

    // --- FXML Table/Search Controls ---
    @FXML private ComboBox<Phim> cboSearchPhim;
    @FXML private ComboBox<PhongChieu> cboSearchPhong;
    @FXML private TableView<SuatChieu> tblSuatChieu;
    @FXML private TableColumn<SuatChieu, Integer> colSCID;
    @FXML private TableColumn<SuatChieu, String> colSCPhim;
    @FXML private TableColumn<SuatChieu, Integer> colSCPhong;
    @FXML private TableColumn<SuatChieu, LocalDateTime> colSCGio;
    @FXML private TableColumn<SuatChieu, Integer> colSCGia;
    @FXML private TableColumn<SuatChieu, Void> colSCAction;

    private PhimDAO phimDAO = new PhimDAO();
    private PhongChieuDAO phongChieuDAO = new PhongChieuDAO();
    private SuatChieuDAO suatChieuDAO = new SuatChieuDAO();
    private ObservableList<SuatChieu> masterSuatChieuList;
    private SuatChieu selectedSuatChieu; // Dùng để xác định chế độ Sửa/Thêm

    @FXML
    public void initialize() {
        // Khởi tạo Spinners cho Giờ/Phút
        spHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, LocalTime.now().getHour()));
        spMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, LocalTime.now().getMinute()));
        dpNgayChieu.setValue(LocalDate.now());

        loadComboBoxOptions();
        setupTable();
        loadSuatChieuData();

        // Listener khi chọn suất chiếu để đổ dữ liệu ra form
        tblSuatChieu.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedSuatChieu = newSelection;
                fillForm(newSelection);
                btnSaveShowtime.setText("Cập nhật Suất Chiếu (ID: " + newSelection.getIdSuatChieu() + ")");
            } else {
                handleResetShowtime();
            }
        });

        // Listener cho thanh tìm kiếm
        cboSearchPhim.setOnAction(e -> handleSearchShowtime());
        cboSearchPhong.setOnAction(e -> handleSearchShowtime());
    }

    private void loadComboBoxOptions() {
        // Tải danh sách Phim
        List<Phim> phimList = phimDAO.getAllPhim();
        cboPhim.setItems(FXCollections.observableArrayList(phimList));
        cboSearchPhim.setItems(FXCollections.observableArrayList(phimList));

        // Cần Custom StringConverter để ComboBox hiển thị Tên Phim thay vì đối tượng Phim
        cboPhim.setConverter(new StringConverter<Phim>() {
            @Override public String toString(Phim object) { return (object != null) ? object.getTen() : ""; }
            @Override public Phim fromString(String string) { return null; }
        });
        cboSearchPhim.setConverter(cboPhim.getConverter()); // Dùng chung converter

        // Tải danh sách Phòng Chiếu
        List<PhongChieu> phongList = phongChieuDAO.getAllPhongChieu();
        cboPhongChieu.setItems(FXCollections.observableArrayList(phongList));
        cboSearchPhong.setItems(FXCollections.observableArrayList(phongList));

        // Cần Custom StringConverter để ComboBox hiển thị ID Phòng
        cboPhongChieu.setConverter(new StringConverter<PhongChieu>() {
            @Override public String toString(PhongChieu object) { return (object != null) ? "Phòng " + object.getIdPhongChieu() : ""; }
            @Override public PhongChieu fromString(String string) { return null; }
        });
        cboSearchPhong.setConverter(cboPhongChieu.getConverter());
    }

    private void setupTable() {
        colSCID.setCellValueFactory(new PropertyValueFactory<>("idSuatChieu"));
        colSCPhim.setCellValueFactory(new PropertyValueFactory<>("tenPhim")); // Phải được set trong DAO
        colSCPhong.setCellValueFactory(new PropertyValueFactory<>("idPhongChieu"));
        colSCGio.setCellValueFactory(new PropertyValueFactory<>("thoiGianBatDau"));
        colSCGia.setCellValueFactory(new PropertyValueFactory<>("giaVe"));

        // Cột Thao tác
        colSCAction.setCellFactory(param -> new TableCell<>() {
            final Button editButton = new Button("Sửa");
            final Button deleteButton = new Button("Xóa");

            // ... (setup button styles)

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    final SuatChieu sc = getTableView().getItems().get(getIndex());
                    editButton.setOnAction(event -> tblSuatChieu.getSelectionModel().select(sc));
                    deleteButton.setOnAction(event -> handleDeleteShowtime(sc));

                    // Ràng buộc xóa: Không cho xóa suất chiếu đã qua
                    if (sc.getThoiGianBatDau().isBefore(LocalDateTime.now())) {
                        editButton.setDisable(true); // Không cho sửa suất chiếu đã qua
                        deleteButton.setDisable(true);
                        setGraphic(new HBox(5, new Label("Hết hạn")));
                    } else {
                        editButton.setDisable(false);
                        setGraphic(new HBox(5, editButton, deleteButton));
                    }
                }
            }
        });
    }

    private void loadSuatChieuData() {
        // Tải dữ liệu Suất chiếu chi tiết (có tên Phim)
        masterSuatChieuList = FXCollections.observableArrayList(suatChieuDAO.getAllSuatChieuDetails());
        tblSuatChieu.setItems(masterSuatChieuList);
    }

    private void fillForm(SuatChieu sc) {
        // Đổ dữ liệu
        cboPhim.setValue(suatChieuDAO.getPhimById(sc.getIdPhim()));
        cboPhongChieu.setValue(suatChieuDAO.getPhongById(sc.getIdPhongChieu()));
        dpNgayChieu.setValue(sc.getThoiGianBatDau().toLocalDate());
        spHour.getValueFactory().setValue(sc.getThoiGianBatDau().getHour());
        spMinute.getValueFactory().setValue(sc.getThoiGianBatDau().getMinute());
        txtGiaVe.setText(String.valueOf(sc.getGiaVe()));

        lblShowtimeMessage.setText("Đang sửa Suất chiếu ID: " + sc.getIdSuatChieu());
    }

    @FXML
    private void handleResetShowtime() {
        selectedSuatChieu = null;
        cboPhim.setValue(null);
        cboPhongChieu.setValue(null);
        dpNgayChieu.setValue(LocalDate.now());
        spHour.getValueFactory().setValue(LocalTime.now().getHour());
        spMinute.getValueFactory().setValue(LocalTime.now().getMinute());
        txtGiaVe.clear();
        btnSaveShowtime.setText("Lưu Suất Chiếu");
        lblShowtimeMessage.setText("Sẵn sàng thêm suất chiếu mới.");
        tblSuatChieu.getSelectionModel().clearSelection();
    }

    private String validateShowtimeForm(SuatChieu sc, Phim phim, PhongChieu phong, LocalDateTime thoiGian) {
        // Validation logic here
        if (phim == null || phong == null || txtGiaVe.getText().trim().isEmpty()) {
            return "Vui lòng chọn Phim, Phòng và nhập Giá vé.";
        }

        // 1. Kiểm tra Giá vé
        int giaVe;
        try {
            giaVe = Integer.parseInt(txtGiaVe.getText().trim());
            if (giaVe <= 0) return "Giá vé phải là số nguyên dương.";
        } catch (NumberFormatException e) {
            return "Giá vé không hợp lệ.";
        }

        // --- 2. KIỂM TRA NGÀY PHÁT HÀNH PHIM (NEW CONSTRAINT) ---
        LocalDate ngayPhatHanhPhim = phim.getNgayPhatHanh();

        if (thoiGian.toLocalDate().isBefore(ngayPhatHanhPhim)) {
            return "Lỗi: Ngày chiếu (" + thoiGian.toLocalDate() + ") không được trước Ngày phát hành phim (" + ngayPhatHanhPhim + ").";
        }

        // 3. Kiểm tra thời gian quá khứ (Áp dụng cho Lưu mới)
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);
        if (sc == null) {
            if (thoiGian.isBefore(now) || thoiGian.isEqual(now)) {
                return "Lỗi: Thời gian bắt đầu phải lớn hơn thời điểm hiện tại (" + now.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ").";
            }
        }

        // 4. Kiểm tra trùng giờ (Ràng buộc 3.4)
        int excludeId = (sc == null) ? 0 : sc.getIdSuatChieu();
        float thoiLuongPhim = phim.getThoiLuong();

        if (suatChieuDAO.isOverlap(thoiGian, thoiLuongPhim, phong.getIdPhongChieu(), excludeId)) {
            return "Trùng lịch chiếu! Phòng này đã có suất chiếu khác trong khoảng thời gian đó.";
        }

        return null; // Form hợp lệ
    }

    @FXML
    private void handleSaveShowtime() {
        lblShowtimeMessage.setText("");

        // 1. Lấy dữ liệu và tạo đối tượng
        Phim phim = cboPhim.getValue();
        PhongChieu phong = cboPhongChieu.getValue();
        int hour = spHour.getValue();
        int minute = spMinute.getValue();

        // FIX: Đảm bảo dpNgayChieu không null trước khi gọi
        if (dpNgayChieu.getValue() == null) {
            lblShowtimeMessage.setText("Lỗi: Vui lòng chọn Ngày chiếu.");
            return;
        }

        LocalDateTime thoiGianBatDau = LocalDateTime.of(dpNgayChieu.getValue(), LocalTime.of(hour, minute));

        SuatChieu scToSave = (selectedSuatChieu == null) ? new SuatChieu() : selectedSuatChieu;

        // 2. Validation
        String validationError = validateShowtimeForm(scToSave, phim, phong, thoiGianBatDau);
        if (validationError != null) {
            lblShowtimeMessage.setText(validationError);
            return;
        }

        // 3. Gán dữ liệu
        scToSave.setIdPhim(phim.getIdPhim());
        scToSave.setIdPhongChieu(phong.getIdPhongChieu());
        scToSave.setThoiGianBatDau(thoiGianBatDau);
        scToSave.setGiaVe(Integer.parseInt(txtGiaVe.getText().trim()));
        scToSave.setIdNguoiDung(UserSession.getInstance().getCurrentUser().getIdNguoiDung());

        // 4. Lưu vào DB
        boolean success = suatChieuDAO.saveSuatChieu(scToSave);

        if (success) {
            lblShowtimeMessage.setText((selectedSuatChieu == null) ? "Thêm suất chiếu thành công!" : "Cập nhật suất chiếu thành công!");
            loadSuatChieuData();
            handleResetShowtime();
        } else {
            lblShowtimeMessage.setText("Lỗi: Lưu suất chiếu thất bại. Lỗi CSDL.");
        }
    }

    @FXML
    private void handleSearchShowtime() {
        // 1. Lấy tham số tìm kiếm
        Phim searchPhim = cboSearchPhim.getValue();
        PhongChieu searchPhong = cboSearchPhong.getValue();

        // 2. Lọc danh sách chính
        List<SuatChieu> filteredList = masterSuatChieuList.stream()
                .filter(sc -> {
                    boolean matchPhim = (searchPhim == null) || (sc.getIdPhim() == searchPhim.getIdPhim());
                    boolean matchPhong = (searchPhong == null) || (sc.getIdPhongChieu() == searchPhong.getIdPhongChieu());
                    return matchPhim && matchPhong;
                })
                .collect(Collectors.toList());

        tblSuatChieu.setItems(FXCollections.observableArrayList(filteredList));
        lblShowtimeMessage.setText("Đã tìm thấy " + filteredList.size() + " suất chiếu.");
    }

    @FXML
    private void handleResetSCSearch() {
        cboSearchPhim.setValue(null);
        cboSearchPhong.setValue(null);
        tblSuatChieu.setItems(masterSuatChieuList);
        lblShowtimeMessage.setText("Đã tải lại toàn bộ danh sách suất chiếu.");
    }

    @FXML
    private void handleDeleteShowtime(SuatChieu sc) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận Xóa");
        alert.setHeaderText("Xóa Suất chiếu ID: " + sc.getIdSuatChieu());
        alert.setContentText("Bạn có chắc chắn muốn xóa suất chiếu này không? Thao tác này sẽ bị từ chối nếu suất chiếu đã có vé.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = suatChieuDAO.deleteSuatChieu(sc.getIdSuatChieu());
            if (success) {
                lblShowtimeMessage.setText("Xóa suất chiếu thành công.");
            } else {
                lblShowtimeMessage.setText("Xóa suất chiếu thất bại! Suất chiếu đã có vé hoặc lỗi CSDL.");
            }
            loadSuatChieuData();
            handleResetShowtime();
        }
    }
}

