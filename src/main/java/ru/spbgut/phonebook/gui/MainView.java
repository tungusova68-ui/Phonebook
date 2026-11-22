
package ru.spbgut.phonebook.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.transform.Scale;
import ru.spbgut.phonebook.model.Abonent;
import ru.spbgut.phonebook.model.PhoneNumber;
import ru.spbgut.phonebook.service.PhonebookService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Главное окно приложения. Отвечает за отображение таблицы,
 * поиск, сортировку и масштабирование интерфейса.
 */
public class MainView {

    private final PhonebookService service;
    private final BorderPane root;
    private final TableView<Abonent> table;
    private final ObservableList<Abonent> tableData;
    private final Scale zoomScale;

    public MainView(PhonebookService service) {
        this.service = service;
        this.root = new BorderPane();
        this.table = new TableView<>();
        this.tableData = FXCollections.observableArrayList();
        this.zoomScale = new Scale(1, 1);

        initTable();

        root.setTop(createTopPanel());
        root.setBottom(createBottomPanel());
        root.setCenter(table);

        updateTable(service.getAll());
    }

    /**
     * Инициализирует таблицу и настраивает колонки.
     */
    private void initTable() {
        TableColumn<Abonent, String> colFio = new TableColumn<>("ФИО");
        colFio.setCellValueFactory(new PropertyValueFactory<>("fio"));

        TableColumn<Abonent, String> colPhones = new TableColumn<>("Телефоны");
        colPhones.setCellValueFactory(data -> {
            Abonent abonent = data.getValue();

            String phones = abonent.getPhoneList() == null
                    ? ""
                    : abonent.getPhoneList().stream()
                    .filter(phone -> phone != null)
                    .map(PhoneNumber::toString)
                    .collect(Collectors.joining(", "));

            return new SimpleStringProperty(phones);
        });

        colFio.setPrefWidth(220);
        colPhones.setPrefWidth(350);

        table.getColumns().addAll(colFio, colPhones);
        table.setItems(tableData);
        table.getTransforms().add(zoomScale);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /**
     * Создает верхнюю панель с поиском, сортировкой и масштабированием.
     */
    private HBox createTopPanel() {
        TextField searchField = new TextField();
        searchField.setPromptText("Поиск...");

        searchField.textProperty().addListener((obs, oldVal, newVal) ->
                updateTable(service.search(newVal)));

        Button btnSort = new Button("Сортировать");
        btnSort.setOnAction(e -> updateTable(service.sortByFio()));

        Button btnZoomIn = new Button("+");
        btnZoomIn.setOnAction(e -> handleZoom(0.1));

        Button btnZoomOut = new Button("-");
        btnZoomOut.setOnAction(e -> handleZoom(-0.1));

        return new HBox(5, searchField, btnSort, btnZoomIn, btnZoomOut);
    }

    /**
     * Изменяет масштаб интерфейса в допустимых пределах.
     */
    private void handleZoom(double delta) {
        double nextScale = Math.min(
                Math.max(zoomScale.getX() + delta, 0.5),
                2.0
        );

        zoomScale.setX(nextScale);
        zoomScale.setY(nextScale);
    }

    /**
     * Создает нижнюю панель с кнопками управления записями.
     */
    private HBox createBottomPanel() {
        Button btnAdd = new Button("Добавить");
        btnAdd.setOnAction(e -> handleAddAction());

        Button btnEdit = new Button("Редактировать");
        btnEdit.setOnAction(e -> handleEditAction());

        Button btnDelete = new Button("Удалить");
        btnDelete.setOnAction(e -> handleDeleteAction());

        Button btnSave = new Button("Сохранить");
        btnSave.setOnAction(e -> saveData());

        return new HBox(5, btnAdd, btnEdit, btnDelete, btnSave);
    }

    /**
     * Добавляет нового абонента.
     */
    private void handleAddAction() {
        Abonent newAb = AbonentDialog.show(null);

        if (newAb != null) {
            try {
                service.addAbonent(newAb);
                saveData();
                updateTable(service.getAll());
            } catch (IllegalArgumentException ex) {
                showErrorAlert(ex.getMessage());
            }
        }
    }

    /**
     * Редактирует выбранную запись.
     */
    private void handleEditAction() {
        Abonent selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showErrorAlert("Выберите абонента для редактирования.");
            return;
        }

        Abonent edited = AbonentDialog.show(selected);

        if (edited != null) {
            saveData();
            updateTable(service.getAll());
        }
    }

    /**
     * Удаляет выбранную запись после подтверждения пользователя.
     */
    private void handleDeleteAction() {
        Abonent selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showErrorAlert("Выберите абонента для удаления.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Подтверждение удаления");
        confirmation.setHeaderText(null);
        confirmation.setContentText(
                "Вы действительно хотите удалить абонента "
                        + selected.getFio() + "?"
        );

        ButtonType result = confirmation.showAndWait().orElse(ButtonType.CANCEL);

        if (result == ButtonType.OK) {
            service.removeAbonent(selected);
            saveData();
            updateTable(service.getAll());
        }
    }

    /**
     * Сохраняет данные и сообщает об ошибке, если сохранение не удалось.
     */
    private void saveData() {
        if (!service.saveData()) {
            showErrorAlert("Не удалось сохранить данные в файл.");
        }
    }

    /**
     * Отображает сообщение об ошибке.
     */
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Обновляет содержимое таблицы.
     */
    private void updateTable(List<Abonent> list) {
        tableData.setAll(list == null ? List.of() : list);
    }

    public Parent getRoot() {
        return root;
    }
}
