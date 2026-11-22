
package ru.spbgut.phonebook.gui;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ru.spbgut.phonebook.model.Abonent;
import ru.spbgut.phonebook.model.PhoneNumber;
import ru.spbgut.phonebook.model.PhoneType;
/**
 * Диалоговое окно для добавления и редактирования абонентов.
 * Позволяет вводить ФИО и управлять телефонными номерами.
 */
public class AbonentDialog {
    /**
     * Открывает окно добавления или редактирования абонента.
     * @param existing существующий абонент или null при добавлении нового
     * @return созданный или измененный абонент либо null при отмене
     */
    public static Abonent show(Abonent existing) {
        Dialog<Abonent> dialog = new Dialog<>();

        dialog.setTitle(existing == null
                ? "Новый абонент"
                : "Редактирование абонента");

        dialog.setHeaderText("Введите данные абонента");

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField txtFio = new TextField();
        txtFio.setPromptText("Введите ФИО");

        ComboBox<PhoneType> cbType = new ComboBox<>();
        cbType.getItems().addAll(PhoneType.values());
        cbType.getSelectionModel().selectFirst();

        TextField txtNumber = new TextField();
        txtNumber.setPromptText("Введите номер телефона");

        ListView<PhoneNumber> listPhones = new ListView<>();
        listPhones.setPrefHeight(120);

        if (existing != null) {
            txtFio.setText(existing.getFio());

            if (existing.getPhoneList() != null) {
                listPhones.getItems().addAll(existing.getPhoneList());
            }
        }

        Button btnAddPhone = createAddPhoneButton(
                txtNumber, cbType, listPhones
        );

        Button btnRemovePhone = new Button("Удалить номер");
        btnRemovePhone.setOnAction(e -> {
            PhoneNumber selected =
                    listPhones.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showError("Выберите номер телефона для удаления.");
                return;
            }

            listPhones.getItems().remove(selected);
        });

        GridPane grid = createDialogGrid(
                txtFio,
                cbType,
                txtNumber,
                btnAddPhone,
                btnRemovePhone,
                listPhones
        );

        dialog.getDialogPane().setContent(grid);

        Button okButton = (Button) dialog.getDialogPane()
                .lookupButton(ButtonType.OK);

        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (txtFio.getText() == null
                    || txtFio.getText().trim().isEmpty()) {

                showError("ФИО не может быть пустым!");
                event.consume();
                return;
            }

            if (listPhones.getItems().isEmpty()) {
                showError(
                        "Необходимо добавить хотя бы один номер телефона!"
                );
                event.consume();
            }
        });

        dialog.setResultConverter(button ->
                processResult(button, txtFio, listPhones, existing)
        );

        return dialog.showAndWait().orElse(null);
    }

    private static Button createAddPhoneButton(
            TextField txtNumber,
            ComboBox<PhoneType> cbType,
            ListView<PhoneNumber> listPhones) {

        Button btnAddPhone = new Button("Добавить номер");

        btnAddPhone.setOnAction(e -> {
            String number = txtNumber.getText() == null
                    ? ""
                    : txtNumber.getText().trim();

            PhoneType type = cbType.getValue();

            if (number.isEmpty()) {
                showError("Введите номер телефона.");
                return;
            }

            if (type == null) {
                showError("Выберите тип телефонного номера.");
                return;
            }

            listPhones.getItems().add(
                    new PhoneNumber(number, type)
            );

            txtNumber.clear();
        });

        return btnAddPhone;
    }

    private static GridPane createDialogGrid(
            TextField txtFio,
            ComboBox<PhoneType> cbType,
            TextField txtNumber,
            Button btnAddPhone,
            Button btnRemovePhone,
            ListView<PhoneNumber> listPhones) {

        GridPane grid = new GridPane();

        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("ФИО:"), 0, 0);
        grid.add(txtFio, 1, 0);

        grid.add(new Label("Тип телефона:"), 0, 1);
        grid.add(cbType, 1, 1);

        grid.add(new Label("Номер:"), 0, 2);
        grid.add(txtNumber, 1, 2);

        grid.add(btnAddPhone, 1, 3);
        grid.add(new Label("Список телефонов:"), 0, 4);
        grid.add(listPhones, 1, 4);
        grid.add(btnRemovePhone, 1, 5);

        return grid;
    }

    private static Abonent processResult(
            ButtonType button,
            TextField txtFio,
            ListView<PhoneNumber> listPhones,
            Abonent existing) {

        if (button != ButtonType.OK) {
            return null;
        }

        String fio = txtFio.getText().trim();

        Abonent result = existing != null
                ? existing
                : new Abonent(fio);

        result.setFio(fio);
        result.getPhoneList().clear();
        result.getPhoneList().addAll(listPhones.getItems());

        return result;
    }

    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
