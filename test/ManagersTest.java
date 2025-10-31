import exceptions.ManagerSaveException;
import interfaces.HistoryManager;
import interfaces.TaskManager;
import managers.Managers;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    void TaskManagerGetDefaultNotNull() {
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager, "Объект должен быть проинициализирован");
    }

    @Test
    void HistoryManagerGetDefaultHistoryNotNull() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "Объект должен быть проинициализирован");
    }

    @Test
    void FileBackedTaskManagerGetDefaultNotNull() {
        File file;

        try {
            file = File.createTempFile("testFile", ".txt");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка создания файла");
        }

        TaskManager taskManager = Managers.getDefault(file);

        assertNotNull(taskManager, "Объект должен быть проинициализирован");
    }
}