
package ru.spbgut.phonebook.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Класс, представляющий телефонный номер абонента.
 * Содержит номер телефона и его тип.
 */
public class PhoneNumber implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String number;
    private PhoneType type;

    public PhoneNumber(String number, PhoneType type) {
        this.number = number;
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public PhoneType getType() {
        return type;
    }

    public void setType(PhoneType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PhoneNumber that = (PhoneNumber) o;

        return Objects.equals(number, that.number)
                && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, type);
    }

    @Override
    public String toString() {
        String phoneNumber = Objects.toString(number, "");
        String phoneType = type == null
                ? "Тип не указан"
                : type.getName();

        return phoneNumber + " (" + phoneType + ")";
    }
}
