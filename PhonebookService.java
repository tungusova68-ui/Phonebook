
package ru.spbgut.phonebook.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbgut.phonebook.model.Abonent;
import ru.spbgut.phonebook.storage.BinaryStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Сервисный класс для управления телефонным справочником.
 * Содержит бизнес-логику: добавление, удаление, поиск и сортировку.
 */
public class PhonebookService {

    private static final Logger logger =
            LogManager.getLogger(PhonebookService.class);

    private final BinaryStorage storage;
    private final List<Abonent> abonents;

    /**
     * Создает сервис и загружает ранее сохраненные данные.
     * @param storage компонент для работы с бинарным файлом
     */
    public PhonebookService(BinaryStorage storage) {
        this.storage = storage;
        this.abonents = storage.load();
    }

    /**
     * Добавляет нового абонента в справочник.
     * @param abonent объект абонента для добавления
     * @throws IllegalArgumentException если абонент или его ФИО отсутствуют
     */
    public void addAbonent(Abonent abonent) {
        if (abonent == null || abonent.getFio() == null
                || abonent.getFio().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "ФИО не может быть пустым"
            );
        }

        abonents.add(abonent);
        logger.info("Добавлен новый абонент: {}", abonent.getFio());
    }

    /**
     * Удаляет абонента из справочника.
     * @param abonent абонент для удаления
     */
    public void removeAbonent(Abonent abonent) {
        if (abonent != null && abonents.remove(abonent)) {
            logger.info("Удален абонент: {}", abonent.getFio());
        }
    }

    /**
     * Возвращает копию списка всех абонентов.
     * @return список абонентов
     */
    public List<Abonent> getAll() {
        return new ArrayList<>(abonents);
    }

    /**
     * Ищет абонентов по ФИО или номеру телефона.
     * @param text строка для поиска
     * @return список найденных абонентов
     */
    public List<Abonent> search(String text) {
        if (text == null || text.trim().isEmpty()) {
            return getAll();
        }

        String query = text.trim().toLowerCase(Locale.ROOT);

        return abonents.stream()
                .filter(a ->
                        a.getFio() != null
                                && a.getFio().toLowerCase(Locale.ROOT)
                                .contains(query)
                                || a.getPhoneList() != null
                                && a.getPhoneList().stream()
                                .anyMatch(p ->
                                        p != null
                                                && p.getNumber() != null
                                                && p.getNumber()
                                                .toLowerCase(Locale.ROOT)
                                                .contains(query)
                                )
                )
                .collect(Collectors.toList());
    }

    /**
     * Сортирует абонентов по ФИО без учета регистра.
     * @return новый отсортированный список
     */
    public List<Abonent> sortByFio() {
        return abonents.stream()
                .sorted(Comparator.comparing(
                        Abonent::getFio,
                        Comparator.nullsLast(
                                String.CASE_INSENSITIVE_ORDER
                        )
                ))
                .collect(Collectors.toList());
    }

    /**
     * Сохраняет текущий список абонентов в файл.
     * @return true, если сохранение прошло успешно
     */
    public boolean saveData() {
        return storage.save(abonents);
    }
}
