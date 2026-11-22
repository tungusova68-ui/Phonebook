package ru.spbgut.phonebook;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.spbgut.phonebook.gui.MainView;
import ru.spbgut.phonebook.service.PhonebookService;
import ru.spbgut.phonebook.storage.BinaryStorage;

import java.io.File;
/**
 * Главный класс приложения телефонного справочника.
 * Отвечает за запуск и первоначальную настройку интерфейса.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        BinaryStorage storage = new BinaryStorage(new File("phonebook.dat"));
        PhonebookService service = new PhonebookService(storage);

        MainView mainView = new MainView(service);

        Scene scene = new Scene(mainView.getRoot(), 850, 550);
        primaryStage.setTitle("Телефонный справочник");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}