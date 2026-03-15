import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class LibraryController {
    @FXML private ComboBox<String> menu;
    @FXML private AnchorPane paneSach, paneDocGia;

    // TAB SÁCH
    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book, String> bookID, bookName;
    @FXML private TableColumn<Book, Integer> remaining;
    @FXML private TableColumn<Book, Void> actionBookCol;
    @FXML private TextField txtTimKiemSach, txtThemIdSach, txtThemTenSach, txtThemSoLuong;
    @FXML private Button btnThemSach;

    // TAB ĐỘC GIẢ
    @FXML private TableView<Reader> readerTable;
    @FXML private TableColumn<Reader, String> readerIDCol, readerNameCol;
    @FXML private TableColumn<Reader, Void> actionReaderCol;
    @FXML private TextArea readerDetails;
    @FXML private TextField txtTimKiemDocGia, txtNhapIdSach, txtThemIdDocGia, txtThemTenDocGia;
    @FXML private Button btnMuonSach, btnTraSach, btnThemDocGia;

    private LibraryManager manager;
    private Reader currentSelectedReader = null;
    private ObservableList<Book> masterBookList = FXCollections.observableArrayList();
    private ObservableList<Reader> masterReaderList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        manager = new LibraryManager();
        setupTabs();
        setupTableColumns();
        setupSearch();
        setupActionButtons();
        refreshData();
    }

    private void setupTabs() {
        menu.getItems().addAll("Quản lý sách", "Quản lý độc giả");
        menu.setValue("Quản lý sách");
        paneSach.toFront();
        menu.setOnAction(e -> {
            if ("Quản lý sách".equals(menu.getValue())) paneSach.toFront();
            else paneDocGia.toFront();
        });
    }

    private void setupTableColumns() {
        bookID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        bookName.setCellValueFactory(new PropertyValueFactory<>("name"));
        remaining.setCellValueFactory(new PropertyValueFactory<>("remaining"));

        readerIDCol.setCellValueFactory(new PropertyValueFactory<>("ID"));
        readerNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Nút Xóa Sách
        actionBookCol.setCellFactory(p -> new TableCell<>() {
            private final Button btn = new Button("Xóa");
            {
                btn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                btn.setOnAction(e -> {
                    Book b = getTableView().getItems().get(getIndex());
                    if (manager.xoaSachDB(b.getID())) refreshData();
                    else showAlert("Lỗi", "Không thể xóa sách đang có người mượn!");
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // Nút Xem & Xóa Độc Giả
        actionReaderCol.setCellFactory(p -> new TableCell<>() {
            private final Button btnV = new Button("Xem"), btnD = new Button("Xóa");
            private final HBox box = new HBox(5, btnV, btnD);
            {
                btnV.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                btnD.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                btnV.setOnAction(e -> {
                    currentSelectedReader = getTableView().getItems().get(getIndex());
                    updateReaderDetails();
                });
                btnD.setOnAction(e -> {
                    Reader r = getTableView().getItems().get(getIndex());
                    if (manager.xoaDocGiaDB(r.getID())) refreshData();
                    else showAlert("Lỗi", "Độc giả đang mượn sách, không thể xóa!");
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void setupSearch() {
        FilteredList<Book> filterB = new FilteredList<>(masterBookList, p -> true);
        txtTimKiemSach.textProperty().addListener((o, old, v) -> filterB.setPredicate(b ->
                v == null || v.isEmpty() || b.getName().toLowerCase().contains(v.toLowerCase()) || b.getID().contains(v)));
        bookTable.setItems(new SortedList<>(filterB));

        FilteredList<Reader> filterR = new FilteredList<>(masterReaderList, p -> true);
        txtTimKiemDocGia.textProperty().addListener((o, old, v) -> filterR.setPredicate(r ->
                v == null || v.isEmpty() || r.getName().toLowerCase().contains(v.toLowerCase()) || r.getID().contains(v)));
        readerTable.setItems(new SortedList<>(filterR));
    }

    private void setupActionButtons() {
        btnThemSach.setOnAction(e -> {
            try {
                int sl = Integer.parseInt(txtThemSoLuong.getText());
                if (manager.themSachDB(new Book(txtThemTenSach.getText(), txtThemIdSach.getText()), sl)) {
                    refreshData();
                    txtThemIdSach.clear(); txtThemTenSach.clear(); txtThemSoLuong.clear();
                }
            } catch (Exception ex) { showAlert("Lỗi", "Dữ liệu nhập không hợp lệ!"); }
        });

        btnThemDocGia.setOnAction(e -> {
            if (manager.themDocGiaDB(new Reader(txtThemTenDocGia.getText(), txtThemIdDocGia.getText()))) {
                refreshData();
                txtThemIdDocGia.clear(); txtThemTenDocGia.clear();
            }
        });

        btnMuonSach.setOnAction(e -> {
            if (currentSelectedReader != null && manager.xuLyMuonSachDB(txtNhapIdSach.getText(), currentSelectedReader.getID())) {
                refreshData(); updateReaderDetails();
            } else showAlert("Lỗi", "Không thể mượn sách!");
        });

        btnTraSach.setOnAction(e -> {
            if (currentSelectedReader != null && manager.xuLyTraSachDB(txtNhapIdSach.getText(), currentSelectedReader.getID())) {
                refreshData(); updateReaderDetails();
            } else showAlert("Lỗi", "Không thể trả sách!");
        });
    }

    private void refreshData() {
        masterBookList.setAll(manager.layTatCaSach());
        masterReaderList.setAll(manager.layTatCaDocGia());
    }

    private void updateReaderDetails() {
        if (currentSelectedReader != null) {
            String info = "ĐỘC GIẢ: " + currentSelectedReader.getName() + "\n" + manager.layDanhSachSachDangMuon(currentSelectedReader.getID());
            readerDetails.setText(info);
        }
    }

    private void showAlert(String title, String content) {
        Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle(title); a.setHeaderText(null); a.setContentText(content); a.showAndWait();
    }
}