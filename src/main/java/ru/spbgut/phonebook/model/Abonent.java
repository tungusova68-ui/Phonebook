package ru.spbgut.phonebook.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * Класс, представляющий абонента телефонного справочника.
 * Содержит ФИО и список телефонных номеров.
 */
public class Abonent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String fio;
    private final List<PhoneNumber> phoneList;

    public Abonent(String fio) {
        this.fio = fio;
        this.phoneList = new ArrayList<>();
    }
    /**
     * Возвращает ФИО абонента.
     * @return строка с ФИО
     */
    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public List<PhoneNumber> getPhoneList() {
        return phoneList;
    }

    public void addPhone(PhoneNumber phone) {
        this.phoneList.add(phone);
    }

    @Override
    public String toString() {
        return fio;
    }
}
