
package ru.spbgut.phonebook.service;

import org.junit.jupiter.api.*;
import ru.spbgut.phonebook.model.*;
import ru.spbgut.phonebook.storage.BinaryStorage;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PhonebookServiceTest {

    private File tmpFile;
    private PhonebookService service;

    @BeforeEach
    void setUp() throws Exception {
        tmpFile = Files.createTempFile(
                "phonebook_test", ".dat"
        ).toFile();

        service = new PhonebookService(
                new BinaryStorage(tmpFile)
        );
    }

    @AfterEach
    void tearDown() {
        if (tmpFile != null && tmpFile.exists()) {
            if (!tmpFile.delete()) {
                System.err.println(
                        "Не удалось удалить временный файл"
                );
            }
        }
    }

    @Test
    void testAddAndSearchByName() {
        Abonent ab = new Abonent("Иванов Иван");
        ab.addPhone(
                new PhoneNumber(
                        "+79990001122",
                        PhoneType.MOBILE
                )
        );

        service.addAbonent(ab);

        assertEquals(1, service.search("Иванов").size());
    }

    @Test
    void testSearchByNumber() {
        Abonent ab = new Abonent("Петров Петр");
        ab.addPhone(
                new PhoneNumber("12345", PhoneType.WORK)
        );

        service.addAbonent(ab);

        assertEquals(1, service.search("123").size());
    }

    @Test
    void testSortByFio() {
        service.addAbonent(new Abonent("Яковлев"));
        service.addAbonent(new Abonent("Абрамов"));

        List<Abonent> sorted = service.sortByFio();

        assertEquals("Абрамов", sorted.get(0).getFio());
    }

    @Test
    void testEmptyNameThrowsException() {
        Abonent emptyAb = new Abonent("   ");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addAbonent(emptyAb)
        );
    }

    @Test
    void testSaveAndLoad() {
        service.addAbonent(new Abonent("Сидоров"));
        service.saveData();

        PhonebookService newService =
                new PhonebookService(
                        new BinaryStorage(tmpFile)
                );

        assertEquals(1, newService.getAll().size());
    }

    @Test
    void testRemoveAbonent() {
        Abonent abonent = new Abonent("Иванов Иван");
        service.addAbonent(abonent);

        service.removeAbonent(abonent);

        assertTrue(service.getAll().isEmpty());
    }

    @Test
    void testSaveAndLoadMultiplePhones() {
        Abonent abonent = new Abonent("Сидоров");

        abonent.addPhone(
                new PhoneNumber(
                        "11111",
                        PhoneType.MOBILE
                )
        );

        abonent.addPhone(
                new PhoneNumber(
                        "22222",
                        PhoneType.WORK
                )
        );

        service.addAbonent(abonent);
        service.saveData();

        PhonebookService newService =
                new PhonebookService(
                        new BinaryStorage(tmpFile)
                );

        assertEquals(1, newService.getAll().size());

        assertEquals(
                2,
                newService.getAll()
                        .get(0)
                        .getPhoneList()
                        .size()
        );
    }
}
