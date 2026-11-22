
package ru.spbgut.phonebook.storage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbgut.phonebook.model.Abonent;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для сохранения и загрузки данных справочника
 * в бинарный файл.
 */
public class BinaryStorage {

    private static final Logger logger =
            LogManager.getLogger(BinaryStorage.class);

    private final File dataFile;

    public BinaryStorage(File dataFile) {
        this.dataFile = dataFile;
    }

    /**
     * Сохраняет список абонентов в бинарный файл.
     * @param list список абонентов
     * @return true, если сохранение прошло успешно,
     *         иначе false
     */
    public boolean save(List<Abonent> list) {
        if (list == null) {
            logger.error("Ошибка сохранения: список абонентов равен null");
            return false;
        }

        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(dataFile))) {

            oos.writeObject(new ArrayList<>(list));
            logger.info("Данные успешно сохранены");

            return true;

        } catch (IOException e) {
            logger.error("Ошибка сохранения файла", e);
            return false;
        }
    }

    /**
     * Загружает список абонентов из бинарного файла.
     * @return список абонентов или пустой список,
     *         если файл отсутствует, пуст или поврежден
     */
    public List<Abonent> load() {
        if (!dataFile.exists() || dataFile.length() == 0) {
            logger.info("Файл данных отсутствует или пуст");
            return new ArrayList<>();
        }

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(dataFile))) {

            Object data = ois.readObject();

            if (!(data instanceof List<?>)) {
                logger.error("Ошибка чтения: файл содержит некорректные данные");
                return new ArrayList<>();
            }

            List<?> loadedList = (List<?>) data;
            List<Abonent> result = new ArrayList<>();

            for (Object item : loadedList) {
                if (!(item instanceof Abonent)) {
                    logger.error("Ошибка чтения: обнаружен некорректный объект");
                    return new ArrayList<>();
                }

                result.add((Abonent) item);
            }

            logger.info("Данные успешно загружены");
            return result;

        } catch (IOException | ClassNotFoundException e) {
            logger.error("Ошибка чтения файла", e);
            return new ArrayList<>();
        }
    }
}
