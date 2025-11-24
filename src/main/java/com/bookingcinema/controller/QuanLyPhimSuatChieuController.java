package com.bookingcinema.controller;

import com.bookingcinema.model.Phim;
import com.bookingcinema.dao.PhimDAO;
import com.bookingcinema.dao.TheLoaiDAO;
import com.bookingcinema.model.TheLoai;
import com.bookingcinema.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class QuanLyPhimSuatChieuController {

    // --- Quản lý Phim FXML ---
    @FXML private TextField txtTenPhim;
    @FXML private DatePicker dpNgayPhatHanh;
    @FXML private TextField txtThoiLuong;
    @FXML private TextField txtNgonNguChinh;
    @FXML private TextArea txtNoiDung;
    @FXML private TextField txtSearchPhim;
    @FXML private TableView<Phim> tblPhim;
    @FXML private TableColumn<Phim, Integer> colPhimID;
    @FXML private TableColumn<Phim, String> colPhimTen;
    @FXML private TableColumn<Phim, LocalDate> colPhimNPH;
    @FXML private TableColumn<Phim, Float> colPhimThoiLuong;
    @FXML private TableColumn<Phim, String> colPhimNN;
    @FXML private TableColumn<Phim, String> colPhimTheLoai;
    @FXML private TableColumn<Phim, Integer> colPhimSLSC;
    @FXML private TableColumn<Phim, Void> colPhimAction;
    @FXML private FlowPane flowPaneTheLoai;
    @FXML private Button btnSavePhim;
    @FXML private Label lblPhimMessage;
    // NEW FXML Variables for Filtering
    @FXML private ComboBox<String> cboFilterTheLoai;
    @FXML private ComboBox<String> cboFilterThoiLuong;
    @FXML private ComboBox<String> cboFilterNgonNgu;

    // --- Tab và Include Controller ---
    @FXML private TabPane mainTabPane;
    @FXML private SuatChieuController suatChieuViewController;

    // --- Quản lý Thể loại FXML ---
    @FXML private TextField txtTheLoaiName;
    @FXML private Button btnSaveTheLoai;
    @FXML private Label lblTheLoaiMessage;
    @FXML private TableView<TheLoai> tblTheLoai;
    @FXML private TableColumn<TheLoai, Integer> colTheLoaiID;
    @FXML private TableColumn<TheLoai, String> colTheLoaiName;
    @FXML private TableColumn<TheLoai, Integer> colTheLoaiSoPhim;
    @FXML private TableColumn<TheLoai, Void> colTheLoaiAction;

    private PhimDAO phimDAO = new PhimDAO();
    private TheLoaiDAO theLoaiDAO = new TheLoaiDAO();
    private ObservableList<Phim> masterPhimList;
    private ObservableList<TheLoai> theLoaiList;
    private Phim selectedPhim;
    private TheLoai selectedTheLoai;
    private List<TheLoai> allTheLoai;
    private List<Integer> selectedGenreIds = new ArrayList<>();
    private final String STYLE_DEFAULT = "-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-text-fill: #333; -fx-cursor: hand; -fx-font-weight: normal;";
    private final String STYLE_SELECTED = "-fx-background-color: #4CAF50; -fx-border-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;";
    private final String CHECKMARK = " \u2713";

    @FXML
    public void initialize() {
        // Khởi tạo cột Phim
        colPhimID.setCellValueFactory(new PropertyValueFactory<>("idPhim"));
        colPhimTen.setCellValueFactory(new PropertyValueFactory<>("ten"));
        colPhimNPH.setCellValueFactory(new PropertyValueFactory<>("ngayPhatHanh"));
        colPhimThoiLuong.setCellValueFactory(new PropertyValueFactory<>("thoiLuong"));
        colPhimNN.setCellValueFactory(new PropertyValueFactory<>("ngonNguChinh"));
        colPhimTheLoai.setCellValueFactory(new PropertyValueFactory<>("theLoai"));
        colPhimSLSC.setCellValueFactory(new PropertyValueFactory<>("showtimeCount"));

        // Khởi tạo cột Thể loại
        colTheLoaiID.setCellValueFactory(new PropertyValueFactory<>("idTheLoai"));
        colTheLoaiName.setCellValueFactory(new PropertyValueFactory<>("noiDung"));
        colTheLoaiSoPhim.setCellValueFactory(new PropertyValueFactory<>("soPhim"));

        loadPhimData();
        loadTheLoaiData();
        loadFilterOptions();
        setupActionButtonColumn();
        setupTheLoaiTable();

        // Listener khi chuyển tab để refresh danh sách phim trong tab Suất chiếu
        mainTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null && newTab.getText().equals("Quản lý Suất chiếu")) {
                // Refresh danh sách phim trong SuatChieuController
                if (suatChieuViewController != null) {
                    suatChieuViewController.refreshPhimList();
                }
            }
        });

        // Listener khi chọn phim để đổ dữ liệu ra form
        tblPhim.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedPhim = newSelection;
                fillForm(newSelection);
                btnSavePhim.setText("Cập nhật Phim");
            } else {
                handleResetPhim();
            }
        });
    }

    private void loadPhimData() {
        masterPhimList = FXCollections.observableArrayList(phimDAO.getAllPhim());
        tblPhim.setItems(masterPhimList);
        loadFilterOptions(); // Bắt buộc refresh filter options
    }

    private void loadTheLoaiData() {
        allTheLoai = theLoaiDAO.getAllTheLoai();
        flowPaneTheLoai.getChildren().clear();

        for (TheLoai tl : allTheLoai) {
            ToggleButton btn = new ToggleButton(tl.getNoiDung());
            btn.setStyle(STYLE_DEFAULT);
            btn.setId(String.valueOf(tl.getIdTheLoai()));
            btn.setOnAction(e -> handleGenreToggle(btn));
            flowPaneTheLoai.getChildren().add(btn);
        }
    }

    private void loadFilterOptions() {
        // --- 1. Thể loại ---
        Set<String> genres = new HashSet<>();
        genres.add("Tất cả");
        masterPhimList.forEach(phim -> {
            if (phim.getTheLoai() != null) {
                String[] parts = phim.getTheLoai().split(", ");
                for (String part : parts) genres.add(part.trim());
            }
        });
        cboFilterTheLoai.setItems(FXCollections.observableArrayList(genres));
        cboFilterTheLoai.setValue("Tất cả");

        // --- 2. Thời lượng ---
        cboFilterThoiLuong.setItems(FXCollections.observableArrayList(
                "Tất cả", "Dưới 90 phút", "90 - 120 phút", "Trên 120 phút"
        ));
        cboFilterThoiLuong.setValue("Tất cả");

        // --- 3. Ngôn ngữ ---
        Set<String> languages = new HashSet<>();
        languages.add("Tất cả");
        // FIX: Thêm null check cho phim.getNgonNguChinh()
        masterPhimList.forEach(phim -> {
            if (phim.getNgonNguChinh() != null) {
                languages.add(phim.getNgonNguChinh());
            }
        });
        cboFilterNgonNgu.setItems(FXCollections.observableArrayList(languages));
        cboFilterNgonNgu.setValue("Tất cả");

        // Listener (Chạy tìm kiếm tổng hợp khi người dùng thay đổi ComboBox)
        cboFilterTheLoai.setOnAction(e -> handleCombinedSearch());
        cboFilterThoiLuong.setOnAction(e -> handleCombinedSearch());
        cboFilterNgonNgu.setOnAction(e -> handleCombinedSearch());
    }

    // Xử lý khi bấm nút Toggle Thể loại
    private void handleGenreToggle(ToggleButton button) {
        int id = Integer.parseInt(button.getId());
        String currentText = button.getText().replace(CHECKMARK, "").trim();

        if (button.isSelected()) {
            selectedGenreIds.add(id);
            button.setStyle(STYLE_SELECTED);
            button.setText(currentText + CHECKMARK);
        } else {
            selectedGenreIds.remove((Integer) id);
            button.setStyle(STYLE_DEFAULT);
            button.setText(currentText);
        }
    }

    private void fillForm(Phim phim) {
        // Đổ dữ liệu cơ bản
        txtTenPhim.setText(phim.getTen());
        dpNgayPhatHanh.setValue(phim.getNgayPhatHanh());
        txtThoiLuong.setText(String.valueOf(phim.getThoiLuong()));
        txtNgonNguChinh.setText(phim.getNgonNguChinh());
        txtNoiDung.setText(phim.getNoiDung());

        // Đổ dữ liệu Thể loại
        List<Integer> assignedIds = phimDAO.getGenreIdsByPhimId(phim.getIdPhim());
        selectedGenreIds.clear();

        flowPaneTheLoai.getChildren().forEach(node -> {
            if (node instanceof ToggleButton) {
                ToggleButton btn = (ToggleButton) node;
                int id = Integer.parseInt(btn.getId());

                if (assignedIds.contains(id)) {
                    btn.setSelected(true);
                    selectedGenreIds.add(id);
                    btn.setStyle(STYLE_SELECTED);
                    btn.setText(btn.getText().replace(CHECKMARK, "").trim() + CHECKMARK);
                } else {
                    btn.setSelected(false);
                    btn.setStyle(STYLE_DEFAULT);
                    btn.setText(btn.getText().replace(CHECKMARK, "").trim());
                }
            }
        });
        lblPhimMessage.setText("");
    }

    @FXML
    private void handleResetPhim() {
        selectedPhim = null;
        txtTenPhim.clear();
        dpNgayPhatHanh.setValue(null);
        txtThoiLuong.clear();
        txtNgonNguChinh.clear();
        txtNoiDung.clear();
        btnSavePhim.setText("Lưu Phim");
        lblPhimMessage.setText("Sẵn sàng thêm phim mới.");

        // Bỏ chọn tất cả ToggleButton
        selectedGenreIds.clear();
        flowPaneTheLoai.getChildren().forEach(node -> {
            if (node instanceof ToggleButton) {
                ((ToggleButton) node).setSelected(false);
                ((ToggleButton) node).setStyle(STYLE_DEFAULT);
                ((ToggleButton) node).setText(((ToggleButton) node).getText().replace(CHECKMARK, "").trim());
            }
        });
        tblPhim.getSelectionModel().clearSelection();
    }

    // RÀNG BUỘC XÓA (Constraint Check)
    private void setupActionButtonColumn() {
        colPhimAction.setCellFactory(new Callback<TableColumn<Phim, Void>, TableCell<Phim, Void>>() {
            @Override
            public TableCell<Phim, Void> call(final TableColumn<Phim, Void> param) {
                return new TableCell<Phim, Void>() {
                    final Button editButton = new Button("Sửa");
                    final Button deleteButton = new Button("Xóa");

                    {
                        editButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand;");
                        deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand;");
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            final Phim phim = getTableView().getItems().get(getIndex());
                            editButton.setOnAction(event -> {
                                tblPhim.getSelectionModel().select(phim);
                            });
                            deleteButton.setOnAction(event -> handleDeletePhim(phim));

                            // RÀNG BUỘC: Nếu có suất chiếu, KHÔNG CHO XÓA
                            if (phim.getShowtimeCount() > 0) {
                                deleteButton.setDisable(true);
                                setGraphic(new HBox(5, editButton));
                                Tooltip tooltip = new Tooltip("Không thể xóa: Phim đang có " + phim.getShowtimeCount() + " suất chiếu.");
                                Tooltip.install(editButton.getParent(), tooltip);
                            } else {
                                deleteButton.setDisable(false);
                                setGraphic(new HBox(5, editButton, deleteButton));
                            }
                        }
                    }
                };
            }
        });
    }

    // NEW: Hàm tìm kiếm tổng hợp (Fix NPE)
    private void handleCombinedSearch() {
        String searchText = txtSearchPhim.getText().trim().toLowerCase();
        String selectedGenre = cboFilterTheLoai.getValue();
        String selectedDuration = cboFilterThoiLuong.getValue();
        String selectedLanguage = cboFilterNgonNgu.getValue();

        List<Phim> filteredList = masterPhimList.stream().filter(phim -> {

            // 1. Text Search (NPE FIX)
            boolean matchText = true;
            if (!searchText.isEmpty()) {
                String movieName = phim.getTen() != null ? phim.getTen().toLowerCase() : "";
                String langName = phim.getNgonNguChinh() != null ? phim.getNgonNguChinh().toLowerCase() : "";
                String genres = phim.getTheLoai() != null ? phim.getTheLoai().toLowerCase() : "";

                matchText = movieName.contains(searchText) ||
                        langName.contains(searchText) ||
                        genres.contains(searchText);
            }

            // 2. Filter Thể loại (AND condition)
            boolean matchGenre = "Tất cả".equals(selectedGenre) ||
                    (phim.getTheLoai() != null && phim.getTheLoai().contains(selectedGenre));

            // 3. Filter Thời lượng (AND condition)
            boolean matchDuration = true;
            if (!"Tất cả".equals(selectedDuration)) {
                float duration = phim.getThoiLuong();
                if ("Dưới 90 phút".equals(selectedDuration)) matchDuration = duration < 90;
                else if ("90 - 120 phút".equals(selectedDuration)) matchDuration = duration >= 90 && duration <= 120;
                else if ("Trên 120 phút".equals(selectedDuration)) matchDuration = duration > 120;
            }

            // 4. Filter Ngôn ngữ (AND condition) (NPE FIX)
            String ngonNguPhim = phim.getNgonNguChinh();
            boolean matchLanguage = "Tất cả".equals(selectedLanguage) ||
                    (ngonNguPhim != null && ngonNguPhim.equals(selectedLanguage));

            return matchText && matchGenre && matchDuration && matchLanguage;

        }).collect(Collectors.toList());

        tblPhim.setItems(FXCollections.observableArrayList(filteredList));
        lblPhimMessage.setText("Đã tìm thấy " + filteredList.size() + " phim theo bộ lọc.");
    }

    @FXML
    private void handleSearchPhim() {
        handleCombinedSearch();
    }

    @FXML
    private void handleResetPhimSearch() {
        txtSearchPhim.clear();
        cboFilterTheLoai.setValue("Tất cả");
        cboFilterThoiLuong.setValue("Tất cả");
        cboFilterNgonNgu.setValue("Tất cả");

        tblPhim.setItems(masterPhimList);
        lblPhimMessage.setText("Đã tải lại toàn bộ danh sách.");
    }

    private void handleDeletePhim(Phim phim) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận Xóa Phim");
        alert.setHeaderText("Ngừng chiếu: " + phim.getTen());
        alert.setContentText("Bạn có chắc chắn muốn xóa phim này không? Thao tác này sẽ xóa phim và các liên kết thể loại. (Có thể lỗi nếu có Suất chiếu/Vé liên quan).");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = phimDAO.deletePhim(phim.getIdPhim());
            if (success) {
                lblPhimMessage.setText("Xóa phim thành công: " + phim.getTen());
            } else {
                lblPhimMessage.setText("Xóa phim thất bại! Phim đang có Suất chiếu hoặc lỗi FK.");
            }
            loadPhimData();
            handleResetPhim();
        }
    }

    private String validatePhimForm(Phim currentPhim) {
        if (txtTenPhim.getText().trim().isEmpty() ||
                dpNgayPhatHanh.getValue() == null ||
                txtThoiLuong.getText().trim().isEmpty() ||
                txtNgonNguChinh.getText().trim().isEmpty() ||
                txtNoiDung.getText().trim().isEmpty()) {
            return "Vui lòng nhập đầy đủ tất cả các trường thông tin phim.";
        }

        try {
            float thoiLuong = Float.parseFloat(txtThoiLuong.getText().trim());
            if (thoiLuong <= 0) {
                return "Thời lượng phim phải là số dương.";
            }
        } catch (NumberFormatException e) {
            return "Thời lượng phim không hợp lệ (phải là số).";
        }

        int excludeId = (currentPhim == null) ? 0 : currentPhim.getIdPhim();
        if (phimDAO.checkDuplicateTen(txtTenPhim.getText().trim(), excludeId)) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Cảnh báo trùng tên");
            alert.setHeaderText("Phim có tên '" + txtTenPhim.getText().trim() + "' đã tồn tại.");
            alert.setContentText("Bạn có muốn tiếp tục lưu phim trùng tên không?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() != ButtonType.OK) {
                return "Chỉnh sửa tên phim để tiếp tục.";
            }
        }

        if (selectedGenreIds.isEmpty()) {
            return "Phim bắt buộc phải có ít nhất 1 thể loại.";
        }

        return null; // Form hợp lệ
    }

    @FXML
    private void handleSavePhim() {
        lblPhimMessage.setText("");
        Phim phimToSave = (selectedPhim == null) ? new Phim() : selectedPhim;

        String validationError = validatePhimForm(phimToSave);
        if (validationError != null) {
            lblPhimMessage.setText(validationError);
            return;
        }

        phimToSave.setTen(txtTenPhim.getText().trim());
        phimToSave.setNgayPhatHanh(dpNgayPhatHanh.getValue());
        phimToSave.setThoiLuong(Float.parseFloat(txtThoiLuong.getText().trim()));
        phimToSave.setNgonNguChinh(txtNgonNguChinh.getText().trim());
        phimToSave.setNoiDung(txtNoiDung.getText().trim());

        String managerId = UserSession.getInstance().getCurrentUser().getIdNguoiDung();
        boolean success;

        success = phimDAO.savePhim(phimToSave, selectedGenreIds, managerId);

        if (success) {
            lblPhimMessage.setText((selectedPhim == null) ? "Thêm phim mới thành công!" : "Cập nhật phim thành công!");
            loadPhimData();
            handleResetPhim();
        } else {
            lblPhimMessage.setText("Lưu phim thất bại! Kiểm tra log.");
        }
    }

    // ==================== QUẢN LÝ THỂ LOẠI ====================
    
    private void setupTheLoaiTable() {
        theLoaiList = FXCollections.observableArrayList(theLoaiDAO.getAllTheLoai());
        tblTheLoai.setItems(theLoaiList);
        
        // Listener khi chọn thể loại
        tblTheLoai.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedTheLoai = newSelection;
                txtTheLoaiName.setText(newSelection.getNoiDung());
                btnSaveTheLoai.setText("Cập nhật");
            } else {
                handleResetTheLoai();
            }
        });
        
        setupTheLoaiActionColumn();
    }
    
    private void setupTheLoaiActionColumn() {
        Callback<TableColumn<TheLoai, Void>, TableCell<TheLoai, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<TheLoai, Void> call(final TableColumn<TheLoai, Void> param) {
                final TableCell<TheLoai, Void> cell = new TableCell<>() {
                    private final Button btnEdit = new Button("Sửa");
                    private final Button btnDelete = new Button("Xóa");
                    {
                        btnEdit.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 11px;");
                        btnDelete.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-size: 11px;");
                        
                        btnEdit.setOnAction(event -> {
                            TheLoai tl = getTableView().getItems().get(getIndex());
                            selectedTheLoai = tl;
                            txtTheLoaiName.setText(tl.getNoiDung());
                            btnSaveTheLoai.setText("Cập nhật");
                        });
                        
                        btnDelete.setOnAction(event -> {
                            TheLoai tl = getTableView().getItems().get(getIndex());
                            handleDeleteTheLoai(tl);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(5, btnEdit, btnDelete);
                            setGraphic(hbox);
                        }
                    }
                };
                return cell;
            }
        };
        colTheLoaiAction.setCellFactory(cellFactory);
    }
    
    @FXML
    private void handleSaveTheLoai() {
        lblTheLoaiMessage.setText("");
        
        String noiDung = txtTheLoaiName.getText().trim();
        if (noiDung.isEmpty()) {
            lblTheLoaiMessage.setText("Tên thể loại không được để trống!");
            return;
        }
        
        int excludeId = (selectedTheLoai == null) ? 0 : selectedTheLoai.getIdTheLoai();
        if (theLoaiDAO.checkDuplicateNoiDung(noiDung, excludeId)) {
            lblTheLoaiMessage.setText("Tên thể loại đã tồn tại!");
            return;
        }
        
        boolean success;
        if (selectedTheLoai == null) {
            TheLoai newTheLoai = new TheLoai();
            newTheLoai.setNoiDung(noiDung);
            success = theLoaiDAO.insertTheLoai(newTheLoai);
            lblTheLoaiMessage.setText(success ? "Thêm thể loại thành công!" : "Thêm thể loại thất bại!");
        } else {
            selectedTheLoai.setNoiDung(noiDung);
            success = theLoaiDAO.updateTheLoai(selectedTheLoai);
            lblTheLoaiMessage.setText(success ? "Cập nhật thể loại thành công!" : "Cập nhật thể loại thất bại!");
        }
        
        if (success) {
            loadTheLoaiTableData();
            loadTheLoaiData(); // Refresh flowpane trong tab phim
            handleResetTheLoai();
        }
    }
    
    @FXML
    private void handleResetTheLoai() {
        txtTheLoaiName.clear();
        selectedTheLoai = null;
        btnSaveTheLoai.setText("Lưu");
        tblTheLoai.getSelectionModel().clearSelection();
        lblTheLoaiMessage.setText("");
    }
    
    private void handleDeleteTheLoai(TheLoai theLoai) {
        if (theLoai.getSoPhim() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Không thể xóa");
            alert.setHeaderText("Thể loại này đang được sử dụng");
            alert.setContentText("Có " + theLoai.getSoPhim() + " phim thuộc thể loại này. Vui lòng xóa liên kết trước!");
            alert.showAndWait();
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText("Bạn có chắc muốn xóa thể loại này?");
        confirm.setContentText("Thể loại: " + theLoai.getNoiDung());
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = theLoaiDAO.deleteTheLoai(theLoai.getIdTheLoai());
            if (success) {
                lblTheLoaiMessage.setText("Xóa thể loại thành công!");
                loadTheLoaiTableData();
                loadTheLoaiData(); // Refresh flowpane trong tab phim
                handleResetTheLoai();
            } else {
                lblTheLoaiMessage.setText("Xóa thể loại thất bại!");
            }
        }
    }
    
    private void loadTheLoaiTableData() {
        theLoaiList = FXCollections.observableArrayList(theLoaiDAO.getAllTheLoai());
        tblTheLoai.setItems(theLoaiList);
    }
}

