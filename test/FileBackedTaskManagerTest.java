import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest {
    static File file;
    static Task task = new Task("Task1", StatusTask.NEW, "Description1");

    @BeforeEach
    void createTemporaryFile() {
        try {
            file = File.createTempFile("testFile", ".txt");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка создания файла");
        }
    }

    @Test
    void loadingEmptyFile() {
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

        assertNotNull(manager, "Менеджер должен быть проинициализирован");
        assertTrue(file.exists(), "Файл должен быть создан");
        assertEquals(0, file.length(), "Файл должен быть пустым");
    }

    @Test
    void savingTaskFile() throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        assertTrue(file.exists(), "Файл должен быть создан");
        assertEquals(0, file.length(), "Файл должен быть пустым");

        manager.createTask(task);
        final List<String> listTask = Files.readAllLines(file.toPath());

        assertNotNull(listTask, "Список не должен быть пустым");
        assertEquals(2, listTask.size(), "Строк должно быть 2");
    }

    @Test
    void loadingTaskFile() throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.createTask(task);

        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(file);
        final List<Task> listTask = newManager.getListTask();

        assertNotNull(listTask, "Список не должен быть пустым");
        assertEquals(1, listTask.size(), "Должна быть 1 задача");

        for (Task task1 : listTask) {
            assertEquals(1, task1.getId(), "id должны совпадать");
            assertEquals("Task1", task1.getNameTask(), "Имя должно совпадать");
            assertEquals(StatusTask.NEW, task1.getStatusTask(), "Статус должен совпадать");
            assertEquals("Description1", task1.getDescription(), "Описание должно совпадать");
        }
    }
}