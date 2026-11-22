package ru.spbgut.phonebook.model;
/**
 * Перечисление типов телефонных номеров.
 * Содержит основные категории номеров абонента.
 */
public enum PhoneType {
    MOBILE("Сотовый"),
    HOME("Домашний"),
    WORK("Рабочий"),
    FAX("Факс");

    private final String name;

    PhoneType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
